package com.xahaolan.emanage.ui.checkwork.apply;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
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
import com.xahaolan.emanage.ui.MainActivity;
import com.xahaolan.emanage.utils.common.ToastUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/3.   申请单(请假、外出登记、出差、加班登记)
 */

public class DocumentActivity extends BaseActivity {
    private static final String TAG = DocumentActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    private Intent intent;
    private int applyType;  //1.请假申请  2.外出登记  3.出差申请  4.加班登记

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
            /* voice */
            case R.id.apply_document_voice_layout:

                break;
            /* photos */
            case R.id.apply_document_photos_icon:

                break;
            /* submit */
            case R.id.apply_document_submit_layout:
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

    /**
     * 请假申请
     */
    public void requestApplyLeave() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckWorkServices(context).leaveOrderAddService(personId, personName, startDate,
                endDate, reason, new Handler() {
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
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckWorkServices(context).outGoingAddService(personId, personName, startDate,
                endDate, reason, new Handler() {
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
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckWorkServices(context).bussinessTripAddService(personId, personName, origin, destination,
                startDate, endDate, vehicle, reason, new Handler() {
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
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckWorkServices(context).workAddService(personId, startDate,
                endDate, reason, new Handler() {
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

    public View getPhotoItemView(String imageUrl){
        View photo_view = LayoutInflater.from(context).inflate(R.layout.item_view_image,null);
        ImageView photo_image = (ImageView) photo_view.findViewById(R.id.item_view_photo_image);
        Glide.with(context).load(imageUrl).into(photo_image);
        return photo_view;
    }
}
