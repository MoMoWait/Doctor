package com.comvee.tnb.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.comvee.tnb.R;
import com.comvee.tool.ResUtil;
public class TasksCompletedView extends View {

	private Paint wRingPaint;
	private Paint waiRingPaint;

	// 画实心圆的画笔
	private Paint mCirclePaint;
	// 画圆环的画笔
	private Paint mRingPaint;
	// 画字体的画笔
	private Paint mTextPaint;
	// 圆形颜色
	private int mCircleColor;
	// 圆环颜色
	private int mRingColor;
	// 半径
	private float mRadius;
	// 圆环半径
	private float mRingRadius;
	// 圆环宽度
	private float mStrokeWidth;
	// 圆心x坐标
	private int mXCenter;
	// 圆心y坐标
	private int mYCenter;
	// 字的长度
	private float mTxtWidth;
	// 字的高度
	private float mTxtHeight;
	// 总进度
	private int mTotalProgress = 100;
	// 当前进度
	private int mProgress;
	// 画当前值
	private int drawProgress;
	// 我的目标
	// private int mTarget;
	private int mCurrent;
	private Context context;
	private Resources res;
	private Paint mTextPaint1;
	// 半径
	private float waiRadius;

	public TasksCompletedView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获取自定义的属性
		this.context = context;
		initAttrs(context, attrs);
		initVariable();

	}

	public TasksCompletedView(Context context) {
		super(context);
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.TasksCompletedView, 0, 0);
		mRadius = typeArray.getDimension(
				R.styleable.TasksCompletedView_radius, 100);
		waiRadius= typeArray.getDimension(
				R.styleable.TasksCompletedView_radius, 145);
		mStrokeWidth = typeArray.getDimension(
				R.styleable.TasksCompletedView_strokeWidth, 30);
		mCircleColor = typeArray.getColor(
				R.styleable.TasksCompletedView_circleColor, 0xFFFFFFFF);
		mRingColor = typeArray.getColor(
				R.styleable.TasksCompletedView_ringColor, 0xFFFFFFFF);

		mRingRadius = mRadius + mStrokeWidth / 2;
	}

	private void initVariable() {
		res = this.context.getResources();

		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setColor(mCircleColor);
		mCirclePaint.setStyle(Paint.Style.FILL);

		mRingPaint = new Paint();
		mRingPaint.setAntiAlias(true);
		mRingPaint.setColor(mRingColor);
		mRingPaint.setStyle(Paint.Style.STROKE);
		mRingPaint.setStrokeWidth(mStrokeWidth);

		wRingPaint = new Paint();
		wRingPaint.setAntiAlias(true);
		wRingPaint.setColor(res.getColor(R.color.t_default));
		wRingPaint.setStyle(Paint.Style.STROKE);
		wRingPaint.setStrokeWidth(mStrokeWidth);

		
		waiRingPaint = new Paint();
		waiRingPaint.setAntiAlias(true);
		waiRingPaint.setColor(res.getColor(R.color.t_default2));
		waiRingPaint.setStyle(Paint.Style.FILL);

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Paint.Style.FILL);
		res = this.context.getResources();
		mTextPaint.setColor(res.getColor(R.color.theme_color_green));
		mTextPaint.setTextSize(mRadius / 2);

		mTextPaint1 = new Paint();
		mTextPaint1.setAntiAlias(true);
		mTextPaint1.setStyle(Paint.Style.FILL);

		mTextPaint1.setColor(res.getColor(R.color.title_txt_color));
		mTextPaint1.setTextSize(mRadius / 4);

		FontMetrics fm = mTextPaint.getFontMetrics();
		mTxtHeight = (int) Math.ceil(fm.descent - fm.ascent);

	}

	@Override
	protected void onDraw(Canvas canvas) {

		mXCenter = getWidth() / 2;
		mYCenter = getHeight() / 2;

		canvas.drawCircle(mXCenter, mYCenter, waiRadius, waiRingPaint);//外圆
		canvas.drawCircle(mXCenter, mYCenter, mRadius, mCirclePaint);//内圆

		if (mProgress >= 0) {
			RectF oval = new RectF();
			oval.left = (mXCenter - mRingRadius);
			oval.top = (mYCenter - mRingRadius);
			oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
			oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
			
	//		for(int i=0;i<30;i++){
			canvas.drawArc(oval, -90, 360, false, wRingPaint); //	
		//	}
			
			canvas.drawArc(oval, -90,
					((float) mProgress / mTotalProgress) * 360, false,
					mRingPaint); //

			String txt = drawProgress+"" ;
			mTxtWidth = mTextPaint.measureText(txt, 0, txt.length());

			
			String str=ResUtil.getString(R.string.calorie);
			float strWidth=mTextPaint1.measureText(str, 0, str.length());
			// 当前值
			canvas.drawText(str, mXCenter - strWidth/2 , mYCenter
					- 3*mTxtHeight/4 , mTextPaint1);
			// 当前值
			canvas.drawText(drawProgress + "", mXCenter - mTxtWidth / 2 ,
					mYCenter + mTxtHeight / 4, mTextPaint);

		}
	}

	// 当前值 目标值
	public void setProgress(int current,int tmp, int target) {
		drawProgress=tmp;
		mProgress = current;
		mTotalProgress = target;
		postInvalidate();
	}

}
