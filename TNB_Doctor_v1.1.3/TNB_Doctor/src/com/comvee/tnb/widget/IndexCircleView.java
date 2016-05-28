package com.comvee.tnb.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.TextView;

public class IndexCircleView extends TextView {
	private int mProgress;
	private Bitmap img;
	private Bitmap tempImg = null;
	private Paint paint = new Paint();
	private RectF oval;
	private Canvas cvs;

	public IndexCircleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public IndexCircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IndexCircleView(Context context) {
		super(context);
		onFinishInflate();
	}

	public void setImageRes(final int res) {
		ViewTreeObserver vto = getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				getViewTreeObserver().removeOnPreDrawListener(this);
				int height = getMeasuredHeight();
				int width = getMeasuredWidth();
				if (width == 0) {
					return true;
				}

				if (img != null) {
					img.recycle();
				}
				if (res != 0) {
					Bitmap img1 = BitmapFactory.decodeResource(getResources(), res);
					tempImg = Bitmap.createBitmap(width, height, Config.ARGB_8888);
					img = Bitmap.createScaledBitmap(img1, width, height, true);
					cvs = new Canvas(tempImg);
					oval = new RectF(0, 0, width, height);
					paint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
					paint.setAntiAlias(true);
					img1.recycle();
				} else {
				}

				return true;
			}
		});

	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (tempImg == null) {
			return;
		}
		float arc = 150 + 2.5f * mProgress;
		canvas.drawColor(Color.TRANSPARENT, Mode.DST_OUT);
		// cvs.drawArc(oval, 0, 360, true, paint);
		cvs.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		// cvs.drawBitmap(img, src, oval, null);
		cvs.drawBitmap(img, 0, 0, null);
		// cvs.drawBitmap(img, 0, 0, null);
		cvs.drawArc(oval, arc, 500 - arc, true, paint);
		canvas.drawBitmap(tempImg, 0, 0, null);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
	}

	public void setProgress(int progress) {
		mProgress = progress;
		postInvalidate();
	}

}
