package com.comvee.tnb.guides;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.comvee.tnb.model.MyTaskInfo;
import com.comvee.tnb.model.PushInfo;

public class DataParser {

	public static ArrayList<MyTaskInfo> createTaskList(JSONObject json) throws Exception {
		JSONArray taskArray = json.optJSONObject("body").optJSONArray("rows");
		int len = taskArray.length();
		ArrayList<MyTaskInfo> taskList = new ArrayList<MyTaskInfo>();
		for (int i = 0; i < len; i++) {
			JSONObject obj = taskArray.optJSONObject(i);

			MyTaskInfo task = new MyTaskInfo();
			task.defaultRemind = obj.optInt("defaultRemind");
			task.doPercent = obj.optInt("doPercent");
			task.doSuggest = obj.optString("doSuggest");
			task.endDt = obj.optString("endDt");
			task.finishNum = obj.optInt("finishNum");
			task.imgUrl = obj.optString("imgUrl");
			task.insertDt = obj.optString("insertDt");
			task.id = obj.optString("memberJobId");
			task.jobInfo = obj.optString("jobInfo");
			task.jobTitle = obj.optString("jobTitle");
			task.jobType = obj.optInt("jobType");
			task.memberJobId = obj.optString("memberJobId");
			task.totalNum = obj.optInt("jobTotal");
			task.residue = obj.optInt("residue");
			task.status = obj.optInt("status");
			task.doctorId = obj.optLong("doctorId");
			task.doctorName = obj.optString("doctorName");
			task.dateStr = obj.optString("dateStr");
			taskList.add(task);
		}
		return taskList;
	}

	public static UIFoodCycleInfo createFoodCycleInfo(JSONObject obj) throws JSONException {
		UIFoodCycleInfo info = new UIFoodCycleInfo();
		info.setTitlebar(obj.optString("content"));
		info.setCalorie(obj.optInt("calorie"));
		info.setDate(obj.optString("date"));
		info.setDinneradd(obj.optString("dinneradd"));
		info.setBreakfastadd(obj.optString("breakfastadd"));
		info.setLunchadd(obj.optString("lunchadd"));
		info.setType(obj.optInt("type"));
		info.setSeq(obj.optInt("seq"));
		info.setTotal(obj.optInt("total"));
		info.setMsgSeq(obj.optInt("msgseq"));

		ArrayList<String> list = new ArrayList<String>();
		JSONArray array = obj.optJSONArray("breakfast");
		for (int i = 0; i < array.length(); i++) {
			list.add(array.optString(i));
		}
		info.setBreakfast(list);

		list = new ArrayList<String>();
		array = obj.optJSONArray("dinner");
		for (int i = 0; i < array.length(); i++) {
			list.add(array.optString(i));
		}
		info.setDinner(list);

		list = new ArrayList<String>();
		array = obj.optJSONArray("lunch");
		for (int i = 0; i < array.length(); i++) {
			list.add(array.optString(i));
		}
		info.setLunch(list);

		return info;
	}

	public static UIFoodInfo createFoodInfo(String json) throws JSONException {
		if (TextUtils.isEmpty(json) || "{}".equals(json) || (new JSONObject(json).optJSONObject("body")).toString().equals("{}")) {
			return null;
		}
		JSONObject obj = new JSONObject(json);
		UIFoodInfo info = new UIFoodInfo();
		ArrayList<String> list = new ArrayList<String>();
		JSONArray array = obj.optJSONArray("list");
		for (int i = 0; i < array.length(); i++) {
			list.add(array.optString(i));
		}
		info.setTitlebar(obj.optString("titlebar"));
		info.setDesc(obj.optString("desc"));
		info.setHighsport(obj.optString("highsport"));
		info.setLinkTask(obj.optString("linkTask"));
		info.setButton(obj.optString("button"));
		info.setList(list);
		info.setLowsport(obj.optString("lowsport"));
		info.setNormalsport(obj.optString("normalsport"));
		info.setNosport(obj.optString("nosport"));
		info.setTitle(obj.optString("title"));
		return info;

	}

	public static GuideSugarMonitorInfo createBloodSuageInfo(String json) throws JSONException {
		if (TextUtils.isEmpty(json) || "{}".equals(json) || (new JSONObject(json).optJSONObject("body")).toString().equals("{}")) {
			return null;
		}
		GuideSugarMonitorInfo info = new GuideSugarMonitorInfo();
		JSONObject obj = new JSONObject(json).optJSONObject("body");
		info.setBstType(obj.optInt("bstType"));
		info.setContent(obj.optString("content"));
		info.setStarttime(obj.optString("starttime"));
		info.setTitle(obj.optString("title"));
		info.setTitlebar(obj.optString("titlebar"));
		return info;
	}

