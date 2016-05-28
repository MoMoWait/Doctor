package com.comvee.tnb.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.ui.remind.TimeRemindTransitionInfo;

public class PharmacyTimesAdapter extends BaseAdapter {
	private List<TimeRemindTransitionInfo> mList;
	private Activity context;
	TimeRemindTransitionInfo mInfo;
	private UpdataAddTimeView mAddTimeView;

	public void setList(List<TimeRemindTransitionInfo> mList) {
		this.mList = mList;
	}

	public PharmacyTimesAdapter(Activity context, List<TimeRemindTransitionInfo> list, UpdataAddTimeView addTimeView) {
		this.context = context;
		this.mList = list;
		this.mAddTimeView = addTimeView;
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList == null ? null : mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		mInfo = mList.get(arg0);
		final ViewHold views;
		if (arg1 == null) {
			arg1 = View.inflate(context, R.layout.item_pharmacy_time, null);
			views = new ViewHold();
			views.line = arg1.findViewById(R.id.line);
			views.img_remove = (ImageView) arg1.findViewById(R.id.img_remove);
			views.tv_name = (TextView) arg1.findViewById(R.id.tv_name);
			views.tv_time = (TextView) arg1.findViewById(R.id.tv_time);
			views.img_remove.setTag(arg0);
			arg1.setTag(views);
		} else {
			views = (ViewHold) arg1.getTag();
		}

		views.tv_time.setText(mInfo.getDrugUnit());
		views.tv_name.setText((mInfo.getHour() > 9 ? mInfo.getHour() : "0" + mInfo.getHour()) + ":"
				+ (mInfo.getMinute() > 9 ? mInfo.getMinute() : "0" + mInfo.getMinute()));
		 if (arg0 == mList.size() - 1) {
		 views.line.setVisibility(View.GONE);
		 } else {
		views.line.setVisibility(View.VISIBLE);
		 }
//		if (arg0 == 0) {
//			views.img_remove.setVisibility(View.INVISIBLE);
//		} else {
			views.img_remove.setVisibility(View.VISIBLE);
//		}
		views.img_remove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mList.remove(arg0);
				mAddTimeView.updataView();
			}
		});
		return arg1;
	}

	public interface UpdataAddTimeView {
		public void updataView();// 更新界面
	}

	private class ViewHold {
		View line;
		ImageView img_remove;
		TextView tv_name;
		TextView tv_time;
	}
}
