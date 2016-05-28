package com.comvee.tnb.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SquareLayout extends LinearLayout {

	@SuppressLint("NewApi")
	public SquareLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
	}

	public SquareLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SquareLayout(Context context) {
		this(context, null);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

	}

}
