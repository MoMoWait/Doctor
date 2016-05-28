package com.comvee.tnb.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.sharesdk.sina.weibo.SinaWeibo;

import com.comvee.BaseApplication;
import com.comvee.tnb.R;
import com.comvee.tnb.adapter.ComveePageAdapter;
import com.comvee.tnb.model.HomeIndex;
import com.comvee.tnb.model.IndexGuideModelNew;
import com.comvee.tnb.model.HomeIndex.Tunrs;
import com.comvee.tnb.network.IndexNewGuideUtil;
import com.comvee.tnb.radio.FixedSpeedScroller;
import com.comvee.tnb.ui.book.ShareUtil.OnShareItemClickListence;
import com.comvee.tnb.ui.heathknowledge.MyViewPager;
import com.comvee.tnb.widget.RoundImageView;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 首页轮播图 Created by friendlove-pc on 16/3/20.
 */
public class IndexBannerView extends RelativeLayout {

	private MyViewPager mViewPager;

	private SamplePagerAdapter mAdapter;
	/**
	 * 默认自动滚动延时时间
	 */
	private static final long DELAY_MILLIS = 3000;
	/**
	 * handle
	 */
	private Handler mHandler;

	private ArrayList<HomeIndex.Tunrs> mListDatas;

	public IndexBannerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public IndexBannerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IndexBannerView(Context context) {
		super(context);
		init();

	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		init();

	}

private void init(){
	View.inflate(getContext(), R.layout.layout_index_banner, this);
	mAdapter = new SamplePagerAdapter();
	mViewPager = (MyViewPager) findViewById(R.id.viewPager);
	mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (null != mHandler) {
				if (mHandler.hasMessages(0)) {
					mHandler.removeMessages(0);
				}
				if (state == ViewPager.SCROLL_STATE_IDLE) {// viewpager空闲，即图片切换属于停止状态时，发消息。
					mHandler.sendMessageDelayed(Message.obtain(mHandler, 0), DELAY_MILLIS);
				}
			}
		}
	});
	mViewPager.setAdapter(mAdapter);
}
	public void setDatas(ArrayList<HomeIndex.Tunrs> mListDatas) {
		this.mListDatas = mListDatas;
		mAdapter.notifyDataSetChanged();
	}

	class SamplePagerAdapter extends ComveePageAdapter {
		@Override
		public int getCount() {

			if (mListDatas == null) {

				return 0;

			} else if (mListDatas.size() == 1) {// 一条数据时不能滑动

				return 1;
			} else {

				return Integer.MAX_VALUE;
			}
			// return mListDatas != null ? Integer.MAX_VALUE : 0;
			// return mListDatas != null ? mListDatas.size() : 0;
		}

		@Override
		public View getView(int position) {

			// final HomeIndex.Tunrs info = mListDatas.get(position);
			final HomeIndex.Tunrs info = mListDatas.get(position % mListDatas.size());
			View view = View.inflate(getContext(), R.layout.item_goods_propaganda, null);
			RoundImageView task_img = (RoundImageView) view.findViewById(R.id.task_img);
			TextView task_title = (TextView) view.findViewById(R.id.task_title);
			TextView task_label = (TextView) view.findViewById(R.id.task_label);
			TextView task_value = (TextView) view.findViewById(R.id.task_value);
			if (info != null) {
				ImageLoaderUtil.getInstance(BaseApplication.getInstance()).displayImage(info.photo, task_img, ImageLoaderUtil.default_options);
				task_title.setText(info.title);
				if (info.turn_type == 1)// 为1时不显示跳转按钮
				{
					task_label.setVisibility(View.INVISIBLE);
				} else {
					task_label.setText(info.button);
				}
				task_value.setText(info.content);
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (listence != null) {
							listence.onBannerClick(info);
						}
					}
				});

			}

			return view;
		}
	}

	/**
	 * 回调监听
	 */

	private BannerClickListener listence;

	public void addBannerClickListener(BannerClickListener clickListence) {
		this.listence = clickListence;
	}

	public interface BannerClickListener {
		void onBannerClick(HomeIndex.Tunrs info);
	}

	/**
	 * 启动轮播
	 */
	public void startAutoFlowTimer() {

		if (null == mHandler) {
			mHandler = new Handler(new Handler.Callback() {

				@Override
				public boolean handleMessage(Message msg) {
					if (mViewPager != null) {

//						try {
//							Field field = ViewPager.class.getDeclaredField("mScroller");
//							field.setAccessible(true);
//							FixedSpeedScroller scroller = new FixedSpeedScroller(getContext().getApplicationContext(), new DecelerateInterpolator());
//							field.set(mViewPager, scroller);
//							scroller.setmDuration(600);
//						} catch (Exception e) {
//						}

						mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
					}
					mHandler.sendMessageDelayed(Message.obtain(mHandler, 0), DELAY_MILLIS);
					return true;
				}
			});
		}
		mHandler.sendMessageDelayed(Message.obtain(mHandler, 0), DELAY_MILLIS);
	}

	// @Override
	// public boolean onInterceptTouchEvent(MotionEvent ev) {
	// final int action = ev.getAction();
	// switch (action) {
	// case MotionEvent.ACTION_DOWN:// 按下时取消轮播
	// if (mHandler != null)
	// mHandler.removeMessages(0);
	// break;
	//
	// case MotionEvent.ACTION_UP:// 抬起时开始轮播
	// if (mHandler != null) {
	// mHandler.sendMessageDelayed(Message.obtain(mHandler, 0), DELAY_MILLIS);
	// }
	// break;
	// }
	// return false;
	// }

	public void stop() {
		if (null != mHandler)
			mHandler.removeMessages(0);

	}

	/**
	 * 跳过ViewPager缓存的那几页
	 */

//	public void skipCacheItem() {
//
//		try {
//			Field field = ViewPager.class.getDeclaredField("mScroller");
//			field.setAccessible(true);
//			FixedSpeedScroller scroller = new FixedSpeedScroller(getContext().getApplicationContext(), new DecelerateInterpolator());
//			field.set(mViewPager, scroller);
//			scroller.setmDuration(0);
//		} catch (Exception e) {
//		}
//
//		if (mViewPager != null) {
//
//			if (mListDatas != null && mListDatas.size() > 0) {
//				mViewPager.setCurrentItem(mViewPager.getCurrentItem() + mListDatas.size());
//			}
//
//		}
//
//	}

	public void clearData() {

		try {
			Field field = ViewPager.class.getDeclaredField("mScroller");
			field.setAccessible(true);
			FixedSpeedScroller scroller = new FixedSpeedScroller(getContext().getApplicationContext(), new DecelerateInterpolator());
			field.set(mViewPager, scroller);
			scroller.setmDuration(0);
		} catch (Exception e) {
		}

		if (mViewPager != null) {

			if (mListDatas != null && mListDatas.size() > 0) {
				mViewPager.setCurrentItem(1);
			}

		}

	}

}
