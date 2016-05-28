package com.comvee.tnb.ui.record;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
import android.widget.TextView;
import android.widget.Toast;

import com.comvee.FinalDb;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDatePickDialog;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.dialog.CustomFloatNumPickDialog;
import com.comvee.tnb.dialog.CustomFloatNumPickDialog.OnNumChangeListener;
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
 * 录入糖化血红蛋白数据
 * 
 * @author friendlove
 * 
 */
@SuppressLint("ValidFragment")
public class RecordHemoglobinFragment extends BaseFragment implements OnHttpListener, OnClickListener, OnNumChangeListener, OnCancelListener {

	private final String mInputUrl = ConfigUrlMrg.TENDENCY_INPUT;
	private TextView tvTime;
	private String mFormatRed = "<font color='#ff3300'>%s</font>";
	private String mFormatGreed = "<font color='#66cc66'>%s</font>";
	private String mFormatBlue = "<font color='#3399ff'>%s</font>";
	private String mLabel = "hemoglobin";
	private double hemoglobin;
	private TextView tvValue;
	private float lowHemoglobin = 4.0f;
	private float hightHemoglobin = 7.0f;
	private String defValue;
	// private Calendar calendar;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
	// private String id;
	private TitleBarView mBarView;
	private Button btn_remove;
	private TendencyPointInfo info;
	private FinalDb db;
	private String dateTime;

	public static RecordHemoglobinFragment newInstance(String time) {
		RecordHemoglobinFragment fragment = new RecordHemoglobinFragment();
		fragment.setDateTime(time);
		return fragment;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public RecordHemoglobinFragment() {
	}

	public void setDefValue(String defValue) {
		this.defValue = defValue;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.layout_hemoglobin_fragment;
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
		showProgressDialog(getString(R.string.msg_loading));
		requsetSugarLimit();
		init();
		mBarView.setRightButton(getString(R.string.save), this);
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

		findViewById(R.id.btn_input).setOnClickListener(this);
		tvValue = (TextView) findViewById(R.id.tv_value);
		final View btnTime = findViewById(R.id.btn_settime);
		tvTime = (TextView) findViewById(R.id.tv_settime);
		btn_remove = (Button) findViewById(R.id.btn_remove);
		btnTime.setOnClickListener(this);
		btn_remove.setOnClickListener(this);

		// 默认日期
		if (TextUtils.isEmpty(dateTime)) {
			onChangeTime(Calendar.getInstance());
		} else {
			tvTime.setText(dateTime);
		}
		mBarView.setTitle(getString(R.string.title_record_modify));
		mBarView.setTitle("记录糖化血红蛋白");
		if (defValue != null) {
			if (defValue.replaceAll("%", "").equals("")) {
				return;
			}
			onChangeValues(Float.parseFloat(defValue.replaceAll("%", "")));
		}
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

		if (TextUtils.isEmpty(tvValue.getText().toString())) {
			showToast("请先填写测量值!");
			return;
		}
		if (hemoglobin == 0) {
			showToast(getText(R.string.hemoglobin_toast_msg).toString());
			return;
		}
		JSONArray array = new JSONArray();
		String time = tvTime.getText().toString();

		JSONObject obj = null;
		obj = new JSONObject();
		obj.put("time", time);
		obj.put("code", "hemoglobin");
		obj.put("value", hemoglobin + "");
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

		switch (what) {
		case 1:
			try {
				ComveePacket packet = ComveePacket.fromJsonString(b);
				if (packet.getResultCode() == 0) {
					showToast(packet.getResultMsg());
					// clearInputData(vPager.getCurrentItem());
					ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.INDEX));
					ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TENDENCY_POINT_LIST));

					int pageIndex = mLabel.equals("hemoglobin") ? 3 : 1;
					toFragment(RecordMainFragment.newInstance(true, 3, RecordMainFragment.BACK_RECORD_CHOOSE_FRG), false, true);
//					FragmentMrg.toBack(getActivity(),new Bundle());
				} else {
					ComveeHttpErrorControl.parseError(getActivity(), packet);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;
		case 4://保存
			cancelProgressDialog();
			ComveePacket packet;
			try {
				packet = ComveePacket.fromJsonString(b);

				if (packet.getResultCode() == 0) {
					showToast(packet.getResultMsg());
//					ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.INDEX));
//					ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TENDENCY_POINT_LIST));					FragmentMrg.toBack(getActivity());
					FragmentMrg.toBack(getActivity(),new Bundle());
//					toFragment(RecordMainFragment.newInstance(true, 3, RecordMainFragment.BACK_RECORD_CHOOSE_FRG), true, true);
				} else {
					ComveeHttpErrorControl.parseError(getActivity(), packet);
				}

			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;
		case 5:
			try {
				parseData(b, fromCache);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

	}

	private void parseData(byte[] b, boolean fromCatch) throws Exception {
		ComveePacket packet = ComveePacket.fromJsonString(b);
		if (packet.getResultCode() == 0) {
			JSONObject body = packet.optJSONObject("body");
			float hi = (float) body.optDouble("highHemoglobin");
			float low = (float) body.optDouble("lowHemoglobin");
			if (hi != 0 && low != 0) {
				if (hi != hightHemoglobin || low != lowHemoglobin) {
					hightHemoglobin = hi;
					lowHemoglobin = low;
					((TextView) findViewById(R.id.tv_error)).setText("控制目标:(" + lowHemoglobin + "-" + hightHemoglobin + ")");
				}
			}
		} else {
			ComveeHttpErrorControl.parseError(getActivity(), packet);
			return;
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

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
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
		case R.id.btn_input:
			showSetSingleValueDialog();
			break;

		case R.id.btn_remove:
			CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
			builder.setMessage("是否确定要删除当前记录？");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					if (info != null) {
						requestRemoveValue();
					}
				}
			});
			builder.create().show();
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

	private void onChangeTime(Calendar cal) {
		if (cal == null) {
			cal = Calendar.getInstance();
		}
		if (Calendar.getInstance().getTimeInMillis() < cal.getTimeInMillis()) {
			Toast.makeText(getContext(), getContext().getResources().getString(R.string.txt_date_choose_limit), Toast.LENGTH_SHORT).show();
			return;
		}
		String str = TimeUtil.fomateTime(cal.getTimeInMillis(), "yyyy-MM-dd");
		tvTime.setText(str);

	}

	private void onChangeValues(float value) {
		hemoglobin = value;
		TextView tv = (TextView) findViewById(R.id.tv_sign);
		if (hemoglobin > hightHemoglobin) {
			tv.setVisibility(View.VISIBLE);
			tv.setText(Html.fromHtml(String.format(mFormatRed, "↑")));
			setInputValue(2, String.format("%.1f", hemoglobin));
		} else if (hemoglobin <= hightHemoglobin && hemoglobin >= lowHemoglobin) {
			tv.setVisibility(View.GONE);
			setInputValue(1, String.format("%.1f", hemoglobin));
		} else {
			tv.setVisibility(View.VISIBLE);
			tv.setText(Html.fromHtml(String.format(mFormatBlue, "↓")));
			setInputValue(0, String.format("%.1f", hemoglobin));
		}
	}

	private void requsetSugarLimit() {

		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.RECORD_SUGAR_SET);
		http.setOnHttpListener(5, this);
		http.startAsynchronous();
	}

