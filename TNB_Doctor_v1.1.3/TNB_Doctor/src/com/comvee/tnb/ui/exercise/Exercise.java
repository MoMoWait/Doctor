package com.comvee.tnb.ui.exercise;

import java.io.Serializable;

public class Exercise extends Object implements Serializable {
	public String id;//运动类型ID
	public String level;
	public String name;
	public String imgUrl;
	public String caloriesOneMinutes;//
	public String caloriesThirtyMinutes;
	
	public String converLevel() {
		if ("1".equals(level)) {
			return "轻度运动";
		} else if ("2".equals(level)) {
			return "轻中强度运动";
		} else if ("3".equals(level)) {
			return "中强度运动";
		} else {
			return "强度运动";
		}
	}

}
