package com.comvee.tnb.ui.task;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDatePickDialog;
import com.comvee.tnb.dialog.CustomDatePickDialog.OnTimeChangeListener;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.TaskCheckInfo;
import com.comvee.tnb.ui.assess.AssessFragment;
import com.comvee.tnb.widget.PinnedHeaderListView;
import com.comvee.tnb.widget.SectionedBaseAdapter;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tnb.widget.XExpandableListView.IXListViewListener;
import com.comvee.tool.UserMrg;
import com.comvee.util.TimeUtil;
import com.comvee.util.Util;

/**
 * 检测任务
 * 
 * @author friendlove
 * 
 */
public class CheckTaskFragment extends BaseFragment implements IXListViewListener, OnItemClickListener, OnHttpListener, OnClickListener {
	private View layoutNonDefault;
	private PinnedHeaderListView listView;
	private DoctorAdapter mAdapter;
	private int totalRows;// 总共多少条
	private boolean mFirstLuanch = true;
	private boolean isinit = true;
	private TitleBarView mBarView;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				layoutNonDefault.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
				mAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}

		};
	};

	public static CheckTaskFragment newInstance() {
		CheckTaskFragment fragment = new CheckTaskFragment();
		return fragment;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragemnt_doctor_recommend;
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	public CheckTaskFragment() {
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		ConfigParams.setTaskNew(getApplicationContext(), false);

		mBarView.setTitle(getString(R.string.title_check_task));
		if (isinit) {
			init();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	private void init() {
		isinit = false;
		layoutNonDefault = findViewById(R.id.layout_non_default);
		final TextView tvNonMsg = (TextView) findViewById(R.id.tv_non_msg);
		final Button btnNonJump = (Button) findViewById(R.id.btn_non_jump);
		final ImageView imgNonTag = (ImageView) findViewById(R.id.img_non_tag);

		tvNonMsg.setText(Html.fromHtml("结合您的健康评估结果，我们将为您制定<font color='#1a9293'>个性化的健康任务</font>"));
		btnNonJump.setOnClickListener(this);
		imgNonTag.setImageResource(R.drawable.task_no_data);
		btnNonJump.setText("先做一份健康评估");

		listView = (PinnedHeaderListView) findViewById(R.id.lv_doctor_recommend);
		listView.setSelector(R.drawable.server_item_doc_line);
		listView.setDivider(getResources().getDrawable(R.drawable.server_item_doc_line));
		LinearLayout footView = new LinearLayout(getApplicationContext());
		TextView tv = new TextView(getApplicationContext());
		tv.setId(1);
		tv.setOnClickListener(this);
		tv.setText("确定");
		tv.setTextSize(20);
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(Color.WHITE);
		int w = Util.dip2px(getApplicationContext(), 20);
		tv.setPadding(0, w / 2, 0, w / 2);
		footView.addView(tv, -1, -2);
		footView.setPadding(w, w, w, w);
		listView.addFooterView(footView, null, false);
		listView.setOnItemClickListener(new PinnedHeaderListView.OnItemClickListener() {
			@Override
			public void onSectionClick(AdapterView<?> adapterView, View view, int section, long id) {
			}

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id) {
				TaskCheckInfo info = listItems.get(section).get(position);
				showSetTimeDialog(info);
				// RemindDetailFragment frag =
				// RemindDetailFragment.newInstance();
				// frag.setTaskInfo(info);
				// toFragment(frag, true, false);
			}
		});
		mAdapter = new DoctorAdapter();
		listView.setAdapter(mAdapter);

		if (mFirstLuanch) {
			requestTaskList();
			mFirstLuanch = false;
		} else {
			mAdapter.notifyDataSetChanged();
		}

	}

	public void showSetTimeDialog(final TaskCheckInfo info) {
		CustomDatePickDialog builder = new CustomDatePickDialog();
		builder.setDefaultTime(Calendar.getInstance());
		builder.setOnTimeChangeListener(new OnTimeChangeListener() {

			@Override
			public void onChange(DialogFragment dialog, int year, int month, int day) {
				// TODO Auto-generated method stub

				Calendar cal = Calendar.getInstance();
				cal.set(year, month - 1, day);
				if (System.currentTimeMillis() > cal.getTimeInMillis()) {
					showToast(getContext().getResources().getString(R.string.txt_date_choose_limit));
				} else {
					info.time = TimeUtil.fomateTime(cal.getTimeInMillis(), ConfigParams.DATE_FORMAT);
					mAdapter.notifyDataSetChanged();
				}

			}
		});
		Calendar cal = Calendar.getInstance();
		if (!TextUtils.isEmpty(info.time)) {
			try {
				cal.setTimeInMillis(TimeUtil.getUTC(info.time, ConfigParams.DATE_FORMAT));
				builder.setDefaultTime(cal);
			} catch (Exception e) {
				e.printStackTrace();
				builder.setDefaultTime(cal);
			}
		} else {
			builder.setDefaultTime(cal);
		}
		builder.setLimitTime(cal.get(cal.YEAR), cal.get(cal.YEAR) + 10);
		builder.show(getActivity().getSupportFragmentManager(), "dialog");
	}

	public void requestTaskList() {
		showProgressDialog(getString(R.string.msg_loading));
		final String url = ConfigUrlMrg.TASK_CHECK_LOAD;
		ComveeHttp http = new ComveeHttp(getApplicationContext(), url);
		http.setOnHttpListener(1, this);
		http.setCallBackAsyn(true);
		http.startAsynchronous();
	}

	public void requestModify() {
		showProgressDialog(getString(R.string.msg_loading));
		final String url = ConfigUrlMrg.TASK_CHECK_MODIFY;
		JSONArray array = new JSONArray();
		for (ArrayList<TaskCheckInfo> list : listItems) {
			for (TaskCheckInfo info : list) {
				if (!TextUtils.isEmpty(info.time)) {
					try {
						JSONObject obj = new JSONObject();
						obj.put("code", info.code);
						obj.put("time", info.time);
						array.put(obj);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		ComveeHttp http = new ComveeHttp(getApplicationContext(), url);
		http.setOnHttpListener(2, this);
		http.setCallBackAsyn(true);
		http.setPostValueForKey("paramStr", array.toString());
		http.startAsynchronous();
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		cancelProgressDialog();
		switch (what) {
		case 1:
			parseTaskList(b, fromCache);
			break;
		case 2:
			try {
				final ComveePacket packet = ComveePacket.fromJsonString(b);
				if (packet.getResultCode() == 0) {
					ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TASK_MINE));
					ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.INDEX));
					showToast(packet.getResultMsg());
					toFragment(MyTaskCenterFragment.newInstance(false), true);
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

	private ArrayList<String> listItemHeads = new ArrayList<String>();
	private ArrayList<ArrayList<TaskCheckInfo>> listItems = new ArrayList<ArrayList<TaskCheckInfo>>();

	/**
	 * 医生建议
	 * 
	 * @param b
	 * @param fromCache
	 */
	private void parseTaskList(final byte[] b, final boolean fromCache) {
		try {
			final ComveePacket packet = ComveePacket.fromJsonString(b);
			if (packet.getResultCode() == 0) {
				JSONArray taskArray = packet.getJSONArray("body");
				int len = taskArray.length();
				String strTempType = null;
				ArrayList<TaskCheckInfo> taskList = new ArrayList<TaskCheckInfo>();
				listItemHeads.clear();
				listItems.clear();
				ArrayList<TaskCheckInfo> tempList = null;
				for (int i = 0; i < len; i++) {
					JSONObject obj = taskArray.getJSONObject(i);

					TaskCheckInfo task = new TaskCheckInfo();
					task.code = obj.optString("code");
					task.cfgId = obj.optString("cfgId");
					task.rateMemo = obj.optString("rateMemo");
					task.memo = obj.optString("memo");
					task.rateType = obj.optInt("rateType");
					task.time = obj.optString("time");
					task.title = obj.optString("title");
					task.type = obj.optInt("type");

					if (TextUtils.isEmpty(strTempType)) {
						strTempType = task.rateMemo;
						tempList = new ArrayList<TaskCheckInfo>();
						listItems.add(tempList);
						listItemHeads.add(task.rateMemo);
						tempList.add(task);
					} else {
						if (strTempType.equals(task.rateMemo)) {
							tempList.add(task);
						} else {
							strTempType = task.rateMemo;
							tempList = new ArrayList<TaskCheckInfo>();
							listItems.add(tempList);
							listItemHeads.add(task.rateMemo);
							tempList.add(task);
						}
					}
					taskList.add(task);
				}
				totalRows = len;
				mHandler.sendMessage(mHandler.obtainMessage(1, totalRows, 0, taskList));
			} else {
				ComveeHttpErrorControl.parseError(getActivity(), packet);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFialed(int what, int errorCode) {
		cancelProgressDialog();
		ComveeHttpErrorControl.parseError(getActivity(), errorCode);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {
		if (totalRows > mAdapter.getCount()) {
			requestTaskList();
		} else {
		}
	}

	class DoctorAdapter extends SectionedBaseAdapter {

		class ViewHolder {
			TextView tvLabel;
			TextView tvDecs;
			TextView tvValue;
		}

		@Override
		public TaskCheckInfo getItem(int section, int position) {
			return listItems.get(section).get(position);
		}

		@Override
		public long getItemId(int section, int position) {
			return position;
		}

		@Override
		public int getSectionCount() {
			return listItemHeads.size();
		}

		@Override
		public int getCountForSection(int section) {
			return listItems.get(section).size();
		}

		@Override
		public View getItemView(int section, int position, View convertView, ViewGroup parent) {
			final TaskCheckInfo info = getItem(section, position);
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(), R.layout.item_input_tendency, null);
				// convertView.setBackgroundResource(R.drawable.button_white_center);
				holder.tvLabel = (TextView) convertView.findViewById(R.id.tv_label);
				holder.tvDecs = (TextView) convertView.findViewById(R.id.tv_decs);
				holder.tvValue = (TextView) convertView.findViewById(R.id.tv_value);
				holder.tvDecs.setVisibility(View.VISIBLE);
				holder.tvValue.setHint("请输入检查时间");
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tvLabel.setText(info.title);
			holder.tvDecs.setText(info.memo);

			holder.tvValue.setText(info.time);

			return convertView;
		}

		@Override
		public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.item_doctor_recommend_header, null);
			}
			String str = listItemHeads.get(section);
			TextView tv = (TextView) convertView.findViewById(R.id.tv_date);
			tv.setBackgroundResource(R.drawable.task_sign_1);
			tv.setText(str);
			return convertView;
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case 1:
			requestModify();
			break;
		case TitleBarView.ID_LEFT_BUTTON:
			FragmentMrg.toBack(getActivity());
			break;
		case R.id.btn_non_jump:
			toFragment(AssessFragment.newInstance(), true, true);
			break;
		default:
			break;
		}

	}

}
