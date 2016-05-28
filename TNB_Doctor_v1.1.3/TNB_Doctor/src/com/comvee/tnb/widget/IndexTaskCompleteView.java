package com.comvee.tnb.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.comvee.tnb.R;
import com.comvee.util.Util;

public class IndexTaskCompleteView extends View
{

	private Bitmap img = null;
	Paint paint = new Paint(Color.BLACK);

	Paint clearPaint = new Paint();

	public IndexTaskCompleteView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public IndexTaskCompleteView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		int width = Util.dip2px(getContext(), 128);
		img = Bitmap.createBitmap(width, width, Config.ARGB_8888);

		// this.setAlpha(0);
		int lineWidth = Util.dip2px(getContext(), 10);
		paint.setAntiAlias(true);
		clearPaint.setAlpha(0);
		// clearPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		clearPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		clearPaint.setAntiAlias(true);
		clearPaint.setStrokeWidth(lineWidth);
	}

	public IndexTaskCompleteView(Context context)
	{
		super(context);

	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		// super.onDraw(canvas);

		if (curMaxPercent == -1)
		{
			curMaxPercent = 100;
		}

		Canvas cv = new Canvas(img);
		int width = Util.dip2px(getContext(), 128);
		float radius = width / 2.3f;
		cv.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		cv.save(Canvas.ALL_SAVE_FLAG);

		RectF rect = new RectF(0, 0, width, width);
		paint.setColor(getResources().getColor(R.color.theme_color_green));
		cv.drawArc(rect, 90, 180 * curMaxPercent / 100f, true, paint);

		paint.setColor(getResources().getColor(R.color.btn_orange));
		cv.drawArc(rect, 270 + (180 * (100 - curMinPercent) / 100f), 180 * curMinPercent / 100f, true, paint);

		cv.drawCircle(width / 2, width / 2, radius, clearPaint);
		cv.drawLine(width / 2, 0, width / 2, width, clearPaint);
		canvas.drawBitmap(img, 0, 0, null);
	}

	@Override
	public void draw(Canvas canvas)
	{
		super.draw(canvas);

	}

	private int maxPercent;
	private int minPercent;

	private int curMaxPercent;
	private int curMinPercent;

	private int mStatus;// 1、上2、下

	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{

			boolean b = false;
			if (mStatus == 1)
			{
				if (100 > curMinPercent)
				{
					curMinPercent++;
					b = true;
				}
				if (100 > curMaxPercent)
				{
					curMaxPercent++;
					b = true;
				}
				if (!b)
				{
					mStatus = 2;
					b = true;
				}
			} else if (mStatus == 2)
			{
				if (minPercent != curMinPercent)
				{
					curMinPercent--;
					b = true;
				}
				if (maxPercent != curMaxPercent)
				{
					curMaxPercent--;
					b = true;
				}
			}

			if (b)
			{
				invalidate();
				mHandler.sendEmptyMessageDelayed(1, 2);
			} else
			{
				setPercent(maxPercent, minPercent);
			}
		};
	};

	public void postPercent(int maxPercent, int minPercent)
	{
		mStatus = 1;

		curMaxPercent = 0;
		curMinPercent = 0;
		this.minPercent = minPercent;
		this.maxPercent = maxPercent==-1?100:maxPercent;
		mHandler.sendEmptyMessage(1);

	}

	public void setPercent(int maxPercent, int minPercent)
	{
		
		
		this.curMinPercent = minPercent;
		this.curMaxPercent = maxPercent==-1?100:maxPercent;
		invalidate();
	}

	public int getMaxPercent()
	{
		return this.curMaxPercent;
	}

	public int getMinPercent()
	{
		return this.minPercent;
	}

}
