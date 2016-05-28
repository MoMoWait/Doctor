package com.comvee.tnb.model;

import java.util.ArrayList;

import com.comvee.annotation.sqlite.Id;
import com.comvee.annotation.sqlite.Table;
import com.comvee.annotation.sqlite.Transient;
import com.comvee.tnb.widget.Voice.VoiceInfo;

@Table(name = "NewAskModel")
public class NewAskModel {
    @Id(column = "id")
    private int id;
    private long sid;// 每条消息的主键
    private long docTellId;// 用于获取医生电话咨询列表的id
    private int is_phone;// 医生是否开通电话咨询服务 0无1有
    private int is_question;// 套餐情况

    private int currentPage;// 当前页
    private int pageSize;// 每页条数
    private int totalPages;// 总页数
    private int totalRows;// 总条数
    private String headImageUrl = "";// 头像地址
    // rows中的数据
    private int isCount;// 是否计数 1 计数 0 不计数
    private long doctorId;// 医生id
    private int ownerType;// 消息所有者 1 患者 2医生
    private String msgContent = "";// 文案
    private int msgType;// 消息类型 1 血糖异常 2 血压异常 3 BMI异常 4 咨询 5 任务推荐 6 创建随访 7 完成随访
    // 8 创建日程提醒 9 请求确认医患关系 10 通过医患关系
    private long memberId;// 成员id
    private String insertDt = "";// 时间 yyyy-MM-dd HH:mm:ss;
    private int isDispose;// 处理状态 1 已处理 2未处理
    private String dataStruct = "";// rows中的业务数据结构的消息体


    private String dataStr;
    private int unDealFollow;// 本地计数未处理的随访数量，用于上方的提示栏

    // dataStruct中的字段
    private String recordTime = "";// 记录时间,通用
    private String paramCode = "";// 参数 code,各种异常通用
    private int paramLevel;// 水平,各种通用
    private String title = "";// 标题,通用
    // 血糖异常
    private double bloodglucoseValue;// 血糖值
    private String unit = "";// 单位值
    // 血压异常
    private int bloodpressuresystolic;// 高压值
    private int bloodpressurediastolic;// 低压值
    // BMI异常
    private double bmiValue;// BMI值
    private int heigth;// 身高
    private int weight;// 体重
    // 任务推荐
    private String content = "";// 内容,随访,图文咨询也用
    private String jobList = "";// 多个用,号隔开
    // 创建随访，完成随访
    private int followupType;// 随访类型
    private long foreignId;// 随访的请求id
    private int entrance;// 创建随访入口 1.患者填写，医生不能； 2.医生填写，患者不能
    // 图文咨询
    private String attachList = "";// 图文咨询内容 list结构 用来取内容的，不用设置获取
    private String attachUrl = "";// attachlist中的
    private String attachType = "";// attachlist中的类型 0 图片 1 语音
    private int voiceMins;// attachlist中的语音长度

    public VoiceInfo voice;
    // 患者日程提醒
    private String remindTitle = "";// 提醒事项
    private String date = "";// 日期
    private String time = "";// 时间
    private int remindPeople;// 提醒人群 1 医生 2 患者 3 医生&&患者
    private String remark = "";// 备注
    // 预约
    private int status;// 预约状态 （0待回访，1已完成，2已过期）
    private String startTime = "";// 开始时间
    private String endTime = "";// 结束时间
    private String doctorName = "";// 医生姓名
    private int messageState;// 消息发送状态，1表示发送中，2表示发送成功 ，3表示发送失败

    private String localUrl;// 本地文件地址
    @Transient
    private ArrayList<ServerListModel> list;
    private String reqNum;// 标识符
    private String isValid;

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public ArrayList<ServerListModel> getList() {
        return list;
    }

