package com.hz.zxk.lib_download;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hz.zxk.commonutils.utils.NetworkUtils;
import com.hz.zxk.lib_download.callback.DownloadCallback;
import com.hz.zxk.lib_download.constants.ErrorCode;
import com.hz.zxk.lib_download.constants.HandlerBuildKey;
import com.hz.zxk.lib_download.db.DownloadDBManager;
import com.hz.zxk.lib_download.db.DownloadInfo;
import com.hz.zxk.lib_download.http.HttpManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 下载器
 * 根据最大下载线程数，分配每个线程的下载大小，创建线程放入线程池中。
 */
public class DownloadTask {


    /**
     * 最大下载线程数
     */
    private int maxThreadSize;

    /**
     * 下载路径
     */
    private String url;

    /**
     * 每个下载任务独有的token,
     * url的MD5值
     */
    private String token;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 存储路径
     */
    private String filepath;

    /**
     * 下载回调
     */
    private DownloadCallback callback;

    /**
     * 消息发送
     */
    private Handler handler;

    /**
     * 文件大小
     */
    private long contentLength;

    /**
     * 总进度
     */
    private long totalProgress = 0;
    private long downloadSize = 0;
    /**
     * 线程列表，方便管理，如：全部关闭
     */
    private ArrayList<DownloadRunnable> downloadRunnables;
    /**
     * 上次计算网速时间
     */
    private long lastSpeedNetworkTime = 0;
    private Bundle mBundle;
    private boolean success = false;

    public DownloadTask(int maxThreadSize, String url, String token, String filename,
                        String filepath, Handler handler) {
        Log.d("TAG", "创建下载任务");
        this.maxThreadSize = maxThreadSize;
        this.token = token;
        this.url = url;
        this.filename = filename;
        this.filepath = filepath;
        this.handler = handler;
        mBundle = new Bundle();
        downloadRunnables = new ArrayList<>();
    }

