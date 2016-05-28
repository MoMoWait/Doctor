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

public class CustomNumPickDialog extends DialogFragment implements OnClickListener {
	private OnChangeNumListener listener;
	private WheelView wheelNum1, wheelNum2, wheelNum3, wheelNum4, wheelNum5, wheelNum6;
	private View btnOK;
	private View btnCancel;
	public String strHeight = "";
	private float minNum;
	private float maxNum;
	private float defaultNum;
	private boolean isFloat;
	private int floatNum = 1;// 小数点后几位
	private String title;
	private String unit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.dialog_layout_4, null);
		btnOK = view.findViewById(R.id.btn_ok);
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

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setCancelable(true);
		// 设置dialog为全屏模式
		setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialogs);

	}

	/**
	 * 设置标题
	 * 
	 * @param strMsg
	 */
	public void setMessage(String strMsg) {
		this.title = strMsg;
	}

	/**
	 * 设置最大最小数
	 * 
	 * @param strMsg
	 */
	public void setLimit(float minNum, float maxNum) {
		this.minNum = minNum;
		this.maxNum = maxNum;
	}

	/**
	 * 设置小数点后有几位数
	 * 
	 * @param floatNum
	 *            1、为一位数，2、为二位数
	 * @return
	 */
	public void setFloatNum(int floatNum) {
		this.floatNum = floatNum;
	}

	/**
	 * 设置是否显示小数
	 * 
	 * @param isFloat
	 */
	public void setFloat(boolean isFloat) {
		this.isFloat = isFloat;
	}

	/**
	 * 设置默认选中项
	 */
	public void setDefualtNum(float num) {
		this.defaultNum = num;
	}

	public void setOnChangeNumListener(OnChangeNumListener l) {
		this.listener = l;
	}

	private void init(View layout) {
		wheelNum1 = (WheelView) layout.findViewById(R.id.num1);
		wheelNum2 = (WheelView) layout.findViewById(R.id.num2);
		wheelNum3 = (WheelView) layout.findViewById(R.id.num3);
		wheelNum4 = (WheelView) layout.findViewById(R.id.num4);
		wheelNum5 = (WheelView) layout.findViewById(R.id.num5);
		wheelNum6 = (WheelView) layout.findViewById(R.id.num6);
		// wheelHeight6 = (WheelView) layout.findViewById(R.id.height6);

		wheelNum1.setCyclic(true);
		wheelNum2.setCyclic(true);
		wheelNum3.setCyclic(true);
		wheelNum4.setCyclic(true);
		wheelNum5.setCyclic(true);
		wheelNum6.setCyclic(true);

		wheelNum1.setVisibleItems(5);
		wheelNum2.setVisibleItems(5);
		wheelNum3.setVisibleItems(5);
		wheelNum4.setVisibleItems(5);
		wheelNum5.setVisibleItems(5);
		wheelNum6.setVisibleItems(5);

		int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getActivity().getResources().getDisplayMetrics());
		int iHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getActivity().getResources().getDisplayMetrics());
		wheelNum1.TEXT_SIZE = textSize;
		wheelNum2.TEXT_SIZE = textSize;
		wheelNum3.TEXT_SIZE = textSize;
		wheelNum4.TEXT_SIZE = textSize;
		wheelNum5.TEXT_SIZE = textSize;
		wheelNum6.TEXT_SIZE = textSize;

		wheelNum1.ADDITIONAL_ITEM_HEIGHT = iHeight;
		wheelNum3.ADDITIONAL_ITEM_HEIGHT = iHeight;
		wheelNum2.ADDITIONAL_ITEM_HEIGHT = iHeight;
		wheelNum4.ADDITIONAL_ITEM_HEIGHT = iHeight;
		wheelNum5.ADDITIONAL_ITEM_HEIGHT = iHeight;
		wheelNum6.ADDITIONAL_ITEM_HEIGHT = iHeight;

		wheelNum1.setShadowsColors(ConfigParams.SHADOWS_COLORS);
		wheelNum1.setNeedBound(false);
		wheelNum2.setShadowsColors(ConfigParams.SHADOWS_COLORS);
		wheelNum2.setNeedBound(false);
		wheelNum3.setShadowsColors(ConfigParams.SHADOWS_COLORS);
		wheelNum3.setNeedBound(false);
		wheelNum4.setShadowsColors(ConfigParams.SHADOWS_COLORS);
		wheelNum4.setNeedBound(false);
		wheelNum5.setShadowsColors(ConfigParams.SHADOWS_COLORS);
		wheelNum5.setNeedBound(false);
		wheelNum6.setShadowsColors(ConfigParams.SHADOWS_COLORS);
		wheelNum6.setNeedBound(false);
		wheelNum1.setNoSelectHeightWeight(3, 8);
		wheelNum2.setNoSelectHeightWeight(3, 8);
		wheelNum3.setNoSelectHeightWeight(3, 8);
		wheelNum4.setNoSelectHeightWeight(3, 8);
		wheelNum5.setNoSelectHeightWeight(3, 8);
		wheelNum6.setNoSelectHeightWeight(3, 8);
		if (isFloat) {
			// wheelHeight6.setVisibility(View.VISIBLE);
			wheelNum4.setLabel(" .");
			wheelNum5.setVisibility(View.VISIBLE);
			wheelNum5.setAdapter(new NumericWheelAdapter(0, 9));

			if (floatNum > 1) {
				wheelNum6.setVisibility(View.VISIBLE);
				wheelNum6.setAdapter(new NumericWheelAdapter(0, 9));
				if (!TextUtils.isEmpty(unit)) {
					wheelNum6.setLabel(unit);
				}
			} else {
				wheelNum6.setVisibility(View.GONE);
				if (!TextUtils.isEmpty(unit)) {
					wheelNum5.setLabel(unit);
				}
			}

			// ArrayList<String> list = new ArrayList<String>();
			// list.add(".");
			// wheelHeight6.setAdapter(new StringListWheelAdapter(list));
		} else {
			// wheelHeight6.setVisibility(View.GONE);
			wheelNum5.setVisibility(View.GONE);
			wheelNum6.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(unit)) {
			wheelNum4.setLabel(unit);
		}
		if (maxNum < 1000) {
			wheelNum1.setVisibility(View.GONE);
		} else {
			int max = (int) Math.floor(maxNum / 1000f) < 9 ? (int) Math.floor(maxNum / 1000f) : 9;
			int min = (int) Math.floor(minNum / 1000f) < 9 ? (int) Math.floor(minNum / 1000f) : 0;
			if (min != 0 || max != 9) {
				wheelNum1.setCyclic(false);
			}
			wheelNum1.setAdapter(new NumericWheelAdapter(0, max));
		}

		if (maxNum < 100) {
			wheelNum2.setVisibility(View.GONE);
		} else {
			int max = (int) Math.floor(maxNum / 100f) < 9 ? (int) Math.floor(maxNum / 100f) : 9;
			int min = (int) Math.floor(minNum / 100f) < 9 ? (int) Math.floor(minNum / 100f) : 0;
			if (min != 0 || max != 9) {
				wheelNum2.setCyclic(false);
			}
			wheelNum2.setAdapter(new NumericWheelAdapter(0, max));

		}

		if (maxNum < 10) {
			wheelNum3.setVisibility(View.GONE);
		} else {

			int ma = (int) Math.floor(maxNum / 10f);
			int mi = (int) Math.floor(minNum / 10f);

			int max = ma < 9 ? ma : 9;
			int min = mi < 9 ? mi : 0;
			if (min != 0 || max != 9) {
				wheelNum3.setCyclic(false);
			}
			wheelNum3.setAdapter(new NumericWheelAdapter(0, max));
		}
		TextView tv_msg = (TextView) layout.findViewById(R.id.tv_msg);
		if (!TextUtils.isEmpty(title)) {
			tv_msg.setText(title);
			tv_msg.setVisibility(View.VISIBLE);
		} else {
			tv_msg.setVisibility(View.GONE);
		}
		wheelNum4.setAdapter(new NumericWheelAdapter(0, 9));
		scrollToNum(this.defaultNum);
	}

	/**
	 * 初始化选中项
	 * 
	 * @param num
	 */
	private void scrollToNum(float num) {

		int q = (int) (num / 1000);// 千
		int b = (int) (num % 1000 / 100);// 百
		int s = (int) (num % 1000 % 100 / 10);// 十
		int g = (int) num % 1000 % 100 % 10;// 个数

		WheelAdapter adapter = null;
		if ((adapter = wheelNum1.getAdapter()) != null) {
			wheelNum1.setCurrentItem(getAdapterIndex(adapter, q + ""));
		}

		if ((adapter = wheelNum2.getAdapter()) != null) {
			wheelNum2.setCurrentItem(getAdapterIndex(adapter, b + ""));
		}

		if ((adapter = wheelNum3.getAdapter()) != null) {
			wheelNum3.setCurrentItem(getAdapterIndex(adapter, s + ""));
		}

		if ((adapter = wheelNum4.getAdapter()) != null) {
			wheelNum4.setCurrentItem(getAdapterIndex(adapter, g + ""));
		}

		if (isFloat) {
			int x = (int) Math.floor(((num - Math.floor(num)) * 10));
			if ((adapter = wheelNum5.getAdapter()) != null) {
				wheelNum5.setCurrentItem(getAdapterIndex(adapter, x + ""));
			}
			if (wheelNum6.getVisibility() == View.VISIBLE) {

				int y = (int) Math.floor(((num - Math.floor(num)) * 100) % 10);
				if ((adapter = wheelNum6.getAdapter()) != null) {
					wheelNum6.setCurrentItem(getAdapterIndex(adapter, y + ""));
				}
			}
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

	private float getNumString() {
		StringBuffer sb = new StringBuffer();

		WheelAdapter adapter = null;
		if ((adapter = wheelNum1.getAdapter()) != null) {
			sb.append(adapter.getItem(wheelNum1.getCurrentItem()));
		}

		if ((adapter = wheelNum2.getAdapter()) != null) {
			sb.append(adapter.getItem(wheelNum2.getCurrentItem()));
		}

		if ((adapter = wheelNum3.getAdapter()) != null) {
			sb.append(adapter.getItem(wheelNum3.getCurrentItem()));
		}

		if ((adapter = wheelNum4.getAdapter()) != null) {
			sb.append(adapter.getItem(wheelNum4.getCurrentItem()));
		}

		if (isFloat) {
			sb.append(".").append(wheelNum5.getAdapter().getItem(wheelNum5.getCurrentItem()));
			if (wheelNum6.getVisibility() == View.VISIBLE) {
				sb.append(wheelNum6.getAdapter().getItem(wheelNum6.getCurrentItem()));
			}
		}
		return Float.valueOf(sb.toString());
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
		void onChange(DialogFragment dialog, float num);
	}
}
