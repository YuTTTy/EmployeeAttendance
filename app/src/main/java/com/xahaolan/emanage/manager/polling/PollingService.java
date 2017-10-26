package com.xahaolan.emanage.manager.polling;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.services.TrailServices;
import com.xahaolan.emanage.utils.common.DateUtil;
import com.xahaolan.emanage.utils.common.LogUtils;
import com.xahaolan.emanage.utils.mine.AppUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by helinjie on 2017/9/28.     轮询服务
 */

public class PollingService extends Service{
    private static final String TAG = PollingService.class.getSimpleName();
    public static final String ACTION = "com.ryantang.service.PollingService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onStart(Intent intent, int startId) {
//        new PollingThread().start();
        //上传位置数据
        getLocation();
    }
    /**
     * Polling thread
     * 模拟向Server轮询的异步线程
     *
     * @Author Ryan
     * @Create 2013-7-13 上午10:18:34
     */
    class PollingThread extends Thread {
        @Override
        public void run() {

        }
    }

    private int personId;//    员工id
    private String longitude;//   经度
    private String latitude;//    纬度
    private String label;//    位置文字信息
    private String autoflag = "0";   //上传方式（0：自动，1：手动）

    private LocationManager locationManager;
    private String locationProvider;

    public void getLocation() {
        //获取地理位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.PASSIVE_PROVIDER)) {
            //如果是PASSIVE定位
            locationProvider = LocationManager.PASSIVE_PROVIDER;
        }else {
            LogUtils.e(TAG, "没有可用的位置提供器");
            return;
        }
        //获取Location
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            //不为空,显示地理位置经纬度
            latitude = location.getLatitude() + "";
            longitude = location.getLongitude() + "";
            LogUtils.e(TAG, "首次获取经纬度 ：" + latitude + ", " + longitude);
            getAddress(location);
//            getAddress(new LatLonPoint(Double.parseDouble(latitude),Double.parseDouble(longitude)));
        }else {
            LogUtils.e(TAG, "首次获取经纬度值为空，开启位置监听");
            //监视地理位置变化
            locationManager.requestLocationUpdates(locationProvider, 3000, 0, locationListener);
        }
    }

    /**
     * 显示地理位置经度和纬度信息
     *
     * @param location
     */
    private void getAddress(final Location location) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //组装反向地理编码的接口位置
                    StringBuilder url = new StringBuilder();
                    url.append("http://maps.googleapis.com/maps/api/geocode/json?latlng=");
                    url.append(location.getLatitude()).append(",");
                    url.append(location.getLongitude());
                    url.append("&sensor=false");
                    HttpClient client = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(url.toString());
                    httpGet.addHeader("Accept-Language", "zh-CN");
                    HttpResponse response = client.execute(httpGet);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = response.getEntity();
                        String res = EntityUtils.toString(entity);
                        //解析
                        JSONObject jsonObject = new JSONObject(res);
                        //获取results节点下的位置信息
                        JSONArray resultArray = jsonObject.getJSONArray("results");
                        if (resultArray.length() > 0) {
                            JSONObject obj = resultArray.getJSONObject(0);
                            //取出格式化后的位置数据
                            label = obj.getString("formatted_address");
                            LogUtils.e(TAG, "上传位置地址 ：" + label);
                            Looper.prepare();
                            requestLoadLoc();
//                            new Handler(){
//                                @Override
//                                public void handleMessage(Message msg) {
//                                    super.handleMessage(msg);
//                                    requestLoadLoc();
//                                }
//                            };
                        }
                    }else {
                        LogUtils.e(TAG, "上传位置地址失败 ："+response.getStatusLine().getStatusCode());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     * 上传位置
     */
    public void requestLoadLoc() {
        personId = AppUtils.getPersonId(getApplicationContext());
        LogUtils.e(TAG, "上传时间 ：" + DateUtil.getStringByFormat(System.currentTimeMillis(),MyConstant.DATE_FORMAT_YMDHMS));
        new TrailServices(getApplicationContext()).uploadLocService(personId, longitude, latitude,
                label, autoflag, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (msg.what == MyConstant.REQUEST_SUCCESS) {
                            LogUtils.e(TAG, "上传位置成功");
                        } else if (msg.what == MyConstant.REQUEST_FIELD) {
                            String errMsg = (String) msg.obj;
                            LogUtils.e(TAG, "上传位置失败 ：" + errMsg);
                        } else if (msg.what == MyConstant.REQUEST_ERROR) {
                            String errMsg = (String) msg.obj;
                            LogUtils.e(TAG, "上传位置错误 ：" + errMsg);
                        }
                    }
                });
    }

    /**
     //     * LocationListern监听器
     //     * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
     //     */

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {
            LogUtils.e(TAG, "onStatusChanged ：" + latitude + ", " + longitude);
        }

        @Override
        public void onProviderEnabled(String provider) {
            LogUtils.e(TAG, "onProviderEnabled ：" + latitude + ", " + longitude);
        }

        @Override
        public void onProviderDisabled(String provider) {
            LogUtils.e(TAG, "onProviderDisabled ：" + latitude + ", " + longitude);
        }

        @Override
        public void onLocationChanged(Location location) {
            LogUtils.e(TAG, "定位经纬度 ：" + latitude + ", " + longitude);
            //如果位置发生变化,重新显示
            if (latitude == null || latitude.equals("")){

            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            //移除监听器
            locationManager.removeUpdates(locationListener);
        }
        System.out.println("Service:onDestroy");
    }
}

