package com.xahaolan.emanage.ui.daily;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;

/**
 * Created by helinjie on 2017/9/5.   发起日报
 */

public class InitiateDailyActivity extends BaseActivity {
    private static final String TAG = InitiateDailyActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;

    private EditText today_et;
    private EditText tomorrow_et;
    private EditText summarize_et;
    private ImageView photo_image;
    private LinearLayout photos_layout;
    private TextView btn_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_initiate_daily);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ic_launcher_round, "", R.color.baseTextMain, "发起日报", R.color.baseTextMain, "", R.color.baseTextMain, 0);
    }

    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        today_et = (EditText) findViewById(R.id.initiate_daily_today_et);
        tomorrow_et = (EditText) findViewById(R.id.initiate_daily_tomorrow_et);
        summarize_et = (EditText) findViewById(R.id.initiate_daily_summarize_et);
        photo_image = (ImageView) findViewById(R.id.initiate_daily_photos_icon);
        photo_image.setOnClickListener(this);
        photos_layout = (LinearLayout) findViewById(R.id.initiate_daily_photos_items_layout);
        btn_text = (TextView) findViewById(R.id.initiate_daily_btn);
        btn_text.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.initiate_daily_photos_icon:

                break;
            case R.id.initiate_daily_btn:
                requestSubmit();
                break;
        }
    }

    @Override
    public void initData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void requestSubmit() {

    }
}
