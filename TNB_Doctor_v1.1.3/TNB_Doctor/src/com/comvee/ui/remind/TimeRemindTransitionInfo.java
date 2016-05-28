package com.comvee.ui.remind;

import java.io.Serializable;


public class TimeRemindTransitionInfo implements Cloneable ,Serializable{
	/**
	 * 
	 */
	private int ID;
	private boolean[] week = { false, false, false, false, false, false, false };// 对应时间(星期日。。。。星期天)
	private int hour;
	private int minute;
	private int type;
	private boolean temp;
	private String remark;
	private boolean isDiabolo;
	private long pmType;// 用药功能中的特殊类型 一般闹钟不用提供
	private String drugName;// 用药功能中的药品名称 一般闹钟不用提供
	private String drugUnit;// 用药功能中的药品单位 一般闹钟不用提供
	private String memberId;
	private String memName;
	private long drugId;// 用药功能中的药品id一般闹钟不用提供
	private long nextTime;

	public long getNextTime() {
		return nextTime;
	}

	public void setNextTime(long nextTiem) {
		this.nextTime = nextTiem;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public String getDrugUnit() {
		return drugUnit;
	}

	public void setDrugUnit(String drugUnit) {
		this.drugUnit = drugUnit;
	}

	public long getDrugId() {
		return drugId;
	}

	public void setDrugId(long drugId) {
		this.drugId = drugId;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getMemName() {
		return memName;
	}

	public void setMemName(String memName) {
		this.memName = memName;
	}

	public long getPmType() {
		return pmType;
	}

	public void setPmType(long pmType) {
		this.pmType = pmType;
	}

	public TimeRemindTransitionInfo() {
		week = new boolean[7];
	}

	public int getHour() {
		return hour;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isTemp() {
		return temp;
	}

	public void setTemp(boolean temp) {
		this.temp = temp;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public boolean isDiabolo() {
		return isDiabolo;
	}

	public void setDiabolo(boolean isDiabolo) {
		this.isDiabolo = isDiabolo;
	}

	public void setPeriodOfSunday(boolean b) {
		week[0] = b;
	}

	public void setPeriodOfMonday(boolean b) {
		week[1] = b;
	}

	public void setPeriodOfTuesday(boolean b) {
		week[2] = b;
	}

	public void setPeriodOfWednesday(boolean b) {
		week[3] = b;
	}

	public void setPeriodOfThursday(boolean b) {
		week[4] = b;
	}

	public void setPeriodOfFriday(boolean b) {
		week[5] = b;
	}

	public void setPeriodOfStaurday(boolean b) {
		week[6] = b;
	}

	public boolean getPeriodOfSunday() {
		return week[0];
	}

	public boolean getPeriodOfMonday() {
		return week[1];
	}

	public boolean getPeriodOfTuesday() {
		return week[2];
	}

	public boolean getPeriodOfWednesday() {
		return week[3];
	}

	public boolean getPeriodOfThursday() {
		return week[4];
	}

	public boolean getPeriodOfFriday() {
		return week[5];
	}

	public boolean getPeriodOfStaurday() {
		return week[6];
	}

	public boolean[] getWeek() {
		return week;
	}

	public void setWeek(boolean[] week) {
		if (week.length == 7) {
			this.week = week;
		} else if (week.length > 7) {
			for (int i = 0; i < week.length; i++) {
				this.week[i] = week[i];
			}
		} else if (week.length < 7) {
			this.week = week;
			for (int i = week.length; i < 7; i++) {
				this.week[i] = false;
			}
		}
	}

	public TimeRemindTransitionInfo(int iD, boolean[] week, int hour, int minute, int type, boolean temp, String remark, boolean isDiabolo) {
		super();
		ID = iD;
		this.week = week;
		this.hour = hour;
		this.minute = minute;
		this.type = type;
		this.temp = temp;
		this.remark = remark;
		this.isDiabolo = isDiabolo;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		TimeRemindTransitionInfo other = (TimeRemindTransitionInfo) obj;
		if (other.pmType == this.pmType)
			return true;
		return false;
	}

	@Override
	public TimeRemindTransitionInfo clone() {
		TimeRemindTransitionInfo sport = null;
		try {
			sport = (TimeRemindTransitionInfo) super.clone();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sport;
	}
}
