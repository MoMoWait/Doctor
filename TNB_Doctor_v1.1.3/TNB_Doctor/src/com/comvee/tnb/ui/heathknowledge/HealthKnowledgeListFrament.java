package com.comvee.tnb.ui.heathknowledge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ImageLoaderUtil;

public class HealthKnowledgeListFrament extends BaseFragment implements OnClickListener, OnItemClickListener, OnHttpListener {
	ListView listView;
	List<Map<String, Object>> listDatas = new ArrayList<Map<String, Object>>();
	String imgUri;
	String titleStr;
	String subSTitleStr;
	SimpleAdapter simpleAdapter;
	ImageView knowledgeIcon;
	TextView knowledgeTitle;
	TextView knowledgeSubTitle;
	private TitleBarView mBarView;

	@Override
	public int getViewLayoutId() {
		return R.layout.healthknowledge_list_frag;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		super.onLaunch();
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		knowledgeIcon = (ImageView) findViewById(R.id.knowledge_ico);
		knowledgeTitle = (TextView) findViewById(R.id.knowledge_title);
		knowledgeSubTitle = (TextView) findViewById(R.id.knowledge_sub_title);

		// 延迟请求数据，避免卡顿 用加载动画做初始显示界面
		if (listDatas.size() == 0) {
			showProgressDialog(getString(R.string.msg_loading));
		}
		handler.sendEmptyMessageDelayed(1, 300);
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			setupView();
		};
	};

	private void setupView() {
		listView = (ListView) findViewById(R.id.listview);
		simpleAdapter = new KnowledgeListAdapter(mContext, listDatas, R.layout.item_health_knowledge, new String[] { "imgUri", "title", "digest" },
				new int[] { R.id.knowledge_ico, R.id.title1, R.id.title2 });
		listView.setAdapter(simpleAdapter);
		listView.setOnItemClickListener(this);
		if (listDatas.size() == 0)
			requestData();
		else {
			simpleAdapter.notifyDataSetChanged();
			fillTtileView();
		}
	}

	private void fillTtileView() {
		ImageLoaderUtil.getInstance(mContext).displayImage(imgUri, knowledgeIcon, ImageLoaderUtil.default_options);
		knowledgeTitle.setText(titleStr);
		knowledgeSubTitle.setText(subSTitleStr);
		mBarView.setTitle(titleStr);

	}

	private void requestData() {
		showProgressDialog(getString(R.string.msg_loading));
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.LOAD_ALL_KNOWLEDGE);
		http.setOnHttpListener(1, this);
		http.startAsynchronous();

	}

	public static BaseFragment newInstance() {
		HealthKnowledgeListFrament frag = new HealthKnowledgeListFrament();
		return frag;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case TitleBarView.ID_RIGHT_BUTTON:
			break;
		default:
			break;
		}

	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		cancelProgressDialog();
		try {
			ComveePacket packet = ComveePacket.fromJsonString(b);
			parsePacket(packet.toString());
		} catch (Exception e) {
			Log.e("tag", e.getMessage(), e);
		}
	}

	private void parsePacket(String packet) {
		try {
			JSONObject jsonObject = new JSONObject(packet);
			JSONObject body = jsonObject.getJSONObject("body");
			imgUri = body.getString("img");
			titleStr = body.getString("newTitle");
			subSTitleStr = body.getString("newSynopsis");
			fillTtileView();
			JSONArray knowledgeList = body.getJSONArray("list");
			for (int i = 0; i < knowledgeList.length(); i++) {
				JSONObject item = knowledgeList.getJSONObject(i);
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("id", item.getString("id"));
				map.put("imgUrl", item.getString("imgUrl"));
				map.put("title", item.getString("title"));
				map.put("digest", item.getString("digest"));
				map.put("subString", item.getJSONArray("subMap"));
				listDatas.add(map);
			}
			simpleAdapter.notifyDataSetChanged();
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		try {

			Bundle bundle = new Bundle();

			JSONArray subJsonArray = (JSONArray) listDatas.get(position).get("subString");
			String subTitle = (String) listDatas.get(position).get("title");
			ArrayList<HashMap<String, String>> subDatas = new ArrayList<HashMap<String, String>>();
			for (int i = 0; i < subJsonArray.length(); i++) {
				JSONObject item = subJsonArray.getJSONObject(i);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("title", item.getString("title"));
				map.put("digest", item.getString("digest"));
				map.put("url", item.getString("url"));
				map.put("id", item.getString("id"));
				subDatas.add(map);
			}
			bundle.putSerializable("subDatas", subDatas);
			bundle.putString("subTitle", subTitle);
			toFragment(HealthKnowledgeSubListFrament.class, bundle, true);
		
		} catch (Exception e) {
		}
	}

}
