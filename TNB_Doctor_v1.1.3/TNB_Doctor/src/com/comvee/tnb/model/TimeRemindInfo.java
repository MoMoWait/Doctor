package com.comvee.tnb.model;

import com.comvee.annotation.sqlite.Id;
import com.comvee.annotation.sqlite.Table;

@Table(name = "TimeRemind")
public class TimeRemindInfo {

	@Id(column = "id")
	private int id;
	private long time;
	private int type;// 类型 1血糖 2用药 3特殊隐藏不显示 4 用药功能

	private String period = "1/1/1/1/1/1/1";// 响铃的周期
	private boolean diabolo;// 是否响铃 0不响 1响
	private String remark = "";// 备注
	private boolean temp;// 是否响铃的备份
	private int hour;
	private int minute;
	private String memberId;
	private String memName;
	private String drugUnit;
	private String drugName;
	private long pmType;
	private long drugId;

	public TimeRemindInfo(int id, long time, int type, String period, boolean diabolo, String remark, boolean temp, int hour, int minute) {
		super();
		this.id = id;
		this.time = time;
		this.type = type;
		this.period = period;
		this.diabolo = diabolo;
		this.remark = remark;
		this.temp = temp;
		this.hour = hour;
		this.minute = minute;
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

	public String getDrugUnit() {
		return drugUnit;
	}

	public void setDrugUnit(String drugUnit) {
		this.drugUnit = drugUnit;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public long getDrugId() {
		return drugId;
	}

	public void setDrugId(long drugId) {
		this.drugId = drugId;
	}

	public long getPmType() {
		return pmType;
	}

	public void setPmType(long pmType) {
		this.pmType = pmType;
	}

	public TimeRemindInfo() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public boolean isDiabolo() {
		return diabolo;
	}

	public void setDiabolo(boolean diabolo) {
		this.diabolo = diabolo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public boolean isTemp() {
		return temp;
	}

	public void setTemp(boolean temp) {
		this.temp = temp;
	}

	public int getHour() {
		return hour;
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

}
