package com.comvee.tnb.model;

import java.io.Serializable;

public class FoodInfo implements Serializable {

	public String imgUrl;
	public String id;// 所属类型 id
	public String name;
	public float heat;// 热量
	public float weight;// 所含热量（heat）对应的重量
	public float selectWeight = -1;// 选择的重量
	public float selectHeat = -1;// 选择的重量所对应的热量
	public String recommendWeight;// 建议重量
	public String recommendHeat;// 建议热量
	public String eatAdvice;// 吃的建议
	public String foodId;// 食物id
	public String gi;
}
