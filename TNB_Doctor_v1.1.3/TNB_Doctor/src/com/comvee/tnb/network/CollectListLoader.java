package com.comvee.tnb.network;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.CollectItem;
import com.comvee.util.JsonHelper;

/**
 * 电台收藏列表接口
 * 
 * @author Administrator
 * 
 */
public class CollectListLoader extends TnbBaseNetwork {
	private NetworkCallBack callBack;

	public CollectListLoader(NetworkCallBack callBack) {
		this.callBack = callBack;
	}

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		if (callBack != null) {
			callBack.callBack(what, status, obj);
		}
	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {

		try {
			JSONArray obj = resData.getJSONObject("body").getJSONArray("rows");
			ArrayList<CollectItem> group = (ArrayList<CollectItem>) JsonHelper.getListByJsonArray(CollectItem.class, obj);
			return group;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void load() {
		putPostValue("page", "1");
		putPostValue("rows", "1000");
		start();
	}

	@Override
	public String getUrl() {
		return ConfigUrlMrg.RADIO_COLLECT_LOAD;
	}
}
