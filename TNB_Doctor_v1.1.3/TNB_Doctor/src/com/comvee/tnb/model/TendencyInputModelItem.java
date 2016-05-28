package com.comvee.tnb.model;

import java.io.Serializable;

public class TendencyInputModelItem implements Serializable{

	/**
	 * 
	 */
	
	public static final String SUPER_LABEL = "super_label";
	public static final String NAME = "name";
	public static final String IS_FLOAT = "isFloat";
	public static final String MAX_VALUE = "maxValue";
	public static final String MIN_VALUE = "minValue";
	public static final String DEF_VALUE = "defValue";
	public static final String TABLE_NAME = "tendency_input_item";
	public static final String UNIT = "strUnit";
	public static final String LIMIT_MAX = "limitMax";
	public static final String LIMIT_MIN = "limitMin";
	public static final String CODE = "code";
	public static final String COLOR = "color";
	
	public String code;
	public String strUnit;//单位
	public int limitMax;//限制输入最大值
	public int limitMin;//限制输入最小值
	public String realValue="";//真实数据
	public String super_label;//标题
	public String name;//父标题
	public boolean isFloat;//是否有浮点
	public float defValue;//默认值
	public float maxValue;//最大值
	public float minValue;//最小值
	public String color;//线的颜色
	
	@Override
	public boolean equals(Object o) {
		return this.name.equals(((TendencyInputModelItem)o).name)||this.code.equals(((TendencyInputModelItem)o).code);
	}
	
}
