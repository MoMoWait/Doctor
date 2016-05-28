package com.comvee.tnb.ui.heathknowledge;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.comvee.tnb.R;

public class SearchAdapter extends BaseAdapter {
	private ArrayList<SearchMsgModel> models;
	private Context context;

	public SearchAdapter(Context context) {
		this.context = context;
	}

	public void setModels(ArrayList<SearchMsgModel> models) {
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
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if (arg1 == null) {
			arg1 = View.inflate(context, R.layout.item_search_list, null);
		}
		TextView textView = (TextView) arg1.findViewById(R.id.tv_item_seach);
		textView.setText(models.get(arg0).getTitle());
		if (arg0 == models.size() - 1) {
			arg1.findViewById(R.id.line_short).setVisibility(View.GONE);
			arg1.findViewById(R.id.line_long).setVisibility(View.VISIBLE);
		} else {
			arg1.findViewById(R.id.line_short).setVisibility(View.VISIBLE);
			arg1.findViewById(R.id.line_long).setVisibility(View.GONE);
		}
		return arg1;
	}
}
