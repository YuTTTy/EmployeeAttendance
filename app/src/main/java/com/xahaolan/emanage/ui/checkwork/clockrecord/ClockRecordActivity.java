package com.xahaolan.emanage.ui.checkwork.clockrecord;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.adapter.ClockRecordAdapter;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.utils.mine.MyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/3.打卡记录
 */

public class ClockRecordActivity extends BaseActivity {
    private static final String TAG = ClockRecordActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;

    private ListView list_view;
    private ClockRecordAdapter adapter;
    private List<Map<String, Object>> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_clock_record);
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
        dataList = new ArrayList<>();
        adapter = new ClockRecordAdapter(context);
        list_view.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestRecordList();
    }

    public void requestRecordList() {

    }
}
