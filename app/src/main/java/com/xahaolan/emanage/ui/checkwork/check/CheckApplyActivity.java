package com.xahaolan.emanage.ui.checkwork.check;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.adapter.CheckApplyAdapter;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.dialog.DialogSingleText;

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
    private String[] strArr = {"全部","请假","外出","出差","加班"};

    private int checkType;//1.查看我的申请 2.查看我的审批

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
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        examine_layout = (LinearLayout) findViewById(R.id.check_apply_examine_layout);
        wait_text = (TextView) findViewById(R.id.check_apply_wait_text);
        wait_text.setOnClickListener(this);
        already_text = (TextView) findViewById(R.id.check_apply_already_text);
        already_text.setOnClickListener(this);
        slt_layout = (LinearLayout) findViewById(R.id.check_apply_slt_layout);
        slt_text = (TextView) findViewById(R.id.check_apply_slt_text);
        slt_layout.setOnClickListener(this);
        list_view = (ListView) findViewById(R.id.check_apply_list_view);

    }

    @Override
    public void initData() {
        dataList = new ArrayList<>();
        intent = getIntent();
        checkType = intent.getIntExtra("CheckType", 1);
        adapter = new CheckApplyAdapter(context);
        list_view.setAdapter(adapter);
        if (checkType == MyConstant.CHECK_MINE_APPLY) {
            setTitle(0, R.color.titleBg, R.drawable.ic_launcher_round, "返回", R.color.baseTextMain, "查看我的申请", R.color.baseTextMain, "", R.color.baseTextMain, 0);
            examine_layout.setVisibility(View.GONE);
        } else if (checkType == MyConstant.CHECK_MINE_EXAMINE) {
            setTitle(0, R.color.titleBg, R.drawable.ic_launcher_round, "返回", R.color.baseTextMain, "查看我的审批", R.color.baseTextMain, "", R.color.baseTextMain, 0);
            examine_layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.check_apply_wait_text:

                break;
            case R.id.check_apply_already_text:

                break;
            case R.id.check_apply_slt_layout:
                new DialogSingleText(context,strArr,new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (msg.what == MyConstant.HANDLER_SUCCESS){
                            int position = (int) msg.obj;
                            requestApply();
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

    public void requestApply() {
        adapter.resetList(dataList);
        adapter.notifyDataSetChanged();
    }

    public void requestExamine() {

    }
}
