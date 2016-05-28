package com.comvee.tnb.model;

import java.io.Serializable;

import com.comvee.annotation.sqlite.Table;

@Table(name="alarm_info")
public class AlarmInfo implements Serializable{
	private String id;
	private String taskId;
	private String time;
	private int status;
	private int type;//1、自定义提醒2、第一次提醒3、第二次提醒4、最后两天提醒
	private String msg ="";//提醒内容
	private String title="";
	
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		
		StringBuffer sb = new StringBuffer();
		sb.append("id:").append(id);
		sb.append("rmdID:").append(taskId);
		sb.append("time:").append(time);
		sb.append("status:").append(status);
		sb.append("type:").append(type);
		sb.append("msg:").append(msg);
		sb.append("title:").append(title);
		
		return sb.toString();
	}

}
