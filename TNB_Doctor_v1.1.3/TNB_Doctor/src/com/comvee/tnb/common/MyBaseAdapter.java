package com.comvee.tnb.common;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.comvee.tool.ViewHolder;

public abstract class MyBaseAdapter<T> extends BaseAdapter {

	protected List<T> datas;
	protected Context context;
	protected int layoutId;

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.size();
	}

	@Override
	public T getItem(int position) {
		return datas == null ? null : datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public List<T> getDatas() {
		return datas;
	}

	public MyBaseAdapter(Context ctx, List<T> datas, int mayoutid) {
		this.context = ctx;
		this.datas = datas;
		this.layoutId = mayoutid;
	}

	public void setDatas(List<T> datas) {
		this.datas = datas;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = ViewHolder.getViewHolder(context, convertView, parent, layoutId);
		doyouself(viewHolder, position);
		return viewHolder.mConvertView;
	}

	protected abstract void doyouself(ViewHolder holder, int position);

}
