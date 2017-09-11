package com.xahaolan.emanage.manager.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * 检查权限的工具类
 * <p/>
 * Created by wangchenlong on 16/1/26.
 */
public class PermissionsChecker {
    private final Context mContext;

    public PermissionsChecker(Context context) {
        mContext = context.getApplicationContext();
    }

    // 判断权限集合
    public boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    // 判断是否缺少权限
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) ==
                PackageManager.PERMISSION_DENIED;
    }

    // 含有全部的权限
    public boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    //    /**
//     *          rxpermissions2 动态获取权限，在6.0系统中的权限判断
//     */
//    public static void getMorePermission(Activity activity, String... permissions) {
//        new RxPermissions(activity).requestEach(permissions)
//                .subscribe(new Consumer<Permission>() {
//                    @Override
//                    public void accept(Permission permission) throws Exception {
//                        if (permission.granted) {
//                            // 用户已经同意该权限
////                            handler.sendEmptyMessage(MyConstant.HANDLER_PERMISSION_AGREE);
//                            LogUtils.e(TAG, permission.name + " is granted.");
//                        } else if (permission.shouldShowRequestPermissionRationale) {
//                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
////                            handler.sendEmptyMessage(MyConstant.HANDLER_PERMISSION_REFUSE_AGAIN_ASK);
//                            LogUtils.e(TAG, permission.name + " is denied. More info should be provided.");
//                        } else {
//                            // 用户拒绝了该权限，并且选中『不再询问』
////                            handler.sendEmptyMessage(MyConstant.HANDLER_PERMISSION_REFUSE_NOT_ASK);
//                            LogUtils.e(TAG, permission.name + " is denied.");
//                        }
//                    }
//                });
//    }

    /**
     * judge permission and apply
     *
     * @param context
     * @param permissionStr
     */
    public static Boolean judgePermission(Context context, String permissionStr) {
        /* 判断是否拥有权限 */
        if (context.getPackageManager().checkPermission(permissionStr, context.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
//                if (ContextCompat.checkSelfPermission(MediaRecordTest.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            ActivityCompat.requestPermissions((Activity) context, new String[]{permissionStr}, 111);
            return false;
        } else {
            return true;
        }
    }
}
