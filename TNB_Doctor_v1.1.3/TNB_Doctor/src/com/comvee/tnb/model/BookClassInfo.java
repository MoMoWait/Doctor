package com.comvee.tnb.model;

import java.util.ArrayList;

public class BookClassInfo {

	private String id;
	private String title;
	private ArrayList<BookClassItemInfo> subMap;
	private String num;

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<BookClassItemInfo> getSubMap() {
		return subMap;
	}

	public void setSubMap(ArrayList<BookClassItemInfo> subMap) {
		this.subMap = subMap;
	}

}