    public void setList(ArrayList<ServerListModel> list) {
        this.list = list;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    public String getDataStr() {
        return dataStr;
    }

    public void setDataStr(String dataStr) {
        this.dataStr = dataStr;
    }

    public long getDocTellId() {
        return docTellId;
    }

    public void setDocTellId(long docTellId) {
        this.docTellId = docTellId;
    }

    public int getIs_phone() {
        return is_phone;
    }

    public void setIs_phone(int is_phone) {
        this.is_phone = is_phone;
    }

    public int getIs_question() {
        return is_question;
    }

    public void setIs_question(int is_question) {
        this.is_question = is_question;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    public int getIsCount() {
        return isCount;
    }

    public void setIsCount(int isCount) {
        this.isCount = isCount;
    }

    public long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(long doctorId) {
        this.doctorId = doctorId;
    }

    public int getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(int ownerType) {
        this.ownerType = ownerType;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
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

    public String getDataStruct() {
        return dataStruct;
    }

    public void setDataStruct(String dataStruct) {
        this.dataStruct = dataStruct;
    }

    public int getUnDealFollow() {
        return unDealFollow;
    }

    public void setUnDealFollow(int unDealFollow) {
        this.unDealFollow = unDealFollow;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public String getParamCode() {
        return paramCode;
    }

    public void setParamCode(String paramCode) {
        this.paramCode = paramCode;
    }

    public int getParamLevel() {
        return paramLevel;
    }

    public void setParamLevel(int paramLevel) {
        this.paramLevel = paramLevel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getBloodglucoseValue() {
        return bloodglucoseValue;
    }

    public void setBloodglucoseValue(double bloodglucoseValue) {
        this.bloodglucoseValue = bloodglucoseValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getBloodpressuresystolic() {
        return bloodpressuresystolic;
    }

    public void setBloodpressuresystolic(int bloodpressuresystolic) {
        this.bloodpressuresystolic = bloodpressuresystolic;
    }

    public int getBloodpressurediastolic() {
        return bloodpressurediastolic;
    }

    public void setBloodpressurediastolic(int bloodpressurediastolic) {
        this.bloodpressurediastolic = bloodpressurediastolic;
    }

    public double getBmiValue() {
        return bmiValue;
    }

    public void setBmiValue(double bmiValue) {
        this.bmiValue = bmiValue;
    }

    public int getHeigth() {
        return heigth;
    }

    public void setHeigth(int heigth) {
        this.heigth = heigth;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getJobList() {
        return jobList;
    }

    public void setJobList(String jobList) {
        this.jobList = jobList;
    }

    public int getFollowupType() {
        return followupType;
    }

    public void setFollowupType(int followupType) {
        this.followupType = followupType;
    }

    public long getForeignId() {
        return foreignId;
    }

    public void setForeignId(long foreignId) {
        this.foreignId = foreignId;
    }

    public int getEntrance() {
        return entrance;
    }

    public void setEntrance(int entrance) {
        this.entrance = entrance;
    }

    public String getAttachList() {
        return attachList;
    }

    public void setAttachList(String attachList) {
        this.attachList = attachList;
    }

    public String getAttachUrl() {
        return attachUrl;
    }

    public void setAttachUrl(String attachUrl) {
        this.attachUrl = attachUrl;
    }

    public String getAttachType() {
        return attachType;
    }

    public void setAttachType(String attachType) {
        this.attachType = attachType;
    }

    public int getVoiceMins() {
        return voiceMins;
    }

    public void setVoiceMins(int voiceMins) {
        this.voiceMins = voiceMins;
    }

    public String getRemindTitle() {
        return remindTitle;
    }

    public void setRemindTitle(String remindTitle) {
        this.remindTitle = remindTitle;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getRemindPeople() {
        return remindPeople;
    }

    public void setRemindPeople(int remindPeople) {
        this.remindPeople = remindPeople;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public int getMessageState() {
        return messageState;
    }

    public void setMessageState(int messageState) {
        this.messageState = messageState;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public String getReqNum() {
        return reqNum;
    }

    public void setReqNum(String reqNum) {
        this.reqNum = reqNum;
    }

}
