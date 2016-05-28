package com.comvee.tnb.ui.remind;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

import com.comvee.tnb.model.AlarmInfo;
import com.comvee.tool.UserMrg;

public class RemindService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		final Context context = getApplicationContext();
		final RemindMrg mrg = RemindMrg.getInstance(context);

		try {

			if (intent != null && RemindMrg.ACTION_REMIND.equalsIgnoreCase(intent.getAction())) {
				try {
//					toRemindAlarm(intent, mrg);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					final long sTime = System.currentTimeMillis();
					mrg.startRemindInfoAlarm();
				}
			}).start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void toRemindAlarm(Intent intent, RemindMrg mrg) {
		
		final Context context = getApplicationContext();
		
		
		
		if (UserMrg.isLoginStatus(getApplicationContext())) {
			return;
		}

		String id = intent.getStringExtra("id");

		AlarmInfo aInfo = mrg.getAlarmInfoById(id);
		aInfo.setStatus(1);
		mrg.updateAlarmInfo(aInfo);

		mrg.startNextAlarm();

		id = aInfo.getId();

		AlarmInfo mInfo = mrg.getAlarmInfoById(id);
		if (mInfo == null) {
			return;
		}

		final boolean allSet = mrg.getRemindSet();
		final boolean cjSet = mrg.getCJRemindSet();
		final boolean ymSet = mrg.getYMRemindSet();
		final boolean otherSet = mrg.getOtherRemindSet();

		final int type = mInfo.getType();
		// 提醒类型 1:产检 2;疫苗 3;自定义
		if (!allSet || (!cjSet && type == 1) || (!ymSet && type == 2) || (!otherSet && type == 3)) {
			return;
		}

		Intent it = new Intent(context, RemindDialogActivity.class);
		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		it.putExtra("obj", mInfo);
		it.putExtra("id", aInfo.getId());
		it.putExtra("title", "温馨提醒");
		it.putExtra("msg", mInfo.getMsg());
		context.startActivity(it);

		playMsgAudio(context);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void playMsgAudio(Context cxt) {
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Ringtone r = RingtoneManager.getRingtone(cxt, notification);
		r.play();
	}

}
