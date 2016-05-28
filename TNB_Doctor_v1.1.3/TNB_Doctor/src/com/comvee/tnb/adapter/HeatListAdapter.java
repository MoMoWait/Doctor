package com.comvee.tnb.adapter;

import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.ComveeBaseAdapter;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.model.HeatInfo;
import com.comvee.tool.ImageLoaderUtil;

public class HeatListAdapter extends ComveeBaseAdapter<HeatInfo> {

	public HeatListAdapter() {
		super(TNBApplication.getInstance(), R.layout.item_food_second);
	}

	@Override
	protected void getView(ViewHolder holder, int position) {
		try {
			HeatInfo info = getItem(position);
			ImageLoaderUtil.getInstance(context).displayImage(info.picurl, (ImageView) holder.get(R.id.food_image), ImageLoaderUtil.default_options);
			((TextView) holder.get(R.id.tv_name)).setText(info.name);
			((TextView) holder.get(R.id.tv_heatNweight)).setText(String.format("%skcal/100克", info.heat));
			changeEatAdvice(info.foodleveltext, (TextView) holder.get(R.id.eat_advice));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void changeEatAdvice(String advice, TextView tv) {
		tv.setText(advice);
		if ("适宜吃".equals(advice)) {
			tv.setBackgroundResource(R.drawable.bg_color_cornner_green);
			tv.setTextColor(Color.parseColor("#66cc66"));
		} else if ("谨慎吃".equals(advice)) {
			tv.setBackgroundResource(R.drawable.bg_color_cornner_red);
			tv.setTextColor(Color.parseColor("#ff2a00"));
		} else if ("少量吃".equals(advice)) {
			tv.setBackgroundResource(R.drawable.bg_color_cornner_yellow);
			tv.setTextColor(Color.parseColor("#ffa200"));
		}
	}
}
