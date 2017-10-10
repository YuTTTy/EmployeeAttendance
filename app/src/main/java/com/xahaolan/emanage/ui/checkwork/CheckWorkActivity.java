package com.xahaolan.emanage.ui.checkwork;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyApplication;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.services.CheckWorkServices;
import com.xahaolan.emanage.manager.MapManage;
import com.xahaolan.emanage.ui.MainActivity;
import com.xahaolan.emanage.ui.checkwork.apply.DocumentActivity;
import com.xahaolan.emanage.ui.checkwork.check.CheckApplyActivity;
import com.xahaolan.emanage.ui.checkwork.clockrecord.ClockRecordActivity;
import com.xahaolan.emanage.utils.common.DateUtil;
import com.xahaolan.emanage.utils.common.LogUtils;
import com.xahaolan.emanage.utils.common.ToastUtils;
import com.xahaolan.emanage.utils.mine.AppUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/3.    考勤
 */

public class CheckWorkActivity extends BaseActivity implements LocationSource, AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener {
    private static final String TAG = CheckWorkActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;

    private LinearLayout item_layout;
    private TextView in_text;
    private TextView out_text;
    private RelativeLayout map_layout;
    private TextView address_text;
    private TextView submit_text;
    private TextView cancle_text;

    private Integer[] imageArr = {R.drawable.apply_leave, R.drawable.apply_out_register, R.drawable.apply_out,
            R.drawable.apply_work_hard, R.drawable.apply_clock_card, R.drawable.check_apply, R.drawable.check_examine};
    private String[] strArr = {"请假申请", "外出登记", "出差申请", "加班登记", "打卡记录", "查看我的申请", "查看我的审批"};

    private MapManage mapManage;
    private MapView map_view;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private GeocodeSearch geocoderSearch;
    private Double locLng; //经度
    private Double locLat; //纬度

