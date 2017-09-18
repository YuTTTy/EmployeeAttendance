package com.xahaolan.emanage.utils.mine;

import android.content.Context;

import com.xahaolan.emanage.base.MyApplication;

import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/16.
 */

public class AppUtils {
    private static final String TAG = MyUtils.class.getSimpleName();
    private Context context;

    public static int getPersonId() {
        int personId = 0;
        Map<String, Object> loginData = MyApplication.getLoginData();
        if (loginData != null && loginData.get("personid") != null) {
            personId = new Double((Double) loginData.get("personid")).intValue();
        }
        return personId;
    }

    public static String getPersonName() {
        String personName = "";
        Map<String, Object> loginData = MyApplication.getLoginData();
        if (loginData != null && loginData.get("personname") != null) {
            personName = (String) loginData.get("personname");
        }
        return personName;
    }

    public static String getUserId() {
        String userId = "";
        Map<String, Object> loginData = MyApplication.getLoginData();
        if (loginData != null && loginData.get("userid") != null) {
            userId = (String) loginData.get("userid");
        }
        return userId;
    }

    public static String getUserName() {
        String userName = "";
        Map<String, Object> loginData = MyApplication.getLoginData();
        if (loginData != null && loginData.get("username") != null) {
            userName = (String) loginData.get("username");
        }
        return userName;
    }

    public static String getDepartmentId() {
        String departmentid = "";
        Map<String, Object> loginData = MyApplication.getLoginData();
        if (loginData != null && loginData.get("departmentid") != null) {
            departmentid = (String) loginData.get("departmentid");
        }
        return departmentid;
    }

    public static String getDpmname() {
        String dpmname = "";
        Map<String, Object> loginData = MyApplication.getLoginData();
        if (loginData != null && loginData.get("dpmname") != null) {
            dpmname = (String) loginData.get("dpmname");
        }
        return dpmname;
    }
}
