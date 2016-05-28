package com.comvee.tnb.model;

import java.io.Serializable;

/**
 * 首页活动相关实体类
 * 
 * @author linbin
 * 
 */
public class SugarPay implements Serializable {

	

	private Alert alert;//首页弹窗
	

	public Alert getAlert() {
		return alert;
	}

	public void setAlert(Alert alert) {
		this.alert = alert;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getMoney_package() {
		return money_package;
	}

	public void setMoney_package(String money_package) {
		this.money_package = money_package;
	}

	private String money;// 奖池剩余金额，现在返回一个，后面确定下来可能是一个数组，然后你们根据数组显示金额

	private String money_package;//判断用户今天拆过红包没，1没拆过，0拆过了。血糖仪记录血糖的时候会用到这个字段

}
