package com.comvee.tnb.guides;

import java.io.Serializable;

public class GuideBrowseInfo  implements Serializable{

	private String title;
	private String content;
	// private String taskCode;
	private int taskCode;
	private int seq;
	private int status;
	private String filePath;
	private int linktype;
	private String linktask;
	private String uniqueid;
	private int msgtype;
	private int msgseq;
	private int total;
	private String url;
	private int pageType;
	private String desc;
	private String desc1;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDesc1() {
		return desc1;
	}

	public void setDesc1(String desc1) {
		this.desc1 = desc1;
	}

	public int getPageType() {
		return pageType;
	}

	public void setPageType(int pageType) {
		this.pageType = pageType;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(int msgtype) {
		this.msgtype = msgtype;
	}

	public int getMsgseq() {
		return msgseq;
	}

	public void setMsgseq(int msgseq) {
		this.msgseq = msgseq;
	}

	public int getLinktype() {
		return linktype;
	}

	public void setLinktype(int linktype) {
		this.linktype = linktype;
	}

	public String getLinktask() {
		return linktask;
	}

	public void setLinktask(String linktask) {
		this.linktask = linktask;
	}

	public String getUniqueid() {
		return uniqueid;
	}

	public void setUniqueid(String uniqueid) {
		this.uniqueid = uniqueid;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getTaskCode() {
		return taskCode;
	}

	public void setTaskCode(int taskCode) {
		this.taskCode = taskCode;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
