package com.xahaolan.emanage.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

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
import com.xahaolan.emanage.fragment.ContactsFragment;
import com.xahaolan.emanage.fragment.LeaseFragment;
import com.xahaolan.emanage.fragment.EngineeFragment;
import com.xahaolan.emanage.fragment.NoticeFragment;
import com.xahaolan.emanage.http.services.TrailServices;
import com.xahaolan.emanage.manager.polling.PollingService;
import com.xahaolan.emanage.manager.polling.PollingUtil;
import com.xahaolan.emanage.utils.common.LogUtils;
import com.xahaolan.emanage.utils.common.ToastUtils;
import com.xahaolan.emanage.utils.mine.AppUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;

import java.util.List;

public class MainActivity extends BaseActivity  implements GeocodeSearch.OnGeocodeSearchListener {
    private static String TAG = MainActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    public static MainActivity instance = null;

    private int[] imageArray = {R.drawable.selector_tab_contacts, R.drawable.selector_tab_notice,
            R.drawable.selector_tab_lease, R.drawable.selector_tab_mine};
    private String[] textArray = {"通讯录", "公告", "租凭", "工程"};
    private Class fragmentArray[] = {ContactsFragment.class, NoticeFragment.class, LeaseFragment.class, EngineeFragment.class};
    private FragmentTabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_main);
        instance = this;
    }

    @Override
    public void setTitleAttribute() {

    }

    @Override
    public void initView() {
        initTabView(); //加载底部tab
        bindTabListener();//tab点击切换
    }

    @Override
    public void initData() {
        tabHost.setCurrentTab(3);
        MyUtils.closeAllActivity(context);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyUtils.hideKeyboard((Activity) context);
        if (MyApplication.getFirstMain()) {
            getLocation();
        }
        MyApplication.setFirstMain(false);
        /* 开启上传位置轮询服务 */
        PollingUtil.startPollingService(context,30*60, PollingService.class,PollingService.ACTION);
    }

    /**
     * 加载底部tab
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void initTabView() {
        tabHost = (FragmentTabHost) findViewById(R.id.main_tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.main_body);
        for (int i = 0; i < fragmentArray.length; i++) {
            /*给每个Tab按钮设置图标、文字和内容*/
            TabHost.TabSpec tabSpec = tabHost.newTabSpec(textArray[i]).setIndicator(getTabItemView(i));
            /*将Tab按钮添加进Tab选项卡中*/
            tabHost.addTab(tabSpec, fragmentArray[i], null);
            /*设置Tab按钮的背景*/
            tabHost.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.color.alpha);
        }
        tabHost.getTabWidget().setDividerDrawable(null);//去除分割线

    }

    /**
     * 给每个Tab按钮设置图标和文字
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public View getTabItemView(int index) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_main_tab, null);
        ImageView tab_iv = (ImageView) itemView.findViewById(R.id.item_main_tab_image);
        tab_iv.setImageResource(imageArray[index]);
        TextView tab_text = (TextView) itemView.findViewById(R.id.item_main_tab_text);
        tab_text.setText(textArray[index]);
        tab_text.setTextColor(Color.parseColor(MyConstant.COLOR_GRAY_TEXT));
        return itemView;
    }

    /**
     * 点击监听，改变字体颜色
     */
    public void bindTabListener() {
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onTabChanged(String tabId) {
                int childCount = tabHost.getTabWidget().getChildCount();//子tab个数
                LogUtils.e(TAG, "tab个数：" + childCount);
                for (int i = 0; i < childCount; i++) {
                    View itemView = tabHost.getTabWidget().getChildTabViewAt(i);
                    TextView textView = (TextView) itemView.findViewById(R.id.item_main_tab_text);
                    /*当前选中tab*/
                    if (textView != null && textView.getText().toString().equals(tabId)) {
                        textView.setTextColor(Color.parseColor(MyConstant.COLOR_GREEN_TEXT));
                    /*未选中tab*/
                    } else {
                        textView.setTextColor(Color.parseColor(MyConstant.COLOR_GRAY_TEXT));
                    }
                }
                LogUtils.e(TAG, "当前页：" + tabId);
            }
        });
    }

    long exitTime = 0L; //退出时间
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                ToastUtils.showShort(MainActivity.this, "再按一次返回退出程序");
                exitTime = System.currentTimeMillis();
            } else {
//                android.os.Process.killProcess(Process.myPid());
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
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
//            getAddress(location);
            getAddress(new LatLonPoint(Double.parseDouble(latitude),Double.parseDouble(longitude)));
        }else {
            LogUtils.e(TAG, "首次获取经纬度值为空，开启位置监听");
            //监视地理位置变化
            locationManager.requestLocationUpdates(locationProvider, 3000, 0, locationListener);
        }
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
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
                LogUtils.e(TAG,"当前定位地址 ：" + label);
                requestLoadLoc();
            } else {
                LogUtils.e(TAG,"没有找到相关地址");
            }
        } else {
            LogUtils.e(TAG,"逆地理编码error : " + rCode);
        }
    }

    /**
     * 上传位置
     */
    public void requestLoadLoc() {
        personId = AppUtils.getPersonId(getApplicationContext());
        LogUtils.e(TAG, "上传位置请求 ：" + personId);
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
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            //移除监听器
            locationManager.removeUpdates(locationListener);
        }
        LogUtils.e(TAG, "onDestroy");
    }
}
