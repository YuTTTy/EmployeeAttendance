package com.xahaolan.emanage.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.BoolRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyApplication;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.services.LoginServices;
import com.xahaolan.emanage.utils.common.SPUtils;
import com.xahaolan.emanage.utils.common.ToastUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/2.
 */

public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    private EditText account_et;
    private EditText pass_et;
    private TextView btn_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Boolean isLogin = (Boolean) SPUtils.get(context, MyConstant.SHARED_SAVE, MyConstant.IS_ALREADY_LOGIN, false);
        if (isLogin) {
            MyUtils.jump(context, MainActivity.class, new Bundle(), false, null);
            finish();
        } else {
            setcontentLayout(R.layout.activity_login);
        }
    }

    @Override
    public void setTitleAttribute() {
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        account_et = (EditText) findViewById(R.id.login_account);
        account_et.setBackground(MyUtils.getShape(MyConstant.COLOR_ALPHA, 5f, 1, MyConstant.COLOR_GRAY_BG));
        pass_et = (EditText) findViewById(R.id.login_password);
        pass_et.setBackground(MyUtils.getShape(MyConstant.COLOR_ALPHA, 5f, 1, MyConstant.COLOR_GRAY_BG));
        btn_text = (TextView) findViewById(R.id.login_btn);
        btn_text.setBackground(MyUtils.getShape(MyConstant.COLOR_ORANGE, 5f, 1, MyConstant.COLOR_GRAY_BG));
        btn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MyUtils.jump(context, MainActivity.class, new Bundle(), false, null);
//                finish();
                login();
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {

    }

    public void login() {
        if (account_et.getText().toString() == null || account_et.getText().toString().equals("")) {
            errDeal();
            ToastUtils.showShort(this, "请输入手机号码");
            return;
        }
        if (pass_et.getText().toString() == null || pass_et.getText().toString().equals("")) {
            errDeal();
            ToastUtils.showShort(this, "请输入密码");
            return;
        }
//        if (pass_et.getText().toString().length() < 6) {
//            errDeal();
//            ToastUtils.showShort(context, "密码不能小于六位");
//            pass_et.setText("");
//            return;
//        }

        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        requestGetSession();
    }

    /**
     * get session
     */
    public void requestGetSession() {
        new LoginServices(context).getSessionService(account_et.getText().toString(), pass_et.getText().toString(), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    String data = (String) msg.obj;
                    requestLogin();
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

    /**
     * login
     */
    public void requestLogin() {
        new LoginServices(context).loginService(account_et.getText().toString(), pass_et.getText().toString(), new Handler() {
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
                        SPUtils.put(context, MyConstant.SHARED_SAVE, MyConstant.SP_LOGIN_DATA,data);
                        SPUtils.put(context, MyConstant.SHARED_SAVE, MyConstant.IS_ALREADY_LOGIN, true);
                        MyUtils.jump(context, MainActivity.class, new Bundle(), false, null);
                        finish();
                    }else {
                        ToastUtils.showShort(context,"未获取用户数据，请稍后再试");
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

    /**
     * 错误处理
     */
    public void errDeal() {
        pass_et.setText("");
        /*隐藏软键盘*/
        MyUtils.hideKeyboard(LoginActivity.this);
    }
}
