package com.xahaolan.emanage.ui.trail;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.xahaolan.emanage.R;
import com.xahaolan.emanage.adapter.PosListAdapter;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.utils.mine.MyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/10.
 */

public class PosListActivity extends BaseActivity {
    private static final String TAG = PosListActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;

    private ListView list_view;
    private PosListAdapter adapter;
    private List<Map<String,Object>> dataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_trail_pos_list);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ic_launcher_round, "", R.color.baseTextMain, "实时位置", R.color.baseTextMain, "选择员工", R.color.textRed, 0);
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
        dataList = new ArrayList<>();
        adapter = new PosListAdapter(context);
        list_view.setAdapter(adapter);
    }
}
