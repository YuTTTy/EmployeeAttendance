package com.xahaolan.emanage.manager.map;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by Administrator on 2016/8/29.    定 位
 */
public class ObtainLocUtil {
    private static final String TAG = ObtainLocUtil.class.getSimpleName();
    private Context context;
    public LocationClient locationClient = null;
    private static int LOCATION_COUTNS = 0;

    public ObtainLocUtil(Context context) {
        this.context = context;
    }
    /**
     * 初始化定位
     */
    public LocationClient initLocation() {
        locationClient = new LocationClient(context);
        //设置定位条件
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);        //是否打开GPS
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//设置坐标类型 可选，默认gcj02，设置返回的定位结果坐标系
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setPriority(LocationClientOption.NetWorkFirst);  //设置定位优先级
        option.setProdName("LocationDemo"); //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
//        option.setScanSpan(1000);    //设置定时定位的时间间隔。单位毫秒 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        locationClient.setLocOption(option);
        locationClient.start();
        return locationClient;
    }

    public void setListener(){
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location == null) {
                    return;
                }
                StringBuffer sb = new StringBuffer(256);
                sb.append("Time : ");
                sb.append(location.getTime());
                sb.append("\nError code : ");
                sb.append(location.getLocType());
                sb.append("\nLatitude : ");
                sb.append(location.getLatitude());
                sb.append("\nLontitude : ");
                sb.append(location.getLongitude());
                sb.append("\nRadius : ");
                sb.append(location.getRadius());
                if (location.getLocType() == BDLocation.TypeGpsLocation){
                    sb.append("\nSpeed : ");
                    sb.append(location.getSpeed());
                    sb.append("\nSatellite : ");
                    sb.append(location.getSatelliteNumber());
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
                    sb.append("\nAddress : ");
                    sb.append(location.getAddrStr());
                }
                LOCATION_COUTNS ++;
                sb.append("\n检查位置更新次数：");
                sb.append(String.valueOf(LOCATION_COUTNS));
            }
        });
    }

    /**
     *      打开关闭定位
     */
    public void openStopLoc(){
        if (locationClient == null) {
            return;
        }
        if (locationClient.isStarted()) {
            locationClient.stop();
        }else {
            locationClient.start();
	        /*
	                     *当所设的整数值大于等于1000（ms）时，定位SDK内部使用定时定位模式。
	                     *调用requestLocation( )后，每隔设定的时间，定位SDK就会进行一次定位。
	                     *如果定位SDK根据定位依据发现位置没有发生变化，就不会发起网络请求，
	                     *返回上一次定位的结果；如果发现位置改变，就进行网络请求进行定位，得到新的定位结果。
	                     *定时定位时，调用一次requestLocation，会定时监听到定位结果。
	                     */
            locationClient.requestLocation();
        }
    }
}
