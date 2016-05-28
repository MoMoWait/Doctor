package com.comvee.tnb.ui.remind;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.comvee.FinalDb;
import com.comvee.tnb.R;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.model.AlarmInfo;
import com.comvee.tnb.model.RecommondInfo;
import com.comvee.tnb.model.RemindInfo;
import com.comvee.tool.ResUtil;
import com.comvee.util.TimeUtil;

public class RemindMrg {

	private final String db_name = ConfigParams.DB_NAME;

	public final static String ACTION_REMIND = "ACTION_REMIND";

	public static boolean IS_PREPARE_REMINDED;

	private static RemindMrg instance;

	private SharedPreferences sp;

	private Context mContext;

	private RemindMrg(Context cxt) {
		mContext = cxt.getApplicationContext();

		sp = cxt.getSharedPreferences("remind", 0);

	}

	public static RemindMrg getInstance(Context cxt) {

		if (instance == null) {
			instance = new RemindMrg(cxt);
		}

		return instance;
	}

	// @SuppressWarnings("unchecked")
	public ArrayList<RemindInfo> getRemindInfos() {
		FinalDb db = FinalDb.create(mContext, db_name);
		ArrayList<RemindInfo> infos = (ArrayList<RemindInfo>) db.findAll(RemindInfo.class);
		return infos;
	}

	public ArrayList<RemindInfo> getUnCompleteRemindInfos() {
		FinalDb db = FinalDb.create(mContext, db_name);
		ArrayList<RemindInfo> infos = (ArrayList<RemindInfo>) db.findAllByWhere(RemindInfo.class, "status=0"
				+ " order by julianday(rmdDt) asc");
		return infos;
	}

	public ArrayList<RemindInfo> getRemindInfos(String oid) {
		FinalDb db = FinalDb.create(mContext, db_name);
		final String sql = String.format("oid='%s'", oid);
		ArrayList<RemindInfo> infos = (ArrayList<RemindInfo>) db.findAllByWhere(RemindInfo.class, sql);
		return infos;
	}

	/**
	 * 过滤出的提醒
	 * 
	 * @param completeType
	 *            完成类型0、未完成1、完成3、全部
	 * @param remindType
	 *            提醒类型1:产检 2;疫苗 3;自定义0全部
	 * @throws ParseException
	 */
	public ArrayList<RemindInfo> getRemindInfos(int completeType, int remindType) {
		FinalDb db = FinalDb.create(mContext, db_name);
		String sql = "";
		if (completeType != 3 && remindType != 0) {
			if (completeType == 1) {
				sql = String.format("type=%d AND status=1", remindType);
			} else {
				sql = String.format("type=%d AND status!=1", remindType);
			}
		} else if (completeType == 3) {
			sql = String.format("type=%d", remindType);
		} else {
			if (completeType == 1) {
				sql = "status=1";
			} else {
				sql = "status!=1";
			}
		}
		// List<DbModel> infos =
		// db.findDbModelListBySQL("selete * where "+sql+"order by rmdTimeLong");
		ArrayList<RemindInfo> infos = (ArrayList<RemindInfo>) db.findAllByWhere(RemindInfo.class, sql
				+ " order by julianday(rmdDt) asc");
		return infos;
	}

	public AlarmInfo getAlarmInfoById(String id) {
		FinalDb db = FinalDb.create(mContext, db_name);
		return (AlarmInfo) db.findById(id, AlarmInfo.class);
	}

	public void updateAlarmInfo(AlarmInfo info) {
		FinalDb db = FinalDb.create(mContext, db_name);
		db.update(info);
	}

	public boolean isAfterRemind(RemindInfo info) {

		RemindInfo lastInfo = getLast_CJ_or_YM_RemindInfo();
		if (lastInfo == null) {
			return false;
		} else {
			return lastInfo.getId().equals(info.getId());
		}
	}

	private RemindInfo getLast_CJ_or_YM_RemindInfo() {
		FinalDb db = FinalDb.create(mContext, db_name);
		ArrayList<RemindInfo> infos = (ArrayList<RemindInfo>) db.findAllByWhere(RemindInfo.class,
				"type!=3 order by julianday(rmdDt) desc");
		if (infos == null || infos.size() == 0) {
			return null;
		} else {
			return infos.get(0);
		}
	}

