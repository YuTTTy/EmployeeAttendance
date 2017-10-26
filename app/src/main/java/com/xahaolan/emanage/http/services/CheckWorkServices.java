package com.xahaolan.emanage.http.services;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
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
 * Created by helinjie on 2017/9/15.     考勤
 */

public class CheckWorkServices extends BaseService {
    private static final String TAG = CheckWorkServices.class.getSimpleName();
    private Context context;
    private RequestQueue requesQueue;

    public CheckWorkServices(Context context) {
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
     *                    签到签退接口
     *
     * @param personId   员工id
     * @param personName  员工姓名
     * @param createdate  上传日期（年月日）
     * @param createTime  上传时间（时分秒）
     * @param longitude   经度
     * @param latitude    纬度
     * @param label       位置文字信息
     * @param signflag    0:签到，1:签退
     * @param handler
     */
    public void addClockAddService(int personId,String personName,String createdate,String createTime,
                                   String longitude,String latitude,String label,int signflag,final Handler handler) {
        LogUtils.e(TAG, "==============================   签到签退接口 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/clock!add.action";
        Map<String, Object> params = new HashMap<>();
        params.put("personId", personId);
        params.put("personName", personName);
//        params.put("createdate", createdate);
//        params.put("createTime", createTime);
        params.put("longitude", longitude);
        params.put("latitude", latitude);
        params.put("label", label);
        params.put("signflag", signflag);
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
                                Log.e(TAG, "签到签退接口 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                List<Map<String,Object>> responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "签到签退接口 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "签到签退接口 field");
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
     *                    签到签退查询接口
     *
     * @param personId   员工id
     * @param handler
     */
    public void addClockQueryService(int personId,int page,int rows,final Handler handler) {
        LogUtils.e(TAG, "==============================   签到签退查询接口 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/clock!query.action";
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("rows", rows);
        params.put("personId", personId);
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
                                Log.e(TAG, "签到签退查询接口 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                Map<String,Object> responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "签到签退查询接口 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "签到签退查询接口 field");
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
     *                  出差登记表添加
     *
     * @param personId    员工id
     * @param personName  员工姓名
     * @param origin      始发地
     * @param destination  目的地
     * @param startDate    开始时间
     * @param endDate   结束时间
     * @param vehicle   交通工具
     * @param reason    出差事由
     * @param sourceFile    出差事由
     * @param handler   二进制文件列表
     */
    public void bussinessTripAddService(int personId,String personName,String origin,String destination,
                                     String startDate,String endDate,String vehicle,String reason,
                                      String[] sourceFile, final Handler handler) {
        LogUtils.e(TAG, "==============================   出差登记表添加 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/businessTrip!add.action";
        Map<String, Object> params = new HashMap<>();
        params.put("personId", personId);
        params.put("personName", personName);
        params.put("origin", origin);
        params.put("destination", destination);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("vehicle", vehicle);
        params.put("reason", reason);
        params.put("sourceFile", sourceFile);
        //        getVerificationParams(params, 1);//获取验证参数
        Map<String,String> mHeaders = getFormHeader();
        GsonRequest<RepBase<List<Map<String,Object>>>> request = null;
        try {
            request = new GsonRequest<>(context, Request.Method.POST, urlStr,mHeaders, params, new TypeToken<RepBase<List<Map<String,Object>>>>() {
            },
                    new Response.Listener<RepBase<List<Map<String,Object>>>>() {
                        @Override
                        public void onResponse(RepBase<List<Map<String,Object>>> response) {
                            if (response == null || response.getSuccess() == null) {
                                Log.e(TAG, "出差登记表添加 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                List<Map<String,Object>> responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "出差登记表添加 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "出差登记表添加 field");
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
     *                  出差登记表查询
     *
     * @param personId    员工id
     * @param handler
     */
    public void bussinessTripFindAllService(int personId,int page,int rows,final Handler handler) {
        LogUtils.e(TAG, "==============================   出差登记表查询 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/businessTrip!findAll.action";
        Map<String, Object> params = new HashMap<>();
        params.put("personId", personId);
        params.put("page", page);
        params.put("rows", rows);
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
                                Log.e(TAG, "出差登记表查询 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                Map<String,Object> responseData = (Map<String, Object>) response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "出差登记表查询 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "出差登记表查询 field");
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
     *                  出差登记表详情查询
     *
     * @param id    员工id
     * @param handler
     */
    public void bussinessDetailService(int id,final Handler handler) {
        LogUtils.e(TAG, "==============================   出差登记表详情查询 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/businessTrip!findbyID.action";
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
                                Log.e(TAG, "出差登记表详情查询 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                Map<String,Object> responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "出差登记表详情查询 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "出差登记表详情查询 field");
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
     *                  出差登记表审核
     *
     * @param id    员工id
     * @param auditflag    审核标志（1:审核通过，2:审核驳回）
     * @param handler
     */
    public void bussinessAgreeService(int id,int auditflag,final Handler handler) {
        LogUtils.e(TAG, "==============================   出差登记表审核 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/businessTrip!verify.action";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("auditflag", auditflag);
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
                                Log.e(TAG, "出差登记表审核 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                Map<String,Object> responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "出差登记表审核 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "出差登记表审核 field");
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
     *                    外出登记查询
     *
     * @param personid   发布人id
     * @param handler
     */
    public void outGoingQueryService(int personid,int page,int rows,final Handler handler) {
        LogUtils.e(TAG, "==============================   外出登记查询 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/outgoingAPPAction!findAll.action";
        Map<String, Object> params = new HashMap<>();
        params.put("personid", personid);
        params.put("page", page);
        params.put("rows", rows);
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
                                Log.e(TAG, "外出登记查询 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                Map<String,Object> responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "外出登记查询 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "外出登记查询 field");
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
     *                    外出登记详情查询
     *
     * @param id   发布人id
     * @param handler
     */
    public void outGoingDetailQueryService(int id,final Handler handler) {
        LogUtils.e(TAG, "==============================   外出登记详情查询 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/outgoingAPPAction!findbyID.action";
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
                                Log.e(TAG, "外出登记详情查询 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                Map<String,Object> responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "外出登记详情查询 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "外出登记详情查询 field");
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
     *                    外出登记审核
     *
     * @param id   发布人id
     * @param auditflag   审核标志（1:审核通过，2:审核驳回）
     * @param handler
     */
    public void outGoingAgreeService(int id,int auditflag,final Handler handler) {
        LogUtils.e(TAG, "==============================   外出登记审核 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/outgoingAPPAction!verify.action";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("auditflag", auditflag);
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
                                Log.e(TAG, "外出登记审核 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                Map<String,Object> responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "外出登记审核 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "外出登记审核 field");
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
     *                    外出登记添加
     *
     * @param personid   员工id
     * @param departmentId   部门id
     * @param date    外出时间（2017-09-11）
     * @param starttime  开始时间（09:12:12）
     * @param endtime  结束时间（14:23:34）
     * @param reason   外出原因
     * @param sourceFile
     * @param handler
     */
    public void outGoingAddService(int personid,int departmentId,String date,String starttime,String endtime,
                                   String reason,String[] sourceFile,final Handler handler) {
        LogUtils.e(TAG, "==============================   外出登记添加 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/outgoingAPPAction!add.action";
        Map<String, Object> params = new HashMap<>();
        params.put("personid", personid);
        params.put("departmentId", departmentId);
        params.put("date", date);
        params.put("starttime", starttime);
        params.put("endtime", endtime);
        params.put("reason", reason);
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
                                Log.e(TAG, "外出登记添加 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                String responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "外出登记添加 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "外出登记添加 field");
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
     *                    外出登记修改
     *
     * @param id
     * @param personid   员工id
     * @param date   外出时间（2017-09-11）
     * @param starttime   开始时间（09:12:12）
     * @param endtime   结束时间（14:23:34）
     * @param reason   外出原因
     * @param handler
     */
    public void outGoingEditService(int id,int personid,String date,String starttime,String endtime,String reason,final Handler handler) {
        LogUtils.e(TAG, "==============================   外出登记修改 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/outgoingAPPAction!edit.action";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("personid", personid);
        params.put("date", date);
        params.put("starttime", starttime);
        params.put("endtime", endtime);
        params.put("reason", reason);
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
                                Log.e(TAG, "外出登记修改 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                String responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "外出登记修改 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "外出登记修改 field");
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
     *                    请假单查询
     *
     * @param personId   员工id
     * @param handler
     */
    public void leaveOrderQueryService(int personId,int page,int rows,final Handler handler) {
        LogUtils.e(TAG, "==============================   请假单查询 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/leaveOrderAPPAction!findALL.action";
        Map<String, Object> params = new HashMap<>();
        params.put("personId", personId);
        params.put("page", page);
        params.put("rows", rows);
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
                                Log.e(TAG, "请假单查询 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                Map<String,Object> data = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = data;
                                Log.e(TAG, "请假单查询 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "请假单查询 field");
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
     *                    请假单详情查询
     *
     * @param id   要查询的记录
     * @param handler
     */
    public void leaveOrderDetailQueryService(int id,final Handler handler) {
        LogUtils.e(TAG, "==============================   请假单详情查询 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/leaveOrderAPPAction!findbyID.action";
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
                                Log.e(TAG, "请假单详情查询 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                Map<String,Object> responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "请假单详情查询 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "请假单详情查询 field");
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
     *                    请假查询
     *
     * @param id   要查询的记录
     * @param auditflag   审核标志（1:审核通过，2:审核驳回）
     * @param handler
     */
    public void leaveOrderAgreeService(int id,int auditflag,final Handler handler) {
        LogUtils.e(TAG, "==============================   请假查询 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/leaveOrderAPPAction!verify.action";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("auditflag", auditflag);
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
                                Log.e(TAG, "请假查询 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                Map<String,Object> responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "请假查询 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "请假查询 field");
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
     *                    请假单创建
     *
     * @param personId  请假员工id
     * @param personName   请假员工姓名
     * @param startDate   开始日期（2017-09-11）
     * @param endDate   结束日期（2017-09-12）
     * @param reason   请假原因
     * @param sourceFile
     * @param handler
     */
    public void leaveOrderAddService(int personId,String personName,String startDate,
                                 String endDate,String reason,String[] sourceFile,final Handler handler) {
        LogUtils.e(TAG, "==============================   请假单创建 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/leaveOrderAPPAction!add.action";
        Map<String, Object> params = new HashMap<>();
        params.put("personId", personId);
        params.put("personName", personName);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("reason", reason);
        params.put("sourceFile", new Gson().toJson(sourceFile));
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
                                Log.e(TAG, "请假单创建 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                String responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "请假单创建 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "请假单创建 field");
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
     *                    请假单修改
     *
     * @param id  修改的记录
     * @param personId  请假员工id
     * @param personName   请假员工姓名
     * @param startDate   开始日期（2017-09-11）
     * @param endDate   结束日期（2017-09-12）
     * @param reason   请假原因
     * @param handler
     */
    public void leaveOrderEditService(int id,int personId,String personName,String startDate,
                                     String endDate,String reason,final Handler handler) {
        LogUtils.e(TAG, "==============================   请假单修改 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/leaveOrderAPPAction!edit.action";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("personId", personId);
        params.put("personName", personName);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("reason", reason);
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
                                Log.e(TAG, "请假单修改 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                String responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "请假单修改 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "请假单修改 field");
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
     *                    加班信息查询
     *
     * @param personid   加班员工id
     * @param handler
     */
    public void workQueryService(int personid,int page,int rows,final Handler handler) {
        LogUtils.e(TAG, "==============================   加班信息查询 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/workAPPAction!findALL.action";
        Map<String, Object> params = new HashMap<>();
        params.put("personid", personid);
        params.put("page", page);
        params.put("rows", rows);
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
                                Log.e(TAG, "加班信息查询 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                Map<String,Object> responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "加班信息查询 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "加班信息查询 field");
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
     *                    加班信息详情查询
     *
     * @param id   要查询的记录
     * @param handler
     */
    public void workDetailQueryService(int id,final Handler handler) {
        LogUtils.e(TAG, "==============================   加班信息详情查询 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/workAPPAction!findbyID.action";
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
                                Log.e(TAG, "加班信息详情查询 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                Map<String,Object> responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "加班信息详情查询 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "加班信息详情查询 field");
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
     *                    加班审核
     *
     * @param id   要查询的记录
     * @param auditflag   审核标志（1:审核通过，2:审核驳回）
     * @param handler
     */
    public void workAgreeService(int id,int auditflag,final Handler handler) {
        LogUtils.e(TAG, "==============================   加班审核 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/workAPPAction!verify.action";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("auditflag", auditflag);
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
                                Log.e(TAG, "加班审核 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                Map<String,Object> responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "加班审核 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "加班审核 field");
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
     *                    加班信息新增
     *
     * @param personId  加班员工id
     * @param startDate   开始日期（2017-09-11）
     * @param endDate   结束日期（2017-09-12）
     * @param reason   加班原因
     * @param sourceFile
     * @param handler
     */
    public void workAddService(int personId,String startDate,String endDate,String reason,
                               String[] sourceFile,final Handler handler) {
        LogUtils.e(TAG, "==============================   加班信息新增 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/workAPPAction!add.action";
        Map<String, Object> params = new HashMap<>();
        params.put("personId", personId);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("reason", reason);
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
                                Log.e(TAG, "加班信息新增 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                String responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "加班信息新增 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "加班信息新增 field");
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
     *                    加班信息修改
     *
     * @param id  修改的记录
     * @param personId  加班员工id
     * @param startDate   开始日期（2017-09-11）
     * @param endDate   结束日期（2017-09-12）
     * @param reason   加班原因
     * @param handler
     */
    public void workEditService(int id,int personId,String startDate,
                               String endDate,String reason,final Handler handler) {
        LogUtils.e(TAG, "==============================   加班信息修改 request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/workAPPAction!edit.action";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("personId", personId);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("reason", reason);
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
                                Log.e(TAG, "加班信息修改 null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                String responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "加班信息修改 success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "加班信息修改 field");
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
     *     down load  images
     *
     * @param attrId  附件记录id
     * @param handler
     */
    public void loadImagesService(int attrId,final Handler handler) {
        LogUtils.e(TAG, "==============================   down load  images request   =======================================");
        String urlStr = MyConstant.BASE_URL + "/app/interface!download.action";
        Map<String, Object> params = new HashMap<>();
        params.put("attrId", attrId);
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
                                Log.e(TAG, "down load  images null" + response);
                                return;
                            }
                            Message message = new Message();
                            if (response.getSuccess()) {
                                String responseData = response.getObj();
                                message.what = MyConstant.REQUEST_SUCCESS;
                                message.obj = responseData;
                                Log.e(TAG, "down load  images success");
                            } else {
                                message.what = MyConstant.REQUEST_FIELD;
                                message.obj = response.getMsg();
                                Log.e(TAG, "down load  images field");
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
