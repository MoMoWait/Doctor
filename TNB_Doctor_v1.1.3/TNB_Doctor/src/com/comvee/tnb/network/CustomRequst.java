package com.comvee.tnb.network;

import org.json.JSONObject;

import android.widget.Toast;


import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.http.TnbBaseNetwork;
/**
 * 自定义接口请求（用于快速代码请求）
 * @author friendlove-pc
 *
 */
public class CustomRequst extends TnbBaseNetwork {

	private String mUrl;
	private NetworkCallBack mCallback;

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		if (status == SUCCESS) {
			if (null != this.mCallback)
				this.mCallback.callBack(what, status, obj);
		} else {
			Toast.makeText(TNBApplication.getInstance(), obj.toString(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {
		return resData;
	}

	@Override
	public String getUrl() {
		return mUrl;
	}

	public void request(String url, NetworkCallBack callback) {
		this.mCallback = callback;
		this.mUrl = url;
		start();
	}

}
