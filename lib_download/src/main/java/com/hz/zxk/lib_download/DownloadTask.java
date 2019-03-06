package com.hz.zxk.lib_download;

import android.os.Handler;

import com.hz.zxk.lib_download.callback.DownloadCallback;

/**
 * 下载器
 * 根据最大下载线程数，分配每个线程的下载大小，创建线程放入线程池中。
 */
public class DownloadTask {

    /**
     * Default constructor
     */
    public DownloadTask() {
    }

    /**
     * 最大下载线程数
     */
    private int maxThreadSize;

    /**
     * 下载路径
     */
    private String url;

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
     * 开始下载
     * @return
     */
    public void start() {
        // TODO implement here

    }

    /**
     * 停止下载
     * @return
     */
    public void stop() {
        // TODO implement here
    }

}