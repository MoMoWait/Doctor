package com.comvee.tnb.ui.record.diet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.comvee.network.BackgroundTasks;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.ui.record.common.NetPic;

public class DietListDataHelper extends TnbBaseNetwork {
	private PostFinishInterface postFinishInterface;
	private String url;
	private int postTag;
	private List<String> dateList;
	private HashMap<String, List<Diet>> itemHashMap;
	private boolean isLastItem;
	public static int LAST_ITEM = -1;// 最后一条记录
	public static final int POST_HISTORY = 1;// 历史请求
	public static final int POST_COLLECT = 2;// 收藏记录
	public static final int POST_REMOVE_COLLECT = 3;// 收藏记录
	public static final int POST_THREE_MEAL = 4;// 我的三餐列表

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		if (postFinishInterface != null) {
			postFinishInterface.postFinish(isLastItem ? LAST_ITEM : status, obj, postTag);
		}
	}

	public DietListDataHelper(String url, int posttag) {
		this.url = url;
		this.postTag = posttag;
	}

	@Override
	public String getUrl() {
		return url;
	}

	/**
	 * 请求历史饮食记录
	 * 
	 * @param dateList2
	 *            记录日期
	 * @param itemHashMap2
	 *            记录数据
	 */
	public void getHistoryDietData(List<String> dateList2, HashMap<String, List<Diet>> itemHashMap2) {
		final int pageSize = 100;// 每次请求数量
		this.dateList = dateList2;
		this.itemHashMap = itemHashMap2;
		putPostValue("isToday", "0");
		putPostValue("rows", pageSize + "");
		int totalItemCount = 0;
		for (String str : itemHashMap.keySet()) {
			totalItemCount = totalItemCount + itemHashMap.get(str).size();
		}
		putPostValue("page", (totalItemCount / pageSize) + 1 + "");
		this.start();
		//BackgroundTasks.getInstance().push(BackgroundTasks.TASK_NETWORK, this);
	}

	/**
	 * 请求我的三餐数据
	 * 
	 * @param dateList2
	 * @param itemHashMap2
	 */
	public void getCollectDietData(List<String> dateList2, HashMap<String, List<Diet>> itemHashMap2) {
		final int pageSize = 100;// 每次请求数量
		this.dateList = dateList2;
		this.itemHashMap = itemHashMap2;
		putPostValue("rows", pageSize + "");
		putPostValue("type", 5 + "");
		int totalItemCount = 0;
		for (String str : itemHashMap.keySet()) {
			totalItemCount = totalItemCount + itemHashMap.get(str).size();
		}
		putPostValue("page", (totalItemCount / pageSize) + 1 + "");
		this.start();
		//BackgroundTasks.getInstance().push(BackgroundTasks.TASK_NETWORK, this);
	}

	public void addOrRemoveCanteen(Diet diet) {
		putPostValue("id", diet.id);
		putPostValue("type", 5 + "");
		this.start();
		//BackgroundTasks.getInstance().push(BackgroundTasks.TASK_NETWORK, this);
	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {
		try {
			if (postTag == POST_HISTORY) {// 历史饮食
				return getDietFromResponse(resData.getJSONObject("body"));
			} else if (postTag == POST_COLLECT) {// 添加收藏

			} else if (postTag == POST_THREE_MEAL) {// 我的三餐
				return getDietFromResponse(resData.getJSONObject("body"));
			}
		} catch (Exception e) {
			Log.e("tag", e.getMessage(), e);
		}
		return null;
	}

	/**
	 * @param resData
	 * @return
	 */
	private Object getDietFromResponse(JSONObject jsonObject_) throws Exception {
		int remoteTotalCount = jsonObject_.getJSONObject("pager").optInt("totalRows");
		int localTtoalCount = 0;
		for (String str : itemHashMap.keySet()) {
			localTtoalCount = localTtoalCount + itemHashMap.get(str).size();
		}
		if (remoteTotalCount == localTtoalCount) {
			isLastItem = true;
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

		JSONArray jsonArray = null;
		if (postTag == POST_HISTORY) {
			jsonArray = jsonObject_.getJSONArray("rows").getJSONObject(0).getJSONArray("foodList");
		} else {
			jsonArray = jsonObject_.getJSONArray("rows");
		}
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String dateStr = sdf.format(sdf1.parse(jsonObject.optString("sysDt")));
			if (!dateList.contains(dateStr))
				dateList.add(dateStr);

			int period = jsonObject.optInt("period");
			Diet diet = new Diet();
			diet.period = period + "";
			diet.id = jsonObject.optString("folderId");
			diet.name = jsonObject.optString("folderName");
			if (postTag == POST_HISTORY) {
				diet.isCollect = jsonObject.optInt("isCollect");
			} else if (postTag == POST_THREE_MEAL) {// 三餐饮食记录需要手动设置isCollect=1
				diet.isCollect = 1;
			}
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
			diet.netpics = netPics;
			JSONArray sugarArray = jsonObject.getJSONArray("mplms");
			for (int k = 0; k < sugarArray.length(); k++) {
				JSONObject sugarJson = sugarArray.getJSONObject(k);
				if (sugarJson.optString("paramCode").contains("before")) {
					diet.beforeSugarValue = Float.valueOf(sugarJson.optString("value"));
				} else {
					diet.afterSugarValue = Float.valueOf(sugarJson.optString("value"));
				}
			}
			if (itemHashMap.get(dateStr) != null) {
				itemHashMap.get(dateStr).add(diet);
			} else {
				List<Diet> ls = new ArrayList<Diet>();
				ls.add(diet);
				itemHashMap.put(dateStr, ls);
			}
		}
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("dateList", dateList);
		returnMap.put("dietMap", itemHashMap);
		return returnMap;

	}

	public void setPostFinishInterface(PostFinishInterface postFinishInterface) {
		this.postFinishInterface = postFinishInterface;
	}

	public interface PostFinishInterface {
		public void postFinish(int status, Object obj, int tag);
	}

}
