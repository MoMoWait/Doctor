package com.comvee.tnb.pedometer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.comvee.tnb.R;


/**
 * Created by zhang on 2016/5/25.
 */
public class RoundProgressBar extends HorizontalProgressBar {
    private final static int DEFAULT_RADIUS=80;
    private int mRadius=dp2px(DEFAULT_RADIUS);

    public RoundProgressBar(Context context) {
        super(context,null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        obtainStyleAttributeSets(attrs);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyleAttributeSets(attrs);
    }

    private void obtainStyleAttributeSets(AttributeSet attrs){
        TypedArray typedArray=getContext().obtainStyledAttributes(attrs
                , R.styleable.RoundPedometerProgressBar);
        mRadius=(int)typedArray.getDimension(R.styleable.RoundPedometerProgressBar_p_radius,DEFAULT_RADIUS);
        mReachedProgressBar=(int)(mReachedProgressBar*2.5f);
        typedArray.recycle();
        mTextSize=sp2px(14);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode= MeasureSpec.getMode(heightMeasureSpec);
        int widthMode= MeasureSpec.getMode(widthMeasureSpec);
        int paintWidth= Math.max(mReachedProgressBar,mUnReachedProgressBar);
        if (heightMode != MeasureSpec.EXACTLY) {

            int exceptHeight = (getPaddingTop() + getPaddingBottom()
                    + mRadius * 2 + paintWidth);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(exceptHeight,
                    MeasureSpec.EXACTLY);
        }
        if (widthMode != MeasureSpec.EXACTLY) {
            int exceptWidth =  (getPaddingLeft() + getPaddingRight()
                    + mRadius * 2 + paintWidth);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(exceptWidth,
                    MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        String text=getProgress()+"步";
        float textWidth=mPaint.measureText(text);
        float textHeight=(mPaint.ascent()+mPaint.descent())/2;

        canvas.save();
        canvas.translate(getPaddingLeft(),getPaddingTop());
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mUnReachedBarColor);
        mPaint.setStrokeWidth(mUnReachedProgressBar);
        canvas.drawCircle(mRadius,mRadius,mRadius,mPaint);
        mPaint.setColor(mReachedBarColor);
        mPaint.setStrokeWidth(mReachedProgressBar);
        float sweepAngle=getProgress()*1.0f/getMax()*360;
        canvas.drawArc(new RectF(0,0,mRadius*2,mRadius*2),-90,
                sweepAngle,false,mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(text,mRadius-textWidth/2,mRadius-textHeight,mPaint);
        canvas.restore();
    }
}
