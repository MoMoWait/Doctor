package com.comvee.tnb.network;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;


import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.MemberDoctorInfo;
import com.comvee.tool.UserMrg;
import com.comvee.util.JsonHelper;

/**
 * 成员拥有的医生接口
 * 
 * @author Administrator
 * 
 */
public class MemberDoctorListLoader extends TnbBaseNetwork {
	private String url;
	private NetworkCallBack callBack;
	private int curPage = 0;
	private int rows = 20;
	private int totalPage;
	// 判断是否加载数据 避免因为用户操作快速而加载同样的数据
	private boolean isRequest = true;
	private String cacheKey = "memberDoctor" + UserMrg.DEFAULT_MEMBER.mId;

	public void starLoader() {
		if (curPage == totalPage && totalPage != 0) {
			callBack.callBack(what, ConfigParams.STATUS_LAST, null);
			return;
		} else if (!isRequest) {
			return;
		}
		resetRequestParams();
		// putPostValue("page", curPage + 1 + "");
		// putPostValue("rows", rows + "");
		url = ConfigUrlMrg.MEMBER_SERVER;
		start();
		isRequest = false;
	}

	public MemberDoctorListLoader(NetworkCallBack callBack) {
		this.callBack = callBack;
	}

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		isRequest = true;
		if (callBack != null) {
			if (curPage == 1) {
				callBack.callBack(what, ConfigParams.STATUS_FIRT_PAGE, obj);
			} else if (totalPage == curPage && totalPage > 0) {
				callBack.callBack(what, ConfigParams.STATUS_LAST, obj);
			} else {
				callBack.callBack(what, status, obj);
			}
		}
	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {
		ArrayList<MemberDoctorInfo> list = new ArrayList<MemberDoctorInfo>();
		JSONArray array = resData.optJSONObject("body").optJSONArray("list");
		// curPage =
		// resData.optJSONObject("body").optJSONObject("pager").optInt("currentPage");
		// totalPage =
		// resData.optJSONObject("body").optJSONObject("pager").optInt("totalPages ");
		if (array == null) {
			return null;
		}
		int len = array.length();
		for (int i = 0; i < len; i++) {
			JSONObject object = array.optJSONObject(i);
			list.add(JsonHelper.getObjecByJsonObject(MemberDoctorInfo.class, object));
		}
		return list;
	}

	@Override
	public String getUrl() {
		return url;
	}

}
