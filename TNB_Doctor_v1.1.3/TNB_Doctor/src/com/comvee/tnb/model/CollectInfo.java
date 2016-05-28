/**
 * 
 */
package com.comvee.tnb.model;

import java.io.Serializable;

/**
 * 我的收藏信息
 * 
 * @author SZM
 * 
 */
public class CollectInfo implements Serializable {

	private String collectId;
	private String insertDt;
	private String memberId;
	private String objId;
	private String title;
	private String url;
	private String imgUrl;
	private String isCollect;
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public CollectInfo(String collectId, String insertDt, String memberId, String objId, String title, String url, String imgUrl, String isCollect,
			String type) {
		super();
		this.collectId = collectId;
		this.insertDt = insertDt;
		this.memberId = memberId;
		this.objId = objId;
		this.title = title;
		this.url = url;
		this.imgUrl = imgUrl;
		this.isCollect = isCollect;
		this.type = type;
	}

	public String getCollectId() {
		return collectId;
	}

	public void setCollectId(String collectId) {
		this.collectId = collectId;
	}

	public String getInsertDt() {
		return insertDt;
	}

	public void setInsertDt(String insertDt) {
		this.insertDt = insertDt;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getObjId() {
		return objId;
	}

	public void setObjId(String objId) {
		this.objId = objId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getIsCollect() {
		return isCollect;
	}

	public void setIsCollect(String isCollect) {
		this.isCollect = isCollect;
	}

}
