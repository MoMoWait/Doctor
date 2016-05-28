package com.comvee.tnb.model;

public class AskQuestionInfo {

	private String answerCon;
	private String answerUserName;
	private int answerUserType;
	private String answerUserUrl;
	private String insertDt;
	private String questionAnswerId;
	private int continueType;
	private String voiceMins;

	public void setVoiceMins(String voiceMins) {
		this.voiceMins = voiceMins;
	}

	public String getVoiceMins() {
		return this.voiceMins;
	}

	public void setAnswerCon(String answerCon) {
		this.answerCon = answerCon;
	}

	public String getAnswerCon() {
		return this.answerCon;
	}

	public void setAnswerUserName(String answerUserName) {
		this.answerUserName = answerUserName;
	}

	public String getAnswerUserName() {
		return this.answerUserName;
	}

	public void setAnswerUserType(int answerUserType) {
		this.answerUserType = answerUserType;
	}

	public int getAnswerUserType() {
		return this.answerUserType;
	}

	public void setAnswerUserUrl(String answerUserUrl) {
		this.answerUserUrl = answerUserUrl;
	}

	public String getAnswerUserUrl() {
		return this.answerUserUrl;
	}

	public void setInsertDt(String insertDt) {
		this.insertDt = insertDt;
	}

	public String getInsertDt() {
		return this.insertDt;
	}

	public void setQuestionAnswerId(String questionAnswerId) {
		this.questionAnswerId = questionAnswerId;
	}

	public String getQuestionAnswerId() {
		return this.questionAnswerId;
	}

	public void setContinueType(int continueType) {
		this.continueType = continueType;
	}

	public int getContinueType() {
		return this.continueType;
	}

}
