package com.comvee.tnb.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.model.MessageModel;
import com.comvee.tool.AppUtil;

public class MessageCentreAdapter extends BaseAdapter {
	private Context context;
	private List<MessageModel> list;
	private MessageModel model;

	public void setList(List<MessageModel> list) {
		this.list = list;
	}

	public MessageCentreAdapter(Context context, List<MessageModel> list) {
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		model = list.get(arg0);
		if (arg1 == null) {
			arg1 = View.inflate(context, R.layout.item_message_centre, null);
		}
		TextView label = (TextView) arg1.findViewById(R.id.message_item_label);
		TextView value = (TextView) arg1.findViewById(R.id.message_item_message);
		ImageView redDot = (ImageView) arg1.findViewById(R.id.message_item_left);
		label.setText(model.getTitle());
		value.setText(AppUtil.dateFormatForm("yyyy-MM-dd HH:mm:ss", model.getInsertDt(), "yyyy-MM-dd HH:mm"));
		if (model.getStatus().equals("0")) {
			redDot.setVisibility(View.VISIBLE);
		}
		if (model.getStatus().equals("1")) {
			redDot.setVisibility(View.INVISIBLE);
		}
		if (arg0 == list.size() - 1) {
			arg1.findViewById(R.id.message_long_line).setVisibility(View.VISIBLE);
			arg1.findViewById(R.id.message_short_line).setVisibility(View.GONE);
		} else {
			arg1.findViewById(R.id.message_long_line).setVisibility(View.GONE);
			arg1.findViewById(R.id.message_short_line).setVisibility(View.VISIBLE);
		}
		if (model.getJobDetailType().equals("55") || model.getJobDetailType().equals("0")) {
			arg1.findViewById(R.id.index_item_right).setVisibility(View.GONE);
		}
		if (model.getJobDetailType().equals("24")) {
			arg1.findViewById(R.id.index_item_right).setVisibility(View.VISIBLE);
		}
		arg1.setTag(model);
		return arg1;
	}
}
