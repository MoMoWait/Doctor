package com.comvee.tnb.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

public class MyGridView extends GridView {

	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyGridView(Context context) {
		super(context);
	}

	public MyGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	public int mItemCount;
	public void setItemCount(int count){
		this.mItemCount = count;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			return false;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if(this.mItemCount== 0){
			this.mItemCount = getCount();
		}
		if (getChildAt(0) != null) {
			View localView1 = getChildAt(0);
			int column = getWidth() / localView1.getWidth();
			int childCount = getChildCount();
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
			localPaint.setStyle(Paint.Style.FILL);
			localPaint2.setStyle(Paint.Style.FILL);
			localPaint.setStrokeWidth(1);
			localPaint2.setStrokeWidth(1);
			localPaint.setColor(Color.parseColor("#cccccc"));
			localPaint2.setColor(Color.parseColor("#cccccc"));
			for (int i = 0; i < childCount; i++) {
				View cellView = getChildAt(i);
				if(i <mItemCount){
					if ((i + 1) % column != 0) {
						canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
						canvas.drawLine(cellView.getRight() + 1, cellView.getTop(), cellView.getRight() + 1, cellView.getBottom(), localPaint2);
					}
					if ((i + 1) <= endAllcolumn) {
						canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
						canvas.drawLine(cellView.getLeft(), cellView.getBottom() + 1, cellView.getRight(), cellView.getBottom() + 1, localPaint2);
					}
				}

			}
			if (childCount % column != 0) {
				for (int j = 0; j < (column - childCount % column); j++) {
					View lastView = getChildAt(childCount - 1);
					canvas.drawLine(lastView.getRight() + lastView.getWidth() * j, lastView.getTop(), lastView.getRight() + lastView.getWidth() * j,
							lastView.getBottom(), localPaint);
					canvas.drawLine(lastView.getRight() + lastView.getWidth() * j + 1, lastView.getTop(), lastView.getRight() + lastView.getWidth()
							* j + 1, lastView.getBottom(), localPaint2);
				}
			}
		}
	}
}
