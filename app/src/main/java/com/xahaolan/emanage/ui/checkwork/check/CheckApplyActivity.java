package com.xahaolan.emanage.ui.checkwork.check;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.xahaolan.emanage.ui.checkwork.apply.DocumentDetailActivity;
import com.xahaolan.emanage.utils.common.ToastUtils;
import com.xahaolan.emanage.utils.mine.AppUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;

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
    private int type = -1; //-1.全部 ，0.(请假单)，1（外出登记），2（出差申请），3（加班）
    private int examineType = 1; //1.待审批  2.已审批
    private int personid; //申请人id
    private int page = 1;  //当前页
    private int rows = 20;   //每页显示记录数
    private Boolean hasNextPage = false;

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
                        if (examineType == 1) {
                            requestExamine();
                        } else {
                            requestAlExamine();
                        }
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
                                if (examineType == 1) {
                                    requestExamine();
                                } else {
                                    requestAlExamine();
                                }
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
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,Object> data = (Map<String, Object>) parent.getAdapter().getItem(position);
                if (data == null){
                    return;
                }
                if (data.get("type") == null){
                    return;
                }
                if (data.get("id") == null){
                    return;
                }
                int personId = new Double((Double)data.get("id")).intValue();
                int type = new Double((Double)data.get("type")).intValue();
                Bundle bundle = new Bundle();
                bundle.putInt("id",personId);
                bundle.putInt("ApplyType",type);
                MyUtils.jump(context, DocumentDetailActivity.class,bundle,false,null);
            }
        });
    }

    @Override
    public void initData() {
//        personid = 49;
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
                examineType = 1;
                requestExamine();
                break;
            /* 已审批 */
            case R.id.check_apply_already_text:
                examineType = 2;
                requestAlExamine();
                break;
            /* 查看类型 */
            case R.id.check_apply_slt_layout:
                new DialogSingleText(context, strArr, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (msg.what == MyConstant.HANDLER_SUCCESS) {
                            type = (int) msg.obj - 1;
                            if (type == -1) {
                                slt_text.setText("全部");
                            } else if (type == 0) {
                                slt_text.setText("请假");
                            } else if (type == 1) {
                                slt_text.setText("外出");
                            } else if (type == 2) {
                                slt_text.setText("出差");
                            } else if (type == 3) {
                                slt_text.setText("加班");
                            }

                            page = 1;
                            if (checkType == MyConstant.CHECK_MINE_APPLY) {
                                requestApply();
                            } else if (checkType == MyConstant.CHECK_MINE_EXAMINE) {
                                if (examineType == 1) {
                                    requestExamine();
                                } else {
                                    requestAlExamine();
                                }
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
            if (examineType == 1) {
                requestExamine();
            } else {
                requestAlExamine();
            }
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
                                        adapter.resetList(regroupList());
                                    }else {
                                        adapter.appendList(regroupList());
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else if (msg.what == MyConstant.REQUEST_FIELD) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                    if (errMsg.equals("session过期")) {
                        BaseActivity.loginOut(context);
                    }
                } else if (msg.what == MyConstant.REQUEST_ERROR) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                }
            }
        });
    }

    /**
     * 我的待审批列表
     */
    public void requestExamine() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckServices(context).examineService(personid, page, rows, new Handler() {
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
                                        adapter.resetList(regroupList());
                                    }else {
                                        adapter.appendList(regroupList());
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else if (msg.what == MyConstant.REQUEST_FIELD) {
                    String errMsg = (String) msg.obj;
                    if (errMsg.equals("session过期")) {
                        ToastUtils.showShort(context, errMsg);
                        BaseActivity.loginOut(context);
                    } else if (errMsg.equals("")) {
                        dataList.clear();
                        adapter.resetList(dataList);
                        adapter.notifyDataSetChanged();
                    }
                } else if (msg.what == MyConstant.REQUEST_ERROR) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                }
            }
        });
    }

    /**
     * 我的已审批列表
     */
    public void requestAlExamine() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckServices(context).alExamineService(personid, page, rows, new Handler() {
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
                                        adapter.resetList(regroupList());
                                    }else {
                                        adapter.resetList(regroupList());
                                    }
                                    if (page == 1){
                                        adapter.resetList(regroupList());
                                    }else {
                                        adapter.appendList(regroupList());
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else if (msg.what == MyConstant.REQUEST_FIELD) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                    if (errMsg.equals("session过期")) {
                        BaseActivity.loginOut(context);
                    }
                } else if (msg.what == MyConstant.REQUEST_ERROR) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                }
            }
        });
    }

    /**
     * 区分全部、请假、外出、出差、加班
     *
     * @return
     */
    public List<Map<String, Object>> regroupList() {
        List<Map<String, Object>> regroupList = new ArrayList<>();
        for (Map<String, Object> data : dataList) {
            if (data.get("type") != null) {
                int reType = new Double((Double) data.get("type")).intValue();
                if (type == -1) {
                    regroupList.add(data);
                } else if (type == reType) {
                    regroupList.add(data);
                }
            }
        }
        return regroupList;
    }
