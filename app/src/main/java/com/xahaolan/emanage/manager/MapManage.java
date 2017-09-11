package com.xahaolan.emanage.manager;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.LinearInterpolator;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.RotateAnimation;

import java.util.List;

/**
 * Created by aiodiy on 2017/9/7.
 */

public class MapManage {
    private static final String TAG = MapManage.class.getSimpleName();
    private Context context;
    private MapView mapView;
    private AMap aMap;

    public MapManage(Context context, MapView mapView) {
        this.context = context;
        this.mapView = mapView;
    }

    /**
     * 此方法必须重写,虚拟机需要在很多情况下保存地图绘制的当前状态
     *
     * @param savedInstanceState
     */
    public void createMapView(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);//
    }

    /**
     * 初始化地图控制器对象
     */
    public AMap initAmap() {
        if (aMap == null) {
            aMap = mapView.getMap(); //
        }
        return aMap;
    }

    /**
     * 控件交互
     */
    public void setUiSettings() {
        UiSettings mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        //缩放按钮
        mUiSettings.setZoomControlsEnabled(true);
        //是否允许显示缩放按钮
        mUiSettings.setZoomControlsEnabled(true);
        //设置缩放按钮的位置
        mUiSettings.setZoomPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
        //获取缩放按钮的位置
        int zoomPosition = mUiSettings.getZoomPosition();

        //指南针 默认不显示
        mUiSettings.setCompassEnabled(true);

        //定位按钮
//        aMap.setLocationSource(this);//通过aMap对象设置定位数据源的监听
        mUiSettings.setMyLocationButtonEnabled(true); //显示默认的定位按钮
        aMap.setMyLocationEnabled(true);// 可触发定位并显示当前位置

        //比例尺控件   （最大比例是1：10m,最小比例是1：1000Km），位于地图右下角，可控制其显示与隐藏
        mUiSettings.setScaleControlsEnabled(true);//控制比例尺控件是否显示

        /* 地图Logo */
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);//设置logo位置
//        //Logo位置说明
//        AMapOptions.LOGO_POSITION_BOTTOM_LEFT
//        LOGO边缘MARGIN（左边）
//        AMapOptions.LOGO_MARGIN_BOTTOM
//        LOGO边缘MARGIN（底部
//        AMapOptions.LOGO_MARGIN_RIGHT
//        LOGO边缘MARGIN（右边）
//        AMapOptions.LOGO_POSITION_BOTTOM_CENTER
//        Logo位置（地图底部居中）
//        AMapOptions.LOGO_POSITION_BOTTOM_LEFT
//        Logo位置（地图左下角）
//        AMapOptions.LOGO_POSITION_BOTTOM_RIGHT
//        Logo位置（地图右下角）

        /* 控制手势生效与否 */
//        mUiSettings.setZoomGesturesEnabled(true);//缩放手势
//        mUiSettings.setScrollGesturesEnabled(true);//滑动手势
//        mUiSettings.setRotateGesturesEnabled(true);//旋转手势
//        mUiSettings.setTiltGesturesEnabled(false);//倾斜手势
        mUiSettings.setAllGesturesEnabled(true);//所有手势

//        /* 检测手势是否生效 */
//        mUiSettings.isZoomGesturesEnabled();//缩放手势
//        mUiSettings.isScrollGesturesEnabled();//滑动手势
//        mUiSettings.isRotateGesturesEnabled();//旋转手势
//        mUiSettings.isTiltGesturesEnabled();//倾斜手势

        //指定屏幕中心点的手势操作
