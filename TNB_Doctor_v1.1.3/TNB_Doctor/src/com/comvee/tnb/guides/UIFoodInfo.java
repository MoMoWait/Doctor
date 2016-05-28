package com.comvee.tnb.guides;

import java.util.ArrayList;

public class UIFoodInfo {
	private String titlebar;
	private String title;
	private String desc;
	private String nosport;
	private String lowsport;
	private String normalsport;
	private String highsport;
	private ArrayList<String> list;
	private String linkTask;
	private String button;
	private int type;
	private int status;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getButton() {
		return button;
	}

	public void setButton(String button) {
		this.button = button;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTitlebar() {
		return titlebar;
	}

	public void setTitlebar(String titlebar) {
		this.titlebar = titlebar;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getNosport() {
		return nosport;
	}

	public void setNosport(String nosport) {
		this.nosport = nosport;
	}

	public String getLowsport() {
		return lowsport;
	}

	public void setLowsport(String lowsport) {
		this.lowsport = lowsport;
	}

	public String getNormalsport() {
		return normalsport;
	}

	public void setNormalsport(String normalsport) {
		this.normalsport = normalsport;
	}

	public String getHighsport() {
		return highsport;
	}

	public void setHighsport(String highsport) {
		this.highsport = highsport;
	}

	public ArrayList<String> getList() {
		return list;
	}

	public void setList(ArrayList<String> list) {
		this.list = list;
	}

	public String getLinkTask() {
		return linkTask;
	}

	public void setLinkTask(String linkTask) {
		this.linkTask = linkTask;
	}
}
