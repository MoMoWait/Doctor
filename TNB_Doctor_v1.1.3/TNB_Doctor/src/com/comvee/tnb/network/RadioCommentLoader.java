package com.comvee.tnb.network;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.RadioComment;
import com.comvee.tnb.model.RadioCommentModel;
import com.comvee.tnb.model.RadioGroup;
import com.comvee.util.JsonHelper;

/**
 * 获取评论列表接口
 * 
 * @author Administrator
 * 
 */
public class RadioCommentLoader extends TnbBaseNetwork {
	private NetworkCallBack callBack;

	private static final int MAX = 30;

	private RadioCommentModel model;
	public static int LOADER_MORE = 2;
	public static int REFRESH = 1;

	public RadioCommentLoader(NetworkCallBack callBack) {
		this.callBack = callBack;
	}

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		if (callBack != null) {
			callBack.callBack(what, status, obj);
		}
	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {
		try {
			model.currentPage = resData.getJSONObject("body").getJSONObject("pager").optInt("currentPage");
			model.totalPages = resData.getJSONObject("body").getJSONObject("pager").optInt("totalPages");

			JSONArray obj = resData.getJSONObject("body").getJSONArray("rows");
			if (obj == null) {
				return null;
			}
			ArrayList<RadioComment> group = new ArrayList<RadioComment>();
			for (int i = 0; i < obj.length(); i++) {
				JSONObject jsonObject = obj.optJSONObject(i);
				RadioComment temp = JsonHelper.getObjecByJsonObject(RadioComment.class, jsonObject);
				if (temp.isHot) {
					model.brilliantCount++;
				}
				group.add(temp);
			}
			model.mList = group;
			return model;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 加载更多
	 * 
	 * @param radioId
	 * @param type
	 */
	public void loadMore(String radioId, String type) {
		if (model == null) {
			model = new RadioCommentModel();
		}
		model.loadType = LOADER_MORE;
		resetRequestParams();
		putPostValue("objId", radioId);
		putPostValue("type", type);
		putPostValue("page", (model.currentPage + 1) + "");
		putPostValue("rows", MAX + "");
		start();	}

	/**
	 * 刷新
	 * 
	 * @param radioId
	 * @param type
	 */
	public void refreshStar(String radioId, String type) {
		model = new RadioCommentModel();
		model.loadType = REFRESH;
		resetRequestParams();
		putPostValue("objId", radioId);
		putPostValue("type", type);
		putPostValue("page", (model.currentPage + 1) + "");
		putPostValue("rows", MAX + "");
		start();
	}

	@Override
	public String getUrl() {
		return ConfigUrlMrg.RADIO_COMMENT;
	}

	interface RadioMainCallBack {
		public void onCallBack(ArrayList<RadioGroup> group, ArrayList<RadioGroup> grou);
	}

}
