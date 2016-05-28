/**
 * 
 */
package com.comvee.tnb.model;

/**随访信息
 * @author SZM
 *
 */
public class FollowInfo {

	private long followupId;
	private int doctorId;
	private int memberId;
	private String insertDt;
	private int fillStatus;
	private String fillDate;
	private int dealStatus;
	private String dealDate;
	private int type;
	private int batchId;
	private String typeText;
	private String doctorName;
	
	public FollowInfo(){}

	public FollowInfo(long followupId, int doctorId, int memberId, String insertDt, int fillStatus, String fillDate, int dealStatus, String dealDate,
			int type, int batchId, String typeText, String doctorName) {
		super();
		this.followupId = followupId;
		this.doctorId = doctorId;
		this.memberId = memberId;
		this.insertDt = insertDt;
		this.fillStatus = fillStatus;
		this.fillDate = fillDate;
		this.dealStatus = dealStatus;
		this.dealDate = dealDate;
		this.type = type;
		this.batchId = batchId;
		this.typeText = typeText;
		this.doctorName = doctorName;
	}

	public long getFollowupId() {
		return followupId;
	}
	public void setFollowupId(long followupId) {
		this.followupId = followupId;
	}
	public int getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(int doctorId) {
		this.doctorId = doctorId;
	}
	public int getMemberId() {
		return memberId;
	}
	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}
	public String getInsertDt() {
		return insertDt;
	}
	public void setInsertDt(String insertDt) {
		this.insertDt = insertDt;
	}
	public int getFillStatus() {
		return fillStatus;
	}
	public void setFillStatus(int fillStatus) {
		this.fillStatus = fillStatus;
	}
	public String getFillDate() {
		return fillDate;
	}
	public void setFillDate(String fillDate) {
		this.fillDate = fillDate;
	}
	public int getDealStatus() {
		return dealStatus;
	}
	public void setDealStatus(int dealStatus) {
		this.dealStatus = dealStatus;
	}
	public String getDealDate() {
		return dealDate;
	}
	public void setDealDate(String dealDate) {
		this.dealDate = dealDate;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getBatchId() {
		return batchId;
	}
	public void setBatchId(int batchId) {
		this.batchId = batchId;
	}
	public String getTypeText() {
		return typeText;
	}
	public void setTypeText(String typeText) {
		this.typeText = typeText;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}
	
}
