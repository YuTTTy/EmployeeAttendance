package com.xahaolan.emanage.ui.daily;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.services.DailyServices;
import com.xahaolan.emanage.manager.PhotoCamerManager;
import com.xahaolan.emanage.manager.VoiceManager;
import com.xahaolan.emanage.utils.common.DateUtil;
import com.xahaolan.emanage.utils.common.ToastUtils;
import com.xahaolan.emanage.utils.mine.AppUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;

/**
 * Created by helinjie on 2017/9/5.   发起日报
 */

public class SendDailyActivity extends BaseActivity {
    private static final String TAG = SendDailyActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    private VoiceManager voiceManager;
    private PhotoCamerManager photoCamerUtil;
    private int audioType = 0;// 0.未播放  1.正在播放

    private EditText today_et;
    private EditText tomorrow_et;
    private EditText summarize_et;
    private ImageView voice_ico;
    private TextView voice_text;
    private ImageView photo_image;
    private LinearLayout photos_layout;
    private TextView btn_text;

    private int department = 0;//  部门id
    private int employeeid = 0;//  员工id
    private int projectid = 0;//  项目id
    private String date = "";//  填报日期（2017-09-11 12:12:12）
    private String conclusion = "";//   本日工作
    private String question = "";//   存在问题
    private String plan = "";//   明日计划
    private String weather = "";//   天气情况
    private int state = 0;//   状态 0，草稿1.已提交
    private String createuser = "";//  创建用户姓名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_initiate_daily);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "", R.color.baseTextMain, "发起日报", R.color.baseTextMain, "", R.color.baseTextMain, 0);
    }

    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        today_et = (EditText) findViewById(R.id.initiate_daily_today_et);
        tomorrow_et = (EditText) findViewById(R.id.initiate_daily_tomorrow_et);
        summarize_et = (EditText) findViewById(R.id.initiate_daily_summarize_et);
        voice_ico = (ImageView) findViewById(R.id.initiate_daily_voice_icon);
        voice_text = (TextView) findViewById(R.id.initiate_daily_voice_length);
        voice_text.setOnClickListener(this);
        photo_image = (ImageView) findViewById(R.id.initiate_daily_photos_icon);
        photo_image.setOnClickListener(this);
        photos_layout = (LinearLayout) findViewById(R.id.initiate_daily_photos_items_layout);
        btn_text = (TextView) findViewById(R.id.initiate_daily_btn);
        btn_text.setOnClickListener(this);

        voice_ico.setOnTouchListener(new View.OnTouchListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        voiceManager.startRecord();
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        voiceManager.stopRecord();
                        voice_text.setBackground(MyUtils.getShape(MyConstant.COLOR_BLUE, 5f, 1, MyConstant.COLOR_BLUE));
                        voice_text.setVisibility(View.VISIBLE);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void initData() {
        voiceManager = new VoiceManager();
        photoCamerUtil = new PhotoCamerManager((Activity) context, context);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            //play audio
            case R.id.initiate_daily_voice_length:
                if (audioType == 0) {
                    audioType = 1;
                    voiceManager.playAudio(context);
                } else if (audioType == 1) {
                    audioType = 0;
                    voiceManager.stopAudio();
                }
                break;
            //拍照
            case R.id.initiate_daily_photos_icon:
                photoCamerUtil.takePhotoCamer(1, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (msg.what == MyConstant.HANDLER_SUCCESS) {

                        }
                    }
                });
                break;
            //发起
            case R.id.initiate_daily_btn:
                getParams();
                requestSubmit();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getParams() {
        employeeid = AppUtils.getPersonId();
        date = DateUtil.getCurrentDateStr(MyConstant.DATE_FORMAT_YMDHMS);
        conclusion = today_et.getText().toString();//   本日工作
        plan = tomorrow_et.getText().toString();//   明日计划
        question = summarize_et.getText().toString();
        createuser = AppUtils.getPersonName();//  创建用户姓名
    }

    public void requestSubmit() {
        if (conclusion == null || conclusion.equals("")){
            ToastUtils.showShort(context,"请输入本日工作");
            return;
        }
        if (conclusion == null || conclusion.equals("")){
            ToastUtils.showShort(context,"请输入明日计划");
            return;
        }
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new DailyServices(context).dailyNewService(department, employeeid, projectid, date, conclusion,
                question, plan, weather, state, createuser, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                            swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                        }
                        if (msg.what == MyConstant.REQUEST_SUCCESS) {
                            finish();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MyUtils.hideKeyboard((Activity) context);
        photoCamerUtil.activityResult(requestCode, resultCode, data);
    }
}
