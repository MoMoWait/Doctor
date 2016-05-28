 package com.comvee.tnb.network;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.ui.exercise.SportRecord;
import com.comvee.tool.UserMrg;
import com.comvee.util.CacheUtil;

/**
 * 运动记录
 * @author friendlove-pc 2015年11月17日10:31:01
 */
public class RecordSportLoader extends TnbBaseNetwork {

	public final static int STATUS_LAST = -10021;
	public final static int STATUS_FIRT_PAGE = -10022;
	private NetworkCallBack callBack;
	private int curPage = 1;
	private int totalPage = 1;
	private final int MAX = 10;
	private String cacheKey = "RecordSportLoader" + UserMrg.DEFAULT_MEMBER.mId;

	public RecordSportLoader(NetworkCallBack callBack) {
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
		putPostValue("order", "desc");
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
		if (resData == null) {
			return null;
		}
		ArrayList<SportRecord> list = new ArrayList<SportRecord>();

		try {
			curPage = resData.getJSONObject("body").getJSONObject("pager").optInt("currentPage", -1) + 1;
			totalPage = resData.getJSONObject("body").getJSONObject("pager").optInt("totalPages", -1);
			JSONObject bodyJsonObject = resData.getJSONObject("body");
			JSONArray jsonArray = bodyJsonObject.getJSONArray("rows");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject item = jsonArray.getJSONObject(i);
				SportRecord sportRecord = new SportRecord();
				sportRecord.imgUrl = item.getString("imgUrl");
				sportRecord.id = item.getString("id");
				sportRecord.inputDt = item.getString("inputDt");
				sportRecord.remark = item.getString("remark");
				sportRecord.sportCalorieId = item.getString("sportCalorieId");
				sportRecord.sportName = item.getString("sportName");
				sportRecord.calorie = item.getString("calorie");
				sportRecord.sportTime = item.getString("sportTime");
				sportRecord.caloriesOneMinutes = item.getString("caloriesOneMinutes");
				list.add(sportRecord);
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
		return ConfigUrlMrg.SPORT_LIST;
	}

	@Override
	protected boolean onStartReady() {

		if (curPage == 1) {
			Object cache = null;
			if ((cache = CacheUtil.getInstance().getObjectById(cacheKey)) != null) {
				postMainThread(STATUS_FIRT_PAGE, cache);
			}
		}

		return super.onStartReady();
	}

}
