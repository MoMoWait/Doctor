package com.comvee.tnb.network;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.comvee.ThreadHandler;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.RadioGroup;
import com.comvee.tnb.model.RadioTurns;
import com.comvee.tnb.radio.RadioUtil;
import com.comvee.util.CacheUtil;
import com.comvee.util.JsonHelper;

/**
 * 电台首页接口
 * 
 * @author Administrator
 * 
 */
public class RadioMainLoader extends TnbBaseNetwork {
	private RadioMainCallBack callBack;
	private boolean isNeedCache;

	@Override
	protected void onDoInMainThread(int status, Object obj) {
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parseResponseJsonData(JSONObject resData) {

		try {
			JSONObject obj = resData.getJSONObject("body").getJSONObject("obj");
			JSONArray array = obj.getJSONArray("newProgramList");
			JSONArray array1 = obj.getJSONArray("turnsList");
			String relationProgramSort = obj.optString("relationProgramSort");
			final int isReg = obj.optInt("isReg");
			final ArrayList<RadioGroup> group = (ArrayList<RadioGroup>) JsonHelper.getListByJsonArray(RadioGroup.class, array);
			final ArrayList<RadioTurns> turns = (ArrayList<RadioTurns>) JsonHelper.getListByJsonArray(RadioTurns.class, array1);
			ThreadHandler.postUiThread(new Runnable() {
				@Override
				public void run() {
					callBack.onCallBack(false, group, turns);
				}
			});

			if (group != null && !group.isEmpty()) {
				CacheUtil.getInstance().putObjectById("RadioGroup", group);
			}

			if (turns != null && !turns.isEmpty()) {
				CacheUtil.getInstance().putObjectById("RadioTurns", turns);
			}
			RadioUtil.setReg(isReg);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void load(boolean isNeedCache, RadioMainCallBack callBack) {
		this.callBack = callBack;
		this.isNeedCache = isNeedCache;
		start();
	}

	@Override
	public String getUrl() {
		return ConfigUrlMrg.RAIOD_MAIN_LIST;
	}

	public interface RadioMainCallBack {
		public void onCallBack(boolean cache, ArrayList<RadioGroup> album, ArrayList<RadioTurns> turns);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean onStartReady() {
		if (this.isNeedCache) {
			final ArrayList<RadioGroup> group = (ArrayList<RadioGroup>) CacheUtil.getInstance().getObjectById("RadioGroup");
			final ArrayList<RadioTurns> turns = (ArrayList<RadioTurns>) CacheUtil.getInstance().getObjectById("RadioTurns");
			if (group != null || turns != null) {
				ThreadHandler.postUiThread(new Runnable() {
					@Override
					public void run() {
						if (null != callBack) {
							callBack.onCallBack(true, group, turns);
						}
					}
				});
			}
		}
		return super.onStartReady();
	}
}
