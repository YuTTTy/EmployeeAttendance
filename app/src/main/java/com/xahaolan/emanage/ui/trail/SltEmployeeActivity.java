package com.xahaolan.emanage.ui.trail;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.adapter.EmployeeAdapter;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.view.contacts.SideBar;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_trail_slt_employee);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ic_launcher_round, "", R.color.baseTextMain, "选择员工", R.color.baseTextMain, "", R.color.textRed, 0);
    }

    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        side_bar = (SideBar) findViewById(R.id.trail_list_employee_sidebar);
        dialog_text = (TextView) findViewById(R.id.trail_list_employee_dialog);
        list_view = (ListView) findViewById(R.id.trail_list_employee_listview);
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
    }
}
