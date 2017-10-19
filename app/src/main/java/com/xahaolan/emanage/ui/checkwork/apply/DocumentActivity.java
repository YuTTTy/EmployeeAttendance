package com.xahaolan.emanage.ui.checkwork.apply;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.dialog.DialogSingleText;
import com.xahaolan.emanage.http.FormRequest;
import com.xahaolan.emanage.http.services.CheckWorkServices;
import com.xahaolan.emanage.manager.PhotoCamerManager;
import com.xahaolan.emanage.manager.VoiceManager;
import com.xahaolan.emanage.manager.camer.PermissionsActivity;
import com.xahaolan.emanage.manager.camer.PermissionsChecker;
import com.xahaolan.emanage.utils.common.BitmapUtils;
import com.xahaolan.emanage.utils.common.LogUtils;
import com.xahaolan.emanage.utils.common.ToastUtils;
import com.xahaolan.emanage.utils.mine.AppUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;
import com.xahaolan.emanage.view.wheel.dialog.ChangeBirthDialog;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/3.   申请单(请假、外出登记、出差、加班登记)
 */

public class DocumentActivity extends BaseActivity {
    private static final String TAG = DocumentActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    private Intent intent;
    private ChangeBirthDialog timeDialog;
    private VoiceManager voiceManager;
    private PhotoCamerManager photoCamerUtil;
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private static final int REQUEST_PERMISSION = 4;  //权限请求
    static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private int applyType;  //1.请假申请  2.外出登记  3.出差申请  4.加班登记
    private int audioType = 0;// 0.未播放  1.正在播放

    /* 始发地 */
    private LinearLayout start_position_layout;
    private EditText start_position_et;
    /* 目的地 */
    private LinearLayout end_position_layout;
    private EditText end_position_et;
    /* 外出时间 */
    private LinearLayout out_time_layout;
    private TextView out_time_text;
    /* 开始时间 */
    private LinearLayout start_time_layout;
    private TextView start_time_text;
    /* 结束时间 */
    private LinearLayout end_time_layout;
    private TextView end_time_text;
    /* 加班时长 */
    private LinearLayout work_time_layout;
    private EditText work_time_et;
    /* 请假天数 */
    private RelativeLayout leave_day_layout;
    private EditText leave_day_et;
    /* 请假类型 */
    private RelativeLayout leave_type_layout;
    private TextView leave_type_text;
    /* 交通工具 */
    private RelativeLayout trafic_tool_layout;
    private TextView trafic_tool_text;
    /* 是由 */
    private LinearLayout reason_layout;
    private TextView reason_text;
    private EditText reason_et;
    /* voice */
    private ImageView voice_icon;
    private TextView voice_text;
    /* photos */
    private LinearLayout photos_layout;
    private ImageView photo_icon;
    private LinearLayout photo_items_layout;
    /* 审核人员 */
    private LinearLayout examine_layout;
    private EditText examine_et;
    /* submit */
    private FrameLayout submit_layout;

    private int personId;//  员工id
    private int departmentId;//  部门id
    private String personName;//  员工姓名
    private String outData;// 外出时间
    private String startDate;// 开始日期（2017-09-11）
    private String endDate;//  结束日期（2017-09-12）
    private String origin;//      始发地
    private String destination;//  目的地
    private String vehicle;//  交通工具
    private String reason;//  原因
    private String voiceFile;
    private String[] sourceFile;
    private List<String> sourceList;
    private Map<String, Object> paramsMap; //非资源文件params
    private List<Map<String,Object>> fileList;//资源文件params

