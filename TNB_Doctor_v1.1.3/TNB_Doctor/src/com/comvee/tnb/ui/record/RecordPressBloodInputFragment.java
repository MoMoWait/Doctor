package com.comvee.tnb.ui.record;

import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.comvee.FinalDb;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDatePickDialog;
import com.comvee.tnb.dialog.CustomMultiNumPickDialog;
import com.comvee.tnb.dialog.CustomMultiNumPickDialog.OnChangeMultiNumListener;
import com.comvee.tnb.dialog.CustomNumPickDialog.OnChangeNumListener;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.TendencyPointInfo;
import com.comvee.tnb.network.NetworkCallBack;
import com.comvee.tnb.network.RequestRecordUtil;
import com.comvee.tnb.ui.tool.blood.BluetoothUtil;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;
import com.comvee.util.TimeUtil;

/**
 * 录入血压数据
 * 
 * @author friendlove
 * 
 */
@SuppressLint("ValidFragment")
public class RecordPressBloodInputFragment extends BaseFragment implements OnHttpListener, OnClickListener, OnChangeMultiNumListener,
		OnChangeNumListener, OnCancelListener {

	private final String mInputUrl = ConfigUrlMrg.TENDENCY_INPUT;
	private TextView tvTime;
	private TextView tvValue;
	private LinearLayout layoutInput;
	private String mFormatRed = "<font color='#ff3300'>%s</font>";
	private String mFormatGreed = "<font color='#66cc66'>%s</font>";
	private String mFormatBlue = "<font color='#3399ff'>%s</font>";
	private int mHighPress, mLowPress;
	private static final int HIGH_PRESS_MIN = 90;
	private static final int HIGH_PRESS_MAX = 140;
	private static final int LOW_PRESS_MIN = 60;
	private static final int LOW_PRESS_MAX = 90;
	private static final String UNIT = "mmHg";
	private TitleBarView mBarView;
	private Button btn_remove;
	private TendencyPointInfo info;
	private FinalDb db;
	private String time;

	public RecordPressBloodInputFragment() {
	}

	public static RecordPressBloodInputFragment newInstance(String time) {
		RecordPressBloodInputFragment fragment = new RecordPressBloodInputFragment();
		fragment.setTime(time);
		return fragment;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.record_pressblood_input_fragment;
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onLaunch(Bundle bundle) {
		db = FinalDb.create(getContext(), ConfigParams.DB_NAME);
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		init();
		mBarView.setRightButton(getString(R.string.save), this);
		showProgressDialog(getString(R.string.msg_loading));
		RequestRecordUtil util = new RequestRecordUtil();
		util.request(new NetworkCallBack() {

			@Override
			public void callBack(int what, int status, Object obj) {
				cancelProgressDialog();
				getLocRecord();
			}
		}, db);
	}

	private void init() {

		final View btnTime = findViewById(R.id.btn_settime);
		layoutInput = (LinearLayout) findViewById(R.id.layout_input);
		tvTime = (TextView) findViewById(R.id.tv_settime);
		tvValue = (TextView) findViewById(R.id.tv_value);
		findViewById(R.id.btn_input).setOnClickListener(this);
		btn_remove = (Button) findViewById(R.id.btn_remove);
		btnTime.setOnClickListener(this);
		btn_remove.setOnClickListener(this);

		// 默认日期
		if (TextUtils.isEmpty(time)) {
			onChangeTime(Calendar.getInstance());
		} else {
			tvTime.setText(time);
		}
		mBarView.setTitle(getString(R.string.title_record_blood));

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onDestroyView() {
		BluetoothUtil.getInstance().close();
		super.onDestroyView();
	}

	public void saveData() throws JSONException {

		JSONArray array = new JSONArray();
		String time = tvTime.getText().toString();

		if (TextUtils.isEmpty(tvValue.getText().toString())) {
			showToast("请先填写测量值!");
			return;
		}

		JSONObject obj = null;
		// 低压
		obj = new JSONObject();
		obj.put("time", time);
		obj.put("code", "bloodpressurediastolic");
		obj.put("value", mLowPress);
		array.put(obj);
		// 高压
		obj = new JSONObject();
		obj.put("time", time);
		obj.put("code", "bloodpressuresystolic");
		obj.put("value", mHighPress);
		array.put(obj);

		String paramStr = array.toString();
		showProgressDialog(getString(R.string.msg_loading));
		ComveeHttp http = new ComveeHttp(getApplicationContext(), mInputUrl);
		http.setPostValueForKey("paramStr", paramStr);
		http.setOnHttpListener(1, this);
		http.startAsynchronous();
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		cancelProgressDialog();
		switch (what) {
		case 1://保存
			try {
				ComveePacket packet = ComveePacket.fromJsonString(b);
				if (packet.getResultCode() == 0) {
					showToast(packet.getResultMsg());
					// clearInputData(vPager.getCurrentItem());
					ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.INDEX));
					ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TENDENCY_POINT_LIST));
//					FragmentMrg.toBack(getActivity(),new Bundle());
					toFragment(RecordMainFragment.newInstance(true, 1, RecordMainFragment.BACK_RECORD_CHOOSE_FRG), false, true);
				} else {
					ComveeHttpErrorControl.parseError(getActivity(), packet);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;
		case 4://删除记录
			try {
				ComveePacket packet = ComveePacket.fromJsonString(b);

				if (packet.getResultCode() == 0) {
					FragmentMrg.toBack(getActivity(),new Bundle());
//					toFragment(RecordMainFragment.newInstance(true, 1, RecordMainFragment.BACK_RECORD_CHOOSE_FRG), true, true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onFialed(int what, int errorCode) {
		cancelProgressDialog();
		ComveeHttpErrorControl.parseError(getActivity(), errorCode);
	}

	@Override
	public boolean onBackPress() {
		FragmentMrg.toBack(getActivity());
		return true;
	}

	private void setInputValue(int index, String value, int b) {
		switch (b) {
		case 0:
			tvValue.setText(Html.fromHtml(String.format(mFormatGreed, value)));
			break;
		case 1:
			tvValue.setText(Html.fromHtml(String.format(mFormatBlue, value)));
			break;
		case 2:
			tvValue.setText(Html.fromHtml(String.format(mFormatRed, value)));
			break;
		default:
			break;
		}

	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.btn_input:
			showSetMultiValueDialog();
			break;
		case TitleBarView.ID_RIGHT_BUTTON:
			try {
				saveData();
			} catch (Exception e) {
				showToast("出错");
				e.printStackTrace();
			}
			break;
		case R.id.btn_settime:
			showTimeDialogDate(Calendar.getInstance());
			break;
		case R.id.btn_remove:
			if (info != null) {
				requestRemoveValue(info.getId());
			}
			break;
		default:
			break;
		}

	}

	private void showTimeDialogDate(Calendar calendar) {
		CustomDatePickDialog dialog = new CustomDatePickDialog();
		dialog.setOnTimeChangeListener(new CustomDatePickDialog.OnTimeChangeListener() {

			@Override
			public void onChange(DialogFragment dialog, int year, int month, int day) {
				// TODO Auto-generated method stub
				String time = String.format("%d-%02d-%02d", year, month, day);
				tvTime.setText(time);
				getLocRecord();
			}
		});

		String dataTiem = tvTime.getText().toString();
		if (!TextUtils.isEmpty(dataTiem)) {
			Calendar date = Calendar.getInstance();
			try {
				date.setTimeInMillis(TimeUtil.getUTC(dataTiem, "yyyy-MM-dd"));
				dialog.setDefaultTime(date);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {

			try {
				dialog.setDefaultTime(calendar);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		dialog.setLimitCanSelectOfEndTime(calendar);
		dialog.setLimitTime(calendar.get(Calendar.YEAR) - 1, calendar.get(Calendar.YEAR));
		dialog.setYearCyclic(false);

		dialog.show(getActivity().getSupportFragmentManager(), "dialog");
	}

	/**
	 * 多值 多列 的选择控件
	 */
	public void showSetMultiValueDialog() {

		int[] max = { 200, 200 };
		int[] min = { 1, 1 };
		// 如果已经填写这个值 就有 用 这个值 如果没有 用默认值
		float[] def = { mHighPress != 0 ? mHighPress : 91, mLowPress != 0 ? mLowPress : 61 };
		String[] titles = { "收缩压(高压)", "舒张压(低压)" };
		String[] units = { UNIT, UNIT };

		CustomMultiNumPickDialog builder = new CustomMultiNumPickDialog();
		builder.setMultiDefualtNum(def);
		// builder.setFloat(mModel.isFloat);
		builder.setMultiTitles(titles);
		builder.setMultiLimit(min, max);
		builder.setMultiUnits(units);
		builder.setOnChangeMultiNumListener(this);
		builder.show(getActivity().getSupportFragmentManager(), "dialog");
		// builder.getDialog().setOnCancelListener(this);
	}

	private void onChangeTime(Calendar cal) {
		if (Calendar.getInstance().getTimeInMillis() < cal.getTimeInMillis()) {
			Toast.makeText(getContext(), getContext().getResources().getString(R.string.txt_date_choose_limit), Toast.LENGTH_SHORT).show();
			return;
		}
		String str = TimeUtil.fomateTime(cal.getTimeInMillis(), "yyyy-MM-dd");
		tvTime.setText(str);
	}

	private void onChangeValues(float... fs) {
		mHighPress = (int) fs[0];
		mLowPress = (int) fs[1];
		String str = mHighPress + "/" + mLowPress + UNIT;
		TextView tv = (TextView) findViewById(R.id.tv_sign);
		tv.setVisibility(View.GONE);
		if (mHighPress > HIGH_PRESS_MAX) {
			if (mLowPress > LOW_PRESS_MIN) {
				tv.setVisibility(View.VISIBLE);
				tv.setText(Html.fromHtml(String.format(mFormatRed, "↑")));
			}
			setInputValue(0, str, 2);
		} else if (mLowPress > LOW_PRESS_MAX) {
			if (mHighPress > HIGH_PRESS_MIN) {
				tv.setVisibility(View.VISIBLE);
				tv.setText(Html.fromHtml(String.format(mFormatRed, "↑")));
			}
			setInputValue(0, str, 2);
		} else if (mHighPress < HIGH_PRESS_MIN) {
			if (mLowPress < LOW_PRESS_MAX) {
				tv.setVisibility(View.VISIBLE);
				tv.setText(Html.fromHtml(String.format(mFormatBlue, "↓")));
			}
			setInputValue(0, str, 1);
		} else if (mLowPress < LOW_PRESS_MIN) {
			if (mHighPress < LOW_PRESS_MAX) {
				tv.setVisibility(View.VISIBLE);
				tv.setText(Html.fromHtml(String.format(mFormatBlue, "↓")));
			}
			setInputValue(0, str, 1);
		} else {
			tv.setVisibility(View.GONE);
			setInputValue(0, str, 0);
		}

	}

	public void clearData() {
		LinearLayout root = layoutInput;
		for (int i = 0; i < root.getChildCount(); i++) {
			View view = root.getChildAt(i);
			TextView tv = (TextView) view.findViewById(R.id.tv_value);
			View tvSign = view.findViewById(R.id.tv_sign);
			tv.setText(null);
			tvSign.setVisibility(View.GONE);
		}

	}

	@Override
	public void onCancel(DialogInterface dialog) {
	}

	@Override
	public void onChange(DialogFragment dialog, float[] num) {
		// TODO Auto-generated method stub
		onChangeValues(num);
	}

	@Override
	public void onChange(DialogFragment dialog, float num) {
		// TODO Auto-generated method stub
		onChangeValues(num);
	}

	private void getLocRecord() {
		String sql = null;
		String code = "code='bloodpressurediastolic'";
		String code_1 = "code='bloodpressuresystolic'";
		String day = String.format("date(time)=date('%s')", tvTime.getText().toString());
		sql = String.format("(%s) and %s order by date(time) desc,insertDt desc", code, day);
		List<TendencyPointInfo> infos = db.findAllByWhere(TendencyPointInfo.class, sql);
		sql = String.format("(%s) and %s order by date(time) desc,insertDt desc", code_1, day);
		List<TendencyPointInfo> infos_1 = db.findAllByWhere(TendencyPointInfo.class, sql);
		if (infos.size() > 0 && infos_1.size() > 0) {
			info = infos.get(0);
			btn_remove.setVisibility(View.VISIBLE);
			float flo[] = { infos_1.get(0).getValue(), infos.get(0).getValue() };
			onChangeValues(flo);
		} else {
			info = null;
			tvValue.setText("");
			btn_remove.setVisibility(View.GONE);
			findViewById(R.id.tv_sign).setVisibility(View.GONE);
		}
	}

	private void requestRemoveValue(String id) {
		showProgressDialog(getString(R.string.msg_loading));
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.RECORD_REMOVE_VALUE);
		http.setPostValueForKey("paramLogId", id);
		http.setOnHttpListener(4, this);
		http.startAsynchronous();
	}
}
