package com.xahaolan.emanage.ui.task;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;

/**
 * Created by helinjie on 2017/9/9.
 */

public class TaskDetailActivity extends BaseActivity {
    private static final String TAG = TaskDetailActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;

    private TextView send_text;
    private TextView execute_text;
    private TextView time_text;
    private TextView content_text;
    private LinearLayout items_layout;
    private TextView btn_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_task_detail);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.icon_return_black, "", R.color.baseTextMain, "任务详情", R.color.baseTextMain, "", R.color.baseTextMain, 0);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestTaskDetail();
    }

    public void requestTaskDetail() {

    }
    public void requestFinishTask() {

    }
}
