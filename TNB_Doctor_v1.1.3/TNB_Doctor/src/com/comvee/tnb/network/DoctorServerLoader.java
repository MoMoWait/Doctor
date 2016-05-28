package com.comvee.tnb.network;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;


import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.DoctorModel;
import com.comvee.tnb.model.DoctorServerListModel;
import com.comvee.tnb.model.PackageModel;
import com.comvee.tnb.ui.voucher.VoucherModel;
import com.comvee.util.JsonHelper;

/**
 * 医生拥有的服务接口
 * 
 * @author Administrator
 * 
 */
public class DoctorServerLoader extends TnbBaseNetwork {
	private String url;
	private DoctorServerListModel doctorServerListModel;
	private NetworkCallBack callBack;

	public DoctorServerLoader(NetworkCallBack callBack) {
		this.callBack = callBack;
		doctorServerListModel = new DoctorServerListModel();
	}

	public void starLoader(String docId) {
		url = ConfigUrlMrg.DOC_SERVER_LIST;
		putPostValue("doctorId", docId);
		start();
	}

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		if (callBack != null) {
			callBack.callBack(what, status, obj);
		}
	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {
		JSONObject body = resData.optJSONObject("body");
		JSONObject doctorModel = body.optJSONObject("doctorModel");
		if (doctorModel != null) {
			doctorServerListModel.info = JsonHelper.getObjecByJsonObject(DoctorModel.class, doctorModel);
		}
		doctorServerListModel.info.setMyPackage(body.optBoolean("isMyDoctor"));
		JSONArray packages = body.optJSONArray("package");
		int len = packages.length();
		doctorServerListModel.priList = new ArrayList<PackageModel>();
		for (int i = 0; i < len; i++) {
			JSONObject packageinfo = packages.optJSONObject(i);
			doctorServerListModel.priList.add(parserJSON(packageinfo, 1));
		}
		JSONArray AllDocPackage = body.optJSONArray("AllDocPackage");
		doctorServerListModel.pubList = new ArrayList<PackageModel>();
		for (int i = 0; i < AllDocPackage.length(); i++) {
			JSONObject packageinfo = AllDocPackage.optJSONObject(i);
			doctorServerListModel.pubList.add(parserJSON(packageinfo, 0));
		}
		return doctorServerListModel;
	}

	@SuppressWarnings("unchecked")
	private PackageModel parserJSON(JSONObject packageinfo, int packageType) {
		PackageModel model = new PackageModel();
		if (packageinfo != null) {
			model = JsonHelper.getObjecByJsonObject(PackageModel.class, packageinfo);
			model.setPackageType(packageType);
		}
		JSONArray couponsArray = packageinfo.optJSONArray("couponList");
		if (couponsArray != null && !TextUtils.isEmpty(couponsArray.toString())) {
			model.setVoucherList((ArrayList<VoucherModel>) DataHelper.getVoucherData(couponsArray));
		}
		return model;
	}

	@Override
	public String getUrl() {
		return url;
	}

}
