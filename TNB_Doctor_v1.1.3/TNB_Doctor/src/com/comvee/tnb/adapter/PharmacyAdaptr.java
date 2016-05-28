package com.comvee.tnb.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.ui.remind.TimeRemindTransitionInfo;

public class PharmacyAdaptr extends BaseAdapter {
	private Context context;
	private List<TimeRemindTransitionInfo> mList;
	private TimeRemindTransitionInfo mInfo;

	public void setList(List<TimeRemindTransitionInfo> mList) {
		this.mList = mList;
	}

	public PharmacyAdaptr(Context context, List<TimeRemindTransitionInfo> list) {
		this.context = context;
		this.mList = list;
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
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		mInfo = mList.get(arg0);
		if (arg1 == null) {
			arg1 = View.inflate(context, R.layout.pharmacy_list_item, null);
		}
		ImageView img_time = (ImageView) arg1.findViewById(R.id.img_time);
		TextView tv_time = (TextView) arg1.findViewById(R.id.tv_time);
		TextView tv_drug_name = (TextView) arg1.findViewById(R.id.tv_drug_name);
		TextView tv_next_time = (TextView) arg1.findViewById(R.id.tv_next_time);
		TextView tv_unit = (TextView) arg1.findViewById(R.id.tv_unit);
		tv_unit.setText(mInfo.getDrugUnit());
		tv_drug_name.setText(mInfo.getDrugName());
		tv_time.setText((mInfo.getHour() > 9 ? mInfo.getHour() : "0" + mInfo.getHour()) + ":"
				+ (mInfo.getMinute() > 9 ? mInfo.getMinute() : "0" + mInfo.getMinute()));
		if (mInfo.isDiabolo()) {
			arg1.setBackgroundColor(context.getResources().getColor(R.color.white));
			img_time.setImageResource(R.drawable.naozhong1);
			tv_time.setTextColor(context.getResources().getColor(R.color.text_color_1));
			tv_drug_name.setTextColor(context.getResources().getColor(R.color.text_color_1));
			tv_unit.setTextColor(context.getResources().getColor(R.color.text_color_1));
			tv_next_time.setVisibility(View.VISIBLE);

			tv_next_time.setText(Html.fromHtml("距下次服药时间:<font color=" + TNBApplication.getInstance().getResources().getColor(R.color.red) + ">"
					+ nextTime(mInfo.getNextTime()) + "</font>"));
		} else {
			arg1.setBackgroundColor(context.getResources().getColor(R.color.backage_color));
			img_time.setImageResource(R.drawable.naozhong2);
			tv_time.setTextColor(context.getResources().getColor(R.color.text_color_3));
			tv_drug_name.setTextColor(context.getResources().getColor(R.color.text_color_3));
			tv_unit.setTextColor(context.getResources().getColor(R.color.text_color_3));
			tv_next_time.setVisibility(View.GONE);
		}
		return arg1;
	}

	private String nextTime(long time) {

		long remindTime = time - System.currentTimeMillis();
		String str = "";
		remindTime = remindTime / 1000;
		int m = (int) (remindTime / 60 % 60);
		int allh = (int) (remindTime / 3600);
		int s = (int) (remindTime % 3600);
		int day = allh / 24;
		int h = allh % 24;

		if (day == 0 && h == 0 && m == 0 && s > 0) {
			str = (m + 1) + "分钟";
		} else if (day == 0 && h == 0 && m > 0) {
			str = (m + 1) + "分钟";
		} else if (day == 0 && s > 0) {
			str = h + "小时" + (m + 1) + "分";
		} else {
			str = day + "天" + h + "小时" + m + "分";
		}

		return str;

	}
}
