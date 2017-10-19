package com.xahaolan.emanage.utils.mine;

import android.content.Context;

import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.utils.common.SPUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/16.
 */

public class AppUtils {
    private static final String TAG = MyUtils.class.getSimpleName();
    private Context context;

    public static int getPersonId(Context context) {
        int personId = 0;
        Map<String, Object> loginData = (Map<String, Object>) SPUtils.get(context, MyConstant.SHARED_SAVE,MyConstant.SP_LOGIN_DATA,new HashMap<String,Object>());
        if (loginData != null && loginData.get("personid") != null) {
            personId = new Double((Double) loginData.get("personid")).intValue();
        }
        return personId;
    }

    public static String getPersonName(Context context) {
        String personName = "";
        Map<String, Object> loginData = (Map<String, Object>) SPUtils.get(context, MyConstant.SHARED_SAVE,MyConstant.SP_LOGIN_DATA,new HashMap<String,Object>());
        if (loginData != null && loginData.get("personname") != null) {
            personName = (String) loginData.get("personname");
        }
        return personName;
    }

    public static String getUserId(Context context) {
        String userId = "";
        Map<String, Object> loginData = (Map<String, Object>) SPUtils.get(context, MyConstant.SHARED_SAVE,MyConstant.SP_LOGIN_DATA,new HashMap<String,Object>());
        if (loginData != null && loginData.get("userid") != null) {
            userId = (String) loginData.get("userid");
        }
        return userId;
    }

    public static String getUserName(Context context) {
        String userName = "";
        Map<String, Object> loginData = (Map<String, Object>) SPUtils.get(context, MyConstant.SHARED_SAVE,MyConstant.SP_LOGIN_DATA,new HashMap<String,Object>());
        if (loginData != null && loginData.get("username") != null) {
            userName = (String) loginData.get("username");
        }
        return userName;
    }

    public static int getDepartmentId(Context context) {
        int departmentid = 0;
        Map<String, Object> loginData = (Map<String, Object>) SPUtils.get(context, MyConstant.SHARED_SAVE,MyConstant.SP_LOGIN_DATA,new HashMap<String,Object>());
        if (loginData != null && loginData.get("departmentid") != null) {
            departmentid = new Double((Double)loginData.get("departmentid")).intValue();
        }
        return departmentid;
    }

    public static String getDpmname(Context context) {
        String dpmname = "";
        Map<String, Object> loginData = (Map<String, Object>) SPUtils.get(context, MyConstant.SHARED_SAVE,MyConstant.SP_LOGIN_DATA,new HashMap<String,Object>());
        if (loginData != null && loginData.get("dpmname") != null) {
            dpmname = (String) loginData.get("dpmname");
        }
        return dpmname;
    }
}
