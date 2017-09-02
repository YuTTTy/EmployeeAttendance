package com.xahaolan.emanage.base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;

import com.xahaolan.emanage.http.volley.RequestQueue;
import com.xahaolan.emanage.http.volley.toolbox.HttpStack;
import com.xahaolan.emanage.http.volley.toolbox.OwnHttpClientStack;
import com.xahaolan.emanage.http.volley.toolbox.Volley;

/**
 * Created by aiodiy on 2017/3/10.          初始化
 */

public class InitializeData {
    private static final String TAG = InitializeData.class.getSimpleName();
    private Context context;
    private static RequestQueue requesQueue;

    public InitializeData(Context context) {
        this.context = context;
        //        queue = Volley.newRequestQueue(context);
        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        HttpStack httpStack = new OwnHttpClientStack(AndroidHttpClient.newInstance(userAgent));
        requesQueue = Volley.newRequestQueue(context, httpStack);

    }
}
