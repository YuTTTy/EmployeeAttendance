package com.xahaolan.emanage.http.services;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.EZErrListener;
import com.xahaolan.emanage.http.GsonRequest;
import com.xahaolan.emanage.http.bean.RepBase;
import com.xahaolan.emanage.http.volley.Request;
import com.xahaolan.emanage.http.volley.RequestQueue;
import com.xahaolan.emanage.http.volley.Response;
import com.xahaolan.emanage.http.volley.toolbox.HttpStack;
import com.xahaolan.emanage.http.volley.toolbox.OwnHttpClientStack;
import com.xahaolan.emanage.http.volley.toolbox.Volley;
import com.xahaolan.emanage.utils.common.LogUtils;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/17.   日报
 */

public class DailyServices extends BaseService {
    private static final String TAG = DailyServices.class.getSimpleName();
    private Context context;
    private RequestQueue requesQueue;

    public DailyServices(Context context) {
        super(context);
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
    /**
     *                    日报查询
     *
     * @param employeeid   员工id ，不传代表查询日报
     * @param handler
     */
    public void dailyQueryService(int employeeid,int page,int rows,final Handler handler) {
        LogUtils.e(TAG, "==============================   日报查询 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/dailyreportAPPAction!findAll.action";
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("rows", rows);
        if (employeeid != 0){
            params.put("employeeid", employeeid);
        }
        //        getVerificationParams(params, 1);//获取验证参数
        Map<String,String> mHeaders = getHeader();
        GsonRequest<RepBase<Map<String,Object>>> request = null;
        try {
            request = new GsonRequest<>(context, Request.Method.POST, urlStr,mHeaders, params, new TypeToken<RepBase<Map<String,Object>>>() {
            },
                    new Response.Listener<RepBase<Map<String,Object>>>() {
                        @Override
                        public void onResponse(RepBase<Map<String,Object>> response) {
                            if (response == null || response.getSuccess() == null) {
                                Log.e(TAG, "日报查询 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                Map<String,Object> responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "日报查询 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "日报查询 field");
                            }
                            handler.sendMessage(message);
                        }
                    }, new EZErrListener<>(context, handler));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requesQueue.add(request);
    }
    /**
     *                    日报详情查询
     *
     * @param id   员工id
     * @param handler
     */
    public void dailyDetailQueryService(int id,final Handler handler) {
        LogUtils.e(TAG, "==============================   日报详情查询 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/dailyreportAPPAction!findbyID.action";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        //        getVerificationParams(params, 1);//获取验证参数
        Map<String,String> mHeaders = getHeader();
        GsonRequest<RepBase<List<Map<String,Object>>>> request = null;
        try {
            request = new GsonRequest<>(context, Request.Method.POST, urlStr,mHeaders, params, new TypeToken<RepBase<List<Map<String,Object>>>>() {
            },
                    new Response.Listener<RepBase<List<Map<String,Object>>>>() {
                        @Override
                        public void onResponse(RepBase<List<Map<String,Object>>> response) {
                            if (response == null || response.getSuccess() == null) {
                                Log.e(TAG, "日报详情查询 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                List<Map<String,Object>> responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "日报详情查询 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "日报详情查询 field");
                            }
                            handler.sendMessage(message);
                        }
                    }, new EZErrListener<>(context, handler));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requesQueue.add(request);
    }

    /**
     *                    新增日报
     *
     * @param department  部门id
     * @param employeeid   员工id
     * @param projectid   项目id
     * @param date   填报日期（2017-09-11 12:12:12）
     * @param conclusion   本日工作
     * @param question   存在问题
     * @param plan   明日计划
     * @param weather   天气情况
     * @param state   状态 0，草稿1.已提交
     * @param createuser   创建用户姓名
     * @param sourceFile
     * @param handler
     */
    public void dailyNewService(int department,int employeeid,int projectid,String date,String conclusion,
                                String question,String plan,String weather,int state,String createuser,
                                String[] sourceFile,final Handler handler) {
        LogUtils.e(TAG, "==============================   新增日报 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/dailyreportAPPAction!add.action";
        Map<String, Object> params = new HashMap<>();
        params.put("department", department);
        params.put("employeeid", employeeid);
        params.put("projectid", projectid);
        params.put("date", date);
        params.put("conclusion", conclusion);
        params.put("question", question);
        params.put("plan", plan);
        params.put("weather", weather);
        params.put("state", state);
        params.put("createuser", createuser);
        params.put("sourceFile", sourceFile);
        //        getVerificationParams(params, 1);//获取验证参数
        Map<String,String> mHeaders = getFormHeader();
        GsonRequest<RepBase<String>> request = null;
        try {
            request = new GsonRequest<>(context, Request.Method.POST, urlStr,mHeaders, params, new TypeToken<RepBase<String>>() {
            },
                    new Response.Listener<RepBase<String>>() {
                        @Override
                        public void onResponse(RepBase<String> response) {
                            if (response == null || response.getSuccess() == null) {
                                Log.e(TAG, "新增日报 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                String responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "新增日报 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "新增日报 field");
                            }
                            handler.sendMessage(message);
                        }
                    }, new EZErrListener<>(context, handler));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requesQueue.add(request);
    }
    /**
     *                    修改日报
     *
     * @param id  要修改的记录
     * @param department  部门id
     * @param employeeid   员工id
     * @param projectid   项目id
     * @param date   填报日期（2017-09-11 12:12:12）
     * @param conclusion   本日工作
     * @param question   存在问题
     * @param plan   明日计划
     * @param weather   天气情况
     * @param state   状态 0，草稿1.已提交
     * @param createuser   创建用户姓名
     * @param handler
     */
    public void dailyEditService(int id,int department,int employeeid,int projectid,String date,String conclusion,
                                 String question,String plan,String weather,int state,String createuser,final Handler handler) {
        LogUtils.e(TAG, "==============================   修改日报 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/dailyreportAPPAction!edit.action";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("department", department);
        params.put("employeeid", employeeid);
        params.put("projectid", projectid);
        params.put("date", date);
        params.put("conclusion", conclusion);
        params.put("question", question);
        params.put("plan", plan);
        params.put("weather", weather);
        params.put("state", state);
        params.put("createuser", createuser);
        //        getVerificationParams(params, 1);//获取验证参数
        Map<String,String> mHeaders = getHeader();
        GsonRequest<RepBase<String>> request = null;
        try {
            request = new GsonRequest<>(context, Request.Method.POST, urlStr,mHeaders, params, new TypeToken<RepBase<String>>() {
            },
                    new Response.Listener<RepBase<String>>() {
                        @Override
                        public void onResponse(RepBase<String> response) {
                            if (response == null || response.getSuccess() == null) {
                                Log.e(TAG, "修改日报 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                String responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "修改日报 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "修改日报 field");
                            }
                            handler.sendMessage(message);
                        }
                    }, new EZErrListener<>(context, handler));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requesQueue.add(request);
    }
}
