package com.comvee.tnb.ui.exercise;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tool.ImageLoaderUtil;

public class SearchAdapter extends BaseAdapter {
	private ArrayList<Exercise> models;
	private Context context;

	public SearchAdapter(Context context) {
		this.context = context;
	}

	public void setModels(ArrayList<Exercise> models) {
		this.models = models;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return models == null ? 0 : models.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return models.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		Exercise exercise = models.get(position);
		if (arg1 == null) {
			arg1 = View.inflate(context, R.layout.item_sport_listview, null);
		}

		if (position == models.size() - 1) {// 最后一个
			arg1.findViewById(R.id.line).setBackgroundColor(context.getResources().getColor(android.R.color.white));
			arg1.findViewById(R.id.last_line).setVisibility(View.VISIBLE);
		} else {
			arg1.findViewById(R.id.last_line).setVisibility(View.GONE);
			arg1.findViewById(R.id.line).setBackgroundColor(context.getResources().getColor(R.color.line_color));
		}
		ImageLoaderUtil.getInstance(context).displayImage(exercise.imgUrl, (ImageView) arg1.findViewById(R.id.sportIv),
				ImageLoaderUtil.default_options);
		((TextView) (arg1.findViewById(R.id.sport_name))).setText(exercise.name);
		((TextView) (arg1.findViewById(R.id.cal))).setText(exercise.caloriesThirtyMinutes+ context.getString(R.string.sport_unit).toString());
		return arg1;
	}
}
