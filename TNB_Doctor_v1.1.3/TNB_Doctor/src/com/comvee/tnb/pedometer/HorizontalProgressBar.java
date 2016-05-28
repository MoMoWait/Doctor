package com.comvee.tnb.pedometer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.comvee.tnb.R;


/**
 * Created by zhang on 2016/5/25.
 */
public class HorizontalProgressBar extends ProgressBar {
    private static final int DEFAULT_TEXT_SIZE = 10;
    private static final int DEFAULT_TEXT_COLOR = 0XFFFC00D1;
    private static final int DEFAULT_COLOR_UNREACHED_COLOR = 0xFFd3d6da;
    private static final int DEFAULT_HEIGHT_REACHED_PROGRESS_BAR = 2;
    private static final int DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR = 2;
    private static final int DEFAULT_SIZE_TEXT_OFFSET = 10;
    protected Paint mPaint=new Paint();
    /**
     * 文本大小
     */
    protected int mTextSize=sp2px(DEFAULT_TEXT_SIZE);
    /**
     * 文本颜色
     */
    protected int mTextColor=DEFAULT_TEXT_COLOR;
    /**
     * 进度到达的（那段）高度
     */
    protected int mReachedProgressBar=dp2px(DEFAULT_HEIGHT_REACHED_PROGRESS_BAR);
    /**
     * 进度未达到的那段高度
     */
    protected int mUnReachedProgressBar=dp2px(DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR);
    /**
     * 进度达到的颜色
     */
    protected int mReachedBarColor=DEFAULT_TEXT_COLOR;
    /**
     * 进度未达到的颜色
     */
    protected int mUnReachedBarColor=DEFAULT_COLOR_UNREACHED_COLOR;
    protected int mTextOffeset=DEFAULT_SIZE_TEXT_OFFSET;
    protected static final int VISIBLE=0;
    protected boolean isDrawText=true;
    protected int mRealWidth;

    public HorizontalProgressBar(Context context) {
        super(context,null);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs,0);
        obtainStyleAttributes(attrs);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    private void obtainStyleAttributes(AttributeSet attrs){
        TypedArray attributes=getContext().obtainStyledAttributes(attrs,
                R.styleable.HorizontalProgressBar);
        mTextColor=attributes.getColor(R.styleable
                .HorizontalProgressBar_p_text_color,DEFAULT_TEXT_COLOR);
        mTextSize=(int)attributes.getDimension(R.styleable
                .HorizontalProgressBar_p_text_size,mTextSize);
        mReachedBarColor=attributes.getColor(R.styleable
                .HorizontalProgressBar_p_reached_color,mTextColor);
        mUnReachedBarColor=attributes.getColor(R.styleable
                .HorizontalProgressBar_p_unreached_color,DEFAULT_COLOR_UNREACHED_COLOR);
        mReachedProgressBar=(int)attributes.getDimension(R.styleable
                .HorizontalProgressBar_p_reached_bar_height,DEFAULT_HEIGHT_REACHED_PROGRESS_BAR);
        mUnReachedProgressBar=(int)attributes.getDimension(R.styleable
                .HorizontalProgressBar_p_unreached_bar_height,DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR);
        mTextOffeset=(int)attributes.getDimension(R.styleable
                .HorizontalProgressBar_p_text_offset,DEFAULT_SIZE_TEXT_OFFSET);
        int textVisible=attributes.getInt(R.styleable
                .HorizontalProgressBar_p_text_visibility,VISIBLE);
        if (textVisible!=VISIBLE){
            isDrawText=false;
        }else{
            isDrawText=true;
        }
        attributes.recycle();
    }
    protected int dp2px(int dpVal){
        return (int) TypedValue.applyDimension(TypedValue
                .COMPLEX_UNIT_DIP,dpVal,getResources().getDisplayMetrics());
    }
    protected int sp2px(int spVal){
        return (int) TypedValue.applyDimension(TypedValue
                .COMPLEX_UNIT_SP,spVal,getResources().getDisplayMetrics());
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height= MeasureSpec.getMode(heightMeasureSpec);
        if (height!= MeasureSpec.EXACTLY){
            float textHeight=(mPaint.descent()+mPaint.ascent());
            int exceptHeight=(int)(getPaddingTop()+getPaddingBottom()+ Math
                    .max(Math.max(mReachedProgressBar,
                            mUnReachedProgressBar), Math.abs(textHeight)));
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(exceptHeight,
                    MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        //画笔平移到指定paddingLeft， getHeight() / 2位置，注意以后坐标都为以此为0，0
        canvas.translate(getPaddingLeft(),getHeight()/2);
        boolean noNeedBg=false;
        //当前进度和总值的比例
        float radio=getProgress()*1.f/getMax();
        //已到达的宽度
        float progressPosX=(int)(mRealWidth*radio);
        //绘制的文本
        String text = getProgress() + "步";

        //拿到字体的宽度和高度
        float textWidth = mPaint.measureText(text);
        float textHeight = (mPaint.descent() + mPaint.ascent()) / 2;

        //如果到达最后，则未到达的进度条不需要绘制
        if (progressPosX + textWidth > mRealWidth)
        {
            progressPosX = mRealWidth - textWidth;
            noNeedBg = true;
        }

        // 绘制已到达的进度
        float endX = progressPosX -mTextOffeset / 2;
        if (endX > 0)
        {
            mPaint.setColor(mReachedBarColor);
            mPaint.setStrokeWidth(mReachedProgressBar);
            canvas.drawLine(0, 0, endX, 0, mPaint);
        }

        // 绘制文本
        if (isDrawText)
        {
            mPaint.setColor(mTextColor);
            canvas.drawText(text, progressPosX, -textHeight, mPaint);
        }

        // 绘制未到达的进度条
        if (!noNeedBg)
        {
            float start = progressPosX + mTextOffeset / 2 + textWidth;
            mPaint.setColor(mUnReachedBarColor);
            mPaint.setStrokeWidth(mUnReachedProgressBar);
            canvas.drawLine(start, 0, mRealWidth, 0, mPaint);
        }

        canvas.restore();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRealWidth=w-getPaddingRight()-getPaddingLeft();
    }
}