//import android.app.Service;
//import android.content.Intent;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//
//import com.amap.api.location.AMapLocation;
//import com.amap.api.location.AMapLocationClient;
//import com.amap.api.location.AMapLocationClientOption;
//import com.amap.api.location.AMapLocationListener;
//import com.xahaolan.emanage.base.MyConstant;
//import com.xahaolan.emanage.http.services.TrailServices;
//import com.xahaolan.emanage.utils.common.DateUtil;
//import com.xahaolan.emanage.utils.common.LogUtils;
//import com.xahaolan.emanage.utils.mine.AppUtils;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
///**
// * Created by helinjie on 2017/9/28.     轮询服务
// */
//
//public class PollingService extends Service{
//    private static final String TAG = PollingService.class.getSimpleName();
//    public static final String ACTION = "com.ryantang.service.PollingService";
//
//    private AMapLocationClient locationClient = null;
//    private AMapLocationClientOption locationOption = null;
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//    }
//
//    @Override
//    public void onStart(Intent intent, int startId) {
////        new PollingThread().start();
//        //上传位置数据
//        getLocation();
//
//    }
//
//    private int personId;//    员工id
//    private String longitude;//   经度
//    private String latitude;//    纬度
//    private String label;//    位置文字信息
//    private String autoflag = "0";   //上传方式（0：自动，1：手动）
//
//    public void getLocation() {
//        //初始化client
//        locationClient = new AMapLocationClient(this.getApplicationContext());
//        getDefaultOption();
//        //设置定位参数
//        locationClient.setLocationOption(locationOption);
//        // 设置定位监听
//        locationClient.setLocationListener(locationListener);
//        /* 开始定位 */
//        startLocation();
//    }
//
//    /**
//     * 默认的定位参数
//     *
//     * @author hongming.wang
//     * @since 2.8.0
//     */
//    private void getDefaultOption() {
//        locationOption = new AMapLocationClientOption();
//        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
//        locationOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
//        locationOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
//        locationOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
//        locationOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
//        locationOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
//        locationOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
//        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
//        locationOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
//        locationOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
//        locationOption.setLocationCacheEnable(false); //可选，设置是否使用缓存定位，默认为true
//    }
//
//    /**
//     * 定位监听
//     */
//    AMapLocationListener locationListener = new AMapLocationListener() {
//        @Override
//        public void onLocationChanged(AMapLocation location) {
//            if (null != location) {
//                if (location.getErrorCode() == 0) {
//                    longitude = location.getLongitude() + "";
//                    latitude = location.getLatitude() + "";
//                    label = location.getAddress();
//                    LogUtils.e(TAG,"经纬度 ：" + longitude + ", " + latitude + " ; 地址：" + label);
//                    requestLoadLoc();
//                } else {
//                    LogUtils.e(TAG, "定位失败 ：" + location.getErrorCode());
//                }
//            } else {
//                LogUtils.e(TAG, "定位失败，loc is null");
//            }
//        }
//    };
//
//    /**
//     * 上传位置
//     */
//    public void requestLoadLoc() {
//        personId = AppUtils.getPersonId(getApplicationContext());
//        LogUtils.e(TAG, "上传时间 ：" + DateUtil.getStringByFormat(System.currentTimeMillis(),MyConstant.DATE_FORMAT_YMDHMS));
//        new TrailServices(getApplicationContext()).uploadLocService(personId, longitude, latitude,
//                label, autoflag, new Handler() {
//                    @Override
//                    public void handleMessage(Message msg) {
//                        super.handleMessage(msg);
//                        if (msg.what == MyConstant.REQUEST_SUCCESS) {
//                            LogUtils.e(TAG, "上传位置成功");
//                        } else if (msg.what == MyConstant.REQUEST_FIELD) {
//                            String errMsg = (String) msg.obj;
//                            LogUtils.e(TAG, "上传位置失败 ：" + errMsg);
//                        } else if (msg.what == MyConstant.REQUEST_ERROR) {
//                            String errMsg = (String) msg.obj;
//                            LogUtils.e(TAG, "上传位置错误 ：" + errMsg);
//                        }
//                    }
//                });
//    }
//
//    /**
//     *    start location
//     */
//    public void startLocation() {
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                locationClient.startLocation();
//            }
//        },3000);
//    }
//
//    /**
//     * 停止定位
//     *
//     * @author hongming.wang
//     * @since 2.8.0
//     */
//    private void stopLocation() {
//        if (null != locationClient) {
//            // 停止定位
//            locationClient.stopLocation();
//            /**
//             * 如果AMapLocationClient是在当前Activity实例化的，
//             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
//             */
//            locationClient.onDestroy();
//            locationClient = null;
//            locationOption = null;
//        }
//    }
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        stopLocation();
//        System.out.println("Service:onDestroy");
//    }
//}
