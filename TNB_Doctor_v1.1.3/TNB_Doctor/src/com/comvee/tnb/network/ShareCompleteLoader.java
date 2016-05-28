package com.comvee.tnb.network;

import org.json.JSONObject;


import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;

public class ShareCompleteLoader extends TnbBaseNetwork {

	public void starLoader(String id) {
		resetRequestParams();
		putPostValue("id", id);
		start();
	}

	@Override
	protected void onDoInMainThread(int status, Object obj) {

	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {

		return null;
	}

	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return ConfigUrlMrg.SHARE_COMPLETE;
	}

}
