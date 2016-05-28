package com.comvee.tnb.guides;

public class IndexTaskInfo {

	public static final int JUMP_BROWSE = 1;// 跳转到阅读模块
	public static final int JUMP_QUES = 2;// 跳转到问题模块
	public static final int JUMP_FULL_CHECK = 8;// 跳转到全面检查
	public static final int JUMP_FOOD_RECOMMED = 5;// 饮食推荐
	public static final int JUMP_FOOD_RECIPE = 6;// 食谱
	public static final int JUMP_SPORTS_VALUE = 7;// 运动任务
	public static final int JUMP_SPORTS_VALUE_HTML = 9;// 运动任务点击继续后的跳转
	public static final int JUMP_BLOOD_SUGAR_MONITOR = 3;// 血糖监测
	public static final int JUMP_BLOOD_SUGAR_TEST = 4;// 完成一次血糖监测
	public static final int JUMP_NEW_FOOD_RECIOE=10;//新食谱结果页
	public static final int JUMP_NEW_FOOD_RECIOE_INDEX=11;//新食谱结果页
	public static final int JUMP_NEW_SPORTS_VALUE=12;//新运动任务
	public static final int JUMP_NEW_SPORTS_VALUE_1=13;//新运动任务 温馨提醒
	public static final int JUMP_NEW_RESULT=14;//结果页

	private int linkType;
	private int linktask;
	private int linktasktype;
	private String linkRelation;
	private String linktaskTime;
	private int total;

	// private String taskCode;
	private int taskCode;
	// private String seq;
	private int seq;// 任务排序
	private int type;
	private String title;
	private String subtitle;
	private String icon;
	private int status;
	private String date = "";
	private String finishDate;
	private String id;
	private String taskId;// 任务ID
	private String relation;
	private int isNew;
	private int relations;// 关系
	private String taskTime;// 任务时间
	private int stage;// 任务阶段
	private String titleBar;// 人物类型
//	private int taskStatus;
	private boolean canBackIndex = true;// 阅读任务判断是否返回首页 临时
	private int tempStatu = -1;//任务是否完成

	public int getTempStatu() {
		return tempStatu;
	}

	public void setTempStatu(int tempStatu) {
		this.tempStatu = tempStatu;
	}

	public String getRelation() {
		return relation;
	}

	public boolean isCanBackIndex() {
		return canBackIndex;
	}

	public void setCanBackIndex(boolean canBackIndex) {
		this.canBackIndex = canBackIndex;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getLinktaskTime() {
		return linktaskTime;
	}

	public void setLinktaskTime(String linktaskTime) {
		this.linktaskTime = linktaskTime;
	}

	public String getLinkRelation() {
		return linkRelation;
	}

	public void setLinkRelation(String linkRelation) {
		this.linkRelation = linkRelation;
	}

	public int getIsNew() {
		return isNew;
	}

	public void setIsNew(int isNew) {
		this.isNew = isNew;
	}

	public int getLinkType() {
		return linkType;
	}

	public void setLinkType(int linkType) {
		this.linkType = linkType;
	}

	public int getLinktask() {
		return linktask;
	}

	public void setLinktask(int linktask) {
		this.linktask = linktask;
	}

	public int getLinktasktype() {
		return linktasktype;
	}

	public void setLinktasktype(int linktasktype) {
		this.linktasktype = linktasktype;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(String finishDate) {
		this.finishDate = finishDate;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public int getRelations() {
		return relations;
	}

	public void setRelations(int relations) {
		this.relations = relations;
	}

	public String getTaskTime() {
		return taskTime;
	}

	public void setTaskTime(String taskTime) {
		this.taskTime = taskTime;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	public String getTitleBar() {
		return titleBar;
	}

	public void setTitleBar(String titleBar) {
		this.titleBar = titleBar;
	}

//	public int getTaskStatus() {
//		return taskStatus;
//	}
//
//	public void setTaskStatus(int taskStatus) {
//		this.taskStatus = taskStatus;
//	}
}
