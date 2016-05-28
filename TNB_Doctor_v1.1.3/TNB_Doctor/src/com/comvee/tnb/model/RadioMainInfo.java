package com.comvee.tnb.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RadioMainInfo implements Serializable {
	
	public ArrayList<RadioGroup> newProgramList;//栏目列表
	public ArrayList<RadioTurns> turnsList;//轮播图列表
	public String turn_market_url;//商城地址
	public int isReg;//是否注册过环信
}
