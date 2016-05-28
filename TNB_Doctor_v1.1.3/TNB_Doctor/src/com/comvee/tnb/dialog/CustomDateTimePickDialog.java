package com.comvee.tnb.dialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.ui.wheelview.NumericWheelAdapter;
import com.comvee.ui.wheelview.StringListWheelAdapter;
import com.comvee.ui.wheelview.WheelView;

public class CustomDateTimePickDialog extends DialogFragment implements OnClickListener {

	private OnDateTimeChangeListener listener;
	private WheelView wheelDate;
	private WheelView wheelHour;
	private WheelView wheelMinute;

	private View btnOk;
	private View btnCancel;
	private TextView titleView;

	public String strDate = "";
	private Calendar defaultCalendar;
	private long startTime;
	private long endTime;
	private ArrayList<String> dateList;
	private String mTitle;
	private final static String[] weeks = TNBApplication.getInstance().getResources().getStringArray(R.array.week_str);

	public void setOnDateTimeChangeListener(OnDateTimeChangeListener l) {
		this.listener = l;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialogs);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.dialog_layout_5, null);
		btnOk = layout.findViewById(R.id.btn_ok);
		titleView = (TextView) layout.findViewById(R.id.tv_msg);
		btnCancel = layout.findViewById(R.id.btn_cancel);
		((TextView) btnCancel).setTextColor(Color.RED);
		((TextView) btnOk).setTextColor(Color.RED);
		if (defaultCalendar == null) {
			defaultCalendar = Calendar.getInstance();
		}
		initView(layout);
		changeTime(defaultCalendar);
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		if (!TextUtils.isEmpty(mTitle)) {
			titleView.setVisibility(View.VISIBLE);
			titleView.setText(mTitle);

		}

		getDialog().getWindow().setLayout(-1, -2);
		getDialog().getWindow().setGravity(Gravity.BOTTOM);
		getDialog().setCanceledOnTouchOutside(true);
		getDialog().getWindow().setWindowAnimations(R.style.DilaogAnimation);
		return layout;
	}

	public void setTitle(String str) {
		this.mTitle = str;
	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.btn_cancel) {
			dismiss();
		} else if (arg0.getId() == R.id.btn_ok) {
			try {

				Calendar cal = Calendar.getInstance();
				String str = wheelDate.getAdapter().getItem(wheelDate.getCurrentItem()).split(" ")[0];
				cal.setTimeInMillis(getUTC(str, "yyyy-MM-dd"));
				int hour = Integer.valueOf(wheelHour.getAdapter().getItem(wheelHour.getCurrentItem()));
				int minute = Integer.valueOf(wheelMinute.getAdapter().getItem(wheelMinute.getCurrentItem()));
				cal.set(Calendar.HOUR_OF_DAY, hour);
				cal.set(Calendar.MINUTE, minute);
				cal.set(Calendar.SECOND, 0);

				if (startTime == 0 && endTime == 0 && cal.getTimeInMillis() / 60 / 1000 <= System.currentTimeMillis() / 60 / 1000) {
					Toast.makeText(getActivity(), R.string.txt_date_choose_limit, Toast.LENGTH_SHORT).show();
				} else {
					if (null != listener) {
						listener.onChange(this, cal);
					}
					dismiss();
				}

			} catch (Exception e) {
				dismiss();
				e.printStackTrace();
			}
		}

	}

	/**
	 * 设置默认时间
	 * 
	 * @param cal
	 */
	public void setDefaultTime(Calendar cal) {

		defaultCalendar = cal;

	}

	/**
	 * 设置开始与结束的时间
	 * 
	 * @param sT
	 * @param eT
	 */
	public void setLimitTime(long sT, long eT) {

		this.startTime = sT;
		this.endTime = eT;

	}

	/**
	 * 获取日期与对应的星期
	 * 
	 * @return
	 */
	private ArrayList<String> getDates() {
		ArrayList<String> list = new ArrayList<String>();
		int len = 0;
		Calendar cal = Calendar.getInstance();

		if (startTime != 0 && endTime != 0 && endTime > startTime) {
			len = (int) ((endTime - startTime) / (86400000));
			cal.setTimeInMillis(startTime);
		} else {
			int maxDay = cal.getActualMaximum(Calendar.DAY_OF_YEAR);
			int thisDay = cal.get(Calendar.DAY_OF_YEAR);
			len = maxDay - thisDay;
		}

		for (int i = 0; i <= len; i++) {
			final String week = weeks[cal.get(Calendar.DAY_OF_WEEK) - 1];
			String date = String.format("%d-%02d-%02d %s", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), week);
			list.add(date);
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		return list;
	}

	/**
	 * 初始化空件
	 * 
	 * @param layout
	 */
	private void initView(View layout) {
		dateList = getDates();
		wheelDate = (WheelView) layout.findViewById(R.id.wheel_date);
		wheelDate.setAdapter(new StringListWheelAdapter(dateList));
		wheelDate.setCyclic(false);
		wheelDate.setVisibleItems(5);

		wheelHour = (WheelView) layout.findViewById(R.id.wheel_hour);
		wheelHour.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
		wheelHour.setCyclic(true);
		wheelHour.setVisibleItems(5);

		wheelMinute = (WheelView) layout.findViewById(R.id.wheel_minute);
		wheelMinute.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		wheelMinute.setCyclic(true);
		wheelMinute.setVisibleItems(5);

		int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getActivity().getResources().getDisplayMetrics());
		int iHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getActivity().getResources().getDisplayMetrics());
		// 设置字体大小
		wheelDate.TEXT_SIZE = textSize;
		wheelHour.TEXT_SIZE = textSize;
		wheelMinute.TEXT_SIZE = textSize;
		// 设置行距
		wheelHour.ADDITIONAL_ITEM_HEIGHT = iHeight;
		wheelDate.ADDITIONAL_ITEM_HEIGHT = iHeight;
		wheelMinute.ADDITIONAL_ITEM_HEIGHT = iHeight;
		// 设置上下模糊部分的颜色
		wheelDate.setShadowsColors(ConfigParams.SHADOWS_COLORS);
		wheelHour.setShadowsColors(ConfigParams.SHADOWS_COLORS);
		wheelMinute.setShadowsColors(ConfigParams.SHADOWS_COLORS);
		// 设置不需要外框
		wheelMinute.setNeedBound(false);
		wheelDate.setNeedBound(false);
		wheelHour.setNeedBound(false);
		// 设置上下模糊部分所占的比例
		wheelMinute.setNoSelectHeightWeight(3, 8);
		wheelHour.setNoSelectHeightWeight(3, 8);
		wheelDate.setNoSelectHeightWeight(3, 8);
		if (defaultCalendar == null) {
			defaultCalendar = Calendar.getInstance();
		}
		changeTime(defaultCalendar);
	}

	/**
	 * 动态改变时间
	 * 
	 * @param cal
	 */
	private void changeTime(final Calendar cal) {
		final String week = weeks[cal.get(Calendar.DAY_OF_WEEK) - 1];
		String date = String.format("%d-%02d-%02d %s", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), week);
		int iDate = dateList.indexOf(date);
		int iHour = cal.get(Calendar.HOUR_OF_DAY);
		wheelHour.setCurrentItem(iHour, false);
		int iMinute = cal.get(Calendar.MINUTE);
		wheelMinute.setCurrentItem(iMinute, false);
		wheelDate.setCurrentItem(iDate == -1 ? 0 : iDate, false);
	}

	/**
	 * 获取时间
	 * 
	 * @param cal
	 */
	public static final long getUTC(String time, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sdf.parse(time);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date.getTime();
	}

	public interface OnDateTimeChangeListener {
		void onChange(DialogFragment dialog, Calendar cal);
	}
}
