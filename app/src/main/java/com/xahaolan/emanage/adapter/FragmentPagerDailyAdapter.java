package com.xahaolan.emanage.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.xahaolan.emanage.fragment.TabDailyFragment;
import com.xahaolan.emanage.utils.common.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by helinjie on 2017/9/4.
 */

public class FragmentPagerDailyAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = FragmentPagerDailyAdapter.class.getSimpleName();
    private Context context;
    private List<Fragment> list = new ArrayList<>();

    public FragmentPagerDailyAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    public FragmentPagerDailyAdapter(FragmentManager fm, Context context, List<Fragment> list) {
        super(fm);
        this.context = context;
        this.list.addAll(list);
    }

    @Override
    public Fragment getItem(int position) {
        return InstanceFragment(position);
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public int getItemPosition(Object object) {
        LogUtils.e(TAG, "getItemPosition() : " + super.getItemPosition(object));
        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        LogUtils.e(TAG, "destroyItem()");
        super.destroyItem(container, position, object);
    }

    public static TabDailyFragment InstanceFragment(int position) {
        TabDailyFragment fragment = new TabDailyFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("ProgressType", position);
        fragment.setArguments(bundle);
        return fragment;
    }
}
