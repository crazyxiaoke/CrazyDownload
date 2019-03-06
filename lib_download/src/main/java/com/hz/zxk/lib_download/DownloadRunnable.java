package com.hz.zxk.lib_download;

import android.os.Handler;
import android.os.Message;

import com.hz.zxk.lib_download.db.DownloadDBManager;
import com.hz.zxk.lib_download.db.DownloadInfo;
import com.hz.zxk.lib_download.http.HttpManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Response;

/**
 * 下载任务
 */
public class DownloadRunnable implements Runnable{
    /**
     * 下载中
     */
    private static final int STATUS_START=1;
    /**
     * 停止下载
     */
    private static final int STATUS_STOP=2;
    /**
     * 下载路径
     */
    private String url;

    /**
     * 线程id
     */
    private String threadId;

    /**
     * 起始位置
     */
    private long startIndex;

    /**
     * 结束位置
     */
    private long endIndex;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 存储路径
     */
    private String filepath;

    /**
     * 下载回调
     */
    private Handler handler;

    /**
     * 下载状态
     */
    private int downloadStatus=STATUS_START;

    /**
     * 数据库保存的信息
     */
    private DownloadInfo downloadInfo;

    /**
     * Default constructor
     */
    public DownloadRunnable(String url,String threadId,long startIndex,long endIndex,
                            String filename,String filepath,Handler handler) {
        this.url=url;
        this.threadId=threadId;
        this.startIndex=startIndex;
        this.endIndex=endIndex;
        this.filename=filename;
        this.filepath=filepath;
        this.handler=handler;
        downloadInfo=DownloadDBManager.getsInstance().queryOne(url,threadId);
    }

    /**
     * 执行下载
     */
    @Override
    public void run() {
        try {
            Response response=HttpManager.getInstance().syncRequestByRange(url,startIndex,endIndex);
            if(response!=null){
                File file=new File(filepath+"/"+filename);
                RandomAccessFile randomAccessFile=new RandomAccessFile(file,"rwd");
                randomAccessFile.seek(startIndex);
                InputStream inputStream=response.body().byteStream();
                byte[] buff =new byte[1024];
                int len;
                while((len=inputStream.read(buff,0,buff.length))!=-1){
                    if(downloadStatus==STATUS_STOP){
                        //停止下载
                        break;
                    }
                    randomAccessFile.write(buff,0,len);
                    //更新数据库下载信息
                    downloadInfo.setProgress(downloadInfo.getProgress()+len);
                    //更改为正在下载中
                    downloadInfo.setStatus(1);
                    DownloadDBManager.getsInstance().update(downloadInfo);
                    //发送更新进度消息
                    sendMessage(DownloadStatus.PROGRESS,len);
                }
                if(downloadStatus==STATUS_START){
                    //在下载状态下完成下载，
                    //删除数据库中这条线程下载信息
                    DownloadDBManager.getsInstance().delete(downloadInfo);
                    //发送成功消息
                    sendMessage(DownloadStatus.THEARDSUCCESS,null);
                }
            }else{
                //发送错误消息
                sendMessage(DownloadStatus.FAIL,"response is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
            //发送错误消息
            sendMessage(DownloadStatus.FAIL,e.getMessage());
        }
    }

    /**
     * 发送消息给handler
     * @param what
     * @param obj
     */
    private void sendMessage(int what,Object obj){
        Message message=new Message();
        message.what=what;
        message.obj=obj;
        handler.sendMessage(message);
    }

    /**
     * 
     */
    public void stop() {
        downloadStatus=STATUS_STOP;
    }

}