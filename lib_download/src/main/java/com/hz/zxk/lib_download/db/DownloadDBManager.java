package com.hz.zxk.lib_download.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;


/**
 *  数据库管理类
 */
public class DownloadDBManager {

    /**
     * 数据库名
     */
    private String db_name = "download_progress";

    private Context context;

    private static DownloadDBManager sInstance;

    private DaoMaster.DevOpenHelper mOpenHelper;


    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        this.context = context;
        if (mOpenHelper == null) {
            mOpenHelper = new DaoMaster.DevOpenHelper(context, db_name);
        }
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static DownloadDBManager getsInstance() {
        if (sInstance == null) {
            synchronized (DownloadDBManager.class) {
                if (sInstance == null) {
                    sInstance = new DownloadDBManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取可读写数据库
     *
     * @return
     */
    private SQLiteDatabase getWritableDatabase() {
        if (mOpenHelper == null) {
            mOpenHelper = new DaoMaster.DevOpenHelper(context, db_name);
        }
        return mOpenHelper.getWritableDatabase();
    }

    /**
     * 获取只读数据库
     *
     * @return
     */
    private SQLiteDatabase getReadableDatabase() {
        if (mOpenHelper == null) {
            mOpenHelper = new DaoMaster.DevOpenHelper(context, db_name);
        }
        return mOpenHelper.getReadableDatabase();
    }

    /**
     * 查询所有下载线程信息
     *
     * @param url
     * @return
     */
    public List<DownloadInfo> query(String url) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DownloadInfoDao dao = daoSession.getDownloadInfoDao();
        QueryBuilder<DownloadInfo> qb = dao.queryBuilder();
        qb.where(DownloadInfoDao.Properties.Url.eq(url));
        return qb.list();
    }

    /**
     * 插入单个线程下载信息
     *
     * @param donwloadInfo
     */
    public void insert(DownloadInfo donwloadInfo) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DownloadInfoDao dao = daoSession.getDownloadInfoDao();
        dao.insert(donwloadInfo);
    }

    /**
     * 更新单个线程下载信息
     * @param donwloadInfo
     */
    public void update(DownloadInfo donwloadInfo) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DownloadInfoDao dao = daoSession.getDownloadInfoDao();
        dao.update(donwloadInfo);
    }

    /**
     * 删除某个线程
     *
     * @param donwloadInfo
     */
    public void delete(DownloadInfo donwloadInfo) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DownloadInfoDao dao = daoSession.getDownloadInfoDao();
        dao.delete(donwloadInfo);
    }

    /**
     * 删除所有下载线程
     */
    public void deleteAll() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DownloadInfoDao dao = daoSession.getDownloadInfoDao();
        dao.deleteAll();
    }

}