    private int personId;   //员工id
    private String personName;  //员工姓名
    private String createdate;  //上传日期（年月日）
    private String createTime;  //上传时间（时分秒）
    private String longtitude;   //经度
    private String latitude;    //纬度
    private String label;       //位置文字信息
    private int signflag = 0;   // 0:签到，1:签退

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_check_work);
        initMap(savedInstanceState);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "", R.color.baseTextMain, "考勤", R.color.baseTextMain, "", R.color.baseTextMain, 0);
    }

    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        item_layout = (LinearLayout) findViewById(R.id.check_work_item_layout);
        in_text = (TextView) findViewById(R.id.check_work_in);
        in_text.setOnClickListener(this);
        out_text = (TextView) findViewById(R.id.check_work_out);
        out_text.setOnClickListener(this);
        map_layout = (RelativeLayout) findViewById(R.id.check_work_map_layout);
        map_view = (MapView) findViewById(R.id.check_work_map);
        address_text = (TextView) findViewById(R.id.check_work_address);
        submit_text = (TextView) findViewById(R.id.check_work_submit);
        submit_text.setOnClickListener(this);
        cancle_text = (TextView) findViewById(R.id.check_work_cancle);
        cancle_text.setOnClickListener(this);

    }

    public void initMap(Bundle savedInstanceState) {
        mapManage = new MapManage(this, map_view);
        mapManage.createMapView(savedInstanceState);
        aMap = mapManage.initAmap();
        mapManage.setUiSettings();
        mapManage.changeZoom(14);

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
            /* 签到 */
            case R.id.check_work_in:
                signflag = 0;
                map_layout.setVisibility(View.VISIBLE);
                initLocation();
                break;
            /* 签退 */
            case R.id.check_work_out:
                signflag = 1;
                map_layout.setVisibility(View.VISIBLE);
                initLocation();
                break;
            /* 提交打卡 */
            case R.id.check_work_submit:
                getSubmitData();
                if (signflag == 0) {
                    requestInOut();
                } else if (signflag == 1) {
                    requestInOut();
                }
                break;
            /* 取消打卡 */
            case R.id.check_work_cancle:
                map_layout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void initData() {
        for (int i = 0; i < imageArr.length; i++) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("image", imageArr[i]);
            data.put("name", strArr[i]);
            addItemView(data, i);
        }
    }

    public void addItemView(Map<String, Object> data, int position) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_view_check_work, null);
        ImageView image_view = (ImageView) itemView.findViewById(R.id.item_view_check_work_image);
        TextView name_text = (TextView) itemView.findViewById(R.id.item_view_check_work_text);
        if (data.get("image") != null) {
            int imageRes = (int) data.get("image");
            image_view.setImageResource(imageRes);
        }
        if (data.get("name") != null) {
            name_text.setText(data.get("name") + "");
        }
        item_layout.addView(itemView);

        setItemClick(itemView, position);
    }

    public void setItemClick(View itemView, final int position) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                switch (position) {
                    //请假申请
                    case 0:
                        bundle.putInt("ApplyType", MyConstant.APPLY_DOCUMENT_LEAVE_APPLY);
                        MyUtils.jump(context, DocumentActivity.class, bundle, false, null);
                        break;
                    //外出登记
                    case 1:
                        bundle.putInt("ApplyType", MyConstant.APPLY_DOCUMENT_OUT_REGISTER);
                        MyUtils.jump(context, DocumentActivity.class, bundle, false, null);
                        break;
                    //出差申请
                    case 2:
                        bundle.putInt("ApplyType", MyConstant.APPLY_DOCUMENT_OUT_APPLY);
                        MyUtils.jump(context, DocumentActivity.class, bundle, false, null);
                        break;
                    //加班登记
                    case 3:
                        bundle.putInt("ApplyType", MyConstant.APPLY_DOCUMENT_WORK_REGISTER);
                        MyUtils.jump(context, DocumentActivity.class, bundle, false, null);
                        break;
                    //打卡记录
                    case 4:
                        MyUtils.jump(context, ClockRecordActivity.class, new Bundle(), false, null);
                        break;
                    //查看我的申请
                    case 5:
                        bundle.putInt("CheckType", MyConstant.CHECK_MINE_APPLY);
                        MyUtils.jump(context, CheckApplyActivity.class, bundle, false, null);
                        break;
                    //查看我的审批
                    case 6:
//                        ToastUtils.showShort(context,"研发中，敬请期待....");
                        bundle.putInt("CheckType", MyConstant.CHECK_MINE_EXAMINE);
                        MyUtils.jump(context, CheckApplyActivity.class, bundle, false, null);
                        break;
                }
            }
        });
    }

    public void getSubmitData() {
        personId = AppUtils.getPersonId(context);
        personName = AppUtils.getPersonName(context);
        createdate = DateUtil.getCurrentDateStr(MyConstant.DATE_FORMAT_YMD);
        createTime = DateUtil.getCurrentDateStr(MyConstant.DATE_FORMAT_HMS);
    }

    /**
     * sign  in  or  out
     */
    public void requestInOut() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckWorkServices(context).addClockAddService(personId, personName, createdate, createTime,
                longtitude, latitude, label, signflag, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                            swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                        }
                        if (msg.what == MyConstant.REQUEST_SUCCESS) {
                            map_layout.setVisibility(View.GONE);
                            if (signflag == 0){
                                ToastUtils.showShort(context,"签到成功");
                            }else if (signflag == 1){
                                ToastUtils.showShort(context,"签退成功");
                            }
                        } else if (msg.what == MyConstant.REQUEST_FIELD) {
                            String errMsg = (String) msg.obj;
                            ToastUtils.showShort(context, errMsg);
                            if (errMsg.equals("session过期")){
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
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        map_view.onResume();
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
                longtitude = amapLocation.getLongitude() + "";
                latitude = amapLocation.getLatitude() + "";
                getAddress(new LatLonPoint(Double.parseDouble(latitude),Double.parseDouble(longtitude)));
//                Toast.makeText(this, "经度 ：" + longtitude + ",纬度 ：" + latitude, Toast.LENGTH_LONG).show();
                LogUtils.e(TAG, "经度 ：" + longtitude + ",纬度 ：" + latitude);
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
                label = result.getRegeocodeAddress().getFormatAddress();
                address_text.setText(label);
                LogUtils.e(TAG,"当前定位地址 ：" + label);
            } else {
                ToastUtils.showShort(context, "对不起，没有搜索到相关数据！");
                LogUtils.e(TAG,"当前定位地址 ：" + label);
            }
        } else {
            ToastUtils.showShort(context, rCode+"");
                LogUtils.e(TAG,"逆地理编码error : " + rCode);
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
