package com.xahaolan.emanage.ui.checkwork.clockrecord;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.adapter.ClockRecordAdapter;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyApplication;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.services.CheckWorkServices;
import com.xahaolan.emanage.utils.common.ToastUtils;
import com.xahaolan.emanage.utils.mine.AppUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/3.      打卡记录
 */

public class ClockRecordActivity extends BaseActivity {
    private static final String TAG = ClockRecordActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;

    private ListView list_view;
    private ClockRecordAdapter adapter;
    private List<Map<String, Object>> dataList;
    private int personId;
    private int page = 1;  //当前页
    private int rows = 20;   //每页显示记录数
    private Boolean hasNextPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_clock_record);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ico_left_gray, "返回", R.color.baseTextMain, "打卡记录", R.color.baseTextMain, "", R.color.baseTextMain, 0);
    }

    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        list_view = (ListView) findViewById(R.id.clock_record_listview);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> data = (Map<String, Object>) parent.getAdapter().getItem(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("clockDetail", (Serializable) data);
                MyUtils.jump(context, ClockDetailActivity.class, bundle, false, null);
            }
        });
        list_view.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        if (hasNextPage) {
                            page++;
                           requestRecordList();
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
        dataList = new ArrayList<>();
        adapter = new ClockRecordAdapter(context);
        list_view.setAdapter(adapter);
        personId = AppUtils.getPersonId(context);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestRecordList();
    }

    public void requestRecordList() {
        if (personId == 0) {
            ToastUtils.showShort(context, "员工id错误");
            return;
        }
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckWorkServices(context).addClockQueryService(personId,page,rows, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    Map<String, Object> response = (Map<String, Object>) msg.obj;
                    if (response != null) {
                        List<Map<String, Object>> footerList = (List<Map<String, Object>>) response.get("footer");
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

    public void regroupList() {
        List<Map<String, Object>> reList = new ArrayList<>();
        for (Map<String, Object> data : dataList) {
            if (data.get("createTime")!= null){
                String createTime = (String) data.get("createTime");

            }
        }
    }
}
