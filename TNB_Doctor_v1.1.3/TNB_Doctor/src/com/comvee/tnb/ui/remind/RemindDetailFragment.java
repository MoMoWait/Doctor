package com.comvee.tnb.ui.remind;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDateTimePickDialog.OnDateTimeChangeListener;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.RecommondInfo;
import com.comvee.tnb.model.TaskItem;
import com.comvee.tnb.ui.ask.AskIndexFragment;
import com.comvee.tnb.ui.task.TaskCenterFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;
import com.comvee.util.TimeUtil;

public class RemindDetailFragment extends BaseFragment implements OnCheckedChangeListener, OnDateTimeChangeListener, OnClickListener, OnHttpListener {

	private ArrayList<TaskItem> listItems;
	private TextView tvRemindTime, tvContent;
	private RecommondInfo mInfo;
	private TextView tvTaskCount;
	private View btnAddTask;
	private TitleBarView mBarView;

	public static RemindDetailFragment newInstance() {
		RemindDetailFragment fragment = new RemindDetailFragment();
		return fragment;
	}

	public void setTaskInfo(RecommondInfo info) {
		this.mInfo = info;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_remind_detail;
	}

	public RemindDetailFragment() {
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		mRoot.setVisibility(View.GONE);
		mBarView.setTitle("推荐行动详情");
		init();
		requestTasks();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	private void init() {
		listItems = new ArrayList<TaskItem>();
		btnAddTask = findViewById(R.id.btn_add_task);
		tvTaskCount = (TextView) findViewById(R.id.tv_task_count);
		final View btnToAsk = findViewById(R.id.btn_to_ask);
		final TextView tvHelp = (TextView) findViewById(R.id.tv_help);
		tvHelp.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvHelp.setOnClickListener(this);
		final TextView tvSuggest = (TextView) findViewById(R.id.tv_content_1);
		tvContent = (TextView) findViewById(R.id.tv_content);
		btnToAsk.setOnClickListener(this);
		tvContent.setText(Html.fromHtml(String.format("<font color='#1a9293'>推荐行动：</font>%s", mInfo.actionDesc)));
		tvSuggest.setText(mInfo.taskDesc);
		btnAddTask.setOnClickListener(this);
	}

	/**
	 * 请求可领取的任务列表
	 */
	private void requestTasks() {
		showProgressDialog(getString(R.string.msg_loading));

		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.RECOMMOND_TASK_LIST);
		http.setPostValueForKey("memberTaskId", mInfo.id);
		http.setOnHttpListener(3, this);
		http.startAsynchronous();
	}

	private void parseTaskList(byte[] b, boolean fromCache) {
		try {
			ComveePacket packet = ComveePacket.fromJsonString(b);
			if (packet.getResultCode() == 0) {
				JSONArray array = packet.getJSONObject("body").getJSONArray("suggestJob");
				int len = array.length();
				for (int i = 0; i < len; i++) {
					JSONObject obj = array.getJSONObject(i);
					TaskItem item = new TaskItem();
					item.setImgUrl(obj.optString("imgUrl"));
					item.setName(obj.optString("jobTitle"));
					item.setIsNew(obj.optInt("isNew"));
					item.setDetail(obj.optString("jobInfo"));
					item.setUse(obj.optString("gainNum"));
					item.setJobCfgId(obj.optString("jobCfgId"));
					item.setComment(obj.optString("commentNum"));
					listItems.add(item);
				}

				if (listItems.isEmpty()) {
					tvTaskCount.setVisibility(View.GONE);
					btnAddTask.setVisibility(View.GONE);
				} else {
					// tvTaskCount.setText(Html.fromHtml(String.format("根据推荐行动，有<font color='#8fc31f'>%d</font>条任务可供领取",
					// listItems.size())));
					tvTaskCount.setText(Html.fromHtml(String.format("根据推荐行动，有%d条任务可供领取", listItems.size())));
				}
				mRoot.setVisibility(View.VISIBLE);

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
		if (what == 3) {
			parseTaskList(b, fromCache);
			return;
		}

		try {
			ComveePacket packet = ComveePacket.fromJsonString(b);
			if (packet.getResultCode() == 0) {
				ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.REMIND_LIST));
				// showToast(packet.getResultMsg());
				FragmentMrg.toBack(getActivity());
			} else {
				// showToast(packet.getResultMsg());
				ComveeHttpErrorControl.parseError(getActivity(), packet);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add_task:
			TaskCenterFragment frag = TaskCenterFragment.newInstance();
			frag.setListItem(listItems);
			frag.setRecommond(true);
			frag.setTitle(getString(R.string.health_steward_recommend_action));
			toFragment(frag, true, true);
			break;
		case R.id.tv_help:

			break;
		case R.id.btn_to_ask:
			toFragment(AskIndexFragment.class, null, true);
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

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	}

	@Override
	public void onChange(DialogFragment dialog, Calendar cal) {
		// TODO Auto-generated method stub
		if (Calendar.getInstance().getTimeInMillis() > cal.getTimeInMillis()) {
			Toast.makeText(getContext(), "亲，该时间已过不可设置哦！", Toast.LENGTH_SHORT).show();
			return;
		}

		tvRemindTime.setText(TimeUtil.fomateTime(cal.getTimeInMillis(), ConfigParams.TIME_FORMAT1));
	}
}
