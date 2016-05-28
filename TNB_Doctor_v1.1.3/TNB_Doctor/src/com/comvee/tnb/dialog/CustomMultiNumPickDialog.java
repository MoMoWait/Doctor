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
import com.comvee.ui.wheelview.WheelAdapter;
import com.comvee.ui.wheelview.WheelView;

public class CustomMultiNumPickDialog extends DialogFragment implements OnClickListener {
	private OnChangeMultiNumListener listener;
	private WheelView wheelNum1, wheelNum2, wheelNum3;

	private View btnOK;
	private View btnCancel;

	public String strHeight = "";
	private int[] minNum;
	private int[] maxNum;
	private float[] defaultNum;
	private String[] mTitles;
	private String[] mUnits;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialogs);
	}

	/**
	 * 设置标题
	 * 
	 * @param titles
	 */
	public void setMultiTitles(String[] titles) {
		this.mTitles = titles;
	}

	/**
	 * 设置单位
	 * 
	 * @param mUnits
	 */
	public void setMultiUnits(String[] mUnits) {
		this.mUnits = mUnits;
	}

	/**
	 * 设置最大与最小数
	 * 
	 * @param minNum
	 * @param maxNum
	 */
	public void setMultiLimit(int[] minNum, int[] maxNum) {
		this.minNum = minNum;
		this.maxNum = maxNum;
	}

	/**
	 * 设置默认选中数
	 * 
	 * @param num
	 */
	public void setMultiDefualtNum(float[] num) {
		this.defaultNum = num;
	}

	public void setOnChangeMultiNumListener(OnChangeMultiNumListener l) {
		this.listener = l;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View layout = inflater.inflate(R.layout.dialog_layout_3, null);
		btnOK = layout.findViewById(R.id.btn_ok);
		btnCancel = layout.findViewById(R.id.btn_cancel);
		((TextView) btnCancel).setTextColor(Color.RED);
		((TextView) btnOK).setTextColor(Color.RED);
		btnOK.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		getDialog().getWindow().setLayout(-1, -2);
		getDialog().getWindow().setGravity(Gravity.BOTTOM);
		getDialog().setCanceledOnTouchOutside(true);
		getDialog().getWindow().setWindowAnimations(R.style.DilaogAnimation);
		init(layout);
		initTitles(layout);
		return layout;
	}

	/**
	 * 初始化控件
	 * 
	 * @param view
	 */
	private void init(View view) {
		wheelNum1 = (WheelView) view.findViewById(R.id.wheel_year);
		wheelNum2 = (WheelView) view.findViewById(R.id.wheel_month);
		wheelNum3 = (WheelView) view.findViewById(R.id.wheel_day);

		wheelNum1.setCyclic(true);
		wheelNum2.setCyclic(true);
		wheelNum3.setCyclic(true);

		wheelNum1.setVisibleItems(5);
		wheelNum2.setVisibleItems(5);
		wheelNum3.setVisibleItems(5);
		int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getActivity().getResources().getDisplayMetrics());
		int iHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getActivity().getResources().getDisplayMetrics());
		// 设置字体大小
		wheelNum1.TEXT_SIZE = textSize;
		wheelNum2.TEXT_SIZE = textSize;
		wheelNum3.TEXT_SIZE = textSize;
		// 设置行距
		wheelNum1.ADDITIONAL_ITEM_HEIGHT = iHeight;
		wheelNum3.ADDITIONAL_ITEM_HEIGHT = iHeight;
		wheelNum2.ADDITIONAL_ITEM_HEIGHT = iHeight;
		// 设置上下模糊部分的颜色
		wheelNum1.setShadowsColors(ConfigParams.SHADOWS_COLORS);
		// 设置不需要外框
		wheelNum1.setNeedBound(false);
		wheelNum2.setShadowsColors(ConfigParams.SHADOWS_COLORS);
		wheelNum2.setNeedBound(false);
		wheelNum3.setShadowsColors(ConfigParams.SHADOWS_COLORS);
		wheelNum3.setNeedBound(false);
		// 设置上下模糊部分的比例
		wheelNum1.setNoSelectHeightWeight(3, 8);
		wheelNum2.setNoSelectHeightWeight(3, 8);
		wheelNum3.setNoSelectHeightWeight(3, 8);
		WheelView[] views = { wheelNum1, wheelNum2, wheelNum3 };
		for (int i = 0; i < views.length; i++) {
			WheelView wheel = views[i];
			if (i < maxNum.length) {
				wheel.setAdapter(new NumericWheelAdapter(minNum[i], maxNum[i], "%01d"));
				wheel.setVisibility(View.VISIBLE);
				if (mUnits != null && !TextUtils.isEmpty(mUnits[i])) {
					wheel.setLabel(mUnits[i]);
				}
			} else {
				wheel.setVisibility(View.GONE);
			}
			switch (maxNum.length) {
			case 1:
				view.findViewById(R.id.tv_line_1).setVisibility(View.GONE);
				view.findViewById(R.id.tv_line_2).setVisibility(View.GONE);
				break;
			case 2:
				view.findViewById(R.id.tv_line_1).setVisibility(View.VISIBLE);
				view.findViewById(R.id.tv_line_2).setVisibility(View.GONE);
				break;
			case 3:
				view.findViewById(R.id.tv_line_1).setVisibility(View.VISIBLE);
				view.findViewById(R.id.tv_line_2).setVisibility(View.VISIBLE);
				break;

			default:
				break;
			}
		}
		scrollToNum(this.defaultNum);
		// initTitles(view);
	}

	private void initTitles(View rootView) {
		TextView tv = (TextView) rootView.findViewById(R.id.tv_msg);
		String[] titles = this.mTitles;
		if (null == titles || titles.length == 0) {
			tv.setVisibility(View.GONE);
			return;
		}
		tv.setVisibility(View.VISIBLE);
		String str = titles[0];
		for (int i = 1; i < titles.length; i++) {
			str = str + "/" + titles[i];
		}
		tv.setText(str);
	}

	/**
	 * 初始化选中位置
	 * 
	 * @param num
	 */
	private void scrollToNum(float[] num) {
		if (num == null) {
			return;
		}
		WheelView[] views = { wheelNum1, wheelNum2, wheelNum3 };
		WheelAdapter adapter = null;
		for (int i = 0; i < views.length; i++) {
			WheelView wheel = views[i];
			if (i < num.length) {
				int value = (int) Math.floor(num[i]);
				if ((adapter = views[i].getAdapter()) != null) {
					wheel.setCurrentItem(getAdapterIndex(adapter, value + ""));
				}
			}
		}
	}

	/**
	 * 获取选中项id
	 * 
	 * @param adapter
	 * @param num
	 * @return
	 */
	private int getAdapterIndex(WheelAdapter adapter, String num) {

		int len = adapter.getItemsCount();
		for (int i = 0; i < len; i++) {
			if (adapter.getItem(i).equals(num)) {
				return i;
			}
		}
		return 0;

	}

	/**
	 * 获取选中项，并转换为数组形式
	 * 
	 * @return
	 */
	private float[] getNumString() {
		float[] values = new float[maxNum.length];
		WheelView[] views = { wheelNum1, wheelNum2, wheelNum3 };
		for (int i = 0; i < views.length; i++) {
			WheelView wheel = views[i];
			if (i < maxNum.length) {
				values[i] = Float.valueOf(wheel.getAdapter().getItem(wheel.getCurrentItem()));
			}
		}
		return values;
	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.btn_cancel) {
			dismiss();
		} else if (arg0.getId() == R.id.btn_ok) {
			listener.onChange(this, getNumString());
			dismiss();
		}

	}

	public interface OnChangeMultiNumListener {
		void onChange(DialogFragment dialog, float[] num);
	}

}
