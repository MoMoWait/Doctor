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
import android.util.AttributeSet;
import android.view.View;

import com.comvee.util.Util;

public class IndexBloodCircleView extends View
{

	private Bitmap img = null;
	private Paint paint = new Paint(Color.BLACK);
	private Paint clearPaint = new Paint();

	public IndexBloodCircleView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public IndexBloodCircleView(Context context, AttributeSet attrs)
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

	public IndexBloodCircleView(Context context)
	{
		super(context);

	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		// super.onDraw(canvas);

		Canvas cv = new Canvas(img);
		int width = Util.dip2px(getContext(), 128);
		float radius = width / 2.3f;
		cv.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		cv.save(Canvas.ALL_SAVE_FLAG);

		RectF rect = new RectF(0, 0, width, width);
		paint.setColor(mColor);
		cv.drawArc(rect, -90, 360 * mProgress / 100f, true, paint);

//		paint.setColor(getResources().getColor(R.color.btn_orange));
//		cv.drawArc(rect, 270 + (180 * (100 - curMinPercent) / 100f), 180 * curMinPercent / 100f, true, paint);

		cv.drawCircle(width / 2, width / 2, radius, clearPaint);
//		cv.drawLine(width / 2, 0, width / 2, width, clearPaint);
		canvas.drawBitmap(img, 0, 0, null);
	}

	@Override
	public void draw(Canvas canvas)
	{
		super.draw(canvas);

	}

	private int mProgress;
	private int mColor = Color.parseColor("#adfe00");

	public void setProgress(int progress){
		this.mProgress = progress;
		invalidate();
	}
	
	public void setCircleColor(int color){
		mColor = color;
		invalidate();
	}
	
}
