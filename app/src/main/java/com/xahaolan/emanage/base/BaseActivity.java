package com.xahaolan.emanage.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.ui.LoginActivity;
import com.xahaolan.emanage.ui.MainActivity;
import com.xahaolan.emanage.utils.common.SPUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;

/**
 * Created by Administrator on 2016/6/17.
 */
public abstract class BaseActivity extends FragmentActivity implements BaseInterface, View.OnClickListener {
    private static final String TAG = BaseActivity.class.getSimpleName();
    protected Context context;

    /*标题栏*/
    public View title_view;
    /*通讯录*/
    public RelativeLayout title_layout;
    public ImageView left_view;
    public TextView left_text;
    public TextView title_text;
    public TextView right_text;
    public ImageView right_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
        context = this;
    }

    /**
     * 初始化布局
     *
     * @param layoutId
     */
    public void setcontentLayout(int layoutId) {
        setContentView(layoutId);
//        //禁止输入法在activity启动时弹出，手动点击输入框可以弹出
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setTitleAttribute();
        initView();
        initData();
    }

    /**
     * 初始化布局 (动态)
     *
     * @param view
     * @param params
     */
    public void setcontentLayout(ViewGroup view, ViewGroup.LayoutParams params) {
        addContentView(view, params);
//        //禁止输入法在activity启动时弹出，手动点击输入框可以弹出
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setTitleAttribute();
        initView();
        initData();
    }

    /**
     * *通讯录标题栏
     *
     * @param stateColorInt  状态栏背景
     * @param titleBgInt     标题栏背景
     * @param leftImageInt   左图
     * @param leftText       左文字
     * @param leftTextColor
     * @param title          标题字
     * @param titleTextColor
     * @param rightText      右文字
     * @param rightTextColor
     * @param rightImageInt  右图
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setTitle(int stateColorInt, int titleBgInt, int leftImageInt,String leftText, int leftTextColor, String title, int titleTextColor, String rightText, int rightTextColor, int rightImageInt) {
        initTitleView();
        if (stateColorInt != 0) {
            getWindow().setStatusBarColor(getResources().getColor(stateColorInt));//设置状态栏颜色
        }

        if (titleBgInt != 0) {
            title_layout.setBackgroundResource(titleBgInt);
        }

        if (leftImageInt == 0) {
            left_view.setVisibility(View.GONE);
        } else {
            left_view.setVisibility(View.VISIBLE);
            left_view.setImageResource(leftImageInt);
        }

        if (leftText != null && !leftText.equals("")) {
            left_text.setVisibility(View.VISIBLE);
            left_text.setText(leftText);
        } else {
            left_text.setVisibility(View.GONE);
        }
        left_text.setTextColor(getResources().getColor(leftTextColor));

        if (title != null && !title.equals("")) {
            title_text.setVisibility(View.VISIBLE);
            title_text.setText(title);
            title_text.setTextColor(getResources().getColor(titleTextColor));
        } else {
            title_text.setVisibility(View.GONE);
        }

        if (rightText != null && !rightText.equals("")) {
            right_text.setVisibility(View.VISIBLE);
            right_text.setText(rightText);
            right_text.setTextColor(getResources().getColor(rightTextColor));
        } else {
            right_text.setVisibility(View.GONE);
        }

        if (rightImageInt == 0) {
            right_view.setVisibility(View.GONE);
        } else {
            right_view.setVisibility(View.VISIBLE);
            right_view.setImageResource(rightImageInt);
        }
    }
    /**
     * 初始化标题栏
     */
    public void initTitleView() {
        title_view = findViewById(R.id.public_title_view);
        title_layout = (RelativeLayout) findViewById(R.id.public_title_list_layout);
        left_view = (ImageView) findViewById(R.id.public_title_back);
        left_text = (TextView) findViewById(R.id.public_title_left_text);
        title_text = (TextView) findViewById(R.id.public_title_title);
        right_text = (TextView) findViewById(R.id.public_title_right_text);
        right_text.setOnClickListener(this);
        right_view = (ImageView) findViewById(R.id.public_title_right_image);
        right_view.setOnClickListener(this);
        left_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtils.hideKeyboard((Activity) context); //隐藏软键盘
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 谷歌自带刷新加载
     *
     * @param swipeLayout
     * @param handler
     */
    public static void setSwipRefresh(SwipeRefreshLayout swipeLayout, final Handler handler) {
        /*1.设置刷新时动画颜色*/
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        /*设置进度圈的背景色*/
        swipeLayout.setProgressBackgroundColor(android.R.color.white);
        /*设置进度圈的大小，只有两个值：DEFAULT、LARGE*/
        swipeLayout.setSize(SwipeRefreshLayout.DEFAULT);
        /*2.设置滚动监听*/
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (handler != null) {
                    handler.sendEmptyMessage(MyConstant.HANDLER_REFRESH_SUCCESS);
                }
            }
        });
    }

    /**
     * 退出登录，清空缓存
     *
     * @param context
     */
    public static void loginOut(Context context) {
        /*清除缓存*/
        SPUtils.clear(context, MyConstant.SHARED_SAVE);
        /*清除内存*/
        MyApplication.getInstance().clearMemory();
//        /* 关闭指定activity */
//        MainActivity.instance.finish();
        /* close all activity */
        MyUtils.closeAllActivity(context);

        MyUtils.jump(context, LoginActivity.class, new Bundle(), false, null);
        ((Activity)context).finish();

    }
}
