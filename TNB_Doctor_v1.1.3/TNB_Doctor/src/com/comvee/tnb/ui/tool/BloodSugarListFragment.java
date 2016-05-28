package com.comvee.tnb.ui.tool;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.HeatInfo;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tnb.widget.XExpandableListView.IXListViewListener;

public class BloodSugarListFragment extends BaseFragment implements OnHttpListener, IXListViewListener, OnItemClickListener {

	private ListView mListView;
	private ArrayList<HeatInfo> listItems = null;
	private MyAdapter mAdapter;
	// private String giId;
	private HeatInfo info;
	private TitleBarView mBarView;

	public HeatInfo getInfo() {
		return info;
	}

	public void setInfo(HeatInfo info) {
		this.info = info;
	}

	public BloodSugarListFragment() {
	}

	public static BloodSugarListFragment newInstance(HeatInfo info) {
		BloodSugarListFragment frg = new BloodSugarListFragment();
		frg.setInfo(info);
		return frg;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_blood_sugar;
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		init();
	}

	private void init() {
		if (info == null) {
			return;
		}
		mBarView.setTitle(info.name);
		mListView = (ListView) findViewById(R.id.lv_task);
		View header = View.inflate(mContext, R.layout.item_bsugar_header, null);
		mListView.addHeaderView(header);
		mAdapter = new MyAdapter();
		mListView.setAdapter(mAdapter);

		requestBloodSugarList();
	}

	private void requestBloodSugarList() {
		showProgressDialog("请稍候...");

		// ComveeHttp http = new ComveeHttp(getApplicationContext(),
		// "http://192.168.9.101:8080/health/mobile/tool/loadGiFoods");
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.BLOOD_SUGAR);
		http.setPostValueForKey("food_cate", info.cate);
		http.setOnHttpListener(1, this);
		http.startAsynchronous();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		cancelProgressDialog();
		switch (what) {
		case 1:
			parseBloodSugarList(b, fromCache);
			break;
		default:
			break;
		}

	}

	private void parseBloodSugarList(byte[] b, boolean fromCache) {

		try {
			ComveePacket packet = ComveePacket.fromJsonString(b);
			if (packet.getResultCode() == 0) {
				if (listItems == null) {
					listItems = new ArrayList<HeatInfo>();
				} else {
					listItems.clear();
				}

				JSONArray array = packet.getJSONObject("body").getJSONArray("rows");
				int len = array.length();
				for (int i = 0; i < len; i++) {
					JSONObject mJsonObject = array.getJSONObject(i);
					HeatInfo info = new HeatInfo();
					info.name = mJsonObject.getString("foodName");
					info.gi = mJsonObject.getString("foodGi");
					info.attri = mJsonObject.getString("attri");
					listItems.add(info);
				}
				mAdapter.notifyDataSetChanged();

			} else {
				showToast(packet.getResultMsg());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listItems == null ? 0 : listItems.size();
		}

		@Override
		public HeatInfo getItem(int arg0) {
			return listItems.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(), R.layout.item_bsugar, null);
				holder.tvFood = (TextView) convertView.findViewById(R.id.tv_food);
				holder.tvGi = (TextView) convertView.findViewById(R.id.tv_gi);
				holder.tvAttri = (TextView) convertView.findViewById(R.id.tv_attri);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			HeatInfo info = listItems.get(arg0);
			holder.tvFood.setText(info.name);
			holder.tvGi.setText(info.gi);
			holder.tvAttri.setText(info.attri);

			return convertView;
		}

		class ViewHolder {
			TextView tvFood;
			TextView tvGi;
			TextView tvAttri;
		}

	}

	@Override
	public void onFialed(int what, int errorCode) {
		// TODO Auto-generated method stub
		cancelProgressDialog();
		ComveeHttpErrorControl.parseError(getActivity(), errorCode);
	}

}
