package com.comvee.tnb.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * title选项卡
 * @author friendlove
 *
 */
public class TitleTabLayout extends LinearLayout implements View.OnClickListener {

	private OnItemClickListener listen;
	private int oldPosition;

	@SuppressLint("NewApi")
	public TitleTabLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TitleTabLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TitleTabLayout(Context context) {
		super(context);
		init();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		initTabLayout();
	}
	
	private void init() {
	}

	public void initTabLayout() {

		final ViewGroup layout = this;

		int len = layout.getChildCount();

		for (int i = 0; i < len; i++) {
			View item = layout.getChildAt(i);
			item.setTag(i);
			item.setOnClickListener(this);
		}

	}

	public void setOnItemClickListener(OnItemClickListener l) {
		this.listen = l;
	}

	public void setSelect(int index){
		if (null == listen) {
			return;
		}
		listen.onItemClick(this,this.getChildAt(index), index,oldPosition,false);
		oldPosition = index;
	}
	
	@Override
	public void onClick(View v) {

		final int position = (Integer) v.getTag();

		if (null == listen) {
			return;
		}
		listen.onItemClick(this, v, position,oldPosition,true);
		
		oldPosition = position;
	}

}
