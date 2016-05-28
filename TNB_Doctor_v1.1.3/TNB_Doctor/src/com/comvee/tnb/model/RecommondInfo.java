package com.comvee.tnb.model;


public class RecommondInfo {

	public String endDt;
	public int status;//1、未开始2、进行中3、完成
	public String startDt;
	public String taskDesc;//任务描述
	public String id;
	public String remindTime;//时间点（08:30）
	public String rateValue;
	public int rateType;
	public String vhActionId;
	public String updateStatusDt;
	public String actionDesc;//任务提醒
	public boolean isRemind;//是否提醒
	public String insertDt;//插入时间

	public boolean isRemind()
	{
		return isRemind;
	}
	public void setRemind(boolean isRemind)
	{
		this.isRemind = isRemind;
	}
	public String getActionDesc() {
		return actionDesc;
	}
	public void setActionDesc(String actionDesc) {
		this.actionDesc = actionDesc;
	}
	public String getEndDt() {
		return endDt;
	}
	public void setEndDt(String endDt) {
		this.endDt = endDt;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getStartDt() {
		return startDt;
	}
	public void setStartDt(String startDt) {
		this.startDt = startDt;
	}
	public String getTaskDesc() {
		return taskDesc;
	}
	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRemindTime() {
		return remindTime;
	}
	public void setRemindTime(String remindTime) {
		this.remindTime = remindTime;
	}
	public String getRateValue() {
		return rateValue;
	}
	public void setRateValue(String rateValue) {
		this.rateValue = rateValue;
	}
	public int getRateType() {
		return rateType;
	}
	public void setRateType(int rateType) {
		this.rateType = rateType;
	}
	public String getVhActionId() {
		return vhActionId;
	}
	public void setVhActionId(String vhActionId) {
		this.vhActionId = vhActionId;
	}

	public String getUpdateStatusDt() {
		return updateStatusDt;
	}
	public void setUpdateStatusDt(String updateStatusDt) {
		this.updateStatusDt = updateStatusDt;
	}
	@Override
	public boolean equals(Object o) {
		return this.id.equals(((RecommondInfo)o).id);
	}
	
}
