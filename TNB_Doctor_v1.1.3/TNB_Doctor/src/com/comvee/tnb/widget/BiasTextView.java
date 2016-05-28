package com.comvee.tnb.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class BiasTextView extends TextView {
	public BiasTextView(Context context) {
		super(context);
	}

	public BiasTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 倾斜度45,上下左右居中
		canvas.rotate(45, getMeasuredWidth()*5/7 , getMeasuredHeight()*3/5);
		super.onDraw(canvas);
	}

}
