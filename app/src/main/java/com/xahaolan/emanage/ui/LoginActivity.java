package com.xahaolan.emanage.ui;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.dao.UserDataManager;
import com.xahaolan.emanage.utils.mine.MyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yujx on 2018/4/17.
 */
public class LoginActivity extends Activity {
    public static final int CONNECTION_TIMEOUT = 40;
    public static final int READ_TIMEOUT = 40;
    @BindView(R.id.account_et)
    EditText etAccount;
    @BindView(R.id.password_et)
    EditText etPassword;
    @BindView(R.id.login_button)
    Button button_login;
    @BindView(R.id.login_rememberpassword)
    CheckBox rememberpassword_login;
    @BindView(R.id.login_avatar)
    ImageView avatar_login;
    @BindView(R.id.btn_register)
    TextView register_btn;
    @BindView(R.id.login_cancle)
    ImageView cancle_btn;
    @BindView(R.id.edit_text)
    View loginView;    //登录
    @BindView(R.id.login_success_view)
    View loginSuccessView;
    @BindView(R.id.login_success_show)
    TextView loginSuccessShow;

    private SharedPreferences sp;
    private String idValue;
    private String passwordValue;
    private static final int PASSWORD_MIWEN = 0x81;
    private UserDataManager mUserDataManager;         //用户数据管理类


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //找到相应的布局
        setContentView(R.layout.activity_login);
        //装载资源文件
        ButterKnife.bind(this);
        sp = this.getSharedPreferences("UserInfo", 0);
        String name=sp.getString("USER_NAME", "");
        String pwd =sp.getString("PASSWORD", "");

        boolean choseRemember = sp.getBoolean("rememberpassword_login",false);
        if (choseRemember){
            etAccount.setText(name);
            etPassword.setText(pwd);
            etPassword.setInputType(PASSWORD_MIWEN);
            rememberpassword_login.setChecked(true);
        }

        //avatar_login.setImageResource(R.drawable.head);

        if (mUserDataManager == null) {
            mUserDataManager = new UserDataManager(this);
            mUserDataManager.openDataBase();                              //建立本地数据库
        }
    }
    //不同按钮按下的监听事件选择

        @OnClick({R.id.btn_register,R.id.login_button,R.id.login_cancle})
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_register:
                    Intent intent_login_to_register = new Intent(LoginActivity.this,
                            RegisterActivity.class);
                    startActivity(intent_login_to_register);
                    finish();
                    break;
                case R.id.login_button:
                    logoin();
                    break;
                case R.id.login_cancle:
                    cancle();
                    break;
            }

        }

    //登录
    public void logoin() {
        if (isUserNameAndPwdValid()){
            String userName = etAccount.getText().toString().trim();
            String userPwd = etPassword.getText().toString().trim();
            SharedPreferences.Editor editor = sp.edit();
            int result = mUserDataManager.findUserByNameAndPwd(userName,userPwd);
            if (result == 1){
                //保存用户名和密码
                editor.putString("USER_NAME", userName);
                editor.putString("PASSWORD", userPwd);
                //是否记住密码
                if (rememberpassword_login.isChecked()){
                    editor.putBoolean("rememberpassword_login",true);
                }else {
                    editor.putBoolean("rememberpassword_login",false);
                }
                editor.commit();
                //切换到MainActivity
                Intent intent_login_to_main = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent_login_to_main);
                finish();
                //登录成功提示
                Toast.makeText(this,"登陆成功！",Toast.LENGTH_SHORT).show();
            }else if (result == 0){
                //登录失败提示
                Toast.makeText(this, "登录失败！",Toast.LENGTH_SHORT).show();
            }
        }
    }
    //注销
    public void cancle() {
        if (isUserNameAndPwdValid()) {
            String userName = etAccount.getText().toString().trim();    //获取当前输入的用户名和密码信息
            String userPwd = etPassword.getText().toString().trim();
            int result=mUserDataManager.findUserByNameAndPwd(userName, userPwd);
            if(result==1){                                             //返回1说明用户名和密码均正确
                Toast.makeText(this, "注销失败！！！",Toast.LENGTH_SHORT).show(); //注销成功提示
                etPassword.setText("");
                etAccount.setText("");
                mUserDataManager.deleteUserDatabyname(userName);
            }else if(result==0){
                Toast.makeText(this, "注销成功！！！",Toast.LENGTH_SHORT).show();  //注销失败提示
            }
        }
    }

    public boolean isUserNameAndPwdValid(){
        if (etAccount.getText().toString().trim().equals("")){
            Toast.makeText(this, getString(R.string.account_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if (etPassword.getText().toString().trim().equals("")){
            Toast.makeText(this, getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        if (mUserDataManager == null) {
            mUserDataManager = new UserDataManager(this);
            mUserDataManager.openDataBase();
        }
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onPause() {
        if (mUserDataManager != null) {
            mUserDataManager.closeDataBase();
            mUserDataManager = null;
        }
        super.onPause();
    }

    /**
     * 错误处理
     */
    public void errDeal() {
        etPassword.setText("");
        /*隐藏软键盘*/
        MyUtils.hideKeyboard(LoginActivity.this);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            System.exit(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
