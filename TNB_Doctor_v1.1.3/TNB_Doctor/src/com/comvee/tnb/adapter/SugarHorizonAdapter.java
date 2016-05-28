package com.comvee.tnb.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.widget.HorizontalListView;

public class SugarHorizonAdapter extends BaseAdapter {
	private final int selectedColor = TNBApplication.getInstance().getResources().getColor(R.color.theme_color_green);
	private final int unselectedColor = Color.parseColor("#666666");
	private Drawable indicatorDrawable;
	private LayoutInflater mInflater;
	private String[] textArr;
	private int selectIndex = -1;

	public SugarHorizonAdapter(Context con, String[] textArr_, HorizontalListView horizontalListView_) {
		mInflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		textArr = textArr_;
		indicatorDrawable = con.getResources().getDrawable(R.drawable.bg_textview_indicator);
		indicatorDrawable.setBounds(0, 0, indicatorDrawable.getMinimumWidth(), indicatorDrawable.getMinimumHeight());
	}

	@Override
	public int getCount() {
		return textArr.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	private static class ViewHolder {
		private TextView title;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.sugar_time_bucket_item, null);
			viewHolder.title = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (position == selectIndex) {
			viewHolder.title.setTextColor(selectedColor);
			viewHolder.title.setCompoundDrawables(null, null, null, indicatorDrawable);
		} else {
			viewHolder.title.setTextColor(unselectedColor);
			viewHolder.title.setCompoundDrawables(null, null, null, null);
		}

		viewHolder.title.setText(textArr[position]);
		return convertView;
	}

	public void setSelectIndex(int i) {
		selectIndex = i;
		this.notifyDataSetChanged();
	}

}