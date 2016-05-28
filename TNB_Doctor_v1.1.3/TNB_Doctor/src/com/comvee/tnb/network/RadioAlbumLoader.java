package com.comvee.tnb.network;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.db.DownloadItemDao;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.tnb.model.RadioGroup;
import com.comvee.util.JsonHelper;

public class RadioAlbumLoader extends TnbBaseNetwork {
	private NetworkCallBack callBack;
	private boolean isNoyeDownload;

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
			int len = obj.length();

			ArrayList<RadioAlbumItem> list = new ArrayList<RadioAlbumItem>();

			for (int i = 0; i < len; i++) {
				JSONObject item = obj.optJSONObject(i);
				RadioAlbumItem album = JsonHelper.getObjecByJsonObject(RadioAlbumItem.class, item);
				if (DownloadItemDao.getInstance().has(album.radioId)) {
					album.locationHas = true;
				}
				list.add(album);
			}

			return list;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	/** 获取专辑节目列表 */
	public void load(String radioId, NetworkCallBack callBack) {
		this.callBack = callBack;
		putPostValue("radioId", radioId);
		start();
	}

	/** 获取还没下载的专辑节目列表 */
	public void loadNoyeDownload(String radioId, NetworkCallBack callBack) {
		this.isNoyeDownload = true;
		this.callBack = callBack;
		putPostValue("radioId", radioId);
		start();
	}

	@Override
	public String getUrl() {
		return ConfigUrlMrg.RAIOD_ALBUM;
	}

	interface RadioMainCallBack {
		public void onCallBack(ArrayList<RadioGroup> group, ArrayList<RadioGroup> grou);
	}

}
