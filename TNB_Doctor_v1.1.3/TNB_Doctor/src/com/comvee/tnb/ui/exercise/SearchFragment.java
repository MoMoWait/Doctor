package com.comvee.tnb.ui.exercise;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.ComveeTask;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.ui.xlistview.XListView.IXListViewListener;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UITool;
import com.comvee.util.BundleHelper;

/**
 * 搜索运动
 * @author Administrator
 *
 */
public class SearchFragment extends BaseFragment implements OnClickListener, OnItemClickListener {
	private EditText titleEditTextView;
	private ImageView imageView;
	private TextView textView;
	private LinearLayout lodinggroup;
	private XListView mListView;
	private SearchAdapter mAdapter;
	private String loadStr;
	ArrayList<Exercise> arrayList;
	private TitleBarView mBarView;
	public SportRecord sportRecord;

	@Override
	public int getViewLayoutId() {
		return R.layout.sport_search_frag;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		setupView();

	}

	@Override
	public void onResume() {
		init();
		super.onResume();
	}

	private void init() {
		mListView = (XListView) findViewById(R.id.list_view_of_search);
		imageView = (ImageView) findViewById(R.id.loadingImageView_of_search);
		textView = (TextView) findViewById(R.id.tv_of_search);
		lodinggroup = (LinearLayout) findViewById(R.id.lin_group_of_search);
		mListView.setPullRefreshEnable(false);
		mListView.setPullLoadEnable(false);
		mAdapter = new SearchAdapter(getApplicationContext());
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				ThreadHandler.postUiThread(new Runnable() {
		            @Override
		            public void run() {
		            	mListView.stopRefresh();mListView.stopLoadMore();
		            }
		        }, 2000);
		    }

			@Override
			public void onLoadMore() {
				ThreadHandler.postUiThread(new Runnable() {
		            @Override
		            public void run() {
		            	mListView.stopLoadMore();mListView.stopRefresh();
		            }
		        }, 2000);
		    
				requestHotSpot(loadStr, false);
			}
		});
		if (arrayList != null) {
			notifySearchList(arrayList);
		} else {
			arrayList = new ArrayList<Exercise>();
		}
		if (loadStr != null && titleEditTextView != null) {
			titleEditTextView.setText(loadStr);
		}
	}

	private void starSearch() {
		mListView.setVisibility(View.GONE);
		lodinggroup.setVisibility(View.VISIBLE);
		imageView.setImageResource(R.drawable.loading_anim);
		AnimationDrawable drawable = (AnimationDrawable) imageView.getDrawable();
		textView.setTextColor(getResources().getColor(R.color.text_color_3));
		textView.setText("正在努力搜索中...");
		drawable.start();
	}

	private void notFindResoult() {
		mListView.setVisibility(View.GONE);
		lodinggroup.setVisibility(View.VISIBLE);
		imageView.setImageResource(R.drawable.task_no_data);
		textView.setTextColor(getResources().getColor(R.color.text_color_1));
		textView.setText("没有找到相关的运动哦~");

	}

	private void setupView() {
		View newTitleView = mBarView.resetLayout(R.layout.health_seach_titleview);
		newTitleView.findViewById(R.id.btn_back).setOnClickListener(this);
		titleEditTextView = (EditText) newTitleView.findViewById(R.id.search_et);
		newTitleView.findViewById(R.id.btn_top_right).setOnClickListener(this);
		UITool.setEditWithClearButton(titleEditTextView, R.drawable.seach_clear);
		UITool.autoOpenInputMethod(mContext, titleEditTextView);
		titleEditTextView.setFocusable(true);
		titleEditTextView.setFocusableInTouchMode(true);
		titleEditTextView.requestFocus();
//		getActivity().getWindow().setSoftInputMode(
//				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

	}

	private void notifySearchList(ArrayList<Exercise> arrayList) {
		mListView.setVisibility(View.VISIBLE);
		lodinggroup.setVisibility(View.GONE);
		mAdapter.setModels(arrayList);
		mAdapter.notifyDataSetChanged();
	}

	public static BaseFragment newInstance() {
		SearchFragment frag = new SearchFragment();
		return frag;
	}

	public static BaseFragment newInstance(SportRecord sportRecord) {
		SearchFragment frag = new SearchFragment();
		frag.sportRecord = sportRecord;
		return frag;
	}

	private void checkSearchStr() {
		loadStr = titleEditTextView.getText().toString().trim();
		if ("".equals(loadStr) || loadStr == null) {
			showToast("请输入搜索关键字");
			return;
		}
		requestHotSpot(loadStr, true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_top_right:
			UITool.closeInputMethodManager(getActivity());
			checkSearchStr();
			break;
		case R.id.btn_back:
			getActivity().onBackPressed();
			break;
		default:
			break;
		}

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	private void requestHotSpot(final String str, boolean isShow) {
		if (isShow) {
			starSearch();
		}
		new ComveeTask<String>() {

			@Override
			protected String doInBackground() {
				ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.SPORT_TYPE);
				http.setPostValueForKey("sportName", str);
				String result = http.startSyncRequestString();
				return result;
			}

			protected void onPostExecute(String result) {
				mListView.stopLoadMore();
				parseList(result);
			};
		}.execute();
	}

	private void parseList(String str) {
		try {
			arrayList.clear();
			ComveePacket packet = ComveePacket.fromJsonString(str);
			if (packet.getResultCode() == 0) {
				JSONObject body = packet.optJSONObject("body");
				JSONArray rows = body.optJSONArray("obj");
				if (rows.length() == 0) {
					notFindResoult();
					return;
				}
				for (int i = 0; i < rows.length(); i++) {
					Exercise model = new Exercise();
					JSONObject object = rows.optJSONObject(i);
					model.caloriesOneMinutes = object.getString("caloriesOneMinutes");
					model.caloriesThirtyMinutes = object.getString("caloriesThirtyMinutes");
					model.id = object.getString("id");
					model.imgUrl = object.getString("imgUrl");
					model.level = object.getString("level");
					model.name = object.getString("name");
					arrayList.add(model);
				}
				notifySearchList(arrayList);
			} else {
				notFindResoult();
				ComveeHttpErrorControl.parseError(getActivity(), packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Exercise exercise = arrayList.get(position - 1);
		Bundle bundle = BundleHelper.getBundleBySerializable(exercise);
		if (sportRecord != null) {
			FragmentMrg.popBackToFragment(getActivity(), UpdateSportFragment.class, bundle);
		} else {
			FragmentMrg.popBackToFragment(getActivity(), CreateSportFragment.class, bundle);
		}
	}
}
