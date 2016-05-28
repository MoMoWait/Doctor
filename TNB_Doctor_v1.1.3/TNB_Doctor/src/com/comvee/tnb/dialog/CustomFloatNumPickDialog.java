package com.comvee.tnb.dialog;

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
import com.comvee.ui.wheelview.WheelAdapter;
import com.comvee.ui.wheelview.WheelView;

public class CustomFloatNumPickDialog extends DialogFragment implements OnClickListener {
	public static final int TEXT_SIZE_WHEEL = 30;
	private int interval = 1;
	private int START_NUM = 0, END_NUM = 20;
	private int FLOAT_STAR = 0, FLOAT_END = 9;
	private int numIndexSize = 0, floatIndexSize = 0;
	private OnNumChangeListener listener;
	private WheelView wv_num, wv_num_decimals;
	private View btnOk;
	private View btnCancel;
	private String unit;
	private boolean isShowFloat;
	private float MaxNum, MinNum;
	private boolean LimitMinNum, LimitMaxNum;
	private float defult = -1;
	private String title;
	private boolean customFloat = false;
	private boolean customNum = false;

	public void setCustomFloat(int min, int indexSize, int interval) {
		this.customFloat = true;
		FLOAT_STAR = min;
		this.floatIndexSize = indexSize;
		this.interval = interval;
	}

