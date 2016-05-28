package com.comvee.tnb.ui.heathknowledge;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.media.Image;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.comvee.BaseApplication;
import com.comvee.tnb.R;
import com.comvee.tnb.ui.book.BookWebActivity;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UITool;

public class HealthViewPagerAdapter extends PagerAdapter implements OnPageChangeListener, OnClickListener {
	List<View> viewLists = new ArrayList<View>();
	List<HeathViewPageModel> viewSourceUriList;
	ViewGroup indicatorViewGroup;
	Activity context;
	boolean carrousel;
	final int indicatorPadding = 3;// dp

	public HealthViewPagerAdapter(List<HeathViewPageModel> sourceUriList, ViewGroup indicatorViewGroup, Activity context, boolean carrousel) {
		this.viewSourceUriList = sourceUriList;
		this.indicatorViewGroup = indicatorViewGroup;
		this.context = context;
		this.carrousel = carrousel;
		if (viewSourceUriList == null || viewSourceUriList.size() == 0) {
			return;
		}
		initItemView();
		if (indicatorViewGroup != null)
			addIndicator();
	}

	private View createItemView(HeathViewPageModel info){
		View view = View.inflate(BaseApplication.getInstance(),R.layout.know_banner_item,null);
		ImageView iv = (ImageView) view.findViewById(R.id.iv_photo);
		TextView tv= (TextView) view.findViewById(R.id.tv_title);
		ImageLoaderUtil.getInstance(context).displayImage(info.getTurnsPlayUrl(), iv, ImageLoaderUtil.default_options);
		tv.setText(info.getHotSpotTitle());
		view.setTag(info);
		view.setOnClickListener(this);

		return view;
	}

	private void initItemView() {
		for (int i = 0; i < viewSourceUriList.size(); i++) {

			View img =createItemView(viewSourceUriList.get(i));



//			ImageView img = new ImageView(context);
//			img.setScaleType(ScaleType.FIT_XY);
//			ImageLoaderUtil.getInstance(context).displayImage(viewSourceUriList.get(i).getTurnsPlayUrl(), img, ImageLoaderUtil.default_options);
//			img.setTag(viewSourceUriList.get(i));
//			img.setOnClickListener(this);
			viewLists.add(img);
		}
		if (viewSourceUriList.size() < 4 && carrousel) {// 如果不满4个又需要轮播，需要填充
			for (int i = 0; i < 4 - viewSourceUriList.size(); i++) {
				View img =createItemView(viewSourceUriList.get(i % viewSourceUriList.size()));

//				ImageView img = new ImageView(context);
//				img.setScaleType(ScaleType.FIT_XY);
//				ImageLoaderUtil.getInstance(context).displayImage(viewSourceUriList.get(i % viewSourceUriList.size()).getTurnsPlayUrl(), img,
//						ImageLoaderUtil.default_options);
//				img.setTag(viewSourceUriList.get(i % viewSourceUriList.size()));
//				img.setOnClickListener(this);
				viewLists.add(img);
			}
		}
	}

	private void addIndicator() {
		int paddingPX = UITool.dip2px(context, indicatorPadding);
		for (int i = 0; i < viewSourceUriList.size(); i++) {
			ImageView imageView = new ImageView(context);
			imageView.setImageResource(R.drawable.shape_solid_gray_circle);
			imageView.setPadding(paddingPX, paddingPX, paddingPX, paddingPX);
			indicatorViewGroup.addView(imageView);
		}
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
	public void destroyItem(View view, int position, Object object) {
		((ViewPager) view).removeView(viewLists.get(position % viewLists.size()));
	}

	@Override
	public Object instantiateItem(View view, int position) {
		((ViewPager) view).addView(viewLists.get(position % viewLists.size()));
		View returnView = viewLists.get(position % viewLists.size());
		return returnView;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		if (indicatorViewGroup != null)
			refreshIndicatorView(position);
	}

	private void refreshIndicatorView(int position) {
		ImageView currentIndicatorImg = null;
		for (int i = 0; i < viewSourceUriList.size(); i++) {
			ImageView img = (ImageView) indicatorViewGroup.getChildAt(i);
			img.setImageResource(R.drawable.shape_solid_gray_circle);
			if (i == position % viewSourceUriList.size()) {
				currentIndicatorImg = img;
			}
		}
		if (currentIndicatorImg != null)
			currentIndicatorImg.setImageResource(R.drawable.shape_solid_while_circle);

	}

	@Override
	public void onClick(View view) {
		HeathViewPageModel model = (HeathViewPageModel) view.getTag();
		// FragmentMrg.toFragment((MainActivity) context, new
		// BookWebFragment(model.getUrl(), model.getHot_spot_id()), true, true);
		BookWebActivity.toWebActivity(context, model.getType() == 0 ? BookWebActivity.MESSAGE : model.getType(), null, null, model.getUrl(),
				model.getHot_spot_id());
	}
}