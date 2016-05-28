package com.comvee.tnb.model;

import java.io.Serializable;

public class MemberInfo implements Serializable {

	public String photo = "";
	public String name = "TA";
	public String type = "";
	public String mId = "";
	public int coordinator;// 1、户主
	public String birthday = "";
	public int callreason = 1;// 是否糖尿病
	public String memberHeight = "";// 身高
	public String memberWeight = "";// 体重
	public String sex;
	public String relative;
	public String isTnb;

	public int birInt;
	public int diseaInt;

	public int hasMachine;// 是否有绑定设备

	public String waistline;// 腰围
	public String issport;// 是否运动
	public String iseatveg;// 是否吃蔬菜
	public String ishighblood;// 是否高血压
	public String issugar;// 血糖
	public String isfamiysugar;// 家庭成员是否高血糖
	public int goal;// 目标

	public int score;// 风险值
	public String testMsg;// 风险提示语
	public String diabetes_plan;// web介绍地址
	public String score_describe;
	public String diabetesTime;// 确诊时间
	public boolean ifLogin;

	public String getRelativeChinese() {
		if ("CBYBRGX001".equals(relative)) {
			return "你";
		} else if ("CBYBRGX002".equals(relative)) {
			return "父亲";
		} else if ("CBYBRGX003".equals(relative)) {
			return "母亲";
		} else if ("CBYBRGX008".equals(relative)) {
			return "丈夫";
		} else if ("CBYBRGX009".equals(relative)) {
			return "妻子";
		} else if ("CBYBRGX010".equals(relative)) {
			return "爷爷";
		} else if ("CBYBRGX011".equals(relative)) {
			return "奶奶";
		} else if ("CBYBRGX012".equals(relative)) {
			return "外公";
		} else if ("CBYBRGX013".equals(relative)) {
			return "外婆";
		} else if ("CBYBRGX014".equals(relative)) {
			return "儿子";
		} else if ("CBYBRGX015".equals(relative)) {
			return "女儿";
		} else if ("CBYBRGX005".equals(relative)) {
			return "亲戚";
		} else if ("CBYBRGX006".equals(relative)) {
			return "朋友";
		} else if ("CBYBRGX007".equals(relative)) {
			return "TA";
		}
		return "你";
	}
}
