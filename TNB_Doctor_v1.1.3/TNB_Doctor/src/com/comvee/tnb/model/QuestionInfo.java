package com.comvee.tnb.model;

import java.util.List;

public class QuestionInfo {
	public int tie;
	public String con;
	public String help;
	public int showSeq;// 题序（题号）
	public int itemType;// 选项类型1、日期2、单选3、多选4、填空
	public String vhQid;// 老外的id(code)
	public String qid;// 问题id
	public int quesType;// 高风险评估、低风险评估
	
	public boolean isNeed;//是否必填
	public int goTo;//非必填 可以调到 goto 位置
	
	public int isAnswer;//是否自动 填题
	public String realValue;//真实 报文 值 、、{“”：“”，“”：‘’}
	public List<QuestionItemInfo> itemList;
}
