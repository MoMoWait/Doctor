package com.comvee.tnb.model;

public class ExceptionList {
	private long newsId;
	private String doctorId;
	private String insertDt;
	private String contentText;
	private int newsType;
	private String adviceContent;
	private String doctorName;
	private int detailNewsType;

	public int getDetailNewsType() {
		return detailNewsType;
	}

	public void setDetailNewsType(int detailNewsType) {
		this.detailNewsType = detailNewsType;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public long getNewsId() {
		return newsId;
	}

	public void setNewsId(long newsId) {
		this.newsId = newsId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getInsertDt() {
		return insertDt;
	}

	public void setInsertDt(String insertDt) {
		this.insertDt = insertDt;
	}

	public String getContentText() {
		return contentText;
	}

	public void setContentText(String contentText) {
		this.contentText = contentText;
	}

	public int getNewsType() {
		return newsType;
	}

	public void setNewsType(int newsType) {
		this.newsType = newsType;
	}

	public String getAdviceContent() {
		return adviceContent;
	}

	public void setAdviceContent(String adviceContent) {
		this.adviceContent = adviceContent;
	}

}
