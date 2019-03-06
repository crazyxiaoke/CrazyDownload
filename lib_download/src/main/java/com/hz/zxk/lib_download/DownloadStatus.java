package com.hz.zxk.lib_download;

public class DownloadStatus {
    /**
     * 开始下载
     */
    public static final int START=1;
    /**
     * 等待下载
     */
    public static final int PENDING=2;
    /**
     * 更新下载进度
     */
    public static final int PROGRESS=3;
    /**
     * 下载成功
     */
    public static final int SUCCESS=4;
    /**
     * 单个线程下载成功
     */
    public static final int THEARDSUCCESS=5;
    /**
     * 下载失败
     */
    public static final int FAIL=6;
    /**
     * 取消下载
     */
    public static final int CANCEL=7;
}
