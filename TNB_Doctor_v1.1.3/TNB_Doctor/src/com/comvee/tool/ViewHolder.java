package com.comvee.tool;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewHolder {
	SparseArray<View> views;
	public View mConvertView;

	public ViewHolder(Context context, int layoutid, ViewGroup parent) {
		mConvertView = LayoutInflater.from(context).inflate(layoutid, parent, false);
		views = new SparseArray<View>();
		mConvertView.setTag(this);
	}

	public static ViewHolder getViewHolder(Context context, View convertView, ViewGroup parent, int layoutid) {
		if (convertView != null && convertView.getTag() != null) {
			return (ViewHolder) convertView.getTag();
		} else {
			return new ViewHolder(context, layoutid, parent);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T get(int viewId) {
		View view = views.get(viewId);
		if (view == null) {
			view = mConvertView.findViewById(viewId);
			views.put(viewId, view);
		}
		return (T) view;
	}
}
