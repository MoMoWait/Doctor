package com.comvee.tnb.ui.record;

public class RecordDetailBean {
	private float highEmpty;
	private float lowEmpty;
	private float highFull;
	private float lowFull;
	private float sugerValue;
	private String paramCode;
	private long paramLogId;
	private String recordTime;
	private String suggerTitle;
	private String suggerText;
	private String suggerImage;

	public String getSuggerTitle() {
		return suggerTitle;
	}

	public void setSuggerTitle(String suggerTitle) {
		this.suggerTitle = suggerTitle;
	}

	public String getSuggerText() {
		return suggerText;
	}

	public void setSuggerText(String suggerText) {
		this.suggerText = suggerText;
	}

	public String getSuggerImage() {
		return suggerImage;
	}

	public void setSuggerImage(String suggerImage) {
		this.suggerImage = suggerImage;
	}

	public float getHighEmpty() {
		return highEmpty;
	}

	public void setHighEmpty(float highEmpty) {
		this.highEmpty = highEmpty;
	}

	public float getLowEmpty() {
		return lowEmpty;
	}

	public void setLowEmpty(float lowEmpty) {
		this.lowEmpty = lowEmpty;
	}

	public float getHighFull() {
		return highFull;
	}

	public void setHighFull(float highFull) {
		this.highFull = highFull;
	}

	public float getLowFull() {
		return lowFull;
	}

	public void setLowFull(float lowFull) {
		this.lowFull = lowFull;
	}

	public float getSugerValue() {
		return sugerValue;
	}

	public void setSugerValue(float sugerValue) {
		this.sugerValue = sugerValue;
	}

	public String getParamCode() {
		return paramCode;
	}

	public void setParamCode(String paramCode) {
		this.paramCode = paramCode;
	}

	public long getParamLogId() {
		return paramLogId;
	}

	public void setParamLogId(long paramLogId) {
		this.paramLogId = paramLogId;
	}

	public String getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}

}
