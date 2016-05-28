package com.comvee.tnb.widget;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.comvee.tnb.R;
import com.comvee.util.Util;

/**
 * 趋势图
 * 
 * @author friendlove
 * 
 */
public final class Tendency extends View implements Runnable
{

	public static final int TEXT_SIZE = 11;
	// public static final String COLOR_LINE = "#F5B302";
	public static final int COLOR_LINE = Color.parseColor("#F5B302");
	public static final int COLOR_TEXT = Color.parseColor("#bfbfbf");
	// public static final String COLOR_LINE = "#000000";

	public int mPointSpace = 1;

	public double minValue = 1;
	private double maxValue = 1;
	// 一度所在用的距离
	private double mper_temp = 1;

	private double pointRadius = 4f;
	private double cellWidth;
	private double cellHeight;
	private double originY;
	private double originX;
	private double coordX;
	private DecimalFormat df = new DecimalFormat("#.#");
	private static Paint paint;
	private static Paint textPaint;
	private static Paint dottedPaint;
	private boolean flag;
	private List<PointD> drawPoints;
	private static Bitmap pointImg;
	private static Bitmap pointImg1;
	private static Bitmap pointImgS;
	private static Bitmap pointImgS1;
	private boolean beChange = false;

	private List<Float> verCoordList;// 纵坐标值
	private List<String> horCoordList;// 横坐标值
	private ArrayList<TendencyLine> mCoordLine = new ArrayList<Tendency.TendencyLine>();

	private double iMax;
	private double iMin;

	@Override
	protected void onDraw(Canvas canvas)
	{

		super.onDraw(canvas);

		// 判断高宽是否被渲染
		if (cellWidth <= 0)
		{
			initValue();
			invalidate();
			return;
		} else if (beChange)
		{
			beChange = false;
			load_data();
			startAnim();
		}

		// 纵线
		int size = horCoordList.size();
		int offx = (int) Dip2Px(5);
		for (int i = 0; i < size; i++)
		{
			final double startX = originX + i * cellWidth;
			if (i % mPointSpace == 0)
			{ // 画实线和横坐标刻度
				if (i < size)
				{
					final String value = String.valueOf(horCoordList.get(i));
					canvas.drawText(value, (float) startX - offx * 3, (float) originY + TEXT_SIZE + offx, textPaint);
				}
				// 纵线
				 canvas.drawLine((float) startX, getPaddingTop(),
				 (float) startX, (float) originY, textPaint);
			}

		}

		// 横线
		final int verCoordCount = verCoordList.size();
		final double endX = originX + (size - 1) * cellWidth;

		for (int i = 0; i < verCoordCount; i++)
		{
			final double startY = originY - cellHeight * i;

			// textPaint.setColor(Color.WHITE);
			if (i == 0)
			{// 实线
				canvas.drawLine((float) originX, (float) startY, (float) endX, (float) startY, textPaint);
			} else
			{// 虚线
				canvas.drawLine((float) originX, (float) startY, (float) endX, (float) startY, dottedPaint);
			}

			// 画纵坐标
			// textPaint.setColor(Color.BLACK);
			if (i < verCoordCount)
			{
				canvas.drawText(df.format(verCoordList.get(i)), (float) coordX, (float) startY, textPaint);
				// canvas.drawText(String.valueOf(verCoordList.get(i)), coordX,
				// startY, textPaint);
			}
		}

		/********** 画点 *********/
		for (int i = 0; i < mCoordLine.size(); i++)
		{
			mCoordLine.get(i).draw(canvas);
		}

	}

	public Tendency(Context context)
	{
		super(context);
		init();
	}

