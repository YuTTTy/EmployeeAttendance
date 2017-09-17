package com.xahaolan.emanage.ui.trail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.adapter.PosListAdapter;
import com.xahaolan.emanage.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/10.        实时位置
 */

public class PosListActivity extends BaseActivity {
    private static final String TAG = PosListActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    private Intent intent;

    private ListView list_view;
    private PosListAdapter adapter;
    private List<Map<String,Object>> locList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_trail_pos_list);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "", R.color.baseTextMain, "实时位置", R.color.baseTextMain, "", R.color.textRed, R.drawable.search);
    }

    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        list_view = (ListView) findViewById(R.id.trail_pos_listview);
    }

    @Override
    public void initData() {
        intent = getIntent();
        locList = (List<Map<String, Object>>) intent.getSerializableExtra("LogList");
        adapter = new PosListAdapter(context);
        list_view.setAdapter(adapter);
        adapter.resetList(locList);
        adapter.notifyDataSetChanged();
    }
}
