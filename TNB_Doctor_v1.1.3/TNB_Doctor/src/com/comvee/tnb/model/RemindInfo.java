package com.comvee.tnb.model;

import java.io.Serializable;

import com.comvee.annotation.sqlite.Table;

@Table(name="remind_info_2")
public class RemindInfo implements Serializable {

	private String id; // 提醒记录id
	private String mPhoto;// 母亲头像（url）
	private String bPhoto;// 宝宝头像(url)
	private String conDt;// 提醒内容时间段部分
	private String con;// 具体提醒内容
	private String rmdDt;// 提醒时间点
	private int status;// 提醒状态 0：未完成, 1：完成, 2:过期
	private int type;// 提醒类型 1:产检 2;疫苗 3;自定义
	private String rmdSDt;//提醒时间范围开始点
	private String rmdEDt;//提醒时间范围结束点
	private int realyRemindStatus;//是否已经提醒过了 0未提醒过,1第一次提醒,2第二次提醒,3,已经完成提醒
	private String fTime;//完成时间
	private String oid;//自定义提醒的分组id

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getFTime() {
		return fTime;
	}

	public void setFTime(String fTime) {
		this.fTime = fTime;
	}

	public String getRmdSDt() {
		return rmdSDt;
	}

	public void setRmdSDt(String rmdSDt) {
		this.rmdSDt = rmdSDt;
	}

	public String getRmdEDt() {
		return rmdEDt;
	}

	public void setRmdEDt(String rmdEDt) {
		this.rmdEDt = rmdEDt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMPhoto() {
		return mPhoto;
	}

	public void setMPhoto(String mPhoto) {
		this.mPhoto = mPhoto;
	}


	public String getBPhoto() {
		return bPhoto;
	}

	public void setBPhoto(String bPhoto) {
		this.bPhoto = bPhoto;
	}

	public String getConDt() {
		return conDt;
	}

	public void setConDt(String conDt) {
		this.conDt = conDt;
	}

	public String getCon() {
		return con;
	}

	public void setCon(String con) {
		this.con = con;
	}

	public String getRmdDt() {
		return rmdDt;
	}

	public void setRmdDt(String rmdDt) {
		this.rmdDt = rmdDt;
	}
	
	@Override
	public boolean equals(Object o) {
		return id.equals(((RemindInfo)o).id);
	}

	/**
	 * 是否已经提醒过了
	 * @param realyRemindStatus
	 *  0未提醒过,1第一次提醒,2第二次提醒
	 */
	public int getRealyRemindStatus() {
		return realyRemindStatus;
	}
	/**
	 * 是否已经提醒过了
	 * @param realyRemindStatus
	 *  0未提醒过,1第一次提醒,2第二次提醒
	 */
	public void setRealyRemindStatus(int realyRemindStatus) {
		this.realyRemindStatus = realyRemindStatus;
	}



}
