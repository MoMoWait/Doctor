package com.comvee.tnb.radio;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.model.RadioGroup;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.util.ListViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 栏目列表（更多）适配器
 * 
 * @author Administrator
 * 
 */
public class RadioAblumListAdapter extends BaseAdapter {
	private ArrayList<RadioGroup.RadioAlbum> mList;
	private ImageLoader imgLoader = ImageLoaderUtil.getInstance(TNBApplication.getInstance());
	public void addList(ArrayList<RadioGroup.RadioAlbum> list) {
		if (mList == null) {
			mList = new ArrayList<RadioGroup.RadioAlbum>();
		}
		mList.clear();
		mList.addAll(list);
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public RadioGroup.RadioAlbum getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public static class ViewHolder {
		public TextView tv_label;
		public ImageView iv_photo;
		public TextView tv_time;
		public TextView tv_content;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(TNBApplication.getInstance(), R.layout.radio_ablumlist_item, null);
			holder = ListViewHelper.getViewHolderByView(ViewHolder.class, convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		RadioGroup.RadioAlbum item = getItem(position);
		imgLoader.displayImage(item.photoUrl, holder.iv_photo);
		holder.tv_label.setText(item.radioTitle);
		holder.tv_content.setText(item.radioSubhead);
		holder.tv_time.setText(item.updateTime);
		return convertView;
	}

}