	public static GuideFoodRecipeInfo createFoodRecipeInfo(String json) throws JSONException {
		if (TextUtils.isEmpty(json) || "{}".equals(json) || (new JSONObject(json).optJSONObject("body")).toString().equals("{}")) {
			return null;
		}

		GuideFoodRecipeInfo info = new GuideFoodRecipeInfo();
		JSONObject obj = new JSONObject(json).optJSONObject("body");

		info.setBreakfast(obj.optString("breakfast"));
		info.setBreakfastadd(obj.optString("breakfastadd"));
		info.setCalorie(obj.optString("calorie"));
		info.setContent(obj.optString("content"));
		info.setDate(obj.optString("date"));
		info.setDesc(obj.optString("desc"));
		info.setDesc1(obj.optString("desc1"));
		info.setDinner(obj.optString("dinner"));
		info.setDinneradd(obj.optString("dinneradd"));
		info.setLunch(obj.optString("lunch"));
		info.setLunchadd(obj.optString("lunchadd"));
		info.setMsgseq(obj.optString("msgseq"));
		info.setNexttasks(obj.optString("nexttasks"));
		info.setPagetype(obj.optString("pagetype"));
		info.setSeq(obj.optString("seq"));
		info.setTotal(obj.optString("total"));
		return info;

	}

