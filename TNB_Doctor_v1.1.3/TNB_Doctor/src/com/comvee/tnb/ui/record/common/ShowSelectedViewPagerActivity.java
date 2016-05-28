package com.comvee.tnb.ui.record.common;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.ui.widget.zoom.PhotoView;
import com.comvee.ui.widget.zoom.PhotoViewAttacher.OnViewTapListener;

/**
 * 查看已选图片
 * 
 * @author PXL
 */
@SuppressLint("ValidFragment")
public class ShowSelectedViewPagerActivity extends Activity implements OnClickListener {

	private ViewPager viewPager;
	private int currentIndex;
	public LayoutInflater mlayInflater;

	private RelativeLayout titlebarLayout;
	private TextView finishTv;
	private TextView titleTv;
	private ImageView leftIv;
	private ImageView selectIv;

	boolean[] stateSelectArray;

	List<ImageItem4LocalImage> datas;

	boolean isfullScreen = false;

	int type = 1;// 1表相册中预览的，2表示编辑界面预览的

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_selectedimage_viewpager);
		mlayInflater = LayoutInflater.from(getApplicationContext());
		findViewById();

		Intent it = getIntent();
		currentIndex = it.getIntExtra("current", 0);
		type = it.getIntExtra("type", 1);
		datas = new ArrayList<ImageItem4LocalImage>();
		switch (type) {
		case 1:
			datas.addAll(MyBitmapFactory.tempSelectBitmapInAlbum);
			break;
		case 2:
			datas.addAll(MyBitmapFactory.tempSelectBitmap);
		default:
			break;
		}
		stateSelectArray = new boolean[datas.size()];

		titleTv.setText(currentIndex + 1 + "/" + datas.size());
		finishTv.setText(String.format("完成(%d)", datas.size()));

		viewPager.setAdapter(new ViewpagerAdapter());
		viewPager.setOnPageChangeListener(new ViewpageChangeAdapter());
		viewPager.setCurrentItem(currentIndex);

		leftIv.setOnClickListener(this);
		selectIv.setOnClickListener(this);
		finishTv.setOnClickListener(this);

	}

	private void findViewById() {
		titlebarLayout = (RelativeLayout) findViewById(R.id.titlebar);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		finishTv = (TextView) findViewById(R.id.finish);
		titleTv = (TextView) findViewById(R.id.title);
		leftIv = (ImageView) findViewById(R.id.back);
		selectIv = (ImageView) findViewById(R.id.select);

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

		@SuppressLint("NewApi")
		@Override
		public Object instantiateItem(View container, int position) {
			View view = mlayInflater.inflate(R.layout.item_viewpager_bigpic, null);
			view.findViewById(R.id.progressbar).setVisibility(View.GONE);
			final PhotoView imageView = (PhotoView) view.findViewById(R.id.pic);

			final ImageItem4LocalImage imageItem4LocalImage = datas.get(position);
			if (TextUtils.isEmpty(imageItem4LocalImage.imagePath)) {
				ImageLoaderUtil.getInstance(getApplicationContext()).displayImage(imageItem4LocalImage.sourceImagePath, imageView,
						ImageLoaderUtil.default_options);
			} else {
				ImageLoaderUtil.getInstance(getApplicationContext()).displayImage("file://" + imageItem4LocalImage.imagePath, imageView,
						ImageLoaderUtil.default_options);
			}
			((ViewPager) container).addView(view);
			imageView.setOnViewTapListener(new OnViewTapListener() {

				@Override
				public void onViewTap(View view, float x, float y) {
					PhotoView pnPhotoView = (PhotoView) view;
					float scale = pnPhotoView.getScale();
					if (isfullScreen) {
						titlebarLayout.setVisibility(View.VISIBLE);
						finishTv.setVisibility(View.VISIBLE);
						//view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
					} else {
						//view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
						titlebarLayout.setVisibility(View.GONE);
						finishTv.setVisibility(View.GONE);
					}
					isfullScreen = !isfullScreen;
					pnPhotoView.zoomTo(scale, x, y);

				}
			});

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
			currentIndex = arg0;
			titleTv.setText(arg0 + 1 + "/" + datas.size());
			if (stateSelectArray[currentIndex]) {
				selectIv.setImageResource(R.drawable.hyd_42);
			} else {
				selectIv.setImageResource(R.drawable.hyd_44);
			}
		}

	}

	@Override
	public void onClick(View v) {
		if (v == finishTv) {
			List<ImageItem4LocalImage> initData = new ArrayList<ImageItem4LocalImage>();
			initData.addAll(datas);
			for (int i = 0; i < stateSelectArray.length; i++) {
				if (stateSelectArray[i]) {
					if (type == 1) {
						MyBitmapFactory.tempSelectBitmapInAlbum.remove(initData.get(i));
					} else {
						MyBitmapFactory.tempSelectBitmap.remove(initData.get(i));
					}
				}
			}
			MyBitmapFactory.tempSelectBitmap.addAll(MyBitmapFactory.tempSelectBitmapInAlbum);
			MyBitmapFactory.canback = false;
			finish();

		} else if (v == leftIv) {
			onBackPressed();
		} else if (v == selectIv) {
			stateSelectArray[currentIndex] = !stateSelectArray[currentIndex];

			if (stateSelectArray[currentIndex]) {
				selectIv.setImageResource(R.drawable.hyd_42);
			} else {
				selectIv.setImageResource(R.drawable.hyd_44);
			}
			int size = 0;
			for (int j = 0; j < stateSelectArray.length; j++) {
				if (!stateSelectArray[j]) {
					size++;
				}
			}
			finishTv.setText(String.format("完成(%d)", size));
			if (type == 1) {
				if (stateSelectArray[currentIndex]) {
					MyBitmapFactory.tempSelectBitmapInAlbum.remove(datas.get(currentIndex));
				} else {
					MyBitmapFactory.tempSelectBitmapInAlbum.add(datas.get(currentIndex));
				}
			} else {
			}
		}
	}
}
