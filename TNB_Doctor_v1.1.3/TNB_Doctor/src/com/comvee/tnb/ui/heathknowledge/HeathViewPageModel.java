package com.comvee.tnb.ui.heathknowledge;

public class HeathViewPageModel {
	private String turnsPlayUrl;
	private int turnsPlayStatus;
	private String hot_spot_id;
	private String url;
	private int type;
	private String hot_spot_title;

	public String getHotSpotTitle(){
		return hot_spot_title;
	}

	public void setHotSpotTitle(String title){
		hot_spot_title = title;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTurnsPlayUrl() {
		return turnsPlayUrl;
	}

	public void setTurnsPlayUrl(String turnsPlayUrl) {
		this.turnsPlayUrl = turnsPlayUrl;
	}

	public int getTurnsPlayStatus() {
		return turnsPlayStatus;
	}

	public void setTurnsPlayStatus(int turnsPlayStatus) {
		this.turnsPlayStatus = turnsPlayStatus;
	}

	public String getHot_spot_id() {
		return hot_spot_id;
	}

	public void setHot_spot_id(String hot_spot_id) {
		this.hot_spot_id = hot_spot_id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