    private Handler threadHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case DownloadStatus.PROGRESS:
                    //更新进度,保持同步
                    synchronized (DownloadTask.class) {
                        totalProgress += (int) msg.obj;
                        downloadSize += (int) msg.obj;
                        //发送消息
                        mBundle.putInt(HandlerBuildKey.PROGRESS, (int) (totalProgress * 100 / contentLength));
                        sendMessage(DownloadStatus.PROGRESS, mBundle);
                        //发送网速
                        speedNetwork();
                    }
                    break;
                case DownloadStatus.THEARDSUCCESS:
                    //每个线程完成后，获取文件大小
                    //判断是否整个文件已经下载完成
                    synchronized (DownloadTask.class) {
                        if (!success) {
                            File file = new File(filepath + filename);
                            if (file.exists()) {
                                long fileLength = file.length();
                                Log.d("TAG", "fileLength=" + fileLength);
                                if (fileLength == contentLength) {
                                    //下载完成，发送消息
                                    mBundle.putString(HandlerBuildKey.FILEPATH, filepath + filename);
                                    sendMessage(DownloadStatus.SUCCESS, mBundle);
                                    success = true;
                                }
                            }
                        }
                    }
                    break;
                case DownloadStatus.FAIL:
                    //线程下载错误,关闭所有线程
                    synchronized (DownloadTask.class) {
                        if (downloadRunnables != null && downloadRunnables.size() > 0) {
                            stopAll();
                            mBundle.putInt(HandlerBuildKey.ERRORCODE, msg.arg1);
                            mBundle.putString(HandlerBuildKey.ERRORMSG, (String) msg.obj);
                            sendMessage(DownloadStatus.FAIL, mBundle);
                        }
                    }
                    break;
            }
            return false;
        }
    });

    /**
     * 开始下载
     *
     * @return
     */
    public void start() {
        if (!toDatabaseGetFileSize()) {
            //如果数据库中没有上次下载的记录，就重新从网络请求下载
            toNetworkGetFileSize();
        }
    }

    /**
     * 从数据库中获取前一次保存的下载信息
     */
    private boolean toDatabaseGetFileSize() {
        List<DownloadInfo> downloadInfos = DownloadDBManager.getsInstance().query(url);
        if (downloadInfos != null && downloadInfos.size() > 0) {
            for (DownloadInfo downloadInfo : downloadInfos) {
                //文件总长度
                contentLength = downloadInfo.getContentLength();
                //计算上次下载的总进度
                totalProgress += downloadInfo.getProgress();
                //起始位置
                long startIndex = downloadInfo.getStartIndex() + downloadInfo.getProgress();
                //结束位置
                long endIndex = downloadInfo.getEndIndex();
                //执行下载
                startRunnable(downloadInfo.getThreadId(), startIndex, endIndex);
            }
            return true;
        }
        return false;
    }

    /**
     * 从网络中获取文件大小
     */
    private void toNetworkGetFileSize() {
        //获取下载文件大小,分配每个线程下载的数据大小
        HttpManager.getInstance().asyncRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Bundle bundle = new Bundle();
                bundle.putInt(HandlerBuildKey.ERRORCODE, ErrorCode.UNKOWN_ERROR_CODE);
                bundle.putString(HandlerBuildKey.ERRORMSG, e.getMessage());
                sendMessage(DownloadStatus.FAIL, bundle);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    contentLength = response.body().contentLength();
                    if (contentLength == -1) {
                        mBundle.putInt(HandlerBuildKey.ERRORCODE, ErrorCode.CONTENT_LENGTH_ERROR_CODE);
                        mBundle.putString(HandlerBuildKey.ERRORMSG, "无法获取文件大小");
                        sendMessage(DownloadStatus.FAIL, mBundle);
                    } else {
                        //执行下载
                        distributionSize(contentLength);
                    }
                }
            }
        });
    }

    /**
     * 分配每个线程下载大小
     * 并加入到线程池中
     *
     * @param contentLength
     */
    private void distributionSize(long contentLength) {
        //单个线程下载的文件大小
        long singleDownloadSize = contentLength / maxThreadSize;
        for (int i = 0; i < maxThreadSize; i++) {
            String threadId = "thread#" + (i + 1);
            //线程下载的起始位置
            long startIndex = i * singleDownloadSize;
            //线程下载的结束位置
            long endIndex = (i + 1) * singleDownloadSize - 1;
            //保存数据库
            saveDatabase(threadId, startIndex, endIndex);
            //执行下载
            startRunnable(threadId, startIndex, endIndex);
        }
    }

    /**
     * 保存数据库
     *
     * @param threadId
     * @param startIndex
     * @param endIndex
     */
    private void saveDatabase(String threadId, long startIndex, long endIndex) {
        DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.setUrl(url);
        downloadInfo.setThreadId(threadId);
        downloadInfo.setFilename(filename);
        downloadInfo.setFilepath(filepath);
        downloadInfo.setContentLength(contentLength);
        downloadInfo.setStartIndex(startIndex);
        downloadInfo.setEndIndex(endIndex);
        downloadInfo.setProgress(0);
        downloadInfo.setStatus(0);
        DownloadDBManager.getsInstance().insert(downloadInfo);
    }

    /**
     * 启动线程下载
     *
     * @param threadId
     * @param startIndex
     * @param endIndex
     */
    private void startRunnable(String threadId, long startIndex, long endIndex) {
        //创建线程
        DownloadRunnable runnable = new DownloadRunnable(url, threadId, startIndex, endIndex
                , filename, filepath, threadHandler);
        //加入线程列表
        downloadRunnables.add(runnable);
        //加入到线程池，并执行下载
        DownloadDispatch.getInstance().getExecutorService().execute(runnable);
    }

    /**
     * 停止全部线程
     */
    private void stopAll() {
        Log.d("TAG", "TASK停在下载");
        if (downloadRunnables != null && downloadRunnables.size() > 0) {
            Log.d("TAG", "TASK 循环");
            for (DownloadRunnable downloadRunnable : downloadRunnables) {
                Log.d("TAG", "TASK 找到runnable");
                downloadRunnable.stop();
            }
            downloadRunnables.clear();
        }
    }

    /**
     * 停止下载
     *
     * @return
     */
    public void stop() {
        // TODO implement here
        stopAll();
        sendMessage(DownloadStatus.CANCEL, new Bundle());
    }

    /**
     * 发送消息给handler
     *
     * @param what
     * @param bundle
     */
    private void sendMessage(int what, Bundle bundle) {
        bundle.putString(HandlerBuildKey.TOKEN, token);
        Message message = new Message();
        message.what = what;
        message.setData(bundle);
        handler.sendMessage(message);

    }

    /**
     * 计算网络速度
     */
    private void speedNetwork() {
        if (System.currentTimeMillis() - lastSpeedNetworkTime >= 1000 &
                totalProgress < contentLength) {
            lastSpeedNetworkTime = System.currentTimeMillis();
            mBundle.putString(HandlerBuildKey.SPEEDNETWORK, NetworkUtils.networkSpeed(downloadSize) + "/s");
            sendMessage(DownloadStatus.SPEEDNETWORK, mBundle);
            downloadSize = 0;
        }
    }

    /**
     * 获取唯一标识符
     *
     * @return
     */
    public String getToken() {
        return token;
    }

}