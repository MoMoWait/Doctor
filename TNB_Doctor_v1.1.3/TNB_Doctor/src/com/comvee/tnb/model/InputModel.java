package com.comvee.tnb.model;

public class InputModel {

	public static final String TABEL_NAME = "create_menber";
	public static final String TYPE = "type";
	public static final String GROUP = "name_group";
	public static final String TAG_GROUP = "tag_group";
	public static final String TAG = "tag";
	public static final String ITEMS = "items";
	public static final String ITEM_VALUES = "item_values";
	public static final String NAME= "name";
	public static final String DEF_VALUE = "defValue";
	public static final String EXTRA = "extra";
	
	public String extra;//如果是复选型的空间需要加入特殊的字段
	public String name;
	public String tag;
	public String group;
	public int type;//0、男女1、日期3、单选4单选5复选6、、、、
	public String[] items;
	public String[] itemValues;
	public String defValue;
	public String tagGroup;
	public String value="";
	
}
