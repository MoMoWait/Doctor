package com.comvee.tnb.network;

import org.json.JSONObject;

import android.content.Context;

import com.comvee.FinalDb;

import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.PageModel;
import com.comvee.tnb.ui.ask.AskQuestionParseHelp;

/**
 * 咨询页面获取聊天数据接口 用于获取insertDt之前的20条数据
 * 
 * @author Administrator
 * 
 */
public class AskQuestionRequestListLoader extends TnbBaseNetwork {
	private String url;
	private NetworkCallBack callBack;
	private FinalDb finalDb;
	private Context context;
	private String docId;
	private boolean isLoader = true;

	public AskQuestionRequestListLoader(Context context, NetworkCallBack callBack, FinalDb finalDb, String docId) {
		this.callBack = callBack;
		this.finalDb = finalDb;
		this.context = context;
		this.docId = docId;
	}

	public void starLoader(String insertDt) {
		if (!isLoader) {
			return;
		}
		resetRequestParams();
		url = ConfigUrlMrg.ASK_MSG_LIST;
		putPostValue("page", 1 + "");
		putPostValue("rows", 20 + "");
		putPostValue("doctorId", docId);
		putPostValue("dateTime", insertDt);
		start();
		isLoader = false;
	}

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		isLoader = true;
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
