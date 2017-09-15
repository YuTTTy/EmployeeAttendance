package com.xahaolan.emanage.http;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.volley.AuthFailureError;
import com.xahaolan.emanage.http.volley.DefaultRetryPolicy;
import com.xahaolan.emanage.http.volley.NetworkResponse;
import com.xahaolan.emanage.http.volley.ParseError;
import com.xahaolan.emanage.http.volley.Request;
import com.xahaolan.emanage.http.volley.Response;
import com.xahaolan.emanage.http.volley.RetryPolicy;
import com.xahaolan.emanage.http.volley.VolleyLog;
import com.xahaolan.emanage.http.volley.toolbox.HttpHeaderParser;
import com.xahaolan.emanage.utils.common.LogUtils;
import com.xahaolan.emanage.utils.common.SPUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/14.    自定义request请求
 */
public class GsonRequest<T> extends Request<T> {
    private static String TAG = GsonRequest.class.getSimpleName();
    private Context context;
    private Gson mGson = new Gson();
    private TypeToken<T> typeToken;
    private String spitUrl;//拼接路径

    /*请求头*/
    private Map<String, String> mHeader = new HashMap<>();

    /*body请求参数组装*/
    private Map<String, Object> params;
    /**
     * Charset for request.
     */
    private static final String PROTOCOL_CHARSET = "UTF-8";
    /**
     * Content type for request.
     */
    private static final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    private final Response.Listener<T> listener;

    public GsonRequest(Context context, int method, String url, Map<String, Object> params, TypeToken<T> typeToken, Response.Listener<T> listener, Response.ErrorListener errorListener) throws JSONException {
        super(method, url, errorListener);
        this.context = context;
        this.spitUrl = url;
        this.params = params;
        this.typeToken = typeToken;
        this.listener = listener;

        parseParams();
    }

    public GsonRequest(Context context, int method, String url, Map<String, String> headers, Map<String, Object> params, TypeToken<T> typeToken, Response.Listener<T> listener, Response.ErrorListener errorListener) throws JSONException {
        super(method, url, errorListener);
        this.context = context;
        this.spitUrl = url;
        this.mHeader = headers;
        this.params = params;
        this.typeToken = typeToken;
        this.listener = listener;

        parseParams();
    }

    /**
     * get请求参数拼接
     */
    public void parseParams() throws JSONException {
        if (super.getMethod() == Method.GET && params != null) {
            /*拼接url*/
            spitUrl = HttpUtils.appendParams(super.getUrl(), params);
            Log.e(TAG, "RequestMethod:" + "GET");
        } else if (super.getMethod() == Method.POST) {

            Log.e(TAG, "RequestMethod:" + "POST");
        } else if (super.getMethod() == Method.PUT) {
            Log.e(TAG, "RequestMethod:" + "PUT");
        } else if (super.getMethod() == Method.DELETE) {
            Log.e(TAG, "RequestMethod:" + "DELETE");
        }
        Log.e(TAG, "request url---------------:" + spitUrl);
    }

    @Override
    public String getUrl() {
        return spitUrl == null ? super.getUrl() : spitUrl;
    }

    /**
     * 修改请求头
     *
     * @return
     * @throws AuthFailureError
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
//        LogUtils.e(TAG, "请求头Header:" + mHeader);
        return mHeader == null ? super.getHeaders() : mHeader;
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    /**
     * 请求参数组装
     *
     * @return
     * @throws AuthFailureError
     */
    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            String bodyParams = HttpUtils.appendParams("",params);
            LogUtils.e(TAG, "params :" + bodyParams);
            return bodyParams == null ? super.getBody() : bodyParams.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException e) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    params, PROTOCOL_CHARSET);
        }
        return null;
    }

    /**
     * 将解析成合适类型的内容传递给它们的监听回调
     *
     * @param response
     */
    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    /**
     * 超时设置
     *
     * @return
     */
    @Override
    public RetryPolicy getRetryPolicy() {
        /*第一个参数，当前超时时间*/
//        RetryPolicy retryPolicy = new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        Log.e(TAG, "请求超时时间:" + DefaultRetryPolicy.DEFAULT_TIMEOUT_MS);
        RetryPolicy retryPolicy = new DefaultRetryPolicy(50 * 1000, 1, 1.0f);
        return retryPolicy;
    }

    /**
     * 解析返回结果
     *
     * @param networkResponse
     * @return
     */
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse networkResponse) {
        /* save session id */
        Map<String, String> responseHeaders = networkResponse.headers;
        String rawCookies = responseHeaders.get("Set-Cookie");
        //Constant是一个自建的类，存储常用的全局变量
        SPUtils.put(context, MyConstant.SHARED_SAVE, MyConstant.SESSION_ID, rawCookies.substring(0, rawCookies.indexOf(";")));

        /* parse response */
        try {
            String jsonString = new String(networkResponse.data, "UTF-8");
//            String jsonString = new String(networkResponse.data, HttpUtils.parseCharset(networkResponse.headers));
            LogUtils.e(TAG, "请求成功返回数据:" + jsonString);
            T parsedGson = mGson.fromJson(jsonString, typeToken.getType());
            return Response.success(parsedGson, HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException je) {
            return Response.error(new ParseError(je));
        }
    }

}
