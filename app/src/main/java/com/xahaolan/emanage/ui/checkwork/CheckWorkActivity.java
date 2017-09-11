package com.xahaolan.emanage.ui.checkwork;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.ui.checkwork.apply.DocumentActivity;
import com.xahaolan.emanage.ui.checkwork.check.CheckApplyActivity;
import com.xahaolan.emanage.ui.checkwork.clockrecord.ClockRecordActivity;
import com.xahaolan.emanage.utils.mine.MyUtils;

import java.util.HashMap;
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

    private Integer[] imageArr = {R.drawable.ic_launcher_round,R.drawable.ic_launcher_round,R.drawable.ic_launcher_round,
            R.drawable.ic_launcher_round,R.drawable.ic_launcher_round,R.drawable.ic_launcher_round,R.drawable.ic_launcher_round};
    private String[] strArr = {"请假申请","外出登记","出差申请","加班登记","打卡记录","查看我的申请","查看我的登记"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_check_work);
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
        item_layout = (LinearLayout) findViewById(R.id.check_work_item_layout);
        in_text = (TextView) findViewById(R.id.check_work_in);
        in_text.setOnClickListener(this);
        out_text = (TextView) findViewById(R.id.check_work_out);
        out_text.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.check_work_in:

                break;
            case R.id.check_work_out:

                break;
        }
    }

    @Override
    public void initData() {
        for (int i =0;i < imageArr.length;i++){
            Map<String,Object> data = new HashMap<String,Object>();
            data.put("image",imageArr[i]);
            data.put("name",strArr[i]);
            addItemView(data,i);
        }
    }

    public void addItemView(Map<String,Object> data,int position){
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_view_check_work,null);
        ImageView image_view = (ImageView) itemView.findViewById(R.id.item_view_check_work_image);
        TextView name_text = (TextView) itemView.findViewById(R.id.item_view_check_work_text);
        if (data.get("image") != null){
            int imageRes = (int) data.get("image");
            image_view.setImageResource(imageRes);
        }
        if (data.get("name") != null){
            name_text.setText(data.get("name")+"");
        }
        item_layout.addView(itemView);

        setItemClick(itemView,position);
    }
    public void setItemClick(View itemView, final int position){
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                switch (position){
                    //请假申请
                    case 0:
                        bundle.putInt("ApplyType", MyConstant.APPLY_DOCUMENT_LEAVE_APPLY);
                        MyUtils.jump(context,DocumentActivity.class,bundle,false,null);
                        break;
                    //外出登记
                    case 1:
                        bundle.putInt("ApplyType", MyConstant.APPLY_DOCUMENT_OUT_REGISTER);
                        MyUtils.jump(context,DocumentActivity.class,bundle,false,null);
                        break;
                    //出差申请
                    case 2:
                        bundle.putInt("ApplyType", MyConstant.APPLY_DOCUMENT_OUT_APPLY);
                        MyUtils.jump(context,DocumentActivity.class,bundle,false,null);
                        break;
                    //加班登记
                    case 3:
                        bundle.putInt("ApplyType", MyConstant.APPLY_DOCUMENT_WORK_REGISTER);
                        MyUtils.jump(context,DocumentActivity.class,bundle,false,null);
                        break;
                    //打卡记录
                    case 4:
                        MyUtils.jump(context,ClockRecordActivity.class,new Bundle(),false,null);
                        break;
                    //查看我的申请
                    case 5:
                        bundle.putInt("CheckType",MyConstant.CHECK_MINE_APPLY);
                        MyUtils.jump(context,CheckApplyActivity.class,bundle,false,null);
                        break;
                    //查看我的审批
                    case 6:
                        bundle.putInt("CheckType",MyConstant.CHECK_MINE_EXAMINE);
                        MyUtils.jump(context,CheckApplyActivity.class,bundle,false,null);
                        break;
                }
            }
        });
    }
}
