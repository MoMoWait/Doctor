package com.comvee.tnb.network;

import org.json.JSONObject;


import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.FoodInfo;
/**
 * 添加新的食物交换
 * @author Administrator
 *
 */
public class FoodExchangeLoader extends TnbBaseNetwork {
	private String url;
	private NetworkCallBack callBack;

	public void deleteFoodHistory(NetworkCallBack callBack, String id) {
		resetRequestParams();
		url = ConfigUrlMrg.DELETE_FOOD_EXCHANGE;
		this.callBack = callBack;
		putPostValue("id", id);
		start();
	}

	public void saveExchange(NetworkCallBack callBack, FoodInfo oldModel, FoodInfo newModel,int msgType) {
		resetRequestParams();
		url = ConfigUrlMrg.ADD_FOOD_EXCHANGE;
		if (oldModel == null || newModel == null) {
			return;
		}
		this.callBack = callBack;
		putPostValue("oldname", oldModel.name);
		putPostValue("oldPICURL", oldModel.imgUrl);
		putPostValue("oldweight", oldModel.weight+"");
		putPostValue("newPICURL", newModel.imgUrl);
		putPostValue("newname", newModel.name);
		putPostValue("newweight", newModel.weight+"");
		putPostValue("heat", oldModel.heat+"");
		putPostValue("msg_type",msgType+"");
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
		return resData.optString("body");
	}

	@Override
	public String getUrl() {
		return url;
	}

}
