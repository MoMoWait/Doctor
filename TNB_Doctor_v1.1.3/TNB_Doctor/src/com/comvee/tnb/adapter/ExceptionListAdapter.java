package com.comvee.tnb.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.model.ExceptionList;

public class ExceptionListAdapter extends BaseAdapter {
	private List<ExceptionList> list;
	private Context context;
	private ExceptionList exceptionList;

	public ExceptionListAdapter(List<ExceptionList> list, Context context) {
		super();
		this.list = list;
		this.context = context;
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
		exceptionList = list.get(arg0);
		if (arg1 == null) {
			arg1 = View.inflate(context, R.layout.follow_list_item, null);
		}
		TextView name = (TextView) arg1.findViewById(R.id.follow_item_type);
		TextView value = (TextView) arg1.findViewById(R.id.follow_item_timeanddoc);
		TextView image = (TextView) arg1.findViewById(R.id.follow_item_state);

		image.setVisibility(View.GONE);
		name.setText("你在" + exceptionList.getInsertDt() + exceptionList.getContentText());
		value.setText(exceptionList.getDoctorName() + "医生给你发了一条建议，点击查看");

		return arg1;
	}

}
