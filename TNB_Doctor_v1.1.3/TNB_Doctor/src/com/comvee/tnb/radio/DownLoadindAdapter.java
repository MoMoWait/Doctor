package com.comvee.tnb.radio;

import java.text.DecimalFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.model.RadioAlbumItem;

public class DownLoadindAdapter extends BaseAdapter {
	private List<RadioAlbumItem> data;
	private Context ctx;
	private DecimalFormat decimalFormat = new DecimalFormat("0.0");
	private NotifyDatasetChangeInt notifyDatasetChangeInt;

	public void setNotifyDatasetChangeInt(NotifyDatasetChangeInt notifyDatasetChangeInt) {
		this.notifyDatasetChangeInt = notifyDatasetChangeInt;
	}

	public List<RadioAlbumItem> getData() {
		return data;
	}

	public DownLoadindAdapter(List<RadioAlbumItem> data, Context ctx) {
		this.data = data;
		this.ctx = ctx;
	}

	public void setdata(List<RadioAlbumItem> data) {
		this.data = data;
	}

	@Override
	public int getItemViewType(int position) {
		return data.get(position).downloadState;
	}

	@Override
	public int getViewTypeCount() {
		return 5;
	}

	@SuppressLint("CutPasteId")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DLWaitVH dlVh = null;
		DLingVH dlLingVH = null;
		DLPauseVH dlPauseVH = null;
		DLFailVH dlFailVH = null;
		RadioAlbumItem radioAlbumItem = data.get(position);
		int state = getItemViewType(position);
		if (convertView == null) {
			switch (state) {
			case 1:// 等待下载
				convertView = LayoutInflater.from(ctx).inflate(R.layout.waiting_download_item, parent, false);
				dlVh = new DLWaitVH();
				dlVh.titleTv = (TextView) convertView.findViewById(R.id.title);
				convertView.setTag(dlVh);
				break;
			case 2:// 下载中
				convertView = LayoutInflater.from(ctx).inflate(R.layout.downloading_item, parent, false);
				dlLingVH = new DLingVH();
				dlLingVH.titleTv = (TextView) convertView.findViewById(R.id.title);
				dlLingVH.progressBar = (ProgressBar) convertView.findViewById(R.id.progressbar);
				dlLingVH.loadedProgressTv = (TextView) convertView.findViewById(R.id.loaded_progress);
				dlLingVH.speedTv = (TextView) convertView.findViewById(R.id.speed);
				convertView.setTag(dlLingVH);
				break;
			case 3:// 暂停下载
				convertView = LayoutInflater.from(ctx).inflate(R.layout.download_pause_item, parent, false);
				dlPauseVH = new DLPauseVH();
				dlPauseVH.titleTv = (TextView) convertView.findViewById(R.id.title);
				convertView.setTag(dlPauseVH);
				break;
			case 4:// 下载失败
				convertView = LayoutInflater.from(ctx).inflate(R.layout.download_fail_item, parent, false);
				dlFailVH = new DLFailVH();
				dlFailVH.titleTv = (TextView) convertView.findViewById(R.id.title);
				convertView.setTag(dlFailVH);
				break;
			}
		}
		String title = radioAlbumItem.radioTitle;
		switch (state) {
		case 1:
			dlVh = (DLWaitVH) convertView.getTag();
			dlVh.titleTv.setText(title);
			break;
		case 2:
			dlLingVH = ((DLingVH) convertView.getTag());
			int loadedPer = radioAlbumItem.downloadedPer;
			float filesize = radioAlbumItem.fileSize;// B
			float speed = radioAlbumItem.speed;// 4.3KB/S
			dlLingVH.titleTv.setText(title);
			dlLingVH.loadedProgressTv.setText(decimalFormat.format(loadedPer * filesize / (100 * 1024 * 1024)) + "MB/"
					+ decimalFormat.format(filesize / (1024 * 1024)) + "MB");
			dlLingVH.progressBar.setProgress((int) (loadedPer));
			if (speed > 1024) {
				dlLingVH.speedTv.setText(decimalFormat.format(speed / 1024) + "M/s");
			} else {
				dlLingVH.speedTv.setText(speed + "K/s");
			}
			break;
		case 3:
			dlPauseVH = (DLPauseVH) convertView.getTag();
			dlPauseVH.titleTv.setText(title);
			break;
		case 4:
			dlFailVH = (DLFailVH) convertView.getTag();
			dlFailVH.titleTv.setText(title);
			break;
		}
		return convertView;

	}

	private class DLWaitVH {
		public TextView titleTv;
	}

	private class DLingVH {
		public TextView titleTv;
		public ProgressBar progressBar;
		public TextView speedTv;
		public TextView loadedProgressTv;
	}

	private class DLPauseVH {
		public TextView titleTv;

	}

	private class DLFailVH {
		public TextView titleTv;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private boolean isallPause() {
		boolean isAllpause = true;
		for (RadioAlbumItem radioAlbumItem : data) {
			if (radioAlbumItem.downloadState == 2) {
				isAllpause = false;
				break;
			}
		}
		return isAllpause;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		if (notifyDatasetChangeInt != null) {
			notifyDatasetChangeInt.isAllPause(isallPause());
		}
	}

	/**
	 * @author Administrator
	 * 
	 */
	public interface NotifyDatasetChangeInt {
		/**
		 * true 表 全部都为暂停状态
		 * 
		 * @param b
		 */
		public void isAllPause(boolean b);
	}
}
