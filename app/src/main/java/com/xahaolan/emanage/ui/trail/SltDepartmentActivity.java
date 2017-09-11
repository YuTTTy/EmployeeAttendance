package com.xahaolan.emanage.ui.trail;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.utils.mine.MyUtils;

/**
 * Created by helinjie on 2017/9/10.
 */

public class SltDepartmentActivity extends BaseActivity {
    private static final String TAG = SltDepartmentActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;

    private LinearLayout items_layout;
    private String[] nameArr = {"销售部","技术部","工程部","采购部","财务部"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_trail_slt_department);
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
        items_layout = (LinearLayout) findViewById(R.id.slt_employee_items_layout);
    }

    @Override
    public void initData() {
        for (int i = 0; i < nameArr.length;i++){
            items_layout.addView(addItemView(nameArr[i]));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public View addItemView(String nameStr){
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_view_slt_employee,null);
        TextView name_text = (TextView) itemView.findViewById(R.id.item_view_employee_name);
        name_text.setText(nameStr);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtils.jump(context,SltEmployeeActivity.class,new Bundle(),false,null);
            }
        });
        return itemView;
    }
}