	private void setInputValue(int type, String value) {
		switch (type) {
		case 0:
			tvValue.setText(Html.fromHtml(String.format(mFormatBlue, value + "%")));
			break;
		case 1:
			tvValue.setText(Html.fromHtml(String.format(mFormatGreed, value + "%")));
			break;
		case 2:
			tvValue.setText(Html.fromHtml(String.format(mFormatRed, value + "%")));
			break;
		default:
			break;
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
	}

	/**
	 * 一个值 多列的选择空间
	 */
	public void showSetSingleValueDialog() {
		float defValue = 0;
		final String dValue = tvValue.getText().toString().replace("%", "");
		if (!TextUtils.isEmpty(dValue)) {
			defValue = Float.valueOf(dValue);
		} else {
			defValue = 4.0f;
		}
		defValue = Math.min(29.9f, defValue);
		CustomFloatNumPickDialog builder = new CustomFloatNumPickDialog();
		builder.setDefult(defValue);
		builder.setFloat(true);
		builder.setUnit("    %");
		builder.setTitle(getString(R.string.hemoglobin_sugar_value));
		builder.setLimitNum(0, 29);
		builder.addOnNumChangeListener(this);
		builder.show(getActivity().getSupportFragmentManager(), "dialog");
	}

	@Override
	public void onChange(DialogFragment dialog, float num) {
		onChangeValues(num);
	}

	private void requestRemoveValue() {
		showProgressDialog(getString(R.string.msg_loading));
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.RECORD_REMOVE_VALUE);
		http.setPostValueForKey("paramLogId", info.getId());
		http.setOnHttpListener(4, this);
		http.startAsynchronous();
	}

	private void getLocRecord() {
		String sql = null;
		String code = "code='hemoglobin'";
		String day = String.format("date(time)=date('%s')", tvTime.getText().toString());
		sql = String.format("(%s) and %s order by date(time) desc,insertDt desc", code, day);
		List<TendencyPointInfo> infos = db.findAllByWhere(TendencyPointInfo.class, sql);
		if (infos.size() > 0) {
			info = infos.get(0);
			hemoglobin = info.getValue();
			btn_remove.setVisibility(View.VISIBLE);
			onChangeValues(info.getValue());
		} else {
			hemoglobin = 0;
			info = null;
			tvValue.setText("");
			btn_remove.setVisibility(View.GONE);
			findViewById(R.id.tv_sign).setVisibility(View.GONE);
		}
	}
}
