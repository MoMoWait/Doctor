package com.comvee.tnb.ui.exercise;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.BaseApplication;
import com.comvee.ComveeBaseAdapter;
import com.comvee.tnb.R;
import com.comvee.tool.ImageLoaderUtil;

public class SportRecordAdapter extends ComveeBaseAdapter<SportRecord> {
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
	SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
	SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm", Locale.CHINA);

	public SportRecordAdapter() {
		super(BaseApplication.getInstance(), R.layout.item_record_input_exercise_listview);
	}

	@Override
	protected void getView(ViewHolder holder, int position) {
		SportRecord sportRecord = getItem(position);
		TextView timeTv = holder.get(R.id.time);
		timeTv.setText(converDate(sportRecord.inputDt));

		ImageView sportImg = holder.get(R.id.sport_name);
		ImageLoaderUtil.getInstance(context).displayImage(sportRecord.imgUrl, sportImg, ImageLoaderUtil.default_options);

		TextView nameTv = holder.get(R.id.name_tx);
		nameTv.setText(sportRecord.sportName);

		TextView timeTx = holder.get(R.id.time_tx);
		timeTx.setText(sportRecord.sportTime);

		TextView cal_tx = holder.get(R.id.cal_tx);
		float perMinuteCal = Float.parseFloat(sportRecord.caloriesOneMinutes) * Integer.parseInt(sportRecord.sportTime);
		int totalCalPasre = Math.round(perMinuteCal);
		cal_tx.setText(totalCalPasre + "");

	}

	private String converDate(String date) {
		try {
			Calendar c1 = Calendar.getInstance();
			c1.setTime(sdf1.parse(date));
			Calendar c2 = Calendar.getInstance();
			if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
				if (c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)) {
					return "今天 " + sdf3.format(c1.getTime());
				} else if (c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR) == 1) {
					return "昨天 " + sdf3.format(c1.getTime());
				}
			}
			return sdf2.format(sdf1.parse(date));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}

}
