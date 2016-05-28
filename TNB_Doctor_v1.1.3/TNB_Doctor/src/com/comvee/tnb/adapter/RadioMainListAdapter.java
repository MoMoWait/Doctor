package com.comvee.tnb.adapter;

import java.util.ArrayList;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.model.RadioGroup;
import com.comvee.tnb.model.RadioGroup.RadioAlbum;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UITool;
import com.comvee.util.ListViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RadioMainListAdapter extends BaseAdapter {

	public static class ViewHolder {
		public TextView tv_group;
		public TextView tv_label1;
		public TextView tv_label2;
		public TextView tv_label3;
		public ImageView iv_photo1;
		public ImageView iv_photo2;
		public ImageView iv_photo3;
		public TextView tv_title1;
		public TextView tv_title2;
		public TextView tv_title3;
		public TextView tv_more;
	}

	private ImageLoader loader = ImageLoaderUtil.getInstance(TNBApplication.getInstance());
	private ArrayList<RadioGroup> mListItem;
	private OnClickListener mListener;
	private int viewWidth;

	public void setOnClickListener(OnClickListener mListener) {
		this.mListener = mListener;
	}

	public void setListItems(ArrayList<RadioGroup> mListItem) {
		this.mListItem = mListItem;
	}

	public RadioMainListAdapter() {
		viewWidth = getItemWidth();
	}

	@Override
	public int getCount() {
		return mListItem == null ? 0 : mListItem.size();
	}

	@Override
	public RadioGroup getItem(int arg0) {
		return mListItem.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View containView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (containView == null) {
			containView = View.inflate(TNBApplication.getInstance(), R.layout.radio_main_item, null);
			holder = ListViewHelper.getViewHolderByView(ViewHolder.class, containView);
			containView.setTag(holder);
		} else {
			holder = (ViewHolder) containView.getTag();
		}

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(viewWidth, viewWidth);
		holder.iv_photo1.setLayoutParams(params);
		holder.iv_photo2.setLayoutParams(params);
		holder.iv_photo3.setLayoutParams(params);
		RadioGroup group = getItem(position);

		holder.tv_group.setText(group.radioTypeName);
		holder.tv_more.setTag(group);
		holder.tv_more.setOnClickListener(mListener);

		try {
			RadioAlbum item1 = group.subList.get(0);

			holder.iv_photo1.setVisibility(View.VISIBLE);
			holder.tv_title1.setVisibility(View.VISIBLE);
			if (item1.radioType == 3) {
				holder.tv_label1.setVisibility(View.GONE);
				holder.tv_title1.setText(item1.radioTitle);
			} else {
				holder.tv_label1.setVisibility(View.VISIBLE);
				holder.tv_label1.setText(item1.radioTitle);
				holder.tv_title1.setText(item1.radioSubhead);
			}
			loader.displayImage(item1.photoUrl, holder.iv_photo1, ImageLoaderUtil.null_defult);

			holder.tv_label1.setTag(item1);
			holder.tv_title1.setTag(item1);
			holder.iv_photo1.setTag(item1);

			holder.iv_photo1.setOnClickListener(mListener);
			holder.tv_title1.setOnClickListener(mListener);
			holder.tv_label1.setOnClickListener(mListener);

		} catch (Exception e) {
			holder.iv_photo1.setVisibility(View.GONE);
			holder.tv_label1.setVisibility(View.GONE);
			holder.tv_title1.setVisibility(View.GONE);
		}
		try {
			RadioAlbum item2 = group.subList.get(1);
			holder.iv_photo2.setVisibility(View.VISIBLE);
			holder.tv_label2.setVisibility(View.VISIBLE);
			holder.tv_title2.setVisibility(View.VISIBLE);

			if (item2.radioType == 3) {
				holder.tv_label2.setVisibility(View.GONE);
				holder.tv_title2.setText(item2.radioTitle);
			} else {
				holder.tv_label2.setVisibility(View.VISIBLE);
				holder.tv_label2.setText(item2.radioTitle);
				holder.tv_title2.setText(item2.radioSubhead);
			}
			loader.displayImage(item2.photoUrl, holder.iv_photo2, ImageLoaderUtil.null_defult);

			holder.tv_label2.setTag(item2);
			holder.tv_title2.setTag(item2);
			holder.iv_photo2.setTag(item2);

			holder.iv_photo2.setOnClickListener(mListener);
			holder.tv_title2.setOnClickListener(mListener);
			holder.tv_label2.setOnClickListener(mListener);

		} catch (Exception e) {
			holder.iv_photo2.setVisibility(View.GONE);
			holder.tv_label2.setVisibility(View.GONE);
			holder.tv_title2.setVisibility(View.GONE);
		}
		try {
			RadioAlbum item3 = group.subList.get(2);
			holder.iv_photo3.setVisibility(View.VISIBLE);
			holder.tv_label3.setVisibility(View.VISIBLE);
			holder.tv_title3.setVisibility(View.VISIBLE);

			if (item3.radioType == 3) {
				holder.tv_label3.setVisibility(View.GONE);
				holder.tv_title3.setText(item3.radioTitle);
			} else {
				holder.tv_label2.setVisibility(View.VISIBLE);
				holder.tv_label3.setText(item3.radioTitle);
				holder.tv_title3.setText(item3.radioSubhead);
			}
			loader.displayImage(item3.photoUrl, holder.iv_photo3, ImageLoaderUtil.null_defult);

			holder.tv_label3.setTag(item3);
			holder.tv_title3.setTag(item3);
			holder.iv_photo3.setTag(item3);

			holder.iv_photo3.setOnClickListener(mListener);
			holder.tv_title3.setOnClickListener(mListener);
			holder.tv_label3.setOnClickListener(mListener);
		} catch (Exception e) {
			holder.iv_photo3.setVisibility(View.GONE);
			holder.tv_label3.setVisibility(View.GONE);
			holder.tv_title3.setVisibility(View.GONE);
		}
		return containView;
	}

	private int getItemWidth() {
		int viewWidth = (int) ((UITool.getDisplayWidth(TNBApplication.getInstance()) - UITool.dip2px(TNBApplication.getInstance(), 40)) / 3);

		return viewWidth;
	}
}
