package com.comvee.tnb.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class RuleScrollView extends ScrollView
{

	public RuleScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public RuleScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public RuleScrollView(Context context)
	{
		super(context);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt)
	{
		super.onScrollChanged(l, t, oldl, oldt);
		if (listener != null)
		{
			listener.onScrollChanged(l, t, oldl, oldt);
		}
	}

	private OnScrollChangedListener listener;

	public void setOnScrollChangedListener(OnScrollChangedListener listener)
	{
		this.listener = listener;

	}

}
