package com.comvee.tnb.adapter;

import java.util.List;

import android.content.Context;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.common.MyBaseAdapter;
import com.comvee.tnb.model.AskTellInfo;
import com.comvee.tool.ResUtil;
import com.comvee.tool.ViewHolder;

/**
 * 电话咨询
 * 
 * @author friendlove-pc
 * 
 */
public class AskTellListAdapter extends MyBaseAdapter<AskTellInfo> {

	public AskTellListAdapter(Context ctx, List<AskTellInfo> datas, int mayoutid) {
		super(ctx, datas, mayoutid);
	}

	@Override
	protected void doyouself(ViewHolder holder, int position) {
		AskTellInfo info = datas.get(position);
		TextView btn = holder.get(R.id.btn_order);
		TextView tv = holder.get(R.id.tv_time);
		tv.setText(info.startTime + "-" + info.endTime);
		btn.setTextColor(context.getResources().getColor(R.color.theme_color_green));
		tv.setTextColor(context.getResources().getColor(R.color.text_color_1));
		if (info.isLast) {
			// 过期
			btn.setEnabled(false);
			btn.setText(ResUtil.getString(R.string.order_past));
			btn.setTextColor(context.getResources().getColor(R.color.line));
			tv.setTextColor(context.getResources().getColor(R.color.line));
		} else if (info.total - info.use <= 0) {
			btn.setEnabled(false);
			btn.setText(ResUtil.getString(R.string.order_full));
			btn.setTextColor(context.getResources().getColor(R.color.line));
			tv.setTextColor(context.getResources().getColor(R.color.line));
		} else {
			btn.setText(ResUtil.getString(R.string.order_now));
			btn.setEnabled(true);

		}
	}

}
