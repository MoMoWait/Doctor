package com.comvee.tnb.adapter;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.common.MyBaseAdapter;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tool.UITool;
import com.comvee.tool.ViewHolder;
import com.comvee.util.TimeUtil;

public class AskTellTimeHorAdapter extends MyBaseAdapter<Calendar> {
	private int selectIndex;
	private float viewWidth;

	public AskTellTimeHorAdapter(Context ctx, List<Calendar> datas, int mayoutid) {
		super(ctx, datas, mayoutid);
	}

	@Override
	protected void doyouself(ViewHolder holder, int position) {
		Calendar time = datas.get(position);
		TextView textView = holder.get(R.id.tv_data);
		if (viewWidth == 0) {
			viewWidth = UITool.getDisplayWidth(context) / 3;
		}
		if (position == selectIndex) {
			holder.get(R.id.view).setVisibility(View.VISIBLE);
			textView.setTextColor(context.getResources().getColor(R.color.theme_color_green));
		} else {
			holder.get(R.id.view).setVisibility(View.INVISIBLE);
			textView.setTextColor(context.getResources().getColor(R.color.text_color_1));
		}
		textView.setText(TimeUtil.fomateTime(time.getTimeInMillis(), ConfigParams.DATE_FORMAT_1) + " "
				+ ConfigParams.strWeeks_1[time.get(Calendar.DAY_OF_WEEK) - 1]);
		holder.mConvertView.setLayoutParams(new AbsListView.LayoutParams((int) viewWidth,LayoutParams.MATCH_PARENT));
	}

	public void setSelectIndex(int selectIndex) {
		this.selectIndex = selectIndex;
		notifyDataSetChanged();
	}
}
