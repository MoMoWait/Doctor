package com.comvee.tnb.network;

import org.json.JSONObject;

import android.widget.Toast;


import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;

public class RadioAdvanceSubmit extends TnbBaseNetwork {

	public String mUrl;
	private NetworkCallBack mCallBack;

	@Override
	protected void onDoInMainThread(int status, Object obj) {

		if (status != SUCCESS) {
			Toast.makeText(TNBApplication.getInstance(), obj.toString(), Toast.LENGTH_SHORT).show();
		} else {
			if (mCallBack != null) {
				mCallBack.callBack(status, what, obj);
			}
		}

	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {
		return null;
	}

	@Override
	public String getUrl() {
		return mUrl;
	}

	public void addRemind(String id, String title, String startime, String timeType, NetworkCallBack mCallBack) {
		mUrl = ConfigUrlMrg.RADIO_ADVANCE_ADD;
		resetRequestParams();
		putPostValue("radioTitle", title);
		putPostValue("startTime", startime);
		putPostValue("foreignId", id);
		putPostValue("timeLevel", timeType );
		this.mCallBack = mCallBack;
		start();

	}

	public void cancleRemind(String id, NetworkCallBack mCallBack) {
		mUrl = ConfigUrlMrg.RADIO_ADVANCE_CANCLE;
		resetRequestParams();
		putPostValue("foreignId", id);
		this.mCallBack = mCallBack;
		start();
	}

}
