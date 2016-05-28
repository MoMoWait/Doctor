package com.comvee.tnb.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import com.comvee.tnb.R;
import com.comvee.util.Util;

public class HeartView extends View
{

	private Paint clearPaint = new Paint();
	private Path mPath = new Path();
	private int index;
	private Paint paint = new Paint();
	public int iScreenWidth;
	public int iTotalWidth;

	public HeartView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public void addPoints(ArrayList<Integer> points)
	{

		if (mPath.isEmpty() && null != points && !points.isEmpty())
		{
			mPath.moveTo(iScreenWidth + index * 1.5f, points.get(0) * 1.5f);
			points.remove(0);
			index++;
		}
		float x = 0;
		for (Integer i : points)
		{
			x = iScreenWidth + index * 1.5f;
			if (x > iTotalWidth)
				break;
			mPath.lineTo(x, i * 1.5f);
			index++;
		}
		postInvalidate();
	}

	public HeartView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		iScreenWidth = Util.getScreenWidth(getContext());
		iTotalWidth = getContext().getResources().getDimensionPixelSize(R.dimen.heart_width);
		int lineWidth = Util.dip2px(getContext(), 10);
		clearPaint.setAlpha(0);
		// clearPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		clearPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		clearPaint.setAntiAlias(true);
		clearPaint.setStrokeWidth(lineWidth);

		paint.setStyle(Style.STROKE);
		paint.setColor(getResources().getColor(android.R.color.black));
		paint.setStrokeWidth(2);
		paint.setAntiAlias(true);

	}

	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
	}

	public HeartView(Context context)
	{
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawText("心电图", 100, 100, paint);
		canvas.drawPath(mPath, paint);
	}

	@Override
	public void draw(Canvas canvas)
	{
		super.draw(canvas);
	}

	public int getRateWidth()
	{
		return (int) (index * 1.5f);
	}

}
