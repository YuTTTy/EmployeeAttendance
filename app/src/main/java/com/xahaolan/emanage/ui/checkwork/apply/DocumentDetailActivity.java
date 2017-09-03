package com.xahaolan.emanage.ui.checkwork.apply;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;

/**
 * Created by helinjie on 2017/9/3.   申请单详情
 */

public class DocumentDetailActivity   extends BaseActivity {
    private static final String TAG = DocumentDetailActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_appley_document_detail);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0,R.color.titleBg,R.drawable.ic_launcher_round,"返回",R.color.baseTextMain,"查看我的登记",R.color.baseTextMain,"",R.color.baseTextMain,0);
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