    private String[] leaveTypeArr = {"病假", "事假", "婚假", "丧假", "产假", "年休假"};
    private String[] trafficTypeArr = {"汽车", "火车", "轮船", "飞机"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_apply_document);
    }

    @Override
    public void setTitleAttribute() {
    }

    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        start_position_layout = (LinearLayout) findViewById(R.id.apply_document_start_position_layout);
        start_position_et = (EditText) findViewById(R.id.apply_document_start_position_et);
        end_position_layout = (LinearLayout) findViewById(R.id.apply_document_end_position_layout);
        end_position_et = (EditText) findViewById(R.id.apply_document_end_position_et);
        out_time_layout = (LinearLayout) findViewById(R.id.apply_document_out_time_layout);
        out_time_text = (TextView) findViewById(R.id.apply_document_out_time_text);
        out_time_layout.setOnClickListener(this);
        start_time_layout = (LinearLayout) findViewById(R.id.apply_document_start_time_layout);
        start_time_text = (TextView) findViewById(R.id.apply_document_start_time_text);
        start_time_layout.setOnClickListener(this);
        end_time_layout = (LinearLayout) findViewById(R.id.apply_document_end_time_layout);
        end_time_text = (TextView) findViewById(R.id.apply_document_end_time_text);
        end_time_layout.setOnClickListener(this);
        work_time_layout = (LinearLayout) findViewById(R.id.apply_document_work_time_layout);
        work_time_et = (EditText) findViewById(R.id.apply_document_work_time_et);
        work_time_layout.setOnClickListener(this);
        leave_day_layout = (RelativeLayout) findViewById(R.id.apply_document_leave_day_layout);
        leave_day_et = (EditText) findViewById(R.id.apply_document_leave_day_et);
        leave_day_layout.setOnClickListener(this);
        leave_type_layout = (RelativeLayout) findViewById(R.id.apply_document_leave_type_layout);
        leave_type_text = (TextView) findViewById(R.id.apply_document_leave_type_et);
        leave_type_layout.setOnClickListener(this);
        trafic_tool_layout = (RelativeLayout) findViewById(R.id.apply_document_traffic_tool_layout);
        trafic_tool_text = (TextView) findViewById(R.id.apply_document_traffic_tool_et);
        trafic_tool_layout.setOnClickListener(this);
        reason_layout = (LinearLayout) findViewById(R.id.apply_document_reason_layout);
        reason_text = (TextView) findViewById(R.id.apply_document_reason_text);
        reason_et = (EditText) findViewById(R.id.apply_document_reason_et);
        voice_icon = (ImageView) findViewById(R.id.apply_document_voice_icon);
        voice_text = (TextView) findViewById(R.id.apply_document_voice_length);
        voice_text.setOnClickListener(this);
        voice_icon.setOnClickListener(this);
        voice_icon.setOnTouchListener(new View.OnTouchListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        voiceManager.startRecord();
                        voice_icon.setImageResource(R.drawable.icon_voice_press);
                        voice_text.setText("录音中.....");
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        voiceManager.stopRecord(new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                if (msg.what == 123) {
                                    String voicePath = (String) msg.obj;
                                    try {
                                        voiceFile = voicePath;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        voice_text.setBackground(MyUtils.getShape(MyConstant.COLOR_BLUE, 5f, 1, MyConstant.COLOR_BLUE));
                        voice_text.setVisibility(View.VISIBLE);
                        voice_text.setText("");
                        voice_icon.setImageResource(R.drawable.icon_voice);
                        break;
                }
                return false;
            }
        });
        photos_layout = (LinearLayout) findViewById(R.id.apply_document_photos_layout);
        photo_icon = (ImageView) findViewById(R.id.apply_document_photos_icon);
        photo_icon.setOnClickListener(this);
        photo_items_layout = (LinearLayout) findViewById(R.id.apply_document_photos_items_layout);
        submit_layout = (FrameLayout) findViewById(R.id.apply_document_submit_layout);
        submit_layout.setOnClickListener(this);
        examine_layout = (LinearLayout) findViewById(R.id.apply_document_examine_layout);
        examine_et = (EditText) findViewById(R.id.apply_document_examine_et);
    }

    @Override
    public void initData() {
        timeDialog = new ChangeBirthDialog(context);
        sourceList = new ArrayList<>();
        paramsMap = new HashMap<>();
        fileList = new ArrayList<>();
        voiceManager = new VoiceManager();
        mPermissionsChecker = new PermissionsChecker(this);
        photoCamerUtil = new PhotoCamerManager((Activity) context, context);
        intent = getIntent();
        applyType = intent.getIntExtra("ApplyType", 1);
        switch (applyType) {
            //请假申请
            case MyConstant.APPLY_DOCUMENT_LEAVE_APPLY:
                setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "返回", R.color.baseTextMain, "请假申请", R.color.baseTextMain, "", R.color.baseTextMain, 0);
                start_position_layout.setVisibility(View.GONE);
                end_position_layout.setVisibility(View.GONE);
                out_time_layout.setVisibility(View.GONE);
                work_time_layout.setVisibility(View.GONE);
                trafic_tool_layout.setVisibility(View.GONE);
                reason_text.setText("请假事由");
                reason_et.setHint("填写请假事由");
                break;
            //外出登记
            case MyConstant.APPLY_DOCUMENT_OUT_REGISTER:
                setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "返回", R.color.baseTextMain, "外出登记", R.color.baseTextMain, "", R.color.baseTextMain, 0);
                start_position_layout.setVisibility(View.GONE);
                end_position_layout.setVisibility(View.GONE);
                work_time_layout.setVisibility(View.GONE);
                leave_day_layout.setVisibility(View.GONE);
                leave_type_layout.setVisibility(View.GONE);
                trafic_tool_layout.setVisibility(View.GONE);
                reason_text.setText("外出事由");
                reason_et.setHint("填写外出事由");
                break;
            //出差申请
            case MyConstant.APPLY_DOCUMENT_OUT_APPLY:
                setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "返回", R.color.baseTextMain, "出差申请", R.color.baseTextMain, "", R.color.baseTextMain, 0);
                out_time_layout.setVisibility(View.GONE);
                work_time_layout.setVisibility(View.GONE);
                leave_day_layout.setVisibility(View.GONE);
                leave_type_layout.setVisibility(View.GONE);
                reason_text.setText("出差事由");
                reason_et.setHint("填写出差事由");
                break;
            //加班登记
            case MyConstant.APPLY_DOCUMENT_WORK_REGISTER:
                setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "返回", R.color.baseTextMain, "加班登记", R.color.baseTextMain, "", R.color.baseTextMain, 0);
                start_position_layout.setVisibility(View.GONE);
                end_position_layout.setVisibility(View.GONE);
                out_time_layout.setVisibility(View.GONE);
                leave_day_layout.setVisibility(View.GONE);
                leave_type_layout.setVisibility(View.GONE);
                trafic_tool_layout.setVisibility(View.GONE);
                reason_text.setText("加班事由");
                reason_et.setHint("填写加班事由");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            /* 外出时间 */
            case R.id.apply_document_out_time_layout:
                ChangeBirthDialog timeDialog = new ChangeBirthDialog(context);
                Calendar calendar = Calendar.getInstance();
                timeDialog.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
                timeDialog.show();
                timeDialog.setBirthdayListener(new ChangeBirthDialog.OnBirthListener() {
                    @Override
                    public void onClick(String year, String month, String day) {
                        String sltTimeStr = year + "-" + MyUtils.suppleSingular(Integer.parseInt(month)) + "-" + day;
                        out_time_text.setText(sltTimeStr);
                    }
                });
                break;
            /* 开始时间 */
            case R.id.apply_document_start_time_layout:
                ChangeBirthDialog startTimeDialog = new ChangeBirthDialog(context);
                Calendar startCalendar = Calendar.getInstance();
                startTimeDialog.setDate(startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH) + 1, startCalendar.get(Calendar.DATE));
                startTimeDialog.show();
                startTimeDialog.setBirthdayListener(new ChangeBirthDialog.OnBirthListener() {
                    @Override
                    public void onClick(String year, String month, String day) {
                        String sltTimeStr = year + "-" + MyUtils.suppleSingular(Integer.parseInt(month)) + "-" + day;
                        start_time_text.setText(sltTimeStr);
                    }
                });
                break;
            /* 结束时间 */
            case R.id.apply_document_end_time_layout:
                ChangeBirthDialog endTimeDialog = new ChangeBirthDialog(context);
                Calendar endCalendar = Calendar.getInstance();
                endTimeDialog.setDate(endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH) + 1, endCalendar.get(Calendar.DATE));
                endTimeDialog.show();
                endTimeDialog.setBirthdayListener(new ChangeBirthDialog.OnBirthListener() {
                    @Override
                    public void onClick(String year, String month, String day) {
                        String sltTimeStr = year + "-" + MyUtils.suppleSingular(Integer.parseInt(month)) + "-" + day;
                        end_time_text.setText(sltTimeStr);
                    }
                });
                break;
            /* 加班时长 */
            case R.id.apply_document_work_time_layout:

                break;
            /* 请假天数 */
            case R.id.apply_document_leave_day_layout:

                break;
            /* 请假类型 */
            case R.id.apply_document_leave_type_layout:
                new DialogSingleText(context, leaveTypeArr, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        int pos = (int) msg.obj;
                        String sltType = leaveTypeArr[pos];
                        if (sltType != null && !sltType.equals("")) {
                            leave_type_text.setText(sltType);
                        }
                    }
                }).show();
                break;
            /* 交通工具 */
            case R.id.apply_document_traffic_tool_layout:
                new DialogSingleText(context, trafficTypeArr, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        int pos = (int) msg.obj;
                        String sltType = trafficTypeArr[pos];
                        if (sltType != null && !sltType.equals("")) {
                            trafic_tool_text.setText(sltType);
                        }
                    }
                }).show();
                break;
            /* play audio */
            case R.id.apply_document_voice_length:
                if (audioType == 0) {
                    audioType = 1;
                    voiceManager.playAudio(context);
                } else if (audioType == 1) {
                    audioType = 0;
                    voiceManager.stopAudio();
                }
                break;
            /* photos */
            case R.id.apply_document_photos_icon:
                //检查权限(6.0以上做权限判断)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                        PermissionsActivity.startActivityForResult(this, REQUEST_PERMISSION,
                                PERMISSIONS);
                    } else {
                        openCamer();
                    }
                } else {
                    openCamer();
                }


                break;
            /* submit */
            case R.id.apply_document_submit_layout:
                getParamsData();
                break;
        }
    }

    public void openCamer() {
        photoCamerUtil.takePhotoCamer(1, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MyConstant.HANDLER_SUCCESS) {
                    String imagePath = (String) msg.obj;
                    if (imagePath == null || imagePath.equals("")) {
                        LogUtils.e(TAG, "拍照图片路径 ：" + imagePath);
                        return;
                    }
                    try {
                        sourceList.add(imagePath);
                        photo_items_layout.removeAllViews();
                        for (int i = 0; i < sourceList.size(); i++) {
                            photo_items_layout.addView(addPhotoItemView(sourceList.get(i)));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void getParamsData() {
        /* 请假申请 */
        if (applyType == MyConstant.APPLY_DOCUMENT_LEAVE_APPLY) {
            if (start_time_text.getText().toString() == null || start_time_text.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请输入开始时间");
                return;
            }
            if (end_time_text.getText().toString() == null || end_time_text.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请输入结束时间");
                return;
            }
//            if (leave_day_et.getText().toString() == null || leave_day_et.getText().toString().equals("")) {
//                ToastUtils.showShort(context, "请输入请假天数");
//                return;
//            }
//            if (reason_et.getText().toString() == null || reason_et.getText().toString().equals("")) {
//                ToastUtils.showShort(context, "请填写请假事由");
//                return;
//            }
            /* 外出登记 */
        } else if (applyType == MyConstant.APPLY_DOCUMENT_OUT_REGISTER) {
//            if (out_time_text.getText().toString() == null || out_time_text.getText().toString().equals("")) {
//                ToastUtils.showShort(context, "请输入外出时间");
//                return;
//            }
            if (start_time_text.getText().toString() == null || start_time_text.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请输入开始时间");
                return;
            }
            if (end_time_text.getText().toString() == null || end_time_text.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请输入结束时间");
                return;
            }
//            if (reason_et.getText().toString() == null || reason_et.getText().toString().equals("")) {
//                ToastUtils.showShort(context, "请填写外出事由");
//                return;
//            }
            /* 出差申请 */
        } else if (applyType == MyConstant.APPLY_DOCUMENT_OUT_APPLY) {
            if (start_position_et.getText().toString() == null || start_position_et.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请输入始发地");
                return;
            }
            if (end_position_et.getText().toString() == null || end_position_et.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请输入目的地");
                return;
            }
            if (start_time_text.getText().toString() == null || start_time_text.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请输入开始时间");
                return;
            }
            if (end_time_text.getText().toString() == null || end_time_text.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请输入结束时间");
                return;
            }
//            if (trafic_tool_et.getText().toString() == null || trafic_tool_et.getText().toString().equals("")) {
//                ToastUtils.showShort(context, "请选择交通工具");
//                return;
//            }
//            if (reason_et.getText().toString() == null || reason_et.getText().toString().equals("")) {
//                ToastUtils.showShort(context, "请填写出差事由");
//                return;
//            }
        /* 加班登记 */
        } else if (applyType == MyConstant.APPLY_DOCUMENT_WORK_REGISTER) {
            if (start_time_text.getText().toString() == null || start_time_text.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请输入开始时间");
                return;
            }
            if (end_time_text.getText().toString() == null || end_time_text.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请输入结束时间");
                return;
            }
//            if (work_time_et.getText().toString() == null || work_time_et.getText().toString().equals("")) {
//                ToastUtils.showShort(context, "请输入加班时长");
//                return;
//            }
//            if (reason_et.getText().toString() == null || reason_et.getText().toString().equals("")) {
//                ToastUtils.showShort(context, "请输入加班事由");
//                return;
//            }
        }
//        if (sourceList == null || sourceList.size() <= 0){
//            ToastUtils.showShort(context, "请上传图片");
//            return;
//        }
//        if (voiceFile == null || voiceFile.equals("")){
//            ToastUtils.showShort(context, "请录制语音");
//            return;
//        }
        personId = AppUtils.getPersonId(context);
        departmentId = AppUtils.getDepartmentId(context);
        personName = AppUtils.getPersonName(context);
        outData = out_time_text.getText().toString();
        startDate = start_time_text.getText().toString();
        endDate = end_time_text.getText().toString();
        origin = start_position_et.getText().toString();//      始发地
        destination = end_position_et.getText().toString();//  目的地
        vehicle = trafic_tool_text.getText().toString();//  交通工具
        reason = reason_et.getText().toString();

        if (applyType == MyConstant.APPLY_DOCUMENT_LEAVE_APPLY) {
            requestApplyLeave();
        } else if (applyType == MyConstant.APPLY_DOCUMENT_OUT_REGISTER) {
            requestApplyOutRegister();
        } else if (applyType == MyConstant.APPLY_DOCUMENT_OUT_APPLY) {
            requestApplyOut();
        } else if (applyType == MyConstant.APPLY_DOCUMENT_WORK_REGISTER) {
            requestApplyWork();
        }
    }

    /**
     * 请假申请
     */
    public void requestApplyLeave() {
        paramsMap.put("personId", personId);
        paramsMap.put("personName", personName);
        paramsMap.put("departmentId", departmentId);
        paramsMap.put("startDate", startDate);
        paramsMap.put("endDate", endDate);
        paramsMap.put("reason", reason);
        Map<String, Object> fileMap;
        if (sourceList != null && sourceList.size() > 0) {
            for (Object sourcePath : sourceList) {
                fileMap = new HashMap<>();
                fileMap.put("sourceFile", sourcePath);
                fileList.add(fileMap);
            }
        }
        if (voiceFile != null && !voiceFile.equals("")) {
            fileMap = new HashMap<>();
            fileMap.put("sourceFile", voiceFile);
            fileList.add(fileMap);
        }
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        LogUtils.e(TAG, "---------------- 请假申请request ----------------");
        LogUtils.e(TAG, "请假申请 request url : " + MyConstant.BASE_URL + "/app/dailyreportAPPAction!add.action");
        new FormRequest(context, MyConstant.BASE_URL + "/app/leaveOrderAPPAction!add.action", paramsMap, fileList, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    finish();
                } else if (msg.what == MyConstant.REQUEST_FIELD) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                    if (errMsg.equals("session过期")) {
                        BaseActivity.loginOut(context);
                    }
                } else if (msg.what == MyConstant.REQUEST_ERROR) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                }
            }
        });

