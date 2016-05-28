package com.comvee.tnb.model;

import java.io.Serializable;

public class TendencyInputModel implements Serializable{

	/**
	 * 
	 */
	
	public static final String LABEL = "label";
	public static final String IS_FLOAT = "isFloat";
	public static final String DEF_DATE = "defDate";
	public static final String TYPE_DISPLAY = "type_display";
	public static final String TABLE_NAME = "tendency_input";
	
	public String label;//标题
	public boolean isFloat;//是否有浮点
	public String defDate;//默认日期
	public int typeDisplay;//展示形式  0、默认1、多排输入
	
	@Override
	public boolean equals(Object o) {
		return this.label.equals(((TendencyInputModel)o).label);
	}
	
}
