package com.comvee.tnb.guides;

import java.util.List;

import android.content.Context;

public abstract class BaseListAdapter<T> extends android.widget.BaseAdapter {
	private List<T> dataSource;
	private Context mContext;

	public BaseListAdapter(Context mContext) {
		this.mContext = mContext;
	}

	public Context getContext() {
		return mContext;
	}

	@Override
	public int getCount() {
		return (dataSource == null) ? 0 : dataSource.size();
	}

	@Override
	public T getItem(int position) {
		return (dataSource == null) ? null : dataSource.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public List<T> getDataSource() {
		return dataSource;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setDataSource(List dataSource) {
		this.dataSource = dataSource;
		notifyDataSetInvalidated();
	}

	public void clearDataSource() {
		if (dataSource != null) {
			dataSource.clear();
			notifyDataSetChanged();
		}
	}

}
