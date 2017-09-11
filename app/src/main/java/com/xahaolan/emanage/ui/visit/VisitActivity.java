package com.xahaolan.emanage.ui.visit;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;

/**
 * Created by helinjie on 2017/9/3.    客户拜访
 */

public class VisitActivity extends BaseActivity {
    private static final String TAG = VisitActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_customer_visit);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0,R.color.titleBg,R.drawable.ico_left_white,"",R.color.baseTextMain,"考勤",R.color.baseTextMain,"",R.color.baseTextMain,0);
    }

    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
    }

    @Override
    public void initData() {

    }
}
