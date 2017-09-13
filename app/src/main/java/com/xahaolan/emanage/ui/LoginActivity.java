package com.xahaolan.emanage.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.services.LoginServices;
import com.xahaolan.emanage.utils.common.ToastUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;

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
        setcontentLayout(R.layout.activity_login);
    }

    @Override
    public void setTitleAttribute() {
    }

    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        account_et = (EditText) findViewById(R.id.login_account);
        pass_et = (EditText) findViewById(R.id.login_password);
        btn_text = (TextView) findViewById(R.id.login_btn);
        btn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtils.jump(context, MainActivity.class, new Bundle(), false, null);
                finish();
//                requestLogin();
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {

    }
    public void requestLogin(){
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
        new LoginServices(context).loginService(account_et.getText().toString(), pass_et.getText().toString(), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    String data = (String) msg.obj;
                    if (data != null) {

                    }
                    MyUtils.jump(context, MainActivity.class, new Bundle(), false, null);
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
    /**
     * 错误处理
     */
    public void errDeal() {
        pass_et.setText("");
        /*隐藏软键盘*/
        MyUtils.hideKeyboard(LoginActivity.this);
    }
}
