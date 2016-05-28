package com.comvee.tnb.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.tencent.sugardoctor.widget.wheelview.TosAdapterView;

public class WheelSelectAdapter extends BaseAdapter implements TosAdapterView.OnItemSelectedListener {
	private int textSizeLeve1 = 30;
	private int textSizeLeve2 = 18;

	private int colorBlack = Color.parseColor("#999999");
	private int selectColor = Color.parseColor("#333333");
	private int seletedIndex = -1;
	private Context mContext;
	private int minValue = -1;
	private int maxValue = -1;
	private List<String> list;

	public WheelSelectAdapter(Context mContext, int minValue, int maxValue) {
		super();
		this.mContext = mContext;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public WheelSelectAdapter(Context mContext, List<String> list) {
		super();
		this.mContext = mContext;
		this.list = list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public List<String> getList() {
		return list;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (list == null && maxValue != -1 && minValue != -1) {
			count = maxValue - minValue + 1;
		}
		if (list != null && maxValue == -1 && minValue == -1) {
			count = list.size();
		}
		return count;
	}

	@Override
	public Object getItem(int arg0) {
		Object obj = null;
		if (list == null && maxValue != -1 && minValue != -1) {
			obj = minValue + arg0;
		}
		if (list != null && maxValue == -1 && minValue == -1) {
			obj = list.get(arg0);
		}
		return obj;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		TextView textView = null;
		if (arg1 == null) {
			arg1 = View.inflate(mContext, R.layout.item_wheel, null);
		}
		textView = (TextView) arg1.findViewById(R.id.tv_text);
		textView.setTextSize(textSizeLeve2);
		textView.setGravity(Gravity.CENTER);
		textView.getPaint().setFakeBoldText(true);

		String text = null;
		if (list == null && maxValue != -1 && minValue != -1) {
			int value = minValue + arg0;
			text = Integer.toString(value);
		}
		if (list != null && maxValue == -1 && minValue == -1) {
			text = list.get(arg0);
		}
		if (arg0 == seletedIndex - 1) {
			textView.setTextSize(textSizeLeve2);
			textView.setTextColor(colorBlack);
		} else if (arg0 == seletedIndex) {
			textView.setTextSize(textSizeLeve1);
			textView.setTextColor(selectColor);
			
		} else if (arg0 == seletedIndex + 1) {
			textView.setTextSize(textSizeLeve2);
			textView.setTextColor(colorBlack);
			
		}
	

		textView.setText(text);
		return arg1;
	}

	/**
	 * @author valexhuang
	 * @return the seletedIndex
	 */
	public int getSeletedIndex() {
		return seletedIndex;
	}

	/**
	 * @author valexhuang
	 * @param seletedIndex
	 *            the seletedIndex to set
	 */
	public void setSeletedIndex(int seletedIndex) {
		this.seletedIndex = seletedIndex;
	}

	@Override
	public void onItemSelected(TosAdapterView<?> parent, View view, int position, long id) {
		int selectColor = this.selectColor;
		setSeletedIndex(position);
		TextView text = (TextView) view.findViewById(R.id.tv_text);
		text.setTextSize(textSizeLeve1);
		text.setTextColor(selectColor);
		int index = Integer.parseInt(view.getTag().toString());
		if (parent.getChildAt(index + 1) != null) {
			text = (TextView) parent.getChildAt(index + 1).findViewById(R.id.tv_text);
			text.setBackgroundColor(Color.TRANSPARENT);
			text.setTextColor(colorBlack);
			text.setTextSize(textSizeLeve2);

		}

	

		if (parent.getChildAt(index - 1) != null) {
			text = (TextView) parent.getChildAt(index - 1).findViewById(R.id.tv_text);
			text.setBackgroundColor(Color.TRANSPARENT);
			text.setTextColor(colorBlack);
			text.setTextSize(textSizeLeve2);

		}

	
	}

	@Override
	public void onNothingSelected(TosAdapterView<?> parent) {

	}

}
