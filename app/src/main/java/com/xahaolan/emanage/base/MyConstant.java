package com.xahaolan.emanage.base;

/**
 * Created by Administrator on 2016/6/17.
 */
public class MyConstant {
    /* test */
//    public static final String SERVICE_API_DOMAIN = "http://apidev.lebuddy.com.cn";  //API域名（开发环境）
//    public static final String SERVICE_WEB_DOMAIN = "apidev.lebuddy.com.cn";  //WEB域名（开发环境）
    /* 正式环境 */
    public static final String SERVICE_API_DOMAIN = "https://api.lebuddy.com.cn";  //API域名 (正式环境)
    public static final String SERVICE_WEB_DOMAIN = "www.lebuddy.com.cn";  //web域名（正式环境）
    public static final String SERVICE_URL = "/api/v1/";   //url
    public static final String BASE_URL = SERVICE_API_DOMAIN + SERVICE_URL; //拼接API路径
    public static final String BASE_WEB_URL = "http://" + SERVICE_WEB_DOMAIN +"/";  //拼接web路径

    /*请求网络*/
    public static final int REQUEST_SUCCESS = 0x1001;//请求成功
    public static final int REQUEST_FIELD = 0x1002;//请求失败
    public static final int REQUEST_ERROR = 0x1003;//请求错误
    public static final int SUCCESS_CODE = 100000;//success code
    public static final int REQUEST_FAILD_PURCHASED = 31309;//没有可以多余购买的份额

    public static final short CONNECTION_SUCCESS = 101;

    /* handler request data */
    public static final int HANDLER_SUCCESS = 0x1011;//消息成功
    public static final int HANDLER_FIELD = 0x1012;//消息失败
    public static final int HANDLER_PROGRESS = 0x1013;//消息progress
    /* handler dialog btn */
    public static final int HANDLER_DIALOG_LIFT_BTN = 0x1021;//
    public static final int HANDLER_DIALOG_RIGHT_BTN = 0x1022;//
    /* handler im monitor */
    public static final int HANDLER_IM_MONITOR_SUCCESS = 0x1031;//
    public static final int HANDLER_IM_MONITOR_FAILED = 0x1032;//
    /* handler refresh list */
    public static final int HANDLER_REFRESH_SUCCESS = 0x1041;//
    /* handler have permission */
    public static final int HANDLER_PERMISSION_OPEN = 0x1051;// 已获取权限
    public static final int HANDLER_PERMISSION_CLOSE = 0x1052;//未获取权限
    /* handler permission back */
    public static final int HANDLER_PERMISSION_AGREE = 0x1061;// 用户已经同意该权限
    public static final int HANDLER_PERMISSION_REFUSE_AGAIN_ASK = 0x1062;//用户拒绝了该权限,下次仍提示
    public static final int HANDLER_PERMISSION_REFUSE_NOT_ASK = 0x1063;//用户拒绝了该权限,下次不再提示
    /* count down timer */
    public static final int HANDLER_COUNT_DOWN_TIMER_OVER = 0x1071;//

    /*intent回调*/
    public static final int INTENT_REQUEST = 111;
    public static final int INTENT_REQUEST_TWO = 121;
    public static final int INTENT_RESULT = 222;
    /*intent 权限获取回调*/
    public static final int INTENT_PERMISSION_RECORD = 131; //录音权限
    public static final int INTENT_PERMISSION_CAMER = 132;  //相机
    public static final int INTENT_PERMISSION_STORAGE = 133; //读写手机存储

    /*图片上传*/
    public static final int RESULT_CAMER = 0x111;//相机
    public static final int RESULT_PHOTO = 0x112;//相册
    public static final int RESULT_TAILER = 0x113;//图片裁剪
    /* date format */
    public static final String DATE_FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";//
    public static final String DATE_FORMAT_YMDH = "yyyy-MM-dd HH";//
    public static final String DATE_FORMAT_YMD = "yyyy-MM-dd";//
    public static final String DATE_FORMAT_HM = "HH:mm";//
    public static final String DATE_FORMAT_YMD_HM = "YYYY年MM月dd号 HH:mm";//

    /* click index */
    public static final int CLICK_INDEX_FIRST = 0;  //
    public static final int CLICK_INDEX_SECOND = 1;  //
    public static final int CLICK_INDEX_THIRD = 2;  //
    public static final int CLICK_INDEX_FORTH = 3;  //
    public static final int CLICK_INDEX_FIFTH = 4;  //

    /* create activity dialog type */
    public static final int DIALOG_TYPE_MAIN = 1; //首页dialog -- create activity
    public static final int DIALOG_TYPE_CHAT = 2; //chat dialog  -- create activity
    public static final int DIALOG_TYPE_DETAIL = 3; //chat dialog -- check activity detail

    /* 设备类型  */
    public static final int OS_TYPE = 1;//1.android  2.IOS

    /*SharedPerferenced存储*/
    public static final String SHARED_SAVE_FIRST = "lebuddyFirstOpen";
    public static final String SP_IS_FIRST_OPEN = "isFirstOpen";
    public static final String SHARED_SAVE = "lebuddyData";
    /*用户登录数据*/
    public static final String SP_LOGIN_DATA = "userData";
    public static final String SP_USER_BALANCE_DATA = "userBalanceData"; //我的钱包
    public static final String SP_ADDRESS_LIST = "addressList"; //address list
    public static final String IS_ALREADY_LOGIN = "isAlreadyLogin";//是否登陆过

    public static final int CAMERA_JAVA_REQUEST_CODE = 1211;//相机权限请求码
    public static final String IM_IMAGE_PATH = "/lebuddy/im/image"; //拍照图片消息存放位置
}
