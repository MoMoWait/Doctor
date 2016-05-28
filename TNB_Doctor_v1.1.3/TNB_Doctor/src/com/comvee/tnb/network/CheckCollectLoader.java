package com.comvee.tnb.network;

import org.json.JSONObject;


import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;

/**
 * 验证电台节目是否已收藏
 * 
 * @author Administrator
 * 
 */
public class CheckCollectLoader extends TnbBaseNetwork {
	private NetworkCallBack callBack;

	public CheckCollectLoader(NetworkCallBack callBack) {
		this.callBack = callBack;
	}

	public void starCheck(String id, String type) {
		resetRequestParams();
		putPostValue("id", id);
		putPostValue("type", type);
		start();

	}

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		try {
			if (callBack != null) {
                callBack.callBack(what, status, obj);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {
		if (getResultCode(resData) == SUCCESS) {
			return resData.optJSONObject("body").optJSONObject("obj").optInt("isCollect");
		}
		return null;
	}

	@Override
	public String getUrl() {
		return ConfigUrlMrg.CHECK_RADIO_COLLECT;
	}

}
