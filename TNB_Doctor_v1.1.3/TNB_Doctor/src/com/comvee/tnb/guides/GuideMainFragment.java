package com.comvee.tnb.guides;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.ComveeTask;
import com.comvee.tnb.widget.TitleBarView;

public class GuideMainFragment extends BaseFragment implements ViewFactory, OnItemClickListener, OnClickListener {
	private ArrayList<IndexTaskInfo> mListItems;
	private IndexTaskAdapter mAdapter;
//	private int stage;// 阶段
	// private boolean isAnimation;
	private int fromWhere;// 1、主页进来（炫酷动画效果）
	private ListView listView;
	private TitleBarView mBarView;
	private ArrayList<IndexTaskInfo> mMainList = new ArrayList<IndexTaskInfo>();

	public static GuideMainFragment newInstance() {
		GuideMainFragment frag = new GuideMainFragment();
		return frag;
	}

	public GuideMainFragment() {
	}

	// 排序比较
	public Comparator<IndexTaskInfo> comparator = new Comparator<IndexTaskInfo>() {

		@Override
		public int compare(IndexTaskInfo index1, IndexTaskInfo index2) {
			// TODO Auto-generated method stub
			if (index1.getStatus() > index2.getStatus()) {
				return 1;
			} else if (index1.getStatus() == index2.getStatus()) {
				return 0;
			} else {
				return -1;
			}
		}
	};

	private void loadMainTaskList() {
		showProgressDialog(getString(R.string.msg_loading));
		new ComveeTask<Integer>() {

			@Override
			protected Integer doInBackground() {
				try {
					ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.NEW_GUIDE_LIST);
					String result = http.startSyncRequestString();
					final ComveePacket packet = ComveePacket.fromJsonString(result);

					if (packet.getResultCode() != 0) {
						postError(packet);
						return null;
					}

					JSONObject body = packet.optJSONObject("body");
					ArrayList<IndexTaskInfo> list = new ArrayList<IndexTaskInfo>();
					mMainList = parseGuidesList(list, body.optJSONArray("s1"));
					mMainList = parseGuidesList(list, body.optJSONArray("s2"));
					mMainList = parseGuidesList(list, body.optJSONArray("s3"));
					mMainList = parseGuidesList(list, body.optJSONArray("s4"));
					mMainList = parseGuidesList(list, body.optJSONArray("s5"));
					mListItems = mMainList;

				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				return 0;
			}

			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if (result == null) {
					showToast(R.string.time_out);
				}
				cancelProgressDialog();
				onLaunch();
			}

		}.execute();

	}

	protected ArrayList<IndexTaskInfo> parseGuidesList(ArrayList<IndexTaskInfo> list, JSONArray array) {

		if (array == null) {
			return list;
		}
		for (int i = 0; i < array.length(); i++) {
			IndexTaskInfo info = new IndexTaskInfo();
			JSONObject nObj = array.optJSONObject(i);
			info.setTaskCode(nObj.optInt("taskCode"));//
			info.setTitle(nObj.optString("title"));//
			info.setSubtitle(nObj.optString("subTitle"));//
			info.setTaskId(nObj.optString("taskID"));//
			info.setIcon(nObj.optString("taskIcon"));//
			info.setIsNew(nObj.optInt("taskNew"));//
			info.setRelations(nObj.optInt("taskRelation"));//
			info.setSeq(nObj.optInt("taskSeq"));//
			info.setStatus(nObj.optInt("taskStatus"));//
			info.setTaskTime(nObj.optString("taskTime"));//
			info.setTotal(nObj.optInt("total"));//
			info.setType(nObj.optInt("type"));//
			list.add(info);
		}

		Collections.sort(list, comparator);

		return list;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_guides_health_main;
	}

	@Override
	public View makeView() {
		TextView textView = new TextView(getApplicationContext());
		textView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(Color.parseColor("#f58b00"));
		textView.setTextSize(20);
		textView.getPaint().setFakeBoldText(true);
		return textView;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		loadMainTaskList();
		mBarView.setTitle(getString(R.string.title_guide));
		mAdapter = new IndexTaskAdapter(getApplicationContext());
		mAdapter.setDataSource(mListItems);

		listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(this);

		if (fromWhere == 1) {
			listView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fragment_up_in));
			LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getApplicationContext(), R.anim.fragment_layout_in);
			listView.setLayoutAnimation(controller);
			listView.startLayoutAnimation();
			fromWhere = 0;
		} else if (fromWhere == 2) {
			findViewById(R.id.layout_main).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
		}

	}

	// 退出动画
	// private void exitAnim() {
	// listView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
	// R.anim.fragment_down_out));
	// LayoutAnimationController controller =
	// AnimationUtils.loadLayoutAnimation(getApplicationContext(),
	// R.anim.fragment_layout_out);
	// listView.setLayoutAnimation(controller);
	// listView.startLayoutAnimation();
	// }

	@Override
	public boolean onBackPress() {
		// if (isExitting) {
		// return true;
		// }
		// isExitting = true;
		// exitAnim();
		// new Handler().postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// getActivity().finish();
		// getActivity().overridePendingTransition(R.anim.activity_fade_enter,
		// R.anim.activity_fade_exit);
		// isExitting = false;
		// }
		// }, 800);

		// super.onBackPress();
		FragmentMrg.toBack(getActivity());
		return true;
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		IndexTaskInfo info = mAdapter.getItem(position);

		// if (info.getType() == IndexTaskInfo.JUMP_BROWSE && info.getStatus()
		// == 1) {
		// info.setSeq(1);
		// }
		GuideMrg.getInstance().jumpGuide(this, info);
	}

}
