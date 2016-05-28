package com.comvee.tnb.ui.task;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.RecommondInfo;
import com.comvee.tnb.ui.assess.AssessFragment;
import com.comvee.tnb.ui.remind.RemindDetailFragment;
import com.comvee.tnb.widget.PinnedHeaderListView;
import com.comvee.tnb.widget.SectionedBaseAdapter;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tnb.widget.XExpandableListView.IXListViewListener;
import com.comvee.tool.UserMrg;
import com.comvee.util.TimeUtil;

/**
 * 推荐行动
 * 
 * @author friendlove
 * 
 */
public class DoctorRecommendFragment extends BaseFragment implements IXListViewListener, OnItemClickListener, OnHttpListener, OnClickListener {
	private View layoutNonDefault;
	private PinnedHeaderListView listView;
	private DoctorAdapter mAdapter;
	private int atCurrentPage = 1;// 当前页
	private int totalRows;// 总共多少条
	private boolean mFirstLuanch = true;
	private TitleBarView mBarView;

	public DoctorRecommendFragment() {
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:

				if (totalRows == 0) {
					layoutNonDefault.setVisibility(View.VISIBLE);
					listView.setVisibility(View.GONE);
				} else {
					layoutNonDefault.setVisibility(View.GONE);
					listView.setVisibility(View.VISIBLE);
				}
				mAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}

		};
	};

	public static DoctorRecommendFragment newInstance() {
		DoctorRecommendFragment fragment = new DoctorRecommendFragment();
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

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		ConfigParams.setTaskNew(getApplicationContext(), false);
		mBarView.setTitle(getString(R.string.health_steward_recommend_action));
		init();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	private void init() {

		layoutNonDefault = findViewById(R.id.layout_non_default);
		final TextView tvNonMsg = (TextView) findViewById(R.id.tv_non_msg);
		final Button btnNonJump = (Button) findViewById(R.id.btn_non_jump);
		final ImageView imgNonTag = (ImageView) findViewById(R.id.img_non_tag);

		tvNonMsg.setText(Html.fromHtml("结合您的健康评估结果，我们将为您制定<font color='#1a9293'>个性化的健康任务</font>"));
		btnNonJump.setOnClickListener(this);
		imgNonTag.setImageResource(R.drawable.task_no_data);
		btnNonJump.setText("先做一份健康评估");

		listView = (PinnedHeaderListView) findViewById(R.id.lv_doctor_recommend);
		// listView.setPullRefreshEnable(false);
		// listView.setPullLoadEnable(false);
		// listView.setXListViewListener(this);
		listView.setOnItemClickListener(new PinnedHeaderListView.OnItemClickListener() {

			@Override
			public void onSectionClick(AdapterView<?> adapterView, View view, int section, long id) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id) {
				RecommondInfo info = listItems.get(section).get(position);
				RemindDetailFragment frag = RemindDetailFragment.newInstance();
				frag.setTaskInfo(info);
				toFragment(frag, true, false);
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

	public void requestTaskList() {
		showProgressDialog(getString(R.string.msg_loading));
		final String url = ConfigUrlMrg.REMIND_LIST;
		ComveeHttp http = new ComveeHttp(getApplicationContext(), url);
		http.setOnHttpListener(1, this);
		http.setCallBackAsyn(true);
		http.setNeedGetCache(true, UserMrg.getCacheKey(url));
		http.startAsynchronous();
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		cancelProgressDialog();
		switch (what) {
		case 1:
			parseTaskList(b, fromCache);
			break;

		default:
			break;
		}
	}

	private ArrayList<String> listItemHeads = new ArrayList<String>();
	private ArrayList<ArrayList<RecommondInfo>> listItems = new ArrayList<ArrayList<RecommondInfo>>();

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
				JSONObject jsonpage = packet.getJSONObject("body").getJSONObject("pager");
				totalRows = jsonpage.optInt("totalRows");
				JSONArray taskArray = packet.getJSONObject("body").getJSONArray("rows");
				int len = taskArray.length();
				String strTempEndDt = null;
				ArrayList<RecommondInfo> taskList = new ArrayList<RecommondInfo>();
				listItemHeads.clear();
				listItems.clear();
				ArrayList<RecommondInfo> tempList = null;
				for (int i = 0; i < len; i++) {
					JSONObject obj = taskArray.getJSONObject(i);

					RecommondInfo task = new RecommondInfo();
					task.updateStatusDt = obj.optString("updateStatusDt");
					String insertDt = obj.optString("insertDt");
					if (TextUtils.isEmpty(strTempEndDt)) {
						task.insertDt = "最新推荐";
						strTempEndDt = insertDt;
						tempList = new ArrayList<RecommondInfo>();
						listItems.add(tempList);
						tempList.add(task);
						listItemHeads.add(task.insertDt);
					} else {
						if (strTempEndDt.equals(insertDt)) {
							task.insertDt = "";
							tempList.add(task);
						} else {
							task.insertDt = TimeUtil.fomateTime("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", obj.optString("insertDt"));
							strTempEndDt = insertDt;
							tempList = new ArrayList<RecommondInfo>();
							listItems.add(tempList);
							listItemHeads.add(task.insertDt);
							tempList.add(task);
						}
					}
					task.startDt = obj.optString("startDt");
					task.taskDesc = obj.optString("taskDesc");
					task.isRemind = obj.optInt("isRemind") == 1;
					task.id = obj.optString("memberTaskId");
					task.status = obj.optInt("status");
					task.vhActionId = obj.optString("vhActionId");
					task.rateType = obj.optInt("rateType");
					task.rateValue = obj.optString("rateValue");
					task.remindTime = obj.optString("remindTime");
					task.actionDesc = obj.optString("actionDesc");

					taskList.add(task);
				}
				atCurrentPage = atCurrentPage + 1;

				if (!fromCache) {
					if (!taskList.isEmpty()) {
						ComveeHttp
								.setCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.REMIND_LIST), ConfigParams.CHACHE_TIME_SHORT, b);
					}
				}

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
		// TODO Auto-generated method stub
		if (totalRows > mAdapter.getCount()) {
			requestTaskList();
		} else {
			// listView.stopLoadMore();
		}
	}

	class DoctorAdapter extends SectionedBaseAdapter {

		class ViewHolder {
			TextView tvDate;
			TextView tvContent;
		}

		private String getCurrentDate() {
			String datestr = "";
			try {
				java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
				datestr = df.format(new java.util.Date());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return datestr;
		}

		@Override
		public RecommondInfo getItem(int section, int position) {
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
			final RecommondInfo info = getItem(section, position);
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(), R.layout.item_doctor_recommend, null);
				holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
				holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (TextUtils.isEmpty(info.insertDt)) {
				holder.tvDate.setVisibility(View.GONE);
			} else {
				holder.tvDate.setVisibility(View.GONE);
			}
			if (getCurrentDate().equals(info.insertDt) || "最新推荐".equals(info.insertDt)) {

				holder.tvDate.setBackgroundResource(R.drawable.task_sign_1);
				holder.tvDate.setText(info.insertDt);
			} else {
				holder.tvDate.setBackgroundResource(R.drawable.task_sign_3);
				try {
					if (!TextUtils.isEmpty(info.insertDt)) {
						holder.tvDate.setText(TimeUtil.fomateTime("yyyy-MM-dd", "yyyy年MM月dd日", info.insertDt));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			holder.tvContent.setText(info.actionDesc);
			// if (info.timeStatus == 2)
			// {// 过期
			// holder.tvDate.setBackgroundResource(R.drawable.task_sign_3);
			// } else
			// {
			// holder.tvDate.setBackgroundResource(R.drawable.task_sign_1);
			// }

			return convertView;
		}

		@Override
		public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.item_doctor_recommend_header, null);
			}
			String str = listItemHeads.get(section);
			TextView tv = (TextView) convertView.findViewById(R.id.tv_date);
			if (getCurrentDate().equals(tv) || "最新推荐".equals(str.toString())) {
				tv.setBackgroundResource(R.drawable.task_sign_1);
				tv.setText(str);
			} else {
				tv.setBackgroundResource(R.drawable.task_sign_3);
				try {
					if (!TextUtils.isEmpty(str)) {
						tv.setText(TimeUtil.fomateTime("yyyy-MM-dd", "yyyy年MM月dd日", str));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return convertView;
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
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
