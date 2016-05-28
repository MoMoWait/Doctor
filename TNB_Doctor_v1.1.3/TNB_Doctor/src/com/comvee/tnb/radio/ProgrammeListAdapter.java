package com.comvee.tnb.radio;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.util.ListViewHelper;

public class ProgrammeListAdapter extends BaseAdapter {
	private List<RadioAlbumItem> items;
	private final int colorSelect = TNBApplication.getInstance().getResources().getColor(R.color.txt_green);
	private final int colorUnSelect = TNBApplication.getInstance().getResources().getColor(R.color.txt_black);

	public ArrayList<RadioAlbumItem> getItems() {
		return (ArrayList<RadioAlbumItem>) items;
	}

	private Context context;

	public ProgrammeListAdapter() {
		this.context = TNBApplication.getInstance();
	}

	public void setData(List<RadioAlbumItem> items) {
		this.items = items;
	}

	@Override
	public int getCount() {
		return items == null ? 0 : items.size();
	}

	@Override
	public RadioAlbumItem getItem(int arg0) {
		return items.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View converView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (converView == null) {
			converView = LayoutInflater.from(context).inflate(R.layout.item_programme_normal, arg2, false);
			holder = ListViewHelper.getViewHolderByView(ViewHolder.class, converView);
			converView.setTag(holder);
		} else {
			holder = (ViewHolder) converView.getTag();
		}
		RadioAlbumItem modle = items.get(position);
		if (TextUtils.isEmpty(modle.updateTime)) {
			holder.tv_time.setVisibility(View.GONE);
		} else {
			holder.tv_time.setVisibility(View.VISIBLE);
			holder.tv_time.setText(modle.updateTime);
		}
		holder.tv_title.setText(modle.radioTitle);
		holder.tv_timelong.setText(RadioUtil.getMedaioDurationString(modle.timeLong));

		if (modle.locationHas) {
			holder.tv_timelong.setCompoundDrawablesWithIntrinsicBounds(R.drawable.prog6, 0, R.drawable.radio_download_complete, 0);
		} else {
			holder.tv_timelong.setCompoundDrawablesWithIntrinsicBounds(R.drawable.prog6, 0, 0, 0);
		}

		RadioAlbumItem curPlay = RadioPlayerMrg.getInstance().getCurrent();
		if (curPlay != null && curPlay.radioId.equals(modle.radioId)) {
			onPlaying(holder, modle);
		} else if (modle.state == 2) {
			onUnPlaying(holder, modle);
		} else {
			onUnPlaying(holder, modle);
		}

		return converView;
	}

	private AnimationDrawable mAnim;

	// 在播放状态或是被选择（暂停的）
	private void onPlaying(ViewHolder holder, RadioAlbumItem modle) {
		mAnim = (AnimationDrawable) holder.iv_photo.getDrawable();
		if (RadioPlayerMrg.getInstance().isPlaying()) {
			holder.iv_photo.setVisibility(View.VISIBLE);
			mAnim.start();
		} else {
			holder.iv_photo.setVisibility(View.GONE);
			mAnim.stop();
		}
		holder.tv_title.setTextColor(colorSelect);
		holder.tv_timelong.setTextColor(colorSelect);
		holder.tv_time.setTextColor(colorSelect);
	}

	// 不在播放状态
	private void onUnPlaying(ViewHolder holder, RadioAlbumItem modle) {
		holder.tv_title.setTextColor(colorUnSelect);
		holder.tv_timelong.setTextColor(colorUnSelect);
		holder.tv_time.setTextColor(colorUnSelect);
		holder.iv_photo.setVisibility(View.GONE);
		mAnim = (AnimationDrawable) holder.iv_photo.getDrawable();
		mAnim.stop();
	}

	public static class ViewHolder {
		public TextView tv_title;
		public TextView tv_time;
		public TextView tv_timelong;
		public ImageView iv_photo;
	}
}
