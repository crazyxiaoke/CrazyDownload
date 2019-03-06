package com.hz.zxk.lib_download.db;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 下载任务信息
 */
@Entity
public class DownloadInfo {

    /**
     * 
     */
    @Id(autoincrement = true)
    private Long id;

    /**
     * 下载路径
     */
    private String url;

    /**
     * 线程标识ID
     */
    private String threadId;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 存储路径
     */
    private String filepath;

    /**
     * 下载的开始位置
     */
    private long startIndex;

    /**
     * 下载的结束位置
     */
    private long endIndex;

    /**
     * 文件总长度
     */
    private long contentLength;

    /**
     * 当前下载大小
     */
    private long progress;

    /**
     * 下载状态，0:等待下载,1:正在下载
     */
    private int status;

    @Generated(hash = 694832464)
    public DownloadInfo(Long id, String url, String threadId, String filename,
            String filepath, long startIndex, long endIndex, long contentLength,
            long progress, int status) {
        this.id = id;
        this.url = url;
        this.threadId = threadId;
        this.filename = filename;
        this.filepath = filepath;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.contentLength = contentLength;
        this.progress = progress;
        this.status = status;
    }

    @Generated(hash = 327086747)
    public DownloadInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThreadId() {
        return this.threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return this.filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public long getStartIndex() {
        return this.startIndex;
    }

    public void setStartIndex(long startIndex) {
        this.startIndex = startIndex;
    }

    public long getEndIndex() {
        return this.endIndex;
    }

    public void setEndIndex(long endIndex) {
        this.endIndex = endIndex;
    }

    public long getContentLength() {
        return this.contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public long getProgress() {
        return this.progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}