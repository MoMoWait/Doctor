package com.comvee.tnb.model;

public class ServerDetailsInfo {
	private String orderId;// 订单id
	private String orderMoney;// 订单价格
	private String orderStatus;// 订单状态 0未付款1已购买2已退款
	private String origin;// ORIGIN003安卓ORIGIN004 苹果
	private String payAccount;// 下单账号
	private String payStatus;// 1支付宝2微信
	private String regAccount;// 下单人掌控端账号 手机号
	private String userName;// 下单名称 下单人主成员姓名
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderMoney() {
		return orderMoney;
	}
	public void setOrderMoney(String orderMoney) {
		this.orderMoney = orderMoney;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getPayAccount() {
		return payAccount;
	}
	public void setPayAccount(String payAccount) {
		this.payAccount = payAccount;
	}
	public String getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}
	public String getRegAccount() {
		return regAccount;
	}
	public void setRegAccount(String regAccount) {
		this.regAccount = regAccount;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
