package com.xahaolan.emanage.http;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.bean.RepBase;
import com.xahaolan.emanage.utils.common.LogUtils;
import com.xahaolan.emanage.utils.common.SPUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/10/10.  模拟表单上传文件，java通过模拟post方式提交表单实现图片上传功能
 */

public class FormRequest {
    private static final String TAG = FormRequest.class.getSimpleName();
    private Handler handler;
//    /**
//     * 测试上传png图片
//     */
//    public static void testUploadImage() {
//        String url = "http://xxx/wnwapi/index.php/Api/Index/testUploadModelBaking";
//        String fileName = "e:/chenjichao/textures/antimap_0017.png";
//        Map<String, String> textMap = new HashMap<String, String>();
//        //可以设置多个input的name，value
//        textMap.put("name", "testname");
//        textMap.put("type", "2");
//        //设置file的name，路径
//        Map<String, String> fileMap = new HashMap<String, String>();
//        fileMap.put("upfile", fileName);
//        String contentType = "";//image/png
//        String ret = formUpload(url, textMap, fileMap, contentType);
//        System.out.println(ret);
//        //{"status":"0","message":"add succeed","baking_url":"group1\/M00\/00\/A8\/CgACJ1Zo-LuAN207AAQA3nlGY5k151.png"}
//    }

    public FormRequest(final Context context, final String urlStr, final Map<String, Object> textMap,
                       final Map<String, Object> fileMap,Handler handler){
        this.handler = handler;
        new Thread(new Runnable() {
            @Override
            public void run() {
                formUpload( context, urlStr,textMap, fileMap,"");
            }
        }).start();
    }
    /**
     * 上传图片
     *
     * @param urlStr
     * @param textMap
     * @param fileMap
     * @param contentType 没有传入文件类型默认采用application/octet-stream
     *                    contentType非空采用filename匹配默认的图片类型
     * @return 返回response数据
     */
    @SuppressWarnings("rawtypes")
    public void formUpload(Context context,String urlStr, Map<String, Object> textMap,
                                    Map<String, Object> fileMap, String contentType) {
        Message message = new Message();
        String response = "";
        HttpURLConnection conn = null;
        // boundary就是request头和上传文件内容的分隔符
        String BOUNDARY = "---------------------------123821742118716";
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
//            conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("User-Agent", "Android");
//            conn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            String sessionId = (String) SPUtils.get(context, MyConstant.SHARED_SAVE, MyConstant.SESSION_ID, new String());
            if (sessionId != null && !sessionId.equals("")) {
                conn.setRequestProperty("cookie", sessionId);
            }
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // text
            if (textMap != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator iter = textMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    Object inputValue = entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    strBuf.append("\r\n").append("--").append(BOUNDARY)
                            .append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\""
                            + inputName + "\"\r\n\r\n");
                    strBuf.append(inputValue);
                }
                out.write(strBuf.toString().getBytes());
                LogUtils.e(TAG,"非文件 Params : "+strBuf.toString());
            }
            // file
            if (fileMap != null) {
                Iterator iter = fileMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    File file = new File(inputValue);
                    String filename = file.getName();

//                    //没有传入文件类型，同时根据文件获取不到类型，默认采用application/octet-stream
//                    contentType = new MimetypesFileTypeMap().getContentType(file);
//                    //contentType非空采用filename匹配默认的图片类型
//                    if (!"".equals(contentType)) {
//                        if (filename.endsWith(".png")) {
                            contentType = "image/png";
//                      ／  } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".jpe")) {
//                            contentType = "image/jpeg";
//                        } else if (filename.endsWith(".gif")) {
//                            contentType = "image/gif";
//                        } else if (filename.endsWith(".ico")) {
//                            contentType = "image/image/x-icon";
//                        }
//                    }
//                    if (contentType == null || "".equals(contentType)) {
//                        contentType = "application/octet-stream";
//                    }
                    StringBuffer strBuf = new StringBuffer();
                    strBuf.append("\r\n").append("--").append(BOUNDARY)
                            .append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\""
                            + inputName + "\"; filename=\"" + filename
                            + "\"\r\n");
                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
                    out.write(strBuf.toString().getBytes());
//                    LogUtils.e(TAG,"图片资源文件 Params : "+strBuf.toString());
                    DataInputStream in = new DataInputStream(
                            new FileInputStream(file));
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    in.close();
                }
            }
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();
            LogUtils.e(TAG, "请求头header ：" + getRequestHeaders(conn));
            // 读取返回数据
            if (conn.getResponseCode() == 200){
                StringBuffer strBuf = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    strBuf.append(line).append("\n");
                }
                response = strBuf.toString();
                message.what = MyConstant.REQUEST_SUCCESS;
                message.obj = response;
                handler.sendMessage(message);
                reader.close();
                reader = null;
            }else {
                RepBase baseRep = new Gson().fromJson(response,new TypeToken<RepBase>(){}.getType());
                message.what = MyConstant.REQUEST_FIELD;
                message.obj = baseRep.getMsg();
                handler.sendMessage(message);
            }
            LogUtils.e(TAG, "请求返回数据 ：" + HttpUtils.decode(response));
        } catch (Exception e) {
            message.what = MyConstant.REQUEST_ERROR;
            message.obj = e.getMessage();
            handler.sendMessage(message);
            LogUtils.e(TAG, "发送POST请求出错 ：" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
    }
    public String getRequestHeaders(HttpURLConnection httpUrlCon){
        StringBuffer buffer = new StringBuffer();
        buffer.append("{"+httpUrlCon.getRequestMethod() + " / " + " HTTP/1.1");
        buffer.append("，Host: " + httpUrlCon.getRequestProperty("Host"));
        buffer.append("，Connection: " + httpUrlCon.getRequestProperty("Connection"));
        buffer.append("，User-Agent: " + httpUrlCon.getRequestProperty("User-Agent"));
        buffer.append("，Cookie: " + httpUrlCon.getRequestProperty("Cookie"));
        buffer.append("，Content-Type: " + httpUrlCon.getRequestProperty("Content-Type"));
        buffer.append("，Accept: " + httpUrlCon.getRequestProperty("Accept"));
        buffer.append("，Accept-Encoding: " + httpUrlCon.getRequestProperty("Accept-Encoding"));
        buffer.append("，Accept-Language: " + httpUrlCon.getRequestProperty("Accept-Language"));
        buffer.append("，Connection: " + httpUrlCon.getHeaderField("Connection")+"}");//利用另一种读取HTTP头字段
        return buffer.toString();
    }
//    public String getHeaders(HttpURLConnection conn){
//        StringBuffer buffer = new StringBuffer();
//        Map<String, List<String>> header = conn.getHeaderFields();
//        for (Map.Entry<String, List<String>> entry : header.entrySet()) {
//            String key = entry.getKey();
//            for (String value : entry.getValue()) {
//                buffer.append(key + ":" + value);
////                System.out.println(key + ":" + value);
//            }
//        }
//        return buffer.toString();
//    }
}
