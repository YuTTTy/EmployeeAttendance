package com.xahaolan.emanage.ui.task;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.adapter.TaskAdapter;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.utils.mine.MyUtils;

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
    private List<Map<String, Object>> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_task);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.icon_return_black, "", R.color.baseTextMain, "任务", R.color.baseTextMain, "", R.color.baseTextMain, R.drawable.ic_launcher_round);
    }

    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        title_layout = (LinearLayout) findViewById(R.id.task_list_title_layout);
        title_text = (TextView) findViewById(R.id.task_list_title_name);
        change_text = (TextView) findViewById(R.id.task_list_change_name);
        list_view = (ListView) findViewById(R.id.task_list_listview);
        setClick();

    }
    public void setClick(){
        right_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtils.jump(context,CreateTaskActivity.class,new Bundle(),false,null);
            }
        });
        title_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == 1){
                    title_text.setText("收到的任务");
                    change_text.setText("已发布的任务");
                    state = 2;
                }else if (state == 2){
                    title_text.setText("已发布的任务");
                    change_text.setText("收到的任务");
                    state = 1;
                }
            }
        });
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyUtils.jump(context,TaskDetailActivity.class,new Bundle(),false,null);
            }
        });
    }

    @Override
    public void initData() {
        listData = new ArrayList<>();
        adapter = new TaskAdapter(context);
        list_view.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestTaskList();
    }

    public void requestTaskList() {

    }
}
