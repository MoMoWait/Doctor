package com.comvee.tnb.model;

import java.io.Serializable;

import com.comvee.annotation.sqlite.Table;

@Table(name="story_item")
public class StoryItemInfo implements Serializable{
	
	public String name;
	public String downloadUrl;
	public String id;
	public String decs;
	public String superName;
	public int beLoc;//0false1true
	
	public int isLoc() {
		return beLoc;
	}

	public int getBeLoc() {
		return beLoc;
	}

	public void setBeLoc(int beLoc) {
		this.beLoc = beLoc;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public String getDecs() {
		return decs;
	}
	public void setDecs(String decs) {
		this.decs = decs;
	}
	public String getSuperName() {
		return superName;
	}
	public void setSuperName(String superName) {
		this.superName = superName;
	}
	
}