//        preRequest();
//        new CheckWorkServices(context).leaveOrderAddService(personId, personName, startDate,
//                endDate, reason, sourceFile, new Handler() {
//                    @Override
//                    public void handleMessage(Message msg) {
//                        super.handleMessage(msg);
//                        if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
//                            swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
//                        }
//                        if (msg.what == MyConstant.REQUEST_SUCCESS) {
//                            finish();
//                        } else if (msg.what == MyConstant.REQUEST_FIELD) {
//                            String errMsg = (String) msg.obj;
//                            ToastUtils.showShort(context, errMsg);
//                            if (errMsg.equals("session过期")){
//                                BaseActivity.loginOut(context);
//                            }
//                        } else if (msg.what == MyConstant.REQUEST_ERROR) {
//                            String errMsg = (String) msg.obj;
//                            ToastUtils.showShort(context, errMsg);
//                        }
//                    }
//                });
    }

    /**
     * 外出登记申请
     */
    public void requestApplyOutRegister() {
        paramsMap.put("personid", personId);
        paramsMap.put("departmentId", departmentId);
        paramsMap.put("data", outData);
        paramsMap.put("starttime", startDate);
        paramsMap.put("endtime", endDate);
        paramsMap.put("reason", reason);
        Map<String, Object> fileMap;
        if (sourceList != null && sourceList.size() > 0) {
            for (Object sourcePath : sourceList) {
                fileMap = new HashMap<>();
                fileMap.put("sourceFile", sourcePath);
                fileList.add(fileMap);
            }
        }
        if (voiceFile != null && !voiceFile.equals("")) {
            fileMap = new HashMap<>();
            fileMap.put("sourceFile", voiceFile);
            fileList.add(fileMap);
        }
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        LogUtils.e(TAG, "---------------- 外出登记申请 request ----------------");
        LogUtils.e(TAG, "外出登记申请 request url : " + MyConstant.BASE_URL + "/app/outgoingAPPAction!add.action");
        new FormRequest(context, MyConstant.BASE_URL + "/app/outgoingAPPAction!add.action", paramsMap, fileList, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    finish();
                } else if (msg.what == MyConstant.REQUEST_FIELD) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                    if (errMsg.equals("session过期")) {
                        BaseActivity.loginOut(context);
                    }
                } else if (msg.what == MyConstant.REQUEST_ERROR) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                }
            }
        });

