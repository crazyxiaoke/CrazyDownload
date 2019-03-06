package com.hz.zxk.lib_download;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.hz.zxk.lib_download.callback.DownloadCallback;
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


    /** 最大下载线程数 */
    private int maxThreadSize;

    /** 下载路径 */
    private String url;

    /**
     * 每个下载任务独有的token,
     * url的MD5值
     */
    private String token;

    /** 文件名称 */
    private String filename;

    /** 存储路径 */
    private String filepath;

    /** 下载回调 */
    private DownloadCallback callback;

    /** 消息发送 */
    private Handler handler;

    /** 文件大小 */
    private long contentLength;

    /** 总进度 */
    private long totalProgress=0;
    /** 线程列表，方便管理，如：全部关闭*/
    private ArrayList<DownloadRunnable> downloadRunnables;

    public DownloadTask(int maxThreadSize,String url,String token,String filename,
                        String filepath,Handler handler) {
        this.maxThreadSize=maxThreadSize;
        this.token=token;
        this.url=url;
        this.filename=filename;
        this.filepath=filepath;
        this.handler=handler;
        downloadRunnables=new ArrayList<>();
    }

    private Handler threadHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle=new Bundle();
            int what=msg.what;
            switch (what){
                case DownloadStatus.PROGRESS:
                    //更新进度,保持同步
                    synchronized (DownloadTask.class){
                        totalProgress+=(long)msg.obj;
                        //发送消息
                        bundle.clear();
                        bundle.putInt("progress",(int)(totalProgress*100/contentLength));
                        sendMessage(DownloadStatus.PROGRESS,bundle);
                    }
                    break;
                case DownloadStatus.THEARDSUCCESS:
                    //每个线程完成后，获取文件大小
                    //判断是否整个文件已经下载完成
                    File file=new File(filepath+filename);
                    if(file.exists()){
                        long fileLength=file.length();
                        if(fileLength==contentLength){
                            //下载完成，发送消息
                            bundle.clear();
                            sendMessage(DownloadStatus.SUCCESS,bundle);
                        }
                    }
                    break;
                case DownloadStatus.FAIL:
                    //线程下载错误,关闭所有线程
                    synchronized (DownloadTask.class){
                        if(downloadRunnables!=null&&downloadRunnables.size()>0){
                            stopAll();
                            bundle.clear();
                            bundle.putString("errorMsg",(String)msg.obj);
                            sendMessage(DownloadStatus.FAIL,bundle);
                        }
                    }
                    break;
            }
            return false;
        }
    });
    /**
     * 开始下载
     * @return
     */
    public void start() {
        if(!toDatabaseGetFileSize()){
            //如果数据库中没有上次下载的记录，就重新从网络请求下载
            toNetworkGetFileSize();
        }
    }

    /**
     * 从数据库中获取前一次保存的下载信息
     */
    private boolean toDatabaseGetFileSize(){
        List<DownloadInfo> downloadInfos=DownloadDBManager.getsInstance().query(url);
        if(downloadInfos!=null&&downloadInfos.size()>0){
            for (DownloadInfo downloadInfo : downloadInfos) {
                //文件总长度
                contentLength=downloadInfo.getContentLength();
                //计算上次下载的总进度
                totalProgress+=downloadInfo.getProgress();
                long startIndex=downloadInfo.getStartIndex()+downloadInfo.getProgress();
                long endIndex=downloadInfo.getEndIndex();
                //执行下载
                startRunnable(downloadInfo.getThreadId(),startIndex,endIndex);
            }
            return true;
        }
        return false;
    }

    /**
     * 从网络中获取文件大小
     */
    private void toNetworkGetFileSize(){
        //获取下载文件大小,分配每个线程下载的数据大小
        HttpManager.getInstance().asyncRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response!=null&&response.isSuccessful()){
                    contentLength=response.body().contentLength();
                    if(contentLength==-1){
                        Bundle bundle=new Bundle();
                        bundle.putString("errorMsg","无法获取文件大小");
                        sendMessage(DownloadStatus.FAIL,bundle);
                    }else{
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
     * @param contentLength
     */
    private void distributionSize(long contentLength){
        //单个线程下载的文件大小
        long singleDownloadSize=contentLength/maxThreadSize;
        for(int i=0;i<maxThreadSize;i++){
            String threadId="thread#"+(i+1);
            //线程下载的起始位置
            long startIndex=i*singleDownloadSize;
            //线程下载的结束位置
            long endIndex=(i+1)*singleDownloadSize-1;
            //保存数据库
            saveDatabase(threadId,startIndex,endIndex);
            //执行下载
            startRunnable(threadId,startIndex,endIndex);
        }
    }

    /**
     * 保存数据库
     * @param threadId
     * @param startIndex
     * @param endIndex
     */
    private void saveDatabase(String threadId,long startIndex,long endIndex){
        DownloadInfo downloadInfo=new DownloadInfo();
        downloadInfo.setUrl(url);
        downloadInfo.setThreadId(threadId);
        downloadInfo.setFilename(filename);
        downloadInfo.setFilepath(filepath);
        downloadInfo.setContentLength(contentLength);
        downloadInfo.setStartIndex(startIndex);
        downloadInfo.setEndIndex(endIndex);
        downloadInfo.setProgress(0);
        downloadInfo.setStatus(0);
    }

    /**
     * 启动线程下载
     * @param threadId
     * @param startIndex
     * @param endIndex
     */
    private void startRunnable(String threadId,long startIndex,long endIndex){
        //创建线程
        DownloadRunnable runnable=new DownloadRunnable(url,threadId,startIndex,endIndex
                ,filename,filepath,threadHandler);
        //加入到线程池，并执行下载
        DownloadDispatch.getExecutorService().execute(runnable);
    }

    /**
     * 停止全部线程
     */
    private void stopAll(){
        if(downloadRunnables!=null&&downloadRunnables.size()>0){
            for (DownloadRunnable downloadRunnable : downloadRunnables) {
                downloadRunnable.stop();
            }
            downloadRunnables.clear();
        }
    }

    /**
     * 停止下载
     * @return
     */
    public void stop() {
        // TODO implement here
    }

    /**
     * 发送消息给handler
     * @param what
     * @param bundle
     */
    private void sendMessage(int what,Bundle bundle){
        bundle.putString("token",token);
        Message message=new Message();
        message.what=what;
        message.setData(bundle);
        handler.sendMessage(message);
    }

}