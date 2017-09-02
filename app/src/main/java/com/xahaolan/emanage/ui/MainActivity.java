package com.xahaolan.emanage.ui;

import android.os.Bundle;
import android.view.View;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;

public class MainActivity extends BaseActivity {
    public static MainActivity instance = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_main);
        instance = this;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void setTitleAttribute() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }
}
