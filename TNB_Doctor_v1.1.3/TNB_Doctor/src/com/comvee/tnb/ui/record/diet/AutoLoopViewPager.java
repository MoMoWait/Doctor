package com.comvee.tnb.ui.record.diet;

import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.comvee.tnb.R;
import com.comvee.tnb.ui.heathknowledge.MyViewPager;
import com.comvee.tool.UITool;
/**
 * 自自轮播viewpager
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public class AutoLoopViewPager extends MyViewPager implements OnPageChangeListener {
	private final static int INDICATOR_PADDING = 3;// dp 标记间隔
	private final static int PLAY_DELAY = 6000;// 轮播间隔
	private AutoLoopViewPagerAdapter mAdapter;
	private LinearLayout indicatorViewGroup;
	private LooperHandler myHandler;
	private boolean isScroll = true;

	public AutoLoopViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void scrollTo(int x, int y) {
		if (isScroll) {
			super.scrollTo(x, y);
		}
	}


	@Override
	public void setAdapter(PagerAdapter pagerAdapter) {
		super.setAdapter(pagerAdapter);
		mAdapter = (AutoLoopViewPagerAdapter) pagerAdapter;
		myHandler = new LooperHandler(this);
		int current = Integer.MAX_VALUE / 2;
		if (mAdapter.getRealCount() != 0) {
			current = ((Integer.MAX_VALUE / 2) / mAdapter.getRealCount()) * mAdapter.getRealCount();
		}
		myHandler.sendEmptyMessageDelayed(0, PLAY_DELAY);
		setCurrentItem(current);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		super.onPageScrolled(arg0, arg1, arg2);
	}

	@Override
	public void onPageSelected(int position) {
		if (!mAdapter.isEmpty()) {
			int parsePosition = position % mAdapter.getRealCount();
			if (indicatorViewGroup != null)
				refreshIndicatorView(parsePosition);
		}
	}

	/*
	 * 是否需要自动播放
	 */
	public void enableLoop(boolean b) {
		if (myHandler != null)
			myHandler.removeMessages(0);
		else
			myHandler.sendEmptyMessageDelayed(0, PLAY_DELAY);
	}

	public void showIndicator(boolean b) {
		if (b) {
			ViewParent viewParent = getParent();
			if (viewParent instanceof RelativeLayout) {
				RelativeLayout parentView = (RelativeLayout) viewParent;
				this.indicatorViewGroup = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.viiewpager_indicator_layout, null);
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				layoutParams.setMargins(0, 0, 0, UITool.dip2px(getContext(), INDICATOR_PADDING));
				indicatorViewGroup.setLayoutParams(layoutParams);
				parentView.addView(indicatorViewGroup);
				addIndicator();
			} else {
			}
		} else if (indicatorViewGroup != null) {
			indicatorViewGroup.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (myHandler != null)
			myHandler.removeMessages(0);
	}

	private void addIndicator() {
		int paddingPX = UITool.dip2px(getContext(), INDICATOR_PADDING);
		for (int i = 0; i < mAdapter.getRealCount(); i++) {
			ImageView imageView = new ImageView(getContext());
			imageView.setImageResource(R.drawable.shape_solid_gray_circle);
			imageView.setPadding(paddingPX, paddingPX, paddingPX, paddingPX);
			indicatorViewGroup.addView(imageView);
		}
	}

	// 更新item点点标记
	private void refreshIndicatorView(int position) {
		ImageView currentIndicatorImg = null;
		for (int i = 0; i < mAdapter.getRealCount(); i++) {
			ImageView img = (ImageView) indicatorViewGroup.getChildAt(i);
			img.setImageResource(R.drawable.shape_solid_gray_circle);
			if (i == position) {
				currentIndicatorImg = img;
			}
		}
		if (currentIndicatorImg != null)
			currentIndicatorImg.setImageResource(R.drawable.shape_solid_while_circle);
	}

	public static abstract class AutoLoopViewPagerAdapter<T> extends PagerAdapter {
		private SparseArray<View> sparseArray;
		private Context context;
		private List<T> datas;
		private int itemLayoutRes;
		private AutoLoopViewPager autoLoopViewPager;

		public AutoLoopViewPagerAdapter(AutoLoopViewPager autoLoopViewPager, Context context, int itemLayoutRes, List<T> datas) {
			super();
			this.autoLoopViewPager = autoLoopViewPager;
			this.context = context;
			this.itemLayoutRes = itemLayoutRes;
			this.datas = datas;
			sparseArray = new SparseArray<View>();
		}

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			if (isEmpty()) {
				return null;
			}
			int parsePosition = position % datas.size();
			View converView = LayoutInflater.from(context).inflate(itemLayoutRes, null);
			((ViewPager) container).addView(converView);
			sparseArray.put(position, converView);
			doSth(converView, datas.get(parsePosition));
			return converView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(sparseArray.get(position));
			sparseArray.remove(position);

		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			if (getRealCount() != 0) {
				int current = ((Integer.MAX_VALUE / 2) / getRealCount()) * getRealCount();
				autoLoopViewPager.setCurrentItem(current);
			}
		}

		@Override
		public int getItemPosition(Object t) {
			return POSITION_NONE;
		}

		/**
		 * 实际item数量
		 * 
		 * @return
		 */
		public int getRealCount() {
			if (datas != null)
				return datas.size();
			return 0;
		}

		public abstract void doSth(View view, T t);

		public boolean isEmpty() {
			return datas == null || datas.size() == 0;
		}

	}

	static class LooperHandler extends Handler {

		WeakReference<ViewPager> mViewpagerReference;

		public LooperHandler(ViewPager mViewpager) {
			super();
			this.mViewpagerReference = new WeakReference<ViewPager>(mViewpager);
		}

		@Override
		public void handleMessage(Message msg) {
			ViewPager viewPager = mViewpagerReference.get();
			viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
			this.sendEmptyMessageDelayed(0, PLAY_DELAY);
		}
	}

	public void setScroll(boolean isScroll) {
		this.isScroll = isScroll;
	}
}
