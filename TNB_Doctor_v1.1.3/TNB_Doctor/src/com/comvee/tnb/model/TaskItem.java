package com.comvee.tnb.model;

public class TaskItem
{

	private String jobCfgId;
	private String imgUrl;
	private String name;
	private int isNew;
	private String detail;
	private String use;
	private String comment;
	private int jobType;
	
	public void setJobType(int jobType){
		this.jobType = jobType;
	}
	
	public int getJobType(){
		return jobType;
	}

	public String getJobCfgId()
	{
		return jobCfgId;
	}

	public void setJobCfgId(String jobCfgId)
	{
		this.jobCfgId = jobCfgId;
	}

	public int getIsNew()
	{
		return isNew;
	}

	public void setIsNew(int isNew)
	{
		this.isNew = isNew;
	}

	public String getImgUrl()
	{
		return imgUrl;
	}

	public void setImgUrl(String imgUrl)
	{
		this.imgUrl = imgUrl;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDetail()
	{
		return detail;
	}

	public void setDetail(String detail)
	{
		this.detail = detail;
	}

	public String getUse()
	{
		return use;
	}

	public void setUse(String use)
	{
		this.use = use;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

}
