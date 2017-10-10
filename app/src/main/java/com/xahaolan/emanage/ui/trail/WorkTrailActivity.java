package com.xahaolan.emanage.ui.trail;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.services.TrailServices;
import com.xahaolan.emanage.manager.MapManage;
import com.xahaolan.emanage.utils.common.DateUtil;
import com.xahaolan.emanage.utils.common.LogUtils;
import com.xahaolan.emanage.utils.common.ToastUtils;
import com.xahaolan.emanage.utils.mine.AppUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/3.    工作轨迹
 */

public class WorkTrailActivity extends BaseActivity implements LocationSource, AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener {
    private static final String TAG = WorkTrailActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    private Intent intent;
    private RelativeLayout time_layout;
    private TextView time_text;
    private TextView pos_list_text;
    private TextView trail_text;
    private TextView location_text;

    private MapManage mapManage;
    private MapView map_view;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private GeocodeSearch geocoderSearch;
    private Double latitude; //经度
    private Double longtitude; //纬度
    private String locAddress;
    private int personId;
    private List<Map<String, Object>> locList; //
    private List<String> timeList;
    private String[] timeArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_work_trail);
        initMap(savedInstanceState);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "", R.color.baseTextMain, "位置轨迹", R.color.baseTextMain, "选择员工", R.color.textRed, 0);
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
                Bundle bundle = new Bundle();
                bundle.putInt("sltType",1);
                MyUtils.jump(context, SltDepartmentActivity.class, bundle, false, null);
            }
        });
    }

    @Override
    public void initData() {
        time_text.setText(DateUtil.getCurrentDateStr(MyConstant.DATE_FORMAT_YMD) + " " + DateUtil.getWeekNumber(System.currentTimeMillis()));
        personId = AppUtils.getPersonId(context);
    }

    public void initMap(Bundle savedInstanceState) {
        mapManage = new MapManage(this, map_view);
        mapManage.createMapView(savedInstanceState);
        aMap = mapManage.initAmap();
        mapManage.setUiSettings();
        mapManage.changeZoom(10);
    }

    public void initLocation() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            /* slt time */
            case R.id.work_trail_time_layout:

                break;
            /* 位置列表 */
            case R.id.work_trail_pos_list_text:
                Bundle bundle = new Bundle();
                bundle.putSerializable("LogList", (Serializable) locList);
                MyUtils.jump(context, PosListActivity.class, bundle, false, null);
                break;
            /* 工作轨迹 */
            case R.id.work_trail_trail_text:
                requestGetPersonTracePlot();
                break;
            /* 立即定位 */
            case R.id.work_trail_location_text:
                initLocation();
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int employeeId = intent.getIntExtra("employeeId", 0);
        if (employeeId != 0) {
            personId = employeeId;
        }
        requestGetPersonNewLoc();
    }

    /**
     * 获取用户最新位置
     */
    public void requestGetPersonNewLoc() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new TrailServices(context).queryUserLocService(personId, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    Map<String, Object> locData = (Map<String, Object>) msg.obj;
                    if (locData != null) {
                        if (locData.get("resultList") != null) {
                            List<Map<String, Object>> resultList = (List<Map<String, Object>>) locData.get("resultList");
                            if (resultList != null && resultList.size() > 0) {
                                Map<String, Object> lastData = compareTime(resultList);
                                if (lastData == null || lastData.get("longitude") == null || lastData.get("latitude") == null || lastData.get("label") == null) {
                                    ToastUtils.showShort(context, "无法获取用户最新位置");
                                    return;
                                }
                                longtitude = Double.parseDouble(lastData.get("longitude") + "");
                                latitude = Double.parseDouble(lastData.get("latitude") + "");
                                locAddress = (String) lastData.get("label");
                                addMarker(latitude, longtitude, DateUtil.getCurrentDateStr(MyConstant.DATE_FORMAT_YMD_HM), locAddress);
                            }
                        }
                    } else if (msg.what == MyConstant.REQUEST_FIELD) {
                        String errMsg = (String) msg.obj;
                        ToastUtils.showShort(context, errMsg);
                        if (errMsg.equals("session过期")) {
                            BaseActivity.loginOut(context);
                        }
                    } else if (msg.what == MyConstant.REQUEST_ERROR) {
                        String errMsg = (String) msg.obj;
                        ToastUtils.showShort(context, errMsg);
                    }
                }
            }
        });
    }

    /**
     * 比对时间（获取最后一次位置数据）
     *
     * @param compareList
     * @return
     */
    public Map<String, Object> compareTime(List<Map<String, Object>> compareList) {
        String tempTime = "";
        timeArr = new String[compareList.size()];
        for (int a = 0; a < compareList.size(); a++) {
            Map<String, Object> compareData = compareList.get(a);
            if (compareData.get("createdate") != null) {
                String reTimeStr = (String) compareData.get("createdate");
                timeArr[a] = reTimeStr;
            }
        }
        for (int i = 0; i < timeArr.length; i++) {
            for (int j = i; j < timeArr.length; j++) {
                if (DateUtil.getLongTime(timeArr[i]) < DateUtil.getLongTime(timeArr[j])) {
                    tempTime = timeArr[i];
                    timeArr[i] = timeArr[j];
                    timeArr[j] = tempTime;
                }
            }
        }
        for (int a = 0; a < compareList.size(); a++) {
            Map<String, Object> compareData = compareList.get(a);
            if (compareData.get("createdate") != null) {
                String reTimeStr = (String) compareData.get("createdate");
                if (reTimeStr.equals(timeArr[0])) {
                    return compareData;
                }
            }
        }
        return null;
    }

    /**
     * 获取用户当天轨迹图
     */

    public void requestGetPersonTracePlot() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new TrailServices(context).queryUserRouteService(personId, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    Map<String, Object> response = (Map<String, Object>) msg.obj;
                    if (response != null){
                        if (response.get("resultList") != null){
                            locList = (List<Map<String, Object>>) response.get("resultList");
                            if (locList != null && locList.size() > 0) {
                                drawPersonTracePlot();
                            }else {
                                ToastUtils.showShort(context, "未生成轨迹路线亲");
                            }
                        }
                    }
                } else if (msg.what == MyConstant.REQUEST_FIELD) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                    if (errMsg.equals("session过期")) {
                        BaseActivity.loginOut(context);
                    }
                } else if (msg.what == MyConstant.REQUEST_ERROR) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                }
            }
        });
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                longtitude = amapLocation.getLongitude();
                latitude = amapLocation.getLatitude();
                LogUtils.e(TAG, "经度 ：" + longtitude + ",纬度 ：" + latitude);

                getAddress(new LatLonPoint(latitude, longtitude));
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Toast.makeText(this, errText, Toast.LENGTH_LONG).show();
                LogUtils.e(TAG, errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }

    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
    }

    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                locAddress = result.getRegeocodeAddress().getFormatAddress();
                /* add marker */
