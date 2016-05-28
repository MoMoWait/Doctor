package com.comvee.tnb.model;

import java.util.ArrayList;

public class ServerApplyInfo {

	public String packageImg;
	public String packageName;
	public String saleFeeMemo;
	public String memo;
	public int hasSale;
	public String packageOwnerId;
	public String decs;
	public int memberIsBuy;//1、已申请0、未
	public ArrayList<ServerApplyItem> items;
}
