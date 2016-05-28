package com.comvee.tnb.network;

import java.util.ArrayList;

import org.json.JSONObject;

import android.widget.Toast;


import com.comvee.ThreadHandler;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.tnb.model.RadioGroup.RadioAlbum;
import com.comvee.util.JsonHelper;
/**
 * 获取下一首音频
 * @author Administrator
 *
 */
public class RadioPlayNextLoader extends TnbBaseNetwork {

	private CallBack mCallBack;
	private String url;

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		try {
			if (status != SUCCESS) {
				Toast.makeText(TNBApplication.getInstance(), obj.toString(), Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {

		JSONObject obj = resData.optJSONObject("body").optJSONObject("obj");
		final RadioAlbum album = JsonHelper.getObjecByJsonObject(RadioAlbum.class, obj.optJSONObject("album"));
		final ArrayList<RadioAlbumItem> list = (ArrayList<RadioAlbumItem>) JsonHelper.getListByJsonArray(RadioAlbumItem.class,
				obj.optJSONArray("list"));

		ThreadHandler.postUiThread(new Runnable() {
			@Override
			public void run() {
				mCallBack.onCallBack(album, list);
			}
		});

		return null;
	}

	@Override
	public String getUrl() {
		return url;
	}

	public interface CallBack {
		public void onCallBack(RadioAlbum album, ArrayList<RadioAlbumItem> list);
	}

	/**
	 * 
	 * @param id
	 *            栏目id
	 * @param proId
	 *            节目id
	 * @param mCallBack
	 */
	public void loadNext(String id, CallBack mCallBack) {
		this.url = ConfigUrlMrg.RADIO_PLAY_NEXT;
		this.mCallBack = mCallBack;
		resetRequestParams();
		putPostValue("radioId", id);
		start();
	}

	public void loadPro(String id, CallBack callBack) {
		this.url = ConfigUrlMrg.RADIO_PLAY_PRO;
		this.mCallBack = callBack;
		resetRequestParams();
		putPostValue("radioId", id);
		start();
	}
}
