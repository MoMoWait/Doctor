package com.comvee.ui.remind;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.comvee.FinalDb;
import com.comvee.db.sqlite.DbModel;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.model.TimeRemindInfo;

/**
 * 本地闹钟 本地数据库存储所有提醒时间，通过遍历数据库，把最近要提醒的时间设置成闹钟，同时将已经过去的时间设置为下一次闹钟时间
 * 
 * @author Administrator
 * 
 */
public class TimeRemindUtil {

	private static TimeRemindUtil instance;
	private FinalDb db;
	private AlarmManager am;
	private PendingIntent pi;
	private Context context;
	public static final String REMIND_ACTION = "com.comvee.ui.remind.ACTION";

	public static TimeRemindUtil getInstance(Context context) {

		if (instance == null) {
			instance = new TimeRemindUtil(context);
		}
		instance.updataTable();
		return instance;

	}

	private void updataTable() {
		DbModel dbModel = db.findDbModelBySQL("SELECT * FROM sqlite_master WHERE type='table' AND name='TimeRemind'");
		if (dbModel != null && ConfigParams.isDeleteRemindTable(context)) {
			db.findDbModelBySQL("alter table   TimeRemind   add   memberId   TEXT");
			db.findDbModelBySQL("alter table   TimeRemind   add   memName   TEXT");
			db.findDbModelBySQL("alter table   TimeRemind   add   drugUnit   TEXT");
			db.findDbModelBySQL("alter table   TimeRemind   add   drugName   TEXT");
			db.findDbModelBySQL("alter table   TimeRemind   add   pmType   long");
			db.findDbModelBySQL("alter table   TimeRemind   add   drugId   long");
			TimeRemindInfo temInfo = new TimeRemindInfo();
			db.save(temInfo);
			db.delete(temInfo);
			ConfigParams.setDeleteRemindTable(context, false);
		} else if (dbModel != null && getRemindTimeList(1).size() != 8) {
			deleteTime("type=1");
			addSugarRemind();

		} else if (dbModel == null) {
			addSugarRemind();
			ConfigParams.setDeleteRemindTable(context, false);
		}
	}

	private void addSugarRemind() {
		boolean[] week = { true, true, true, true, true, true, true };
		addTime(new TimeRemindTransitionInfo(0, week, 2, 30, 1, true, "1_1", false));
		addTime(new TimeRemindTransitionInfo(0, week, 6, 30, 1, true, "1_1", false));
		addTime(new TimeRemindTransitionInfo(0, week, 9, 30, 1, true, "1_1", false));
		addTime(new TimeRemindTransitionInfo(0, week, 10, 00, 1, true, "1_1", false));
		addTime(new TimeRemindTransitionInfo(0, week, 12, 30, 1, true, "1_1", false));
		addTime(new TimeRemindTransitionInfo(0, week, 16, 30, 1, true, "1_1", false));
		addTime(new TimeRemindTransitionInfo(0, week, 20, 30, 1, true, "1_1", false));
		addTime(new TimeRemindTransitionInfo(0, week, 22, 30, 1, true, "1_1", false));
	}

