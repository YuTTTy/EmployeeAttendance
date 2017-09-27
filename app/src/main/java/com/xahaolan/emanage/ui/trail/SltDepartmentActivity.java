package com.xahaolan.emanage.ui.trail;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.services.TrailServices;
import com.xahaolan.emanage.utils.common.ToastUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/10.
 */

public class SltDepartmentActivity extends BaseActivity {
    private static final String TAG = SltDepartmentActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;

    private LinearLayout items_layout;
    //    private String[] nameArr = {"销售部","技术部","工程部","采购部","财务部"};
    private List<Map<String, Object>> dataList;

    private int personid; //申请人id
    private int page = 1;  //当前页
    private int rows = 20;   //每页显示记录数
    private Boolean hasNextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_trail_slt_department);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "", R.color.baseTextMain, "选择员工", R.color.baseTextMain, "", R.color.textRed, 0);
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        dataList = new ArrayList<>();
        requestDepartment();
    }

    /**
     * 部门列表
     */
    public void requestDepartment() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new TrailServices(context).departmentListService(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    dataList = (List<Map<String, Object>>) msg.obj;
                    if (dataList != null && dataList.size() > 0) {
                        for (int i = 0; i < dataList.size();i++){
                            items_layout.addView(addItemView(dataList.get(i)));
                        }
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

    public View addItemView(final Map<String,Object> data) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_view_slt_employee, null);
        TextView name_text = (TextView) itemView.findViewById(R.id.item_view_employee_name);
        name_text.setText(data.get("name")+"");
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.get("departmentid") != null){
                    int departmentId = new Double((Double) data.get("departmentid")).intValue();
                    Bundle bundle = new Bundle();
                    bundle.putInt("departmentid",departmentId);
                    MyUtils.jump(context, SltEmployeeActivity.class, bundle, false, null);
                }
            }
        });
        return itemView;
    }
}
