package com.comvee.tnb.model;

import java.util.ArrayList;

public class AskInfo {

	public String questionCon;
	public String fromUserName;
	public String fromUserUrl;
	public String attachUrl;
	public String insertDt;
	public String questionId;
	public int isAnswer;
	public ArrayList<AskDocInfo> list;
	public int leaveNum;
	public int newAnswer;
	
	public int questionNum;// 追问次数，
	public int replyNum;//回复次数，
	public int printNum;//图片数量
	
}
