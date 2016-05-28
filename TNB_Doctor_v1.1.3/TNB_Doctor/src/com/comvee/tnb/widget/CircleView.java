package com.comvee.tnb.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class CircleView extends TextView
{
	private int mProgress;
	private Bitmap img = null;
	private Paint paint = new Paint(Color.BLACK);
	private int radius = 200;
	private int mCircleWidth = 2;

	public CircleView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public CircleView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setTextAlign(Align.CENTER);
		paint.setStrokeWidth(mCircleWidth);
		paint.setShadowLayer(mCircleWidth*2, 0, 0, paint.getColor());

		this.setGravity(Gravity.CENTER);
	}

	public CircleView(Context context)
	{
		super(context);
	}

	public void setRadius(int radius)
	{
		img = Bitmap.createBitmap(radius * 2, radius * 2, Config.ARGB_8888);
		this.radius = radius;
		postInvalidate();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if (img == null)
			return;
		Canvas cv = new Canvas(img);
		cv.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		cv.save(Canvas.ALL_SAVE_FLAG);
		if (mProgress == 0)
			mProgress = 100;

		int offX = mCircleWidth + 2;
		RectF rect = new RectF(offX, offX, radius * 2 - offX, radius * 2 - offX);
		cv.drawArc(rect, 0, mProgress * 360 / 100f, true, paint);
		canvas.drawBitmap(img, 0, 0, null);
	}

	@Override
	public void draw(Canvas canvas)
	{
		super.draw(canvas);
	}

	public void setProgress(int progress)
	{
		mProgress = progress;
		postInvalidate();
	}

	public void setCircleColor(int color)
	{
		paint.setColor(color);
		paint.setShadowLayer(mCircleWidth*2, 0, 0, paint.getColor());
		postInvalidate();
	}

}