//        preRequest();
//        new CheckWorkServices(context).outGoingAddService(personId, outData, startDate,
//                endDate, reason, sourceFile, new Han／dler() {
//                    @Override
//                    public void handleMessage(Message msg) {
//                        super.handleMessage(msg);
//                        if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
//                            swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
//                        }
//                        if (msg.what == MyConstant.REQUEST_SUCCESS) {
//                            finish();
//                        } else if (msg.what == MyConstant.REQUEST_FIELD) {
//                            String errMsg = (String) msg.obj;
//                            ToastUtils.showShort(context, errMsg);
//                            if (errMsg.equals("session过期")){
//                                BaseActivity.loginOut(context);
//                            }
//                        } else if (msg.what == MyConstant.REQUEST_ERROR) {
//                            String errMsg = (String) msg.obj;
//                            ToastUtils.showShort(context, errMsg);
//                        }
//                    }
//                });
    }

    /**
     * 出差申请
     */
    public void requestApplyOut() {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("personId", personId);
        paramsMap.put("personName", personName);
        paramsMap.put("departmentId", departmentId);
        paramsMap.put("origin", origin);
        paramsMap.put("destination", destination);
        paramsMap.put("startDate", startDate);
        paramsMap.put("endDate", endDate);
        paramsMap.put("vehicle", vehicle);
        paramsMap.put("reason", reason);
        Map<String, Object> fileMap;
        if (sourceList != null && sourceList.size() > 0) {
            for (Object sourcePath : sourceList) {
                fileMap = new HashMap<>();
                fileMap.put("sourceFile", sourcePath);
                fileList.add(fileMap);
            }
        }
        if (voiceFile != null && !voiceFile.equals("")) {
            fileMap = new HashMap<>();
            fileMap.put("sourceFile", voiceFile);
            fileList.add(fileMap);
        }
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        LogUtils.e(TAG, "---------------- 出差申请 request ----------------");
        LogUtils.e(TAG, "出差申请 request url : " + MyConstant.BASE_URL + "/app/businessTrip!add.action");
        new FormRequest(context, MyConstant.BASE_URL + "/app/businessTrip!add.action", paramsMap, fileList, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    finish();
                } else if (msg.what == MyConstant.REQUEST_FIELD) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                    if (errMsg.equals("session过期")) {
                        BaseActivity.loginOut(context);
                    }
                } else if (msg.what == MyConstant.REQUEST_ERROR) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                }
            }
        });

