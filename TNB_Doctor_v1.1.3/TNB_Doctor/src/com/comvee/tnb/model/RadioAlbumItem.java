package com.comvee.tnb.model;

import java.io.Serializable;

public class RadioAlbumItem implements Serializable {
	public String radioTitle;
	public String updateTime;
	public int radioType;
	public long timeLong;
	public String pariseNum;
	public String refId;// 上层ID
	public String playUrl;
	public String clickNum;
	public String radioId;// ID
	public String radioSubhead;
	public String photoUrl;
	public String collectNum;
	public String _id;
	public int state;// 1正在播放2未在播放

	public boolean beChecked;

	public int downloadedPer;// 已下载的百分比(0到100之间 )
	public long fileSize;// 原始文件大小（单位是B）
	public float speed;// 下载速度（KB/S）
	public String localFileName;// 下载到本地的文件名
	/**
	 * 1，等待下载； 2，下载中； 3，暂停下载； 4，下载失败； 5，下载成功。
	 */
	public int downloadState;
	public int isCollect;
	public boolean locationHas;
	public String shareHtml;
}
