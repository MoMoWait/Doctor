package com.comvee.tnb.model;

import java.io.Serializable;

import com.comvee.annotation.sqlite.Id;
import com.comvee.annotation.sqlite.Table;
import com.comvee.annotation.sqlite.Transient;

@Table(name = "AskServerInfoNew")
public class AskServerInfo implements Serializable {
	/**
	 * 
	 */
	@Transient
	private static final long serialVersionUID = 1L;
	@Id(column = "id")
	private int id;
	private String sid;// 消息主键
	private String headImageUrl;// 医生头像地址
	private String dataStr;// 具体消息
	private int count;// 未读条数
	private String doctorId;// 医生id
	private int ownerType;// 消息拥有者
	private String userMsg;// 消息描述
	private int msgType;// 消息类型
	private String memberId;// 患者id
	private String doctorName;// 医生名称
	private String insertDt;// 插入时间
	private int isDispose;// 是否处理
	private String docTellId;// 用于获取医生电话咨询列表id
	private String isValid;
	private boolean isAdvisors;


	public boolean isAdvisors() {
		return isAdvisors;
	}

	public void setAdvisors(boolean isAdvisors) {
		this.isAdvisors = isAdvisors;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getIsValid() {
		return isValid;
	}

	public void setIsValid(String isValid) {
		this.isValid = isValid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDocTellId() {
		return docTellId;
	}

	public void setDocTellId(String docTellId) {
		this.docTellId = docTellId;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getHeadImageUrl() {
		return headImageUrl;
	}

	public void setHeadImageUrl(String headImageUrl) {
		this.headImageUrl = headImageUrl;
	}

	public String getDataStr() {
		return dataStr;
	}

	public void setDataStr(String dataStr) {
		this.dataStr = dataStr;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public int getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(int ownerType) {
		this.ownerType = ownerType;
	}

	public String getUserMsg() {
		return userMsg;
	}

	public void setUserMsg(String msgContent) {
		this.userMsg = msgContent;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getInsertDt() {
		return insertDt;
	}

	public void setInsertDt(String insertDt) {
		this.insertDt = insertDt;
	}

	public int getIsDispose() {
		return isDispose;
	}

	public void setIsDispose(int isDispose) {
		this.isDispose = isDispose;
	}

}
