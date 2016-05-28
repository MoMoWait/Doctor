package com.comvee.tnb.ui.exercise;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.widget.PinnedHeaderListView;
import com.comvee.tnb.widget.PinnedHeaderListView.OnItemClickListener;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.util.BundleHelper;

/**
 * 选择运动
 *
 * @author Administrator
 */
@SuppressLint("ValidFragment")
public class ChooseSportFragment extends BaseFragment implements OnClickListener {

	private List<List<Exercise>> data = new ArrayList<List<Exercise>>();// 请求回来的数据
	private SportRecord sportRecord;// 更新跳转过来的数据

	private PinnedHeaderListView listView;
	private SportAdapter adapter;
	private TitleBarView mBarView;

	public static ChooseSportFragment newInstance() {
		ChooseSportFragment fragment = new ChooseSportFragment();
		return fragment;
	}

	public int getViewLayoutId() {
		return R.layout.choose_sport_fragment;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		if (bundle != null) {
			this.sportRecord = (SportRecord) bundle.get("sportRecord");
		}
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		mBarView.setTitle("运动类型");
		mBarView.setRightButton(R.drawable.jkzs_03, this);

		listView = (PinnedHeaderListView) findViewById(R.id.listview);
		adapter = new SportAdapter(data, getApplicationContext());
		listView.setAdapter(adapter);

		if (data.size() == 0) {
			for (int i = 0; i < 4; i++) {// 轻度运动,轻中强度运动 中强度运动 高强度运动
				data.add(new ArrayList<Exercise>());// 轻度运动
			}
			fetchData();
		}
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onSectionClick(AdapterView<?> adapterView, View view, int section, long id) {

			}

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id) {
				Exercise exercise = data.get(section).get(position);
				if (sportRecord != null) {
					sportRecord.sportCalorieId = exercise.id;
					sportRecord.sportName = exercise.name;
					sportRecord.caloriesOneMinutes = exercise.caloriesOneMinutes;
				} else {
//					Intent mIntent = new Intent(CreateSportFragment.SPORT_ACTION);
//					Bundle bundle = new Bundle();
//					bundle.putSerializable("exercise", (Serializable) (exercise));
//					mIntent.putExtras(bundle);
//					getActivity().sendBroadcast(mIntent);
				}
//				FragmentMrg.toBack(getActivity());
				FragmentMrg.toBack(getActivity(),BundleHelper.getBundleBySerializable(exercise));

			}
		});
	}

	private void fetchData() {
		showProgressDialog(getString(R.string.msg_loading));

		ObjectLoader<Exercise> loader = new ObjectLoader<Exercise>();
		loader.loadArrayByBodyobj(Exercise.class, ConfigUrlMrg.SPORT_TYPE, loader.new CallBack() {
			@Override
			public void onBodyArraySuccess(boolean isFromCache, ArrayList<Exercise> obj) {
				super.onBodyArraySuccess(isFromCache, obj);
								cancelProgressDialog();
				try {
					ArrayList<Exercise> list = (ArrayList<Exercise>) obj;
					for (Exercise item : list) {
						data.get(Integer.parseInt(item.level) - 1).add(item);
					}
				}catch (Exception e){
					e.printStackTrace();
				}
				adapter.notifyDataSetChanged();
			}

			@Override
			public boolean onFail(int status) {
				cancelProgressDialog();
				return super.onFail(status);
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case TitleBarView.ID_RIGHT_BUTTON:
			if (sportRecord != null) {
				toFragment(SearchFragment.newInstance(sportRecord), true, false);
			} else {
				toFragment(SearchFragment.newInstance(), true, false);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onFragmentResult(Bundle data) {
		super.onFragmentResult(data);
		if (data != null) {

		}
	}
}
