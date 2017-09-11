package com.xahaolan.emanage.ui.trail;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.manager.MapManage;
import com.xahaolan.emanage.utils.mine.MyUtils;

/**
 * Created by helinjie on 2017/9/3.    工作轨迹
 */

public class WorkTrailActivity extends BaseActivity {
    private static final String TAG = WorkTrailActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    private RelativeLayout time_layout;
    private TextView time_text;
    private TextView pos_list_text;
    private TextView trail_text;
    private TextView location_text;

    private MapManage mapManage;
    private MapView map_view;
    private AMap aMap;
    private Double locLng; //经度
    private Double locLat; //纬度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_work_trail);
        initMap(savedInstanceState);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ic_launcher_round, "", R.color.baseTextMain, "位置轨迹", R.color.baseTextMain, "选择员工", R.color.textRed, 0);
    }

    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        map_view = (MapView) findViewById(R.id.clock_card_baidumap);
        time_layout = (RelativeLayout) findViewById(R.id.work_trail_time_layout);
        time_layout.setOnClickListener(this);
        time_text = (TextView) findViewById(R.id.work_trail_time_text);
        pos_list_text = (TextView) findViewById(R.id.work_trail_pos_list_text);
        pos_list_text.setOnClickListener(this);
        trail_text = (TextView) findViewById(R.id.work_trail_trail_text);
        trail_text.setOnClickListener(this);
        location_text = (TextView) findViewById(R.id.work_trail_location_text);
        location_text.setOnClickListener(this);

        right_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtils.jump(context, SltDepartmentActivity.class, new Bundle(), false,null);
            }
        });
    }

    @Override
    public void initData() {

    }
    public void initMap(Bundle savedInstanceState){
        mapManage = new MapManage(this, map_view);
        mapManage.createMapView(savedInstanceState);
        aMap = mapManage.initAmap();
        mapManage.setUiSettings();
        mapManage.changeZoom(14);
        mapManage.initLocation();
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.work_trail_time_layout:

                break;
            case R.id.work_trail_pos_list_text:
                MyUtils.jump(context, PosListActivity.class, new Bundle(),false, null);
                break;
            case R.id.work_trail_trail_text:

                break;
            case R.id.work_trail_location_text:

                break;
        }
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        map_view.onResume();
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        map_view.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        map_view.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        map_view.onDestroy();
    }
}
