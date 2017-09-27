package com.xahaolan.emanage.ui.checkwork.apply;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
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
import com.xahaolan.emanage.base.MyApplication;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.services.CheckWorkServices;
import com.xahaolan.emanage.manager.PhotoCamerManager;
import com.xahaolan.emanage.manager.VoiceManager;
import com.xahaolan.emanage.ui.MainActivity;
import com.xahaolan.emanage.utils.common.BitmapUtils;
import com.xahaolan.emanage.utils.common.ToastUtils;
import com.xahaolan.emanage.utils.mine.AppUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/3.   申请单(请假、外出登记、出差、加班登记)
 */

public class DocumentActivity extends BaseActivity {
    private static final String TAG = DocumentActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    private Intent intent;
    private VoiceManager voiceManager;
    private PhotoCamerManager photoCamerUtil;
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
    private EditText out_time_et;
    /* 开始时间 */
    private LinearLayout start_time_layout;
    private EditText start_time_et;
    /* 结束时间 */
    private LinearLayout end_time_layout;
    private EditText end_time_et;
    /* 加班时长 */
    private LinearLayout work_time_layout;
    private EditText work_time_et;
    /* 请假天数 */
    private RelativeLayout leave_day_layout;
    private EditText leave_day_et;
    /* 请假类型 */
    private RelativeLayout leave_type_layout;
    private EditText leave_type_et;
    /* 交通工具 */
    private RelativeLayout trafic_tool_layout;
    private EditText trafic_tool_et;
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
    private String personName;//  员工姓名
    private String startDate;// 开始日期（2017-09-11）
    private String endDate;//  结束日期（2017-09-12）
    private String origin;//      始发地
    private String destination;//  目的地
    private String vehicle;//  交通工具
    private String reason;//  原因
    private String voiceFile;
    private String[] sourceFile;
    private List<String> sourceList;

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
        out_time_et = (EditText) findViewById(R.id.apply_document_out_time_et);
        out_time_layout.setOnClickListener(this);
        start_time_layout = (LinearLayout) findViewById(R.id.apply_document_start_time_layout);
        start_time_et = (EditText) findViewById(R.id.apply_document_start_time_et);
        start_time_layout.setOnClickListener(this);
        end_time_layout = (LinearLayout) findViewById(R.id.apply_document_end_time_layout);
        end_time_et = (EditText) findViewById(R.id.apply_document_end_time_et);
        end_time_layout.setOnClickListener(this);
        work_time_layout = (LinearLayout) findViewById(R.id.apply_document_work_time_layout);
        work_time_et = (EditText) findViewById(R.id.apply_document_work_time_et);
        work_time_layout.setOnClickListener(this);
        leave_day_layout = (RelativeLayout) findViewById(R.id.apply_document_leave_day_layout);
        leave_day_et = (EditText) findViewById(R.id.apply_document_leave_day_et);
        leave_day_layout.setOnClickListener(this);
        leave_type_layout = (RelativeLayout) findViewById(R.id.apply_document_leave_type_layout);
        leave_type_et = (EditText) findViewById(R.id.apply_document_leave_type_et);
        leave_type_layout.setOnClickListener(this);
        trafic_tool_layout = (RelativeLayout) findViewById(R.id.apply_document_traffic_tool_layout);
        trafic_tool_et = (EditText) findViewById(R.id.apply_document_traffic_tool_et);
        trafic_tool_layout.setOnClickListener(this);
        reason_layout = (LinearLayout) findViewById(R.id.apply_document_reason_layout);
        reason_text = (TextView) findViewById(R.id.apply_document_reason_text);
        reason_et = (EditText) findViewById(R.id.apply_document_reason_et);
        voice_icon = (ImageView) findViewById(R.id.apply_document_voice_icon);
        voice_icon.setOnClickListener(this);
        voice_text = (TextView) findViewById(R.id.apply_document_voice_length);
        voice_text.setOnClickListener(this);
        voice_icon.setOnTouchListener(new View.OnTouchListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        voiceManager.startRecord();
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        voiceManager.stopRecord(new Handler(){
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                if (msg.what == 123){
                                    String voicePath = (String) msg.obj;
                                    try {
                                        voiceFile = String.valueOf(BitmapUtils.readStream(voicePath));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        voice_text.setBackground(MyUtils.getShape(MyConstant.COLOR_BLUE, 5f, 1, MyConstant.COLOR_BLUE));
                        voice_text.setVisibility(View.VISIBLE);
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
        sourceList = new ArrayList<>();
        voiceManager = new VoiceManager();
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
                reason_text.setText("请假是由");
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

                break;
            /* 开始时间 */
            case R.id.apply_document_start_time_layout:

                break;
            /* 结束时间 */
            case R.id.apply_document_end_time_layout:

                break;
            /* 加班时长 */
            case R.id.apply_document_work_time_layout:

                break;
            /* 请假天数 */
            case R.id.apply_document_leave_day_layout:

                break;
            /* 请假类型 */
            case R.id.apply_document_leave_type_layout:

                break;
            /* 交通工具 */
            case R.id.apply_document_traffic_tool_layout:

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
                photoCamerUtil.takePhotoCamer(1, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (msg.what == MyConstant.HANDLER_SUCCESS) {
                            String imagePath = (String) msg.obj;
                            try {
                                sourceList.add(String.valueOf(BitmapUtils.readStream(imagePath)));
                                for (int i=0;i < sourceList.size(); i ++){
                                    photos_layout.addView(addPhotoItemView(sourceList.get(i)));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                break;
            /* submit */
            case R.id.apply_document_submit_layout:
                getParamsData();
                if (applyType == MyConstant.APPLY_DOCUMENT_LEAVE_APPLY) {
                    requestApplyLeave();
                } else if (applyType == MyConstant.APPLY_DOCUMENT_OUT_REGISTER) {
                    requestApplyOutRegister();
                } else if (applyType == MyConstant.APPLY_DOCUMENT_OUT_APPLY) {
                    requestApplyOut();
                } else if (applyType == MyConstant.APPLY_DOCUMENT_WORK_REGISTER) {
                    requestApplyWork();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void getParamsData() {
        /* 请假申请 */
        if (applyType == MyConstant.APPLY_DOCUMENT_LEAVE_APPLY) {
            if (start_time_et.getText().toString() == null || start_time_et.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请输入开始时间");
                return;
            }
            if (end_time_et.getText().toString() == null || end_time_et.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请输入结束时间");
                return;
            }
            if (leave_day_et.getText().toString() == null || leave_day_et.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请输入请假天数");
                return;
            }
            if (reason_et.getText().toString() == null || reason_et.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请填写请假事由");
                return;
            }
            /* 外出登记 */
        } else if (applyType == MyConstant.APPLY_DOCUMENT_OUT_REGISTER) {
            if (out_time_et.getText().toString() == null || out_time_et.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请输入外出时间");
                return;
            }
            if (start_time_et.getText().toString() == null || start_time_et.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请输入开始时间");
                return;
            }
            if (end_time_et.getText().toString() == null || end_time_et.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请输入结束时间");
                return;
            }
            if (reason_et.getText().toString() == null || reason_et.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请填写外出事由");
                return;
            }
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
            if (start_time_et.getText().toString() == null || start_time_et.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请输入开始时间");
                return;
            }
            if (end_time_et.getText().toString() == null || end_time_et.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请输入结束时间");
                return;
            }
            if (trafic_tool_et.getText().toString() == null || trafic_tool_et.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请选择交通工具");
                return;
            }
            if (reason_et.getText().toString() == null || reason_et.getText().toString().equals("")) {
                ToastUtils.showShort(context, "请填写出差事由");
                return;
            }
/* 加班登记 */
        } else if (applyType == MyConstant.APPLY_DOCUMENT_WORK_REGISTER) {

        }
        personId = AppUtils.getPersonId(context);
        personName = AppUtils.getPersonName(context);
        startDate = start_time_et.getText().toString();
        endDate = end_time_et.getText().toString();
        origin = start_position_et.getText().toString();//      始发地
        destination = end_position_et.getText().toString();//  目的地
        vehicle = trafic_tool_et.getText().toString();//  交通工具
        reason = reason_et.getText().toString();
    }

    /**
     * 请假申请
     */
    public void requestApplyLeave() {
        if (sourceList == null || sourceList.size() <=0){
            ToastUtils.showShort(context,"请上传图片");
            return;
        }
        if (voiceFile == null || voiceFile.equals("")){
            sourceFile = new String[sourceList.size()];
            for (int i = 0;i < sourceList.size();i++){
                sourceFile[i] = sourceList.get(i);
            }
        }else {
            sourceFile = new String[sourceList.size()+1];
            for (int i = 0;i < sourceList.size()+1;i++){
                if (i == 0){
                    sourceFile[i] = voiceFile;
                }else {
                    sourceFile[i+1] = sourceList.get(i);

                }
            }
        }
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckWorkServices(context).leaveOrderAddService(personId, personName, startDate,
                endDate, reason, sourceFile,new Handler() {
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
                        } else if (msg.what == MyConstant.REQUEST_ERROR) {
                            String errMsg = (String) msg.obj;
                            ToastUtils.showShort(context, errMsg);
                        }
                    }
                });
    }

    /**
     * 外出登记申请
     */
    public void requestApplyOutRegister() {
        if (sourceList == null || sourceList.size() <=0){
            ToastUtils.showShort(context,"请上传图片");
            return;
        }
        if (voiceFile == null || voiceFile.equals("")){
            sourceFile = new String[sourceList.size()];
            for (int i = 0;i < sourceList.size();i++){
                sourceFile[i] = sourceList.get(i);
            }
        }else {
            sourceFile = new String[sourceList.size()+1];
            for (int i = 0;i < sourceList.size()+1;i++){
                if (i == 0){
                    sourceFile[i] = voiceFile;
                }else {
                    sourceFile[i+1] = sourceList.get(i);

                }
            }
        }
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckWorkServices(context).outGoingAddService(personId, personName, startDate,
                endDate, reason, sourceFile,new Handler() {
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
                        } else if (msg.what == MyConstant.REQUEST_ERROR) {
                            String errMsg = (String) msg.obj;
                            ToastUtils.showShort(context, errMsg);
                        }
                    }
                });
    }

    /**
     * 出差申请
     */
    public void requestApplyOut() {
        if (sourceList == null || sourceList.size() <=0){
            ToastUtils.showShort(context,"请上传图片");
            return;
        }
        if (voiceFile == null || voiceFile.equals("")){
            sourceFile = new String[sourceList.size()];
            for (int i = 0;i < sourceList.size();i++){
                sourceFile[i] = sourceList.get(i);
            }
        }else {
            sourceFile = new String[sourceList.size()+1];
            for (int i = 0;i < sourceList.size()+1;i++){
                if (i == 0){
                    sourceFile[i] = voiceFile;
                }else {
                    sourceFile[i+1] = sourceList.get(i);

                }
            }
        }
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckWorkServices(context).bussinessTripAddService(personId, personName, origin, destination,
                startDate, endDate, vehicle, reason,sourceFile,new Handler() {
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
                        } else if (msg.what == MyConstant.REQUEST_ERROR) {
                            String errMsg = (String) msg.obj;
                            ToastUtils.showShort(context, errMsg);
                        }
                    }
                });
    }

    /**
     * 加班登记
     */
    public void requestApplyWork() {
        if (sourceList == null || sourceList.size() <=0){
            ToastUtils.showShort(context,"请上传图片");
            return;
        }
        if (voiceFile == null || voiceFile.equals("")){
            sourceFile = new String[sourceList.size()];
            for (int i = 0;i < sourceList.size();i++){
                sourceFile[i] = sourceList.get(i);
            }
        }else {
            sourceFile = new String[sourceList.size()+1];
            for (int i = 0;i < sourceList.size()+1;i++){
                if (i == 0){
                    sourceFile[i] = voiceFile;
                }else {
                    sourceFile[i+1] = sourceList.get(i);

                }
            }
        }
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckWorkServices(context).workAddService(personId, startDate,
                endDate, reason,sourceFile, new Handler() {
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
                        } else if (msg.what == MyConstant.REQUEST_ERROR) {
                            String errMsg = (String) msg.obj;
                            ToastUtils.showShort(context, errMsg);
                        }
                    }
                });
    }

    public View addPhotoItemView(String imageUrl) {
        View photo_view = LayoutInflater.from(context).inflate(R.layout.item_view_image, null);
        ImageView photo_image = (ImageView) photo_view.findViewById(R.id.item_view_photo_image);
        Glide.with(context).load(imageUrl).into(photo_image);
        return photo_view;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MyUtils.hideKeyboard((Activity) context);
        photoCamerUtil.activityResult(requestCode, resultCode, data);
    }
}
