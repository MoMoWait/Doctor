package com.comvee.tnb.ui.pharmacy;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;

import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.ui.xlistview.XListView;

public class SearchMedicinalAsync extends AsyncTask<String, Integer, String> {
	private List<MedicinalModel> listdatas;
	private XListView xListView;
	private MedicinalAdapter medicinalAdapter;
	private Activity act;
	private SearchFragment searchFragment;
	
	private final int PAGE_COUNT = 20;// 每页数目

	public SearchMedicinalAsync(List<MedicinalModel> listdatas, XListView xListView, MedicinalAdapter medicinalAdapter, Activity act,
			SearchFragment bfBaseFragment) {
		super();
		this.act = act;
		this.searchFragment = bfBaseFragment;
		this.listdatas = listdatas;
		this.xListView = xListView;
		this.medicinalAdapter = medicinalAdapter;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	protected String doInBackground(String... params) {
		ComveeHttp http = new ComveeHttp(act.getApplicationContext(), ConfigUrlMrg.MEDICINAL_LIST);
		http.setPostValueForKey("drugName", params[0] + "");
		http.setPostValueForKey("page", listdatas.size() / PAGE_COUNT + 1 + "");
		http.setPostValueForKey("rows", PAGE_COUNT + "");
		String result = http.startSyncRequestString();
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		searchFragment.cancelProgressDialog();
		parseList(result);
	}

	private void parseList(String str) {
		try {
			ComveePacket packet = ComveePacket.fromJsonString(str);
			if (packet.getResultCode() == 0) {
				JSONObject body = packet.optJSONObject("body");
				int totalRows = body.optJSONObject("pager").optInt("totalRows");
				if (totalRows == listdatas.size()) {
					xListView.setPullLoadEnable(false);
				} else {
					xListView.setPullLoadEnable(false);
				}
				JSONArray rows = body.optJSONArray("rows");
				if (rows.length() == 0) {
					// dosomething
					return;
				}
				for (int i = 0; i < rows.length(); i++) {
					MedicinalModel model = new MedicinalModel();
					JSONObject object = rows.optJSONObject(i);
					model.name = object.optString("drugName");
					model.dose = object.optString("dose");
					model.unit = object.getString("unit");
					model.id = object.optLong("id");
					listdatas.add(model);
				}
				//xListView.hideFootView();
			} else {
				ComveeHttpErrorControl.parseError(act, packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			medicinalAdapter.notifyDataSetChanged();
			if (listdatas.size() == 0) {
				searchFragment.notFindResoult();
			} else {
				searchFragment.notifySearchList();
			}
		}
	}
}
