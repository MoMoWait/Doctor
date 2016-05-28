package com.comvee.tnb.adapter;

import java.util.List;

import android.view.View;

public class ViewPageAdapter extends ComveePageAdapter {

	private List<View> mViews;

	public ViewPageAdapter(List<View> mViews) {
		this.mViews = mViews;
	}

	@Override
	public int getCount() {
		return mViews == null ? 0 : mViews.size();
	}

	@Override
	public View getView(int position) {
		return mViews.get(position);
	}

}
