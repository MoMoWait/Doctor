//package com.comvee.tnb.dialog;
//
//import org.chenai.util.Util;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup.LayoutParams;
//import android.view.Window;
//
//import com.comvee.tnb.R;
//import com.comvee.tnb.config.ConfigParams;
//import com.comvee.ui.wheelview.NumericWheelAdapter;
//import com.comvee.ui.wheelview.WheelAdapter;
//import com.comvee.ui.wheelview.WheelView;
//
//public class CustomSingleNumPickDialog extends Dialog {
//
//	public CustomSingleNumPickDialog(Context context, int theme) {
//		super(context, theme);
//	}
//
//	public CustomSingleNumPickDialog(Context context) {
//		super(context);
//
//	}
//
//	/**
//	 * Helper class for creating a custom dialog
//	 */
//	public static class Builder {
//
//		private Context context;
//		private OnChangeNumListener listener;
//		private WheelView wheelNum1, wheelNum2;
//		private View btnOK;
//		private View btnCancel;
//		public String strHeight = "";
//		private int minNum;
//		private int maxNum;
//		private String mUnit;
//		private float defaultNum;
//		private boolean isFloat;
//
//		private DialogInterface.OnClickListener positiveButtonClickListener, negativeButtonClickListener;
//
//		public Builder(Context context) {
//			this.context = context;
//		}
//
//		public Builder setUnit(String unit) {
//			this.mUnit = unit;
//			return this;
//		}
//
//		public Builder setLimit(int minNum, int maxNum) {
//			this.minNum = minNum;
//			this.maxNum = maxNum;
//			return this;
//		}
//
//		public Builder setFloat(boolean isFloat) {
//			this.isFloat = isFloat;
//			return this;
//		}
//
//		public Builder setDefualtNum(float num) {
//			this.defaultNum = num;
//			return this;
//		}
//
//		public Builder setOnChangeNumListener(OnChangeNumListener l) {
//			this.listener = l;
//			return this;
//		}
//
//		public CustomSingleNumPickDialog create() {
//			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//			final CustomSingleNumPickDialog dialog = new CustomSingleNumPickDialog(context, R.style.Dialog);
//			dialog.getWindow().setWindowAnimations(R.style.DilaogAnimation);
//
//			View layout = inflater.inflate(R.layout.dialog_single_num_pick, null);
//			dialog.addContentView(layout, new LayoutParams(-1, -2));
//			Window win = dialog.getWindow();
//			win.setGravity(Gravity.BOTTOM);
//			dialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
//
//			btnOK = layout.findViewById(R.id.btn_ok);
//			btnCancel = layout.findViewById(R.id.btn_cancel);
//
//			btnCancel.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if (negativeButtonClickListener != null) {
//						negativeButtonClickListener.onClick(dialog, R.id.btn_cancel);
//					} else {
//						dialog.cancel();
//					}
//				}
//			});
//			btnOK.setOnClickListener(new View.OnClickListener() {
//				public void onClick(View v) {
//
//					if (positiveButtonClickListener != null) {
//						positiveButtonClickListener.onClick(dialog, R.id.btn_ok);
//						return;
//					}
//					try {
//						if (listener != null) {
//							listener.onChange(dialog, getNumString());
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					} finally {
//						dialog.dismiss();
//						// 显示textView
//
//					}
//				}
//			});
//
//			int iHeight = Util.dip2px(context, 20);
//			int textSize = Util.dip2px(context, ConfigParams.TEXT_SIZE_WHEEL);
//
//			wheelNum1 = (WheelView) layout.findViewById(R.id.wheel_num0);
//			wheelNum2 = (WheelView) layout.findViewById(R.id.wheel_num1);
//			wheelNum1.TEXT_SIZE = textSize;
//			wheelNum2.TEXT_SIZE = textSize;
//			wheelNum1.ADDITIONAL_ITEM_HEIGHT = iHeight;
//			wheelNum2.ADDITIONAL_ITEM_HEIGHT = iHeight;
//			wheelNum1.setCyclic(true);
//			wheelNum2.setCyclic(true);
//
//			wheelNum1.setVisibleItems(5);
//			wheelNum2.setVisibleItems(5);
//
//			if (isFloat) {
//				wheelNum1.setLabel(" .");
//				wheelNum2.setVisibility(View.VISIBLE);
//				wheelNum2.setLabel(mUnit);
//				wheelNum2.setAdapter(new NumericWheelAdapter(0, 9, "%01d"));
//			} else {
//				wheelNum1.setLabel(mUnit);
//				wheelNum2.setVisibility(View.GONE);
//			}
//
//			wheelNum1.setAdapter(new NumericWheelAdapter(minNum, maxNum));
//			scrollToNum(this.defaultNum);
//			return dialog;
//		}
//
//		private void scrollToNum(float num) {
//			wheelNum1.setCurrentItem(getAdapterIndex(wheelNum1.getAdapter(), (int) Math.floor(num) + ""));
//			if (isFloat) {
//				int x = (int) Math.floor((num * 10 - Math.floor(num) * 10));
//				wheelNum2.setCurrentItem(getAdapterIndex(wheelNum2.getAdapter(), x + ""));
//			}
//		}
//
//		private int getAdapterIndex(WheelAdapter adapter, String num) {
//
//			int len = adapter.getItemsCount();
//			for (int i = 0; i < len; i++) {
//				if (adapter.getItem(i).equals(num)) {
//					return i;
//				}
//			}
//			return 0;
//
//		}
//
//		private float getNumString() {
//			StringBuffer sb = new StringBuffer();
//			WheelAdapter adapter = null;
//			if ((adapter = wheelNum1.getAdapter()) != null) {
//				sb.append(adapter.getItem(wheelNum1.getCurrentItem()));
//			}
//			if (isFloat) {
//				sb.append(".").append(wheelNum2.getAdapter().getItem(wheelNum2.getCurrentItem()));
//			}
//			return Float.valueOf(sb.toString());
//		}
//	}
//
//	public interface OnChangeNumListener {
//		void onChange(Dialog dialog, float num);
//	}
//
// }
