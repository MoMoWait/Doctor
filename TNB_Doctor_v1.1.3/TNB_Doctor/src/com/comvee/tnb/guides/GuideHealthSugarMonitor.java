package com.comvee.tnb.guides;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;

import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.ui.record.RecordSugarInputNewFrag;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UITool;
import com.comvee.ui.remind.TimeRemindTransitionInfo;
import com.comvee.ui.remind.TimeRemindUtil;
import com.comvee.util.TimeUtil;

public class GuideHealthSugarMonitor extends BaseFragment implements OnClickListener {
	private GuideSugarMonitorInfo mInfo;
	private LinearLayout layoutList;
	private TextView tvTitle;
	private Button btnStart;
	private long remindTime;
	private TimeRemindUtil util;
	private TimeRemindTransitionInfo data = new TimeRemindTransitionInfo();
	private TitleBarView mBarView;

	public void setMonitorInfo(GuideSugarMonitorInfo info) {
		this.mInfo = info;
	}

	public GuideHealthSugarMonitor() {
	}

	public static GuideHealthSugarMonitor newInstance(GuideSugarMonitorInfo info) {
		GuideHealthSugarMonitor frag = new GuideHealthSugarMonitor();
		frag.setMonitorInfo(info);
		return frag;

	}

	@Override
	public int getViewLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.guide_health_sugar_monitor;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		if (mInfo != null) {
			util = TimeRemindUtil.getInstance(getApplicationContext());
			String titleBar = mInfo.getTitlebar();
			mBarView.setTitle(titleBar);
			tvTitle = (TextView) findViewById(R.id.tv_title);
			btnStart = (Button) findViewById(R.id.btn_start);
			btnStart.setOnClickListener(this);

			remindTime = SugarTestMrg.getLastRemindTime(mInfo.getBstType(), mInfo.getStarttime());

			layoutList = (LinearLayout) findViewById(R.id.layout_conent);
			tvTitle.setText(mInfo.getTitle());
			String list = mInfo.getContent();
			// int bstType = mInfo.getBstType();客户端
			// btnClickable(bstType);
			JSONArray array;
			try {
				array = new JSONArray(list);
				for (int i = 0; i < array.length(); i++) {
					addText(array.getString(i));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// String time = mInfo.getStarttime();
			addTextInfo();
		}
	}

	// private void btnClickable(int bstType) {
	// // TODO Auto-generated method stub
	// switch (bstType) {
	// case 1:
	// Log.i("", "立即测试血糖");
	// break;
	// case 2:
	// Log.i("", "");
	// break;
	// case 3:
	// break;
	// case 4:
	// break;
	// case 5:
	// break;
	// case 6:
	// break;
	// case 7:
	// break;
	// default:
	// break;
	// }
	//
	// }

	private void addText(String str) {
		View view = View.inflate(getApplicationContext(), R.layout.guides_result_item, null);
		TextView tv = (TextView) view.findViewById(R.id.tv_text);
		tv.setText(str);
		layoutList.addView(view);
	}

	// 添加血糖监测的时间提醒
	private void addTextInfo() {
		View view = View.inflate(getApplicationContext(), R.layout.guides_bloodsugar_item, null);
		TextView tv = (TextView) view.findViewById(R.id.tv_text);
		TextView tv2 = (TextView) view.findViewById(R.id.tv_text2);
		TextPaint tp = tv.getPaint();
		tp.setFakeBoldText(true);
		TextPaint tp2 = tv2.getPaint();
		tp2.setFakeBoldText(true);

		if (mInfo.getBstType() == 2 || mInfo.getBstType() == 3) {
			tv.setText(getString(R.string.sugar_check_time_after));
		} else if (mInfo.getBstType() == 4) {
			tv.setText(getString(R.string.sugar_check_time_before));
		} else if (mInfo.getBstType() == 7) {
			tv.setText(getString(R.string.sugar_check_time_sleep));
		} else if (mInfo.getBstType() == 6) {
			tv.setText(getString(R.string.sugar_check_time_night));
		}

		// layoutList.addView(view);

		if (remindTime == 0) {
			// UITool.setTextView(getView(), R.id.btn_start, "立即录入");
			// UITool.setTextView(getView(), R.id.tv_text2, "检测时间：现在");
			btnStart.setText(R.string.btn_input_imediately);
			tv2.setText(R.string.sugar_check_time_now);
		} else {
			if (SugarTestMrg.checkIsSetReminded(getApplicationContext(), mInfo.getBstType(), System.currentTimeMillis())) {
				btnStart.setText(R.string.txt_remind_already);
				findViewById(R.id.btn_start).setBackgroundResource(R.drawable.button_gray);
			} else {
				btnStart.setText(R.string.txt_remind_me);
			}
			tv2.setText("检测时间：" + TimeUtil.fomateTime(remindTime, "yyyy年MM月dd日 HH:mm"));
		}

		layoutList.addView(view);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btn_start) {
			if (remindTime == 0) {
				toFragment(RecordSugarInputNewFrag.class, null, true);
			} else {
				if (!SugarTestMrg.checkIsSetReminded(getApplicationContext(), mInfo.getBstType(), System.currentTimeMillis())) {
					createTimeData(remindTime);
					ConfigParams.setInt(getContext(), mInfo.getBstType() + "", remindTime);
					UITool.setTextView(getView(), R.id.btn_start, getString(R.string.txt_remind_already));
					findViewById(R.id.btn_start).setBackgroundResource(R.drawable.button_gray);
				}
			}
		}

	}

	// 将需要响铃的数据存入数据库，用来获取响铃的数据
	private void createTimeData(long dbTime) {
		// TODO Auto-generated method stub
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(dbTime));
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		data.setHour(hour);
		data.setMinute(minute);
		data.setType(3);// 血糖监测,并且设置为提醒列表不可见
		boolean week[] = { true, true, true, true, true, true, true };
		data.setWeek(week);

		data.setDiabolo(true);// 设置为响铃状态
		// db.update(data, "id=" + data.getId());
		util.addTime(data);
		util.star();

	}

	// 返回当前时间，格式：2014-12-24 11:29
	// private String getTime() {
	// Calendar cal = Calendar.getInstance();
	// String str = TimeUtil.fomateTime(cal.getTimeInMillis(),
	// "yyyy-MM-dd HH:mm");
	// return str;
	// }

}
