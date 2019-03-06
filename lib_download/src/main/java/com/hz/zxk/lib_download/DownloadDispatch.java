package com.hz.zxk.lib_download;

import android.content.Context;

import com.hz.zxk.lib_download.callback.DownloadCallback;
import com.hz.zxk.lib_download.config.DownloadConfig;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 下载分发器
 */
public class DownloadDispatch {

    /**
     * Default constructor
     */
    public DownloadDispatch() {
    }

    /**
     * 
     */
    private Context context;

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

    /**
     * 线程池
     */
    private ThreadPoolExecutor executorService;

    /**
     * 单例
     */
    public void getInstance() {
        // TODO implement here
    }

    /**
     * 初始化
     * @param context 
     * @param config 配置参数
     */
    public void init(Context context, DownloadConfig config) {
        // TODO implement here
    }

    /**
     * @param url 开始下载
     * @param callback 回调
     */
    public void download(String url, DownloadCallback callback) {
        // TODO implement here
    }

    /**
     * @param url 
     * @param filename 存储文件名
     * @param callback 回调
     */
    public void download(String url, String filename, DownloadCallback callback) {
        // TODO implement here
    }

    /**
     * @param url 
     * @param filepath 文件存储路径
     * @param filename 存储文件名
     * @param callback 回调
     */
    public void download(String url, String filepath, String filename, DownloadCallback callback) {
        // TODO implement here
    }

    /**
     * 停止下载
     * @param url 需要停止的url
     */
    public void stop(String url) {
        // TODO implement here
    }

}