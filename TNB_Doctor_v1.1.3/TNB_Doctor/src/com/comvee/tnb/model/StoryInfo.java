package com.comvee.tnb.model;

public class StoryInfo {

	public String name;
	public String decs;
	public String id;
	public String imgUrl;
	public String url;
	public String time;
	public int count;
	public String group;
	
	@Override
	public String toString() {
		return String.format("%s\n%s\n%s\n%s", name,time,url,imgUrl);
	}
	
}
