package com.comvee.tnb.model;

import com.comvee.annotation.sqlite.Table;

@Table(name = "TendencyPoint2")
public class TendencyPointInfo {

	public int type;// 0、个人1、医生
	public float value;
	public String time;
	public String insertDt;
	public String comment;// 备注
	public int bloodGlucoseStatus;

//	public String getTempTime() {
//		return tempTime;
//	}
//
//	public void setTempTime(String tempTime) {
//		this.tempTime = tempTime;
//	}

	public int getBloodGlucoseStatus() {
		return bloodGlucoseStatus;
	}

	public void setBloodGlucoseStatus(int bloodGlucoseStatus) {
		this.bloodGlucoseStatus = bloodGlucoseStatus;
	}

	public String getInsertDt() {
		return insertDt;
	}

	public void setInsertDt(String insertDt) {
		this.insertDt = insertDt;
	}

	public String code;
	public String id;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
