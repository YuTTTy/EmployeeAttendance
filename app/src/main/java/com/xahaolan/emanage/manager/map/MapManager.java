package com.xahaolan.emanage.manager.map;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.xahaolan.emanage.manager.map.view.ZoomControlsView;
import com.xahaolan.emanage.utils.common.LogUtils;

/**
 * Created by Administrator on 2016/7/6.      百度地图
 */
public class MapManager {
    private static String TAG = MapManager.class.getSimpleName();
    public Context context;

    /*百度地图*/
    private BaiduMap baiduMap;
    private MapView mapView;
    private ZoomControlsView zcvZomm;//自定义缩放控件
    /*定位相关*/
    LocationClient mLocClient;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    boolean isFirstLoc = true;// 是否首次定位

    /*定位经纬度*/
    public Double locLat;
    public Double locLng;

    public MapManager(Context context) {
        this.context = context;
    }

    public MapManager(Context context, MapView mapView) {
        this.context = context;
        this.mapView = mapView;
    }

    public MapManager(Context context, MapView mapView, ZoomControlsView zcvZomm) {
        this.context = context;
        this.mapView = mapView;
        this.zcvZomm = zcvZomm;
    }

    /**
     * 初始化基础地图
     */
    public BaiduMap initMap() {
        /*地图初始化*/
        mapView.showZoomControls(false);//隐藏缩放控件
        //获取地图对象控制器
        baiduMap = mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        baiduMap.setBuildingsEnabled(true);//设置显示楼体
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(14f));//设置地图状态
//        //获取缩放控件
//        zcvZomm.setMapView(mapView);//设置百度地图控件

        /*删除内部控件*/
        deleteChildView();
        return baiduMap;
    }

    /**
     * 初始化基础地图并设定初始位置
     */
    public BaiduMap initStartMap() {
        /*地图初始化*/
        mapView.showZoomControls(false);//隐藏缩放控件
        //获取地图对象控制器
        baiduMap = mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        baiduMap.setBuildingsEnabled(true);//设置显示楼体
        return baiduMap;
    }

    /**
     * 初始化定位
     */
    public LocationClient initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);        //是否打开GPS
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//设置坐标类型 可选，默认gcj02，设置返回的定位结果坐标系
//        option.setScanSpan(100000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setPriority(LocationClientOption.NetWorkFirst);  //设置定位优先级
        option.setProdName("LocationDemo"); //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要

        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(context);

        mLocClient.setLocOption(option);
        mLocClient.start();

        /*定位图标设置*/
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;//普通
//        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;//跟随
//        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;//罗盘
        mCurrentMarker = null;// 传入null则，恢复默认图标
//        mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);//修改为自定义marker
        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, true, mCurrentMarker));

        return mLocClient;
    }

    /**
     * 定位SDK监听函数
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

                if (isFirstLoc) {
                    isFirstLoc = false;
                    locLat = location.getLatitude();
                    locLng = location.getLongitude();
                    LatLng ll = new LatLng(locLat, locLng);
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                    baiduMap.animateMapStatus(u);
                }
                LogUtils.e(TAG, "定位点坐标：经度 = " + locLng + ",纬度 = " + locLat);
            }
        });
    }

    /**
     * 删除内部控件
     */
    public void deleteChildView() {
//        // 隐藏缩放控件
//        mapView.showZoomControls(false);
//        // 隐藏比例尺控件
//        mapView.removeViewAt(3);
        // 删除百度地图logo
        mapView.removeViewAt(1);
        // 隐藏指南针
        UiSettings mUiSettings = baiduMap.getUiSettings();
        mUiSettings.setCompassEnabled(false);
    }

    public String encodeAddress; //编码后的地址

    /**
     * 地理编码功能
     */
    public GeoCoder geoCoder(Double lng, Double lat, String city, String address) {
        // 初始化搜索模块，注册事件监听
        GeoCoder mSearch = GeoCoder.newInstance();// 搜索模块，也可去掉地图模块独立使用
        // 反Geo搜索
        if (lat != null && lng != null) {
            LatLng ptCenter = new LatLng(lat, lng);
            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(ptCenter));
        }
        // Geo搜索
        if (address != null && !address.equals("")) {
            mSearch.geocode(new GeoCodeOption().city(city).address(address));
        }
        return mSearch;
    }
}
