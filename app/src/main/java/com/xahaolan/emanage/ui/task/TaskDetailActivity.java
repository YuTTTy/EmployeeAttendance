package com.xahaolan.emanage.ui.task;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.services.TaskService;
import com.xahaolan.emanage.utils.common.ToastUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by               task detail
 */

public class TaskDetailActivity extends BaseActivity {
    private static final String TAG = TaskDetailActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    private Intent intent;

    private TextView send_text;
    private TextView execute_text;
    private TextView time_text;
    private TextView content_text;
    private LinearLayout items_layout;
    private TextView btn_text;

    private int taskId;
    private Map<String,Object> detailData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_task_detail);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "", R.color.baseTextMain, "任务详情", R.color.baseTextMain, "", R.color.baseTextMain, 0);
    }

    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        send_text = (TextView) findViewById(R.id.task_detail_send_name);
        execute_text = (TextView) findViewById(R.id.task_detail_execute_name);
        time_text = (TextView) findViewById(R.id.task_detail_time_name);
        content_text = (TextView) findViewById(R.id.task_detail_content);
        items_layout = (LinearLayout) findViewById(R.id.task_detail_item_layout);
        btn_text = (TextView) findViewById(R.id.task_detail_btn);

        setClick();

    }

    public void setClick() {
        btn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestFinishTask();
            }
        });
    }

    @Override
    public void initData() {
        intent = getIntent();
        taskId = intent.getIntExtra("taskId",0);

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestTaskDetail();
    }

    /**
     *      task detail
     */
    public void requestTaskDetail() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new TaskService(context).taskDetailService(taskId, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    detailData = (Map<String, Object>) msg.obj;
                    if (detailData != null){
                        setViewData();
                    }

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
     *       finish task
     */
    public void requestFinishTask() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new TaskService(context).finishTaskService(taskId, new Handler() {
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
    public void setViewData(){
        if (detailData.get("createName") != null){
            send_text.setText(detailData.get("createName")+"");
        }
        if (detailData.get("executorName") != null){
            execute_text.setText(detailData.get("executorName")+"");
        }
        if (detailData.get("endDate") != null){
            time_text.setText(detailData.get("endDate")+"");
        }
        if (detailData.get("content") != null){
            content_text.setText(detailData.get("content")+"");
        }
    }
}
