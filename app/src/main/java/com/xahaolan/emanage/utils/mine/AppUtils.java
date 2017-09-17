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

    public static int getPersonId(){
        int personId = 0;
        List<Map<String,Object>> dataList = (List<Map<String, Object>>) MyApplication.getLoginData();
        if (dataList != null && dataList.size() > 0){
            Map<String,Object> loginData = dataList.get(0);
            if (loginData.get("personId") != null){
                personId = new Double((Double)loginData.get("personId")).intValue();
            }
        }
        return personId;
    }
}
