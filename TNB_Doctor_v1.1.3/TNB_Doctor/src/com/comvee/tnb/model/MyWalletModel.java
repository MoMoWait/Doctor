package com.comvee.tnb.model;

import java.io.Serializable;
import java.util.ArrayList;

public class MyWalletModel implements Serializable{

	public String money;
	public String card_count;
	public ArrayList<ShowList> showList;
	public static  class ShowList {
				public String pic;//"pic": "aaa",
				public String title;//"title": "商城",
				public String turn_type;//"turn_type": "0",
				public String turn_url;//"turn_url": "http://www.baidu.com"

	}

}
