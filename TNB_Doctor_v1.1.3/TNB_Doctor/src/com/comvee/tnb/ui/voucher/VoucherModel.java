package com.comvee.tnb.ui.voucher;

import java.io.Serializable;

public class VoucherModel implements Serializable{
	/**
	 * 
	 */
	
	public int price;// 金额
	public String name;// 套餐名称
	public int totalNum = 1;// 总数
	public String from;// 有效期从
	public String to;// 有效期至
	public int couponTemplatePlatForm;
	public String couponTemplateId;// couponTemplateId一致的是同一种卡券
	public int status;// 1未使用，2已使用
	public String useDt;// 使用时间
	public String sid;
	public String promotionId;// 活动id

}
