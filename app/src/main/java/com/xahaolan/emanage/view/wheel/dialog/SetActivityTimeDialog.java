package com.xahaolan.emanage.view.wheel.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.view.wheel.adapters.AbstractWheelTextAdapter;
import com.xahaolan.emanage.view.wheel.views.OnWheelChangedListener;
import com.xahaolan.emanage.view.wheel.views.OnWheelScrollListener;
import com.xahaolan.emanage.view.wheel.views.WheelView;

import java.util.ArrayList;

/**
 * 日期选择对话框
 *
 * @author ywl
 */
public class SetActivityTimeDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private WheelView number_view;// 1 - 12
    private WheelView unit_view;//单位:小时、天、周、月
    private WheelView wvDay;//单位:小时、天、周、月

    private View vChangeBirth;
    private View vChangeBirthChild;
    private TextView btnSure;
    private TextView btnCancel;

    private ArrayList<String> arry_years = new ArrayList<String>();
    private ArrayList<String> arry_months = new ArrayList<String>();
    private CalendarTextAdapter mYearAdapter;
    private CalendarTextAdapter mMonthAdapter;

    private int maxTextSize = 24;
    private int minTextSize = 14;

    private String selectNumber = "2";
    private String selectUnit = "天";

    private OnBirthListener onBirthListener;

    public SetActivityTimeDialog(Context context) {
        super(context, R.style.ShareDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_myinfo_changebirth);
        number_view = (WheelView) findViewById(R.id.wv_birth_year);
        unit_view = (WheelView) findViewById(R.id.wv_birth_month);
        wvDay = (WheelView) findViewById(R.id.wv_birth_day);
        wvDay.setVisibility(View.GONE);

        btnSure = (TextView) findViewById(R.id.btn_myinfo_sure);
        btnCancel = (TextView) findViewById(R.id.btn_myinfo_cancel);

        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        initYears();
        mYearAdapter = new CalendarTextAdapter(context, arry_years, 1, maxTextSize, minTextSize);
        number_view.setVisibleItems(5);
        number_view.setViewAdapter(mYearAdapter);
        number_view.setCurrentItem(1);

        initMonths();
        mMonthAdapter = new CalendarTextAdapter(context, arry_months, 1, maxTextSize, minTextSize);
        unit_view.setVisibleItems(5);
        unit_view.setViewAdapter(mMonthAdapter);
        unit_view.setCurrentItem(1);

        number_view.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) mYearAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mYearAdapter);

            }
        });

        number_view.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) mYearAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mYearAdapter);
                selectNumber = currentText;
            }
        });

        unit_view.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) mMonthAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mMonthAdapter);

            }
        });
        unit_view.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) mMonthAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mMonthAdapter);
                selectUnit = currentText;
            }
        });
    }

    public void initYears() {
        for (int i = 1; i < 13; i++) {
            arry_years.add(i + "");
        }
    }

    public void initMonths() {
        arry_months.clear();
        arry_months.add("小时");
        arry_months.add("天");
        arry_months.add("周");
        arry_months.add("月");
    }

    private class CalendarTextAdapter extends AbstractWheelTextAdapter {
        ArrayList<String> list;

        protected CalendarTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize, int minsize) {
            super(context, R.layout.item_birth_year, NO_RESOURCE, currentItem, maxsize, minsize);
            this.list = list;
            setItemTextResource(R.id.tempValue);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index) + "";
        }
    }


    public interface OnBirthListener {
        void onClick(String year, String month);
    }
    public void setBirthdayListener(OnBirthListener onBirthListener) {
        this.onBirthListener = onBirthListener;
    }

    @Override
    public void onClick(View v) {
        if (v == btnSure) {
            if (onBirthListener != null) {
                onBirthListener.onClick(selectNumber, selectUnit);
            }
        } else if (v == btnSure) {

        } else if (v == vChangeBirthChild) {
            return;
        } else {
            dismiss();
        }
        dismiss();

    }

    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, CalendarTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(maxTextSize);
            } else {
                textvew.setTextSize(minTextSize);
            }
        }
    }

}