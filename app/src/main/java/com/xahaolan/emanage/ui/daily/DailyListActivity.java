package com.xahaolan.emanage.ui.daily;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.adapter.FragmentPagerDailyAdapter;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.utils.mine.MyUtils;
import com.xahaolan.emanage.view.MyView.DialyTabView;

/**
 * Created by helinjie on 2017/9/3.     日报
 */

public class DailyListActivity extends BaseActivity {
    private static final String TAG = DailyListActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;

    private DialyTabView tab_view;
    private ViewPager view_pager;
    private FragmentPagerDailyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_daily);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "", R.color.baseTextMain, "日报", R.color.baseTextMain, "", R.color.baseTextMain, R.drawable.ic_launcher_round);
    }

    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        tab_view = (DialyTabView) findViewById(R.id.daily_list_tab);
        view_pager = (ViewPager) findViewById(R.id.daily_list_viewpager);
        tab_view.setOncheckedChangeListener(new DialyTabView.onCheckedChangeListener() {
            @Override
            public void onCheckedChange(int position) {
                view_pager.setCurrentItem(position);
            }
        });
        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tab_view.setChecked(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        right_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtils.jump(context,InitiateDailyActivity.class,new Bundle(),false,null);
            }
        });
    }

    @Override
    public void initData() {
        FragmentManager manager = getSupportFragmentManager();
        view_pager.setOffscreenPageLimit(0);//预加载页面，0为不预加载
        adapter = new FragmentPagerDailyAdapter(manager, context);
        view_pager.setAdapter(adapter);

        tab_view.setChecked(0);
        view_pager.setCurrentItem(0);
    }
}
