package com.comvee.tnb.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.comvee.tnb.R;

public class MenuAdapter extends BaseAdapter {
	private ArrayList<Integer> itemIcon;
	private ArrayList<String> itemValues;
	private Context context;

	public void setContext(Context context) {
		this.context = context;
	}

	public void setItemIcon(ArrayList<Integer> itemIcon) {
		this.itemIcon = itemIcon;
	}

	public void setItemValues(ArrayList<String> itemValues) {
		this.itemValues = itemValues;
	}

	@Override
	public int getCount() {
		return itemValues == null ? 0 : itemValues.size();
	}

	@Override
	public Object getItem(int position) {
		return itemValues == null ? null : itemValues.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.menu_item_layout, null);
		}
		TextView name = (TextView) convertView.findViewById(R.id.tv_menu_item);
		name.setText(itemValues.get(position));
		if (itemIcon != null && itemIcon.size() == itemValues.size()) {
			name.setCompoundDrawablesWithIntrinsicBounds(itemIcon.get(position), 0, 0, 0);
		}
		return convertView;
	}
}
