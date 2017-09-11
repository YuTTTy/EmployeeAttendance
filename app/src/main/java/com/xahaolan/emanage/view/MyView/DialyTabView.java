package com.xahaolan.emanage.view.MyView;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xahaolan.emanage.R;

/**
 * Created by helinjie on 2017/9/4.
 */

public class DialyTabView extends LinearLayout implements View.OnClickListener{
    private static final String TAG = DialyTabView.class.getSimpleName();
    private Context context;

    /*全部*/
    private LinearLayout all_layout;
    private TextView all_text;
//    private View all_view;
    /*我的*/
    private LinearLayout mine_layout;
    private TextView mine_text;
//    private View effect_view;
    /*日报*/
    private LinearLayout daily_layout;
    private TextView daily_text;
//    private View result_view;
    /*周报*/
    private LinearLayout week_layout;
    private TextView week_text;
//    private View performance_view;

    public DialyTabView(Context context) {
        super(context);
    }

    public DialyTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public DialyTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initView() {
        View rootView = LayoutInflater.from(context).inflate(R.layout.dialy_tab_view, null);
        addView(rootView);
        all_layout = (LinearLayout) rootView.findViewById(R.id.tab_activity_progress_all_layout);
        all_layout.setOnClickListener(this);
        all_text = (TextView) rootView.findViewById(R.id.tab_activity_progress_all_text);
//        all_view = rootView.findViewById(R.id.tab_activity_progress_all_view);

        mine_layout = (LinearLayout) rootView.findViewById(R.id.tab_activity_progress_effect_layout);
        mine_layout.setOnClickListener(this);
        mine_text = (TextView) rootView.findViewById(R.id.tab_activity_progress_effect_text);
//        effect_view = rootView.findViewById(R.id.tab_activity_progress_effect_view);

        daily_layout = (LinearLayout) rootView.findViewById(R.id.tab_activity_progress_result_layout);
        daily_layout.setOnClickListener(this);
        daily_text = (TextView) rootView.findViewById(R.id.tab_activity_progress_result_text);
//        result_view = rootView.findViewById(R.id.tab_activity_progress_result_view);

        week_layout = (LinearLayout) rootView.findViewById(R.id.tab_activity_progress_performance_layout);
        week_layout.setOnClickListener(this);
        week_text = (TextView) rootView.findViewById(R.id.tab_activity_progress_performance_text);
//        performance_view = rootView.findViewById(R.id.tab_activity_progress_performance_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_activity_progress_all_layout:
                listener.onCheckedChange(0);
                break;
            case R.id.tab_activity_progress_effect_layout:
                listener.onCheckedChange(1);
                break;
            case R.id.tab_activity_progress_result_layout:
                listener.onCheckedChange(2);
                break;
            case R.id.tab_activity_progress_performance_layout:
                listener.onCheckedChange(3);
                break;
        }
    }

    /**
     * select  view
     *
     * @param position
     */
    public void setChecked(int position) {
        switch (position) {
            /*全部*/
            case 0:
                setViewChange("#99ccff", true, "#878787", false, "#878787", false, "#878787", false, "#878787", false);
                break;
            /*邀请中*/
            case 1:
                setViewChange("#878787", false, "#99ccff", true, "#878787", false, "#878787", false, "#878787", false);
                break;
            /*判定中*/
            case 2:
                setViewChange("#878787", false, "#878787", false, "#99ccff", true, "#878787", false, "#878787", false);
                break;
            /*履约中*/
            case 3:
                setViewChange("#878787", false, "#878787", false, "#878787", false, "#99ccff", true, "#878787", false);
                break;
            /*完成*/
            case 4:
                setViewChange("#878787", false, "#878787", false, "#878787", false, "#878787", false, "#99ccff", true);
                break;
        }
    }

    /**
     *                                  change  view
     * @param allText
     * @param sltAll
     * @param effectText
     * @param sltEffect
     * @param resultText
     * @param sltResult
     * @param performanceText
     * @param sltPerformance
     * @param finishText
     * @param sltFinish
     */
    public void setViewChange(String allText, Boolean sltAll, String effectText, Boolean sltEffect, String resultText, Boolean sltResult,
                              String performanceText,Boolean sltPerformance,String finishText,Boolean sltFinish) {
        all_text.setTextColor(Color.parseColor(allText));
//        if (sltAll) {
//            all_view.setVisibility(View.VISIBLE);
//        } else {
//            all_view.setVisibility(View.INVISIBLE);
//        }

        mine_text.setTextColor(Color.parseColor(effectText));
//        if (sltEffect) {
//            effect_view.setVisibility(View.VISIBLE);
//        } else {
//            effect_view.setVisibility(View.INVISIBLE);
//        }

        daily_text.setTextColor(Color.parseColor(resultText));
//        if (sltResult) {
//            result_view.setVisibility(View.VISIBLE);
//        } else {
//            result_view.setVisibility(View.INVISIBLE);
//        }

        week_text.setTextColor(Color.parseColor(performanceText));
//        if (sltPerformance) {
//            performance_view.setVisibility(View.VISIBLE);
//        } else {
//            performance_view.setVisibility(View.INVISIBLE);
//        }
    }

    private onCheckedChangeListener listener;
    public interface onCheckedChangeListener {
        void onCheckedChange(int position);
    }
    public void setOncheckedChangeListener(onCheckedChangeListener listener) {
        this.listener = listener;
    }
}
