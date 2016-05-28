package com.comvee.tnb.network;

import org.json.JSONObject;

import android.content.Context;


import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;

/**
 * 成员未读消息条数接口 用于刷新侧边栏系统消息 和导航栏医生消息 未读条数
 * 
 * @author Administrator
 * 
 */
public class MemUnReadMsgLoader extends TnbBaseNetwork {
	private String url;
	private NetworkCallBack callBack;
	private Context context;

	public MemUnReadMsgLoader(NetworkCallBack callBack, Context context) {
		this.callBack = callBack;
		this.context = context;
	}

	public void starLoader() {
		url = ConfigUrlMrg.GET_MSG_NUM;
		start(POOL_THREAD_2);
	}

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		if (callBack != null) {
			callBack.callBack(what, status, obj);
		}
	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {
		JSONObject body = resData.optJSONObject("body");
		int msgDocCount = body.optInt("msgDocCount");
		int msgSysCount = body.optInt("msgSysCount");
		ConfigParams.setMsgSysCount(context, msgSysCount);
		ConfigParams.setMsgDocCount(context, msgDocCount);
		return msgDocCount;
	}

	@Override
	public String getUrl() {
		return url;
	}

}
