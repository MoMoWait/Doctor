package com.comvee.tnb.ui.log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.comvee.FinalDb;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.RecordInfo;
import com.comvee.tnb.ui.task.CalendarFragment;
import com.comvee.tnb.ui.task.CalendarFragment.OnChoiceCalendarListener;
import com.comvee.tnb.view.SingleInputItemWindow;
import com.comvee.tnb.widget.Panel;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;
import com.comvee.util.TimeUtil;

/**
 * 健康日志 日历页面
 * 
 * @author friendlove
 * 
 */
public class RecordCalendarFragment extends BaseFragment implements OnClickListener, OnItemClickListener, OnHttpListener, OnChoiceCalendarListener {
	private Calendar mCalendar;
	private Panel mPanel;
	private CalendarFragment fragCalendar;

	private HashMap<String, String> mSubmitMap;
	private HashMap<String, String> mTempMap;
	FinalDb db;
	private int[] res;
	private String[] codes;
	private String[][] values;
	private boolean isChange;
	private TitleBarView mBarView;
	// dinner　正餐食量
	// vagetable　蔬菜
	// fat　脂肪
	// salt　盐
	// water　水
	// sport　运动
	// smoke　吸烟
	// drink　喝酒
	// pill　用药
	// sugarMonitor　血糖监测
	// mood　情绪
	// assistFood;// 饮食配餐
	// 非糖尿病
	private String[] codes1 = { "assistFood", "dinner", "vagetable", "fat", "salt", "water", "sport", "smoke", "drink", "mood" };
	// 糖尿病
	private String[] codes2 = { "assistFood", "dinner", "vagetable", "fat", "salt", "sport", "smoke", "drink", "pill", "sugarMonitor" };

	// 非糖尿病
	private int[] res1 = { R.id.btn_record_11, R.id.btn_record_12, R.id.btn_record_13, R.id.btn_record_14, R.id.btn_record_15, R.id.btn_record_16,
			R.id.btn_record_17, R.id.btn_record_18, R.id.btn_record_19, R.id.btn_record_20 };

	// 糖尿病
	private int[] res2 = { R.id.btn_record_0, R.id.btn_record_1, R.id.btn_record_2, R.id.btn_record_3, R.id.btn_record_4, R.id.btn_record_5,
			R.id.btn_record_6, R.id.btn_record_7, R.id.btn_record_8, R.id.btn_record_9 };

	// 非糖尿病
	private String[][] values1 = { { "严格按营养师说的", "部分按营养师说的", "随心所欲" },// 饮食配餐
			{ "7分饱", "8-9分饱", "饥饿或暴饮暴食" },// 正餐食量
			{ "红黄蓝白黑都有", "2-4种颜色食物", "没有颜色搭配" },// 蔬菜
			{ "少吃肥肉和猪油", "吃了一些肥肉和猪油", "吃大量肥肉和猪油" },// 脂肪
			{ "摄入量＜6g（一个啤酒瓶盖的量）", "摄入量6g-10g", "10g以上" },// 盐
			{ "不渴也喝，定时定量", "渴了才喝", "很少喝水" },// 饮水
			{ "饭后1-2小时运动30分钟以上", "运动不定时，且少于30分钟", "没有运动" },// 运动
			{ "没有抽烟", "抽烟或被动抽烟" },// 抽烟
			{ "没有喝酒", "喝了啤酒350ml或红酒150ml或低度白酒45ml" },// 喝酒
			{ "高兴", "一般", "压抑、焦虑" },// 情绪
	};

