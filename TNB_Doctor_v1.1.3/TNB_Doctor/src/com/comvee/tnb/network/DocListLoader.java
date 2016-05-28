package com.comvee.tnb.network;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;


import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.DocDetailModel;
import com.comvee.tnb.model.DoctorPackageInfo;
import com.comvee.tnb.ui.ask.DocListFragment;
import com.comvee.util.JsonHelper;
/**
 * 医生列表接口
 * @author Administrator
 *
 */
public class DocListLoader extends TnbBaseNetwork {
	private String url;
	private NetworkCallBack callBack;
	private int curPage = 0;
	private int rows = 10;
	private int totalPage;
	// 判断是否加载数据 避免因为用户操作快速而加载同样的数据
	private boolean isRequest = true;

	public void starLoader(int fromWhere, String couponId) {

		if (curPage == totalPage && totalPage != 0) {
			callBack.callBack(what, ConfigParams.STATUS_LAST, null);
			return;
		} else if (!isRequest) {
			return;
		}
		resetRequestParams();
		url = ConfigUrlMrg.ASK_DOC_LIST;
		if (fromWhere == DocListFragment.WHERE_VOUCHER) {
			putPostValue("couponId", couponId);
		}
		if (fromWhere == DocListFragment.WHERE_PRIVATE_DOCTOR) {
			putPostValue("consult", "1");
		}
		putPostValue("page", curPage + 1 + "");
		putPostValue("rows", rows + "");
		start();
		isRequest = false;
	}

	public DocListLoader(NetworkCallBack callBack) {
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

	;

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {
		JSONObject body = resData.optJSONObject("body");
		JSONArray list = body.optJSONArray("list");
		curPage = body.optJSONObject("pager").optInt("currentPage");
		totalPage = body.optJSONObject("pager").optInt("totalPages");
		ArrayList<DocDetailModel> listItems = new ArrayList<DocDetailModel>();
		int len = list.length();
		for (int i = 0; i < len; i++) {
			JSONObject obj = list.optJSONObject(i);
			DocDetailModel info = new DocDetailModel();
			info.businessDoctorId = obj.optString("USER_ID");
			info.doctorName = obj.optString("PER_NAME");
			info.photoUrl = obj.optString("PER_PER_REAL_PHOTO") + obj.optString("PER_REAL_PHOTO");
			info.hospitalName = obj.optString("HOS_NAME");
			info.positionName = obj.optString("PER_POSITION");
			info.if_doctor = obj.optInt("if_doctor");
			info.tags = obj.optString("TAGS");
			JSONArray jsonArray = obj.optJSONArray("doctorPackageInfo");
			if (jsonArray != null) {
				ArrayList<DoctorPackageInfo> infos = new ArrayList<DoctorPackageInfo>();
				for (int j = 0; j < jsonArray.length(); j++) {
					if (null != jsonArray.optJSONObject(j)) {
						DoctorPackageInfo tempindo = JsonHelper.getObjecByJsonObject(DoctorPackageInfo.class, jsonArray.optJSONObject(j));
						infos.add(tempindo);
					}
				}
				info.arrayList = infos;
			}
			listItems.add(info);
		}

		return listItems;
	}

	@Override
	public String getUrl() {
		return url;
	}

}
