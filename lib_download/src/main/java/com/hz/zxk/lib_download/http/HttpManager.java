package com.hz.zxk.lib_download.http;


import java.io.IOException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络请求工具
 */
public class HttpManager {


    private static HttpManager sInstance;

    private OkHttpClient httpClient;

    public HttpManager() {
        httpClient = new OkHttpClient();
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static HttpManager getInstance() {
        if (sInstance == null) {
            synchronized (HttpManager.class) {
                if (sInstance == null) {
                    sInstance = new HttpManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 异步请求
     *
     * @param url      请求路径
     * @param callback 请求回调
     */
    public void asyncRequest(String url, Callback callback) {
        // TODO implement here
        Request request = new Request.Builder()
                .url(url).build();
        httpClient.newCall(request).enqueue(callback);
    }

    /**
     * 同步请求，根据range，下载文件块
     *
     * @param url
     * @param startIndex 起始位置
     * @param endIndex
     * @return
     */
    public Response syncRequestByRange(String url, long startIndex, long endIndex)
            throws IOException {
        // TODO implement here
        Request request = new Request.Builder()
                .url(url).addHeader("Range", "bytes=" + startIndex + "-" + endIndex)
                .build();
        return httpClient.newCall(request).execute();
    }

}