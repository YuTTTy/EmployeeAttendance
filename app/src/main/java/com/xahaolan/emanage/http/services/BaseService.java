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
import java.util.UUID;

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
//        mHeader.put("Content-Type", "application/json");
        mHeader.put("Content-Type", "application/x-www-form-urlencoded");
        mHeader.put("User-Agent", "Android");
        String sessionId = (String) SPUtils.get(context, MyConstant.SHARED_SAVE, MyConstant.SESSION_ID, new String());
        if (sessionId != null && !sessionId.equals("")) {
            mHeader.put("cookie", sessionId);
        }
        return mHeader;
    }
    /**
     *   创建表单请求头
     *
     * @return
     */
    public Map<String, String> getFormHeader() {
        /*请求头*/
        Map<String, String> mHeader = new HashMap<>();
        mHeader.put("Content-Type", "multipart/form-data; boundary=---------------------------123821742118716");
        mHeader.put("User-Agent", "Android");
        String sessionId = (String) SPUtils.get(context, MyConstant.SHARED_SAVE, MyConstant.SESSION_ID, new String());
        if (sessionId != null && !sessionId.equals("")) {
            mHeader.put("cookie", sessionId);
        }
        return mHeader;
    }

}
