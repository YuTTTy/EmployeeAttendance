package com.xahaolan.emanage.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyApplication;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.fragment.ContactsFragment;
import com.xahaolan.emanage.fragment.LeaseFragment;
import com.xahaolan.emanage.fragment.EngineeFragment;
import com.xahaolan.emanage.fragment.NoticeFragment;
import com.xahaolan.emanage.utils.common.LogUtils;
import com.xahaolan.emanage.utils.common.ToastUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;

public class MainActivity extends BaseActivity {
    private static String TAG = MainActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    public static MainActivity instance = null;

    private int[] imageArray = {R.drawable.selector_tab_contacts, R.drawable.selector_tab_notice, R.drawable.selector_tab_lease, R.drawable.selector_tab_mine};
    private String[] textArray = {"通讯录", "公告", "租凭", "工程"};
    private Class fragmentArray[] = {ContactsFragment.class, NoticeFragment.class, LeaseFragment.class, EngineeFragment.class};
    private FragmentTabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_main);
        instance = this;
    }

    @Override
    public void setTitleAttribute() {

    }

    @Override
    public void initView() {
        initTabView(); //加载底部tab
        bindTabListener();//tab点击切换
    }

    @Override
    public void initData() {
        MyUtils.closeAllActivity(context);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyUtils.hideKeyboard((Activity) context);
        MyApplication.setFirstMain(false);
    }

    /**
     * 加载底部tab
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void initTabView() {
        tabHost = (FragmentTabHost) findViewById(R.id.main_tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.main_body);
        for (int i = 0; i < fragmentArray.length; i++) {
            /*给每个Tab按钮设置图标、文字和内容*/
            TabHost.TabSpec tabSpec = tabHost.newTabSpec(textArray[i]).setIndicator(getTabItemView(i));
            /*将Tab按钮添加进Tab选项卡中*/
            tabHost.addTab(tabSpec, fragmentArray[i], null);
            /*设置Tab按钮的背景*/
            tabHost.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.color.alpha);
        }
        tabHost.getTabWidget().setDividerDrawable(null);//去除分割线

    }

    /**
     * 给每个Tab按钮设置图标和文字
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public View getTabItemView(int index) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_main_tab, null);
        ImageView tab_iv = (ImageView) itemView.findViewById(R.id.item_main_tab_image);
        tab_iv.setImageResource(imageArray[index]);
        TextView tab_text = (TextView) itemView.findViewById(R.id.item_main_tab_text);
        tab_text.setText(textArray[index]);
        tab_text.setTextColor(Color.parseColor(MyConstant.COLOR_GRAY_TEXT));
        return itemView;
    }

    /**
     * 点击监听，改变字体颜色
     */
    public void bindTabListener() {
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onTabChanged(String tabId) {
                int childCount = tabHost.getTabWidget().getChildCount();//子tab个数
                LogUtils.e(TAG, "tab个数：" + childCount);
                for (int i = 0; i < childCount; i++) {
                    View itemView = tabHost.getTabWidget().getChildTabViewAt(i);
                    TextView textView = (TextView) itemView.findViewById(R.id.item_main_tab_text);
                    /*当前选中tab*/
                    if (textView != null && textView.getText().toString().equals(tabId)) {
                        textView.setTextColor(Color.parseColor(MyConstant.COLOR_BLUE));
                    /*未选中tab*/
                    } else {
                        textView.setTextColor(Color.parseColor(MyConstant.COLOR_GRAY_TEXT));
                    }
                }
                LogUtils.e(TAG, "当前页：" + tabId);
            }
        });
    }

    long exitTime = 0L; //退出时间

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                ToastUtils.showShort(MainActivity.this, "再按一次返回退出程序");
                exitTime = System.currentTimeMillis();
            } else {
//                android.os.Process.killProcess(Process.myPid());
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e(TAG, "onDestroy");
    }
}
