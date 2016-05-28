package com.comvee.tnb.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BalanceStatementInfo implements Serializable {
	public String familyId;
	public String insertDt;// 插入时间
	public String content;
	public String memberId;
	public String memberName;// 成员姓名
	public String money;// 金额
	public String moneyType;// 转入类型
	public String recordPic;// 图标
	public String recordType;// 转入类型

}
