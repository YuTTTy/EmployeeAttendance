package com.xahaolan.emanage.ui.trail;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.manager.map.MapManager;
import com.xahaolan.emanage.utils.common.LogUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;

/**
 * Created by helinjie on 2017/9/3.    工作轨迹
 */

public class WorkTrailActivity extends BaseActivity {
    private static final String TAG = WorkTrailActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    private MapView mapView;
    private RelativeLayout time_layout;
    private TextView time_text;
    private TextView pos_list_text;
    private TextView trail_text;
    private TextView location_text;

    /*百度地图*/
    private BaiduMap baiduMap;
    /*定位相关*/
    LocationClient mLocClient;
    private Double locLng; //经度
    private Double locLat; //纬度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_work_trail);
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
        loadMap();
    }

    public void loadMap() {
        mapView = (MapView) findViewById(R.id.clock_card_baidumap);
        MapManager mapUtils = new MapManager(context, mapView);
        baiduMap = mapUtils.initMap();//初始化基础地图
        mLocClient = mapUtils.initLocation();//初始化定位
        /*设置定位监听*/
        setLocationListener();
    }

    /**
     * 设置定位监听
     */
    public void setLocationListener() {
        mLocClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                // map view 销毁后不在处理新接收的位置
                if (location == null || mapView == null)
                    return;
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(100).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();
                baiduMap.setMyLocationData(locData);

                locLat = location.getLatitude();
                locLng = location.getLongitude();
                LatLng locLatLng = new LatLng(locLat, locLng);
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(locLatLng);
                baiduMap.animateMapStatus(u);
                LogUtils.e(TAG, "定位点坐标：经度 = " + locLng + ",纬度 = " + locLat);

            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
    }
}
