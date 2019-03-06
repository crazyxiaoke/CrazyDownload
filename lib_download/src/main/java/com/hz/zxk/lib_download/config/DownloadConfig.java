package com.hz.zxk.lib_download.config;

import java.util.concurrent.TimeUnit;

/**
 * 配置类
 */
public class DownloadConfig {

    /**
     * 核心线程数
     */
    private int coreThreadSize;

    /**
     * 最大线程数
     */
    private int maxThreadSize;

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

    /**
     * Default constructor
     */
    private DownloadConfig(Builder builder) {
        coreThreadSize = builder.coreThreadSize;
        maxThreadSize = builder.maxThreadSize;
        downloadThreadSize = builder.downloadThreadSize;
        defaultFilepath = builder.defaultFilepath;
        keepAliveTime = builder.keepAliveTime;
        unit = builder.unit;
    }

    public int getCoreThreadSize() {
        return coreThreadSize;
    }

    public int getMaxThreadSize() {
        return maxThreadSize;
    }

    public int getDownloadThreadSize() {
        return downloadThreadSize;
    }

    public String getDefaultFilepath() {
        return defaultFilepath;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public static class Builder {
        /**
         * 核心线程数
         */
        private int coreThreadSize;

        /**
         * 最大线程数
         */
        private int maxThreadSize;

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

        /**
         * @return
         */
        public Builder setCoreThreadSize(int coreThreadSize) {
            this.coreThreadSize = coreThreadSize;
            return this;
        }

        /**
         * @return
         */
        public Builder setMaxThreadSize(int maxThreadSize) {
            this.maxThreadSize = maxThreadSize;
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

        /**
         * 创建
         */
        public DownloadConfig build() {
            // TODO implement here
            return new DownloadConfig(this);
        }


    }


}