package com.comvee.tnb.ui.exercise;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.widget.SectionedBaseAdapter;
import com.comvee.tool.ImageLoaderUtil;

public class SportAdapter extends SectionedBaseAdapter {
	private List<List<Exercise>> datas;// 含有四个级别的的运动类型
	Context context;

	public SportAdapter(List<List<Exercise>> data, Context ctx) {
		this.context = ctx;
		this.datas = data;
	}

	@Override
	public Object getItem(int section, int position) {

		return datas.get(section).get(position);
	}

	@Override
	public long getItemId(int section, int position) {
		return 0;
	}

	@Override
	public int getSectionCount() {
		return datas.size();
	}

	@Override
	public int getCountForSection(int section) {
		return datas.get(section).size();
	}

	@Override
	public View getItemView(int section, int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_sport_listview, null);
			viewHolder.sportLevel = (TextView) convertView.findViewById(R.id.sportLevel);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.sportIv);
			viewHolder.nametV = (TextView) convertView.findViewById(R.id.sport_name);
			viewHolder.calTv = (TextView) convertView.findViewById(R.id.cal);
			viewHolder.lineView = convertView.findViewById(R.id.line);
			viewHolder.lastView = convertView.findViewById(R.id.last_line);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Exercise exercise = datas.get(section).get(position);
		List<Exercise> belongArray = datas.get(Integer.parseInt(exercise.level) - 1);
		viewHolder.lastView.setVisibility(View.GONE);
		if (belongArray.indexOf(exercise) == belongArray.size() - 1) {// 最后一个
			viewHolder.lineView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
		} else {
			viewHolder.lineView.setBackgroundColor(context.getResources().getColor(R.color.line_color));
		}
		ImageLoaderUtil.getInstance(context).displayImage(exercise.imgUrl, viewHolder.imageView, ImageLoaderUtil.default_options);
		viewHolder.nametV.setText(exercise.name);
		viewHolder.calTv.setText(exercise.caloriesThirtyMinutes + context.getString(R.string.sport_unit).toString());
		return convertView;
	}

	@Override
	public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.selection_sport_item_istview, null);
		}
		TextView selectionTv = (TextView) convertView.findViewById(R.id.sportLevel);
		switch (section) {
		case 0:
			selectionTv.setText("轻度运动");
			break;
		case 1:
			selectionTv.setText("轻中度运动");
			break;
		case 2:
			selectionTv.setText("中强度运动");
			break;
		case 3:
			selectionTv.setText("高强度运动");
			break;

		default:
			break;
		}
		return convertView;
	}

	class ViewHolder {
		public TextView sportLevel;
		public ImageView imageView;
		public TextView nametV;
		public TextView calTv;
		public View lineView;
		public View lastView;
	}

}
