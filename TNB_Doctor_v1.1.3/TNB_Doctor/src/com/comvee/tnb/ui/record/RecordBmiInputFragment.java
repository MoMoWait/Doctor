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
 * 录入bmi数据
 * 
 * @author friendlove
 * 
 */
@SuppressLint("ValidFragment")
public class RecordBmiInputFragment extends BaseFragment implements OnHttpListener, OnClickListener, OnChangeMultiNumListener, OnCancelListener {

	private final String mInputUrl = ConfigUrlMrg.TENDENCY_INPUT;
	private TextView tvTime;
	private String mFormatRed = "<font color='#ff3300'>%s</font>";
	private String mFormatGreed = "<font color='#66cc66'>%s</font>";
	private String mFormatBlue = "<font color='#3399ff'>%s</font>";
	private String mLabel = "BMI";
	private int mHeight, mWeight;
	// private double hemoglobin;
	private TextView tvValue;
	private TitleBarView mBarView;
	private String dataTime;
	private FinalDb db;
	private Button btn_remove;
	private TendencyPointInfo info;

	public RecordBmiInputFragment() {
	}

	public static RecordBmiInputFragment newInstance(String dataTime) {
		RecordBmiInputFragment fragment = new RecordBmiInputFragment();
		fragment.setDataTime(dataTime);
		return fragment;
	}

	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.record_bmi_input_fragment;
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
			public void callBack(int what,int status, Object obj) {
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
		if (TextUtils.isEmpty(dataTime)) {
			onChangeTime(Calendar.getInstance());
		} else {
			tvTime.setText(dataTime);
		}
		mBarView.setTitle(getString(R.string.title_record_bmi));
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
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

		JSONArray array = new JSONArray();
		String time = tvTime.getText().toString();

		JSONObject obj = null;
		obj = new JSONObject();
		obj.put("time", time);
		obj.put("code", "weight");
		obj.put("value", mWeight + "");
		array.put(obj);

		obj = new JSONObject();
		obj.put("time", time);
		obj.put("code", "height");
		obj.put("value", mHeight);
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
		case 4://删除
			try {
				ComveePacket packet = ComveePacket.fromJsonString(b);

				if (packet.getResultCode() == 0) {
					FragmentMrg.toBack(getActivity(),new Bundle());
//					toFragment(RecordMainFragment.newInstance(true, 2, RecordMainFragment.BACK_RECORD_CHOOSE_FRG), true, true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 1://保存
			try {
				ComveePacket packet = ComveePacket.fromJsonString(b);
				if (packet.getResultCode() == 0) {
					showToast(packet.getResultMsg());
					// clearInputData(vPager.getCurrentItem());
					ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.INDEX));
					ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TENDENCY_POINT_LIST));

					int pageIndex = mLabel.equals("BMI") ? 2 : 1;
//					FragmentMrg.toBack(getActivity(),new Bundle());
					toFragment(RecordMainFragment.newInstance(true, 2, RecordMainFragment.BACK_RECORD_CHOOSE_FRG), true, true);
				} else {
					ComveeHttpErrorControl.parseError(getActivity(), packet);
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
			showSetMultiValueDialog();
			break;
		case R.id.btn_remove:
			if (info != null) {
				requestRemoveValue(info.id);
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

		int[] max = { 280, 200 };
		int[] min = { 70, 20 };
		// 如果已经填写这个值 就有 用 这个值 如果没有 用默认值
		float[] def = { mHeight != 0 ? mHeight : 170, mWeight != 0 ? mWeight : 60 };
		String[] titles = { "身高", "体重" };
		String[] units = { "cm", "kg" };

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
		mHeight = (int) fs[0];
		mWeight = (int) fs[1];

		float bmi = (mWeight * 10000) / (float) (mHeight * mHeight);
		TextView tv = (TextView) findViewById(R.id.tv_sign);
		if (bmi >= 28) {
			tv.setVisibility(View.VISIBLE);
			tv.setText(Html.fromHtml(String.format(mFormatRed, "↑")));
			setInputValue(2, String.format("%.1f", bmi));
		} else if (bmi < 28 && bmi >= 24) {
			tv.setVisibility(View.VISIBLE);
			tv.setText(Html.fromHtml(String.format(mFormatRed, "↑")));
			setInputValue(2, String.format("%.1f", bmi));
		} else if (bmi <= 23.9 && bmi >= 18.5) {
			tv.setVisibility(View.GONE);
			setInputValue(1, String.format("%.1f", bmi));
		} else {
			tv.setVisibility(View.VISIBLE);
			tv.setText(Html.fromHtml(String.format(mFormatBlue, "↓")));
			setInputValue(0, String.format("%.1f", bmi));
		}
	}

	private void setInputValue(int type, String value) {
		switch (type) {
		case 0:
			tvValue.setText(Html.fromHtml(String.format(mFormatBlue, value)));
			break;
		case 1:
			tvValue.setText(Html.fromHtml(String.format(mFormatGreed, value)));
			break;
		case 2:
			tvValue.setText(Html.fromHtml(String.format(mFormatRed, value)));
			break;
		default:
			break;
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

	private void getLocRecord() {
		String sql = null;
		String code_weight = "code='weight'";
		String code_height = "code='height'";
		String day = String.format("date(time)=date('%s')", tvTime.getText().toString());
		sql = String.format("(%s) and %s order by date(time) desc,insertDt desc", code_weight, day);
		List<TendencyPointInfo> infos_weight = db.findAllByWhere(TendencyPointInfo.class, sql);
		sql = String.format("(%s) and %s order by date(time) desc,insertDt desc", code_height, day);
		List<TendencyPointInfo> infos_height = db.findAllByWhere(TendencyPointInfo.class, sql);
		if (infos_weight.size() > 0 && infos_height.size() > 0) {
			info = infos_height.get(0);
			btn_remove.setVisibility(View.VISIBLE);
			float flo[] = { infos_height.get(0).getValue(), infos_weight.get(0).getValue() };
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
