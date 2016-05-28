package com.comvee.tnb.dialog;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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

import com.comvee.tnb.R;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.ui.wheelview.NumericWheelAdapter;
import com.comvee.ui.wheelview.OnWheelChangedListener;
import com.comvee.ui.wheelview.WheelView;

public class CustomDatePickDialog extends DialogFragment implements OnClickListener {

	private int START_YEAR = 1990, END_YEAR = 2100;
	private Calendar defaultCalendar;
	private OnTimeChangeListener listener;
	private WheelView wv_year;
	private WheelView wv_month;
	private WheelView wv_day;
	private View btnOk;
	private View btnCancel;
	public String strDate = "";
	private int MaxYear, MaxMouth, MaxDay, MinYear, MinMouth, MinDay;
	private boolean LimitStartTime, LimitEndTime;
	private boolean year_cyclic = true, month_cyclic = true, day_cyclic = true;

	public void setOnTimeChangeListener(OnTimeChangeListener l) {
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
		View layout = inflater.inflate(R.layout.dialog_layout_2, null);
		btnOk = layout.findViewById(R.id.btn_ok);
		btnCancel = layout.findViewById(R.id.btn_cancel);
		((TextView) btnCancel).setTextColor(Color.RED);
		((TextView) btnOk).setTextColor(Color.RED);
		if (defaultCalendar == null) {
			defaultCalendar = Calendar.getInstance();
		}
		initDateTimePicker(layout);
		changeTime(defaultCalendar);
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		getDialog().getWindow().setLayout(-1, -2);
		getDialog().getWindow().setGravity(Gravity.BOTTOM);
		getDialog().setCanceledOnTouchOutside(true);
		getDialog().getWindow().setWindowAnimations(R.style.DilaogAnimation);
		isSelect();
		return layout;
	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.btn_cancel) {
			dismiss();
		} else if (arg0.getId() == R.id.btn_ok) {
			if (listener != null) {
				int nYear = Integer.parseInt(wv_year.getAdapter().getItem(wv_year.getCurrentItem()));
				int nMonth = Integer.parseInt(wv_month.getAdapter().getItem(wv_month.getCurrentItem()));
				int nDays = Integer.parseInt(wv_day.getAdapter().getItem(wv_day.getCurrentItem()));
				listener.onChange(this, nYear, nMonth, nDays);
			}
			dismiss();
		}

	}

	/**
	 * @Description: TODO 弹出日期时间选择器
	 */
	private void initDateTimePicker(View view) {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		final int day = calendar.get(Calendar.DATE);

		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		// 年
		wv_year = (WheelView) view.findViewById(R.id.wheel_year);
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
		wv_year.setCyclic(year_cyclic);// 可循环滚动
		wv_year.setLabel(getString(R.string.year));// 添加文字
		wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

		// 月
		wv_month = (WheelView) view.findViewById(R.id.wheel_month);
		wv_month.setAdapter(new NumericWheelAdapter(1, 12, "%02d"));
		wv_month.setCyclic(month_cyclic);
		wv_month.setLabel(getString(R.string.month));
		wv_month.setCurrentItem(month);

		// 日
		wv_day = (WheelView) view.findViewById(R.id.wheel_day);
		wv_day.setCyclic(day_cyclic);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 31, "%02d"));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 30, "%02d"));
		} else {
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new NumericWheelAdapter(1, 29, "%02d"));
			else
				wv_day.setAdapter(new NumericWheelAdapter(1, 28, "%02d"));
		}
		wv_day.setLabel(getString(R.string.day));
		wv_day.setCurrentItem(day - 1);
		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + START_YEAR;

				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31, "%02d"));
				} else if (list_little.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30, "%02d"));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29, "%02d"));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28, "%02d"));
				}
				if (TextUtils.isEmpty(wv_day.getAdapter().getItem(wv_day.getCurrentItem()))) {
					wv_day.setCurrentItem(0);
				}
				isSelect();
			}
		};

		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31, "%02d"));

				} else if (list_little.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30, "%02d"));
				} else {
					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year.getCurrentItem() + START_YEAR) % 100 != 0)
							|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29, "%02d"));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28, "%02d"));
				}
				if (TextUtils.isEmpty(wv_day.getAdapter().getItem(wv_day.getCurrentItem()))) {
					wv_day.setCurrentItem(0);
				}
				isSelect();

			}
		};
		// 添加日的监听
		OnWheelChangedListener wheelListener_day = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int day_num = newValue + 1;
				isSelect();

			}
		};
		// 根据屏幕密度来指定选择器字体的大小
		int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getActivity().getResources().getDisplayMetrics());
		int iHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getActivity().getResources().getDisplayMetrics());
		// 添加监听
		wv_year.addChangingListener(wheelListener_year);
		// 设置字体大小
		wv_year.TEXT_SIZE = textSize;
		// 设置行距
		wv_year.ADDITIONAL_ITEM_HEIGHT = iHeight;
		// 设置显示行数
		wv_year.setVisibleItems(5);
		// 设置上下模糊部分的颜色
		wv_year.setShadowsColors(ConfigParams.SHADOWS_COLORS);
		// 设置不需要外框
		wv_year.setNeedBound(false);
		// 设置上下模糊部分所占的比例
		wv_year.setNoSelectHeightWeight(3, 8);

		wv_month.addChangingListener(wheelListener_month);
		wv_month.TEXT_SIZE = textSize;
		wv_month.ADDITIONAL_ITEM_HEIGHT = iHeight;
		wv_month.setVisibleItems(5);
		wv_month.setShadowsColors(ConfigParams.SHADOWS_COLORS);
		wv_month.setNeedBound(false);
		wv_month.setNoSelectHeightWeight(3, 8);

		wv_day.addChangingListener(wheelListener_day);
		wv_day.TEXT_SIZE = textSize;
		wv_day.ADDITIONAL_ITEM_HEIGHT = iHeight;
		wv_day.setVisibleItems(5);
		wv_day.setShadowsColors(ConfigParams.SHADOWS_COLORS);
		wv_day.setNeedBound(false);
		wv_day.setNoSelectHeightWeight(3, 8);

	}

	/**
	 * 设置“年”选项是否循环滚动
	 * 
	 * @param cyclic
	 */
	public void setYearCyclic(boolean cyclic) {
		year_cyclic = cyclic;
	}

	/**
	 * 设置“月”选项是否循环滚动
	 * 
	 * @param cyclic
	 */
	public void setMonthCyclic(boolean cyclic) {
		month_cyclic = cyclic;
	}

	/**
	 * 设置 “日” 选项是否循环滚动
	 * 
	 * @param cyclic
	 */
	public void setDayCyclic(boolean cyclic) {
		day_cyclic = cyclic;
	}

	/**
	 * 限定时间的选择，只能选择限定内的时间
	 */
	public void isSelect() {
		if (LimitEndTime && LimitStartTime) {
		} else if (LimitEndTime) {
			MinYear = START_YEAR;
			MinMouth = 1;
			MinDay = 1;
		} else if (LimitStartTime) {
			MaxYear = END_YEAR;
			MaxMouth = 12;
			MaxDay = 31;
		} else {
			return;
		}
		int year_num = wv_year.getCurrentItem() + START_YEAR;
		int month_num = wv_month.getCurrentItem() + 1;
		int day_num = wv_day.getCurrentItem() + 1;
		if (year_num < MaxYear && year_num > MinYear) {
			btnOk.setEnabled(true);
			((TextView) btnOk).setTextColor(Color.RED);
		} else if (year_num > MaxYear || year_num < MinYear) {
			btnOk.setEnabled(false);
			((TextView) btnOk).setTextColor(Color.GRAY);
		} else if (year_num == MaxYear) {
			if (month_num < MaxMouth) {
				btnOk.setEnabled(true);
				((TextView) btnOk).setTextColor(Color.RED);
			}
			if (month_num > MaxMouth) {
				btnOk.setEnabled(false);
				((TextView) btnOk).setTextColor(Color.GRAY);
			}
			if (month_num == MaxMouth) {
				if (day_num <= MaxDay) {
					btnOk.setEnabled(true);
					((TextView) btnOk).setTextColor(Color.RED);
				} else {
					btnOk.setEnabled(false);
					((TextView) btnOk).setTextColor(Color.GRAY);
				}
			}
		} else if (year_num == MinYear) {
			if (month_num > MinMouth) {
				btnOk.setEnabled(true);
				((TextView) btnOk).setTextColor(Color.RED);
			}
			if (month_num < MinMouth) {
				btnOk.setEnabled(false);
				((TextView) btnOk).setTextColor(Color.GRAY);
			}
			if (month_num == MinMouth) {
				if (day_num >= MinDay) {
					btnOk.setEnabled(true);
					((TextView) btnOk).setTextColor(Color.RED);
				} else {
					btnOk.setEnabled(false);
					((TextView) btnOk).setTextColor(Color.GRAY);
				}
			}
		}

	}

	/**
	 * 设置限定选择的开始时间，设置后在这个之前时间将无法选择
	 * 
	 * @param cal
	 * @return
	 */
	public void setLimitCanSelectOfStartTime(Calendar cal) {
		MinDay = cal.get(Calendar.DAY_OF_MONTH);
		MinYear = cal.get(Calendar.YEAR);
		MinMouth = cal.get(Calendar.MONTH) + 1;
		this.LimitStartTime = true;
	}

	/**
	 * 设置限定选择的结束时间，设置后在这个之后时间将无法选择
	 * 
	 * @param cal
	 * @return
	 */
	public void setLimitCanSelectOfEndTime(Calendar cal) {
		MaxDay = cal.get(Calendar.DAY_OF_MONTH);
		MaxYear = cal.get(Calendar.YEAR);
		MaxMouth = cal.get(Calendar.MONTH) + 1;
		this.LimitEndTime = true;
	}

	/**
	 * 设置选中项
	 * 
	 * @param cal
	 */
	private void changeTime(Calendar cal) {
		wv_year.setCurrentItem(cal.get(Calendar.YEAR) - START_YEAR, false);
		int month = cal.get(Calendar.MONTH);
		wv_month.setCurrentItem(month, false);
		int day = cal.get(Calendar.DAY_OF_MONTH) - 1;
		wv_day.setCurrentItem(day, false);
	}

	/**
	 * 设置默认选中时间
	 * 
	 * @param cal
	 */
	public void setDefaultTime(Calendar cal) {
		this.defaultCalendar = cal;
	}

	/**
	 * 设置开始与结束时间
	 * 
	 * @param sYear
	 * @param eYear
	 */
	public void setLimitTime(int sYear, int eYear) {

		this.START_YEAR = sYear;
		this.END_YEAR = eYear;

	}

	public interface OnTimeChangeListener {
		void onChange(DialogFragment dialog, int year, int month, int day);
	}

}