	public void setCompleteAll_CJ_YM() {
		sp.edit().putBoolean("complete_all", true).commit();
	}

	public boolean isCompleteAll_CJ_YM() {
		return sp.getBoolean("complete_all", false);
	}

	public void setCompleteAll_CJ_YM_Time(long time) {
		sp.edit().putLong("complete_all_time", time).commit();
	}

	public long getCompleteAll_CJ_YM_Time() {
		return sp.getLong("complete_all_time", System.currentTimeMillis());
	}

	public void addRemind(RemindInfo info) {
		FinalDb db = FinalDb.create(mContext, db_name);
		db.delete(info);
		db.save(info);
	}

	public void updateRemind(RemindInfo info) {
		FinalDb db = FinalDb.create(mContext, db_name);
		db.update(info);
	}

	public void deleteRemind(RemindInfo info) {
		FinalDb db = FinalDb.create(mContext, db_name);
		db.delete(info);
	}

	public ArrayList<AlarmInfo> getAlarmsByCalendar(Calendar cal) {
		FinalDb db = FinalDb.create(mContext, db_name);
		String sql = String.format("date(time)=date('%s')", TimeUtil.fomateTime(cal.getTimeInMillis(), "yyyy-MM-dd"));
		ArrayList<AlarmInfo> infos = (ArrayList<AlarmInfo>) db.findAllByWhere(AlarmInfo.class, sql
				+ " order by julianday(time) asc");
		return infos;
	}

	/**
	 * 获取未提醒列表中的最早的一个
	 * 
	 * @return
	 * @throws ParseExceptiormdTimeLong
	 *             RemindInfo getFirstUnCompleteRemindInfo() throws
	 *             ParseException { final long sTime =
	 *             System.currentTimeMillis(); final int dayLong = 24 * 60 * 60
	 *             * 1000; long minTime = Long.MAX_VALUE; RemindInfo info =
	 *             null; for (int i = 0; i < mInfos.size(); i++) { final
	 *             RemindInfo obj = mInfos.get(i); if (0 == obj.getStatus() &&
	 *             obj.getRealyRemindStatus() == 0) { long time =
	 *             TimeUtil.getUTC(obj.getRmdDt(), "yyyy-MM-dd HH:mm:ss");
	 * 
	 *             // if((time - sTime)/1000<0){ // continue; // }
	 * 
	 *             if (obj.getType() != 3) {// 非自定义提醒 // 提前一天时间判定、、、延后一天时间判定
	 *             long tagTime = 0; if (((tagTime = time - dayLong) < minTime
	 *             && tagTime > sTime) || (tagTime = time + dayLong) < minTime
	 *             && tagTime > sTime) { minTime = time; info = obj;
	 *             info.setRealyRmindTime(tagTime); } } else {// 自定义提醒 if (time
	 *             < minTime) { minTime = time; info = obj;
	 *             info.setRealyRmindTime(time); } }
	 * 
	 *             } } System.out.println("计算首个提醒的耗时--->" +
	 *             (System.currentTimeMillis() - sTime)); return info; }
	 * 
	 *             /** 获取未提醒列表中的最早的一个
	 * 
	 * @param info
	 *            忽略此RemindInfo
	 * @return
	 */
	// public RemindInfo getFirstUnCompleteRemindInfoAndIgnore(RemindInfo rInfo)
	// {
	// long minTime = Long.MAX_VALUE;
	// RemindInfo info = null;
	// try {
	// for (int i = 0; i < mInfos.size(); i++) {
	// final RemindInfo obj = mInfos.get(i);
	// if (rInfo != null && rInfo == obj) {
	// continue;
	// }
	//
	// if (0 == obj.getStatus()) {
	// long time = TimeUtil.getUTC(obj.getRmdDt(), "yyyy-MM-dd HH:mm:ss");
	// if (time < minTime) {
	// minTime = time;
	// info = obj;
	// }
	// }
	// }
	// return info;
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return null;
	// }

	// public void commit() {
	// try {
	// sp.edit().putString("list", SerializUtil.toString(mInfos)).commit();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	// @SuppressWarnings("unchecked")
	public void setRemindList(ArrayList<RemindInfo> list) {
		FinalDb db = FinalDb.create(mContext, db_name);
		db.deleteByWhere(RemindInfo.class, "");
		db.saveList(list);
		// for (int i = 0; i < list.size(); i++) {
		//
		// }
	}

