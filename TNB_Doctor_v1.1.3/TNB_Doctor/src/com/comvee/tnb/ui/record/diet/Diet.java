package com.comvee.tnb.ui.record.diet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.comvee.tnb.ui.record.common.NetPic;

public class Diet implements Serializable {
	
	public String id;// 食谱ID
	public String name;// 食谱名称
	public String time;// 食谱创建时间
	public String period;// 食谱类型（1，2，3，分别表示早，中，晚）
	public float beforeSugarValue;// 餐前血糖值
	public float afterSugarValue;// 餐后血糖值
	public int isCollect;// 是否被收藏，0未收藏
	List<NetPic> netpics = new ArrayList<NetPic>();
}
