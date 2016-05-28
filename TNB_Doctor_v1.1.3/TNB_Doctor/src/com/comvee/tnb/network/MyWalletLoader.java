package com.comvee.tnb.network;

import org.json.JSONObject;


import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.MyWalletModel;

/**
 * 我的钱包 数据接口
 * 
 * @author yujun
 * 
 */
public class MyWalletLoader extends TnbBaseNetwork {

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
		
		body.optString("money");
		body.optString("card_count");
		String optString = body.optString("market_url");
		
		MyWalletModel walletModel = new MyWalletModel();
		/*walletModel.setMoney(body.optString("money"));
		walletModel.setCardCount(body.optString("card_count"));
		walletModel.setMarketUrl(body.optString("market_url"));*/
		return walletModel;
	}

	@Override
	public String getUrl() {
		return ConfigUrlMrg.MONEY_MY_WALLET;
	}

	public void load(NetworkCallBack call) {
		this.callBack = call;
		start();
	}

}
