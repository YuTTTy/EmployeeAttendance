package com.xahaolan.emanage.view.comments;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * Created by aiodiy on 2017/5/8.     custom  horizontal  Scrollview
 */

public class MyHorizontalScrollView extends HorizontalScrollView{
    public MyHorizontalScrollView(Context context) {
        super(context);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = (View) getChildAt(getChildCount() - 1);
        // 如果为0，证明滑动到最左边
        if (view.getLeft() - getScrollX() == 0) {
            onScrollListener.onLeft();
            // 如果为0证明滑动到最右边
        } else if ((view.getRight() - (getWidth() + getScrollX())) == 0) {
            onScrollListener.onRight();
            // 说明在中间
        } else {
            onScrollListener.onScroll();
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    /**
     * 定义接口
     *
     * @author admin
     */
    public interface OnScrollListener1 {
        void onRight();

        void onLeft();

        void onScroll();
    }

    private OnScrollListener1 onScrollListener;

    public void setOnScrollListener(OnScrollListener1 onScrollListener) {
        this.onScrollListener = onScrollListener;
    }
}
