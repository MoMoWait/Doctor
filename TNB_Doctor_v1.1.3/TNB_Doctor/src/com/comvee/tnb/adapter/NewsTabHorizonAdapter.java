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

/**
 * 知识首页Tab标题设置和颜色设置的Adapter
 */
public class NewsTabHorizonAdapter extends BaseAdapter {
	private final int selectedColor = TNBApplication.getInstance().getResources().getColor(R.color.theme_color_green);
	private final int unselectedColor = Color.parseColor("#5d5d5d");
	private LayoutInflater mInflater;
	private String[] textArr;
	private int selectIndex = -1;

	public NewsTabHorizonAdapter(Context con, String[] textArr_, HorizontalListView horizontalListView_) {
		mInflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		textArr = textArr_;
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
			convertView = mInflater.inflate(R.layout.tab_time_bucket_item, null);
			viewHolder.title = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (position == selectIndex) {
			viewHolder.title.setTextColor(selectedColor);
			viewHolder.title.setTextSize(15);
		} else {
			viewHolder.title.setTextColor(unselectedColor);
			viewHolder.title.setTextSize(13);
		}

		viewHolder.title.setText(textArr[position]);
		return convertView;
	}

	public void setSelectIndex(int i) {
		selectIndex = i;
		this.notifyDataSetChanged();
	}

}