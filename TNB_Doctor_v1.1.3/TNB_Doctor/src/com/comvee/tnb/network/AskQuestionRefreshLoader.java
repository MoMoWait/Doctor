package com.comvee.tnb.network;

import org.json.JSONObject;

import android.content.Context;

import com.comvee.FinalDb;

import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.PageModel;
import com.comvee.tnb.ui.ask.AskQuestionParseHelp;

/**
 * 咨询页面刷新聊天数据接口   用于获取returnTime之后的最新数据
 * 
 * @author Administrator
 * 
 */
public class AskQuestionRefreshLoader extends TnbBaseNetwork {
	private String url;
	private NetworkCallBack callBack;
	private FinalDb finalDb;
	private Context context;
	private String docId;

	public AskQuestionRefreshLoader(Context context, NetworkCallBack callBack, FinalDb finalDb, String docId) {
		this.callBack = callBack;
		this.finalDb = finalDb;
		this.context = context;
		this.docId = docId;
	}

	public void starLoader(String returnTime) {
		resetRequestParams();
		url = ConfigUrlMrg.REFRES_MSG;
		putPostValue("doctorId", docId);
		putPostValue("returnDate", returnTime);
		putPostValue("rows", 999 + "");
		putPostValue("page", 1 + "");
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
		PageModel model = AskQuestionParseHelp.parseMsgList(context, docId, finalDb, resData);
		return model;
	}

	@Override
	public String getUrl() {
		return url;
	}

}
