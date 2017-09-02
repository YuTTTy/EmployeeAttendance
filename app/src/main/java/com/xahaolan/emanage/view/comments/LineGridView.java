package com.xahaolan.emanage.view.comments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

/**
 * @author KAMI918           自定义gridview，添加网格线
 * 
 *         通过重写dispatchDraw 利用Paint进行绘制
 */
public class LineGridView extends GridView {
	public LineGridView(Context context) {
		super(context);
	}

	public LineGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LineGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expanSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expanSpec);

	}
	/* 
     *                     画网格线
	 */
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (getChildAt(0) != null) {
			View localView1 = getChildAt(0);
			/*列数*/
			int column = getWidth() / localView1.getWidth();
			/*item总数*/
			int childCount = getChildCount();
			
			/*算出共有多少行*/
			int row = 0;
			if (childCount % column == 0) {
				row = childCount / column;
			} else {
				row = childCount / column + 1;
			}
			
			int endAllcolumn = (row - 1) * column;
			
			Paint localPaint, localPaint2;
			localPaint = new Paint();
			localPaint2 = new Paint();
			localPaint.setStyle(Paint.Style.STROKE);
			localPaint2.setStyle(Paint.Style.STROKE);
			localPaint.setStrokeWidth(1);
			localPaint2.setStrokeWidth((float) 0.1);
			localPaint.setColor(Color.parseColor("#e2e2e2"));
			localPaint2.setColor(Color.parseColor("#e2e2e2"));
			for (int i = 0; i < childCount; i++) {
				View cellView = getChildAt(i);
				if ((i + 1) % column != 0) {
					canvas.drawLine(cellView.getRight(), cellView.getTop(),
							cellView.getRight(), cellView.getBottom(),
							localPaint);
					canvas.drawLine(cellView.getRight() + 1, cellView.getTop(),
							cellView.getRight() + 1, cellView.getBottom(),
							localPaint2);
				}
				if ((i + 1) <= endAllcolumn) {
					canvas.drawLine(cellView.getLeft(), cellView.getBottom(),
							cellView.getRight(), cellView.getBottom(),
							localPaint);
					canvas.drawLine(cellView.getLeft(),
							cellView.getBottom() + 1, cellView.getRight(),
							cellView.getBottom() + 1, localPaint2);
				}
			}
			if (childCount % column != 0) {
				for (int j = 0; j < (column - childCount % column); j++) {
					View lastView = getChildAt(childCount - 1);
					canvas.drawLine(lastView.getRight() + lastView.getWidth()
							* j, lastView.getTop(), lastView.getRight()
							+ lastView.getWidth() * j, lastView.getBottom(),
							localPaint);
					canvas.drawLine(lastView.getRight() + lastView.getWidth()
							* j + 1, lastView.getTop(), lastView.getRight()
							+ lastView.getWidth() * j + 1,
							lastView.getBottom(), localPaint2);
				}
			}
		}
	}
}