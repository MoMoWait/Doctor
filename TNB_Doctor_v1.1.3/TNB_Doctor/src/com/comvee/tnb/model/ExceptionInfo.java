package com.comvee.tnb.model;

public class ExceptionInfo {
	private String time;
	private String paramLogId;
	private String value;
	private String insertDt;
	private int bloodGlucoseStatus;
	private String type;
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getParamLogId() {
		return paramLogId;
	}

	public void setParamLogId(String paramLogId) {
		this.paramLogId = paramLogId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getInsertDt() {
		return insertDt;
	}

	public void setInsertDt(String insertDt) {
		this.insertDt = insertDt;
	}

	public int getBloodGlucoseStatus() {
		return bloodGlucoseStatus;
	}

	public void setBloodGlucoseStatus(int bloodGlucoseStatus) {
		this.bloodGlucoseStatus = bloodGlucoseStatus;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
