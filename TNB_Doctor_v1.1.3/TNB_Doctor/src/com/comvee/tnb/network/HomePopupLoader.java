package com.comvee.tnb.network;

import java.util.HashMap;

import org.json.JSONObject;

import android.text.style.BackgroundColorSpan;


import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.MyWalletModel;

/**
 * 首页弹窗 数据接口
 * 
 * @author linbin
 * 
 */
public class HomePopupLoader extends TnbBaseNetwork {

	private String url;
	private NetworkCallBack callBack;

	// 调用主线程
	@Override
	protected void onDoInMainThread(int status, Object obj) {
		if (callBack != null) {
			callBack.callBack(what, status, obj);
		}
	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {
		if (resData == null) {
			return null;
		}
		JSONObject body = resData.optJSONObject("body");
		
		MyWalletModel walletModel = new MyWalletModel();
	/*	walletModel.setMoney(body.optString("money"));
		walletModel.setCardCount(body.optString("card_count"));
		walletModel.setMarketUrl(body.optString("market_url"));*/
		return walletModel;
	}

	@Override
	public String getUrl() {
		return url;
	}

	public void load(NetworkCallBack call) {
		this.callBack = call;
		start();
	}

}
