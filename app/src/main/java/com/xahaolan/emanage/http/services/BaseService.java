package com.xahaolan.emanage.http.services;

import android.content.Context;
import android.os.Message;

import com.google.gson.Gson;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.HttpUtils;
import com.xahaolan.emanage.utils.common.DateUtil;
import com.xahaolan.emanage.utils.common.SPUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aiodiy on 2016/12/20.
 */
public class BaseService {
    private static final String TAG = BaseService.class.getSimpleName();
    private static Context context;
    private Gson gson = new Gson();

    public BaseService(Context context) {
        this.context = context;
    }

    public BaseService(Context context, Gson gson) {
        this.context = context;
        this.gson = gson;
    }

    public Message installMessage(Message message, int msgWhat, Object msgObj) {
        message.what = msgWhat;
        message.obj = msgObj;
        return message;
    }

    /**
     * 获取请求头
     *
     * @return
     */
    public Map<String, String> getHeader() {
        /*请求头*/
        Map<String, String> mHeader = new HashMap<>();
        mHeader.put("Content-Type", "application/json");
        String sessionId = (String) SPUtils.get(context, MyConstant.SHARED_SAVE, MyConstant.SESSION_ID, new String());
        if (sessionId != null && !sessionId.equals("")) {
            mHeader.put("cookie", sessionId);
        }
        return mHeader;
    }

//    /**
//     * 获取验证参数
//     *
//     * @param params
//     * @param type   0.登陆注册不需要apitoken,使用登陆前signature生成方法  1.需要apitoken，使用登陆后signature生成方法
//     * @return
//     */
//    public static Map<String, Object> getVerificationParams(Map<String, Object> params, int type) {
//        String timeStr = DateUtil.getCurrentDateStr("yyyy-MM-dd HH:mm:ss");
//        String deviceUuid = new HttpUtils(context).getDeviceUUID();
//        String apiToken = "";
//        if (type == 1) {
//            Map<String, Object> loginData = (Map<String, Object>) SPUtils.get(context, MyConstant.SHARED_SAVE, MyConstant.SP_LOGIN_DATA, new HashMap<String, Object>());
//            if (loginData != null) {
//                if (loginData.get("access_token") != null) {
//                    apiToken = (String) loginData.get("access_token");
//                    params.put("apiToken", apiToken);
//                }
//            }
//        }
//        params.put("signature", getSigniture(timeStr, deviceUuid, apiToken));
//        params.put("deviceUUID", deviceUuid);
//        params.put("queryDate", timeStr);
//        return params;
//    }
//
//    /**
//     * 登录前后signiture生成
//     *
//     * @param apiToken 登陆前后
//     * @return
//     */
//    public static String getSigniture(String timeStr, String deviceUuid, String apiToken) {
//        String timeUUid = HttpUtils.appendTime(timeStr, deviceUuid, apiToken, "");
//        String deviceUUid = HttpUtils.getSHA256Encrypt(timeUUid);
////        LogUtils.e(TAG,"signiture生成 ==== time : " + timeStr);
////        LogUtils.e(TAG,"signiture生成 ==== deviceUuid : " + deviceUuid);
////        LogUtils.e(TAG,"signiture生成 ==== 拼接时间 : " + timeUUid);
////        LogUtils.e(TAG,"signiture生成 ==== getSHA256Encrypt : " + deviceUUid);
//        return deviceUUid;
//    }

}
