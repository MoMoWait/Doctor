package com.comvee.tnb.ui.heathknowledge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.ui.book.BookWebActivity;
import com.comvee.tnb.widget.TitleBarView;

public class HealthKnowledgeSubListFrament extends BaseFragment implements OnClickListener, OnItemClickListener {
	ListView listView;
	static List<Map<String, String>> listDatas = new ArrayList<Map<String, String>>();
	SimpleAdapter simpleAdapter;
	private String title;
	private TitleBarView mBarView;

	private Class<? extends BaseFragment> backClazz;

	@Override
	public int getViewLayoutId() {
		return R.layout.healthknowledge_sublist_frag;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLaunch(Bundle dataBundle) {
		super.onLaunch(dataBundle);
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		if (dataBundle != null) {
			mBarView.setTitle(dataBundle.getString("subTitle"));
			backClazz = (Class<? extends BaseFragment>) dataBundle.getSerializable("class");
			listDatas = (List<Map<String, String>>) dataBundle.getSerializable("subDatas");
		}
		setupListView();
	}

	public void setTitleStr(String title) {
		this.title = title;
	}

	private void setupListView() {
		listView = (ListView) findViewById(R.id.listview);
		simpleAdapter = new SimpleAdapter(mContext, listDatas, R.layout.item_health_sub_knowledge, new String[] { "title", "digest" }, new int[] {
				R.id.title1, R.id.title2 }) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				View view = super.getView(position, convertView, parent);
				View line1 = view.findViewById(R.id.line1);
				View line2 = view.findViewById(R.id.line2);
				if (position == getCount() - 1) {
					line1.setVisibility(View.GONE);
					line2.setVisibility(View.VISIBLE);
				} else {
					line1.setVisibility(View.VISIBLE);
					line2.setVisibility(View.GONE);
				}

				return view;
			}
		};
		listView.setAdapter(simpleAdapter);
		listView.setOnItemClickListener(this);

	}

	public static Fragment newInstance() {
		HealthKnowledgeSubListFrament frag = new HealthKnowledgeSubListFrament();
		return frag;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String url = (String) listDatas.get(position).get("url");
		// BookWebFragment bFragment = new
		// BookWebFragment(BookWebFragment.FROM_BOOKS, url, (String)
		// listDatas.get(position).get("title"),
		// (String) listDatas.get(position).get("id"));
		// toFragment(bFragment, true, true, true);
		BookWebActivity.toWebActivity(getActivity(), BookWebActivity.BOOKS, null, (String) listDatas.get(position).get("title"), url,
				(String) listDatas.get(position).get("id"));

	}

	@Override
	public boolean onBackPress() {
		if (backClazz != null) {
			FragmentMrg.popBackToFragment(getActivity(), backClazz, null);
			return true;
		}
		return super.onBackPress();
	}
}
