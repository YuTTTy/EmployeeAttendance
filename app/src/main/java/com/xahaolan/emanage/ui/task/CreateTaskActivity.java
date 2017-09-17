package com.xahaolan.emanage.ui.task;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.services.CheckWorkServices;
import com.xahaolan.emanage.http.services.TaskService;
import com.xahaolan.emanage.utils.common.ToastUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/9.
 */

public class CreateTaskActivity extends BaseActivity {
    private static final String TAG = CreateTaskActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;

    private EditText content_et;
    private LinearLayout execute_layout;
    private TextView execute_text;
    private LinearLayout time_layout;
    private TextView time_text;
    private ImageView photo_icon;
    private LinearLayout photos_layout;
    private TextView btn_text;

    private int createId;//   发布人id
     private String createName;//  发布人姓名
     private int executorId;//  执行人id
     private String content;//     任务内容
     private String endDate;//     截止日期
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_create_task);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "", R.color.baseTextMain, "创建任务", R.color.baseTextMain, "", R.color.baseTextMain, 0);
    }

    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        content_et = (EditText) findViewById(R.id.create_task_content);
        execute_layout = (LinearLayout) findViewById(R.id.create_task_execute_layout);
        execute_text = (TextView) findViewById(R.id.create_task_execute_name);
        time_layout = (LinearLayout) findViewById(R.id.create_task_time_layout);
        time_text = (TextView) findViewById(R.id.create_task_time_text);
        photo_icon = (ImageView) findViewById(R.id.create_task_photo);
        photos_layout = (LinearLayout) findViewById(R.id.create_task_photos_layout);
        btn_text = (TextView) findViewById(R.id.create_task_btn);
        setClick();

    }

    public void setClick() {
        execute_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        photo_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCreateTask();
            }
        });
    }

    @Override
    public void initData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestCreateTask();
    }

    public void requestCreateTask() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new TaskService(context).addTaskAddService( createId, createName, executorId,
         content, endDate, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    String response = (String) msg.obj;
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
}
