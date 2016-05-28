package com.comvee.tnb.model;

import com.comvee.annotation.sqlite.Id;
import com.comvee.annotation.sqlite.Table;

@Table(name = "ServerListModel")
public class ServerListModel {
	private String content;// 全国公立三甲医院医生7*24小时在线健康咨询",
	private String image;
	private String url;
	private long sid;
	@Id(column = "id")
	private int id;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getSid() {
		return sid;
	}
	public void setSid(long sid) {
		this.sid = sid;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	
}
