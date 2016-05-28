package com.comvee.tnb.model;

import java.util.ArrayList;

public class OrderInfo {

	public String payMoney;
	public String orderId;
	public String userName;
	public String logisticsNum;
	public int payStatus;// 0为未支付，1为支付2配送中3已退款4已换货5订单取消6订单完成7货到付款
	public String orderType;
	public String address;
	public String logisticsFirmName;
	public String orderNum;
	public String insertDt;
	public String mobile;
	public ArrayList<OrderItemInfo> list;
	public int totalNum;
	public String packageRemark;
	public int type;// 产品类型  1实体产品2 虚拟产品

}
