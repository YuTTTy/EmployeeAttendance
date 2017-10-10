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
 * Created by helinjie on 2017/9/17.   任务
 */

public class TaskService extends BaseService {
    private static final String TAG = TaskService.class.getSimpleName();
    private Context context;
    private RequestQueue requesQueue;

    public TaskService(Context context) {
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
     *                    任务添加
     *
     * @param createId   发布人id
     * @param createName  发布人姓名
     * @param executorId  执行人id
     * @param content     任务内容
     * @param endDate     截止日期
     * @param handler
     */
    public void addTaskAddService(int createId,String createName,int executorId,
                                  String content,String endDate,final Handler handler) {
        LogUtils.e(TAG, "==============================   任务添加 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/task!add.action";
        Map<String, Object> params = new HashMap<>();
        params.put("createId", createId);
        params.put("createName", createName);
        params.put("executorId", executorId);
        params.put("content", content);
        params.put("endDate", endDate);
//        params.put("sourceFile", sourceFile);
        //        getVerificationParams(params,／ 1);//获取验证参数
        Map<String,String> mHeaders = getFormHeader();
        GsonRequest<RepBase<List<Map<String,Object>>>> request = null;
        try {
            request = new GsonRequest<>(context, Request.Method.POST, urlStr,mHeaders, params, new TypeToken<RepBase<List<Map<String,Object>>>>() {
            },
                    new Response.Listener<RepBase<List<Map<String,Object>>>>() {
                        @Override
                        public void onResponse(RepBase<List<Map<String,Object>>> response) {
                            if (response == null || response.getSuccess() == null) {
                                Log.e(TAG, "任务添加 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                List<Map<String,Object>> responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "任务添加 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "任务添加 field");
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
     *                    任务列表查询
     *
     * @param createId   发布人id
     * @param executorId  执行人id
     * @param handler
     */
    public void addTaskQueryService(int createId,int executorId,int page,int rows,final Handler handler) {
        LogUtils.e(TAG, "==============================   任务列表查询 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/task!query.action";
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("rows", rows);
        if (createId != 0){
            params.put("createId", createId);
        }
        if (executorId != 0){
            params.put("executorId", executorId);
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
                                Log.e(TAG, "任务列表查询 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                Map<String,Object> responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "任务列表查询 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "任务列表查询 field");
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
     *                    任务详情
     *
     * @param id
     * @param handler
     */
    public void taskDetailService(int id,final Handler handler) {
        LogUtils.e(TAG, "==============================   任务详情 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/task!findbyID.action";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
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
                                Log.e(TAG, "任务详情 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                Map<String,Object> responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "任务详情 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "任务详情 field");
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
     *                    完成任务
     *
     * @param id
     * @param handler
     */
    public void finishTaskService(int id,final Handler handler) {
        LogUtils.e(TAG, "==============================   完成任务 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/task!done.action";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
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
                                Log.e(TAG, "完成任务 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                String responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "完成任务 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "完成任务 field");
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
