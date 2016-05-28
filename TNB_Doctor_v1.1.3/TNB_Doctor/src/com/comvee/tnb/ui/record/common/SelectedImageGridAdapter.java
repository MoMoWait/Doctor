package com.comvee.tnb.ui.record.common;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tool.ImageLoaderUtil;

public class SelectedImageGridAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private ArrayList<ImageItem4LocalImage> datas;
	private OnDeleteListener onDeleteListener;

	private List<ImageView> allImageView = new ArrayList<ImageView>();

	public OnDeleteListener getOnDeleteListener() {
		return onDeleteListener;
	}

	public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
		this.onDeleteListener = onDeleteListener;
	}

	public SelectedImageGridAdapter(Activity activity, ArrayList<ImageItem4LocalImage> datas_) {
		this.activity = activity;
		this.datas = datas_;
		inflater = LayoutInflater.from(activity.getApplicationContext());
	}

	public int getCount() {
		if (datas.size() == 9) {
			return 9;
		}
		return (datas.size() + 1);
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_record_select_laboratory_gridview, parent, false);
			holder = new ViewHolder();
			holder.itemlayout = (ViewGroup) convertView.findViewById(R.id.itemlayout);
			holder.imageSource = (ImageView) convertView.findViewById(R.id.select_img);
			holder.imageDel = (ImageView) convertView.findViewById(R.id.del);
			holder.tvFail = (TextView) convertView.findViewById(R.id.tv_fail);
			holder.pbUploading = (ProgressBar) convertView.findViewById(R.id.pb_loading);
			holder.rlUploadState = (RelativeLayout) convertView.findViewById(R.id.rl_uploadstate);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == datas.size()) {
			holder.imageSource.setImageBitmap(BitmapFactory.decodeResource(activity.getResources(), R.drawable.hyd_01));
			holder.imageDel.setVisibility(View.GONE);
			holder.rlUploadState.setVisibility(View.GONE);
			if (position == 9) {
				holder.imageSource.setVisibility(View.GONE);
			}
		} else {
			ImageItem4LocalImage current = datas.get(position);
			switch (current.uploadState) {
			case 1:// 未上传
				holder.imageDel.setVisibility(View.VISIBLE);
				holder.rlUploadState.setVisibility(View.GONE);
				break;
			case 2:// 上传中
				holder.imageDel.setVisibility(View.GONE);
				holder.rlUploadState.setVisibility(View.VISIBLE);
				holder.pbUploading.setVisibility(View.VISIBLE);
				holder.tvFail.setVisibility(View.GONE);

				break;
			case 3:// 上传成功

				break;
			case 4:// 上传失败
				holder.imageDel.setVisibility(View.VISIBLE);
				holder.rlUploadState.setVisibility(View.VISIBLE);
				holder.pbUploading.setVisibility(View.GONE);
				holder.tvFail.setVisibility(View.VISIBLE);
				break;

			}

			if (!TextUtils.isEmpty(current.smallImagePath)) {// 网络图片
				ImageLoaderUtil.getInstance(activity).displayImage(current.smallImagePath, holder.imageSource, ImageLoaderUtil.default_options);
			} else if (current.drawableThumb != null) {// 缩略图
				holder.imageSource.setImageDrawable(datas.get(position).drawableThumb);
			} else if (!TextUtils.isEmpty(current.imagePath)) {// 拍照图片
				try {
					holder.imageSource.setImageBitmap(MyBitmapFactory.revitionImageSize(current.imagePath));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			holder.imageDel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onDeleteListener.onDelete(position, parent);
				}
			});
		}
		if (allImageView.size() <= position) {
			allImageView.add(holder.imageSource);
		}
		return convertView;
	}

	public List<ImageView> getAllImageView() {
		return allImageView;
	}

	public class ViewHolder {
		public ViewGroup itemlayout;
		public ImageView imageSource;
		public ImageView imageDel;
		public TextView tvFail;
		public ProgressBar pbUploading;
		public RelativeLayout rlUploadState;
	}

	public interface OnDeleteListener {
		void onDelete(int position, ViewGroup parent);
	}
}