package com.comvee.tnb.guides;

import java.util.ArrayList;

public class UIFoodCycleInfo {

	private int type;
	private int seq;
	private int msgSeq;
	private int total;
	private String titlebar;
	private String date;
	private int calorie;
	private String breakfastadd;
	private String lunchadd;
	private String dinneradd;
	private ArrayList<String> breakfast;
	private ArrayList<String> lunch;
	private ArrayList<String> dinner;

	public int getMsgSeq() {
		return msgSeq;
	}

	public void setMsgSeq(int msgSeq) {
		this.msgSeq = msgSeq;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getTitlebar() {
		return titlebar;
	}

	public void setTitlebar(String titlebar) {
		this.titlebar = titlebar;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getCalorie() {
		return calorie;
	}

	public void setCalorie(int calorie) {
		this.calorie = calorie;
	}

	public String getBreakfastadd() {
		return breakfastadd;
	}

	public void setBreakfastadd(String breakfastadd) {
		this.breakfastadd = breakfastadd;
	}

	public String getLunchadd() {
		return lunchadd;
	}

	public void setLunchadd(String lunchadd) {
		this.lunchadd = lunchadd;
	}

	public String getDinneradd() {
		return dinneradd;
	}

	public void setDinneradd(String dinneradd) {
		this.dinneradd = dinneradd;
	}

	public ArrayList<String> getBreakfast() {
		return breakfast;
	}

	public void setBreakfast(ArrayList<String> breakfast) {
		this.breakfast = breakfast;
	}

	public ArrayList<String> getLunch() {
		return lunch;
	}

	public void setLunch(ArrayList<String> lunch) {
		this.lunch = lunch;
	}

	public ArrayList<String> getDinner() {
		return dinner;
	}

	public void setDinner(ArrayList<String> dinner) {
		this.dinner = dinner;
	}
}
