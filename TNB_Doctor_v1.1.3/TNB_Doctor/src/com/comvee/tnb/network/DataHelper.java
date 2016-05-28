package com.comvee.tnb.network;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.ui.voucher.VoucherModel;

public class DataHelper extends TnbBaseNetwork {

	private PostFinishInterface postFinishInterface;
	private String url;
	public int totalNum = 0;
	private int postTag;

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		postFinishInterface.postFinish(status, obj, postTag);
	}

	public DataHelper(String url) {
		this.url = url;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {
		if (resData == null) {
			return null;
		}
		if (ConfigUrlMrg.GET_VOUCHER.equals(url)) {
			try {
				return getVoucherData(resData.optJSONObject("body").getJSONArray("obj"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 卡券列表请求，包括已使用，未使用，已过期
	 * 
	 * @param resData
	 * @return
	 */
	public static Object getVoucherData(JSONArray jsonArray) {
		if (jsonArray == null) {
			return null;
		}
		List<VoucherModel> currentvList = new ArrayList<VoucherModel>();
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				boolean exitElement = false;
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				VoucherModel voucherModel = new VoucherModel();
				voucherModel.from = jsonObject.optString("startDt").split(" ")[0];
				voucherModel.to = jsonObject.optString("endDt").split(" ")[0];
				voucherModel.name = jsonObject.optString("couponName");
				voucherModel.price = jsonObject.optInt("couponAmount");
				voucherModel.couponTemplateId = jsonObject.optString("couponTemplateId");
				voucherModel.useDt = jsonObject.optString("useDt");
				voucherModel.sid = jsonObject.optString("sid");
				voucherModel.promotionId = jsonObject.optString("promotionId");
				for (VoucherModel voucherModel2 : currentvList) {
					if (voucherModel2.couponTemplateId.equals(voucherModel.couponTemplateId)) {
						exitElement = true;
						voucherModel2.totalNum++;
						break;
					}
				}
				if (!exitElement)
					currentvList.add(voucherModel);
			}
			return currentvList;
		} catch (Exception e) {
			Log.e("tag", e.getMessage(), e);
			return null;
		}

	}

	public void setPostFinishInterface(PostFinishInterface postFinishInterface) {
		this.setPostFinishInterface(postFinishInterface, 0);
	}

	public void setPostFinishInterface(PostFinishInterface postFinishInterface, int posttag) {
		this.postFinishInterface = postFinishInterface;
		this.postTag = posttag;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public interface PostFinishInterface {
		public void postFinish(int status, Object obj, int tag);
	}

}