	public Tendency(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public Tendency(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public void checkValue()
	{
		this.maxValue = verCoordList.get(0);
		this.minValue = verCoordList.get(0);
		for (float value : verCoordList)
		{
			if (maxValue < value)
				maxValue = value;
			if (minValue > value)
				minValue = value;
		}
		mper_temp = (cellHeight * (verCoordList.size() - 1)) / (maxValue - minValue);
	}

	/**
	 * 设置纵坐标值
	 * 
	 * @param list
	 */
	public void setVerticalCoord(List<Float> list)
	{
		this.verCoordList = list;
	}

	/**
	 * 设置横坐标值
	 * 
	 * @param list
	 */
	public void setHorizontalCoord(List<String> list)
	{
		this.horCoordList = list;
	}

	/**
	 * 设置坐标值
	 * 
	 * @param list
	 */
	public void addCoordValues(TendencyLine line)
	{
		mCoordLine.add(line);
	}

	/**
	 * 开始动画
	 * 
	 * @param state
	 */
	public void show()
	{
		mper_temp = 1;
		maxValue = 0;
		minValue = 0;
		cellHeight = 0;
		cellWidth = 0;
		beChange = true;
		invalidate();
	}

	public void clear()
	{
		if (verCoordList != null)
		{
			verCoordList.clear();// 纵坐标值
		}
		if (horCoordList != null)
		{
			horCoordList = null;// 横坐标值
		}
		if (mCoordLine != null)
		{
			mCoordLine.clear();
		}
		mper_temp = 1;
		maxValue = 0;
		minValue = 0;
		cellHeight = 0;
		cellWidth = 0;
		beChange = true;
		invalidate();
	}

	protected void load_data()
	{
		checkValue();
		for (int i = 0; i < mCoordLine.size(); i++)
		{
			mCoordLine.get(i).init();
		}
	}

	public void setDrawLimit(int iMax, int iMin)
	{
		this.iMax = iMax;
		this.iMin = iMin;
	}

	private void init()
	{

		iMax = originY - (iMax - minValue) * mper_temp;
		iMin = originY - (iMin - minValue) * mper_temp;

		pointRadius = Dip2Px(4);
		initPaint();
		// 初始化“点”图片
		if (pointImg == null || pointImg1 == null || pointImgS == null || pointImgS1 == null)
		{
			pointImg = BitmapFactory.decodeResource(getResources(), R.drawable.tendencypoit0);// 正常值
			pointImg1 = BitmapFactory.decodeResource(getResources(), R.drawable.tendencypoit2);// 超标
			pointImgS = BitmapFactory.decodeResource(getResources(), R.drawable.tendencypoit4);
			pointImgS1 = BitmapFactory.decodeResource(getResources(), R.drawable.tendencypoit5);
		}
	}

	private void initValue()
	{
		try
		{
			if (verCoordList == null || null == horCoordList || verCoordList.isEmpty())
			{
				return;
			}

			int offx = (int) Dip2Px(40);
			final int width = (getWidth() - getPaddingRight() - getPaddingLeft() - offx);
			cellWidth = (double) width / (horCoordList.size() - 1);
			if (cellWidth <= 0)
			{
				return;
			}
			final int height = (getHeight() - getPaddingTop() - getPaddingBottom());
			cellHeight = height / verCoordList.size();
			originY = height - Dip2Px(TEXT_SIZE * 1.5f);
			coordX = getPaddingLeft();
			originX = coordX + Dip2Px(TEXT_SIZE * 2f);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void initPaint()
	{
		if (textPaint == null)
		{
			textPaint = new Paint();
			textPaint.setAntiAlias(true);
			textPaint.setTextSize(Dip2Px(TEXT_SIZE));
			textPaint.setColor(COLOR_TEXT);

			paint = new Paint();
			paint.setAntiAlias(true);// 去锯齿
			paint.setColor(COLOR_LINE);// 设置paint的颜色
			paint.setStyle(Paint.Style.STROKE);/* 设置paint的　style　为STROKE：空心 */
			paint.setStrokeWidth(2.5f);/* 设置paint的外框宽度 */
			// PathEffect effects = new CornerPathEffect(5);
			// paint.setPathEffect(effects);

			// 虚线Paint
			dottedPaint = new Paint();
			dottedPaint.setColor(COLOR_TEXT);
			dottedPaint.setAntiAlias(true);
			PathEffect effects = new DashPathEffect(new float[]
			{ 10, 10, 10, 10 }, 1);
			dottedPaint.setPathEffect(effects);
			// dottedPaint.setStrokeWidth(3);
			dottedPaint.setAlpha(100);
		}

	}

	// dip值转换到px值
	private float Dip2Px(float dipValue)
	{
		return Util.dip2px(getContext(), dipValue);
	}

	/*********** 将一条线分割成五个点 ************/
	private void addCutPoint(float beginX, float beginY, float endX, float endY)
	{
		int num = 2;
		float distanceX = (endX - beginX) / num;
		float angle = (endY - beginY) / (endX - beginX);
		for (int i = 0; i <= num; i++)
		{
			float x = distanceX * i + beginX;
			float y = beginY + distanceX * i * angle;
			drawPoints.add(new PointD(x, y));
		}
	}

	private void startAnim()
	{
		flag = true;
		new Thread(this).start();
	}

	public void run()
	{

		while (flag)
		{
			try
			{
				Tendency.this.postInvalidate();
				Thread.sleep(5);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void setPointSpace(int space)
	{
		this.mPointSpace = space < 1 ? 1 : space;
	}

	public float getTextWidth(String text, float Size)
	{
		TextPaint FontPaint = new TextPaint();
		FontPaint.setTextSize(Size);
		return FontPaint.measureText(text);
	}

	public TendencyLine createLine()
	{
		return new TendencyLine();
	}

	public class TendencyLine
	{
		private List<Float> coordValueList;
		private List<String> mHorCoordList;
		private ArrayList<PointD> points;
		private int lineColor;
		private float mMinValue = Integer.MIN_VALUE;
		private float mMaxValue = Integer.MAX_VALUE;
		private float mDataMinValue = Integer.MIN_VALUE;
		private float mDataMaxValue = Integer.MAX_VALUE;

		private Bitmap pointImgMax;
		private Bitmap pointImgMin;
		private Bitmap pointImgNormal;

		/**
		 * 设置点图片
		 */
		public void setPointBitmap(Bitmap normal, Bitmap min, Bitmap max)
		{
			this.pointImgMax = max;
			this.pointImgMin = min;
			this.pointImgNormal = normal;
		}

		public void setPointBitmap(int normalRes, int minRes, int maxRes)
		{
			pointImgMax = BitmapFactory.decodeResource(getResources(), maxRes);// 超标
			pointImgMin = BitmapFactory.decodeResource(getResources(), minRes);//
			pointImgNormal = BitmapFactory.decodeResource(getResources(), normalRes);// 正常值
		}

		public TendencyLine(ArrayList<Float> coordValueList)
		{
			this.coordValueList = coordValueList;
		}

		public void setLineColor(int color)
		{
			lineColor = color;
		}

		public float getDataListMaxValue()
		{
			return this.mDataMaxValue;
		}

		public float getDataListMinValue()
		{
			return this.mDataMinValue;
		}

		public void setDataListMaxValue(float max)
		{
			this.mDataMaxValue = max;
		}

		public void setDataListMinValue(float min)
		{
			this.mDataMinValue = min;
		}

		public float getMaxLimitValue()
		{
			return this.mMaxValue;
		}

		public float getMinLimitValue()
		{
			return this.mMinValue;
		}

		public void setMaxLimitValue(float max)
		{
			this.mMaxValue = max;
		}

		public void setMinLimitValue(float min)
		{
			this.mMinValue = min;
		}

		public TendencyLine()
		{
		}

		public void setCoordValues(List<Float> coordValueList, List<String> horCoordList)
		{
			this.coordValueList = coordValueList;
			this.mHorCoordList = horCoordList;
		}

		public void init()
		{
			points = new ArrayList<PointD>();
			final int size = coordValueList.size();
			for (int i = 0; i < size; i++)
			{
				int index = horCoordList.indexOf(mHorCoordList.get(i));
				if (index < 0)
				{
					continue;
				}
				float value = coordValueList.get(i);
				final PointD p = new PointD(originX + cellWidth * index, originY - (value - minValue) * mper_temp);
				points.add(p);
			}
		}

		protected void draw(Canvas canvas)
		{
			try
			{
				int c = paint.getColor();
				paint.setColor(lineColor);

				PointD p;
				final int len = points.size();
				for (int j = 0; j < len; j++)
				{
					p = points.get(j);
					if (p != null)
					{
						// 画线
						if (j < len - 1)
						{
							PointD p2 = points.get(j + 1);
							canvas.drawLine((float) p.x, (float) p.y, (float) p2.x, (float) p2.y, paint);
						}

						// =============选择画什么图片点============//
						Bitmap img = null;
						float value = coordValueList.get(j);
						if (value < mMinValue)
						{
							img = pointImgMin == null ? pointImg1 : pointImgMin;
						} else if (value > mMaxValue)
						{
							img = pointImgMax == null ? pointImg1 : pointImgMax;
						} else
						{
							img = pointImgNormal == null ? pointImg : pointImgNormal;
						}
						int tempInt = img.getWidth() / 2;
						pointRadius = tempInt != 0 ? tempInt : pointRadius;

						// 画点
						canvas.drawBitmap(img, (float) (p.x - pointRadius), (float) (p.y - pointRadius), null);
					}
				}
				paint.setColor(c);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

}
