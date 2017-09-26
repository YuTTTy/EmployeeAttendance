package com.xahaolan.emanage.ui.checkwork.check;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.adapter.CheckApplyAdapter;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.dialog.DialogSingleText;
import com.xahaolan.emanage.http.services.CheckServices;
import com.xahaolan.emanage.http.services.CheckWorkServices;
import com.xahaolan.emanage.utils.common.ToastUtils;
import com.xahaolan.emanage.utils.mine.AppUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/3.  查看我的申请，审批
 */

public class CheckApplyActivity extends BaseActivity {
    private static final String TAG = CheckApplyActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    private Intent intent;

    private LinearLayout examine_layout;
    private TextView wait_text;
    private TextView already_text;
    private LinearLayout slt_layout;
    private TextView slt_text;
    private ListView list_view;
    private CheckApplyAdapter adapter;
    private List<Map<String, Object>> dataList;
    private String[] strArr = {"全部", "请假", "外出", "出差", "加班"};

    private int checkType;//1.查看我的申请 2.查看我的审批
    private int personid; //申请人id
    private int page = 1;  //当前页
    private int rows = 20;   //每页显示记录数
    private Boolean hasNextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_check_apply);
    }

    @Override
    public void setTitleAttribute() {

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
                    adapter = new CheckApplyAdapter(context);
                    list_view.setAdapter(adapter);
                    if (checkType == MyConstant.CHECK_MINE_APPLY) {
                        requestApply();
                    } else {
                        requestExamine();
                    }
                }
            }
        });
        examine_layout = (LinearLayout) findViewById(R.id.check_apply_examine_layout);
        wait_text = (TextView) findViewById(R.id.check_apply_wait_text);
        wait_text.setOnClickListener(this);
        already_text = (TextView) findViewById(R.id.check_apply_already_text);
        already_text.setOnClickListener(this);
        slt_layout = (LinearLayout) findViewById(R.id.check_apply_slt_layout);
        slt_text = (TextView) findViewById(R.id.check_apply_slt_text);
        slt_layout.setOnClickListener(this);
        list_view = (ListView) findViewById(R.id.check_apply_list_view);
        list_view.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        if (hasNextPage) {
                            page++;
                            if (checkType == MyConstant.CHECK_MINE_APPLY) {
                                requestApply();
                            } else if (checkType == MyConstant.CHECK_MINE_EXAMINE) {
                                requestExamine();
                            }
                        } else if (list_view.getFooterViewsCount() <= 0) {
                            ToastUtils.showShort(context, "没有更多了");
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
        personid = AppUtils.getPersonId(context);
        dataList = new ArrayList<>();
        intent = getIntent();
        checkType = intent.getIntExtra("CheckType", 1);
        adapter = new CheckApplyAdapter(context);
        list_view.setAdapter(adapter);
        if (checkType == MyConstant.CHECK_MINE_APPLY) {
            setTitle(0, R.color.titleBg, R.drawable.ico_left_gray, "返回", R.color.baseTextMain, "查看我的申请", R.color.baseTextMain, "", R.color.baseTextMain, 0);
            examine_layout.setVisibility(View.GONE);
        } else if (checkType == MyConstant.CHECK_MINE_EXAMINE) {
            setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "返回", R.color.baseTextMain, "查看我的审批", R.color.baseTextMain, "", R.color.baseTextMain, 0);
            examine_layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            /* 待审批 */
            case R.id.check_apply_wait_text:

                break;
            /* 已审批 */
            case R.id.check_apply_already_text:

                break;
            /* 查看类型 */
            case R.id.check_apply_slt_layout:
                new DialogSingleText(context, strArr, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (msg.what == MyConstant.HANDLER_SUCCESS) {
                            int position = (int) msg.obj;
                            page = 1;
                            switch (position) {
                                case 0: //全部
                                    requestApply();
                                    break;
                                case 1: //请假
                                    requestLeave();
                                    break;
                                case 2: //外出
                                    requestOut();
                                    break;
                                case 3: //出差
                                    reqeustOutWork();
                                    break;
                                case 4: //加班
                                    requestWork();
                                    break;
                            }
                        }
                    }
                }).show();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkType == MyConstant.CHECK_MINE_APPLY) {
            requestApply();
        } else if (checkType == MyConstant.CHECK_MINE_EXAMINE) {
            requestExamine();
        }
    }

    /**
     * 我的申请列表
     */
    public void requestApply() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckServices(context).myApplyService(personid, page, rows, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    Map<String, Object> dataResponse = (Map<String, Object>) msg.obj;
                    if (dataResponse != null) {
                        if (dataResponse.get("resultList") != null) {
                            dataList = (List<Map<String, Object>>) dataResponse.get("resultList");
//                            if (lastPage == 0 || currentPage == lastPage) {
//                                hasNextPage = false;
//                            } else {
//                                hasNextPage = true;
//                            }
                            if (dataList != null && dataList.size() > 0) {
//                            if (page == 1) {
//                                activityAdapter.resetList(activityList);
//                            } else {
//                                activityAdapter.appendList(activityList);
//                            }
                                adapter.notifyDataSetChanged();
                            }
                        }
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
     * 我的请假列表
     */
    public void requestLeave() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckWorkServices(context).leaveOrderQueryService(personid, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    dataList = (List<Map<String, Object>>) msg.obj;
                    if (dataList != null && dataList.size() >= 0) {
//                            if (lastPage == 0 || currentPage == lastPage) {
//                                hasNextPage = false;
//                            } else {
//                                hasNextPage = true;
//                            }
                        if (dataList != null && dataList.size() > 0) {
//                            if (page == 1) {
//                                activityAdapter.resetList(activityList);
//                            } else {
//                                activityAdapter.appendList(activityList);
//                            }
                            adapter.notifyDataSetChanged();
                        }
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
     * 外出列表
     */
    public void requestOut() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckWorkServices(context).outGoingQueryService(personid, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    dataList = (List<Map<String, Object>>) msg.obj;
                    if (dataList != null && dataList.size() >= 0) {
//                            if (lastPage == 0 || currentPage == lastPage) {
//                                hasNextPage = false;
//                            } else {
//                                hasNextPage = true;
//                            }
                        if (dataList != null && dataList.size() > 0) {
//                            if (page == 1) {
//                                activityAdapter.resetList(activityList);
//                            } else {
//                                activityAdapter.appendList(activityList);
//                            }
                            adapter.notifyDataSetChanged();
                        }
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
     * 出差列表
     */
    public void reqeustOutWork() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckWorkServices(context).bussinessTripFindAllService(personid, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    dataList = (List<Map<String, Object>>) msg.obj;
                    if (dataList != null && dataList.size() >= 0) {
//                            if (lastPage == 0 || currentPage == lastPage) {
//                                hasNextPage = false;
//                            } else {
//                                hasNextPage = true;
//                            }
                        if (dataList != null && dataList.size() > 0) {
//                            if (page == 1) {
//                                activityAdapter.resetList(activityList);
//                            } else {
//                                activityAdapter.appendList(activityList);
//                            }
                            adapter.notifyDataSetChanged();
                        }
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
     * 加班列表
     */
    public void requestWork() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckWorkServices(context).workQueryService(personid, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    dataList = (List<Map<String, Object>>) msg.obj;
                    if (dataList != null && dataList.size() >= 0) {
//                            if (lastPage == 0 || currentPage == lastPage) {
//                                hasNextPage = false;
//                            } else {
//                                hasNextPage = true;
//                            }
                        if (dataList != null && dataList.size() > 0) {
//                            if (page == 1) {
//                                activityAdapter.resetList(activityList);
//                            } else {
//                                activityAdapter.appendList(activityList);
//                            }
                            adapter.notifyDataSetChanged();
                        }
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
     * 我的审批列表
     */
    public void requestExamine() {

    }
}
