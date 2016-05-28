package com.comvee.tnb.network;

import org.json.JSONObject;


import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.IndexVoucherModel;
import com.comvee.util.JsonHelper;
/**
 * 首页卡券接口
 * @author Administrator
 *
 */
public class IndexRequestUtils extends TnbBaseNetwork {
	private String url;
	private NetworkCallBack callBack;

	public void requestVoucher(NetworkCallBack callBack) {
		resetRequestParams();
		url = ConfigUrlMrg.INDEX_DISCOUNT;
		this.callBack = callBack;
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
		if (resData == null) {
			return null;
		}
		if (ConfigUrlMrg.INDEX_DISCOUNT.equals(url)) {
			if (resData.optJSONObject("body") == null||resData.optJSONObject("body").optJSONArray("list")==null) {
				return resData;
			}
			IndexVoucherModel model = JsonHelper.getObjecByJsonObject(IndexVoucherModel.class, resData.optJSONObject("body"));
			return model;
		}
		return resData;
	}

	@Override
	public String getUrl() {
		return url;
	}

}
