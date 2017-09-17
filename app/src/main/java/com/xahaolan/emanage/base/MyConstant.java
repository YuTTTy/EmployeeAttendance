package com.xahaolan.emanage.base;

/**
 * Created by Administrator on 2016/6/17.
 */
public class MyConstant {
    /* baseUrl*/
    public static final String BASE_URL = "http://159.226.139.228:9990/building";

    /*请求网络*/
    public static final int REQUEST_SUCCESS = 0x1001;//请求成功
    public static final int REQUEST_FIELD = 0x1002;//请求失败
    public static final int REQUEST_ERROR = 0x1003;//请求错误
//    public static final int SUCCESS_CODE = 100000;//success code

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

    /*SharedPerferenced存储*/
    public static final String SHARED_SAVE = "xahaolanData";
    public static final String SESSION_ID = "seddionId";

    /*用户登录数据*/
    public static final String SP_LOGIN_DATA = "userData";
    public static final String IS_ALREADY_LOGIN = "isAlreadyLogin";//是否登陆过

    public static final int CAMERA_JAVA_REQUEST_CODE = 1211;//相机权限请求码
    public static final String IM_IMAGE_PATH = "/lebuddy/im/image"; //拍照图片消息存放位置

    /* 色值*/
    public static final String COLOR_TEXT = "#485259";//
    public static final String COLOR_TEXT_ASSIST = "#CFCFCF";//
    public static final String COLOR_GRAY_TEXT = "#878787";//
    public static final String COLOR_BASE_LINE = "#ECECEC";//
    public static final String COLOR_GREEN_TEXT = "#1afa29";//
    public static final String COLOR_WHITE = "#ffffff";//
    public static final String COLOR_BASE_BG = "#F8F8FA";//
    public static final String COLOR_ACTIVITY_BG = "#fbfbfe";//
    public static final String COLOR_GRAY_BG = "#CFCFCF";//
    public static final String COLOR_RED = "#ff6666";//
    public static final String COLOR_BLUE = "#6ab7ff";//
    public static final String COLOR_ALPHA = "#00ffffff";//
    public static final String COLOR_ORANGE = "#FF8000";//

    /* 考勤 */
    public static final int APPLY_DOCUMENT_LEAVE_APPLY = 1;  //请假申请
    public static final int APPLY_DOCUMENT_OUT_REGISTER = 2;  //外出登记
    public static final int APPLY_DOCUMENT_OUT_APPLY = 3;  //出差申请
    public static final int APPLY_DOCUMENT_WORK_REGISTER = 4;  //加班登记

    /* 查看我的申请，审批 */
    public static final int CHECK_MINE_APPLY = 1;  //查看我的申请
    public static final int CHECK_MINE_EXAMINE = 2;  //查看我的审批
}
