package com.comvee.ui.remind;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.TaskRemindAdaapter;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.AlarmInfo;
import com.comvee.tnb.model.MyTaskInfo;
import com.comvee.tnb.ui.task.TaskDetailFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;

/**
 * 任务提醒
 * 
 * @author Administrator
 * 
 */
public class TaskRemindFragement extends BaseFragment implements OnHttpListener, OnItemClickListener {
	private ListView listView;
	private ArrayList<MyTaskInfo> taskList = null;
	private TaskRemindAdaapter mAdaapter;
	private TitleBarView mBarView;

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_task_remind;
	}

	public TaskRemindFragement() {
	}

	public static TaskRemindFragement newInstance() {
		TaskRemindFragement fragment = new TaskRemindFragement();
		return fragment;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		mBarView.setTitle(getString(R.string.remind_remind_task_tip));
		init();
		requestTaskList();
	}

	public void requestTaskList() {
		showProgressDialog(getResources().getString(R.string.msg_loading));
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.TASK_MINE);
		http.setPostValueForKey("type", "0");
		http.setOnHttpListener(1, this);
		http.startAsynchronous();
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	private void init() {
		taskList = new ArrayList<MyTaskInfo>();
		listView = (ListView) findViewById(R.id.lv_task_remind);
		mAdaapter = new TaskRemindAdaapter(taskList, getApplicationContext());
		listView.setAdapter(mAdaapter);
		listView.setEmptyView(findViewById(R.id.emptyview));
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		switch (what) {
		case 1:
			try {
				cancelProgressDialog();
				ComveePacket packet = ComveePacket.fromJsonString(b);
				boolean suggest = packet.optJSONObject("body").optInt("suggest") != 1;

				parseTaskList(b, packet, fromCache);

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

	/**
	 * 1、未开始 2、进行中 3、已完成
	 * 
	 * @param b
	 * @param fromCache
	 */
	private void parseTaskList(final byte[] b, final ComveePacket packet, final boolean fromCache) {
		if (packet.getResultCode() == 0) {

			// 比较耗时，开一个线程先这样搞
			// new Thread(new Runnable() {
			// @Override
			// public void run() {
			try {
				// if (!fromCache) {
				JSONArray taskArray = packet.getJSONObject("body").getJSONArray("rows");
				int len = taskArray.length();
				ArrayList<AlarmInfo> timeList = new ArrayList<AlarmInfo>();
				taskList.clear();
				for (int i = 0; i < len; i++) {
					JSONObject obj = taskArray.getJSONObject(i);

					MyTaskInfo task = new MyTaskInfo();
					task.defaultRemind = obj.optInt("defaultRemind");
					task.doPercent = obj.optInt("doPercent");
					task.doSuggest = obj.optString("doSuggest");
					task.endDt = obj.optString("endDt");
					task.finishNum = obj.optInt("finishNum");
					task.imgUrl = obj.optString("imgUrl");
					task.insertDt = obj.optString("insertDt");
					task.id = obj.optString("memberJobId");
					task.jobInfo = obj.optString("jobInfo");
					task.jobTitle = obj.optString("jobTitle");
					task.jobType = obj.optInt("jobType");
					task.memberJobId = obj.optString("memberJobId");
					task.totalNum = obj.optInt("jobTotal");
					task.residue = obj.optInt("residue");
					task.status = obj.optInt("status");

					task.doctorId = obj.optLong("doctorId");
					task.doctorName = obj.optString("doctorName");
					final String[] times = obj.optString("dateStr").trim().split(",");
					for (String t : times) {
						AlarmInfo item = new AlarmInfo();
						item.setTime(t);
						item.setId(task.id + t);
						item.setTaskId(task.id);
						item.setStatus(task.status);
						timeList.add(item);
					}
					if (task.status == 1) {
						taskList.add(task);
					}
				}

				if (!taskList.isEmpty()) {
					ComveeHttp.setCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TASK_MINE), ConfigParams.CHACHE_TIME_SHORT, b);

				}
				mAdaapter.setList(taskList);
				mAdaapter.notifyDataSetChanged();
				// }

			} catch (Exception e) {
				e.printStackTrace();
				// }
			}
			// }).start();

		} else {
			ComveeHttpErrorControl.parseError(getActivity(), packet);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		MyTaskInfo info = (MyTaskInfo) mAdaapter.getItem(arg2);
		TaskDetailFragment frag = TaskDetailFragment.newInstance();
		frag.setDoctorName(info.getDoctorName());
		frag.setTaskId(info.memberJobId);
		toFragment(frag, true, true);
	}
}
