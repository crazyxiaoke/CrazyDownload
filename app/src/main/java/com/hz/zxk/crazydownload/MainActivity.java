package com.hz.zxk.crazydownload;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hz.zxk.crazydownload.adapter.DownloadListAdapter;
import com.hz.zxk.crazydownload.entiy.ApkInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private DownloadListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdapter = new DownloadListAdapter(this, this);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        List<ApkInfo> datas = initData();
        mAdapter.refreshDatas(datas);
    }

    private List<ApkInfo> initData() {
        List<ApkInfo> list = new ArrayList<>();
        ApkInfo jjcj = new ApkInfo();
        jjcj.setName("将军财经");
        jjcj.setUrl("http://gyxz.ukdj3d.cn/a31/rj_zmy1/jiangjuncaijing.apk");
        list.add(jjcj);
        ApkInfo aqy = new ApkInfo();
        aqy.setName("爱奇异");
        aqy.setUrl("http://gyxz.ro4uw.cn/hk1/rj_gyc1/aiqiyihd.apk");
        list.add(aqy);
        ApkInfo ypgpt = new ApkInfo();
        ypgpt.setName("优品股票通");
        ypgpt.setUrl("http://gyxz.ro4uw.cn/hk1/rj_yx1/youpingupiaotong.apk");
        list.add(ypgpt);
        ApkInfo qbs = new ApkInfo();
        qbs.setName("七八社");
        qbs.setUrl("http://gyxz.ro4uw.cn/hk/rj_xb1/qibashe.apk");
        list.add(qbs);
        return list;
    }
}
