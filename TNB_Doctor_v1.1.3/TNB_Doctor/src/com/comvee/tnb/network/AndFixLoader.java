package com.comvee.tnb.network;

import org.json.JSONObject;


import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.AndFixModel;
import com.comvee.util.JsonHelper;

/**
 * 请求判断当前版本是否有补丁包
 * 
 * @author Administrator
 * 
 */
public class AndFixLoader extends TnbBaseNetwork {
	private NetworkCallBack callBack;

	public void loaderStar(NetworkCallBack callBack, String versionCode, String patchCode) {
		this.callBack = callBack;
		putPostValue("ver", versionCode);
		putPostValue("patch_num", patchCode);
		start();
	}

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		if (callBack != null) {
			callBack.callBack(what, status, obj);
		}
	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {
		if (resData == null || resData.optJSONObject("body") == null) {
			return null;
		}

		return JsonHelper.getObjecByJsonObject(AndFixModel.class, resData.optJSONObject("body"));
	}

	@Override
	public String getUrl() {
		return ConfigUrlMrg.GET_ANDFIX_FILE;
	}

}
