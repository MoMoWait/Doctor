package com.comvee.tnb.ui.record.diet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.network.PostFinishInterface;
import com.comvee.tnb.ui.record.common.NetPic;
import com.comvee.tool.UserMrg;
import com.comvee.util.CacheUtil;

public class IndexDataHelper extends TnbBaseNetwork {
	private PostFinishInterface postFinishInterface;
	private String url;
	private final String[] beforeLableArr = { "早餐前", "午餐前", "晚餐前" };
	private final String[] afterLableArr = { "早餐后", "午餐后", "晚餐后" };
	private final String[] beforeKeyArr = { "beforeBreakfast", "beforeLunch", "beforeDinner" };
	private final String[] afterKeyArr = { "afterBreakfast", "afterLunch", "afterDinner" };
	private final int DIET_COUNT = 3;
	private final String cacheKey = "IndexDataHelper" + UserMrg.DEFAULT_MEMBER.mId;

	public IndexDataHelper(String url) {
		this.url = url;
	}

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		if (postFinishInterface != null) {
			postFinishInterface.postFinish(status, obj);
		}
	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {
		try {
			HashMap<String, Object> returnData = new HashMap<String, Object>();
			JSONObject jsonObject = resData.optJSONObject("body").getJSONArray("rows").getJSONObject(0);
			returnData.put("diets", getTodayDiet(jsonObject));
			JSONObject urlJson = jsonObject.getJSONArray("noticeList").getJSONObject(0);
			returnData.put("urltitle", urlJson.optString("proclamationTitle"));
			returnData.put("proclamationUrl", urlJson.optString("proclamationUrl"));
			CacheUtil.getInstance().putObjectById(cacheKey, returnData);
			return returnData;

		} catch (Exception e) {
			return null;
		}
	}

	public List<TodayDiet> getTodayDiet(JSONObject jsonObject_) throws Exception {
		JSONObject jsonObjectSugarValues = jsonObject_.getJSONObject("todaySugarLogs");
		JSONArray jsonArrayFood = jsonObject_.getJSONArray("foodList");
		if (jsonArrayFood.length() > 3) {
			throw new Exception();
		}

		List<TodayDiet> todayDiets = new ArrayList<TodayDiet>();

		String beforeSugarValueStr;
		String afterSugarValueStr;
		for (int i = 0; i < DIET_COUNT; i++) {
			TodayDiet todayDiet = new TodayDiet();
			todayDiets.add(todayDiet);
			todayDiet.beforeLabel = beforeLableArr[i];
			todayDiet.afterLabel = afterLableArr[i];

			JSONObject mealTimeBefore = jsonObjectSugarValues.optJSONObject(beforeKeyArr[i]);
			if (mealTimeBefore != null) {
				beforeSugarValueStr = mealTimeBefore.optString("value");
				todayDiet.beforeSugarValue = Float.valueOf(TextUtils.isEmpty(beforeSugarValueStr) ? "0" : beforeSugarValueStr);
			}
			JSONObject mealTimeAfter = jsonObjectSugarValues.optJSONObject(afterKeyArr[i]);
			if (mealTimeAfter != null) {
				afterSugarValueStr = mealTimeAfter.optString("value");
				todayDiet.afterSugarValue = Float.valueOf(TextUtils.isEmpty(afterSugarValueStr) ? "0" : afterSugarValueStr);
			}

		}

		for (int i = 0; i < jsonArrayFood.length(); i++) {
			JSONObject jsonObject = jsonArrayFood.getJSONObject(i);
			int period = jsonObject.optInt("period");
			TodayDiet todayDiet = todayDiets.get(period - 1);
			todayDiet.id = jsonObject.optString("folderId");
			todayDiet.name = jsonObject.optString("folderName");
			todayDiet.period = period + "";
			List<NetPic> netPics = new ArrayList<NetPic>();
			JSONArray picArray = jsonObject.getJSONArray("uploadPics");
			for (int j = 0; j < picArray.length(); j++) {
				JSONObject jsonObject2 = picArray.getJSONObject(j);
				NetPic netPic = new NetPic();

				netPic.picId = jsonObject2.getString("picId");
				netPic.picSmall = jsonObject2.getString("picSmall");
				netPic.picBig = jsonObject2.getString("picBig");
				netPics.add(netPic);
			}
			todayDiet.netpics = netPics;
		}
		return todayDiets;

	}

	@Override
	public String getUrl() {
		return url;
	}

	public void setPostFinishInterface(PostFinishInterface postFinishInterface) {
		this.postFinishInterface = postFinishInterface;
	}

	

	@Override
	protected boolean onStartReady() {
		Object cache = null;
		if ((cache = CacheUtil.getInstance().getObjectById(cacheKey)) != null) {
			postMainThread(0, cache);
		}
		return super.onStartReady();
	}
}
