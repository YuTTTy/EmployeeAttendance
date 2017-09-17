package com.xahaolan.emanage.ui.checkwork;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyApplication;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.services.CheckWorkServices;
import com.xahaolan.emanage.http.services.LoginServices;
import com.xahaolan.emanage.ui.MainActivity;
import com.xahaolan.emanage.ui.checkwork.apply.DocumentActivity;
import com.xahaolan.emanage.ui.checkwork.check.CheckApplyActivity;
import com.xahaolan.emanage.ui.checkwork.clockrecord.ClockRecordActivity;
import com.xahaolan.emanage.utils.common.ToastUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/3.    考勤
 */

public class CheckWorkActivity extends BaseActivity {
    private static final String TAG = CheckWorkActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;

    private LinearLayout item_layout;
    private TextView in_text;
    private TextView out_text;

    private Integer[] imageArr = {R.drawable.ic_launcher_round, R.drawable.ic_launcher_round, R.drawable.ic_launcher_round,
            R.drawable.ic_launcher_round, R.drawable.ic_launcher_round, R.drawable.ic_launcher_round, R.drawable.ic_launcher_round};
    private String[] strArr = {"请假申请", "外出登记", "出差申请", "加班登记", "打卡记录", "查看我的申请", "查看我的登记"};

    private int personId;   //员工id
    private String personName;  //员工姓名
    private String createdate;  //上传日期（年月日）
    private String createTime;  //上传时间（时分秒）
    private String longitude;   //经度
    private String latitude;    //纬度
    private String label;       //位置文字信息
    private int signflag;   // 0:签到，1:签退

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_check_work);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "", R.color.baseTextMain, "考勤", R.color.baseTextMain, "", R.color.baseTextMain, 0);
    }

    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        item_layout = (LinearLayout) findViewById(R.id.check_work_item_layout);
        in_text = (TextView) findViewById(R.id.check_work_in);
        in_text.setOnClickListener(this);
        out_text = (TextView) findViewById(R.id.check_work_out);
        out_text.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.check_work_in:
                signflag = 0;
                requestInOut();
                break;
            case R.id.check_work_out:
                signflag = 1;
                requestInOut();
                break;
        }
    }

    @Override
    public void initData() {
        for (int i = 0; i < imageArr.length; i++) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("image", imageArr[i]);
            data.put("name", strArr[i]);
            addItemView(data, i);
        }
    }

    public void addItemView(Map<String, Object> data, int position) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_view_check_work, null);
        ImageView image_view = (ImageView) itemView.findViewById(R.id.item_view_check_work_image);
        TextView name_text = (TextView) itemView.findViewById(R.id.item_view_check_work_text);
        if (data.get("image") != null) {
            int imageRes = (int) data.get("image");
            image_view.setImageResource(imageRes);
        }
        if (data.get("name") != null) {
            name_text.setText(data.get("name") + "");
        }
        item_layout.addView(itemView);

        setItemClick(itemView, position);
    }

    public void setItemClick(View itemView, final int position) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                switch (position) {
                    //请假申请
                    case 0:
                        bundle.putInt("ApplyType", MyConstant.APPLY_DOCUMENT_LEAVE_APPLY);
                        MyUtils.jump(context, DocumentActivity.class, bundle, false, null);
                        break;
                    //外出登记
                    case 1:
                        bundle.putInt("ApplyType", MyConstant.APPLY_DOCUMENT_OUT_REGISTER);
                        MyUtils.jump(context, DocumentActivity.class, bundle, false, null);
                        break;
                    //出差申请
                    case 2:
                        bundle.putInt("ApplyType", MyConstant.APPLY_DOCUMENT_OUT_APPLY);
                        MyUtils.jump(context, DocumentActivity.class, bundle, false, null);
                        break;
                    //加班登记
                    case 3:
                        bundle.putInt("ApplyType", MyConstant.APPLY_DOCUMENT_WORK_REGISTER);
                        MyUtils.jump(context, DocumentActivity.class, bundle, false, null);
                        break;
                    //打卡记录
                    case 4:
                        MyUtils.jump(context, ClockRecordActivity.class, new Bundle(), false, null);
                        break;
                    //查看我的申请
                    case 5:
                        bundle.putInt("CheckType", MyConstant.CHECK_MINE_APPLY);
                        MyUtils.jump(context, CheckApplyActivity.class, bundle, false, null);
                        break;
                    //查看我的审批
                    case 6:
                        bundle.putInt("CheckType", MyConstant.CHECK_MINE_EXAMINE);
                        MyUtils.jump(context, CheckApplyActivity.class, bundle, false, null);
                        break;
                }
            }
        });
    }

    public void requestInOut() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new CheckWorkServices(context).addClockAddService(personId, personName, createdate, createTime,
                longitude, latitude, label, signflag, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                            swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                        }
                        if (msg.what == MyConstant.REQUEST_SUCCESS) {
                            List<Map<String, Object>> dataList = (List<Map<String, Object>>) msg.obj;
                            if (dataList != null && dataList.size() > 0) {
                                Map<String, Object> data = dataList.get(0);
                                MyApplication.setLoginData(data);
                                MyUtils.jump(context, MainActivity.class, new Bundle(), false, null);
                                finish();
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
}
