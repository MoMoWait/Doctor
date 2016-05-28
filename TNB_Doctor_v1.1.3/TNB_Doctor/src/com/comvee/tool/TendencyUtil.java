package com.comvee.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.graphics.Color;

import com.comvee.tnb.R;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.widget.Tendency;
import com.comvee.tnb.widget.Tendency.TendencyLine;
import com.comvee.util.TimeUtil;

public class TendencyUtil
{

	/**
	 * 获取小时横坐标
	 * 
	 * @return
	 */
	public static List<String> getHoursTimeStrings()
	{
		return Arrays.asList(ConfigParams.HOURS);
	}

	/**
	 * 获取一周的横坐标
	 * 
	 * @return
	 */
	public static List<String> getWeekTimeStrings()
	{
		return getDayTimeStrings(7, 1);
	}

	/**
	 * 获取一个月的横坐标
	 * 
	 * @return
	 */
	public static List<String> getMonthTimeStrings()
	{
		return getDayTimeStrings(31, 5);
	}

	/**
	 * 获取三个月的横坐标
	 * 
	 * @return
	 */
	public static List<String> getThreeMonthTimeStrings()
	{
		return getDayTimeStrings(91, 14);
	}

	private static List<String> getDayTimeStrings(int dayNum, int offerDayNum)
	{
		ArrayList<String> list = new ArrayList<String>();
		while (dayNum > 0)
		{
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -(--dayNum) + offerDayNum);
			String time = TimeUtil.fomateTime(cal.getTimeInMillis(), "MM/dd");
			list.add(time);
		}
		return list;
	}

	/**
	 * 获取纵坐标
	 * 
	 * @param minV
	 * @param maxV
	 * @param lineNum
	 * @return
	 */
	public static List<Float> getVerticalCoords(float minV, float maxV, int lineNum)
	{
		maxV++;
		minV = minV > 1 ? minV - 1 : 0;
		final ArrayList<Float> valueList = new ArrayList<Float>();
		int count = 8;
		float cell = (float) Math.ceil((maxV - minV) / (float) count);
		if (cell < 1)
		{
			cell = 1;
		}
		minV = (int) Math.floor(minV);
		for (int i = 0; i < count + 1; i++)
		{
			valueList.add((minV + cell * i));
		}
		return valueList;
	}

	public static <E> void createLines(int timeType, Tendency tendency, List<E> datalist,
			TendencyLineFactory<E> factory, float max, float min)
	{
		createLines(timeType, tendency, datalist, factory, max, min, Color.parseColor("#c2e212"),
				R.drawable.tendencypoit0);
	}

	public static <E> void createLines(int timeType, Tendency tendency, List<E> datalist,
			TendencyLineFactory<E> factory, float max, float min, int lineColor, int pointNormalRes)
	{
		createLines(timeType, tendency, datalist, factory, max, min, lineColor, pointNormalRes, R.drawable.tendencypoit2,
				R.drawable.tendencypoit2);
	}

	public static <E> void createLines(int timeType, Tendency tendency, TendencyLineFactory<E> factory, float[] max,
			float[] min, List<E>... datalist)
	{

		createLines(timeType, tendency, factory, max, min, new int[]
		{ Color.parseColor("#c2e212"), Color.parseColor("#ff9900") }, new int[]
		{ R.drawable.tendencypoit0, R.drawable.tendencypoit0 }, new int[]
		{ R.drawable.tendencypoit2, R.drawable.tendencypoit2 }, new int[]
		{ R.drawable.tendencypoit2, R.drawable.tendencypoit2 }, datalist);
	}

	public static <E> void createLines(int timeType, Tendency tendency, List<E> datalist,
			TendencyLineFactory<E> factory, float max, float min, int lineColor, int pointNormalRes, int pointLowRes,
			int pointHighRes)
	{
		// 创建一条线
		TendencyLine line1 = factory.getTendencyLine(tendency, datalist);

		// 设置最大最小值
		line1.setMaxLimitValue(max);
		line1.setMinLimitValue(min);

		// 设置颜色
		line1.setLineColor(lineColor);
		line1.setPointBitmap(pointNormalRes, pointLowRes, pointHighRes);

		float minV = line1.getDataListMinValue();
		float maxV = line1.getDataListMaxValue();

		// 把线添加到图中
		tendency.addCoordValues(line1);
		createTendency(tendency, minV, maxV, timeType);
	}

	public static <E> void createLines(int timeType, Tendency tendency, TendencyLineFactory<E> factory, float[] max,
			float[] min, int[] lineColor, int[] pointNormalRes, int[] pointLowRes, int[] pointHighRes,
			List<E>... datalist)
	{
		float minV = 0;
		float maxV = 0;
		int len = datalist.length;
		for (int i = 0; i < len; i++)
		{
			// 创建一条线
			TendencyLine line1 = factory.getTendencyLine(tendency, datalist[i]);

			// 设置最大最小值
			line1.setMaxLimitValue(max[i]);
			line1.setMinLimitValue(min[i]);

			// 设置颜色
			line1.setLineColor(lineColor[i]);
			line1.setPointBitmap(pointNormalRes[i], pointLowRes[i], pointHighRes[i]);

			if (minV == 0 && maxV == 0)
			{
				minV = line1.getDataListMinValue();
				maxV = line1.getDataListMaxValue();
			} else
			{
				minV = minV > line1.getDataListMinValue() ? line1.getDataListMinValue() : minV;
				maxV = maxV < line1.getDataListMaxValue() ? line1.getDataListMaxValue() : maxV;
			}

			// 把线添加到图中
			tendency.addCoordValues(line1);
		}
		createTendency(tendency, minV, maxV, timeType);
	}

	private static void createTendency(Tendency tendency, float minV, float maxV, int timeType)
	{
		List<String> horList = new ArrayList<String>();

		switch (timeType)
		{
		case 0:
			horList = TendencyUtil.getHoursTimeStrings();
			tendency.setPointSpace(4);
			break;
		case 1:
			horList = TendencyUtil.getWeekTimeStrings();
			tendency.setPointSpace(1);
			break;
		case 2:
			horList = TendencyUtil.getMonthTimeStrings();
			tendency.setPointSpace(5);
			break;
		case 3:
			horList = TendencyUtil.getThreeMonthTimeStrings();
			tendency.setPointSpace(15);
			break;
		default:

		}
		// 设置横坐标
		tendency.setHorizontalCoord(horList);
		// 设置纵坐标
		tendency.setVerticalCoord(TendencyUtil.getVerticalCoords(minV, maxV, 8));
	}

}
