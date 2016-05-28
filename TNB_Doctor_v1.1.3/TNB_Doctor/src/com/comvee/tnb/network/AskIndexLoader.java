//package com.comvee.tnb.network;
//
//import java.util.ArrayList;
//import java.util.jar.JarOutputStream;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.text.TextUtils;
//
//import com.comvee.FinalDb;
//
//import com.comvee.tnb.TNBApplication;
//import com.comvee.tnb.config.ConfigParams;
//import com.comvee.tnb.config.ConfigUrlMrg;
//import com.comvee.tnb.http.ComveeHttp;
//import com.comvee.tnb.http.ComveePacket;
//import com.comvee.tnb.http.TnbBaseNetwork;
//import com.comvee.tnb.model.AskIndexAllModel;
//import com.comvee.tnb.model.AskLoopModel;
//import com.comvee.tnb.model.AskServerInfo;
//import com.comvee.tnb.model.DocDetailModel;
//import com.comvee.tool.UserMrg;
//import com.comvee.util.CacheUtil;
//import com.comvee.util.JsonHelper;
//
//public class AskIndexLoader extends TnbBaseNetwork {
//	public final static int STATUS_LAST = -10021;
//	public final static int STATUS_FIRT_PAGE = -10022;
//	public final static int STATUS_CATCH = -10023;
//	public static String GET_CACHE = "cachemsg";
//	private String url;
//	private NetworkCallBack callBack;
//	private int curPage = 0;
//	private int totalPage = 0;
//	private AskIndexAllModel allModel;
//	private int rows = 1;
//	private String cacheKeyLoop = "AskIndexBanner";
//	private String cacheKeyRecomend = "AskIndexRecomendDoc";
//
//	public AskIndexLoader(NetworkCallBack callBack) {
//		this.callBack = callBack;
//		allModel = new AskIndexAllModel();
//	}
//
//	public void starLoader() {
//		if (curPage == totalPage && totalPage != 0) {
//			callBack.callBack(what, STATUS_LAST, null);
//			return;
//		}
//		url = ConfigUrlMrg.ASK_NEW_SERVER_LIST;
//		resetRequestParams();
//		putPostValue("city", ConfigParams.getNowCity(TNBApplication.getInstance()));
//		putPostValue("page", curPage + 1 + "");
//		putPostValue("rows", rows + "");
//		start();
//	}
//
//	@Override
//	protected void onDoInMainThread(int status, Object obj) {
//		if (null != callBack) {
//			if (curPage == 1) {
//				callBack.callBack(what, STATUS_FIRT_PAGE, obj);
//			} else if (totalPage == curPage && totalPage > 0) {
//				callBack.callBack(what, STATUS_LAST, obj);
//			} else {
//				callBack.callBack(what, status, obj);
//			}
//		}
//	}
//
//	@Override
//	protected Object parseResponseJsonData(JSONObject resData) {
//		try {
//			JSONObject body = resData.optJSONObject("body");
//			String qusrepository = body.optString("qusrepository");
//			if (!TextUtils.isEmpty(qusrepository)) {
//				ConfigParams.setExceprionDbUrl(TNBApplication.getInstance(), qusrepository);
//			}
//			curPage = body.getJSONObject("pager").optInt("currentPage", 0);
//			totalPage = body.getJSONObject("pager").optInt("totalPages", 0);
//			JSONArray docList = body.optJSONArray("recommendDoctors");
//			ArrayList<DocDetailModel> docDetailModels = new ArrayList<DocDetailModel>();
//			for (int i = 0; i < docList.length(); i++) {
//				docDetailModels.add(JsonHelper.getObjecByJsonObject(DocDetailModel.class, docList.optJSONObject(i)));
//			}
//			ArrayList<AskLoopModel> askLoopModels = new ArrayList<AskLoopModel>();
//			// JSONArray loopArray = body.optJSONArray("banner");
//			// for (int i = 0; i < loopArray.length(); i++) {
//			AskLoopModel loopModel = JsonHelper.getObjecByJsonObject(AskLoopModel.class, body.optJSONObject("banner"));
//			if (!TextUtils.isEmpty(loopModel.imgUrl)) {
//				askLoopModels.add(loopModel);
//			}
//			// }
//			allModel.docDetailModels = docDetailModels;
//			allModel.askLoopModels = askLoopModels;
//			if (allModel.askLoopModels != null && allModel.askLoopModels.size() > 0) {
//				ComveeHttp.setCache(TNBApplication.getInstance(), cacheKeyLoop, ConfigParams.DAY_TIME_LONG, body.optJSONObject("banner").toString());
//			}
//			if (allModel.docDetailModels != null && allModel.docDetailModels.size() > 0) {
//				ComveeHttp.setCache(TNBApplication.getInstance(), cacheKeyRecomend, ConfigParams.DAY_TIME_LONG, body.optString("qusrepository")
//						.toString());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return allModel;
//		}
//
//		return allModel;
//	}
//
//	@Override
//	protected boolean onStartReady() {
//		try {
//			String cache = ComveeHttp.getCache(TNBApplication.getInstance(), cacheKeyLoop);
//			String cache1 = ComveeHttp.getCache(TNBApplication.getInstance(), cacheKeyRecomend);
//			if (!TextUtils.isEmpty(cache)) {
//				ArrayList<AskLoopModel> askLoopModels = new ArrayList<AskLoopModel>();
//				AskLoopModel loopModel;
//				loopModel = JsonHelper.getObjecByJsonObject(AskLoopModel.class, ComveePacket.fromJsonString(cache).optJSONObject("banner"));
//				if (!TextUtils.isEmpty(loopModel.imgUrl)) {
//					askLoopModels.add(loopModel);
//				}
//				if (askLoopModels != null && askLoopModels.size() > 0) {
//					allModel.askLoopModels = askLoopModels;
//					postMainThread(STATUS_CATCH, allModel);
//				}
//			}
//			if (!TextUtils.isEmpty(cache1)) {
//				JSONArray docList = ComveePacket.fromJsonString(cache).optJSONArray("recommendDoctors");
//				ArrayList<DocDetailModel> docDetailModels = new ArrayList<DocDetailModel>();
//				for (int i = 0; i < docList.length(); i++) {
//					docDetailModels.add(JsonHelper.getObjecByJsonObject(DocDetailModel.class, docList.optJSONObject(i)));
//				}
//				if (docDetailModels.size() > 0) {
//					allModel.docDetailModels = docDetailModels;
//				}
//			}
//			if ((allModel.docDetailModels != null && allModel.docDetailModels.size() > 0)
//					|| (allModel.askLoopModels != null && allModel.askLoopModels.size() > 0)) {
//				postMainThread(STATUS_CATCH, allModel);
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return super.onStartReady();
//	}
//
//	@Override
//	public String getUrl() {
//		return url;
//	}
//
//}
