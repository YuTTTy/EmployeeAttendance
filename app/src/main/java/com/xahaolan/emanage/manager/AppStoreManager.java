package com.xahaolan.emanage.manager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import com.xahaolan.emanage.utils.common.ApplicationUtils;
import com.xahaolan.emanage.utils.common.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aiodiy on 2017/8/16.          跳转到应用市场
 *
 * 主流应用商店对应的包名如下：
 com.qihoo.appstore--------360手机助手
 com.taobao.appcenter-------淘宝手机助手
 com.tencent.Android.qqdownloader-------应用宝
 com.hiapk.marketpho--------安卓市场
 cn.goapk.market--------安智市场
 */

public class AppStoreManager {
    private static final String TAG = AppStoreManager.class.getSimpleName();

    /**
     *     根据应用包名,跳转到应用市场 启动到app详情界面
     *
     * @param marketPkg     应用市场的包名
     */
    public static void jumpToMarket(Context context, String marketPkg) {
        String packageName = ApplicationUtils.getPackageName(context);
        try {
            Uri uri = Uri.parse("market://details?id=" + packageName);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (marketPkg != null) {// 如果没给市场的包名，则系统会弹出市场的列表让你进行选择。
                intent.setPackage(marketPkg);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG,"您没有安装应用市场");
        }
    }

    private static List<String> MarketPackages = new ArrayList<>();
    static {
        MarketPackages.add("com.lenovo.leos.appstore");
        MarketPackages.add("com.android.vending");
        MarketPackages.add("com.xiaomi.market");
        MarketPackages.add("com.qihoo.appstore");
        MarketPackages.add("com.wandoujia.phoenix2");
        MarketPackages.add("com.baidu.appsearch");
        MarketPackages.add("com.tencent.android.qqdownloader");
    }
    /**
     *过滤掉手机上没有安装的应用商店
     */
    public static List<ActivityInfo> queryInstalledMarketInfos(Context context) {
        List<ActivityInfo> infos = new ArrayList<>();
        if (context == null) return infos;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_MARKET);
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        if (resolveInfos == null || infos.size() == 0) {
            return infos;
        }
        for (int i = 0; i < resolveInfos.size(); i++) {
            try {
                infos.add(resolveInfos.get(i).activityInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return infos;
    }
    public static List<ApplicationInfo> filterInstalledPkgs(Context context) {
        List<ApplicationInfo> infos = new ArrayList<>();
        if (context == null || MarketPackages == null || MarketPackages.size() == 0)
            return infos;
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> installedPkgs = pm.getInstalledPackages(0);
        int li = installedPkgs.size();
        int lj = MarketPackages.size();
        for (int j = 0; j < lj; j++) {
            for (int i = 0; i < li; i++) {
                String installPkg = "";
                String checkPkg = MarketPackages.get(j);
                try {
                    installPkg = installedPkgs.get(i).applicationInfo.packageName;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (TextUtils.isEmpty(installPkg))
                    continue;
                if (installPkg.equals(checkPkg)) {
                    infos.add(installedPkgs.get(i).applicationInfo);
                    break;
                }
            }
        }
        return infos;
    }
    /**
     * 获取已安装应用商店的包名列表
     *
     * @param context
     * @return
     */
    public static ArrayList<String> queryInstalledMarketPkgs(Context context) {
        ArrayList<String> pkgs = new ArrayList<String>();
        if (context == null)
            return pkgs;
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.APP_MARKET");
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        if (infos == null || infos.size() == 0)
            return pkgs;
        int size = infos.size();
        for (int i = 0; i < size; i++) {
            String pkgName = "";
            try {
                ActivityInfo activityInfo = infos.get(i).activityInfo;
                pkgName = activityInfo.packageName;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(pkgName))
                pkgs.add(pkgName);
        }
        return pkgs;
    }
    /**
     * 过滤出已经安装的包名集合
     *
     * @param context
     * @param pkgs 待过滤包名集合
     * @return 已安装的包名集合
     */
    public static ArrayList<String> filterInstalledPkgs(Context context, ArrayList<String> pkgs) {
        ArrayList<String> empty = new ArrayList<String>();
        if (context == null || pkgs == null || pkgs.size() == 0)
            return empty;
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> installedPkgs = pm.getInstalledPackages(0);
        int li = installedPkgs.size();
        int lj = pkgs.size();
        for (int j = 0; j < lj; j++) {
            for (int i = 0; i < li; i++) {
                String installPkg = "";
                String checkPkg = pkgs.get(j);
                try {
                    installPkg = installedPkgs.get(i).applicationInfo.packageName;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (TextUtils.isEmpty(installPkg))
                    continue;
                if (installPkg.equals(checkPkg)) {
                    empty.add(installPkg);
                    break;
                }
            }
        }
        return empty;
    }
}
