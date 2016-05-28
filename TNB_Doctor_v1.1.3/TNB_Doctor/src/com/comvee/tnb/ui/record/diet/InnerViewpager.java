package com.comvee.tnb.ui.record.diet;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.comvee.tnb.R;

public class InnerViewpager extends ViewPager {
	ViewGroup indicatorVg;

	public InnerViewpager(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public InnerViewpager(Context context) {
		this(context, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	/*	int height = 0;
		InnerViewpagerAdapterInter adapter = (InnerViewpagerAdapterInter) getAdapter();
		View child = adapter.getPrimaryItem();
		if (child != null) {
			child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			height = child.getMeasuredHeight();
			height = Math.max(height, Util.dip2px(getContext(), 290));
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		}*/
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public<T extends PagerAdapter> void setAdapterAndIndicator(T pagerAdapter , ViewGroup indicatorVg) {
		setAdapter(pagerAdapter);
		this.indicatorVg = indicatorVg;
		initIndicator();
		setOnPageChangeListener(new PageChangeListener());
	}

	private class PageChangeListener implements OnPageChangeListener {
		private ViewGroup[] indicatorArray;

		public PageChangeListener() {
			indicatorArray = new ViewGroup[indicatorVg.getChildCount()];
			for (int i = 0; i < indicatorArray.length; i++) {
				indicatorArray[i] = (ViewGroup) indicatorVg.getChildAt(i);
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int index) {
			updateIndicator(index);
		}

		private void updateIndicator(int index) {
			for (int i = 0; i < indicatorArray.length; i++) {
				ViewGroup vg = indicatorArray[i];
				TextView name = (TextView) vg.getChildAt(0);
				View indicatorline = vg.getChildAt(1);
				if (i == index) {
					name.setTextColor(Color.parseColor("#30c29d"));
					indicatorline.setVisibility(View.VISIBLE);
				} else {
					name.setTextColor(Color.parseColor("#333333"));
					indicatorline.setVisibility(View.INVISIBLE);
				}
			}

		}
	}

	/**
	 *初始化指示横线
	 */
	private void initIndicator() {
		final ViewGroup[] indicatorArray = new ViewGroup[indicatorVg.getChildCount()];
		for (int i = 0; i < indicatorArray.length; i++) {
			final int currentindex = i;
			indicatorArray[i] = (ViewGroup) indicatorVg.getChildAt(i);
			indicatorArray[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					for (int i = 0; i < indicatorArray.length; i++) {
						ViewGroup vg = indicatorArray[i];
						TextView name = (TextView) vg.getChildAt(0);
						View indicator = vg.getChildAt(1);
						setCurrentItem(currentindex);
						if (i == currentindex) {
							name.setTextColor(getContext().getResources().getColor(R.color.btn_green));
							indicator.setVisibility(View.VISIBLE);

						} else {
							name.setTextColor(getContext().getResources().getColor(R.color.black));
							indicator.setVisibility(View.INVISIBLE);
						}
					}
				}
			});
		}
	}
}