//        preRequest();
//        new CheckWorkServices(context).bussinessTripAddService(personId, personName, origin, destination,
//                startDate, endDate, vehicle, reason, sourceFile, new Handler() {
//                    @Override
//                    public void handleMessage(Message msg) {
//                        super.handleMessage(msg);
//                        if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
//                            swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
//                        }
//                        if (msg.what == MyConstant.REQUEST_SUCCESS) {
//                            finish();
//                        } else if (msg.what == MyConstant.REQUEST_FIELD) {
//                            String errMsg = (String) msg.obj;
//                            ToastUtils.showShort(context, errMsg);
//                            if (errMsg.equals("session过期")){
//                                BaseActivity.loginOut(context);
//                            }
//                        } else if (msg.what == MyConstant.REQUEST_ERROR) {
//                            String errMsg = (String) msg.obj;
//                            ToastUtils.showShort(context, errMsg);
//                        }
//                    }
//                });
    }

    /**
     * 加班登记
     */
    public void requestApplyWork() {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("personid", personId);
        paramsMap.put("startDate", startDate);
        paramsMap.put("departmentId", departmentId);
        paramsMap.put("endDate", endDate);
        paramsMap.put("reason", reason);
        Map<String, Object> fileMap;
        if (sourceList != null && sourceList.size() > 0) {
            for (Object sourcePath : sourceList) {
                fileMap = new HashMap<>();
                fileMap.put("sourceFile", sourcePath);
                fileList.add(fileMap);
            }
        }
        if (voiceFile != null && !voiceFile.equals("")) {
            fileMap = new HashMap<>();
            fileMap.put("sourceFile", voiceFile);
            fileList.add(fileMap);
        }
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        LogUtils.e(TAG, "---------------- 加班登记 request ----------------");
        LogUtils.e(TAG, "加班登记 request url : " + MyConstant.BASE_URL + "/app/workAPPAction!add.action");
        new FormRequest(context, MyConstant.BASE_URL + "/app/workAPPAction!add.action", paramsMap, fileList, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    finish();
                } else if (msg.what == MyConstant.REQUEST_FIELD) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                    if (errMsg.equals("session过期")) {
                        BaseActivity.loginOut(context);
                    }
                } else if (msg.what == MyConstant.REQUEST_ERROR) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                }
            }
        });

