package com.comvee.tnb.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.ExceptRecords;
import com.comvee.tnb.model.ExceptionInfo;
import com.comvee.tnb.widget.TitleBarView;

public class ExceptionFragment extends BaseFragment implements OnHttpListener {
	private String newsId;// 异常id
	private TextView tvlabel, tvdocName, tvdocValue;
	private LinearLayout root;
	private List<ExceptionInfo> allinfo;// 所有异常数据 未分类
	private List<ExceptRecords> records;// 每天的异常数据
	private int type;// 异常类型
	private TitleBarView mBarView;

	@Override
	public int getViewLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_exception;
	}

	public ExceptionFragment(String id, int type) {
		newsId = id;
		this.type = type;
	}

	public static ExceptionFragment newInstance(String id, int type) {
		return new ExceptionFragment(id, type);

	}

	public ExceptionFragment() {

	}

	@Override
	public void onLaunch(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onLaunch();
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setTitle(getString(R.string.title_remind_error));
		mBarView.setLeftDefault(this);
		initView();
		requestExcept();
	}

	private void initView() {
		tvlabel = (TextView) findViewById(R.id.exc_lable);
		tvdocName = (TextView) findViewById(R.id.exc_doc_name);
		tvdocValue = (TextView) findViewById(R.id.exc_doc_value);
		root = (LinearLayout) findViewById(R.id.exc_listview);

	}

	private void requestExcept() {
		showProgressDialog(getString(R.string.msg_loading));
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.GET_GRAPHS_FOR_EXCETPTION);
		String str = null;
		if (type == 10) {
			str = createSugarString();
		} else if (type == 12) {
			str = creatPressureString();
		} else {
			str = createBMIString();
		}

		http.setPostValueForKey("paramKey", str);
		http.setPostValueForKey("newsId", newsId);
		http.setOnHttpListener(1, this);
		http.startAsynchronous();
	}

	private String createBMIString() {
		JSONArray array = new JSONArray();
		try {
			JSONObject obj = new JSONObject();
			obj.put("code", "bmi");
			array.put(obj);
			obj = new JSONObject();
			obj.put("code", "height");
			array.put(obj);
			obj = new JSONObject();
			obj.put("code", "weight");
			array.put(obj);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return array.toString();
	}

	private String creatPressureString() {
		JSONArray array = new JSONArray();

		try {
			JSONObject obj = new JSONObject();
			obj.put("code", "bloodpressurediastolic");
			array.put(obj);
			obj = new JSONObject();
			obj.put("code", "bloodpressuresystolic");
			array.put(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return array.toString();

	}

	public static String createSugarString() {
		JSONArray array = new JSONArray();
		try {
			JSONObject obj = new JSONObject();
			obj.put("code", "beforedawn");
			array.put(obj);
			obj = new JSONObject();
			obj.put("code", "beforeBreakfast");
			array.put(obj);
			obj = new JSONObject();
			obj.put("code", "afterDinner");
			array.put(obj);
			obj = new JSONObject();
			obj.put("code", "afterLunch");
			array.put(obj);
			obj = new JSONObject();
			obj.put("code", "beforeLunch");
			array.put(obj);
			obj = new JSONObject();
			obj.put("code", "beforeDinner");
			array.put(obj);
			obj = new JSONObject();
			obj.put("code", "afterBreakfast");
			array.put(obj);
			obj = new JSONObject();
			obj.put("code", "beforeSleep");
			array.put(obj);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return array.toString();
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		// TODO Auto-generated method stub
		cancelProgressDialog();
		switch (what) {
		case 1:
			parseExceptionInfo(b);
			getDays(allinfo);

			createChileView(records);
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

	private void parseExceptionInfo(byte[] b) {

		try {
			final ComveePacket packet = ComveePacket.fromJsonString(b);
			if (packet.getResultCode() == 0) {
				allinfo = new ArrayList<ExceptionInfo>();
				JSONObject body = packet.optJSONObject("body");
				JSONArray advices = body.optJSONArray("advice");
				for (int i = 0; i < advices.length(); i++) {
					JSONObject advice = advices.optJSONObject(i);
					String value = advice.optString("adviceContent");
					tvdocValue.setText(value);
				}

				JSONArray listParam = body.optJSONArray("listParam");
				for (int i = 0; i < listParam.length(); i++) {
					JSONObject object = listParam.optJSONObject(i);
					String code = object.optString("code");
					JSONArray lists = object.optJSONArray("list");
					for (int j = 0; j < lists.length(); j++) {
						JSONObject info = lists.optJSONObject(j);
						ExceptionInfo exceptionInfo = new ExceptionInfo();
						String time = info.optString("time");
						String paramLogId = info.optString("paramLogId");
						String value = info.optString("value");
						String insertDt = info.optString("insertDt");
						int bloodGlucoseStatus = info.optInt("bloodGlucoseStatus");
						String type = info.optString("type");

						exceptionInfo.setBloodGlucoseStatus(bloodGlucoseStatus);
						exceptionInfo.setInsertDt(insertDt);
						exceptionInfo.setParamLogId(paramLogId);
						exceptionInfo.setTime(time);
						exceptionInfo.setType(type);
						exceptionInfo.setValue(value);
						exceptionInfo.setCode(code);
						allinfo.add(exceptionInfo);

					}

				}
				JSONObject newsModel = body.optJSONObject("newsModel");
				String time = newsModel.optString("insertDt");
				String contentText = newsModel.optString("contentText");
				String doctorName = newsModel.optString("doctorName");

				tvdocName.setText(doctorName + "医生建议");
				tvlabel.setText("你在" + time + contentText);
			} else {
				ComveeHttpErrorControl.parseError(getActivity(), packet);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 获取异常数据的天数
	 */
	private void getDays(List<ExceptionInfo> list) {
		if (list == null || list.size() == 0) {
			// showToast(getResources().getString(R.string.error));
			FragmentMrg.toBack(getActivity());
			return;
		}
		records = new ArrayList<ExceptRecords>();
		for (int i = 0; i < list.size(); i++) {
			ExceptionInfo info = list.get(i);
			String str[] = info.getTime().split(" ");
			String time[] = str[0].split("-");
			ExceptRecords record = new ExceptRecords(time[1] + "-" + time[2]);
			if (!records.contains(record)) {
				records.add(record);
			}
		}
		Collections.sort(records, new MyComparator());
	}

	/*
	 * 创建每天的数据表格 并添加
	 */

	private void createChileView(List<ExceptRecords> list) {
		if (list == null || list.size() == 0) {
			// showToast(getResources().getString(R.string.error));
			// FragmentMrg.toBack(this);
			return;
		}
		// 添加表格头标题
		View headview = View.inflate(getContext(), R.layout.tendency_table_header, null);
		headview.findViewById(R.id.lin_head).setVisibility(View.GONE);
		TextView tvLabel = (TextView) headview.findViewById(R.id.tv_label);
		if (type == 10) {
			tvLabel.setText(getString(R.string.item_record_table_blood_glucose));
			headview.findViewById(R.id.layout_xuetang).setVisibility(View.VISIBLE);
		} else if (type == 12) {

			tvLabel.setText(getString(R.string.exception_blood_title));
			headview.findViewById(R.id.layout_xueya).setVisibility(View.VISIBLE);
		} else {
			tvLabel.setText(getString(R.string.exception_bmi_title));
			headview.findViewById(R.id.layout_bmi).setVisibility(View.VISIBLE);
		}
		root.addView(headview, 0);
		// 生成每天的数据视图
		View view = null;
		for (int i = 0; i < list.size(); i++) {
			if (type == 10) {
				view = View.inflate(getApplicationContext(), R.layout.item_tendency_table, null);
				TextView times = (TextView) view.findViewById(R.id.tv_date);
				times.setText(list.get(i).time);
			} else if (type == 12) {
				view = View.inflate(getApplicationContext(), R.layout.item_tendency_table_xueya, null);
				TextView times = (TextView) view.findViewById(R.id.tv_date);
				times.setText(list.get(i).time);
			} else {
				view = View.inflate(getContext(), R.layout.item_tendency_table_bmi, null);
				TextView times = (TextView) view.findViewById(R.id.tv_date);
				times.setText(list.get(i).time);
			}
			for (int j = 0; j < allinfo.size(); j++) {

				ExceptionInfo info = allinfo.get(j);
				String str[] = info.getTime().split(" ");
				String time[] = str[0].split("-");
				if (list.get(i).time.equals(time[1] + "-" + time[2])) {
					setSuggerText(view, info);
				}
			}
			root.addView(view);
		}
	}

	/*
	 * 设置表格中的数值
	 */
	private void setSuggerText(View view, ExceptionInfo exceptionInfo) {
		TextView textView = null;

		String code = exceptionInfo.getCode();
		if (code.equals("beforedawn"))
			textView = (TextView) view.findViewById(R.id.tv_value);
		else if (code.equals("beforeBreakfast"))
			textView = (TextView) view.findViewById(R.id.tv_value_0);
		else if (code.equals("afterBreakfast"))
			textView = (TextView) view.findViewById(R.id.tv_value_1);
		else if (code.equals("beforeLunch"))
			textView = (TextView) view.findViewById(R.id.tv_value_2);
		else if (code.equals("afterLunch"))
			textView = (TextView) view.findViewById(R.id.tv_value_3);
		else if (code.equals("beforeDinner"))
			textView = (TextView) view.findViewById(R.id.tv_value_4);
		else if (code.equals("afterDinner"))
			textView = (TextView) view.findViewById(R.id.tv_value_5);
		else if (code.equals("beforeSleep"))
			textView = (TextView) view.findViewById(R.id.tv_value_6);
		else if (code.equals("bloodpressuresystolic"))
			textView = (TextView) view.findViewById(R.id.tv_ssy);
		else if (code.equals("bloodpressurediastolic"))
			textView = (TextView) view.findViewById(R.id.tv_szy);
		else if (code.equals("height"))
			textView = (TextView) view.findViewById(R.id.tv_value0);
		else if (code.equals("weight"))
			textView = (TextView) view.findViewById(R.id.tv_value1);
		else if (code.equals("bmi")) {
			textView = (TextView) view.findViewById(R.id.tv_value2);
			exceptionInfo.setValue(String.format("%.1f", Float.valueOf(exceptionInfo.getValue())));
		}
		textView.setText(exceptionInfo.getValue());
		switch (exceptionInfo.getBloodGlucoseStatus()) {
		case 1:
		case 2:
			textView.setTextColor(getResources().getColor(R.color.blue));
			break;
		case 3:
			textView.setTextColor(getResources().getColor(R.color.green));
			break;
		case 4:
		case 5:
			textView.setTextColor(getResources().getColor(R.color.red));
			break;

		default:
			break;
		}

	}

	@SuppressWarnings("rawtypes")
	class MyComparator implements Comparator {
		@Override
		public int compare(Object obj1, Object obj2) {
			ExceptRecords k1 = (ExceptRecords) obj1;
			String time1 = k1.time.split("-")[0] + k1.time.split("-")[1];
			ExceptRecords k2 = (ExceptRecords) obj2;
			String time2 = k2.time.split("-")[0] + k2.time.split("-")[1];
			if (Integer.parseInt(time1) > Integer.parseInt(time2))
				return 1;
			else if (Integer.parseInt(time1) < Integer.parseInt(time2))
				return -1;
			else
				return 0;
		}
	}
}