	// 糖尿病
	private String[][] values2 = { { "严格按营养师说的", "部分按营养师说的", "随心所欲" },// 饮食配餐
			{ "7分饱", "8-9分饱", "饥饿或暴饮暴食" },// 正餐食量
			{ "红黄蓝白黑都有", "2-4种颜色食物", "没有颜色搭配" },// 蔬菜
			{ "少吃肥肉和猪油", "吃了一些肥肉和猪油", "吃大量肥肉和猪油" },// 脂肪
			{ "摄入量＜6g（一个啤酒瓶盖的量）", "摄入量6g-10g", "10g以上" },// 盐
			{ "饭后1-2小时运动30分钟以上", "运动不定时，且少于30分钟", "没有运动" },// 运动
			{ "没有抽烟", "抽烟或被动抽烟" },// 抽烟
			{ "没有喝酒", "喝了啤酒350ml或红酒150ml或低度白酒45ml" },// 喝酒
			{ "严格按照医嘱用药", "用医生开的药，但不准时服用", "自己给自己开药" },// 用药
			{ "达到医生制定的标准", "没有达到标准", "出现低血糖或高血糖引起的相关症状" },// 血糖监测
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			showDateData(Calendar.getInstance());
			fragCalendar.toCalendar(Calendar.getInstance());
		};
	};

	public static RecordCalendarFragment newInstance() {
		return new RecordCalendarFragment();
	}

	public RecordCalendarFragment() {
	}

	@Override
	public void onDestroyView() {
		mTempMap = mSubmitMap;
		requestSaveData();
		super.onDestroyView();
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_record;
	}

	@Override
	public void onStart() {
		super.onStart();

		// requestAllData();
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		db = FinalDb.create(getApplicationContext(), ConfigParams.DB_NAME);
		mBarView.setRightButton("分析", this);
		init();
		mBarView.setTitle(getString(R.string.title_log));
		// 延迟请求数据，避免卡顿 用加载动画做初始显示界面
		showProgressDialog(getString(R.string.msg_loading));
		handler.sendEmptyMessageDelayed(1, 300);
	}

	private void init() {
		if (UserMrg.DEFAULT_MEMBER != null && UserMrg.DEFAULT_MEMBER.callreason == 1) {
			values = values2;
			res = res2;
			codes = codes2;

			findViewById(R.id.layout_tnb).setVisibility(View.VISIBLE);
			findViewById(R.id.layout_notnb).setVisibility(View.GONE);
		} else {
			values = values1;
			res = res1;
			codes = codes1;

			findViewById(R.id.layout_tnb).setVisibility(View.GONE);
			findViewById(R.id.layout_notnb).setVisibility(View.VISIBLE);
		}
		mCalendar = Calendar.getInstance();
		mPanel = (Panel) findViewById(R.id.slidingdrawer);
		mPanel.setOpen(true, true);
		initCalendar();

		for (int i = 0; i < res.length; i++) {
			findViewById(res[i]).setOnClickListener(this);
		}

	}

	private void initCalendar() {
		fragCalendar = CalendarFragment.newInstance(1);
		fragCalendar.setNeedInitCalendar(false);
		fragCalendar.setOnChoiceCalendarListener(this);
		FragmentTransaction tran = getFragmentManager().beginTransaction();
		tran.replace(R.id.layout_calendar_content, fragCalendar);
		tran.commit();
	}

	@Override
	public void onItemChoice(CalendarFragment frag, int position, Calendar cal) {
		requestSaveData();
		mCalendar = cal;
		mTempMap = mSubmitMap;
		showDateData(cal);
		isChange = false;
	}

	@Override
	public void onMonthChange(CalendarFragment frag, Calendar cal) {
		requestSaveData();
		mCalendar = cal;
		showDateData(cal);
		isChange = false;
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		cancelProgressDialog();
		switch (what) {
		case 1:
			parseAllData(b, fromCache);
			break;
		case 2:
			parseSaveData(b);
			break;
		default:
			break;
		}

	}

	@Override
	public void onFialed(int what, int errorCode) {
		cancelProgressDialog();
		try {
			showToast(R.string.error);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	private HashMap<String, String> getMapByObj(RecordInfo info) {
		HashMap<String, String> map = new HashMap<String, String>();
		if (!TextUtils.isEmpty(info.assistFood))
			map.put("assistFood", info.assistFood);
		if (!TextUtils.isEmpty(info.dinner))
			map.put("dinner", info.dinner);
		if (!TextUtils.isEmpty(info.drink))
			map.put("drink", info.drink);
		if (!TextUtils.isEmpty(info.fat))
			map.put("fat", info.fat);
		if (!TextUtils.isEmpty(info.insertDt))
			map.put("insertDt", info.insertDt);
		if (!TextUtils.isEmpty(info.mood))
			map.put("mood", info.mood);
		if (!TextUtils.isEmpty(info.pill))
			map.put("pill", info.pill);
		if (!TextUtils.isEmpty(info.salt))
			map.put("salt", info.salt);
		if (!TextUtils.isEmpty(info.smoke))
			map.put("smoke", info.smoke);
		if (!TextUtils.isEmpty(info.sport))
			map.put("sport", info.sport);
		if (!TextUtils.isEmpty(info.sugarMonitor))
			map.put("sugarMonitor", info.sugarMonitor);
		if (!TextUtils.isEmpty(info.water))
			map.put("water", info.water);
		if (!TextUtils.isEmpty(info.vagetable))
			map.put("vagetable", info.vagetable);
		return map;
	}

	private void showDateData(Calendar cal) {

		try {
			String time = TimeUtil.fomateTime(cal.getTimeInMillis(), ConfigParams.DATE_FORMAT);
			List<RecordInfo> infos = db.findAllByWhere(RecordInfo.class, String.format("insertDt='%s'", time));
			if (infos != null && !infos.isEmpty()) {

				RecordInfo info = infos.get(0);
				mSubmitMap = getMapByObj(info);

				for (int i = 0; i < codes.length; i++) {
					TextView tv = (TextView) findViewById(res[i]).findViewById(R.id.tv_value);

					String value = mSubmitMap.get(codes[i]);
					if (TextUtils.isEmpty(value)) {
						tv.setText("...");
					} else {
						int index = Integer.valueOf(value);
						int type = values[i].length;
						if (type == 3) {
							tv.setText(values[i][index - 1]);

							int which = index - 1;
							if (which == 0) {
								tv.setTextColor(getResources().getColor(R.color.theme_color_green));
							} else if (which == 1) {
								tv.setTextColor(getResources().getColor(R.color.btn_orange));
							} else {
								tv.setTextColor(getResources().getColor(R.color.red));
							}

						} else if (type == 2) {
							if (index == 1) {
								tv.setTextColor(getResources().getColor(R.color.theme_color_green));
								tv.setText(values[i][0]);
							} else if (index == 3) {
								tv.setTextColor(getResources().getColor(R.color.theme_color_green));
								tv.setText(values[i][1]);
							}
						}
					}
				}

			} else {
				mSubmitMap = new HashMap<String, String>();
				for (int i = 0; i < codes.length; i++) {
					TextView tv = (TextView) findViewById(res[i]).findViewById(R.id.tv_value);
					tv.setText("...");
					tv.setTextColor(getResources().getColor(R.color.theme_color_green));
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private int getCodeIndex(String code) {
		for (int i = 0; i < codes.length; i++) {
			if (code.equals(codes[i])) {
				return i;
			}
		}
		return -1;
	}

	private int getViewIndex(View v) {
		for (int i = 0; i < res.length; i++) {
			if (res[i] == v.getId()) {
				return i;
			}
		}
		return -1;
	}

	private void parseSaveData(byte[] b) {
		try {
			ComveePacket packet = ComveePacket.fromJsonString(b);
			if (packet.getResultCode() == 0) {
				showToast("保存成功");
				isChange = false;

				RecordInfo info = new RecordInfo();
				info.setAssistFood(mTempMap.get("assistFood"));
				info.setDinner(mTempMap.get("dinner"));
				info.setDrink(mTempMap.get("drink"));
				info.setFat(mTempMap.get("fat"));
				info.setInsertDt(mTempMap.get("insertDt"));
				info.setMood(mTempMap.get("mood"));
				info.setPill(mTempMap.get("pill"));
				info.setSalt(mTempMap.get("salt"));
				info.setSmoke(mTempMap.get("smoke"));
				info.setSport(mTempMap.get("sport"));
				info.setSugarMonitor(mTempMap.get("sugarMonitor"));
				info.setVagetable(mTempMap.get("vagetable"));
				info.setWater(mTempMap.get("water"));

				List<RecordInfo> infos = db.findAllByWhere(RecordInfo.class, String.format("insertDt='%s'", mTempMap.get("insertDt")));
				if (infos == null || infos.isEmpty()) {
					if (isVisible()) {
						requestAllData();
					}
				} else {
					db.update(info);
				}
				ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.INDEX));
				getApplicationContext().sendBroadcast(new Intent(ConfigParams.ACTION_INDEX_UPDATE));
			} else {
				ComveeHttpErrorControl.parseError(getActivity(), packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseAllData(byte[] b, boolean fromCache) {
		try {
			final ComveePacket packet = ComveePacket.fromJsonString(b);
			if (packet.getResultCode() == 0) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							ArrayList<RecordInfo> list = new ArrayList<RecordInfo>();
							JSONArray array = packet.getJSONObject("body").getJSONArray("rows");
							int len = array.length();
							RecordInfo info = null;
							for (int i = 0; i < len; i++) {
								JSONObject obj = array.getJSONObject(i);
								info = new RecordInfo();
								info.assistFood = obj.optString("assistFood");
								info.dinner = obj.optString("dinner");
								info.drink = obj.optString("drink");
								info.fat = obj.optString("fat");
								info.mood = obj.optString("mood");
								info.pill = obj.optString("pill");
								info.salt = obj.optString("salt");
								info.sport = obj.optString("sport");
								info.smoke = obj.optString("smoke");
								info.sugarMonitor = obj.optString("sugarMonitor");
								info.vagetable = obj.optString("vagetable");
								info.water = obj.optString("water");
								info.insertDt = obj.optString("insertDt");
								list.add(info);
							}
							db.deleteByWhere(RecordInfo.class, "");
							db.saveList(list);
							mHandler.sendEmptyMessage(1);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();

			} else {
				ComveeHttpErrorControl.parseError(getActivity(), packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			requestAllData();
		};
	};

	private void requestAllData() {
		showProgressDialog(getString(R.string.msg_loading));
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.RECORD_DATA);
		http.setCallBackAsyn(true);
		http.setNeedGetCache(true, UserMrg.getCacheKey(ConfigUrlMrg.RECORD_DATA));
		http.setOnHttpListener(1, this);
		http.startAsynchronous();

	}

	private void requestSaveData() {
		if (!isChange) {
			return;
		}

		JSONArray array = new JSONArray();
		try {
			JSONObject obj = new JSONObject();
			for (String str : mSubmitMap.keySet()) {
				obj.put(str, mSubmitMap.get(str));
			}
			obj.put("insertDt", TimeUtil.fomateTime(mCalendar.getTimeInMillis(), ConfigParams.DATE_FORMAT));
			array.put(obj);
		} catch (Exception E) {
			E.printStackTrace();
		}

		// showProDialog(getString(R.string.msg_loading));

		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.RECORD_ADD);
		http.setPostValueForKey("paramStr", array.toString());
		http.setOnHttpListener(2, this);
		http.startAsynchronous();
	}

	@Override
	public void onClick(final View v) {

		if (v.getId() == TitleBarView.ID_RIGHT_BUTTON) {
			toFragment(RecordChooseFragment.newInstance(true), true, true);
		} else {

			if (TimeUtil.isBefore(System.currentTimeMillis(), mCalendar.getTimeInMillis())) {
				showToast("只能填写今天过去的健康日志");
				return;
			}

			final int index = getViewIndex(v);
			final String[] items = values[index];
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						if (mSubmitMap == null) {
							mSubmitMap = new HashMap<String, String>();
						}
						TextView tv = (TextView) v.findViewById(R.id.tv_value);
						tv.setText(items[which]);
						if (items.length == 3) {
							mSubmitMap.put(codes[index], (which + 1) + "");

							if (which == 0) {
								tv.setTextColor(getResources().getColor(R.color.theme_color_green));
							} else if (which == 1) {
								tv.setTextColor(getResources().getColor(R.color.btn_orange));
							} else {
								tv.setTextColor(getResources().getColor(R.color.red));
							}
						} else {
							if (which == 0) {
								tv.setTextColor(getResources().getColor(R.color.theme_color_green));
								mSubmitMap.put(codes[index], "1");
							} else {
								tv.setTextColor(getResources().getColor(R.color.red));
								mSubmitMap.put(codes[index], "3");
							}
						}
						isChange = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			};
			SingleInputItemWindow dialog = new SingleInputItemWindow(getContext(), items, "选择方式", -1, 0, 0);
			dialog.setOnItemClick(listener);
			dialog.setOutTouchCancel(true);
			dialog.showAtLocation(mRoot, Gravity.CENTER, 0, 0);
		}

	}

	@Override
	public boolean onBackPress() {
		return false;
	}

	@Override
	public boolean isHashTask(CalendarFragment frag, Calendar cal) {
		// TODO Auto-generated method stub
		return false;
	}

}