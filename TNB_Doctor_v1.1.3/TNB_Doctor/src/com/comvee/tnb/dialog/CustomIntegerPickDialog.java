package com.comvee.tnb.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.ui.wheelview.WheelAdapter;
import com.comvee.ui.wheelview.WheelView;

public class CustomIntegerPickDialog extends DialogFragment implements OnClickListener {
	private OnChangeNumListener listener;
	private WheelView wheelNum1;
	private int inteval = 5;// 间隔
	private View btnOK;
	private TextView titleView;
	private String title;
	private View btnCancel;
	public String strHeight = "";
	private int defaultIndex = 5;// 默认值
	private boolean canCycle = false;
	private int minValue = 0;
	private int indexSize = 10;
	private String label;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_layout_6, null);
		btnOK = view.findViewById(R.id.btn_ok);
		titleView = (TextView) view.findViewById(R.id.tv_msg);
		titleView.setText(title);
		btnCancel = view.findViewById(R.id.btn_cancel);
		((TextView) btnCancel).setTextColor(Color.RED);
		((TextView) btnOK).setTextColor(Color.RED);
		init(view);
		btnOK.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		getDialog().getWindow().setLayout(-1, -2);
		getDialog().getWindow().setGravity(Gravity.BOTTOM);
		getDialog().setCanceledOnTouchOutside(true);
		getDialog().getWindow().setWindowAnimations(R.style.DilaogAnimation);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setCancelable(true);
		// 设置dialog为全屏模式
		setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialogs);

	}

	public CustomIntegerPickDialog setTitle(String str) {
		this.title = str;
		return this;
	}

	public void setOnChangeNumListener(OnChangeNumListener l) {
		this.listener = l;
	}

	public int getInteval() {
		return inteval;
	}

	public CustomIntegerPickDialog setInteval(int inteval) {
		this.inteval = inteval;
		return this;
	}

	public int getDefaultIndex() {
		return defaultIndex;
	}

	public CustomIntegerPickDialog setDefaultIndex(int defaultIndex) {
		this.defaultIndex = defaultIndex;
		return this;
	}

	private void init(View layout) {
		wheelNum1 = (WheelView) layout.findViewById(R.id.num1);
		wheelNum1.setCyclic(canCycle);
		wheelNum1.setVisibleItems(5);
		int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getActivity().getResources().getDisplayMetrics());
		int iHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getActivity().getResources().getDisplayMetrics());
		wheelNum1.TEXT_SIZE = textSize;
		wheelNum1.ADDITIONAL_ITEM_HEIGHT = iHeight;
		wheelNum1.setShadowsColors(ConfigParams.SHADOWS_COLORS);
		wheelNum1.setNeedBound(false);
		wheelNum1.TEXT_SIZE = textSize;
		wheelNum1.setNoSelectHeightWeight(3, 8);
		wheelNum1.setLabel(label);
		wheelNum1.setAdapter(new IntegerAdapter(inteval, indexSize, minValue));
		wheelNum1.setCurrentItem(defaultIndex);
	}

	private int getNumString() {
		int num = 0;
		WheelAdapter adapter = null;
		if ((adapter = wheelNum1.getAdapter()) != null) {
			num = Integer.parseInt(adapter.getItem(wheelNum1.getCurrentItem()));
		}
		return num;
	}

	public CustomIntegerPickDialog setLable(String lable) {
		this.label = lable;
		return this;
	}

	public CustomIntegerPickDialog canCircle(boolean b) {
		this.canCycle = b;
		return this;
	}

	public CustomIntegerPickDialog setIndexSize(int i) {
		this.indexSize = i;
		return this;
	}

	public CustomIntegerPickDialog setMinValue(int min) {
		this.minValue = min;
		return this;
	}

	private class IntegerAdapter implements WheelAdapter {
		private int interval;
		private int minValue = 0;
		private int maxValue = 0;

		public IntegerAdapter(int interval, int indexSize, int minValue) {
			this.interval = interval;
			this.minValue = minValue;
			this.maxValue = indexSize * interval;
		}

		@Override
		public int getItemsCount() {
			return (maxValue - minValue) / interval + 1;
		}

		@Override
		public String getItem(int index) {
			return (minValue + index * interval) + "";
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

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.btn_cancel) {
			dismiss();
		} else if (arg0.getId() == R.id.btn_ok) {
			listener.onChange(this, getNumString());
			dismiss();
		}

	}

	public interface OnChangeNumListener {
		void onChange(DialogFragment dialog, int num);
	}
}
