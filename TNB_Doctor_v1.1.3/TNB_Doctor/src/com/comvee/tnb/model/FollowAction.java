package com.comvee.tnb.model;

public class FollowAction {
	private String title;
	private int type;
	private String detailText;
	private String imgUrl;
	private String jobInfo;
	private String jobTitle;
	private long jobCfgId;
	private int gainNum;
	private String doSuggest;
	private int isNew;
	private int commentNum;
	private String doctroName;
	private long doctroId;

	public String getDoctroName() {
		return doctroName;
	}

	public void setDoctroName(String doctroName) {
		this.doctroName = doctroName;
	}

	public long getDoctroId() {
		return doctroId;
	}

	public void setDoctroId(long doctroId) {
		this.doctroId = doctroId;
	}

	public int getGainNum() {
		return gainNum;
	}

	public void setGainNum(int gainNum) {
		this.gainNum = gainNum;
	}

	public String getDoSuggest() {
		return doSuggest;
	}

	public void setDoSuggest(String doSuggest) {
		this.doSuggest = doSuggest;
	}

	public int getIsNew() {
		return isNew;
	}

	public void setIsNew(int isNew) {
		this.isNew = isNew;
	}

	public int getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getType() {
		return type;

	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDetailText() {
		return detailText;
	}

	public void setDetailText(String detailText) {
		this.detailText = detailText;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getJobInfo() {
		return jobInfo;
	}

	public void setJobInfo(String jobInfo) {
		this.jobInfo = jobInfo;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public long getJobCfgId() {
		return jobCfgId;
	}

	public void setJobCfgId(long jobCfgId) {
		this.jobCfgId = jobCfgId;
	}

}
