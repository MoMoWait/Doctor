//package com.comvee.tnb.view;
//
//import org.chenai.util.Util;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.drawable.BitmapDrawable;
//import android.text.TextUtils;
//import android.view.Gravity;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//import android.widget.TextView;
//
//import com.comvee.tnb.R;
//import com.comvee.tnb.dialog.CustomSingleNumPickDialog;
//import com.comvee.tnb.dialog.CustomSingleNumPickDialog.OnChangeNumListener;
//import com.comvee.tnb.model.InputModel;
//
//public class SmokeInputItemWindow extends PopupWindow implements View.OnClickListener {
//
//	private Context mContext;
//	private View mRootView;
//	private OnClick itemClick;
//	private LinearLayout layoutCheck;
//	private TextView tvOk;
//	private TextView tvNo;
//	private View btnOk;
//
//	private InputItemView view1;
//	private InputItemView view2;
//	private InputItemView view3;
//	private InputItemView view4;
//
//	public SmokeInputItemWindow(Context cxt, String title, final String items[], String defValue, int type) {
//		super(cxt);
//		this.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
//		this.mContext = cxt;
//		mRootView = createRootView();
//
//		final TextView tvTitle = (TextView) mRootView.findViewById(R.id.tv_title);
//		tvTitle.setText(title);
//		if (title.length() > 14) {
//			tvTitle.setTextSize(16);
//		}
//
//		tvOk = (TextView) mRootView.findViewById(R.id.check_ok);
//		tvNo = (TextView) mRootView.findViewById(R.id.check_no);
//
//		tvOk.setTag(false);
//		tvNo.setTag(false);
//
//		tvNo.setOnClickListener(this);
//		tvOk.setOnClickListener(this);
//
//		btnOk = mRootView.findViewById(R.id.btn_ok);
//		btnOk.setOnClickListener(this);
//
//		layoutCheck = (LinearLayout) mRootView.findViewById(R.id.check_layout);
//		try {
//			InputModel model = null;
//			model = new InputModel();
//			view1 = new InputItemView(mContext);
//			view1.setBackgroundResource(R.drawable.bg_check_style1);
//			model.name = "每日吸烟数";
//			model.type = 2;
//			view1.setInputModel(model);
//			layoutCheck.addView(view1, -1, -2);
//			view1.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					showInputNumDialog(view1, "支", items[7]);
//				}
//			});
//			view1.setDisplayValue(getNumValue(view1, defValue, "CYDASHXG00403") + "支");
//
//			model = new InputModel();
//			view2 = new InputItemView(mContext);
//			view2.setBackgroundResource(R.drawable.bg_check_style1);
//			model.name = "烟龄";
//			model.type = 2;
//			view2.setInputModel(model);
//			layoutCheck.addView(view2, -1, -2);
//			view2.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					showInputNumDialog(view2, "年", items[6]);
//				}
//			});
//			view2.setDisplayValue(getNumValue(view2, defValue, "CYDASHXG00404") + "年");
//
//			model = new InputModel();
//			view3 = new InputItemView(mContext);
//			view3.setBackgroundResource(R.drawable.bg_check_style1);
//			model.name = "是否愿意戒烟";
//			model.type = 1;
//			model.items = new String[] { "是", "否" };
//			model.itemValues = new String[] { items[2], items[3] };
//			view3.setInputModel(model);
//			layoutCheck.addView(view3, -1, -2);
//			view3.setOnClickListener(this);
//			getCheck(view3, defValue);
//
//			model = new InputModel();
//			view4 = new InputItemView(mContext);
//			view4.setBackgroundResource(R.drawable.bg_check_style1);
//			model.name = "是否需要戒烟建议";
//			model.type = 1;
//			model.items = new String[] { "是", "否" };
//			model.itemValues = new String[] { items[4], items[5] };
//			view4.setInputModel(model);
//			layoutCheck.addView(view4, -1, -2);
//			view4.setOnClickListener(this);
//			getCheck(view4, defValue);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		if (type == 1) {
//			checkButtonOk(true);
//		} else if (type == 2) {
//			checkButtonNo(true);
//		}
//
//		setContentView(mRootView);
//		setWidth(-1);
//		setHeight(-1);
//		setBackgroundDrawable(new BitmapDrawable());
//		setAnimationStyle(R.style.PopupAnimation);
//		setFocusable(true);
//	}
//
//	private String getNumValue(InputItemView iv, String value, String key) throws Exception {
//		try {
//			JSONArray jsons = new JSONArray("[" + value + "]");
//			int len = jsons.length();
//			for (int j = 0; j < len; j++) {
//				if (jsons.getJSONObject(j).optString("code").equals(key)) {
//					iv.setValue(jsons.getJSONObject(j).toString());
//					return jsons.getJSONObject(j).optString("value");
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return "";
//	}
//
//	private boolean getCheck(InputItemView iv, String value) throws JSONException {
//		try {
//			JSONArray jsons = new JSONArray("[" + value + "]");
//			int len = jsons.length();
//			for (int j = 0; j < len; j++) {
//				if (iv.isJsonObjectEquals(jsons.getJSONObject(j), new JSONObject(iv.getInputModel().itemValues[0]))) {
//					iv.setCheck(true);
//					return true;
//				}
//			}
//			iv.setCheck(false);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
//
//	public void showInputNumDialog(final InputItemView view, final String unit, final String format) {
//		float defValue = 0;
//
//		CustomSingleNumPickDialog.Builder builder = new CustomSingleNumPickDialog.Builder(mContext);
//		builder.setDefualtNum(defValue);
//		builder.setFloat(false);
//		builder.setUnit(unit);
//		builder.setLimit(1, 100);
//		builder.setDefualtNum(2);
//		builder.setOnChangeNumListener(new OnChangeNumListener() {
//			@Override
//			public void onChange(Dialog dialog, float num) {
//				try {
//					view.setValue(format.replace("time", (int) num + ""));
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				view.setDisplayValue((int) num + unit);
//			}
//		});
//		builder.create().show();
//	}
//
//	public void setDefaultValue(boolean b, boolean[] bs) {
//		if (b) {
//			checkButtonOk(true);
//			for (int i = 0; i < bs.length; i++) {
//				checkItem(i, bs[i]);
//			}
//		} else {
//			checkButtonNo(true);
//		}
//	}
//
//	public void setOutTouchCancel(boolean b) {
//		mRootView.setOnClickListener(b ? this : null);
//	}
//
//	private View createRootView() {
//		View layout = View.inflate(mContext, R.layout.window_input_item_smoke, null);
//		return layout;
//	}
//
//	public void createItemAction(Context context, String str, LinearLayout root, int index) {
//		TextView tv = new TextView(context);
//		int pading = Util.dipToPx(mContext, 50);
//		tv.setPadding(pading, 0, 12, 0);
//		tv.setText(str);
//		tv.setTextColor(Color.BLACK);
//		tv.setTag(false);
//		tv.setTextSize(18);
//		tv.setGravity(Gravity.CENTER_VERTICAL);
//		tv.setBackgroundResource(R.drawable.bg_check_style1);
//		tv.setId(index);
//		tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check_style_1_a, 0);
//		tv.setOnClickListener(this);
//		root.addView(tv);
//	}
//
//	public void setOnClick(OnClick itemClick) {
//		this.itemClick = itemClick;
//	}
//
//	private void checkButtonOk(boolean b) {
//		checkButton(tvOk, b);
//		layoutCheck.setVisibility(b ? View.VISIBLE : View.GONE);
//		btnOk.setVisibility(View.VISIBLE);
//	}
//
//	private void checkButtonNo(boolean b) {
//		checkButton(tvNo, b);
//	}
//
//	private void checkButton(TextView v, boolean b) {
//		v.setTag(b);
//		v.setCompoundDrawablesWithIntrinsicBounds(0, 0, !b ? R.drawable.check_style_0_a : R.drawable.check_style_0_b, 0);
//	}
//
//	private void onClick() throws JSONException {
//		String value = "";
//		int type = 0;
//		boolean bNO = (Boolean) tvNo.getTag();
//		boolean bOK = (Boolean) tvOk.getTag();
//		if (bNO && bOK) {
//			type = 0;
//		} else if (bOK) {
//
//			StringBuffer sb = new StringBuffer();
//			JSONObject obj = new JSONObject();
//			obj.put("code", "CYDASHXG004");
//			obj.put("pcode", "CYDASHXG");
//			obj.put("value", "RADIO_VALUE_IS");
//			sb.append(obj.toString()).append(",");
//			final String value1 = view1.getValue();
//			final String value2 = view2.getValue();
//			final String value3 = view3.getValue();
//			final String value4 = view4.getValue();
//
//			if (!TextUtils.isEmpty(value1)) {
//				sb.append(value1).append(",");
//			}
//			if (!TextUtils.isEmpty(value2)) {
//				sb.append(value2).append(",");
//			}
//			if (!TextUtils.isEmpty(value3)) {
//				sb.append(value3).append(",");
//			}
//			if (!TextUtils.isEmpty(value4)) {
//				sb.append(value4).append(",");
//			}
//			value = sb.toString();
//			value = value.substring(0, value.length() - 1);
//			type = 1;
//		} else {
//			JSONObject obj = new JSONObject();
//			obj.put("code", "CYDASHXG004");
//			obj.put("pcode", "CYDASHXG");
//			obj.put("value", "RADIO_VALUE_ISNOT");
//			value = obj.toString();
//			type = 2;
//		}
//		itemClick.onClick(type, value);
//	}
//
//	@Override
//	public void onClick(View v) {
//
//		if (v instanceof InputItemView) {
//			InputItemView iv = ((InputItemView) v);
//			iv.checkOnClick();
//		} else if (v.getId() == R.id.btn_ok) {
//			if (itemClick != null) {
//				try {
//					onClick();
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//			dismiss();
//		} else if (v.getId() == R.id.root_layout) {
//			dismiss();
//		} else if (v.getId() == R.id.check_ok) {
//			boolean b = !((Boolean) tvOk.getTag());
//			checkButtonOk(b);
//			if (b) {
//				checkButtonNo(false);
//				btnOk.setVisibility(View.VISIBLE);
//			} else {
//				btnOk.setVisibility(View.GONE);
//			}
//		} else if (v.getId() == R.id.check_no) {
//			boolean b = !((Boolean) tvNo.getTag());
//			checkButtonNo(b);
//			if (b) {
//				btnOk.setVisibility(View.GONE);
//				checkButtonOk(false);
//				if (itemClick != null) {
//					try {
//						onClick();
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//				dismiss();
//			}
//		} else {
//			// final int position = (Integer) v.getId();
//			TextView tv = (TextView) v;
//			boolean b = !((Boolean) tv.getTag());
//			tv.setTag(b);
//			tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, !b ? R.drawable.check_style_1_a
//					: R.drawable.check_style_1_b, 0);
//		}
//
//	}
//
//	private boolean isCheckItem(int position) {
//		return (Boolean) layoutCheck.getChildAt(position).getTag();
//	}
//
//	private void checkItem(int position, boolean b) {
//		TextView tv = (TextView) layoutCheck.getChildAt(position);
//		tv.setTag(b);
//		tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, !b ? R.drawable.check_style_1_a : R.drawable.check_style_1_b,
//				0);
//	}
//
//	public interface OnClick {
//		void onClick(int checkType, String bs);
//	}
//
//}
