package com.comvee.tool;

import org.json.JSONObject;

import android.support.v4.app.FragmentActivity;

import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.http.ComveePacket;

public class MainActivityUtil {

	public static int parseMsgNum(FragmentActivity activity, String result) {
		int msgDocCount = 0;
		try {
			ComveePacket packet = ComveePacket.fromJsonString(result);
			if (packet.getResultCode() == 0) {
				JSONObject body = packet.optJSONObject("body");
				msgDocCount = body.optInt("msgDocCount");
				int msgSysCount = body.optInt("msgSysCount");
				ConfigParams.setMsgSysCount(activity, msgSysCount);
				ConfigParams.setMsgDocCount(activity, msgDocCount);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msgDocCount;
	}
}
