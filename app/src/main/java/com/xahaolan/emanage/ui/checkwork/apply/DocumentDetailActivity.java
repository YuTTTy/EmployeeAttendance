package com.xahaolan.emanage.ui.checkwork.apply;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.services.CheckWorkServices;
import com.xahaolan.emanage.utils.common.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/3.   申请单详情
 */

public class DocumentDetailActivity extends BaseActivity {
    private static final String TAG = DocumentDetailActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    private Intent intent;
    private int applyType;  //1.请假申请  2.外出登记  3.出差申请  4.加班登记

    private ImageView head_image;
    private TextView name_text;
    private TextView time_text;
    private TextView state_text;
    private LinearLayout items_layout;
    private TextView reason_name;
    private TextView reason_text;
    private TextView voice_text;
    private LinearLayout phots_layout;
    private TextView check_name;

    private int id;
    private String[] nameArr;
    private String[] valueArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_appley_document_detail);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "返回", R.color.baseTextMain, "查看我的登记", R.color.baseTextMain, "", R.color.baseTextMain, 0);
    }

    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        head_image = (ImageView) findViewById(R.id.apply_detail_title_head);
        name_text = (TextView) findViewById(R.id.apply_detail_title_name);
        time_text = (TextView) findViewById(R.id.apply_detail_title_time);
        state_text = (TextView) findViewById(R.id.apply_detail_title_state);
        items_layout = (LinearLayout) findViewById(R.id.apply_detail_items_layout);
        reason_name = (TextView) findViewById(R.id.apply_detail_reason_name);
        reason_text = (TextView) findViewById(R.id.apply_detail_reason_text);
        voice_text = (TextView) findViewById(R.id.apply_detail_voice_image);
        phots_layout = (LinearLayout) findViewById(R.id.apply_detail_photos_layout);
        check_name = (TextView) findViewById(R.id.apply_detail_check_name);
    }

    @Override
    public void initData() {
        intent = getIntent();
        applyType = intent.getIntExtra("ApplyType", 1);

    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (applyType) {
            //请假申请
            case MyConstant.APPLY_DOCUMENT_LEAVE_APPLY:
                setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "", R.color.baseTextMain, "请假详情", R.color.baseTextMain, "", R.color.baseTextMain, 0);
                reason_name.setText("请假是由");
                nameArr = new String[]{"开始时间", "结束时间", "请假天数", "请假类型"};
                requestApplyLeave();
                break;
            //外出登记
            case MyConstant.APPLY_DOCUMENT_OUT_REGISTER:
                setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "", R.color.baseTextMain, "外出详情", R.color.baseTextMain, "", R.color.baseTextMain, 0);
                reason_name.setText("外出是由");
                nameArr = new String[]{"开始时间", "结束时间"};
                requestApplyOutRegister();
                break;
            //出差申请
            case MyConstant.APPLY_DOCUMENT_OUT_APPLY:
                setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "", R.color.baseTextMain, "出差详情", R.color.baseTextMain, "", R.color.baseTextMain, 0);
                reason_name.setText("出差是由");
                nameArr = new String[]{"始发地", "目的地","开始时间", "结束时间","交通工具"};
                requestApplyOut();
                break;
            //加班登记
            case MyConstant.APPLY_DOCUMENT_WORK_REGISTER:
                setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "", R.color.baseTextMain, "加班详情", R.color.baseTextMain, "", R.color.baseTextMain, 0);
                reason_name.setText("加班是由");
                nameArr = new String[]{"开始时间", "结束时间", "加班时长"};
                requestApplyWork();
                break;
        }
    }

    /**
     * 请假
     */
    public void requestApplyLeave() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckWorkServices(context).leaveOrderDetailQueryService(id, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {

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
     * 外出登记
     */
    public void requestApplyOutRegister() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckWorkServices(context).outGoingDetailQueryService(id, new Handler() {
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
     * 出差
     */
    public void requestApplyOut() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckWorkServices(context).bussinessTripFindAllService(id, new Handler() {
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
        new CheckWorkServices(context).workDetailQueryService(id, new Handler() {
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

    public void getItemsData() {
        for (int i = 0; i < nameArr.length; i++) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("name", nameArr[i]);
            itemData.put("value", valueArr[i]);
            items_layout.addView(getItemView(itemData));
        }
    }

    public View getItemView(Map<String, Object> itemData) {
        View item_view = LayoutInflater.from(context).inflate(R.layout.item_view_apply_detail, null);
        TextView name_text = (TextView) item_view.findViewById(R.id.item_view_apply_detail_name);
        TextView value_text = (TextView) item_view.findViewById(R.id.item_view_apply_detail_value);
        if (itemData != null) {
            if (itemData.get("name") != null) {
                name_text.setText(itemData.get("name") + "");
            }
            if (itemData.get("value") != null) {
                value_text.setText(itemData.get("value") + "");
            }
        }
        return item_view;
    }

    public View getPhotoItemView(String imageUrl) {
        View photo_view = LayoutInflater.from(context).inflate(R.layout.item_view_image, null);
        ImageView photo_image = (ImageView) photo_view.findViewById(R.id.item_view_photo_image);
        Glide.with(context).load(imageUrl).into(photo_image);
        return photo_view;
    }
}
