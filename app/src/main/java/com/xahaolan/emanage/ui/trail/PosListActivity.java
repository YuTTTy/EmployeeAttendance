package com.xahaolan.emanage.ui.trail;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.AbsListView;
import android.widget.ListView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.adapter.PosListAdapter;
import com.xahaolan.emanage.adapter.TaskAdapter;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.services.CheckServices;
import com.xahaolan.emanage.utils.common.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/10.        实时位置
 */

public class PosListActivity extends BaseActivity {
    private static final String TAG = PosListActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    private Intent intent;

    private ListView list_view;
    private PosListAdapter adapter;
    private List<Map<String,Object>> locList;
    private int page = 1;  //当前页
    private int rows = 20;   //每页显示记录数
    private Boolean hasNextPage = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_trail_pos_list);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "", R.color.baseTextMain, "实时位置", R.color.baseTextMain, "", R.color.textRed, R.drawable.search);
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
                    adapter = new PosListAdapter(context);
                    list_view.setAdapter(adapter);
                    requestPosList();
                }
            }
        });
        list_view = (ListView) findViewById(R.id.trail_pos_listview);
        list_view.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        if (hasNextPage) {
                            page++;
                            requestPosList();
                        } else if (list_view.getFooterViewsCount() <= 0) {
                            ToastUtils.showShort(context,"没有更多了");
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
        intent = getIntent();
        locList = (List<Map<String, Object>>) intent.getSerializableExtra("LogList");
        adapter = new PosListAdapter(context);
        list_view.setAdapter(adapter);
        if (locList != null && locList.size() > 0){
            adapter.resetList(locList);
            adapter.notifyDataSetChanged();
        }
    }

    public void requestPosList() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
//        new CheckServices(context).myApplyService(personid, page, rows, new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
//                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
//                }
//                if (msg.what == MyConstant.REQUEST_SUCCESS) {
//                    Map<String, Object> dataResponse = (Map<String, Object>) msg.obj;
//                    if (dataResponse != null) {
//                        if (dataResponse.get("resultList") != null) {
//                            locList = (List<Map<String, Object>>) dataResponse.get("resultList");
////                            if (lastPage == 0 || currentPage == lastPage) {
////                                hasNextPage = false;
////                            } else {
////                                hasNextPage = true;
////                            }
//                            if (locList != null && locList.size() > 0) {
////                            if (page == 1) {
////                                activityAdapter.resetList(activityList);
////                            } else {
////                                activityAdapter.appendList(activityList);
////                            }
//                                adapter.notifyDataSetChanged();
//                            }
//                        }
//                    }
//                } else if (msg.what == MyConstant.REQUEST_FIELD) {
//                    String errMsg = (String) msg.obj;
//                    ToastUtils.showShort(context, errMsg);
//                } else if (msg.what == MyConstant.REQUEST_ERROR) {
//                    String errMsg = (String) msg.obj;
//                    ToastUtils.showShort(context, errMsg);
//                }
//            }
//        });
    }
}
