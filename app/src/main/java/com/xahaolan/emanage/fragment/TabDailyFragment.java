package com.xahaolan.emanage.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.adapter.TabDailyAdapter;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.ui.daily.InitiateDailyActivity;
import com.xahaolan.emanage.utils.common.LogUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/4.
 */

public class TabDailyFragment extends BaseFragment {
    private static final String TAG = TabDailyFragment.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    private ListView list_view;
    private ImageView null_view;
    private TabDailyAdapter adapter;

    private boolean isPrepared; //标志位，标志已经初始化完成
    private View rootView;
    private int stage; //活动状态 0.全部  1.邀请中  2.判定中 3.履约中 4.完成
    private List activityList;
    private int page = 1;
    private Boolean hasNextPage = false;
    private View foot;//页脚

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_daily_list, null);
            initView(rootView);
            stage = getArguments().getInt("ProgressType");
            LogUtils.e(TAG, "当前进程Progress ：" + stage);
            isPrepared = true;
            lazyLoad();
        }

        //因为共用一个Fragment视图，所以当前这个视图已被加载到Activity中，必须先清除后再加入Activity
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    public void initView(View contentView) {
        swipeLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.swipe_refresh_layout);
        /*下拉刷新*/
        BaseActivity.setSwipRefresh(swipeLayout, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MyConstant.HANDLER_REFRESH_SUCCESS) {
                    page = 1;
                    adapter = new TabDailyAdapter(getActivity());
                    list_view.setAdapter(adapter);
                    requestDailyList();
                }
            }
        });
        list_view = (ListView) contentView.findViewById(R.id.fragment_daily_list_listview);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> itemData = (Map<String, Object>) parent.getAdapter().getItem(position);
                if (itemData != null) {
                    MyUtils.jump(getActivity(), InitiateDailyActivity.class, new Bundle(), false, null);
                }
            }
        });
        foot = LayoutInflater.from(getActivity()).inflate(R.layout.new_fresh_item, null);
        list_view.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        if (hasNextPage) {
                            page++;
                            requestDailyList();
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

    public void initData() {
        adapter = new TabDailyAdapter(getActivity());
        list_view.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        requestDailyList();
    }

    /**
     * 活动列表
     */
    public void requestDailyList() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }

    }
}
