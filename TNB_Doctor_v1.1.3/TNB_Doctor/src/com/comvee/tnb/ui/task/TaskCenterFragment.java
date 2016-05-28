package com.comvee.tnb.ui.task;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.TaskAdapter;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.TaskFilterInfo;
import com.comvee.tnb.model.TaskItem;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.ui.xlistview.XListView.IXListViewListener;
import com.comvee.tnb.view.TaskFilterWindow;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;

public class TaskCenterFragment extends BaseFragment implements OnClickListener, IXListViewListener, OnItemClickListener, OnHttpListener {

	private ArrayList<TaskItem> listItems = null;
	private XListView listView;
	private TaskAdapter mAdapter;
	private int pageNum = 1;
	private String mTitle;
	private boolean isRecommond;// 是否是推荐行动
	private View layoutNonDefault;
	private boolean beSelfTask;
	private ArrayList<TaskFilterInfo> mTaskFilterList;
	private String type;
	private int isTaskcent = 1;// 1任务中心 2 推荐任务中心
	private String doctorId;// 推荐任务中心医生id
	private TitleBarView mBarView;

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setIsTaskcent(int isTaskcent) {
		this.isTaskcent = isTaskcent;
	}

	public void setSelfTask(boolean isSelf) {
		beSelfTask = isSelf;
	}

	public void setRecommond(boolean isRecommond) {
		this.isRecommond = isRecommond;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	private void setDataList(ArrayList<TaskItem> listItems) {
		this.listItems = listItems;
	}

	public TaskCenterFragment() {
	}

	public static TaskCenterFragment newInstance(ArrayList<TaskItem> listItems) {
		TaskCenterFragment fragment = new TaskCenterFragment();
		fragment.setDataList(listItems);
		fragment.setSelfTask(true);
		return fragment;
	}

	public static TaskCenterFragment newInstance() {
		TaskCenterFragment fragment = new TaskCenterFragment();
		return fragment;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_task_center;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		super.onLaunch(bundle);
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		mRoot.setVisibility(View.GONE);
		if (TextUtils.isEmpty(mTitle)) {
			mBarView.setTitle(getString(R.string.label_taskcenter));
			if (isTaskcent == 1) {
				mBarView.setRightButton("筛选", this);
			}
		} else {
			mBarView.setTitle(mTitle);
		}

		// setRightButton("筛选", this);

		init();
	}

	public void setListItem(ArrayList<TaskItem> listItems) {
		this.listItems = listItems;
	}

	private void init() {
		listView = (XListView) mRoot.findViewById(R.id.lv_task);
		listView.setPullRefreshEnable(false);
		listView.setPullLoadEnable(false);
		listView.setXListViewListener(this);
		listView.setOnItemClickListener(this);
		if (beSelfTask) {
			View view = View.inflate(getApplicationContext(), R.layout.layout_no_task1, null);
			listView.addHeaderView(view, null, false);
		}
		mAdapter = new TaskAdapter(getApplicationContext());
		listView.setAdapter(mAdapter);
		layoutNonDefault = findViewById(R.id.layout_non_default);
		layoutNonDefault.setVisibility(View.GONE);
		if (listItems == null || listItems.isEmpty()) {
			if (isRecommond) {
				mRoot.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
				layoutNonDefault.setVisibility(View.VISIBLE);
				final TextView tvNonMsg = (TextView) findViewById(R.id.tv_non_msg);
				final Button btnNonJump = (Button) findViewById(R.id.btn_non_jump);
				final ImageView imgNonTag = (ImageView) findViewById(R.id.img_non_tag);
				tvNonMsg.setText(Html.fromHtml("好棒，推荐任务已经<font color='#1a9293'>被领取完了</font>要记得执行哦！"));
				btnNonJump.setOnClickListener(this);
				imgNonTag.setImageResource(R.drawable.task_no_data);
				btnNonJump.setText("看看其他的推荐行动");
			} else {
				switch (isTaskcent) {
				case 1:
					requestTaskCenter(type);
					break;
				case 2:

					requesetrecommendJobList(type);
					break;
				default:
					break;
				}

			}
		} else {
			mRoot.setVisibility(View.VISIBLE);
			listView.setPullLoadEnable(false);
			mAdapter.setListItems(listItems);
			mAdapter.notifyDataSetChanged();
		}
	}

	private void requestTaskCenter(String type) {
		showProgressDialog(getString(R.string.msg_loading));

		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.TASK_CENTER);
		http.setOnHttpListener(1, this);
		http.setPostValueForKey("page", "" + 1);
		http.setPostValueForKey("rows", "" + 10000);
		http.setPostValueForKey("type", type);
		if (pageNum == 1) {
			if (listItems != null) {
				listItems.clear();
			}
			http.setNeedGetCache(true, UserMrg.getCacheKey(ConfigUrlMrg.TASK_CENTER));
		}
		http.startAsynchronous();
	}

