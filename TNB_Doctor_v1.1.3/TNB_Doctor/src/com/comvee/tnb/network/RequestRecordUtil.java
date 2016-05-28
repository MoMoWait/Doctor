package com.comvee.tnb.network;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.comvee.FinalDb;
import com.comvee.db.sqlite.DbModel;

import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.TendencyPointInfo;
import com.comvee.tnb.ui.record.RecordTendencyFragment;
import com.comvee.util.TimeUtil;

public class RequestRecordUtil extends TnbBaseNetwork {
	private NetworkCallBack callBack;
	private FinalDb db;

	public void request(NetworkCallBack callBack, FinalDb db) {
		this.callBack = callBack;
		this.db = db;
		// if (!getLocCount(db)) {
		// callBack.callBack(0, null);
		// return;
		// }
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 24);
		String endDt = TimeUtil.fomateTime(today.getTimeInMillis(), "yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -3);
		String startDt = TimeUtil.fomateTime(cal.getTimeInMillis(), "yyyy-MM-dd HH:mm:ss");
		putPostValue("startDt", startDt);
		putPostValue("endDt", endDt);
		putPostValue("paramKey", RecordTendencyFragment.createParamString());
		start();
	}

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		callBack.callBack(what, status, obj);
	}

	@Override
	protected Object parseResponseJsonData(JSONObject packet) {
		ArrayList<TendencyPointInfo> infos = null;
		try {
			if (getResultCode(packet) == 0) {
				infos = new ArrayList<TendencyPointInfo>();
				JSONArray array = packet.getJSONArray("body");
				int len = array.length();
				for (int i = 0; i < len; i++) {
					JSONObject obj = array.getJSONObject(i);
					final String code = obj.optString("code");
					JSONArray list = obj.getJSONArray("list");
					int count = list.length();

					for (int j = 0; j < count; j++) {
						JSONObject o = list.getJSONObject(j);
						// info =
						// JsonHelper.getObjecByJsonObject(TendencyPointInfo.class,
						// list.optJSONObject(j));
						TendencyPointInfo info = new TendencyPointInfo();
						info.code = code.trim();
						info.time = o.optString("time");
						info.bloodGlucoseStatus = o.optInt("bloodGlucoseStatus");
						info.value = (float) o.optDouble("value");
						info.type = o.optInt("type");
						info.insertDt = o.optString("insertDt");
						info.id = o.optString("paramLogId");
						if (!TextUtils.isEmpty(info.getTime())) {
							infos.add(info);
							info.time = info.getTime().substring(0, info.getTime().lastIndexOf(":"));
							System.out.println(info.time);
						}
					}
				}
				try {
					db.deleteByWhere(TendencyPointInfo.class, "");
				} catch (Exception e) {
					e.printStackTrace();
				}
				db.saveList(infos);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return infos;
	}

	@Override
	public String getUrl() {
		return ConfigUrlMrg.TENDENCY_POINT_LIST;
	}

	private boolean getLocCount(FinalDb db) {
		TendencyPointInfo inf = new TendencyPointInfo();
		inf.id = -1 + "";
		db.save(inf);
		db.delete(inf);
		DbModel dbModel = db.findDbModelBySQL("select count(*) from TendencyPoint2");
		if (Integer.parseInt((String) dbModel.getDataMap().get("count(*)")) == 0) {
			return true;
		}
		return false;
	}
}
