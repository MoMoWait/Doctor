package com.comvee.tnb.model;

import java.util.ArrayList;

public class AskListModel {
	public int curPage = 0;
	public int totalPage = 0;
	public ArrayList<AskServerInfo> arrayList;
	public int unReadCount;
	public ArrayList<AskLoopModel> askLoopModels;
	public ArrayList<DocDetailModel> docDetailModels;
}
