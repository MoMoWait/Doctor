package com.comvee.tnb.widget;

import android.view.View;
import android.view.ViewGroup;

public interface OnItemClickListener {

	public void onItemClick(ViewGroup fatherView,View v,int position,int oldPosition,boolean fromClick);
	
}
