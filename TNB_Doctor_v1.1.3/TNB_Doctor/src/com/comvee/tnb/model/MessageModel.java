package com.comvee.tnb.model;

public class MessageModel {
	private String num;// 数量
	private String title;// 标题
	private String status;// 完成状态
	private String memberJobDetailId;// id
	private String score;// 得分
	private String memberJobId;// 大任务id
	private String caption;// 跳转标题
	private String jobDetailUrl;// 跳转url
	private String jobDetailType;// 跳转类型
									// (0默认不跳转,1一般任务,2建议,3跳转任务用到链接,4跳转到血糖,5跳转到健康评估6进入首页7医生回复,8建议,9.我完成了,10.制定监测计划11任务中心12咨询页面13我的任务中心14上传图片15记录日志16制定目标17月度计划18:血糖信息)
	private String type;// 任务类型 1日志2评估3任务4监测5阅读6公告7温馨提示
	private String typeValue;// 任务类型描述
	private String jobCenterType;// 进任务中心 展示的任务类型
	private String insertDt;// 时间
	private String detailInfo;
	public String getDetailInfo() {
		return detailInfo;
	}

	public void setDetailInfo(String detailInfo) {
		this.detailInfo = detailInfo;
	}

	public String getInsertDt() {
		return insertDt;
	}

	public void setInsertDt(String insertDt) {
		this.insertDt = insertDt;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMemberJobDetailId() {
		return memberJobDetailId;
	}

	public void setMemberJobDetailId(String memberJobDetailId) {
		this.memberJobDetailId = memberJobDetailId;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getMemberJobId() {
		return memberJobId;
	}

	public void setMemberJobId(String memberJobId) {
		this.memberJobId = memberJobId;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getJobDetailUrl() {
		return jobDetailUrl;
	}

	public void setJobDetailUrl(String jobDetailUrl) {
		this.jobDetailUrl = jobDetailUrl;
	}

	public String getJobDetailType() {
		return jobDetailType;
	}

	public void setJobDetailType(String jobDetailType) {
		this.jobDetailType = jobDetailType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeValue() {
		return typeValue;
	}

	public void setTypeValue(String typeValue) {
		this.typeValue = typeValue;
	}

	public String getJobCenterType() {
		return jobCenterType;
	}

	public void setJobCenterType(String jobCenterType) {
		this.jobCenterType = jobCenterType;
	}

}
