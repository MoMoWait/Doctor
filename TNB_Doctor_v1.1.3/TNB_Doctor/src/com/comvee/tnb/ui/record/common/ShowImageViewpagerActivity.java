package com.comvee.tnb.ui.record.common;

import java.io.Serializable;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.comvee.tnb.R;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UITool;
import com.comvee.ui.widget.zoom.PhotoView;
import com.comvee.ui.widget.zoom.PhotoViewAttacher.OnViewTapListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ShowImageViewpagerActivity extends Activity {

	private List<NetPic> datas;
	private int initIndex;
	private ViewPager viewPager;
	private int[] locXs;
	private int[] locYs;
	private int[] locWs;
	private int[] locHs;
	private boolean[] isloaded;// 用于标记是否加载过,加载过则不显示progressbar
	private Bitmap bitmapsSmall;
	private LayoutInflater mlayInflater;
	private final int indicatorPadding = 3;// dp
	private boolean isFirstLuancher = true;
	public ViewGroup indicatorViewGroup;
	private SparseArray<PhotoView> map = new SparseArray<PhotoView>();
	public DisplayImageOptions mOptions;

	@SuppressWarnings({ "unchecked", "deprecation" })
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_image_viewpager);
		Intent it = getIntent();

		datas = (List<NetPic>) it.getSerializableExtra("data");
		isloaded = new boolean[datas.size()];
		initIndex = it.getIntExtra("current", 0);
		locXs = it.getIntArrayExtra("locationxs");
		locYs = it.getIntArrayExtra("locationys");
		locWs = it.getIntArrayExtra("widths");
		locHs = it.getIntArrayExtra("heights");
		bitmapsSmall = it.getParcelableExtra("bitmap");
		mOptions = new DisplayImageOptions.Builder().cacheInMemory(true) // 缓存用
				.cacheOnDisc(true) // 缓存
				.build();

		mlayInflater = LayoutInflater.from(getApplicationContext());

		viewPager = (ViewPager) findViewById(R.id.viewpager);
		indicatorViewGroup = (ViewGroup) findViewById(R.id.indicator_layout);
		viewPager.setAdapter(new ViewpagerAdapter());
		viewPager.setOnPageChangeListener(new ViewpageChangeAdapter());
		addIndicator();
		viewPager.setCurrentItem(initIndex);
		viewPager.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		refreshIndicatorView(initIndex);
	}

	public static Intent getIntent(Activity act, List<NetPic> datas, int position, List<ImageView> imageViews) {
		Intent it = new Intent(act, ShowImageViewpagerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("data", (Serializable) datas);
		bundle.putInt("current", position);
		int[] locxs = new int[imageViews.size()];
		int[] locys = new int[imageViews.size()];
		int[] widhts = new int[imageViews.size()];
		int[] heights = new int[imageViews.size()];
		for (int i = 0; i < imageViews.size(); i++) {
			int[] location = new int[2];
			ImageView iv = imageViews.get(i);
			iv.getLocationOnScreen(location);
			locxs[i] = location[0];
			locys[i] = location[1];
			widhts[i] = iv.getWidth();
			heights[i] = iv.getHeight();
		}

		bundle.putIntArray("locationxs", locxs);
		bundle.putIntArray("locationys", locys);
		bundle.putIntArray("widths", widhts);
		bundle.putIntArray("heights", heights);
		Bitmap img = drawableToBitamp(imageViews.get(position).getDrawable());
		bundle.putParcelable("bitmap", img);
		it.putExtras(bundle);
		return it;
	}

	private class ViewpagerAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(View container, final int position) {

			View view = mlayInflater.inflate(R.layout.item_viewpager_bigpic, null);
			final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
			progressBar.setVisibility(View.GONE);
			final PhotoView photoView = (PhotoView) view.findViewById(R.id.pic);
			photoView.setOriginalInfo(locWs[position], locHs[position], locXs[position], locYs[position]);
			photoView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
			photoView.setScaleType(ScaleType.FIT_CENTER);
			photoView.setOnTransformListener(new PhotoView.TransformListener() {
				@Override
				public void onTransformComplete(int mode) {
					if (mode == 1) {
						if (!TextUtils.isEmpty(datas.get(position).localPath)) {
							ImageLoaderUtil.getInstance(getApplicationContext()).displayImage("file://" + datas.get(position).localPath, photoView,
									mOptions);
						} else {
							ImageLoaderUtil.getInstance(getApplicationContext()).displayImage(datas.get(position).picBig, photoView, mOptions,
									new MImageLoadingListener(progressBar, position));
						}
					}
				}
			});
			if (position == initIndex && isFirstLuancher) {
				photoView.transformIn();
				isFirstLuancher = false;
				photoView.setImageBitmap(bitmapsSmall);
			} else {
				if (!TextUtils.isEmpty(datas.get(position).localPath)) {
					ImageLoaderUtil.getInstance(getApplicationContext()).displayImage("file://" + datas.get(position).localPath, photoView, mOptions);
				} else {
					ImageLoaderUtil.getInstance(getApplicationContext()).displayImage(datas.get(position).picBig, photoView, mOptions,
							new MImageLoadingListener(progressBar, position));
				}
			}
			photoView.setOnViewTapListener(new OnViewTapListener() {
				@Override
				public void onViewTap(View view, float x, float y) {
					onBackPressed();
				}
			});
			view.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
			((ViewPager) container).addView(view);
			map.put(position, photoView);
			return view;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}
	}

	private class ViewpageChangeAdapter implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			refreshIndicatorView(arg0);
		}

	}

	private void refreshIndicatorView(int position) {
		ImageView currentIndicatorImg = null;
		for (int i = 0; i < datas.size(); i++) {
			ImageView img = (ImageView) indicatorViewGroup.getChildAt(i);
			img.setImageResource(R.drawable.shape_solid_gray_circle1);
			if (i == position) {
				currentIndicatorImg = img;
			}
		}
		if (currentIndicatorImg != null)
			currentIndicatorImg.setImageResource(R.drawable.shape_solid_while_circle);
	}

	private void addIndicator() {
		int paddingPX = UITool.dip2px(getApplicationContext(), indicatorPadding);
		for (int i = 0; i < datas.size(); i++) {
			ImageView imageView = new ImageView(getApplicationContext());
			imageView.setImageResource(R.drawable.shape_solid_gray_circle1);
			imageView.setPadding(paddingPX, paddingPX, paddingPX, paddingPX);
			indicatorViewGroup.addView(imageView);
		}
	}

	private static Bitmap drawableToBitamp(Drawable drawable) {
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		System.out.println("Drawable转Bitmap");
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
		return bitmap;
	}

	private class MImageLoadingListener implements ImageLoadingListener {
		private int position;

		private void hideProgressBar() {
			AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
			alphaAnimation.setDuration(1000);
			progressBar.startAnimation(alphaAnimation);
			alphaAnimation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					progressBar.setVisibility(View.GONE);
					isloaded[position] = true;
				}
			});
		}

		private ProgressBar progressBar;

		public MImageLoadingListener(ProgressBar progressBar, int pos) {
			this.progressBar = progressBar;
			this.position = pos;
		}

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {
			hideProgressBar();
		}

		@Override
		public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
			hideProgressBar();
		}

		@Override
		public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			hideProgressBar();
		}

		@Override
		public void onLoadingStarted(String arg0, View arg1) {
			if (!isloaded[position]) {
				progressBar.setVisibility(View.VISIBLE);
			}
		}

	}

	@Override
	public void onBackPressed() {
		indicatorViewGroup.setVisibility(View.INVISIBLE);
		int currentPos = viewPager.getCurrentItem();
		final PhotoView imageView = map.get(currentPos);
		if (currentPos == initIndex) {
			imageView.setImageBitmap(bitmapsSmall);
		}
		imageView.setOnTransformListener(new PhotoView.TransformListener() {
			@Override
			public void onTransformComplete(int mode) {
				if (mode == 2) {
					imageView.setVisibility(View.INVISIBLE);
					finish();
				}
			}
		});
		imageView.transformOut();
	}

	@Override
	protected void onDestroy() {
		if (bitmapsSmall != null) {
			try {
				if (!bitmapsSmall.isRecycled()) {
					bitmapsSmall.recycle();
				}
			} catch (Exception e) {
				Log.e("comvee", e.getMessage(), e);
			}
		}
		super.onDestroy();
	}
}
