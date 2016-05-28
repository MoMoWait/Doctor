//package com.comvee.tnb.view;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//
//import org.chenai.util.TimeUtil;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.FragmentActivity;
//import android.text.Editable;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.util.AttributeSet;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.EditText;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.comvee.tnb.R;
//import com.comvee.tnb.activity.MainActivity;
//import com.comvee.tnb.dialog.CustomDatePickDialog;
//import com.comvee.tnb.dialog.CustomDatePickDialog.OnTimeChangeListener;
//import com.comvee.tnb.dialog.CustomFloatNumPickDialog;
//import com.comvee.tnb.dialog.CustomFloatNumPickDialog.OnNumChangeListener;
//import com.comvee.tnb.model.InputModel;
//import com.comvee.tnb.view.MultiInputItemWindow.OnItemClick;
//import com.comvee.tool.UITool;
//
//public class InputItemView extends RelativeLayout implements OnClickListener, OnTimeChangeListener {
//
//	public InputModel mInputModel;
//
//	private TextView tvName;
//	private TextView tvValue;
//	private View btnCheck;
//	private EditText edtValue;
//	private boolean isHashSign;
//	private View imgTag;
//
//	private DialogInterface.OnClickListener itemListener;
//	private OnCheckListener checkListener;
//
//	public InputModel getInputModel() {
//		return mInputModel;
//	}
//
//	public void setmInputModel(InputModel mInputModel) {
//		this.mInputModel = mInputModel;
//	}
//
//	public void setOnRadioClickListener(DialogInterface.OnClickListener itemListener) {
//		this.itemListener = itemListener;
//	}
//
//	public void setCheckListener(OnCheckListener checkListener) {
//		this.checkListener = checkListener;
//	}
//
//	public InputItemView(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//	}
//
//	public InputItemView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//	}
//
//	public InputItemView(Context context) {
//		super(context);
//	}
//
//	@Override
//	protected void onFinishInflate() {
//		super.onFinishInflate();
//	}
//
//	private void init(final InputModel mInputModel) {
//
//		LayoutInflater.from(getContext()).inflate(R.layout.item_input, this, true);
//		imgTag = findViewById(R.id.img_tag);
//		tvName = (TextView) findViewById(R.id.tv_label);
//		tvValue = (TextView) findViewById(R.id.tv_value);
//		btnCheck = findViewById(R.id.btn_check);
//		edtValue = (EditText) findViewById(R.id.edt_value);
//		// btnCheck.setTag(false);
//
//		if (mInputModel.type == 1) {
//			btnCheck.setVisibility(View.VISIBLE);
//			tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//			tvValue.setVisibility(View.GONE);
//		} else {
//			btnCheck.setVisibility(View.GONE);
//			// tvValue.setVisibility(View.VISIBLE);
//		}
//
//		// View btn = findViewById(R.id.btn_label);
//		// btn.setOnClickListener(this);
//
//		tvName.setText(mInputModel.name);
//
//		if (mInputModel.type == 1) {
//			initCheckBtn(mInputModel.items[0], mInputModel.items[1]);
//			// setCheck(false);
//		} else if (mInputModel.type == 2) {
//			// if (!TextUtils.isEmpty(mInputModel.defValue)) {
//			// if ("now".equalsIgnoreCase(mInputModel.defValue)) {
//			// mInputModel.defValue =
//			// TimeUtil.fomateTime(Calendar.getInstance().getTimeInMillis(),
//			// "yyyy-MM-dd");
//			// } else {
//			// mInputModel.defValue = mInputModel.defValue;
//			// }
//			// setValue(mInputModel.defValue);
//			// }
//		} else if (mInputModel.type == 7) {
//			tvName.setOnClickListener(this);
//			tvValue.setVisibility(View.GONE);
//			edtValue.setVisibility(View.VISIBLE);
//			edtValue.addTextChangedListener(new TextWatcher() {
//				@Override
//				public void onTextChanged(CharSequence s, int start, int before, int count) {
//				}
//
//				@Override
//				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//				}
//
//				@Override
//				public void afterTextChanged(Editable s) {
//					mInputModel.value = mInputModel.itemValues[0].replace("txt", edtValue.getText().toString());
//				}
//			});
//		}
//
//		setTag(mInputModel.tagGroup.trim());
//
//	}
//
//	public void setInputModel(InputModel mInputModel) {
//		this.mInputModel = mInputModel;
//		init(mInputModel);
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.tv_label:
//		case R.id.btn_label:
//			checkOnClick();
//			break;
//		default:
//			break;
//		}
//	}
//
//	public void checkOnClick() {
//		switch (mInputModel.type) {
//		case 1:
//			setCheck(!isCheck());
//			break;
//		case 3:
//			showItemsDialog(this, mInputModel.items);
//			break;
//		case 2:
//			showSetTimeDialog();
//			break;
//		case 4:
//			showCheckDialog(this, mInputModel.items);
//			break;
//		case 6:
//			showNumDialog(this, mInputModel.items);
//			break;
//		case 7:
//			edtValue.requestFocus();
//			UITool.openInputMethodManager(getContext(), edtValue);
//			break;
//		default:
//			break;
//		}
//
//	}
//
//	public void showNumDialog(View view, final String[] items) {
//		CustomFloatNumPickDialog buidler = new CustomFloatNumPickDialog();
//
//		buidler.setLimitNum(Integer.valueOf(items[0]), Integer.valueOf(items[1]));
//		buidler.setFloat(false);
//		buidler.setDefult(Integer.valueOf(mInputModel.defValue));
//		buidler.setLabel(items[2]);
//		buidler.addOnNumChangeListener(new OnNumChangeListener() {
//
//			@Override
//			public void onChange(DialogFragment dialog, float num) {
//				String n = String.valueOf((int) num);
//				setDisplayValue(n + items[2]);
//				mInputModel.defValue = n;
//				mInputModel.value = mInputModel.itemValues[0].replace("num", n);
//			}
//		});
//		buidler.show(((MainActivity) getContext()).getSupportFragmentManager(), "dialog");
//	}
//
//	public void showCheckDialog(View view, String[] items) {
//		final String[] extr = mInputModel.extra.split("\\&");
//		OnItemClick listener = new OnItemClick() {
//			@Override
//			public void onClick(int checkType, boolean[] bs) {
//				try {
//					StringBuffer sb = new StringBuffer();
//					if (checkType == 1) {// 是
//						StringBuffer sb1 = new StringBuffer();
//						sb.append(extr[0]);
//						sb.append(",");
//						boolean isCheck = false;
//						for (int i = 0; i < bs.length; i++) {
//							if (bs[i]) {
//								isCheck = true;
//								sb.append(mInputModel.itemValues[i]);
//								sb.append(",");
//								sb1.append(mInputModel.items[i]);
//								sb1.append("，");
//							}
//						}
//						if (isCheck) {
//							sb.append(extr[3]);
//						}
//
//						if (isCheck) {
//							setDisplayValue("是（" + sb1.substring(0, sb1.length() - 1) + "）");
//						} else {
//							setDisplayValue("是");
//						}
//					} else if (checkType == 2) {// 否
//						sb.append(extr[1]);
//						setDisplayValue("否");
//					} else if (checkType == 3) {
//						sb.append(extr[2]);
//						setDisplayValue("不知道");
//					} else {
//						setDisplayValue("");
//					}
//					mInputModel.value = sb.toString();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//
//		};
//
//		MultiInputItemWindow window = new MultiInputItemWindow(getContext(), items, mInputModel.name);
//
//		try {
//			JSONArray array = new JSONArray(String.format("[%s]", mInputModel.value));
//			final boolean ok = containMultiItem(array, extr[0]);
//			final boolean no = containMultiItem(array, extr[1]);
//			final boolean unKown = containMultiItem(array, extr[2]);
//
//			if (ok) {
//				int len = mInputModel.itemValues.length;
//				boolean[] b = new boolean[len];
//				for (int i = 0; i < len; i++) {
//					b[i] = containMultiItem(array, mInputModel.itemValues[i]);
//				}
//				window.setDefaultValue(1, b);
//			} else if (no) {
//				window.setDefaultValue(2, null);
//			} else if (unKown) {
//				window.setDefaultValue(3, null);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		;
//		window.setOutTouchCancel(true);
//		window.setOnItemClick(listener);
//		window.showAtLocation(this, Gravity.CENTER, 0, 0);
//	}
//
//	public boolean containMultiItem(JSONArray array, String str) {
//		try {
//			JSONObject obj = new JSONObject(str);
//			final int len = array.length();
//			for (int i = 0; i < len; i++) {
//				JSONObject o = array.getJSONObject(i);
//				if (isJsonObjectEquals(o, obj)) {
//					return true;
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
//
//	public boolean isJsonObjectEquals(JSONObject o, JSONObject obj) {
//		if (obj.optString("code").equalsIgnoreCase(o.optString("code")) && obj.optString("value").equalsIgnoreCase(o.optString("value"))) {
//			return true;
//		}
//		return false;
//	}
//
//	public int getCheckIndex() {
//		int selectIndex = -1;
//		try {
//			selectIndex = getIndexByList(mInputModel.itemValues, mInputModel.value);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return selectIndex;
//	}
//
//	private int getIndexByList(String[] items, String value) throws JSONException {
//		if (TextUtils.isEmpty(value)) {
//			return -1;
//		}
//		for (int i = 0; i < items.length; i++) {
//			JSONObject o = new JSONObject(items[i]);
//			JSONObject o1 = new JSONObject(value);
//			if (isJsonObjectEquals(o, o1)) {
//				return i;
//			}
//		}
//		return -1;
//	}
//
//	private ArrayList<String> getMultiItems(String value) {
//		ArrayList<String> list = new ArrayList<String>();
//		try {
//			JSONArray array = new JSONArray(String.format("[%s]", value));
//			int len = array.length();
//			for (int i = 0; i < len; i++) {
//				list.add(array.getJSONObject(i).toString());
//				System.out.println(list.get(i));
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return list;
//	}
//
//	// private String getValuePacket(String code, String value, String
//	// fatherValue) {
//	// // {"code":"编码","value":"值","pcode":"父编码"}
//	// try {
//	// if (mInputModel.name.equals("") || mInputModel.name.contains("基本信息")) {
//	// if (mInputModel.type == 2) {// 时间
//	// return value;
//	// } else {
//	// return code;
//	// }
//	// } else {
//	// JSONObject json = new JSONObject();
//	// json.put("code", code);
//	// json.put("pcode", fatherValue);
//	// json.put("value", value);
//	// return json.toString();
//	// }
//	// } catch (Exception e) {
//	// e.printStackTrace();
//	// }
//	// return "";
//	// }
//
//	public void showItemsDialog(View view, String[] items) {
//		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//
//				String str = mInputModel.items[which];
//				// if (str != null && str.length() > 4)
//				// {
//				// str = str.substring(0, 4) + "...";
//				// }
//				setDisplayValue(str);
//				mInputModel.value = mInputModel.itemValues[which];
//				// dialog.cancel();
//				if (null != itemListener) {
//					itemListener.onClick(dialog, which);
//				}
//				if (checkListener != null) {
//					checkListener.onCheck(InputItemView.this, which);
//				}
//			}
//		};
//
//		int selectIndex = -1;
//		try {
//			selectIndex = getIndexByList(mInputModel.itemValues, mInputModel.value);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		// selectIndex =
//		// Arrays.asList(mInputModel.itemValues).indexOf(mInputModel.value);
//		SingleInputItemWindow window = new SingleInputItemWindow(getContext(), items, mInputModel.name, selectIndex, R.drawable.check_style_0_b,
//				R.drawable.check_style_0_a);
//		window.setOutTouchCancel(true);
//		window.setOnItemClick(listener);
//		window.showAtLocation(this, Gravity.CENTER, 0, 0);
//
//		// new AlertDialog.Builder(getContext()).setTitle(mInputModel.name)
//		// .setSingleChoiceItems(items, selectIndex, listener).show();
//	}
//
//	public void showSetTimeDialog() {
//		CustomDatePickDialog builder = new CustomDatePickDialog();
//		builder.setDefaultTime(Calendar.getInstance());
//		builder.setOnTimeChangeListener(this);
//		Calendar cal = Calendar.getInstance();
//		try {
//			final String time = tvValue.getText().toString();
//			if (!TextUtils.isEmpty(time)) {
//				cal.setTimeInMillis(TimeUtil.getUTC(time, "yyyy-MM-dd"));
//			} else {
//				if (!TextUtils.isEmpty(mInputModel.defValue)) {
//					if ("now".equalsIgnoreCase(mInputModel.defValue)) {
//						mInputModel.defValue = TimeUtil.fomateTime(Calendar.getInstance().getTimeInMillis(), "yyyy-MM-dd");
//					} else {
//						// mInputModel.defValue = mInputModel.defValue;
//					}
//					cal.setTimeInMillis(TimeUtil.getUTC(mInputModel.defValue, "yyyy-MM-dd"));
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		builder.setDefaultTime(cal);
//
//		if (mInputModel.items.length == 0) {
//			builder.setLimitTime(1800, Calendar.getInstance().get(Calendar.YEAR));
//		} else {
//			builder.setLimitTime(parseYear(mInputModel.items[0]), parseYear(mInputModel.items[1]));
//		}
//
//		builder.show(((MainActivity) getContext()).getSupportFragmentManager(), "dialog");
//	}
//
//	private int parseYear(String strYear) {
//		int year = 2013;
//		if (TextUtils.isEmpty(strYear)) {
//			year = Calendar.getInstance().get(Calendar.YEAR);
//		} else if ("now".equalsIgnoreCase(strYear)) {
//			year = Calendar.getInstance().get(Calendar.YEAR);
//		} else {
//			year = Integer.valueOf(strYear);
//		}
//		return year;
//	}
//
//	public void initCheckBtn(String ok, String no) {
//		TextView tvOk = (TextView) btnCheck.findViewById(R.id.check_ok);
//		TextView tvNo = (TextView) btnCheck.findViewById(R.id.check_no);
//
//		tvOk.setText(ok);
//		tvNo.setText(no);
//
//	}
//
//	public void setCheck(boolean b) {
//
//		TextView tvOk = (TextView) btnCheck.findViewById(R.id.check_ok);
//		TextView tvNo = (TextView) btnCheck.findViewById(R.id.check_no);
//
//		tvOk.setCompoundDrawablesWithIntrinsicBounds(b ? R.drawable.check_ok : R.drawable.check_no, 0, 0, 0);
//		tvNo.setCompoundDrawablesWithIntrinsicBounds(b ? R.drawable.check_no : R.drawable.check_ok, 0, 0, 0);
//		// tvNo.setVisibility(b?View.INVISIBLE:View.VISIBLE);
//		// tvOk.setVisibility(b?View.VISIBLE:View.INVISIBLE);
//
//		// btnCheck.setBackgroundResource(b ? R.drawable.check_ok :
//		// R.drawable.check_no);
//		btnCheck.setTag(b);
//		int i = b ? 0 : 1;
//		mInputModel.value = mInputModel.itemValues[i];
//		if (checkListener != null) {
//			checkListener.onCheck(this, i);
//		}
//	}
//
//	public boolean isCheck() {
//		if (btnCheck.getTag() == null) {
//			return false;
//		}
//		return (Boolean) btnCheck.getTag();
//	}
//
//	public String getValue() {
//		return mInputModel.value;
//	}
//
//	public void setValue(String value) throws Exception {
//		mInputModel.value = value;
//		if (mInputModel.type == 1) {
//			int selectIndex = getIndexByList(mInputModel.itemValues, mInputModel.value);
//			if (selectIndex == 0) {
//				setCheck(true);
//			} else if (selectIndex == 1) {
//				setCheck(false);
//			}
//		} else if (mInputModel.type == 2) {
//			setDisplayValue(new JSONObject(value).getString("value"));
//		} else if (mInputModel.type == 3) {
//			int selectIndex = getIndexByList(mInputModel.itemValues, mInputModel.value);
//			if (selectIndex >= 0) {
//				setDisplayValue(mInputModel.items[selectIndex]);
//			}
//		} else if (mInputModel.type == 4) {
//			JSONArray array = new JSONArray(String.format("[%s]", mInputModel.value));
//			final boolean ok = containMultiItem(array, mInputModel.extra.split("\\&")[0]);
//			final boolean no = containMultiItem(array, mInputModel.extra.split("\\&")[1]);
//			final boolean unKown = containMultiItem(array, mInputModel.extra.split("\\&")[2]);
//
//			if (ok) {
//				StringBuffer sb = new StringBuffer();
//				sb.append("是");
//				boolean isCheck = false;
//				int len = mInputModel.itemValues.length;
//				boolean[] b = new boolean[len];
//				for (int i = 0; i < len; i++) {
//					b[i] = containMultiItem(array, mInputModel.itemValues[i]);
//					if (b[i]) {
//						isCheck = true;
//						sb.append(mInputModel.items[i]);
//						sb.append("，");
//					}
//
//				}
//				if (isCheck) {
//					setDisplayValue("是（" + sb.substring(0, sb.length() - 1) + "）");
//				} else {
//					setDisplayValue("是");
//				}
//
//			} else if (no) {
//				setDisplayValue("否");
//			} else if (unKown) {
//				setDisplayValue("不知道");
//			} else {
//				setDisplayValue("");
//			}
//		} else if (mInputModel.type == 5) {
//			JSONArray array = new JSONArray("[" + value + "]");
//			if (containMultiItem(array, mInputModel.itemValues[0])) {
//				setDisplayValue("是");
//			} else if (containMultiItem(array, mInputModel.itemValues[1])) {
//				setDisplayValue("否");
//			}
//
//		} else if (mInputModel.type == 6) {
//			String str = new JSONObject(value).getString("value");
//			if (!TextUtils.isEmpty(str)) {
//				str += mInputModel.items[2];
//			}
//			setDisplayValue(str);
//		} else {
//			String str = new JSONObject(value).getString("value");
//			setDisplayValue(str.toString());
//		}
//
//	}
//
//	public void setDisplayValue(String str) {
//		if (mInputModel.type == 7) {
//			edtValue.setText(str);
//		} else {
//			tvValue.setText(str);
//			tvValue.setVisibility(TextUtils.isEmpty(str) ? View.GONE : View.VISIBLE);
//		}
//		if (isHashSign) {
//			imgTag.setVisibility(!TextUtils.isEmpty(str) ? View.INVISIBLE : View.VISIBLE);
//		}
//		// if (TextUtils.isEmpty(str))
//		// {
//		// tvValue.setCompoundDrawablesWithIntrinsicBounds(0, 0,
//		// R.drawable.dir_right, 0);
//		// } else
//		// {
//		// tvValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//		// }
//	}
//
//	public void setInputSgin(boolean b) {
//		isHashSign = b;
//		if (mInputModel.type != 1) {
//			imgTag.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
//		}
//		// tvName.setCompoundDrawablesWithIntrinsicBounds(b ?
//		// R.drawable.member_input_sgin : 0, 0, 0, 0);
//	}
//
//	public boolean isHashInputSgin() {
//		return isHashSign;
//	}
//
//	public interface OnCheckListener {
//		public void onCheck(InputItemView view, int check);
//	}
//
//	@Override
//	public void onChange(DialogFragment dialog, int year, int month, int day) {
//		// TODO Auto-generated method stub
//		Calendar cal = Calendar.getInstance();
//		cal.set(year, month - 1, day);
//		if (mInputModel.items.length > 1 && "now".equalsIgnoreCase(mInputModel.items[1]) && Calendar.getInstance().before(cal)) {
//			Toast.makeText(getContext(), getContext().getResources().getString(R.string.txt_date_choose_limit), Toast.LENGTH_SHORT).show();
//			return;
//		} else {
//			String time = String.format("%d-%02d-%02d", year, month, day);
//			setDisplayValue(time);
//			mInputModel.defValue = time;
//			mInputModel.value = mInputModel.itemValues[0].replace("time", time);
//		}
//	}
//
//}