	private TimeRemindUtil(Context context) {
		this.context = context;
		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);// 获得AlarmManager实例
		db = FinalDb.create(context, ConfigParams.DB_NAME);
	}

	/**
	 * 添加新的提醒时间
	 * 
	 * @param info
	 * @return
	 */
	public int addTime(TimeRemindTransitionInfo info) {
		if (info == null) {
			return -1;
		}
		TimeRemindInfo times = getTimeRemindInfo(info);
		db.save(times);
		TimeRemindInfo temp = db.findAllByWhere(TimeRemindInfo.class,
				String.format("time=%s and period='%s' and type", times.getTime(), times.getPeriod())).get(0);
		return temp.getId();
	}

	/**
	 * 添加多条提醒时间
	 * 
	 * @param list
	 */
	public void addTimes(List<TimeRemindTransitionInfo> list) {
		for (int i = 0; i < list.size(); i++) {
			db.save(getTimeRemindInfo(list.get(i)));
		}
	}

	/**
	 * 将用户提醒时间类转换为时间提醒的数据库表
	 * 
	 * @param info
	 * @return
	 */
	private TimeRemindInfo getTimeRemindInfo(TimeRemindTransitionInfo info) {
		TimeRemindInfo remindInfo = new TimeRemindInfo();
		remindInfo.setDiabolo(info.isDiabolo());
		remindInfo.setTemp(info.isTemp());
		remindInfo.setType(info.getType());
		remindInfo.setRemark(info.getRemark());
		remindInfo.setHour(info.getHour());
		remindInfo.setMinute(info.getMinute());
		remindInfo.setTime(getRemindTimeOfMillis(info.getWeek(), info.getHour(), info.getMinute()));
		remindInfo.setPeriod(getWeekString(info.getWeek()));
		remindInfo.setDrugName(info.getDrugName());
		remindInfo.setPmType(info.getPmType());
		remindInfo.setDrugUnit(info.getDrugUnit());
		remindInfo.setId(info.getID());
		remindInfo.setDrugId(info.getDrugId());
		remindInfo.setMemberId(info.getMemberId());
		remindInfo.setMemName(info.getMemName());
		return remindInfo;
	}

	/**
	 * 判断所传的时间（hour minue）下一次响铃的时间毫秒数
	 * 
	 * @param week
	 *            每周响铃的重复日期
	 * @param hour
	 *            响铃时间 小时（24小时制）
	 * @param minute
	 *            响铃时间 分钟（24小时制）
	 * @return
	 */
	private long getRemindTimeOfMillis(boolean[] week, int hour, int minute) {
		Calendar calende = Calendar.getInstance();
		calende.set(Calendar.HOUR_OF_DAY, hour);
		calende.set(Calendar.MINUTE, minute);
		calende.set(Calendar.SECOND, 0);
		calende.set(Calendar.MILLISECOND, 0);

		long remindTime = calende.getTimeInMillis();
		int day = calende.get(Calendar.DAY_OF_WEEK);
		// 如果响铃时间在今天还未到（如： 响铃时间hour=10点 当前系统时间是9点 则响铃时间还未到），则设置这个响铃时间的日期是今天
		if (remindTime > System.currentTimeMillis() && week[day - 1]) {
			remindTime = calende.getTimeInMillis();
		} else {
			// 如果响铃时间已过（如： 响铃时间hour=9点 当前系统时间是10点 则响铃时间已过）则寻找下一次响铃的日期
			for (int i = 0; i < week.length; i++) {
				if (week[(day + i) % 7]) {
					remindTime = remindTime + ((i + 1) * 24 * 60 * 60 * 1000);
					break;
				} else {
					continue;
				}

			}
		}
		return remindTime;
	}

	/**
	 * 转换响铃日期
	 * 
	 * @param week
	 * @return
	 */
	private String getWeekString(boolean[] week) {
		String weekString = "";
		for (int i = 0; i < week.length; i++) {
			if (week[i]) {
				weekString = weekString + "1";
			} else {
				weekString = weekString + "0";
			}
			if (i == 6) {
				break;
			} else {
				weekString = weekString + "/";
			}
		}
		return weekString;
	}

	// public void nextRemindTime() {
	// List<TimeRemindInfo> infos = new ArrayList<TimeRemindInfo>();
	// infos = db.findAllByWhere(TimeRemindInfo.class,
	// " time > 0 order by time");
	//
	// }
	/**
	 * 开始响铃
	 */
	public void star() {
		TimeRemindInfo nextInfo = getNextTimeRemindInfo();
		TimeRemindTransitionInfo info = getTimeRemindTransitionInfo(nextInfo);
		if (info == null) {
			stopRemind();
			return;
		}

		Intent intent = new Intent();
		intent.putExtra("timeModel", info);
		intent.setClass(context, TimeRemind.class);
		pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// intent.setAction(REMIND_ACTION);
		// if (pi == null) {
		// pi = PendingIntent.getBroadcast(context, 0, intent, 0); //
		// 实例化PendingIntent}
		//
		// }
		if (am == null) {
			am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);// 获得AlarmManager实例
		}
		am.set(AlarmManager.RTC_WAKEUP, nextInfo.getTime(), pi);

	}

	private void updataRemindTime() {
		List<TimeRemindInfo> infos = db.findAllByWhere(TimeRemindInfo.class, " time > 0");
		for (int i = 0; i < infos.size(); i++) {
			TimeRemindInfo tempInfo = infos.get(i);
			tempInfo.setTime(getRemindTimeOfMillis(getWeek(tempInfo.getPeriod()), tempInfo.getHour(), tempInfo.getMinute()));
			db.update(tempInfo);
		}
	}

	/**
	 * 获取下一次响铃数据
	 * 
	 * @return
	 */
	private TimeRemindInfo getNextTimeRemindInfo() {
		updataRemindTime();
		TimeRemindInfo info = null;
		List<TimeRemindInfo> infos = db.findAllByWhere(TimeRemindInfo.class, " time > 0 order by time asc");
		for (int i = 0; i < infos.size(); i++) {
			if (infos.get(i) == null) {
				continue;
			}
			if (infos.get(i).isDiabolo()) {
				info = infos.get(i);
				return info;
			}
		}
		return info;
	}

	/**
	 * 通过类型获取提醒时间列表
	 * 
	 * @param type
	 * @return
	 */
	public List<TimeRemindTransitionInfo> getRemindTimeList(int type) {
		if (type == -1) {
			return null;
		}
		List<TimeRemindTransitionInfo> list = new ArrayList<TimeRemindTransitionInfo>();
		List<TimeRemindInfo> infos = new ArrayList<TimeRemindInfo>();
		infos = db.findAllByWhere(TimeRemindInfo.class, "type=" + type);

		for (int i = 0; i < infos.size(); i++) {
			if (infos.get(i) == null) {
				continue;
			}
			list.add(getTimeRemindTransitionInfo(infos.get(i)));
		}
		return list;
	}

	public List<TimeRemindTransitionInfo> getRemindTimeList(String where) {
		List<TimeRemindTransitionInfo> list = new ArrayList<TimeRemindTransitionInfo>();
		List<TimeRemindInfo> infos = new ArrayList<TimeRemindInfo>();
		infos = db.findAllByWhere(TimeRemindInfo.class, where);

		for (int i = 0; i < infos.size(); i++) {
			if (infos.get(i) == null) {
				continue;
			}
			list.add(getTimeRemindTransitionInfo(infos.get(i)));
		}
		return list;
	}

	/**
	 * 获取所有的时间列表
	 * 
	 * @return
	 */
	public List<TimeRemindTransitionInfo> getAllRemindTimeList() {
		List<TimeRemindTransitionInfo> list = new ArrayList<TimeRemindTransitionInfo>();
		List<TimeRemindInfo> infos = new ArrayList<TimeRemindInfo>();
		infos = db.findAll(TimeRemindInfo.class);

		for (int i = 0; i < infos.size(); i++) {
			if (infos.get(i) == null) {
				continue;
			}
			list.add(getTimeRemindTransitionInfo(infos.get(i)));
		}
		return list;
	}

	/**
	 * 根据id获取提醒时间
	 * 
	 * @param id
	 * @return
	 */
	public TimeRemindTransitionInfo getRemindTime(int id) {
		if (id == -1) {
			return null;
		}

		TimeRemindTransitionInfo info = getTimeRemindTransitionInfo(db.findById(id, TimeRemindInfo.class));
		return info;
	}

	/**
	 * 将数据库表类转换为时间提醒类
	 * 
	 * @param timeRemindInfo
	 * @return
	 */
	private TimeRemindTransitionInfo getTimeRemindTransitionInfo(TimeRemindInfo timeRemindInfo) {
		if (timeRemindInfo == null) {
			return null;
		}
		TimeRemindTransitionInfo info = new TimeRemindTransitionInfo();
		info.setDiabolo(timeRemindInfo.isDiabolo());
		info.setTemp(timeRemindInfo.isTemp());
		info.setHour(getTime(timeRemindInfo.getTime(), Calendar.HOUR_OF_DAY));
		info.setMinute(getTime(timeRemindInfo.getTime(), Calendar.MINUTE));
		info.setID(timeRemindInfo.getId());
		info.setWeek(getWeek(timeRemindInfo.getPeriod()));
		info.setType(timeRemindInfo.getType());
		info.setRemark(timeRemindInfo.getRemark());
		info.setDrugName(timeRemindInfo.getDrugName());
		info.setPmType(timeRemindInfo.getPmType());
		info.setDrugUnit(timeRemindInfo.getDrugUnit());
		info.setDrugId(timeRemindInfo.getDrugId());
		info.setMemberId(timeRemindInfo.getMemberId());
		info.setMemName(timeRemindInfo.getMemName());
		info.setNextTime(timeRemindInfo.getTime());
		return info;
	}

	/**
	 * 更新数据库提醒时间
	 * 
	 * @param info
	 */
	public void updataTime(TimeRemindTransitionInfo info) {
		if (info == null) {
			return;
		}
		TimeRemindInfo timeRemindInfo = getTimeRemindInfo(info);
		db.update(timeRemindInfo, "id=" + timeRemindInfo.getId());
		// star();
	}

	public void updataTime(TimeRemindTransitionInfo info, String where) {
		if (TextUtils.isEmpty(where)) {
			return;
		}
		TimeRemindInfo timeRemindInfo = getTimeRemindInfo(info);
		db.update(timeRemindInfo, where);
		// star();
	}

	/**
	 * 删除提醒时间
	 * 
	 * @param info
	 */
	public void deleateTime(TimeRemindTransitionInfo info) {
		if (info == null) {
			return;
		}
		db.deleteByWhere(TimeRemindInfo.class, "id=" + info.getID());
		// star();
	}

	public void deleteTime(String where) {
		if (TextUtils.isEmpty(where)) {
			return;
		}
		db.deleteByWhere(TimeRemindInfo.class, where);
		// star();
	}

	/**
	 * 删除提醒时间
	 * 
	 * @param id
	 */
	public void deleateTime(int id) {
		db.deleteByWhere(TimeRemindInfo.class, "id=" + id);
		// star();
	}

	/**
	 * 获取每周的重复提醒时间
	 * 
	 * @param str
	 * @return
	 */
	private boolean[] getWeek(String str) {
		boolean week[] = new boolean[7];
		String[] strs = str.split("/");
		if (strs.length > 1) {
			for (int i = 0; i < strs.length; i++) {
				week[i] = strs[i].equals("1") ? true : false;
			}
		}

		return week;
	}

	/**
	 * 时间转换
	 * 
	 * @param millis
	 * @param calendarField
	 * @return
	 */
	private int getTime(long millis, int calendarField) {
		Calendar calende = Calendar.getInstance();
		calende.setTimeInMillis(millis);
		return calende.get(calendarField);
	}

	public void stopRemind() {
		Intent intent = new Intent();
		intent.setClass(context, TimeRemind.class);
		pi = PendingIntent.getActivity(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		if (am == null) {
			am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);// 获得AlarmManager实例
		}
		am.cancel(pi);
	}

	public void addDisposableAlarm(int pendingCode, TimeRemindTransitionInfo model, long alarmTime) {
		Intent intent = new Intent();
		intent.putExtra("timeModel", model);
		intent.setClass(context, TimeRemind.class);
		pi = PendingIntent.getActivity(context, pendingCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		if (am == null) {
			am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);// 获得AlarmManager实例
		}
		am.set(AlarmManager.RTC_WAKEUP, alarmTime, pi);
	}

	public void cancleDisposableAlarm(int pendingCode) {
		Intent intent = new Intent();
		intent.setClass(context, TimeRemind.class);
		pi = PendingIntent.getActivity(context, pendingCode, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		if (am == null) {
			am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);// 获得AlarmManager实例
		}
		am.cancel(pi);
	}
}
