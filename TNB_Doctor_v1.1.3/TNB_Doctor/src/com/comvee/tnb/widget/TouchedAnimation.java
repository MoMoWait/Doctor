package com.comvee.tnb.widget;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class TouchedAnimation
{
	public static final OnTouchListener TouchLight = new OnTouchListener()
	{
		public final float[] BT_SELECTED = new float[]
		{ 1, 0, 0, 0, 50, 0, 1, 0, 0, 50, 0, 0, 1, 0, 50, 0, 0, 0, 1, 0 };
		public final float[] BT_NOT_SELECTED = new float[]
		{ 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			Drawable draw = v.getBackground();
			if (draw != null)
			{
				draw.mutate();
				ColorMatrix cm = new ColorMatrix();
				cm.setSaturation(0);
				ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm);
				draw.setColorFilter(cf);
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					v.setBackgroundDrawable(draw);
					;
				} else if (event.getAction() == MotionEvent.ACTION_UP)
				{
					v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));
					v.setBackgroundDrawable(draw);
				}
			}
			return false;
		}
	};

}
