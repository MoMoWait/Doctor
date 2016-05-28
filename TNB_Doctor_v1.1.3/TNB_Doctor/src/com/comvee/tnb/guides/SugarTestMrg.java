package com.comvee.tnb.guides;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;

import com.comvee.tnb.config.ConfigParams;
import com.comvee.util.TimeUtil;

public class SugarTestMrg {
	// 测试血糖状态
	public static final int BEFORE_BREAKFAST = 0;
	public static final int AFTER_BREAKFAST = 1;
	public static final int BEFORE_LUNCH = 2;
	public static final int AFTER_LUNCH = 3;
	public static final int BEFORE_DINNER = 4;
	public static final int AFTER_DINNER = 5;
	public static final int BEDTIME = 6;
	public static final int TIMERANGE_COUNT = 7;

	public static final int[] TIMERANGE_TIME = { 0, 800, 1000, 1200, 1600, 1800, 2000 };
	public static final int[] TIMERANGES = { BEFORE_BREAKFAST, AFTER_BREAKFAST, BEFORE_LUNCH, AFTER_LUNCH, BEFORE_DINNER, AFTER_DINNER, BEDTIME };

	public static int getTimeRange(Calendar datetime) {

		int hour = datetime.get(Calendar.HOUR_OF_DAY);
		int minute = datetime.get(Calendar.MINUTE);
		int time = hour * 100 + minute;

		int index = TIMERANGE_TIME.length - 1;
		for (int i = TIMERANGE_TIME.length - 1; i >= 0; --i) {
			index = i;
			if (time >= TIMERANGE_TIME[i]) {
				break;
			}
		}
		return TIMERANGES[index];
	}

	public static int getTimeRange(long datetime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(datetime);
		return getTimeRange(calendar);
	}

	// 设置 提醒（发送广播）
	public static void setRmind(Context cxt, int type, long date, int timeBucket) {
		Intent intent = new Intent("com.comvee.ui.remind.ACTION");
		// intent.putExtra("type", type + "");
		// intent.putExtra("date", date);
		// intent.putExtra("timeBucket", timeBucket);
		cxt.sendBroadcast(intent);

		// db = FinalDb.create(cxt, ConfigParams.DB_NAME);
		// 记录设置的时间
		ConfigParams.setInt(cxt, type + "", date);
	}

	// 判断是否设置过 时间
	public static boolean checkIsSetReminded(Context cxt, int type, long curTime) {
		long remindTime = ConfigParams.getInt(cxt, type + "");
		if (remindTime > curTime) {
			return true;
		}
		return false;
	}

	public static long getLastRemindTime(int bstType, String startTime) {
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTimeInMillis(TimeUtil.getUTC(startTime, "yyyy-MM-dd"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

		if (bstType == 4 || bstType == 9) {// 空腹血糖 新添了type == 9
			if (System.currentTimeMillis() > cal.getTimeInMillis()) {
				if (hour > 8) {
					// 下一天 7点提醒
					cal.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
					cal.add(Calendar.DAY_OF_MONTH, 1);
					cal.set(Calendar.HOUR_OF_DAY, 7);
				} else {
					return 0;
				}
			} else {
				// 第二天7点提醒
				cal.set(Calendar.HOUR_OF_DAY, 7);
			}
		} else if (bstType == 5 || bstType == 8 || bstType == 11) {// 新添加了type
																	// == 8和 11
			return 0;
			// 阅读任务
		} else if (bstType == 2 || bstType == 3) {// 餐后

			// 8:00-9:59
			// 12:00-13:59
			// 18:00-19:59 餐后立即测试

			// 0:00-7:59
			// 10:00-11:59
			// 14:00-17:59
			// 20:00-23:59 餐后“提醒我”

			if (System.currentTimeMillis() > cal.getTimeInMillis()) {
				cal.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
				if (hour > 0 && hour < 8) {
					cal.set(Calendar.HOUR_OF_DAY, 8);
				} else if (hour > 10 && hour < 12) {
					cal.set(Calendar.HOUR_OF_DAY, 13);
				} else if (hour > 12 && hour < 18) {
					cal.set(Calendar.HOUR_OF_DAY, 19);
					cal.set(Calendar.MINUTE, 30);
				} else if (hour > 20) {
					cal.add(Calendar.DAY_OF_MONTH, 1);
					cal.set(Calendar.HOUR_OF_DAY, 8);
				} else {
					return 0;
				}
			} else {
				// 第二天 8点提醒
				cal.set(Calendar.HOUR_OF_DAY, 8);
			}
		} else if (bstType == 7) {// 睡前
//			if (cal.before(today)) {
//				cal.setTimeInMillis(System.currentTimeMillis());
//				cal.set(Calendar.MINUTE, 0);
//				cal.set(Calendar.SECOND, 0);
//			}
			if (System.currentTimeMillis() > cal.getTimeInMillis()) {
				cal.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
				if (hour > 0 && hour < 20) {
					cal.set(Calendar.HOUR_OF_DAY, 21);
				} else {
					return 0;
				}
			} else if (hour < 20) {
				cal.add(Calendar.DAY_OF_MONTH, -1);
				cal.set(Calendar.HOUR_OF_DAY, 21);
			} else {
				return 0;
			}
		} else if (bstType == 6) {// 空腹血糖>10 超过3次
			if (System.currentTimeMillis() > cal.getTimeInMillis()) {
				cal.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
				if (hour > 0 && hour < 2) {
					cal.set(Calendar.HOUR_OF_DAY, 2);
				} else if (hour > 3) {
					cal.add(Calendar.DAY_OF_MONTH, 1);
					cal.set(Calendar.HOUR_OF_DAY, 2);
				} else {
					return 0;
				}
			} else if (hour < 2) {
				cal.set(Calendar.HOUR_OF_DAY, 2);
			} else if (hour > 3) {
				cal.add(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.HOUR_OF_DAY, 2);
			} else {
				return 0;
			}
		} else if (bstType == 10) {// 记录餐前测试血糖
			if (System.currentTimeMillis() > cal.getTimeInMillis()) {
				cal.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
				if (hour > 0 && hour < 10) {
					cal.set(Calendar.HOUR_OF_DAY, 10);// 午餐前
				} else if (hour > 10 && hour < 17) {
					cal.set(Calendar.HOUR_OF_DAY, 17);// 晚餐前
				} else if (hour > 17) {
					cal.set(Calendar.HOUR_OF_DAY, 10);
				} else {
					return 0;
				}
			} else {
				// 第二天 10点提醒
				cal.set(Calendar.HOUR_OF_DAY, 10);// 午餐前
			}
		} else
			return 0;
		return cal.getTimeInMillis();
	}

}
