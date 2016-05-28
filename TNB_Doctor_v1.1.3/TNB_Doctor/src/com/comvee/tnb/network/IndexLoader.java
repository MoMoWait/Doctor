package com.comvee.tnb.network;

import org.json.JSONObject;


import com.comvee.ThreadHandler;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.IndexGuideModelNew;
import com.comvee.tnb.model.IndexSugarInfo;
import com.comvee.tool.UserMrg;
import com.comvee.util.CacheUtil;
import com.comvee.util.JsonHelper;
import com.comvee.util.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 首页接口
 * @author Administrator
 *
 */
public class IndexLoader extends TnbBaseNetwork {

	public String mUrl;
	private NetworkCallBack callBack;
	private String cacheKey = "LoadSugarRecord" + UserMrg.DEFAULT_MEMBER.mId;
	private String cacheKey1 = "LoadTaskGuide" + UserMrg.DEFAULT_MEMBER.mId;

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
		if (ConfigUrlMrg.INDEX_TASK_GUIDE_NEW.equals(mUrl)) {
			if (resData.optJSONObject("body") == null) {
				return null;
			}
			IndexGuideModelNew info = JsonHelper.getObjecByJsonObject(IndexGuideModelNew.class, resData.optJSONObject("body"));
			CacheUtil.getInstance().putObjectById(cacheKey1, info);
			return info;
		} else if (ConfigUrlMrg.INDEX_SUGAR_MSG.equals(mUrl)) {
			if (resData.optJSONObject("body").optJSONObject("log") == null) {
				return null;
			}
			IndexSugarInfo info = JsonHelper.getObjecByJsonObject(IndexSugarInfo.class, resData.optJSONObject("body").optJSONObject("log"));
			CacheUtil.getInstance().putObjectById(cacheKey, info);
			return info;
		}
		return null;
	}

	@Override
	public String getUrl() {
		return mUrl;
	}

	public void startLoadTaskGuide(NetworkCallBack callBack) {
		mUrl = ConfigUrlMrg.INDEX_TASK_GUIDE_NEW;
		this.callBack = callBack;
		start();
	}

	public void startLoadSugarRecord(NetworkCallBack callBack) {
		mUrl = ConfigUrlMrg.INDEX_SUGAR_MSG;
		this.callBack = callBack;
		start();
	}

	@Override
	protected boolean onStartReady() {
		if (ConfigUrlMrg.INDEX_SUGAR_MSG.equals(mUrl)) {
			final IndexSugarInfo info = (IndexSugarInfo) CacheUtil.getInstance().getObjectById(cacheKey);
			if (info != null) {
				//postMainThread(SUCCESS, info);
				ThreadHandler.postUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							IndexLoader.this.onDoInMainThread(SUCCESS,info);
						} catch (Exception var3) {
							var3.printStackTrace();
						}
					}
				});
				return true;
			}
		} else if (ConfigUrlMrg.INDEX_TASK_GUIDE_NEW.equals(mUrl)) {
			final IndexGuideModelNew info = (IndexGuideModelNew) CacheUtil.getInstance().getObjectById(cacheKey1);
			if (info != null) {
				//postMainThread(SUCCESS, info);
				ThreadHandler.postUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							IndexLoader.this.onDoInMainThread(SUCCESS,info);
						} catch (Exception var3) {
							var3.printStackTrace();
						}
					}
				});
				return super.onStartReady();
			}
		}

		return super.onStartReady();
	}

}
