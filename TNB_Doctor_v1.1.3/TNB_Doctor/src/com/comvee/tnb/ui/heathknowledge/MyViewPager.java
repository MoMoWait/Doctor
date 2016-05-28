package com.comvee.tnb.ui.heathknowledge;

import java.lang.reflect.Field;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

public class MyViewPager extends ViewPager {
	
	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		setMyScroller();
	}

	@Override
	public void setAdapter(PagerAdapter arg0) {
		super.setAdapter(arg0);
		if (arg0 instanceof HealthViewPagerAdapter) {
			setOnPageChangeListener((OnPageChangeListener) arg0);
		}
	}
	private void setMyScroller() {
		try {
			Class<?> viewpager = ViewPager.class;
			Field scroller = viewpager.getDeclaredField("mScroller");
			scroller.setAccessible(true);
			Field mFirstLayout = ViewPager.class.getDeclaredField("mFirstLayout");
			mFirstLayout.setAccessible(true);
			scroller.set(this, new MyScroller(getContext()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class MyScroller extends Scroller {
	public MyScroller(Context context) {
		super(context, new DecelerateInterpolator());
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		super.startScroll(startX, startY, dx, dy, 600);
	}

}
