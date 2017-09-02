package com.xahaolan.emanage.view.comments;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by aiodiy on 2017/5/7.
 */

public class InnerGridView extends GridView {
    public InnerGridView(Context context) {
        super(context);
    }

    public InnerGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InnerGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expanSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expanSpec);

    }
}
