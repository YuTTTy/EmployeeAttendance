package com.xahaolan.emanage.manager.map;

import android.content.Context;
import android.util.Log;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.Trace;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.utils.common.LogUtils;
import com.xahaolan.emanage.utils.common.SPUtils;

/**
 * Created by admin on 2016/6/12.
 */
public class BaiduTraceTools {
    private static String TAG = BaiduTraceTools.class.getSimpleName();
    public static LBSTraceClient client;
    public static Trace trace;

    /**
     * 打开轨迹追踪服务
     */
    public static void openTrace(Context context) {
        //实例化轨迹服务客户端
        client = new LBSTraceClient(context);
        //鹰眼服务ID
        long serviceId = 0;
        Log.e(TAG, "鹰眼服务ID：" );

        //entity标识
        String entityName = "";
        Log.e(TAG, "轨迹追踪entity标识：" + entityName);

        //轨迹服务类型（0 : 不上传位置数据，也不接收报警信息； 1 : 不上传位置数据，但接收报警信息；2 : 上传位置数据，且接收报警信息）
        int traceType = 2;
        //实例化轨迹服务
        trace = new Trace(context, serviceId, entityName, traceType);
        //实例化开启轨迹服务回调接口
        final OnStartTraceListener startTraceListener = new OnStartTraceListener() {
            //开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
            @Override
            public void onTraceCallback(int arg0, String arg1) {
                LogUtils.e(TAG, "轨迹服务开启回调----消息编码:" + arg0 + ",消息内容:" + arg1);
            }

            //轨迹服务推送接口（用于接收服务端推送消息，arg0 : 消息类型，arg1 : 消息内容，详情查看类参考）
            @Override
            public void onTracePushCallback(byte arg0, String arg1) {
                LogUtils.e(TAG, "轨迹服务推送接口----用于接收服务端推送消息:" + arg0 + ",消息内容:" + arg1);
            }
        };

        //位置采集周期
        int gatherInterval = 10;
        //打包周期
        int packInterval = 60;
        //设置位置采集和打包周期
        client.setInterval(gatherInterval, packInterval);

        // 设置协议类型，0为http，1为https
        int protocoType = 0;
        client.setProtocolType(protocoType);

        //开启轨迹服务
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.startTrace(trace, startTraceListener);
            }
        }).start();
        Log.e(TAG, "轨迹追踪服务开启");
    }

    /**
     * 关闭轨迹追踪
     */
    public static void closeTrace() {
        //实例化停止轨迹服务回调接口
        OnStopTraceListener stopTraceListener = new OnStopTraceListener() {
            // 轨迹服务停止成功
            @Override
            public void onStopTraceSuccess() {
                Log.e(TAG, "轨迹服务停止成功");
            }

            // 轨迹服务停止失败（arg0 : 错误编码，arg1 : 消息内容，详情查看类参考）
            @Override
            public void onStopTraceFailed(int arg0, String arg1) {
                Log.e(TAG, "轨迹服务停止失败 ===  错误编码:" + arg0 + ",消息内容:" + arg1);
            }
        };

        //停止轨迹服务
        if (client != null) {
            client.stopTrace(trace, stopTraceListener);
            Log.e(TAG, "轨迹追踪服务停止");
        }
    }
}
