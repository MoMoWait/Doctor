package com.comvee.tnb.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 记录血糖后领取红包(如果是正常注册账号)
 * 
 * @author linbin
 */
public class GetRedForApp implements Serializable {

	public String money;// 红包金额
	public String pic;// 分享的图片
//	public String isGuest;
	public String  notisMsg;//
	public String getRedMsg;//
}
