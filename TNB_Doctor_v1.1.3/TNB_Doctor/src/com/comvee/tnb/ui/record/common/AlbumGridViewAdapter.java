package com.comvee.tnb.ui.record.common;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.comvee.tnb.R;
import com.comvee.tnb.ui.record.laboratory.BitmapCache;
import com.comvee.tnb.ui.record.laboratory.BitmapCache.ImageCallback;

/**
 * 这个是显示一个文件夹里面的所有图片时用的适配器
 * 
 */
public class AlbumGridViewAdapter extends BaseAdapter {
	LayoutInflater layoutInflater;
	int itemGridViewHeight;

	private List<ImageItem4LocalImage> dataList;

	BitmapCache cache;
	ImageCallback callback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				} else {
					Log.e("tag", "callback, bmp not match");
				}
			} else {
				Log.e("tag", "callback, bmp null");
			}
		}
	};

	public AlbumGridViewAdapter(int itemGridViewHeight, Context c, List<ImageItem4LocalImage> dataList) {
		this.itemGridViewHeight = itemGridViewHeight;
		layoutInflater = LayoutInflater.from(c);
		cache = new BitmapCache();

		this.dataList = dataList;
	}

	public int getCount() {
		return dataList.size();
	}

	public Object getItem(int position) {
		return dataList.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	private class ViewHolder {
		public RelativeLayout parent;
		public ImageView imageView;
		public ImageView seleteStateView;
		public View frontView;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.item_record_selectimage_gridview, null);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.parent = (RelativeLayout) convertView.findViewById(R.id.parentLayout);
			viewHolder.seleteStateView = (ImageView) convertView.findViewById(R.id.state_select);
			viewHolder.frontView = convertView.findViewById(R.id.front);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		ImageItem4LocalImage imageItem = dataList.get(position);

		viewHolder.imageView.setTag(imageItem.getImagePath());
		cache.displayBmp(viewHolder.imageView, imageItem.thumbnailPath, imageItem.imagePath, callback);
		LayoutParams layoutParams = viewHolder.parent.getLayoutParams();
		layoutParams.height = itemGridViewHeight;

		if (MyBitmapFactory.tempSelectBitmapInAlbum.contains(dataList.get(position))) {
			viewHolder.frontView.setVisibility(View.VISIBLE);
			viewHolder.seleteStateView.setImageResource(R.drawable.hyd_40);
			
		} else {
			viewHolder.frontView.setVisibility(View.GONE);
			viewHolder.seleteStateView.setImageResource(R.drawable.hyd_38);
		}
		return convertView;
	}
}
