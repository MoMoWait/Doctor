package com.comvee.tnb.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AccountBalancefInfo implements Serializable {

	public String alipayId;
	public float money;
	public ArrayList<String> memo;
	public int isAllTake;
	public String alipayName;
	public String alertMemo;
	public String bannerMemo;

}
