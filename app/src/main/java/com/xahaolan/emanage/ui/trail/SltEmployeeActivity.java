package com.xahaolan.emanage.ui.trail;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.adapter.CheckApplyAdapter;
import com.xahaolan.emanage.adapter.EmployeeAdapter;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.services.TrailServices;
import com.xahaolan.emanage.utils.common.StringUtils;
import com.xahaolan.emanage.utils.common.ToastUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;
import com.xahaolan.emanage.view.contacts.SideBar;

import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/10.
 */

public class SltEmployeeActivity extends BaseActivity {
    private static final String TAG = SltEmployeeActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;

    private SideBar side_bar;
    private TextView dialog_text;
    private ListView list_view;
    private EmployeeAdapter adapter;
    private List<Map<String, Object>> dataList;
    private int personId;
    private int personid; //申请人id
    private int page = 1;  //当前页
    private int rows = 20;   //每页显示记录数
    private Boolean hasNextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_trail_slt_employee);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "", R.color.baseTextMain, "选择员工", R.color.baseTextMain, "", R.color.textRed, 0);
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
                    requestEmployeeList();
                }
            }
        });
        side_bar = (SideBar) findViewById(R.id.trail_list_employee_sidebar);
        dialog_text = (TextView) findViewById(R.id.trail_list_employee_dialog);
        list_view = (ListView) findViewById(R.id.trail_list_employee_listview);
        list_view.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        if (hasNextPage) {
                            page++;
                            requestEmployeeList();
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
                Map<String, Object> employeeData = (Map<String, Object>) parent.getAdapter().getItem(position);
                if (employeeData.get("personId") != null) {
                    personId = (int) employeeData.get("personId");
                }
                Bundle bundle = new Bundle();
                bundle.putInt("personId", personId);
                MyUtils.jump(context, WorkTrailActivity.class, bundle, false, null);
                finish();
            }
        });
        side_bar.setTextView(dialog_text);
        /*设置字母导航触摸监听*/
        side_bar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // TODO Auto-generated method stub
                // 该字母首次出现的位置
                final int position = adapter.getPositionForSelection(s.charAt(0));
                if (position != -1) {
                    list_view.setSelection(position);
                }
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter = new EmployeeAdapter(context);
        list_view.setAdapter(adapter);
        requestEmployeeList();
    }

    /**
     * 员工列表
     */
    public void requestEmployeeList() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new TrailServices(context).employeeListService(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    dataList = (List<Map<String, Object>>) msg.obj;
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
