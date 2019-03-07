package com.hz.zxk.lib_download;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.hz.zxk.commonutils.utils.SignUtils;
import com.hz.zxk.commonutils.utils.StringUtils;
import com.hz.zxk.lib_download.callback.DownloadCallback;
import com.hz.zxk.lib_download.config.DownloadConfig;
import com.hz.zxk.lib_download.constants.HandlerBuildKey;
import com.hz.zxk.lib_download.db.DownloadDBManager;

import org.greenrobot.greendao.annotation.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 下载分发器
 */
public class DownloadDispatch {

    private static DownloadDispatch sInstance;

    /**
     * 配置参数
     */
    private DownloadConfig config;

    /**
     * 下载路径
     */
    private String url;

    /**
     * 下载中的任务列表
     */
    private List<DownloadTask> runningTasks;

    /**
     * 等待下载的任务列表
     */
    private List<DownloadTask> readyTasks;

    private HashMap<String, DownloadCallback> downloadCallbacks;

    /**
     * 线程池
     */
    private static ThreadPoolExecutor executorService;

    /**
     * Default constructor
     */
    private DownloadDispatch() {
        //正在下载列表初始化
        runningTasks = new ArrayList<>();
        //等待下载列表初始化
        readyTasks = new ArrayList<>();
        //存储每个下载的回调函数
        downloadCallbacks = new HashMap<>();

    }

    /**
     * 单例
     */
    public static DownloadDispatch getInstance() {
        if (sInstance == null) {
            synchronized (DownloadDispatch.class) {
                if (sInstance == null) {
                    sInstance = new DownloadDispatch();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化
     *
     * @param context
     * @param config  配置参数
     */
    public void init(Context context, @NonNull DownloadConfig config) {
        this.config = config;
        //数据库初始化
        DownloadDBManager.getsInstance().init(context);
    }

    /**
     * 创建线程池
     *
     * @return
     */
    public ThreadPoolExecutor getExecutorService() {
        if (executorService == null) {
            synchronized (DownloadDispatch.class) {
                if (executorService == null) {
                    executorService = new ThreadPoolExecutor(config.getMaxDownloadSize() * config.getDownloadThreadSize(),
                            config.getMaxDownloadSize() * config.getDownloadThreadSize(), config.getKeepAliveTime(), config.getUnit(),
                            new LinkedBlockingDeque<Runnable>(), new ThreadFactory() {
                        @Override
                        public Thread newThread(@NonNull Runnable r) {
                            AtomicInteger integer = new AtomicInteger(0);
                            Thread thread = new Thread(r, "thread#" + integer.getAndIncrement());
                            thread.setDaemon(config.getDeamon());
                            return thread;
                        }
                    });
                }
            }
        }
        return executorService;
    }

    //消息回调
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int what = msg.what;
            Bundle bundle = msg.getData();
            //获取唯一标识符
            String token = bundle.getString("token");
            //通过标识符获取回调函数
            DownloadCallback callback = downloadCallbacks.get(token);
            switch (what) {
                case DownloadStatus.SUCCESS:
                    //下载成功
                    String filepath = bundle.getString(HandlerBuildKey.FILEPATH);
                    if (StringUtils.isNotEmpty(filepath)) {
                        File file = new File(filepath);
                        callback.success(file);
                    }
                    //移除已成功的任务
                    removeTask(runningTasks, token);
                    //执行下一个下载任务
                    nextDownload();
                    //移除已成功的回调
                    downloadCallbacks.remove(token);
                    break;
                case DownloadStatus.CANCEL:
                    //移除取消下载的任务
                    removeTask(runningTasks, token);
                    //下载取消
                    synchronized (DownloadDispatch.class){
                        if(callback!=null){
                            callback.cancel();
                        }
                    }
                    //执行下一个下载任务
                    nextDownload();
                    //移除已取消的回调
                    downloadCallbacks.remove(token);
                    break;
                case DownloadStatus.PROGRESS:
                    //更新进度
                    if(callback!=null){
                        int progress = bundle.getInt(HandlerBuildKey.PROGRESS);
                        callback.progress(progress);
                    }
                    break;
                case DownloadStatus.SPEEDNETWORK:
                    //下载网速
                    if(callback!=null){
                        String speedNetwork = bundle.getString(HandlerBuildKey.SPEEDNETWORK);
                        callback.speedNetwork(speedNetwork);
                    }
                    break;
            }
            return false;
        }
    });


    /**
     * @param url      开始下载
     * @param callback 回调
     */
    public void download(String url, DownloadCallback callback) {
        //获取文件名
        String fileName = url.substring(url.lastIndexOf("/"), url.length());
        download(url, fileName, callback);
    }

    /**
     * 开始下载
     *
     * @param url
     * @param fileName 存储文件名
     * @param callback 回调
     */
    public void download(String url, String fileName, DownloadCallback callback) {
        // TODO implement here
        download(url, config.getDefaultFilepath(), fileName, callback);
    }

    /**
     * 开始下载
     *
     * @param url
     * @param filepath 文件存储路径
     * @param fileName 存储文件名
     * @param callback 回调
     */
    public void download(String url, String filepath, String fileName, DownloadCallback callback) {
        // TODO implement here
        //生成每个下载链接的唯一标识，供后续使用
        String token = SignUtils.getMD5(url);
        //把callback存入map中，后续通过token来获取callback
        downloadCallbacks.put(token, callback);
        DownloadTask task = new DownloadTask(config.getMaxDownloadSize(), url, token, fileName, filepath
                , mHandler);
        if (runningTasks.size() < config.getMaxDownloadSize()) {
            //下载数没有达到最大下载数，直接执行下载
            runningTasks.add(task);
            task.start();
            //回调开始下载方法
            callback.start();
        } else {
            //已经达到最大下载数，放入等待列表
            readyTasks.add(task);
            //回调等待方法
            callback.pending();
        }
    }

    /**
     * 停止下载
     *
     * @param url 需要停止的url
     */
    public void stop(String url) {
        // TODO implement here
        String token = SignUtils.getMD5(url);
        //如果要停止的下载任务已经在下载中的列表中，停止下载，并从下载中的列表中移除
        if (!stopTask(token)) {
            //如果在等待下载列表中，则直接删除
            removeTask(readyTasks, token);
        }
    }

    /**
     * 停止下载任务
     *
     * @param token
     * @return
     */
    private boolean stopTask(String token) {
        for (DownloadTask task : runningTasks) {
            if (task.getToken().equals(token)) {
                task.stop();
                return true;
            }
        }
        return false;
    }

    /**
     * 移除下载任务
     *
     * @param tasks
     * @param token
     */
    private void removeTask(List<DownloadTask> tasks, String token) {
        Iterator<DownloadTask> it = tasks.iterator();
        while (it.hasNext()) {
            DownloadTask task = it.next();
            if (task.getToken().equals(token)) {
                it.remove();
                return;
            }
        }
    }

    /**
     * 执行下一个下载任务
     */
    private void nextDownload() {
        if (readyTasks != null && readyTasks.size() > 0) {
            synchronized (DownloadDispatch.class) {
                if (readyTasks != null && readyTasks.size() > 0) {
                    DownloadTask task = readyTasks.get(0);
                    runningTasks.add(task);
                    task.start();
                    readyTasks.remove(task);
                }
            }
        }
    }

}