	public void setCustomNum(int min, int indexSize, int interval) {
		this.customNum = true;
		START_NUM = min;
		this.numIndexSize = indexSize;
		this.interval = interval;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.dialog_layout_1, null);
		btnOk = view.findViewById(R.id.btn_ok);
		btnCancel = view.findViewById(R.id.btn_cancel);
		((TextView) btnCancel).setTextColor(Color.RED);
		((TextView) btnOk).setTextColor(Color.RED);
		initDateTimePicker(view);
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		getDialog().getWindow().setLayout(-1, -2);
		getDialog().getWindow().setGravity(Gravity.BOTTOM);
		getDialog().setCanceledOnTouchOutside(true);
		getDialog().getWindow().setWindowAnimations(R.style.DilaogAnimation);
		return view;
	}

	/**
	 * 设置单位
	 * 
	 * @param label
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * 设置标题
	 * 
	 * @param label
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 是否显示小数点后的数字
	 * 
	 * @param isShow
	 */
	public void setFloat(boolean isShow) {
		isShowFloat = isShow;
	}

	/**
	 * 设置选项的区间
	 * 
	 * @param min
	 * @param max
	 */
	public void setLimitNum(int min, int max) {
		START_NUM = min;
		END_NUM = max;
	}

	/**
	 * 设置限定最小数字 效果：如果选中值小于限定值 保存键无法点击 注意：限定值必须在选项的区间内
	 * 
	 * @param minNum
	 */
	public void setLimitCanSelectMinNum(float minNum) {
		LimitMinNum = true;
		this.MinNum = minNum;
	}

	/**
	 * 设置限定最大数字 效果：如果选中值大于限定值 保存键无法点击 注意：限定值必须在选项的区间内
	 * 
	 * @param minNum
	 */
	public void setLimitCanSelectMaxNum(float maxNum) {
		LimitMaxNum = true;
		this.MaxNum = maxNum;
	}

	/**
	 * 设置默认值
	 * 
	 * @param minNum
	 */
	public void setDefult(float defult) {
		this.defult = defult;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setCancelable(true);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialogs);

	}

	/**
	 * 判断限制是否生效 如果生效 保存键就无法点击
	 */
	private void isCanSelect() {

		if (LimitMinNum && LimitMaxNum) {
		} else if (LimitMinNum) {
			MaxNum = END_NUM + (float) 0.9;
		} else if (LimitMaxNum) {
			MinNum = START_NUM;
		} else {
			return;
		}

		if (wv_num != null && wv_num_decimals != null) {
			if (MinNum >= START_NUM && MinNum <= MaxNum) {
				float selectNum = Float.parseFloat((wv_num.getAdapter().getItem(wv_num.getCurrentItem()) + "." + wv_num_decimals.getAdapter()
						.getItem(wv_num_decimals.getCurrentItem())));
				if (selectNum >= MinNum && selectNum <= MaxNum) {
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
	 * 初始化界面
	 * 
	 * @Description: TODO
	 */
	private void initDateTimePicker(View view) {
		wv_num = (WheelView) view.findViewById(R.id.wheel_num);
		wv_num_decimals = (WheelView) view.findViewById(R.id.wheel_num_decimals);

		wv_num.setCyclic(true);
		wv_num_decimals.setCyclic(true);

		wv_num.setVisibleItems(5);
		wv_num_decimals.setVisibleItems(5);
		int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getActivity().getResources().getDisplayMetrics());
		int iHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getActivity().getResources().getDisplayMetrics());
		// 设置字体大小
		wv_num.TEXT_SIZE = textSize;
		wv_num_decimals.TEXT_SIZE = textSize;
		// 设置行距
		wv_num.ADDITIONAL_ITEM_HEIGHT = iHeight;
		wv_num_decimals.ADDITIONAL_ITEM_HEIGHT = iHeight;
		// 设置控件上下部分的颜色
		wv_num.setShadowsColors(ConfigParams.SHADOWS_COLORS);
		// 设置不需要外框
		wv_num.setNeedBound(false);
		// 设置上下模糊区域的比例
		wv_num.setNoSelectHeightWeight(3, 8);
		wv_num_decimals.setNoSelectHeightWeight(3, 8);
		wv_num_decimals.setShadowsColors(ConfigParams.SHADOWS_COLORS);
		wv_num_decimals.setNeedBound(false);
		if (customNum) {
			wv_num.setAdapter(new CustomAdapter(interval, numIndexSize, START_NUM, "%01d"));
			wv_num.setCyclic(false);
		} else {
			wv_num.setAdapter(new NumericWheelAdapter(START_NUM, END_NUM, "%01d"));

		}
		if (customFloat) {
			wv_num_decimals.setAdapter(new CustomAdapter(interval, floatIndexSize, FLOAT_STAR, "%01d"));
			wv_num_decimals.setCyclic(false);
		} else {
			wv_num_decimals.setAdapter(new NumericWheelAdapter(FLOAT_STAR, FLOAT_END, "%01d"));
		}
		wv_num.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				isCanSelect();
			}
		});
		wv_num_decimals.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				isCanSelect();
			}
		});
		if (isShowFloat) {
			wv_num.setLabel(" .");
			wv_num_decimals.setLabel(unit);
			wv_num_decimals.setVisibility(View.VISIBLE);
		} else {
			wv_num.setLabel(unit);
			wv_num_decimals.setVisibility(View.GONE);
		}
		TextView tv_msg = (TextView) view.findViewById(R.id.tv_msg);
		if (!TextUtils.isEmpty(title)) {
			tv_msg.setText(title);
			tv_msg.setVisibility(View.VISIBLE);
		} else {
			tv_msg.setVisibility(View.GONE);
		}
		scrollToNum(defult);
		isCanSelect();
	}

	private void scrollToNum(float num) {
		wv_num.setCurrentItem(getAdapterIndex(wv_num.getAdapter(), (int) Math.floor(num) + ""));
		if (isShowFloat) {
			int x = (int) Math.floor((num * 10 - Math.floor(num) * 10));
			wv_num_decimals.setCurrentItem(getAdapterIndex(wv_num_decimals.getAdapter(), x + ""));
		}
	}

	private int getAdapterIndex(WheelAdapter adapter, String num) {

		int len = adapter.getItemsCount();
		for (int i = 0; i < len; i++) {
			if (adapter.getItem(i).equals(num)) {
				return i;
			}
		}
		return 0;

	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.btn_cancel) {
			dismiss();
		} else if (arg0.getId() == R.id.btn_ok) {
			if (listener != null) {
				if (!isShowFloat) {
					int num = Integer.parseInt(wv_num.getAdapter().getItem(wv_num.getCurrentItem()));
					listener.onChange(this, (float) Math.ceil(num));
				} else {
					float num = Float.parseFloat(wv_num.getAdapter().getItem(wv_num.getCurrentItem()) + "."
							+ wv_num_decimals.getAdapter().getItem(wv_num_decimals.getCurrentItem()));
					listener.onChange(this, num);
				}
			}
			dismiss();
		}

	}

	public void addOnNumChangeListener(OnNumChangeListener listener) {
		this.listener = listener;
	}

	public interface OnNumChangeListener {
		void onChange(DialogFragment dialog, float num);
	}

	private class CustomAdapter implements WheelAdapter {

		private int interval;
		private int minValue = 0;
		private int maxValue = 0;
		private String format = null;

		public CustomAdapter(int interval, int indexSize, int minValue, String format) {
			this.interval = interval;
			this.minValue = minValue;
			this.maxValue = indexSize * interval - (interval - minValue);
			this.format = format;
		}

		@Override
		public int getItemsCount() {
			return (maxValue - minValue) / interval + 1;
		}

		@Override
		public String getItem(int index) {

			if (index >= 0 && index < getItemsCount()) {
				int value = minValue + index * interval;
				return format != null ? String.format(format, value) : Integer.toString(value);
			}
			return null;

		}

		@Override
		public int getMaximumLength() {
			int max = Math.max(Math.abs(maxValue), Math.abs(minValue));
			int maxLen = Integer.toString(max).length();
			if (minValue < 0) {
				maxLen++;
			}
			return maxLen;
		}
	}
}
