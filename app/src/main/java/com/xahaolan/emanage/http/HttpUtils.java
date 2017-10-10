package com.xahaolan.emanage.http;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.xahaolan.emanage.utils.common.LogUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by aiodiy on 2016/12/20.
 */
public class HttpUtils {
    private static String TAG = HttpUtils.class.getSimpleName();
    private Context context;

    public HttpUtils(Context context) {
        this.context = context;
    }

    /**
     * 拼接url、参数
     *
     * @param urlStr
     * @param params
     * @return
     */
    public static String appendParams(String urlStr, Map<String, Object> params) {
        StringBuffer buffer = null;
        if (urlStr != null && !urlStr.equals("")) {
            buffer = new StringBuffer(urlStr);
            buffer.append("?");
        } else {
            buffer = new StringBuffer();
        }
        if (params == null|| params.size() <= 0){
            return "";
        }
        for (Map.Entry<String, Object> entry :
                params.entrySet()) {
            buffer.append(entry.getKey());
            buffer.append("=");
            buffer.append(entry.getValue());
            buffer.append("&");
        }
        buffer.deleteCharAt(buffer.length() - 1);

        return buffer.toString();
    }

    /**
     * 通过设备唯一识别码deviceId、序列号、android_id生成UUID
     *
     * @return
     */
    public String getDeviceUUID() {
        String uuid = null;
//        String[] camerPermission = new String[]{
//                Manifest.permission.ACCESS_WIFI_STATE,
//                Manifest.permission.READ_PHONE_STATE,};
//        PermissionsChecker mChecker = new PermissionsChecker(context);
//        /* 未获取权限*/
//        if (mChecker.lacksPermissions(camerPermission)) {
//            ActivityCompat.requestPermissions((Activity) context, camerPermission, MyConstant.INTENT_PERMISSION_CAMER);// 请求权限兼容低版本
//        /* 已获取权限 */
//        } else {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //设备唯一识别码
            String tmDevice = "" + manager.getDeviceId();
//        LogUtils.e(TAG, "设备唯一识别码 = " + tmDevice);
            //序列号
            String tmSerial = "" + manager.getSimSerialNumber();
//        LogUtils.e(TAG, "序列号 = " + tmSerial);
            //获取android id号
            String androidId = android.provider.Settings.System.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
//        LogUtils.e(TAG, "android_id = " + androidId);
        /*获取UUID*/
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            uuid = deviceUuid.toString().replace("-", "");
//        LogUtils.e(TAG, "通过设备唯一识别码deviceId、序列号、android_id生成UUID = " + uuid);

//        /*获取随机的UUID*/
//        UUID radomUUid = UUID.randomUUID();
//        LogUtils.e("radomUUid = ", radomUUid.toString().replace("-", ""));
//        }
        return uuid;
    }

    /**
     * 生成的UUID拼接时间
     *
     * @param uuid
     * @param apiToken
     * @return
     */
    public static String appendTime(String timeStr, String uuid, String apiToken, String activityGuid) {
        StringBuilder builder = new StringBuilder();
        builder.append(timeStr.substring(17,timeStr.length()));
        builder.append(timeStr.substring(14,16));
        builder.append(timeStr.substring(11,13));
        builder.append("&");
        builder.append(uuid);
        builder.append("&");
        if (!apiToken.equals("")) {
            builder.append(apiToken);
            builder.append("&");
        }
        if (!activityGuid.equals("")) {
            builder.append(activityGuid);
            builder.append("&");
        }
        builder.append(timeStr.substring(8,10));
        builder.append(timeStr.substring(5,7));
        builder.append(timeStr.substring(0,4));
//        LogUtils.e(TAG, "拼接时间 ：" + builder.toString());
        return builder.toString();
    }

    /**
     * 使用hash算法进行sha256计算
     *
     * @param password
     * @return
     */
    public static String getSHA256Encrypt(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        digest.reset();
        byte[] data = digest.digest(password.getBytes());
        String uuidHash = String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data)).toLowerCase();