//        preRequest();
//        new CheckWorkServices(context).workAddService(personId, startDate,
//                endDate, reason, sourceFile, new Handler() {
//                    @Override
//                    public void handleMessage(Message msg) {
//                        super.handleMessage(msg);
//                        if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
//                            swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
//                        }
//                        if (msg.what == MyConstant.REQUEST_SUCCESS) {
//                            finish();
//                        } else if (msg.what == MyConstant.REQUEST_FIELD) {
//                            String errMsg = (String) msg.obj;
//                            ToastUtils.showShort(context, errMsg);
//                            if (errMsg.equals("session过期")){
//                                BaseActivity.loginOut(context);
//                            }
//                        } else if (msg.what == MyConstant.REQUEST_ERROR) {
//                            String errMsg = (String) msg.obj;
//                            ToastUtils.showShort(context, errMsg);
//                        }
//                    }
//                });
    }

    /**
     * 添加照片
     *
     * @param imageUrl
     * @return
     */
    public View addPhotoItemView(String imageUrl) {
        View photo_view = LayoutInflater.from(context).inflate(R.layout.item_view_image, null);
        ImageView photo_image = (ImageView) photo_view.findViewById(R.id.item_view_photo_image);
        photo_image.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(context).load(imageUrl).into(photo_image);
        return photo_view;
    }

//    /**
//     *  资源文件
//     */
//    public void preRequest() {
////        if (sourceList == null || sourceList.size() <= 0) {
////            ToastUtils.showShort(context, "请上传图片");
////            return;
////        }
//        if (voiceFile == null || voiceFile.equals("")) {
//            sourceFile = new String[sourceList.size()];
//            for (int i = 0; i < sourceList.size(); i++) {
//                try {
//                    String imageStr = BitmapUtils.readStream(sourceList.get(i));
//                    LogUtils.e(TAG, "imageStr : " + imageStr);
//                    sourceFile[i] = imageStr;
//                    LogUtils.e(TAG, "sourceFile : " + sourceFile.toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        } else {
//            sourceFile = new String[sourceList.size() + 1];
//            for (int i = 0; i < sourceList.size() + 1; i++) {
//                if (i == sourceList.size()) {
//                    try {
//                        sourceFile[i] = BitmapUtils.readStream(voiceFile);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    try {
//                        sourceFile[i] = BitmapUtils.readStream(sourceList.get(i));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        }
//        if (swipeLayout != null) {
//            swipeLayout.setRefreshing(true);
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MyUtils.hideKeyboard((Activity) context);
        photoCamerUtil.activityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        voiceManager.stopAudio();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
