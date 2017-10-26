package com.xahaolan.emanage.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyApplication;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.fragment.ContactsFragment;
import com.xahaolan.emanage.fragment.EngineeFragment;
import com.xahaolan.emanage.fragment.LeaseFragment;
import com.xahaolan.emanage.fragment.NoticeFragment;
import com.xahaolan.emanage.http.services.TrailServices;
import com.xahaolan.emanage.utils.common.DateUtil;
import com.xahaolan.emanage.utils.common.LogUtils;
import com.xahaolan.emanage.utils.common.ToastUtils;
import com.xahaolan.emanage.utils.mine.AppUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {
    private static String TAG = MainActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    public static MainActivity instance = null;

    private int[] imageArray = {R.drawable.selector_tab_contacts, R.drawable.selector_tab_notice,
            R.drawable.selector_tab_lease, R.drawable.selector_tab_mine};
    private String[] textArray = {"通讯录", "公告", "租凭", "工程"};
    private Class fragmentArray[] = {ContactsFragment.class, NoticeFragment.class, LeaseFragment.class, EngineeFragment.class};
    private FragmentTabHost tabHost;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

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
//        /* 开启上传位置轮询服务 */
//        PollingUtil.startPollingService(context, 1 * 60, PollingService.class, PollingService.ACTION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyUtils.hideKeyboard((Activity) context);
        MyUtils.closeAllActivity(context);
        LogUtils.e(TAG, "是否首次登录 ：" + MyApplication.getFirstMain());
        if (MyApplication.getFirstMain()) {
            getLocation();
        }
        MyApplication.setFirstMain(false);
//        /* 开启上传位置轮询服务 */
//        PollingUtil.startPollingService(context, 5 * 60, PollingService.class, PollingService.ACTION);
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
//                System.exit(0);
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

    public void getLocation() {
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
        /* 开始定位 */
        startLocation();
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void getDefaultOption() {
        locationOption = new AMapLocationClientOption();
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        locationOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        locationOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        locationOption.setInterval(10*60*1000);//可选，设置定位间隔。默认为2秒
        locationOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        locationOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        locationOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        locationOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        locationOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        locationOption.setLocationCacheEnable(false); //可选，设置是否使用缓存定位，默认为true
        acquireWakeLock();
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                if (location.getErrorCode() == 0) {
                    longitude = location.getLongitude() + "";
                    latitude = location.getLatitude() + "";
                    label = location.getAddress();
                    LogUtils.e(TAG, "经纬度 ：" + longitude + ", " + latitude + " ; 地址：" + label);
//                    ToastUtils.showShort(context, "经纬度 ：" + longitude + ", " + latitude + " ; 地址：" + label);
                    requestLoadLoc();
                } else {
                    LogUtils.e(TAG, "定位失败 : "+ location.getErrorInfo());
//                    ToastUtils.showShort(context,"定位失败 : "+ location.getErrorInfo());
                }
            } else {
                LogUtils.e(TAG, "定位失败，loc is null");
//                ToastUtils.showShort(context, "定位失败，loc is null");
            }
        }
    };
    private PowerManager.WakeLock wakeLock;

    /**
     * 申请电源锁，禁止休眠
     */
    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass()
                    .getCanonicalName());
            if (null != wakeLock) {
                wakeLock.acquire();
//                wakeLock.setReferenceCounted(false);
                LogUtils.e(TAG,"获取电源锁 -------------------");
            }
        }
    }
    // 释放设备电源锁
    private void releaseWakeLock() {
        if (null != wakeLock) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    /**
     * 上传位置
     */
    public void requestLoadLoc() {
        personId = AppUtils.getPersonId(getApplicationContext());
        LogUtils.e(TAG, "===================== 上传定位数据时间 ：" + DateUtil.getStringByFormat(System.currentTimeMillis(), MyConstant.DATE_FORMAT_YMDHMS));
        new TrailServices(getApplicationContext()).uploadLocService(personId, longitude, latitude,
                label, autoflag, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (msg.what == MyConstant.REQUEST_SUCCESS) {
                            LogUtils.e(TAG, "上传位置成功");
//                            ToastUtils.showShort(context, "上传位置成功");
                        } else if (msg.what == MyConstant.REQUEST_FIELD) {
                            stopLocation();
                            String errMsg = (String) msg.obj;
                            if (errMsg.equals("session过期")) {
                                BaseActivity.loginOut(context);
                            }
//                            ToastUtils.showShort(context,errMsg);
                        } else if (msg.what == MyConstant.REQUEST_ERROR) {
                            String errMsg = (String) msg.obj;
                            LogUtils.e(TAG, "上传位置错误 ：" + errMsg);
                        }
                    }
                });
    }

    /**
     * start location
     */
    public void startLocation() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                locationClient.startLocation();
            }
        }, 3000);
    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        if (null != locationClient) {
            // 停止定位
            locationClient.stopLocation();
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
//            ToastUtils.showShort(context, "停止定位");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        stopLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        stopLocation();
        LogUtils.e(TAG, "onDestroy");
    }
}
