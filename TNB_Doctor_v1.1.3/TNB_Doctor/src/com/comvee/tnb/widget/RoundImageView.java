package com.comvee.tnb.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.comvee.tnb.R;

/**
 * 圆环/圆
 * 
 * @author PXL
 * 
 */
public class RoundImageView extends ImageView {
	private Paint mBitmapPaint;
	private int type = 0;// 类型，0表示圆形，1表示圆角，2表示圆环
	private float mRadius;// 圆半径
	private float ringWidth = 0;// 圆环宽度
	private float ringWidthDefault = 4;// 圆环默认宽度
	private float roundRadus;// 圆角半径
	private float roundRadusDefault = 5;// 圆角默认
	private RectF mRoundRect;
	/**
	 * 3x3 矩阵，主要用于缩小放大
	 */
	private Matrix mMatrix;
	/**
	 * 渲染图像，使用图像为绘制图形着色
	 */
	private BitmapShader mBitmapShader;
	private int ringColor = 0xff85cfff;// 环背景
	private int ringColorDefault = 0xff85cfff;
	/**
	 * view的宽度
	 */
	private int mWidth;

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mMatrix = new Matrix();
		mBitmapPaint = new Paint();
		mBitmapPaint.setAntiAlias(true);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
		int n = a.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.RoundImageView_type:
				type = a.getInt(R.styleable.RoundImageView_type, 0);
				break;
			case R.styleable.RoundImageView_ringColor1:
				ringColor = a.getColor(attr, ringColorDefault);
				break;
			case R.styleable.RoundImageView_ringWidth:
				ringWidth = a.getDimension(attr, ringWidthDefault);
				break;
			case R.styleable.RoundImageView_roundRadus:
				roundRadus = a.getDimension(attr, roundRadusDefault);
				break;
			default:
				break;
			}
		}
		a.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
		mRadius = 1.0f * mWidth / 2 - ringWidth;
		setMeasuredDimension(mWidth, mWidth);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 圆角图片的范围
		if (type == 1)
			mRoundRect = new RectF(0, 0, getWidth(), getHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (getDrawable() == null) {
			return;
		}
		setUpShader();
		switch (type) {
		case 0:
			canvas.drawCircle(ringWidth + mRadius, ringWidth + mRadius, mRadius, mBitmapPaint);
			break;
		case 1:
			canvas.drawRoundRect(mRoundRect, roundRadus, roundRadus, mBitmapPaint);
			break;
		case 2:
			Paint ringPaint = new Paint();
			RectF ringBoundRectF = new RectF();
			ringBoundRectF.set(ringWidth / 2, ringWidth / 2, mWidth - ringWidth / 2, mWidth - ringWidth / 2);
			ringPaint.setColor(ringColor);
			ringPaint.setAntiAlias(true);
			ringPaint.setStyle(Style.STROKE);
			ringPaint.setStrokeWidth(ringWidth);
			canvas.drawArc(ringBoundRectF, 360, 360, false, ringPaint);
			canvas.drawCircle(ringWidth + mRadius, ringWidth + mRadius, mRadius + 2, mBitmapPaint);
			break;
		default:
			break;
		}
	}

	/**
	 * 初始化BitmapShader
	 */
	private void setUpShader() {
		Drawable drawable = getDrawable();
		if (drawable == null) {
			return;
		}
		Bitmap bmp = drawableToBitamp(drawable);
		mBitmapShader = new BitmapShader(bmp, TileMode.CLAMP, TileMode.CLAMP);
		float scale = 1.0f;
		if (type == 0 || type == 2) {
			int bSize = Math.min(bmp.getWidth(), bmp.getHeight());
			scale = mWidth * 1.0f / bSize;
		} else if (type == 1) {
			scale = Math.max(getWidth() * 1.0f / bmp.getWidth(), getHeight() * 1.0f / bmp.getHeight());
		}
		mMatrix.setScale(scale, scale);
		mBitmapShader.setLocalMatrix(mMatrix);
		mBitmapPaint.setShader(mBitmapShader);
	}

	/**
	 * drawable转bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	private Bitmap drawableToBitamp(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bd = (BitmapDrawable) drawable;
			return bd.getBitmap();
		}
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
		return bitmap;
	}

}