package com.comvee.tnb.network;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.comvee.ThreadHandler;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.FoodExchangeModel;
import com.comvee.tool.UserMrg;
import com.comvee.util.CacheUtil;
import com.comvee.util.JsonHelper;
/**
 * 食物交换历史接口
 * @author Administrator
 *
 */
public class FoodExchangeHistoryLoader extends TnbBaseNetwork {
	public final static int STATUS_LAST = -10021;
	public final static int STATUS_FIRT_PAGE = -10022;
	private NetworkCallBack callBack;
	private int curPage = 1;
	private int totalPage = 1;
	private final int MAX = 20;
	private String cacheKey = "FoodExchangeHistoryLoader" + UserMrg.getMemberSessionId(TNBApplication.getInstance());

	public FoodExchangeHistoryLoader(NetworkCallBack callBack) {
		this.callBack = callBack;
	}

	public void loadMore() {
		// 判断是否是最后一页了
		if (totalPage == curPage - 1) {
			try {
				callBack.callBack(what, STATUS_LAST, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		resetRequestParams();
		putPostValue("page", curPage + "");
		putPostValue("rows", MAX + "");
		start();
	}

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		if (callBack != null) {
			callBack.callBack(what, curPage - 1 == 1 ? STATUS_FIRT_PAGE : status, obj);
		}
	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {
		ArrayList<FoodExchangeModel> list = new ArrayList<FoodExchangeModel>();
		try {
			curPage = resData.getJSONObject("body").getJSONObject("pager").optInt("currentPage", -1) + 1;
			totalPage = resData.getJSONObject("body").getJSONObject("pager").optInt("totalPages", -1);
			JSONObject bodyJsonObject = resData.getJSONObject("body");
			JSONArray jsonArray = bodyJsonObject.getJSONArray("rows");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject item = jsonArray.getJSONObject(i);
				list.add(JsonHelper.getObjecByJsonObject(FoodExchangeModel.class, item));
			}
			if (curPage - 1 == 1) {// 只缓存第一页
				CacheUtil.getInstance().putObjectById(cacheKey, list);
			} else if (totalPage == 0) {
				CacheUtil.getInstance().putObjectById(cacheKey, null);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public String getUrl() {
		return ConfigUrlMrg.FOOD_EXCHANGE_HISTORY;
	}

	@Override
	protected boolean onStartReady() { 

		if (curPage == 1) {
			Object cache = null;
			if ((cache = CacheUtil.getInstance().getObjectById(cacheKey)) != null) {
				//postMainThread(STATUS_FIRT_PAGE, cache);
				final Object finalCache = cache;
				ThreadHandler.postUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							FoodExchangeHistoryLoader.this.onDoInMainThread(STATUS_FIRT_PAGE, finalCache);
						} catch (Exception var3) {
							var3.printStackTrace();
						}
					}
				});
			}
		}

		return super.onStartReady();
	}

}