	@SuppressWarnings("unchecked")
	private void prepareAlarm() {
		final long dayTime = 60 * 60 * 1000 * 24;
		FinalDb db = FinalDb.create(mContext, db_name);
		db.deleteByWhere(AlarmInfo.class, "");
		String sql = "status!='3'";
		ArrayList<RecommondInfo> list = (ArrayList<RecommondInfo>) db.findAllByWhere(RecommondInfo.class, sql);
		pareRemindList(db, list);

	}

	public void pareRemindList(FinalDb db, ArrayList<RecommondInfo> taskList) {

		ArrayList<AlarmInfo> alarmList = new ArrayList<AlarmInfo>();
		for (int i = 0; i < taskList.size(); i++) {
			RecommondInfo task = taskList.get(i);
			if (task.rateType == -1 || TextUtils.isEmpty(task.remindTime)) {
				continue;
			}
			try {
				alarmList.addAll(getAlarmInfo(task));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		db.deleteByWhere(AlarmInfo.class, "");
		db.saveList(alarmList);

	}

	private List<AlarmInfo> getAlarmInfo(RecommondInfo info) throws ParseException {
		ArrayList<AlarmInfo> list = new ArrayList<AlarmInfo>();
		Calendar calEndTime = Calendar.getInstance();
		Calendar calStartTime = Calendar.getInstance();
		calStartTime.setTimeInMillis(TimeUtil.getUTC(info.remindTime, ConfigParams.TIME_FORMAT1));
		if (info.endDt.contains("永久")) {
			calEndTime.add(Calendar.YEAR, 1);
		} else {
			calEndTime.setTimeInMillis(TimeUtil.getUTC(info.endDt, ConfigParams.TIME_FORMAT));
		}

		int num = TimeUtil.getOfferDay(calStartTime.getTimeInMillis(), calEndTime.getTimeInMillis());
		if (info.rateType == 0) {
			AlarmInfo item = new AlarmInfo();
			item.setMsg(info.taskDesc);
			item.setTitle(ResUtil.getString(R.string.toask_warning));
			item.setTime(TimeUtil.fomateTime(calStartTime.getTimeInMillis(), ConfigParams.TIME_FORMAT1));
			item.setId(info.id + "");
			item.setTaskId(info.id);
			list.add(item);
		}
		if (info.rateType == 1) {
			int day = Integer.valueOf(info.rateValue);
			int count = num / day;
			int i = 0;
			do {

				AlarmInfo item = new AlarmInfo();
				item.setMsg(info.taskDesc);
				item.setTime(TimeUtil.fomateTime(calStartTime.getTimeInMillis(), ConfigParams.TIME_FORMAT1));
				item.setId(info.id + i);
				item.setTaskId(info.id);
				item.setTitle(ResUtil.getString(R.string.toask_warning));
				list.add(item);
				calStartTime.add(Calendar.DAY_OF_MONTH, day);
				i++;
			} while (i < count);

		} else if (info.rateType == 2) {

			int day = Integer.valueOf(info.rateValue) * 7;
			int count = num / day;
			int i = 0;
			do {
				AlarmInfo item = new AlarmInfo();
				item.setMsg(info.taskDesc);
				item.setTime(TimeUtil.fomateTime(calStartTime.getTimeInMillis(), ConfigParams.TIME_FORMAT1));
				item.setId(info.id + i);
				item.setTitle(ResUtil.getString(R.string.toask_warning));
				item.setTaskId(info.id);
				list.add(item);
				calStartTime.add(Calendar.DAY_OF_MONTH, day);
				i++;
			} while (i < count);

		} else if(info.rateType == 3){
			
			int month = Integer.valueOf(info.rateValue);
			int i = 0;
			do {

				int dayOfMonth = calStartTime.get(Calendar.DAY_OF_MONTH);
				int monthDays = calStartTime.getActualMaximum(Calendar.DAY_OF_MONTH);
				
				if(dayOfMonth < monthDays){
					AlarmInfo item = new AlarmInfo();
					item.setMsg(info.taskDesc);
					item.setTime(TimeUtil.fomateTime(calStartTime.getTimeInMillis(), ConfigParams.TIME_FORMAT1));
					item.setId(info.id + i);
					item.setTaskId(info.id);
					item.setTitle(ResUtil.getString(R.string.toask_warning));
					list.add(item);
				}
				
				calStartTime.add(Calendar.MONTH,month);
				i++;
			} while (TimeUtil.isBefore(calStartTime.getTimeInMillis(), calEndTime.getTimeInMillis()));
			
		} else if(info.rateType == 4){
			
		} else if(info.rateType == 5){
			String[] ws = info.rateValue.split(",");
			final List<String> weeks = Arrays.asList(ws);
			for(int i=0;i<num;i++){
				if(weeks.contains(calStartTime.get(Calendar.DAY_OF_WEEK)+"")){
					AlarmInfo item = new AlarmInfo();
					item.setMsg(info.taskDesc);
					item.setTime(TimeUtil.fomateTime(calStartTime.getTimeInMillis(), ConfigParams.TIME_FORMAT1));
					item.setId(info.id + i);
					item.setTitle(ResUtil.getString(R.string.toask_warning));
					item.setTaskId(info.id);
					list.add(item);
				}
				calStartTime.add(Calendar.DAY_OF_MONTH, 1);
			}
			
		}
		return list;

	}
	
	public void startNextAlarm() {
		final String ctime = TimeUtil.fomateTime(System.currentTimeMillis(), ConfigParams.TIME_FORMAT1);
		FinalDb db = FinalDb.create(mContext, db_name);
		String sql = String
				.format("datetime('%s') <= datetime(time) and status!=1 order by julianday(time) asc", ctime);
		ArrayList<AlarmInfo> list = (ArrayList<AlarmInfo>) db.findAllByWhere(AlarmInfo.class, sql);
		try {
			if (list != null && list.size() != 0) {
				AlarmInfo info = list.get(0);
				final long time = TimeUtil.getUTC(info.getTime(), ConfigParams.TIME_FORMAT1);
				AlarmManager aMrg = (AlarmManager) mContext.getSystemService("alarm");
				Intent localIntent = new Intent(mContext, RemindService.class);
				localIntent.setAction(ACTION_REMIND);
				localIntent.putExtra("id", info.getId());
				PendingIntent localPendingIntent = PendingIntent.getService(mContext, 0, localIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				aMrg.set(AlarmManager.RTC_WAKEUP, time, localPendingIntent);
				RemindMrg.IS_PREPARE_REMINDED = true;
			}
		} catch (Exception e) {
			RemindMrg.IS_PREPARE_REMINDED = false;
			e.printStackTrace();
		}

	}

	public void startRemindInfoAlarm() {
		prepareAlarm();
		startNextAlarm();
	}

	public void startRemindInfoAlarmService() {
		mContext.startService(new Intent(mContext, RemindService.class));
	}

	public boolean getCJRemindSet() {
		return sp.getBoolean("set_cj", true);
	}

	public boolean getOtherRemindSet() {
		return sp.getBoolean("set_other", true);
	}

	public boolean getYMRemindSet() {
		return sp.getBoolean("set_ym", true);
	}

	public boolean getRemindSet() {
		return sp.getBoolean("set_all", true);
	}

	public void setRemindSet(boolean b) {
		sp.edit().putBoolean("set_all", b).commit();
	}

	public void setCJRemindSet(boolean b) {
		sp.edit().putBoolean("set_cj", b).commit();
	}

	public void setOtherRemindSet(boolean b) {
		sp.edit().putBoolean("set_other", b).commit();
	}

	public void setYMRemindSet(boolean b) {
		sp.edit().putBoolean("set_ym", b).commit();
	}

	public void clearReminds() {
		sp.edit().clear().commit();
		FinalDb db = FinalDb.create(mContext, db_name);
		db.deleteByWhere(RemindInfo.class, "");
		db.deleteByWhere(AlarmInfo.class, "");
	}

	// rmdFTime
	// rmdOTime
	// rmdBTime

	public String getFtime() {
		return sp.getString("f_time", "");
	}

	public void setFtime(String time) {
		sp.edit().putString("f_time", time).commit();
	}

	public String getOtime() {
		return sp.getString("o_time", "");
	}

	public void setOtime(String time) {
		sp.edit().putString("o_time", time).commit();
	}

	public String getBTime() {
		return sp.getString("b_time", "");
	}

	public void setBTime(String time) {
		sp.edit().putString("b_time", time).commit();
	}

}
