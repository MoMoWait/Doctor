package com.comvee.tnb.model;

public class FollowRecord {
	private String dictName;
	private String path;
	private String dictValue;
	private String Pcode;
	public String getPcode() {
		return Pcode;
	}
	public void setPcode(String pcode) {
		Pcode = pcode;
	}
	public String getDictName() {
		return dictName;
	}
	public void setDictName(String dictName) {
		this.dictName = dictName;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getDictValue() {
		return dictValue;
	}
	public void setDictValue(String dictValue) {
		this.dictValue = dictValue;
	}
	
}
