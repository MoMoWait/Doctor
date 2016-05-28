package com.comvee.tnb.ui.index;

import org.json.JSONObject;

import android.content.Context;

import com.comvee.tool.UserMrg;
import com.comvee.ui.remind.TimeRemindTransitionInfo;
import com.comvee.ui.remind.TimeRemindUtil;

public class IndexModel {
	private static final String TN = "user";

	public static void addTempTime(Context ctx, int pendingCode, long miSecond) {
		long time = System.currentTimeMillis() + miSecond;
		TimeRemindTransitionInfo tempinfo = new TimeRemindTransitionInfo();
		tempinfo.setMemberId(UserMrg.DEFAULT_MEMBER.mId);
		String remark = null;

		remark = "监测餐后2小时血糖的时间到咯！";
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("title", remark);
		} catch (Exception e) {
			e.printStackTrace();
		}

		tempinfo.setType(5);
		tempinfo.setRemark(jsonObject.toString());
		TimeRemindUtil.getInstance(ctx).addDisposableAlarm((int) pendingCode, tempinfo, time);
	}
	/**
	 * 开启成员的闹钟
	 */
	public static  void notifyMemberClock(Context ctx) {
		long sustatinTime = UserMrg.getMemberMealClockTime( UserMrg.DEFAULT_MEMBER.mId);
		long deltaTime = (System.currentTimeMillis() - sustatinTime) / 1000;
		// 如果当前成员 之前有设置过闹钟并且没超过2小时则启动
		if (sustatinTime > 0 && deltaTime <= IndexFrag.ALARM_TIME && deltaTime > 0) {
			IndexModel.addTempTime(ctx, IndexFrag.PENDING_CODE, (IndexFrag.ALARM_TIME - deltaTime) * 1000);
		} else {
			UserMrg.setMemberMealClockTime( UserMrg.DEFAULT_MEMBER.mId, 0);
		}
	}
}
