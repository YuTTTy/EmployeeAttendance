package com.xahaolan.emanage.ui.checkwork.clockrecord;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.adapter.ClockDetailAdapter;
import com.xahaolan.emanage.adapter.ClockRecordAdapter;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.services.CheckWorkServices;
import com.xahaolan.emanage.utils.common.ToastUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/3.     打卡详情
 */

public class ClockDetailActivity  extends BaseActivity {
    private static final String TAG = ClockDetailActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    private Intent intent;

    private ListView list_view;
    private ClockDetailAdapter adapter;
    private List<Map<String, Object>> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_clock_detail);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ic_launcher_round, "返回", R.color.baseTextMain, "打卡记录", R.color.baseTextMain, "", R.color.baseTextMain, 0);
    }

    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        list_view = (ListView) findViewById(R.id.clock_record_listview);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyUtils.jump(context,ClockDetailActivity.class,new Bundle(),false,null);
            }
        });
    }

    @Override
    public void initData() {
        intent = getIntent();
        dataList = new ArrayList<>();
        adapter = new ClockDetailAdapter(context);
        list_view.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        requestRecordDetail();
    }

    public void requestRecordDetail() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
//        new CheckWorkServices(context).addClockDetailService(personId, new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
//                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
//                }
//                if (msg.what == MyConstant.REQUEST_SUCCESS) {
//                    dataList = (List<Map<String, Object>>) msg.obj;
//                    if (dataList != null && dataList.size() > 0) {
//                        adapter.resetList(dataList);
//                        adapter.notifyDataSetChanged();
//                    }
//                } else if (msg.what == MyConstant.REQUEST_FIELD) {
//                    String errMsg = (String) msg.obj;
//                    ToastUtils.showShort(context, errMsg);
//                } else if (msg.what == MyConstant.REQUEST_ERROR) {
//                    String errMsg = (String) msg.obj;
//                    ToastUtils.showShort(context, errMsg);
//                }
//            }
//        });
    }
}
