package com.hz.zxk.lib_download.config;

import android.content.Context;

import com.hz.zxk.commonutils.utils.FileUtils;
import com.hz.zxk.commonutils.utils.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 配置类
 */
public class DownloadConfig {
    private Context context;
    /**
     * 最大下载数
     */
    private int maxDownloadSize;

    /**
     * 给每个下载任务分配的线程数
     */
    private int downloadThreadSize;

    /**
     * 统一下载存储路径
     */
    private String defaultFilepath;

    /**
     *
     */
    private long keepAliveTime;

    /**
     *
     */
    private TimeUnit unit;

    private boolean daemon;

    /**
     * Default constructor
     */
    private DownloadConfig(Context context, Builder builder) {
        this.context = context;
        if (builder != null) {
            this.maxDownloadSize = builder.maxDownloadSize;
            this.downloadThreadSize = builder.downloadThreadSize;
            this.defaultFilepath = builder.defaultFilepath;
            this.keepAliveTime = builder.keepAliveTime;
            this.unit = builder.unit;
            this.daemon = builder.deamon;
        }
    }

    public int getMaxDownloadSize() {
        return maxDownloadSize <= 0 ? 3 : maxDownloadSize;
    }

    public int getDownloadThreadSize() {
        return downloadThreadSize <= 0 ? 4 : downloadThreadSize;
    }

    public String getDefaultFilepath() {
        if (StringUtils.isEmpty(defaultFilepath)) {
            return FileUtils.getRootCachePath(context);
        }
        return defaultFilepath;
    }

    public long getKeepAliveTime() {
        return keepAliveTime <= 0 ? 60 : keepAliveTime;
    }

    public TimeUnit getUnit() {
        return unit == null ? TimeUnit.MICROSECONDS : unit;
    }

    public boolean getDeamon() {
        return daemon;
    }

    public static class Builder {
        /**
         * 最大下载数
         */
        private int maxDownloadSize;

        /**
         * 给每个下载任务分配的线程数
         */
        private int downloadThreadSize;

        /**
         * 统一下载存储路径
         */
        private String defaultFilepath;

        /**
         * 线程
         */
        private long keepAliveTime;

        /**
         *
         */
        private TimeUnit unit;

        /**
         * 是否启动线程保护
         */
        private boolean deamon;

        public Builder setMaxDownloadSize(int maxDownloadSize) {
            this.maxDownloadSize = maxDownloadSize;
            return this;
        }

        /**
         * @return
         */
        public Builder setDownloadThreadSize(int downloadThreadSize) {
            this.downloadThreadSize = downloadThreadSize;
            return this;
        }

        /**
         * @return
         */
        public Builder setDefaultFilePath(String defaultFilePath) {
            this.defaultFilepath = defaultFilePath;
            return this;
        }

        public Builder setDeamon(boolean deamon) {
            this.deamon = deamon;
            return this;
        }

        /**
         * 创建
         */
        public DownloadConfig build(Context context) {
            // TODO implement here
            return new DownloadConfig(context, this);
        }

    }


}