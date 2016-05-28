package com.comvee.tnb.model;

public class MyTaskInfo
{

	public int defaultRemind;
	public int doPercent;
	public String doSuggest;
	public String endDt;
	public int finishNum;// 完成天数
	public int totalNum;// 总共数
	public int residue;// 剩余数
	public String imgUrl;
	public String insertDt;

	public String id;
	public String jobInfo;
	public String jobTitle;
	public int jobType;
	public String memberJobId;
	public int status;// 1、进行中0、过期完成
	public int delFlag;//1:可删除0:不可删除
	public String doctorName;
	public long doctorId;
	public String dateStr;
	
	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public long getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(long doctorId) {
		this.doctorId = doctorId;
	}

	public int getResidue()
	{
		return residue;
	}

	public void setResidue(int residue)
	{
		this.residue = residue;
	}

	public int getTotalNum()
	{
		return totalNum;
	}

	public void setTotalNum(int totalNum)
	{
		this.totalNum = totalNum;
	}

	public int getStatus()
	{
		return status;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public int getDefaultRemind()
	{
		return defaultRemind;
	}

	public void setDefaultRemind(int defaultRemind)
	{
		this.defaultRemind = defaultRemind;
	}

	public int getDoPercent()
	{
		return doPercent;
	}

	public void setDoPercent(int doPercent)
	{
		this.doPercent = doPercent;
	}

	public String getDoSuggest()
	{
		return doSuggest;
	}

	public void setDoSuggest(String doSuggest)
	{
		this.doSuggest = doSuggest;
	}

	public String getEndDt()
	{
		return endDt;
	}

	public void setEndDt(String endDt)
	{
		this.endDt = endDt;
	}

	public int getFinishNum()
	{
		return finishNum;
	}

	public void setFinishNum(int finishNum)
	{
		this.finishNum = finishNum;
	}

	public String getImgUrl()
	{
		return imgUrl;
	}

	public void setImgUrl(String imgUrl)
	{
		this.imgUrl = imgUrl;
	}

	public String getInsertDt()
	{
		return insertDt;
	}

	public void setInsertDt(String insertDt)
	{
		this.insertDt = insertDt;
	}

	public String getJobInfo()
	{
		return jobInfo;
	}

	public void setJobInfo(String jobInfo)
	{
		this.jobInfo = jobInfo;
	}

	public String getJobTitle()
	{
		return jobTitle;
	}

	public void setJobTitle(String jobTitle)
	{
		this.jobTitle = jobTitle;
	}

	public int getJobType()
	{
		return jobType;
	}

	public void setJobType(int jobType)
	{
		this.jobType = jobType;
	}

	public String getMemberJobId()
	{
		return memberJobId;
	}

	public void setMemberJobId(String memberJobId)
	{
		this.memberJobId = memberJobId;
	}

}
