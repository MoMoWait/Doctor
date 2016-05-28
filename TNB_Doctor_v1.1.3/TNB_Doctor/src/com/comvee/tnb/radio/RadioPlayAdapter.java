package com.comvee.tnb.radio;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.util.ListViewHelper;

public class RadioPlayAdapter extends BaseAdapter {

	public static class ViewHolder {
		public TextView tv_label;
		public TextView tv_time;
	}

	@Override
	public int getCount() {
		return RadioPlayerMrg.getInstance().getAudioListSize();
	}

	@Override
	public Object getItem(int position) {
		return RadioPlayerMrg.getInstance().getAudio(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(TNBApplication.getInstance(), R.layout.radio_play_item, null);
			holder = ListViewHelper.getViewHolderByView(ViewHolder.class, convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		RadioAlbumItem obj = RadioPlayerMrg.getInstance().getAudio(position);
		holder.tv_label.setText(obj.radioTitle);

		return convertView;
	}

}