	/**
	 * 指导任务的json转换info
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	public static ArrayList<GuideQuesInfo> createAssess(String json) throws JSONException {

		if (TextUtils.isEmpty(json) || "{}".equals(json)) {
			return null;
		}
		JSONObject mObj = new JSONObject(json);
		JSONArray arrObj = mObj.optJSONArray("body");
		JSONObject obj = arrObj.optJSONObject(0);
		ArrayList<GuideQuesInfo> list = new ArrayList<GuideQuesInfo>();
		int len = arrObj.length();
		GuideQuesInfo info;
		for (int i = 0; i < len; i++) {
			obj = arrObj.optJSONObject(i);
			info = new GuideQuesInfo();

			list.add(info);

			info.setHasGoto(obj.optInt("hasGoto"));
			info.setTitleBar(obj.optString("titleBar"));
			info.setTopicIcon(obj.optString("topicIcon"));
			info.setTopicID(obj.optString("topicID"));
			info.setTopicKeyword(obj.optString("topicKeyword"));
			info.setTopicSeq(obj.optInt("topicSeq"));
			info.setTopicType(obj.optInt("topicType"));
			info.setTopicTitle(obj.optString("topicTitle"));
			JSONArray array = obj.optJSONArray("topicItems");
			ArrayList<GuideItemInfo> items = new ArrayList<GuideItemInfo>();
			info.setItems(items);
			int itemcoutn = array.length();
			for (int j = 0; j < itemcoutn; j++) {
				JSONObject item = array.optJSONObject(j);
				GuideItemInfo itemInfo = new GuideItemInfo();

				itemInfo.setDefaultValue(item.optString("defaultValue"));
				itemInfo.setHasGoto(item.optInt("hasGoto"));
				itemInfo.setIfFloat(item.optInt("ifFloat"));
				itemInfo.setItemID(item.optString("itemID"));
				itemInfo.setItemSeq(item.optInt("itemSeq"));
				itemInfo.setItemTitle(item.optString("itemTitle"));
				itemInfo.setItemUnit(item.optString("itemTitle"));
				itemInfo.setItemValue(item.optString("itemValue"));
				itemInfo.setMaxValue(item.optInt("maxValue"));
				itemInfo.setMinValue(item.optInt("minValue"));
				items.add(itemInfo);
			}

			// 坑爹的接口不给处理
			if (info.getTopicType() == GuideQuesInfo.JUMP_CHOOSE_TWO && items.size() == 3) {
				info.setTopicType(GuideQuesInfo.JUMP_CHOOSE_TRHEE);
			}

		}

		return list;
	}

	public static IndexTaskInfo createIndexTaskInfo(JSONObject obj) throws Exception {
		IndexTaskInfo info = new IndexTaskInfo();
		// info.setId(obj.optString("taskID"));
		// info.setIcon(obj.optString("icon"));
		// info.setSeq(obj.optInt("seq"));
		// info.setSubtitle(obj.optString("subtitle"));
		// info.setTaskCode(obj.optInt("taskCode"));
		// info.setTitle(obj.optString("title"));
		// info.setType(obj.optInt("type"));
		// info.setStatus(obj.optInt("status"));
		// info.setDate(obj.optString("tasktime"));
		// info.setTotal(obj.optInt("total"));
		// info.setLinktask(obj.optInt("linktask"));
		// info.setRelations(obj.optInt("relation"));//relations
		// info.setLinktasktype(obj.optInt("linktasktype"));
		// info.setIsNew(obj.optInt("new"));

		// 新修改的
		info.setTaskCode(obj.optInt("taskCode"));//
		info.setTitle(obj.optString("title"));//
		info.setSubtitle(obj.optString("subTitle"));//
		info.setTaskId(obj.optString("taskID"));//
		info.setIcon(obj.optString("taskIcon"));//
		info.setIsNew(obj.optInt("taskNew"));//
		info.setRelations(obj.optInt("taskRelation"));//
		info.setSeq(obj.optInt("taskSeq"));//
		info.setStatus(obj.optInt("taskStatus"));//
		info.setTaskTime(obj.optString("taskTime"));//
		info.setTotal(obj.optInt("total"));//
		info.setType(obj.optInt("type"));//
		return info;
	}

	public static ArrayList<IndexTaskInfo> createIndexTaskInfos(JSONArray json) throws Exception {

		ArrayList<IndexTaskInfo> list = new ArrayList<IndexTaskInfo>();
		JSONObject obj = null;
		int len = json == null ? 0 : json.length();
		// IndexTaskInfoDom dom = IndexTaskInfoDom.getInstance();
		for (int i = 0; i < len; i++) {
			obj = json.optJSONObject(i);
			list.add(createIndexTaskInfo(obj));
		}
		// dom.close();


		return list;

	}

	public static GuideResultInfo createAssessResultInfo(String json) throws Exception {
		GuideResultInfo info = new GuideResultInfo();
		JSONObject obj = new JSONObject(json).optJSONObject("body");
		info.setContent(obj.optString("content"));
		info.setDesc(obj.optString("desc"));
		info.setDesc1(obj.optString("desc1"));
		info.setFirst(obj.optInt("first"));
		info.setLevel(obj.optInt("level"));
		info.setScore(obj.optInt("score"));
		info.setPagetype(obj.optInt("pageType"));
		info.setNexttasks(obj.optString("nextTask"));
		info.setPrevtasks(obj.optString("prevTask"));
		info.setTitleBar(obj.optString("titleBar"));

		return info;
	}

	public static GuideFoodInfo createFoodRecommend(String json) {
		GuideFoodInfo info = new GuideFoodInfo();
		try {
			JSONObject nObj = new JSONObject(json);
			JSONObject obj = nObj.optJSONObject("body");
			info.setDesc(obj.optString("desc"));
			info.setList(obj.optString("list"));
			info.setTitle(obj.optString("title"));
			info.setTitlebar(obj.optString("titlebar"));
			info.setLinkTask(obj.optString("linkTask"));
		} catch (Exception e) {
		}
		return info;
	}

	public static GuideFullCheckInfo createTaskFullCheckInfo(String json) {
		GuideFullCheckInfo info = new GuideFullCheckInfo();
		try {
			JSONObject nObj = new JSONObject(json);
			JSONObject obj = nObj.optJSONObject("body");
			info.setButton(obj.optString("button"));
			info.setDesc(obj.optString("desc"));
			info.setList(obj.optString("list"));
			info.setTitle(obj.optString("title"));
			info.setTitlebar(obj.optString("titlebar"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return info;
	}

	public static GuideBrowseInfo createTaskContentInfo(String json) {
		GuideBrowseInfo info = new GuideBrowseInfo();

		try {

			JSONObject nObj = new JSONObject(json);
			JSONObject obj = nObj.optJSONObject("body");
			info.setLinktask(obj.optString("linkTask"));// 链接的内容
			info.setMsgseq(obj.optInt("msgseq"));//
			info.setMsgtype(obj.optInt("msgtype"));//
			info.setLinktype(obj.optInt("linktype"));//
			info.setSeq(obj.optInt("seq"));//
			info.setUrl(obj.optString("url"));//
			info.setTotal(obj.optInt("total"));
			info.setPageType(obj.optInt("pagetype"));
			info.setDesc(obj.optString("desc"));
			info.setDesc1(obj.optString("desc1"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

	public static String getTodayString() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
		return sdf.format(cal.getTime());
	}

	public static String getTimeString(long milliseconds) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
		return sdf.format(new Date(milliseconds));
	}

	// 百度推送解析
	public static PushInfo createPushInfo(String message) {
		final PushInfo info = new PushInfo();
		try {
			JSONObject obj = new JSONObject(message);
			info.title = obj.optString("title");
			info.decs = obj.optString("description");
			JSONObject json = obj.optJSONObject("custom_content");
			info.busi_type = json.optInt("busi_type");
			info.id = json.optLong("id");
			info.type = json.optInt("type");
			info.pk = json.optString("pk");
			
			info.url = json.optString("url");
			info.memId=json.optString("memId");
			info.sendId=json.optString("id");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

	// /**
	// * 友盟推送解析
	// * @param message
	// * @return
	// */
	// public static PushInfo createPushInfo(String message){
	// final PushInfo info = new PushInfo();
	// try {
	// JSONObject obj = new JSONObject(message);
	// info.ticker = obj.optString("ticker");
	// info.title = obj.optString("title");
	// info.decs = obj.optString("text");
	// JSONObject json = obj.optJSONObject("custom");
	// info.id = json.optLong("id");
	// info.busi_type = json.optInt("busi_type");
	// info.url = json.optString("url");
	//
	// info.play_lights = obj.optBoolean("play_lights");
	// info.play_sound = obj.optBoolean("play_sound");
	// info.play_vibrate = obj.optBoolean("play_vibrate");
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// }
	// return info;
	//
	// }
	
	
}
