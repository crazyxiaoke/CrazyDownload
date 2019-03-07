package com.hz.zxk.crazydownload.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hz.zxk.crazydownload.R;
import com.hz.zxk.crazydownload.entiy.ApkInfo;
import com.hz.zxk.lib_download.DownloadDispatch;
import com.hz.zxk.lib_download.callback.DownloadCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
 * Created by zxk on 19-3-4.
 */
public class DownloadListAdapter extends RecyclerView.Adapter<DownloadListAdapter.ViewHolder> {
    private Context mContext;
    private Activity mActivity;
    private List<ApkInfo> datas;

    private OnClickListener mOnClickListener;

    public DownloadListAdapter(Activity activity, Context context) {
        mActivity = activity;
        mContext = context;
        datas = new ArrayList<>();
    }

    public void refreshDatas(List<ApkInfo> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public ApkInfo getData(int position) {
        return datas.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_download, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final ApkInfo apkInfo = datas.get(i);
        viewHolder.name.setText(apkInfo.getName());
        if (apkInfo.getDownloadStatus() == 0) {
            viewHolder.downloadBtn.setText("下载");
        } else {
            viewHolder.downloadBtn.setText("暂停");
        }
        viewHolder.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (apkInfo.getDownloadStatus() == 0) {
                    apkInfo.setDownloadStatus(1);
                    viewHolder.downloadBtn.setText("暂停");
                    DownloadDispatch.getInstance().download(apkInfo.getUrl(), new DownloadCallback() {
                        @Override
                        public void start() {
                            viewHolder.networkSpeed.setText("开始下载");
                        }

                        @Override
                        public void pending() {
                            viewHolder.networkSpeed.setText("等待下载");
                        }

                        @Override
                        public void success(File file) {
                            viewHolder.networkSpeed.setText("下载完成");
                        }

                        @Override
                        public void fail(int code, String errorMsg) {
                            viewHolder.networkSpeed.setText("下载失败");
                        }

                        @Override
                        public void cancel() {
                            viewHolder.networkSpeed.setText("取消下载");
                        }

                        @Override
                        public void progress(int progress) {
                            viewHolder.progressBar.setProgress(progress);
                        }

                        @Override
                        public void speedNetwork(String network) {
                            viewHolder.networkSpeed.setText(network);
                        }
                    });
                } else {
                    apkInfo.setDownloadStatus(0);
                    viewHolder.downloadBtn.setText("下载");
                    DownloadDispatch.getInstance().stop(apkInfo.getUrl());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ProgressBar progressBar;
        private TextView networkSpeed;
        private Button downloadBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            progressBar = itemView.findViewById(R.id.progress);
            networkSpeed = itemView.findViewById(R.id.networkspeed);
            downloadBtn = itemView.findViewById(R.id.download);
        }
    }

    public interface OnClickListener {
        void onClick(int position);
    }
}
