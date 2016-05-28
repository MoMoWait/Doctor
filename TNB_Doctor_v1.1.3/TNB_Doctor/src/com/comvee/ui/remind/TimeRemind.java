package com.comvee.ui.remind;

import java.util.Calendar;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.model.PushInfo;
import com.comvee.tnb.ui.index.BringToFrontReceiver;
import com.comvee.tool.UserMrg;

/**
 * 闹钟弹窗
 * 
 * @author Administrator
 * 
 */
public class TimeRemind extends Activity implements OnClickListener {
	private TextView title, value_1, value_2;
	private Button btn_1, btn_2;
	private MediaPlayer mMediaPlayer;
	private WakeLock mWakelock;
	private TimeRemindUtil util;
	private TimeRemindTransitionInfo timeInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remind);
		util = TimeRemindUtil.getInstance(getApplicationContext());
		final Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		Intent intent = this.getIntent();
		timeInfo = (TimeRemindTransitionInfo) intent.getSerializableExtra("timeModel");
		if (timeInfo == null) {
			finish();
			return;
		}
		init(timeInfo.getType());
		if (timeInfo.getType() % 10 == 0 || timeInfo.getType() == 3 || timeInfo.getType() == 5) {
			util.deleateTime(timeInfo.getID());
		}
		setIntent(new Intent());
		handler.sendEmptyMessageDelayed(0, 2 * 60 * 1000);
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				mMediaPlayer.stop();
				break;
			default:
				break;
			}
		};
	};

	private void init(int type) {
		title = (TextView) findViewById(R.id.tv_title);
		value_1 = (TextView) findViewById(R.id.tv_msg_1);
		value_2 = (TextView) findViewById(R.id.tv_msg_2);
		btn_1 = (Button) findViewById(R.id.btn_ok);
		btn_2 = (Button) findViewById(R.id.btn_no);
		btn_1.setOnClickListener(this);
		btn_2.setOnClickListener(this);
		switch (type) {
		case 1:
		case 10:
		case 3:
		case 30:
			title.setText("掌控糖尿病-血糖监测提醒");
			value_1.setText("测血糖时间到啦！");
			value_2.setText("快拿出血糖仪测试一下吧！");
			break;
		case 2:
		case 20:
			title.setText("掌控糖尿病-用药提醒");
			value_1.setText("服药时间到咯");
			if (!timeInfo.getRemark().equals("") && timeInfo.getRemark() != null) {
				value_2.setVisibility(View.VISIBLE);
				value_2.setText(timeInfo.getRemark());
			} else {
				value_2.setVisibility(View.GONE);
			}
			break;

		case 4:
		case 40:
			title.setText("服药时间到咯");
			value_1.setText(timeInfo.getDrugName() + "   " + timeInfo.getDrugUnit());
			if (!TextUtils.isEmpty(timeInfo.getRemark())) {
				value_2.setVisibility(View.VISIBLE);
				value_2.setText("备注: " + timeInfo.getRemark());
			} else {
				value_2.setVisibility(View.GONE);
			}
			break;
		case 5:
			try {
				JSONObject jsonObject = new JSONObject(timeInfo.getRemark());
				UserMrg.setMemberMealClockTime(UserMrg.DEFAULT_MEMBER.mId,0);
//				UserMrg.setMemberCountTimeByMemberId(getApplicationContext(), -1, UserMrg.DEFAULT_MEMBER.mId);
				if (!TextUtils.isEmpty(jsonObject.optString("title"))) {
					value_1.setText(jsonObject.optString("title"));
					btn_1.setText("记录血糖");

				} else {
					finish();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;
		case 0:
			finish();
			break;
		case 6:
			title.setText("直播提醒");
			value_1.setText("您预约的线上活动开始啦");
			value_2.setVisibility(View.GONE);
			btn_1.setVisibility(View.GONE);
			break;
		default:
			break;
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		Uri mediaUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		mMediaPlayer = MediaPlayer.create(getApplicationContext(), mediaUri);
		mMediaPlayer.setLooping(true);
		mMediaPlayer.start();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
	}

	private void toRecordSugger() {
		PushInfo info = new PushInfo();
		info.busi_type = -100100;
		info.title = timeInfo.getRemark();
		info.memId = timeInfo.getMemberId();
		Intent intent = new Intent(BringToFrontReceiver.ACTION_BRING_TO_FRONT);
		intent.putExtras(com.comvee.util.BundleHelper.getBundleByObject(info));
		sendBroadcast(intent);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_no:
			try {
				finish();
			} catch (Exception e) {
			}

			break;
		case R.id.btn_ok:
			if (timeInfo.getType() == 5) {
				toRecordSugger();
			} else {
				addTempRemind();
			}
			try {

				finish();
			} catch (Exception e) {
			}
			break;
		default:
			break;
		}

	}

	/**
	 * 添加临时响铃（五分钟后响铃）
	 */
	private void addTempRemind() {
		Calendar calende = Calendar.getInstance();
		long time = System.currentTimeMillis() + (5 * 60 * 1000);
		calende.setTimeInMillis(time);
		int type2 = timeInfo.getType();
		if (timeInfo.getType() % 10 != 0) {
			type2 = timeInfo.getType() * 10;
		}
		boolean[] week = { true, true, true, true, true, true, true };
		TimeRemindTransitionInfo tempinfo = timeInfo.clone();
		tempinfo.setID(0);
		tempinfo.setHour(calende.get(Calendar.HOUR_OF_DAY));
		tempinfo.setMinute(calende.get(Calendar.MINUTE));
		tempinfo.setWeek(week);
		tempinfo.setType(type2);
		util.addTime(tempinfo);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		if (mWakelock == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");
			mWakelock.acquire();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMediaPlayer.stop();
		mWakelock.release();
		mWakelock = null;
	}

	@Override
	protected void onDestroy() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
		}
		util.star();
		super.onDestroy();

	}
}