//        LogUtils.e(TAG, "sha256加密 ：" + uuidHash);
        return uuidHash;
    }

    /**
     * hash算法
     *
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String sha1(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(data.getBytes());
        StringBuffer buf = new StringBuffer();
        byte[] bits = md.digest();
        for (int i = 0; i < bits.length; i++) {
            int a = bits[i];
            if (a < 0) a += 256;
            if (a < 16) buf.append("0");
            buf.append(Integer.toHexString(a));
        }
        return buf.toString();
    }
    /**
     *                   获取具体年月日时分秒
     */
    public static void getTimeDetail() {
        Calendar calendar = Calendar.getInstance();
//        long currentTime = System.currentTimeMillis();
//        calendar.setTimeInMillis(currentTime);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String year = suppleSingular(calendar.get(Calendar.YEAR));
        String month = suppleSingular(calendar.get(Calendar.MONTH) + 1);
        String day = suppleSingular(calendar.get(Calendar.DAY_OF_MONTH));
        String hour = suppleSingular(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = suppleSingular(calendar.get(Calendar.MINUTE));
        String second = suppleSingular(calendar.get(Calendar.SECOND));
        LogUtils.e(TAG, "Calendar获得的时间 ：" + year + "年 " + month + "月 " + day + "日 " + hour + "时 " + minute + "分 " + second + "秒");
    }

    /**
     * 自动补全单数
     *
     * @return
     */
    public static String suppleSingular(int number) {
        String suppleStr = "";
        if (number <= 9) {
            suppleStr = "0" + number;
        } else {
            suppleStr = number + "";
        }
        return suppleStr;
    }
    /**
     * MD5加密
     *
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String md5(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data.getBytes());
        StringBuffer buf = new StringBuffer();
        byte[] bits = md.digest();
        for (int i = 0; i < bits.length; i++) {
            int a = bits[i];
            if (a < 0) a += 256;
            if (a < 16) buf.append("0");
            buf.append(Integer.toHexString(a));
        }
        return buf.toString();
    }

    /**
     * url参数中文Unicode编码
     */
    public static String urlEncode(String str, String encode) {
        String encoderStr = null;
        try {
            encoderStr = URLEncoder.encode(str, encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "0";
        }
        return encoderStr;
    }

    /**
     * url参数中文Unicode编码(GBK)
     *
     * @param str
     * @return
     */
    public static String urlEncodeGBK(String str) {
        String encodeStr = null;
        try {
            encodeStr = URLEncoder.encode(str, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "0";
        }
        return encodeStr;
    }

    /**
     * url参数中文Unicode编码(UTF-8)
     *
     * @param str
     * @return
     */
    public static String urlEncodeUTF(String str) {
        String encodeStr = null;
        try {
            encodeStr = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "0";
        }
        return encodeStr;
    }
    //中文转Unicode
    public static String gbEncoding(final String gbString) {   //gbString = "测试"
        char[] utfBytes = gbString.toCharArray();   //utfBytes = [测, 试]
        String unicodeBytes = "";
        for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
            String hexB = Integer.toHexString(utfBytes[byteIndex]);   //转换为16进制整型字符串
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        System.out.println("unicodeBytes is: " + unicodeBytes);
        return unicodeBytes;
    }

    //Unicode转中文
    public static String decodeUnicode(final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }

    public static String decode(String in) {
        try {
            return decode(in.toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return in;
    }

    private static String decode(char[] in) throws Exception {
        int off = 0;
        char c;
        char[] out = new char[in.length];
        int outLen = 0;
        while (off < in.length) {
            c = in[off++];
            if (c == '\\') {
                if (in.length > off) { // 是否有下一个字符
                    c = in[off++]; // 取出下一个字符
                } else {
                    out[outLen++] = '\\'; // 末字符为'\'，返回
                    break;
                }
                if (c == 'u') { // 如果是"\\u"
                    int value = 0;
                    if (in.length > off + 4) { // 判断"\\u"后边是否有四个字符
                        boolean isUnicode = true;
                        for (int i = 0; i < 4; i++) { // 遍历四个字符
                            c = in[off++];
                            switch (c) {
                                case '0':
                                case '1':
                                case '2':
                                case '3':
                                case '4':
                                case '5':
                                case '6':
                                case '7':
                                case '8':
                                case '9':
                                    value = (value << 4) + c - '0';
                                    break;
                                case 'a':
                                case 'b':
                                case 'c':
                                case 'd':
                                case 'e':
                                case 'f':
                                    value = (value << 4) + 10 + c - 'a';
                                    break;
                                case 'A':
                                case 'B':
                                case 'C':
                                case 'D':
                                case 'E':
                                case 'F':
                                    value = (value << 4) + 10 + c - 'A';
                                    break;
                                default:
                                    isUnicode = false; // 判断是否为unicode码
                            }
                        }
                        if (isUnicode) { // 是unicode码转换为字符
                            out[outLen++] = (char) value;
                        } else { // 不是unicode码把"\\uXXXX"填入返回值
                            off = off - 4;
                            out[outLen++] = '\\';
                            out[outLen++] = 'u';
                            out[outLen++] = in[off++];
                        }
                    } else { // 不够四个字符则把"\\u"放入返回结果并继续
                        out[outLen++] = '\\';
                        out[outLen++] = 'u';
                        continue;
                    }
                } else {
                    switch (c) { // 判断"\\"后边是否接特殊字符，回车，tab一类的
                        case 't':
                            c = '\t';
                            out[outLen++] = c;
                            break;
                        case 'r':
                            c = '\r';
                            out[outLen++] = c;
                            break;
                        case 'n':
                            c = '\n';
                            out[outLen++] = c;
                            break;
                        case 'f':
                            c = '\f';
                            out[outLen++] = c;
                            break;
                        default:
                            out[outLen++] = '\\';
                            out[outLen++] = c;
                            break;
                    }
                }
            } else {
                out[outLen++] = (char) c;
            }
        }
        return new String(out, 0, outLen);
    }
}
