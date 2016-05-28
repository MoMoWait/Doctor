package com.comvee.tnb.network;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.comvee.FinalDb;
import com.comvee.ThreadHandler;
import com.comvee.annotation.sqlite.Transient;

import com.comvee.network.BaseHttpRequest;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.AskListModel;
import com.comvee.tnb.model.AskLoopModel;
import com.comvee.tnb.model.AskServerInfo;
import com.comvee.tnb.model.DocDetailModel;
import com.comvee.tool.UserMrg;
import com.comvee.util.JsonHelper;

/**
 * 咨询首页接口 获取咨询首页数据
 * 
 * @author Administrator
 * 
 */
public class AskIndexConsulLoader extends TnbBaseNetwork {
	public final static int STATUS_LAST = -10021;
	public final static int STATUS_FIRT_PAGE = -10022;
	public final static int STATUS_CATCH = -10023;
	private String url;
	private NetworkCallBack callBack;
	private int curPage = 0;
	private int totalPage = 0;
	private FinalDb finalDb;
	private int rows = 30;
	private String sqlWhere = "memberId='" + UserMrg.DEFAULT_MEMBER.mId + "'order by count desc, insertDt desc";
	@Transient
	private AskListModel model;

	private String cacheKeyLoop = "AskIndexBanner";
	private String cacheKeyRecomend = "AskIndexRecomendDoc";

	public AskIndexConsulLoader(NetworkCallBack callBack, FinalDb db) {
		this.callBack = callBack;
		this.finalDb = db;

		model = new AskListModel();
	}

	public void starLoader(boolean loadMore, int locationRows) {

		url = ConfigUrlMrg.ASK_NEW_SERVER_LIST;
		resetRequestParams();
		putPostValue("page", "1");
		putPostValue("city", ConfigParams.getNowCity(TNBApplication.getInstance()));
		if (loadMore) {
			putPostValue("rows", (locationRows + rows) + "");
		} else {
			putPostValue("rows", (locationRows > rows ? locationRows + 5 : rows) + "");
		}
		start();
	}

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		if (null != callBack) {
			if (totalPage == curPage && totalPage > 0) {
				callBack.callBack(what, STATUS_LAST, obj);
			} else if (curPage == 1) {
				callBack.callBack(what, STATUS_FIRT_PAGE, obj);
			} else {
				callBack.callBack(what, status, obj);
			}
		}
	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {
		try {
			JSONObject body = resData.optJSONObject("body");
			String qusrepository = body.optString("qusrepository");
			if (!TextUtils.isEmpty(qusrepository)) {
				ConfigParams.setExceprionDbUrl(TNBApplication.getInstance(), qusrepository);
			}
			curPage = body.getJSONObject("pager").optInt("currentPage", 0);
			totalPage = body.getJSONObject("pager").optInt("totalPages", 0);
			model.curPage = curPage;
			model.totalPage = totalPage;
			JSONArray array = body.optJSONArray("memMsg");
			ArrayList<AskServerInfo> tempList = new ArrayList<AskServerInfo>();
			for (int i = 0; i < array.length(); i++) {
				AskServerInfo info = JsonHelper.getObjecByJsonObject(AskServerInfo.class, array.optJSONObject(i));
				tempList.add(info);
			}
			JSONArray docList = body.optJSONArray("recommendDoctors");
			ArrayList<DocDetailModel> docDetailModels = new ArrayList<DocDetailModel>();
			for (int i = 0; i < docList.length(); i++) {
				docDetailModels.add(JsonHelper.getObjecByJsonObject(DocDetailModel.class, docList.optJSONObject(i)));
			}
			ArrayList<AskLoopModel> askLoopModels = new ArrayList<AskLoopModel>();
			// JSONArray loopArray = body.optJSONArray("banner");
			// for (int i = 0; i < loopArray.length(); i++) {
			AskLoopModel loopModel = JsonHelper.getObjecByJsonObject(AskLoopModel.class, body.optJSONObject("banner"));
			if (!TextUtils.isEmpty(loopModel.imgUrl)) {
				askLoopModels.add(loopModel);
			}
			// }
			model.docDetailModels = docDetailModels;
			model.askLoopModels = askLoopModels;
			setLocServerList(tempList);
			if (model.askLoopModels != null && model.askLoopModels.size() > 0) {
				ComveeHttp.setCache(TNBApplication.getInstance(), cacheKeyLoop, ConfigParams.DAY_TIME_LONG, resData.toString());
			}
			if (model.docDetailModels != null && model.docDetailModels.size() > 0) {
				ComveeHttp.setCache(TNBApplication.getInstance(), cacheKeyRecomend, ConfigParams.DAY_TIME_LONG, body.optString("qusrepository")
						.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			model.arrayList = (ArrayList<AskServerInfo>) finalDb.findAllByWhere(AskServerInfo.class, sqlWhere);
			return model;
		}
		model.arrayList = (ArrayList<AskServerInfo>) finalDb.findAllByWhere(AskServerInfo.class, sqlWhere);
		return model;
	}

	private void setLocServerList(ArrayList<AskServerInfo> array) {
		finalDb.deleteByWhere(AskServerInfo.class, "memberId='" + UserMrg.DEFAULT_MEMBER.mId + "'");
		for (int i = 0; i < array.size(); i++) {
			finalDb.save(array.get(i));
		}
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	protected boolean onStartReady() {
		try {
			String cache = ComveeHttp.getCache(TNBApplication.getInstance(), cacheKeyLoop);
			String cache1 = ComveeHttp.getCache(TNBApplication.getInstance(), cacheKeyRecomend);
			if (!TextUtils.isEmpty(cache)) {
				ArrayList<AskLoopModel> askLoopModels = new ArrayList<AskLoopModel>();
				AskLoopModel loopModel;
				loopModel = JsonHelper.getObjecByJsonObject(AskLoopModel.class, ComveePacket.fromJsonString(cache).optJSONObject("banner"));
				if (!TextUtils.isEmpty(loopModel.imgUrl)) {
					askLoopModels.add(loopModel);
				}
				if (askLoopModels != null && askLoopModels.size() > 0) {
					model.askLoopModels = askLoopModels;
					//postMainThread(STATUS_CATCH, model);
					ThreadHandler.postUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								AskIndexConsulLoader.this.onDoInMainThread(STATUS_CATCH,model);
							} catch (Exception var3) {
								var3.printStackTrace();
							}
						}
					});
				}
			}
			if (!TextUtils.isEmpty(cache1)) {
				JSONArray docList = ComveePacket.fromJsonString(cache).optJSONArray("recommendDoctors");
				ArrayList<DocDetailModel> docDetailModels = new ArrayList<DocDetailModel>();
				for (int i = 0; i < docList.length(); i++) {
					docDetailModels.add(JsonHelper.getObjecByJsonObject(DocDetailModel.class, docList.optJSONObject(i)));
				}
				if (docDetailModels.size() > 0) {
					model.docDetailModels = docDetailModels;
				}
			}
			if ((model.docDetailModels != null && model.docDetailModels.size() > 0)
					|| (model.askLoopModels != null && model.askLoopModels.size() > 0)) {
				//postMainThread(STATUS_CATCH, model);
				ThreadHandler.postUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							AskIndexConsulLoader.this.onDoInMainThread(STATUS_CATCH,model);
						} catch (Exception var3) {
							var3.printStackTrace();
						}
					}
				});
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}

		return super.onStartReady();
	}
}