//        aMap.setPointToCenter( int x, int y);//x、y均为屏幕坐标，屏幕左上角为坐标原点，即(0,0)点。
        //开启以中心点进行手势操作的方法
        aMap.getUiSettings().setGestureScaleByMapCenter(true);
    }

    /**
     * 改变地图的中心点
     *
     * @param latitude  纬度
     * @param longitude 经度
     */
    public void changeMapCenter(double latitude, double longitude) {
        //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(latitude, longitude), 18, 30, 0));
    }

    /**
     * 改变地图的缩放级别
     *
     * @param zoomValue 地图的缩放级别一共分为 17 级，从 3 到 19。数字越大，展示的图面信息越精细。
     */
    public void changeZoom(float zoomValue) {
        //设置希望展示的地图缩放级别
        CameraUpdate mCameraUpdate = CameraUpdateFactory.zoomTo(zoomValue);//
        //ZoomTo  缩放地图到指定的缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomValue));
//        //ZoomIn  缩放地图到当前缩放级别的上一级
//        aMap.moveCamera(CameraUpdateFactory.zoomIn());
    }

    /**
     * 限制地图的显示范围
     * 从地图 SDK V4.1.0 起新增了设置地图显示范围的方法，手机屏幕仅显示设定的地图范围
     * 注意：如果限制了地图显示范围，地图旋转手势将会失效。
     *
     * @param southLat
     * @param southLng
     * @param northLat
     * @param northLng
     */
    public void setLatlngBounds(double southLat, double southLng, double northLat, double northLng) {
        LatLng southwestLatLng = new LatLng(southLat, southLng);
        LatLng northeastLatLng = new LatLng(northLat, northLng);
        LatLngBounds latLngBounds = new LatLngBounds(southwestLatLng, northeastLatLng);
        aMap.setMapStatusLimits(latLngBounds);
    }

    /**
     * 改变地图默认显示区域
     *
     * @param lat
     * @param lng
     */
    public void changeDefaultRegion(double lat, double lng) {
        LatLng centerBJPoint = new LatLng(lat, lng);
        // 定义了一个配置 AMap 对象的参数类
        AMapOptions mapOptions = new AMapOptions();
        // 设置了一个可视范围的初始化位置
// CameraPosition 第一个参数： 目标位置的屏幕中心点经纬度坐标。
// CameraPosition 第二个参数： 目标可视区域的缩放级别
// CameraPosition 第三个参数： 目标可视区域的倾斜度，以角度为单位。
// CameraPosition 第四个参数： 可视区域指向的方向，以角度为单位，从正北向顺时针方向计算，从0度到360度
        mapOptions.camera(new CameraPosition(centerBJPoint, 10f, 0, 0));
//// 定义一个 MapView 对象，构造方法中传入 mapOptions 参数类
//        MapView mapView = new MapView(this, mapOptions);
//// 调用 onCreate方法 对 MapView LayoutParams 设置
//        mapView.onCreate(savedInstanceState);
    }

    /**
     * set location
     */
    public void initLocation() {
        /* 实现定位蓝点 */
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        //设置定位频次方法，单位：毫秒，默认值：1000毫秒，如果传小于1000的任何值将按照1000计算。该方法只会作用在会执行连续定位的工作模式上。
        long interval = 1000;
        myLocationStyle.interval(interval);

        /* 定位蓝点展现模式 */
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//只定位一次。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);//连续定位、且将视角移动到地图中心点，地图依照设备方向旋转，定位点会跟随设备移动。（1秒1次定位）
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
//        //以下三种模式从5.1.0版本开始提供
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。

        //设置是否显示定位小蓝点，用于满足只想使用定位，不想使用定位小蓝点的场景，设置false以后图面上不再有定位蓝点的概念，但是会持续回调位置信息。
        myLocationStyle.showMyLocation(true);//方法自5.1.0版本后支持

//        /* 自定义定位蓝点图标 */
//        myLocationStyle.myLocationIcon(BitmapDescriptor myLocationIcon);//设置定位蓝点的icon图标方法，需要用到BitmapDescriptor类对象作为参数。

//        /* 自定义定位蓝点图标的锚点 */
//        //锚点是指定位蓝点图标像素与定位蓝点坐标的关联点，例如需要将图标的左下方像素点与定位蓝点的经纬度关联在一起，通过如下方法传入（0.0,1.0）。图标左上点为像素原点。
//        float u = 0;
//        float v = 0;
//        myLocationStyle.anchor(u, v);//设置定位蓝点图标的锚点方法。

//        /* 精度圆圈的自定义 */
//        int frameColor = 0;
//        int fillColor = 0;
//        myLocationStyle.strokeColor(frameColor);//设置定位蓝点精度圆圈的边框颜色的方法。
//        myLocationStyle.radiusFillColor(fillColor);//设置定位蓝点精度圆圈的填充颜色的方法。
//        //精度圈边框宽度自定义方法如下
//        MyLocationStyle strokeWidth(float width);//设置定位蓝点精度圈的边框宽度的方法。

//        /* 定位的频次自定义 */
//        MyLocationStyle.LOCATION_TYPE_FOLLOW ;//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（默认1秒1次定位）
//        MyLocationStyle.LOCATION_TYPE_MAP_ROTATE;//连续定位、且将视角移动到地图中心点，地图依照设备方向旋转，定位点会跟随设备移动。（默认1秒1次定位）
//        MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE;//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（默认1秒1次定位）默认执行此种模式。

        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

    }

    /**
     * 绘制默认 Marker
     *
     * @param lat
     * @param lng
     * @param titleStr    点标记的标题
     * @param contentStr  点标记的内容
     * @param isDraggable 点标记是否可拖拽
     */
    public void addMarker(double lat, double lng, String titleStr, String contentStr, Boolean isDraggable) {
        /* Marker 常用属性 */
//        position 在地图上标记位置的经纬度值。必填参数
//        title 点标记的标题
//        snippet 点标记的内容
//        draggable 点标记是否可拖拽
//        visible 点标记是否可见
//        anchor 点标记的锚点
//        alpha 点的透明度
        LatLng latLng = new LatLng(lat, lng);
        aMap.addMarker(new MarkerOptions().position(latLng).title(titleStr).snippet(contentStr).draggable(isDraggable));
    }

    /**
     * 自定义 Marker
     *
     * @param lat        在地图上标记位置的纬度值
     * @param lng        在地图上标记位置的经度值
     * @param titleStr   点标记的标题
     * @param contentStr 点标记的内容
     * @param iconRes    图标
     */
    public void defineMarker(double lat, double lng, String titleStr, String contentStr, int iconRes) {
        MarkerOptions markerOption = new MarkerOptions();
        LatLng latLng = new LatLng(lat, lng);
        markerOption.position(latLng);
        markerOption.title(titleStr).snippet(contentStr);

        markerOption.draggable(true);//设置Marker可拖动
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(context.getResources(), iconRes)));
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(true);//设置marker平贴地图效果
        aMap.addMarker(markerOption);
    }

    /**
     * 绘制动画效果 Marker
     *
     * @param marker
     */
    public void setAnimDefineMarker(Marker marker) {
        Animation animation = new RotateAnimation(marker.getRotateAngle(), marker.getRotateAngle() + 180, 0, 0, 0);
        long duration = 1000L;
        animation.setDuration(duration);
        animation.setInterpolator(new LinearInterpolator());

        marker.setAnimation(animation);
        marker.startAnimation();
    }

    /**
     * Marker 点击事件
     */
    public void setMarkerClickListener(final Handler handler) {
        // 定义 Marker 点击事件监听
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            // marker 对象被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                Message message = new Message();
                message.obj = marker;
                message.what = 222;
                handler.sendMessage(message);
                return false;
            }
        });
    }

    /**
     * Marker 拖拽事件
     */
    public void setMarkerDragListener(final Handler handler) {
        aMap.setOnMarkerDragListener(new AMap.OnMarkerDragListener() {

            // 当marker开始被拖动时回调此方法, 这个marker的位置可以通过getPosition()方法返回。
            // 这个位置可能与拖动的之前的marker位置不一样。
            // marker 被拖动的marker对象。
            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(111);
            }

            // 在marker拖动完成后回调此方法, 这个marker的位置可以通过getPosition()方法返回。
            // 这个位置可能与拖动的之前的marker位置不一样。
            // marker 被拖动的marker对象。
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(222);

            }

            // 在marker拖动过程中回调此方法, 这个marker的位置可以通过getPosition()方法返回。
            // 这个位置可能与拖动的之前的marker位置不一样。
            // marker 被拖动的marker对象。
            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(333);

            }
        });
    }

    /**
     * 绘制线
     *
     * @param latLngs
     * @param lineWidth
     * @return
     */
    public void setPolyLine(List<LatLng> latLngs, int lineWidth) {
        /* 绘制线常用方法 */
//        setCustomTexture(BitmapDescriptor customTexture) 设置线段的纹理，建议纹理资源长宽均为2的n次方
//        setCustomTextureIndex(java.util.List<java.lang.Integer> custemTextureIndexs)  设置分段纹理index数组
//        setCustomTextureList(java.util.List customTextureList)  设置分段纹理list
//        setDottedLine(boolean isDottedLine)  设置是否画虚线，默认为false，画实线。
//        setUseTexture(boolean useTexture)  是否使用纹理贴图
//        useGradient(boolean useGradient)  设置是否使用渐变色
//        visible(boolean isVisible)  设置线段的可见性
//        width(float width)  设置线段的宽度，单位像素
//        zIndex(float zIndex) 设置线段Z轴的值
        PolylineOptions polylineOptions = new PolylineOptions().addAll(latLngs).width(lineWidth).color(Color.parseColor("#aa0000")).setDottedLine(false);
        aMap.addPolyline(polylineOptions);
    }
}