//    /**
//     * 请假列表
//     */
//    public void requestLeave() {
//        if (swipeLayout != null) {
//            swipeLayout.setRefreshing(true);
//        }
//        new CheckWorkServices(context).leaveOrderQueryService(personid, page, rows, new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
//                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
//                }
//                if (msg.what == MyConstant.REQUEST_SUCCESS) {
//                    Map<String, Object> response = (Map<String, Object>) msg.obj;
//                    if (response != null) {
//                        dataList = (List<Map<String, Object>>) response.get("rows");
//                        if (dataList != null && dataList.size() >= 0) {
//                            adapter.resetList(dataList);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                } else if (msg.what == MyConstant.REQUEST_FIELD) {
//                    String errMsg = (String) msg.obj;
//                    ToastUtils.showShort(context, errMsg);
//                    if (errMsg.equals("session过期")) {
//                        BaseActivity.loginOut(context);
//                    }
//                } else if (msg.what == MyConstant.REQUEST_ERROR) {
//                    String errMsg = (String) msg.obj;
//                    ToastUtils.showShort(context, errMsg);
//                }
//            }
//        });
//    }
//
//    /**
//     * 外出列表
//     */
//    public void requestOut() {
//        if (swipeLayout != null) {
//            swipeLayout.setRefreshing(true);
//        }
//        new CheckWorkServices(context).outGoingQueryService(personid, page, rows, new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
//                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
//                }
//                if (msg.what == MyConstant.REQUEST_SUCCESS) {
//                    dataList = (List<Map<String, Object>>) msg.obj;
//                    Map<String, Object> response = (Map<String, Object>) msg.obj;
//                    if (response != null) {
//                        dataList = (List<Map<String, Object>>) response.get("rows");
//                        if (dataList != null && dataList.size() >= 0) {
//                            adapter.resetList(dataList);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                } else if (msg.what == MyConstant.REQUEST_FIELD) {
//                    String errMsg = (String) msg.obj;
//                    ToastUtils.showShort(context, errMsg);
//                    if (errMsg.equals("session过期")) {
//                        BaseActivity.loginOut(context);
//                    }
//                } else if (msg.what == MyConstant.REQUEST_ERROR) {
//                    String errMsg = (String) msg.obj;
//                    ToastUtils.showShort(context, errMsg);
//                }
//            }
//        });
//    }
//
//    /**
//     * 出差列表
//     */
//    public void reqeustOutWork() {
//        if (swipeLayout != null) {
//            swipeLayout.setRefreshing(true);
//        }
//        new CheckWorkServices(context).bussinessTripFindAllService(personid, page, rows, new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
//                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
//                }
//                if (msg.what == MyConstant.REQUEST_SUCCESS) {
//                    Map<String, Object> response = (Map<String, Object>) msg.obj;
//                    if (response != null) {
//                        dataList = (List<Map<String, Object>>) response.get("rows");
//                        if (dataList != null && dataList.size() >= 0) {
//                            adapter.resetList(dataList);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                } else if (msg.what == MyConstant.REQUEST_FIELD) {
//                    String errMsg = (String) msg.obj;
//                    ToastUtils.showShort(context, errMsg);
//                    if (errMsg.equals("session过期")) {
//                        BaseActivity.loginOut(context);
//                    }
//                } else if (msg.what == MyConstant.REQUEST_ERROR) {
//                    String errMsg = (String) msg.obj;
//                    ToastUtils.showShort(context, errMsg);
//                }
//            }
//        });
//    }
//
//    /**
//     * 加班列表
//     */
//    public void requestWork() {
//        if (swipeLayout != null) {
//            swipeLayout.setRefreshing(true);
//        }
//        new CheckWorkServices(context).workQueryService(personid, page, rows, new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
//                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
//                }
//                if (msg.what == MyConstant.REQUEST_SUCCESS) {
//                    Map<String, Object> response = (Map<String, Object>) msg.obj;
//                    if (response != null) {
//                        dataList = (List<Map<String, Object>>) response.get("rows");
//                        if (dataList != null && dataList.size() >= 0) {
//                            adapter.resetList(dataList);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                } else if (msg.what == MyConstant.REQUEST_FIELD) {
//                    String errMsg = (String) msg.obj;
//                    ToastUtils.showShort(context, errMsg);
//                    if (errMsg.equals("session过期")) {
//                        BaseActivity.loginOut(context);
//                    }
//                } else if (msg.what == MyConstant.REQUEST_ERROR) {
//                    String errMsg = (String) msg.obj;
//                    ToastUtils.showShort(context, errMsg);
//                }
//            }
//        });
//    }

}
