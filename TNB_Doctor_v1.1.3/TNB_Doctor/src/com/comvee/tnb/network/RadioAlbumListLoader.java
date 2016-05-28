package com.comvee.tnb.network;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.RadioGroup.RadioAlbum;
import com.comvee.util.JsonHelper;

/**
 * 
 * @author friendlove-pc 加载专辑列表
 */
public class RadioAlbumListLoader extends TnbBaseNetwork {
	private NetworkCallBack callBack;

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

			ArrayList<RadioAlbum> group = (ArrayList<RadioAlbum>) JsonHelper.getListByJsonArray(RadioAlbum.class, obj);
			return group;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void load(String radioId,String sort, NetworkCallBack callBack) {
		this.callBack = callBack;
		if (sort.equals("1")){
			putPostValue("order","desc");
		}else if (sort.equals("2")){
			putPostValue("order","asc");
		}
		putPostValue("refId", radioId);
		putPostValue("page", "1");
		putPostValue("rows", "100");
		start();
	}

	@Override
	public String getUrl() {
		return ConfigUrlMrg.RAIOD_ALBUM_LIST;
	}

}
