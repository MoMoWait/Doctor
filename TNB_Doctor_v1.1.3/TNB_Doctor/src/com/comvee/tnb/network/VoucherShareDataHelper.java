package com.comvee.tnb.network;

import org.json.JSONObject;


import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.ui.more.VoucherShareModel;
import com.comvee.util.JsonHelper;

public class VoucherShareDataHelper extends TnbBaseNetwork {
	private String url;
	private NetworkCallBack callBack;

	public void requestShareMsg(NetworkCallBack callBack, String promotionId) {
		this.callBack = callBack;
		url = ConfigUrlMrg.GET_VOUCHER_SHARE_MSG;
		putPostValue("promotionId", promotionId);
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
		if (ConfigUrlMrg.GET_VOUCHER_SHARE_MSG.equals(url)) {
			if (resData.optJSONObject("body") == null) {
				return null;
			}
			return JsonHelper.getObjecByJsonObject(VoucherShareModel.class, resData.optJSONObject("body"));
		}
		return null;
	}

	@Override
	public String getUrl() {
		return url;
	}

}