//                addMarker(latitude, longtitude, DateUtil.getCurrentDateStr(MyConstant.DATE_FORMAT_YMD_HM), locAddress);
                LogUtils.e(TAG, "当前定位地址 ：" + locAddress);
            } else {
                ToastUtils.showShort(context, "对不起，没有搜索到相关数据！");
            }
        } else {
            ToastUtils.showShort(context, rCode + "");
            LogUtils.e(TAG, "逆地理编码error : " + rCode);
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        map_view.onPause();
        deactivate();
    }

    /**
     * add  marker
     *
     * @param markerLat
     * @param markerLng
     * @param title
     * @param content
     */
    public void addMarker(Double markerLat, Double markerLng, String title, String content) {
        aMap.clear();
        mapManage.addMarker(markerLat, markerLng, title, content, true);
        /* 改变地图的中心点 */
        mapManage.setMapCenter(markerLat, markerLng);
//        mapManage.setMarkerClickListener(new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (msg.what == 222) {
//                    Marker marker = (Marker) msg.obj;
//                    Toast.makeText(context, "marker点击生效", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//        mapManage.setMarkerDragListener(new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (msg.what == 111) {
//                    Toast.makeText(context, "当marker开始被拖动时回调此方法", Toast.LENGTH_LONG).show();
//                } else if (msg.what == 222) {
//                    Toast.makeText(context, "在marker拖动完成后回调此方法, 这个marker的位置可以通过getPosition()方法返回", Toast.LENGTH_LONG).show();
//                } else if (msg.what == 333) {
//                    Toast.makeText(context, "在marker拖动过程中回调此方法, 这个marker的位置可以通过getPosition()方法返回。", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
    }

    /**
     * 绘制轨迹
     */
    public void drawPersonTracePlot() {
        aMap.clear();
        List<LatLng> latLngs = new ArrayList<LatLng>();
        for (int i = 0; i < locList.size();i++) {
            Map<String,Object> locData = locList.get(i);
            Double latitude = Double.parseDouble(locData.get("latitude")+"");
            Double longtitude = Double.parseDouble(locData.get("longitude")+"");
            latLngs.add(new LatLng(latitude, longtitude));
            if (i == 0){
                /* 改变地图的中心点 */
                mapManage.setMapCenter(latitude,longtitude);
            }
        }
        mapManage.setPolyLine(latLngs, 10);
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
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }
}
