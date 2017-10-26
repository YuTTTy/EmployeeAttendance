package com.xahaolan.emanage.ui.task;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.adapter.TabDailyAdapter;
import com.xahaolan.emanage.adapter.TaskAdapter;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.services.CheckWorkServices;
import com.xahaolan.emanage.http.services.TaskService;
import com.xahaolan.emanage.utils.common.ToastUtils;
import com.xahaolan.emanage.utils.mine.AppUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/3.   任务
 */

public class TaskActivity extends BaseActivity {
    private static final String TAG = TaskActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    private int state = 1;

    private LinearLayout title_layout;
    private TextView title_text;
    private TextView change_text;

    private ListView list_view;
    private TaskAdapter adapter;
    private int createId = 0;  //发布人id
    private int executorId = 0;//执行人id
    private List<Map<String, Object>> dataList;
    private int page = 1;  //当前页
    private int rows = 20;   //每页显示记录数
    private Boolean hasNextPage = false;
    private View foot;//页脚

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_task);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "", R.color.baseTextMain, "任务", R.color.baseTextMain, "", R.color.baseTextMain, R.drawable.add);
    }

    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        /*下拉刷新*/
        BaseActivity.setSwipRefresh(swipeLayout, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MyConstant.HANDLER_REFRESH_SUCCESS) {
                    page = 1;
                    adapter = new TaskAdapter(context);
                    list_view.setAdapter(adapter);
                    requestTaskList();
                }
            }
        });
        title_layout = (LinearLayout) findViewById(R.id.task_list_title_layout);
        title_text = (TextView) findViewById(R.id.task_list_title_name);
        change_text = (TextView) findViewById(R.id.task_list_change_name);
        list_view = (ListView) findViewById(R.id.task_list_listview);
        setClick();

    }

    public void setClick() {
        right_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtils.jump(context, CreateTaskActivity.class, new Bundle(), false, null);
            }
        });
        title_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == 1) {
                    title_text.setText("收到的任务");
                    change_text.setText("已发布的任务");
                    createId = AppUtils.getPersonId(context);
                    executorId = 0;
                    state = 2;
                } else if (state == 2) {
                    title_text.setText("已发布的任务");
                    change_text.setText("收到的任务");
                    executorId = AppUtils.getPersonId(context);
                    createId = 0;
                    state = 1;
                }
                requestTaskList();
            }
        });
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> data = (Map<String, Object>) parent.getAdapter().getItem(position);
                if (data != null) {
                    if (data.get("id")!= null){
                        int taskId = new Double((Double)data.get("id")).intValue();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("taskId",taskId);
                        if (data.get("state") != null) {
                            int state = new Double((Double) data.get("state")).intValue();
                            bundle.putInt("state",state);
                        }
                        MyUtils.jump(context, TaskDetailActivity.class, bundle, false, null);
                    }
                }
            }
        });
        foot = LayoutInflater.from(context).inflate(R.layout.new_fresh_item, null);
        list_view.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        if (hasNextPage) {
                            page++;
                            requestTaskList();
                        } else if (list_view.getFooterViewsCount() <= 0) {
                            list_view.addFooterView(foot);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    public void initData() {
        adapter = new TaskAdapter(context);
        list_view.setAdapter(adapter);
        title_text.setText("已发布的任务");
        change_text.setText("收到的任务");
        executorId = AppUtils.getPersonId(context);
        state = 1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestTaskList();
    }

    public void requestTaskList() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new TaskService(context).addTaskQueryService(createId, executorId,page,rows, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    Map<String, Object> response = (Map<String, Object>) msg.obj;
                    if (response != null) {
                        if (response.get("rows") != null) {
                            dataList = (List<Map<String, Object>>) response.get("rows");
                            if (dataList != null && dataList.size() >= 0) {
                                if (response.get("total") != null){
                                    int total = new Double((Double)response.get("total")).intValue();
                                    if (total >= 20){
                                        hasNextPage = true;
                                    }else {
                                        hasNextPage=false;
                                    }
                                    if (page == 1){
                                        adapter.resetList(dataList);
                                    }else {
                                        adapter.appendList(dataList);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else if (msg.what == MyConstant.REQUEST_FIELD) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                    if (errMsg.equals("session过期")){
                        BaseActivity.loginOut(context);
                    }
                } else if (msg.what == MyConstant.REQUEST_ERROR) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                }
            }
        });
    }
}
