package com.comvee.tnb.ui.record.diet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.FoodInfo;

public class DoctorRecommendRequest extends TnbBaseNetwork {
	private PostFinishInterface postFinishInterface;
	private String url;

	public DoctorRecommendRequest(String url, PostFinishInterface callback) {
		this.url = url;
		this.postFinishInterface = callback;
	}

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		if (postFinishInterface != null) {
			postFinishInterface.postFinish(status, obj);
		}
	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {
		List<List<FoodInfo>> allData = new ArrayList<List<FoodInfo>>();
		try {
			JSONObject bodyJson = resData.getJSONObject("body");
			List<FoodInfo> morningList = new ArrayList<FoodInfo>();
			List<FoodInfo> noonList = new ArrayList<FoodInfo>();
			List<FoodInfo> nightList = new ArrayList<FoodInfo>();
			JSONArray morningJsonArray = bodyJson.getJSONArray("1");
			JSONArray noonJsonArray = bodyJson.getJSONArray("2");
			JSONArray nightJsonArray = bodyJson.getJSONArray("3");
			appendData(morningList, morningJsonArray);
			appendData(noonList, noonJsonArray);
			appendData(nightList, nightJsonArray);
			allData.add(morningList);
			allData.add(noonList);
			allData.add(nightList);
		} catch (Exception e) {
		}

		return allData;
	}

	private void appendData(List<FoodInfo> list, JSONArray jsonArray) throws JSONException {
		DecimalFormat decimalFormat = new DecimalFormat(".0");
		for (int i = 0; i < jsonArray.length(); i++) {
			FoodInfo foodInfo = new FoodInfo();
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			foodInfo.name = jsonObject.optString("name");
			foodInfo.gi = jsonObject.optString("gi");
			foodInfo.heat = (float) jsonObject.optDouble("heat");
			foodInfo.foodId = jsonObject.optString("id");
			foodInfo.imgUrl = jsonObject.optString("picurl");
			foodInfo.weight = (float) jsonObject.optDouble("weight");
			foodInfo.id = jsonObject.optString("type");
			foodInfo.eatAdvice = jsonObject.optString("foodleveltext");
			foodInfo.recommendWeight = jsonObject.optString("recommendWeight");
			float unit = foodInfo.heat / foodInfo.weight;
			decimalFormat.format(unit * Float.parseFloat(foodInfo.recommendWeight));
			String recommendHeat = decimalFormat.format(unit * Float.parseFloat(foodInfo.recommendWeight));
			foodInfo.recommendHeat = recommendHeat;
			list.add(foodInfo);
		}
	}

	@Override
	public String getUrl() {
		return url;
	}

	public interface PostFinishInterface {
		public void postFinish(int status, Object obj);
	}
}
