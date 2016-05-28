package com.comvee.tnb.network;

import org.json.JSONObject;


import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;

public class RadioCollectRequest extends TnbBaseNetwork {

	private static final int TASK_COLLECT = 1010010;
	private NetworkCallBack callBack;
	public RadioCollectRequest(NetworkCallBack callBack) {
		this.callBack = callBack;
	}

	/**
	 * 添加收藏
	 * 
	 * @param fontsize
	 */
	public void requestCollect(String type, String id) {
		mUrl = ConfigUrlMrg.RADIO_COLLECT_ADD;
		resetRequestParams();
		putPostValue("type", type);
		putPostValue("id", id);
		start(POOL_THREAD_2);
	}

	/**
	 * 取消收藏
	 * 
	 * @param fontsize
	 */
	public void requestCancleCollect(String requestBody) {
		mUrl = ConfigUrlMrg.RADIO_COLLECT_CANCLE;
		resetRequestParams();
		if ("selectAll".equals(requestBody)) {
			putPostValue("isAll", "1");
		} else {
			putPostValue("id", requestBody);
			putPostValue("isAll", "0");
		}
		start(POOL_THREAD_2);
	}

	private String mUrl;

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		if (callBack != null) {
			callBack.callBack(what, status, obj);
		}
	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {
		return getResultCode(resData);
	}

	@Override
	public String getUrl() {
		return mUrl;
	}

}
