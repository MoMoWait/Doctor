package com.comvee.tnb.model;

import java.util.ArrayList;

public class RadioCommentModel {
	public ArrayList<RadioComment> mList;
	public int currentPage;
	public int totalPages;
	public int loadType;// 加载类型 1 刷新 2 加载更多
	public int brilliantCount;// 精彩评论条数
}
