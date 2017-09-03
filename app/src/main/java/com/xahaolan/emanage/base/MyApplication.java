package com.xahaolan.emanage.base;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2016/6/17.
 */
public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();
    private Context applicationContext;

    public static MyApplication myApplication;

    private static Object obj = new Object();
    private static MyApplication instance = null;

    public static MyApplication getInstance() {
        // if already inited, no need to get lock everytime
//        if (instance == null) {
//            synchronized (obj) {
        if (instance == null) {
            instance = new MyApplication();
        }
//            }
//        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        applicationContext = this;
    }

    /*是否首次进入主页*/
    private static Boolean firstMain = true;

    public void clearMemory(){

    }

    public static Boolean getFirstMain() {
        return firstMain;
    }

    public static void setFirstMain(Boolean firstMain) {
        MyApplication.firstMain = firstMain;
    }
}