	// 推荐任务请求
	private void requesetrecommendJobList(String type) {
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.RECOMMENT_TASK_CENTER);
		http.setOnHttpListener(1, this);
		http.setPostValueForKey("page", "" + 1);
		http.setPostValueForKey("rows", "" + 10000);
		http.setPostValueForKey("type", type);
		http.setPostValueForKey("doctorId", doctorId);
		if (pageNum == 1) {
			if (listItems != null) {
				listItems.clear();
			}

		}
		http.startAsynchronous();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if(window != null){
			window.dismiss();
		}
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		cancelProgressDialog();
		switch (what) {
		case 1:
			parseTaskCenter(b, fromCache);
			break;
		default:
			break;
		}
	}

	private void parseTaskCenter(byte[] b, boolean fromCache) {
		listView.stopLoadMore();

		try {
			ComveePacket packet = ComveePacket.fromJsonString(b);
			if (packet.getResultCode() == 0) {
				if (listItems == null) {
					listItems = new ArrayList<TaskItem>();
				} else {
					listItems.clear();
				}

				// ====================筛选列表===================
				JSONArray array = packet.getJSONObject("body").getJSONArray("typeList");
				int len = array.length();
				mTaskFilterList = new ArrayList<TaskFilterInfo>();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < len; i++) {
					JSONObject obj = array.getJSONObject(i);
					TaskFilterInfo item = new TaskFilterInfo();
					item.id = obj.optString("id");
					item.name = obj.optString("value");
					mTaskFilterList.add(item);
					sb.append(item.id).append(",");
				}

				TaskFilterInfo info = new TaskFilterInfo();
				info.name = "全部";
				info.id = sb.substring(0, sb.length() - 1);

				mTaskFilterList.add(0, info);
				// =======================================

				array = packet.getJSONObject("body").getJSONArray("rows");
				len = array.length();
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
					item.setJobType(obj.optInt("jobType"));
					listItems.add(item);
				}

				int total = packet.getJSONObject("body").getJSONObject("pager").optInt("totalRows");
				int curPage = packet.getJSONObject("body").getJSONObject("pager").optInt("currentPage");
				if (listItems.size() >= total) {
					listView.setPullLoadEnable(false);
				}
				mRoot.setVisibility(View.VISIBLE);
				mAdapter.setListItems(listItems);
				mAdapter.notifyDataSetChanged();
				if (!fromCache && curPage == 1 && type == null) {// 只缓存第一页
					ComveeHttp.setCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TASK_CENTER), ConfigParams.CHACHE_TIME_SHORT, b);
				}
			} else {
				showToast(packet.getResultMsg());
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
		TaskIntroduceFragment frag = TaskIntroduceFragment.newInstance();
		frag.setTaskInfo(mAdapter.getItem(beSelfTask ? arg2 - 2 : arg2 - 1));
		frag.setRecommondList(listItems);
		frag.setRecommond(isRecommond);
		if (doctorId != null) {
			frag.setDoctorId(Long.parseLong(doctorId));
		}
		toFragment(frag, true, true);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore() {
		requestTaskCenter(null);
	}

	private TaskFilterWindow window;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case TitleBarView.ID_RIGHT_BUTTON:
			if (mTaskFilterList != null) {
				if(window == null){
					window = new TaskFilterWindow(getActivity(), mTaskFilterList, TaskCenterFragment.this.type);
				}
				if(!window.isShowing()){
					int[] loc = new int[2];
					mBarView.getLocationInWindow(loc);
					window.setOnListener(new TaskFilterWindow.OnItemClick() {
						@Override
						public void onClick(String type) {
							TaskCenterFragment.this.type = type;
							ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TASK_CENTER));
							requestTaskCenter(type);
						}
					});
					window.showAsDropDown(mBarView);
				}

			}

			break;
		case R.id.btn_non_jump:

			if (isRecommond) {
				FragmentMrg.toBack(getActivity(),new Bundle());
			}
			break;

		default:
			break;
		}
	}
}
