package com.xahaolan.emanage.manager.polling;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.xahaolan.emanage.utils.common.DateUtil;
import com.xahaolan.emanage.utils.common.LogUtils;

/**
 * Created by helinjie on 2017/9/28.
 */

public class PollingUtil {
    //开启轮询服务
    public static void startPollingService(Context context, int seconds, Class<?> cls, String action) {
        //获取AlarmManager系统服务
        AlarmManager manager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        //包装需要执行Service的Intent
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //触发服务的起始时间
//        long triggerAtTime = System.currentTimeMillis();
        long triggerAtTime = SystemClock.elapsedRealtime();
//        LogUtils.e("触发服务的起始时间 ：",triggerAtTime +", " + DateUtil.getStringByFormat(triggerAtTime,"yyyy-MM-dd HH:mm:ss"));

        //使用AlarmManger的setRepeating方法设置定期执行的时间间隔（seconds秒）和需要执行的Service
        manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime,
                seconds * 1000, pendingIntent);
    }
    //停止轮询服务
    public static void stopPollingService(Context context, Class<?> cls,String action) {
        AlarmManager manager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //取消正在执行的服务
        manager.cancel(pendingIntent);
    }
}
