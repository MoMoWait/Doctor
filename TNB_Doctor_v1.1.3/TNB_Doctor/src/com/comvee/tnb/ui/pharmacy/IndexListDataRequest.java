package com.comvee.tnb.ui.pharmacy;

import org.json.JSONObject;

import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.network.PostFinishInterface;
import com.comvee.tool.UserMrg;
import com.comvee.util.CacheUtil;

public class IndexListDataRequest extends TnbBaseNetwork {
	private final String cacheKey = "IndexListDataRequest" + UserMrg.DEFAULT_MEMBER.mId;
	private PostFinishInterface postFinishInterface;

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		if (postFinishInterface != null) {
			postFinishInterface.postFinish(status, obj);
		}
	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {
		CacheUtil.getInstance().putObjectById(cacheKey, resData+"");
		return resData;
	}

	@Override
	public String getUrl() {
		return ConfigUrlMrg.DRUG_REMIND_LIST;
	}

	@Override
	protected boolean onStartReady() {
		Object cache = null;
		if ((cache = CacheUtil.getInstance().getObjectById(cacheKey)) != null) {
			postMainThread(0, cache);
		}
		return super.onStartReady();
	}

	public void setPostFinishInterface(PostFinishInterface callback) {
		postFinishInterface = callback;
	}

	public void clearCache(){
		CacheUtil.getInstance().putObjectById(cacheKey, null);
	}

}
