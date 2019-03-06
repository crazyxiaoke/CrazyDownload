package com.hz.zxk.lib_download.callback;

import java.io.File;

/**
 *
 */
public interface DownloadCallback {

    /**
     * 开始下载
     *
     * @return
     */
    void start();

    /**
     * 下载等待中
     *
     * @return
     */
    void pending();

    /**
     * 下载成功
     *
     * @param file
     * @return
     */
    void success(File file);

    /**
     * @param code     错误码
     * @param errorMsg 错误信息
     * @return 下载失败
     */
    void fail(int code, String errorMsg);

    /**
     * @return 取消下载
     */
    void cancel();

    /**
     * 下载进度
     *
     * @param progress 下载进度
     * @return
     */
    void progress(int progress);

}