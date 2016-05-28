package com.comvee.tnb.ui.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.MyTaskInfo;
import com.comvee.tnb.model.TaskItemInfo;
import com.comvee.tnb.ui.book.BookWebActivity;
import com.comvee.tnb.widget.PinnedHeaderListView;
import com.comvee.tnb.widget.SectionedBaseAdapter;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tnb.widget.TouchedAnimation;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UserMrg;
import com.comvee.util.TimeUtil;

/**
 * 任务列表
 * 
 * @author friendlove
 * 
 */
public class TaskDetailFragment extends BaseFragment implements OnClickListener, OnHttpListener {

	private PinnedHeaderListView mListView;
	private TaskListAdapter mAdapter;
	private MyTaskInfo mInfo;
	private TextView tvName, tvTime, tvPercent, tvDecs;
	private ProgressBar proPercent;
	private ImageView imgPhoto;
	// private CheckBox btnOpen;// 开关
	private String mTaskID;
	private TitleBarView mBarView;
	private String doctorName;

	public String getDoctorName() {
		return doctorName;
	}

	public TaskDetailFragment() {
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				refreshView();
				mRoot.setVisibility(View.VISIBLE);

				break;

			default:
				break;
			}
		};
	};

	public void setTaskId(String taskId) {
		mTaskID = taskId;
	}

	public static TaskDetailFragment newInstance() {
		TaskDetailFragment fragment = new TaskDetailFragment();
		return fragment;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_task_detail;
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		requestTaskDetail();
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		mRoot.setVisibility(View.GONE);
		init();
		mBarView.setTitle(getString(R.string.task_introduce_task_detail));
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	private void init() {

		// btnOpen = (CheckBox) findViewById(R.id.check1);
		mListView = (PinnedHeaderListView) findViewById(R.id.list_view);
		// mListView.addHeaderView(createHeaderView());
		createHeaderView();
		mAdapter = new TaskListAdapter();
		mListView.setAdapter(mAdapter);
		// btnOpen.setOnClickListener(this);
		mListView.setOnItemClickListener(new PinnedHeaderListView.OnItemClickListener() {

			@Override
			public void onSectionClick(AdapterView<?> adapterView, View view, int section, long id) {
			}

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id) {
				TaskItemInfo info = listItems.get(section).get(position);
				if (info.jobDetailType == 3) {
					// BookWebFragment fragment = null;
					if (info.status != 1 && info.timeStatus != 2) {
						// fragment = new BookWebFragment(mInfo.jobTitle,
						// info.jobDetailUrl, info.memberJobDetailId);
						BookWebActivity.toWebActivity(getActivity(), BookWebActivity.TASK, null, mInfo.jobTitle, info.jobDetailUrl,
								info.memberJobDetailId);
					} else {
						// fragment = new BookWebFragment(mInfo.jobTitle,
						// info.jobDetailUrl, null);
						BookWebActivity.toWebActivity(getActivity(), BookWebActivity.TASK, null, mInfo.jobTitle, info.jobDetailUrl, "");
					}

					// toFragment(fragment, true, true);
				}
			}
		});
		refreshView();

	}

	private View createHeaderView() {
		// View view = View.inflate(getApplicationContext(), R.layout.item_task,
		// null);
		View view = findViewById(R.id.layout_top);

		tvDecs = (TextView) view.findViewById(R.id.tv_stauts);
		tvName = (TextView) view.findViewById(R.id.tv_label);// 内容
		tvTime = (TextView) view.findViewById(R.id.tv_time);// 时间
		imgPhoto = (ImageView) view.findViewById(R.id.img_photo);
		tvPercent = (TextView) view.findViewById(R.id.tv_percent);
		proPercent = (ProgressBar) view.findViewById(R.id.pro_percent);
		return view;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_modify:
			CheckTaskFragment frag2 = CheckTaskFragment.newInstance();
			toFragment(frag2, true, true);
			break;
		case TitleBarView.ID_RIGHT_BUTTON:
			CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
			builder.setMessage("真的要放弃任务吗？");
			builder.setNegativeButton("取消", null);
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					requestTaskRemove();
				}
			});
			builder.create().show();
			break;
		// case R.id.check1:
		// requestModifyTask();
		// break;

		default:
			break;
		}
	}

	// 删除任务
	private void requestTaskRemove() {
		showProgressDialog("请稍候...");
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.TASK_REMOVE);
		http.setPostValueForKey("memberJobId", mTaskID);
		http.setOnHttpListener(4, this);
		http.startAsynchronous();
	}

	//
	// // 设置开关
	// private void requestModifyTask() {
	// showProDialog("请稍候...");
	// ComveeHttp http = new ComveeHttp(getApplicationContext(),
	// ConfigUrlMrg.TASK_MODIFY);
	// http.setPostValueForKey("memberJobId", mTaskID);
	// http.setPostValueForKey("defualtRemind", btnOpen.isChecked() ? "1" :
	// "0");
	// http.setOnHttpListener(3, this);
	// http.startAsynchronous();
	// }

	private void requestTaskComplete(String id) {
		showProgressDialog("请稍候...");
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.TASK_COMPLETE);
		http.setPostValueForKey("memberJobDetailId", id);
		http.setOnHttpListener(2, this);
		http.startAsynchronous();
	}

	private void requestTaskDetail() {
		showProgressDialog("请稍候...");
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.TASK_DETAIL);
		http.setPostValueForKey("memberJobId", mTaskID);
		http.setOnHttpListener(1, this);
		http.setCallBackAsyn(true);
		http.startAsynchronous();
	}

	class TaskListAdapter extends SectionedBaseAdapter implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			final int id = v.getId();
			final String[] position = v.getTag().toString().split(",");
			switch (id) {
			case R.id.btn_complete:
				requestTaskComplete(getItem(Integer.valueOf(position[0]), Integer.valueOf(position[1])).memberJobDetailId);
				break;
			default:
				break;
			}
		}

		@Override
		public TaskItemInfo getItem(int section, int position) {
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
		public View getItemView(int section, int position, View arg1, ViewGroup parent) {
			if (arg1 == null) {
				arg1 = View.inflate(getContext(), R.layout.item_task_detail, null);
			}

			final TaskItemInfo info = getItem(section, position);

			final TextView tvTime = (TextView) arg1.findViewById(R.id.tv_time);
			final TextView tvContent = (TextView) arg1.findViewById(R.id.tv_content);
			final ImageView ivTag = (ImageView) arg1.findViewById(R.id.iv_tag);
			final TextView btnComplete = (TextView) arg1.findViewById(R.id.btn_complete);
			tvTime.setText(info.remindTime);
			tvContent.setText(info.doSuggest);

			// if(info.jobDetailType == 3){
			// btnComplete.setText("我读完了");
			// }else{
			// if (info.timeStatus != 2) {
			// tvTime.setVisibility(View.GONE);
			// }
			btnComplete.setEnabled(true);
			btnComplete.setText("我完成啦");
			btnComplete.setTextColor(getResources().getColor(R.color.white));
			btnComplete.setBackgroundResource(R.drawable.btn_green_button_radius);
			// }

			if (info.status == 1) {
				// ivTag.setImageResource(R.drawable.task_status_complete);
				// ivTag.setVisibility(View.VISIBLE);
				btnComplete.setBackgroundResource(0);
				btnComplete.setText("已完成");
				btnComplete.setEnabled(false);
				btnComplete.setTextColor(getResources().getColor(R.color.theme_color_green));
			} else if (info.status == 0) {
				if (info.timeStatus == 2) {
					btnComplete.setVisibility(View.VISIBLE);
					btnComplete.setBackgroundResource(0);
					btnComplete.setText("未完成");
					btnComplete.setEnabled(false);
					btnComplete.setTextColor(Color.RED);
				} else {
					ivTag.setImageResource(0);
					ivTag.setVisibility(View.GONE);
					btnComplete.setVisibility(View.VISIBLE);
				}
				// btnComplete.setVisibility(View.GONE);
			} else {
				btnComplete.setVisibility(View.VISIBLE);
				btnComplete.setBackgroundResource(0);
				btnComplete.setText("未完成");
				btnComplete.setEnabled(false);
				btnComplete.setTextColor(Color.RED);
				// ivTag.setVisibility(View.VISIBLE);
				// ivTag.setImageResource(R.drawable.task_status_uncomplete);
			}

			if (info.timeStatus == 2 || info.status == 1) {
				btnComplete.setVisibility(View.VISIBLE);

			} else {
				btnComplete.setVisibility(View.VISIBLE);

			}

			btnComplete.setOnClickListener(this);
			btnComplete.setTag(section + "," + position);
			return arg1;
		}

		@Override
		public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.item_doctor_recommend_header, null);
			}
			String str = listItemHeads.get(section);
			TextView tv = (TextView) convertView.findViewById(R.id.tv_date);
			tv.setText(str);
			return convertView;
		}

	}

	private ArrayList<String> listItemHeads = new ArrayList<String>();
	private ArrayList<List<TaskItemInfo>> listItems = new ArrayList<List<TaskItemInfo>>();

	private void parseTaskDetail(byte[] arg1, boolean fromCach) {
		try {
			ComveePacket packet = ComveePacket.fromJsonString(arg1);
			listItems.clear();
			listItemHeads.clear();
			if (packet.getResultCode() == 0) {
				Calendar curCal = Calendar.getInstance();
				curCal.set(Calendar.HOUR_OF_DAY, 0);
				curCal.set(Calendar.MINUTE, 0);
				curCal.set(Calendar.SECOND, 0);

				JSONArray array = packet.getJSONObject("body").getJSONArray("jobDetail");
				int len = array.length();

				LinkedList<TaskItemInfo> mListItem = new LinkedList<TaskItemInfo>();
				LinkedList<TaskItemInfo> mlistItemToday = null;
				LinkedList<TaskItemInfo> mlistItemHistory = null;

				for (int i = len - 1; i >= 0; i--) {
					TaskItemInfo info = new TaskItemInfo();
					JSONObject json = array.getJSONObject(i);
					info.datailInfo = json.optString("datailInfo");
					info.title = json.optString("title");
					info.remindTime = json.optString("remindTime");
					info.memberJobDetailId = json.optString("memberJobDetailId");
					info.url = json.optString("url");
					info.doSuggest = json.optString("doSuggest");
					info.jobTime = json.optString("jobTime");
					info.remindDt = json.optString("remindDt");
					info.memberJobId = json.optString("memberJobId");
					info.status = json.optInt("status");
					info.jobDetailType = json.optInt("jobDetailType");
					info.jobDetailUrl = json.optString("jobDetailUrl");

					Calendar tempCal = Calendar.getInstance();
					tempCal.setTimeInMillis(TimeUtil.getUTC(info.remindTime, "yyyy-MM-dd HH:mm:ss"));

					String tempTime = TimeUtil.fomateTime("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", info.remindTime);
					String curTime = TimeUtil.fomateTime(curCal.getTimeInMillis(), "yyyy-MM-dd");

					int result = tempCal.compareTo(curCal);
					if (tempTime.equals(curTime)) {
						info.timeStatus = 0;
						if (mlistItemToday == null) {
							mlistItemToday = new LinkedList<TaskItemInfo>();
							listItemHeads.add("今日任务");
							listItems.add(mlistItemToday);
						}
						if (info.status == 1) {// 已完成拍到后面
							mlistItemToday.addLast(info);
						} else {// 未完成排到前面
							mlistItemToday.addFirst(info);
						}

					} else if (result == -1)// 过期的
					{
						info.timeStatus = 2;
						if (mlistItemHistory == null) {
							mlistItemHistory = new LinkedList<TaskItemInfo>();
							listItemHeads.add("历史任务");
							listItems.add(mlistItemHistory);
						}
						// mlistItemHistory.add(info);
						mlistItemHistory.addFirst(info);

					} else {
						info.timeStatus = 1;
					}

				}

				if (listItemHeads.size() > 1) {
					// 将分类排序倒过来
					try {
						listItemHeads.add(listItemHeads.get(0));
						listItemHeads.remove(0);
						listItems.add(listItems.get(0));
						listItems.remove(0);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				mInfo = new MyTaskInfo();
				JSONObject obj = packet.getJSONObject("body").getJSONObject("job");
				mInfo.defaultRemind = obj.optInt("defaultRemind");
				mInfo.doPercent = obj.optInt("doPercent");
				mInfo.doSuggest = obj.optString("doSuggest");
				mInfo.endDt = obj.optString("endDt");
				mInfo.finishNum = obj.optInt("finishNum");
				mInfo.imgUrl = obj.optString("imgUrl");
				mInfo.insertDt = obj.optString("insertDt");
				mInfo.id = obj.optString("jobCfgId");
				mInfo.jobInfo = obj.optString("jobInfo");
				mInfo.jobTitle = obj.optString("jobTitle");
				mInfo.jobType = obj.optInt("jobType");
				mInfo.memberJobId = obj.optString("memberJobId");
				mInfo.totalNum = obj.optInt("jobTotal");
				mInfo.residue = obj.optInt("residue");
				mInfo.delFlag = obj.optInt("delFlag");

				if (mInfo.delFlag == 1) {
					mBarView.setRightButton(R.drawable.top_menu_remove, this);
				} else {
					mBarView.hideRightButton();
				}

				if (listItemHeads.isEmpty() && mInfo.jobType == 10) {
					findViewById(R.id.tv_no_data).setVisibility(View.VISIBLE);
				}

				if (!fromCach) {
					ComveeHttp.setCache(getApplicationContext(), ConfigUrlMrg.TASK_DETAIL, ConfigParams.CHACHE_TIME_LONG, arg1);
				}

				mHandler.sendMessage(mHandler.obtainMessage(1, 0, 0, mListItem));
			} else {
				showToast(packet.getResultMsg());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseTaskComplete(byte[] arg1, boolean fromCach) {
		try {
			ComveePacket packet = ComveePacket.fromJsonString(arg1);

			if (packet.getResultCode() == 0) {

				showToast("完成任务");
				requestTaskDetail();
				ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TASK_MINE));
				ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.INDEX));
			} else {
				ComveeHttpErrorControl.parseError(getActivity(), packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// private void parseTaskModify(byte[] arg1, boolean fromCach) {
	// try {
	// ComveePacket packet = ComveePacket.fromJsonString(arg1);
	//
	// if (packet.getResultCode() == 0) {
	// showToast(btnOpen.isChecked() ? "成功开启任务提醒" : "成功关闭任务提醒");
	// } else {
	// ComveeHttpErrorControl.parseError(getActivity(), packet);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	private void parseTaskRemove(byte[] arg1, boolean fromCach) {
		try {
			ComveePacket packet = ComveePacket.fromJsonString(arg1);

			if (packet.getResultCode() == 0) {
				ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.INDEX));

				ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TASK_MINE));
				FragmentMrg.toBack(getActivity(),new Bundle());
			} else {
				ComveeHttpErrorControl.parseError(getActivity(), packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		cancelProgressDialog();
		switch (what) {
		case 1:
			parseTaskDetail(b, fromCache);
			break;
		case 2:
			parseTaskComplete(b, fromCache);
			break;
		// case 3:
		// parseTaskModify(b, fromCache);
		// break;
		case 4://删除
			parseTaskRemove(b, fromCache);
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
		return false;
	}

	private void refreshView() {

		if (mInfo == null) {
			return;
		}
		// final View tvPercentLabel = (TextView)
		// findViewById(R.id.tv_percent1);
		final View btnModify = findViewById(R.id.btn_modify);
		// final TextView tvdoctor = (TextView) findViewById(R.id.tv_doctor);
		btnModify.setOnTouchListener(TouchedAnimation.TouchLight);
		btnModify.setVisibility(mInfo.jobType == 10 ? View.VISIBLE : View.GONE);
		btnModify.setOnClickListener(this);
		// btnOpen.setChecked(mInfo.defaultRemind == 1);
		ImageLoaderUtil.getInstance(mContext).displayImage(mInfo.getImgUrl(), imgPhoto, ImageLoaderUtil.default_options);

		tvName.setText(mInfo.getJobTitle());

		// if (!TextUtils.isEmpty(doctorName)) {
		// tvdoctor.setVisibility(View.VISIBLE);
		// tvdoctor.setText(doctorName + "\n医生推荐");
		// }
		if (mInfo.jobType == 10) {
			mBarView.hideRightButton();
			tvPercent.setVisibility(View.GONE);
			proPercent.setVisibility(View.GONE);
			tvDecs.setVisibility(View.GONE);
			// tvPercentLabel.setVisibility(View.GONE);
		} else {
			tvPercent.setText(mInfo.doPercent + "%");
			proPercent.setMax(100);
			proPercent.setProgress(mInfo.doPercent);

			tvDecs.setText(String.format("总共%d天  还需%d天", mInfo.totalNum, mInfo.residue));
		}
		try {
			String str = String.format("于%s日添加本任务", TimeUtil.fomateTime("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", mInfo.getInsertDt()));

			tvTime.setText(str);
		} catch (Exception E) {
		}
		mAdapter.notifyDataSetChanged();

	}
}
