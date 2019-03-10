package com.hz.zxk.crazydownload;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.hz.zxk.lib_download.DownloadDispatch;
import com.hz.zxk.lib_download.config.DownloadConfig;

import java.util.concurrent.TimeUnit;

/**
 * 　　┏┓　　　　┏┓
 * 　┏┛┻━━━━┛┻┓
 * 　┃　　　　　　　　┃
 * 　┃　　　━　　　　┃
 * 　┃　┳┛　┗┳　　┃
 * 　┃　　　　　　　　┃
 * 　┃　　　┻　　　　┃
 * 　┃　　　　　　　　┃
 * 　┗━━┓　　　┏━┛
 * 　　　　┃　　　┃　　　神兽保佑
 * 　　　　┃　　　┃　　　代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * <p>
 * Created by zxk on 19-3-7.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
//        DownloadConfig config = new DownloadConfig.Builder()
//                .setMaxDownloadSize(3)
//                .setDownloadThreadSize(3)
//                .setDeamon(true)
//                .setDefaultFilePath("/")
//                .setKeepAliveTime(60)
//                .setUnit(TimeUnit.MILLISECONDS)
//                .build(this);
        DownloadDispatch.getInstance().init(this);
    }
}
