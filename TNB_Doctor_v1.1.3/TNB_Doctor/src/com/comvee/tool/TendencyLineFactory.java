package com.comvee.tool;

import java.util.ArrayList;
import java.util.List;

import com.comvee.tnb.widget.Tendency;
import com.comvee.tnb.widget.Tendency.TendencyLine;

public abstract class TendencyLineFactory<E>
{

	public abstract String getTime(E obj);

	public abstract float getValue(E obj);

//	public abstract TendencyLine getTendencyLine(ArrayList<String> horList, ArrayList<Float> valueList);

	public TendencyLine getTendencyLine(Tendency tendency, List<E> list)
	{
		ArrayList<String> horCoordList = new ArrayList<String>();
		ArrayList<Float> coordValueList = new ArrayList<Float>();
		float minV = 0;// 纵坐标最小值
		float maxV = 0;// 纵坐标最大值
		String timeTemp1 = null;
		TendencyLine line = tendency.createLine();
		for (int i = 0; i < list.size(); i++)
		{
			try
			{
				E info1 = list.get(i);
				String time1 = getTime(info1);

				// /////////排除重复////////////
				if (time1.equals(timeTemp1))
				{
					continue;
				} else
				{
					timeTemp1 = time1;
				}
				// /////////排除重复////////////

				float value1 = getValue(info1);

				horCoordList.add(time1);
				coordValueList.add(value1);

				// ///////求最大最小值/////////////
				if (i == 0 && (minV == 0 || maxV == 0))
				{
					minV = value1;
					maxV = value1;
				} else if (value1 > maxV)
				{
					maxV = value1;
				} else if (value1 < minV)
				{
					minV = value1;
				}
				// ///////求最大最小值/////////////
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		line.setDataListMaxValue(maxV);
		line.setDataListMinValue(minV);
		line.setCoordValues(coordValueList, horCoordList);
		return line;

	}

}
