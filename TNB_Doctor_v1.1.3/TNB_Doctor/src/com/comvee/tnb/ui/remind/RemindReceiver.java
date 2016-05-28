package com.comvee.tnb.ui.remind;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.comvee.tool.UserMrg;

public class RemindReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (UserMrg.isLoginStatus(context)) {//位登入
			return;
		}
//本地闹钟
//		RemindMrg mrg = RemindMrg.getInstance(context);
//		final String action = intent.getAction();
//		System.out.println("action-----" + action + "------");
//		if (RemindMrg.ACTION_REMIND.equals(action)) {
//		} else {
//			if (!RemindMrg.IS_PREPARE_REMINDED) {
//				mrg.startRemindInfoAlarmService();
//			}
//		}

	}

}
