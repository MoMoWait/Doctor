package com.comvee.tnb.guides;

import java.util.ArrayList;

public class GuideQuesInfo {

	public static final int JUMP_CHOOSE_NUM = 4;// 选择数值页面
	public static final int JUMP_CHOOSE_DATE = 1;// 选择日期页面
	public static final int JUMP_CHOOSE_TRHEE = 102;// 三项选择题页面
	public static final int JUMP_CHOOSE_TWO = 2;// 两项选择题页面
	// 1、日期填空，2单选题，3多选题，4填空题，5文本填空，6多选规则跳转,7阅读型

	private String topicKeyword;
	private int topicSeq;
	private String topicTitle;
	private int topicType;
	private int hasGoto;
	private String titleBar;
	private String topicID;
	private String topicIcon;
	private String taskID;

	private String value;
	private int taskStatus;
	private int status;

	private ArrayList<GuideItemInfo> items;

	public ArrayList<GuideItemInfo> getItems() {
		return items;
	}

	public void setItems(ArrayList<GuideItemInfo> items) {
		this.items = items;
	}

	public String getTopicKeyword() {
		return topicKeyword;
	}

	public void setTopicKeyword(String topicKeyword) {
		this.topicKeyword = topicKeyword;
	}

	public int getTopicSeq() {
		return topicSeq;
	}

	public void setTopicSeq(int topicSeq) {
		this.topicSeq = topicSeq;
	}

	public String getTopicTitle() {
		return topicTitle;
	}

	public void setTopicTitle(String topicTitle) {
		this.topicTitle = topicTitle;
	}

	public int getTopicType() {
		return topicType;
	}

	public void setTopicType(int topicType) {
		this.topicType = topicType;
	}

	public int getHasGoto() {
		return hasGoto;
	}

	public void setHasGoto(int hasGoto) {
		this.hasGoto = hasGoto;
	}

	public String getTitleBar() {
		return titleBar;
	}

	public void setTitleBar(String titleBar) {
		this.titleBar = titleBar;
	}

	public String getTopicID() {
		return topicID;
	}

	public void setTopicID(String topicID) {
		this.topicID = topicID;
	}

	public String getTopicIcon() {
		return topicIcon;
	}

	public void setTopicIcon(String topicIcon) {
		this.topicIcon = topicIcon;
	}

	public String getTaskID() {
		return taskID;
	}

	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}

	public int getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(int taskStatus) {
		this.taskStatus = taskStatus;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
