package com.comvee.tnb.network;

import org.json.JSONObject;


import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.RadioRoom;
import com.comvee.util.JsonHelper;

public class RadioJoinRomm extends TnbBaseNetwork {

	private NetworkCallBack mCallback;

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		if (mCallback != null) {
			mCallback.callBack(status, status, obj);
		}
	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {
		try {
			return JsonHelper.getObjecByJsonObject(RadioRoom.class, resData.optJSONObject("body").optJSONObject("obj"));
		} catch (Exception E) {
			E.printStackTrace();
		}

		return resData;
	}

	@Override
	public String getUrl() {
		return ConfigUrlMrg.RADIO_JOIN_ROOM;
	}

	public void join(String dateTime, String lohasName, String radioId, NetworkCallBack mCallback) {
		putPostValue("dateTime", dateTime);
		putPostValue("lohasName", lohasName);
		putPostValue("radioId", radioId);
		this.mCallback = mCallback;
		start();
	}

}
