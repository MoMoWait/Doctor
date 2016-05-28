package com.comvee.tnb.model;

import java.util.ArrayList;

public class IndexVoucherModel {
	private int type;
	private ArrayList<DiscountItem> list;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ArrayList<DiscountItem> getList() {
		return list;
	}

	public void setList(ArrayList<DiscountItem> list) {
		this.list = list;
	}

	public static class DiscountItem {
		private double couponsCondition;// 优惠条件
		private String couponsName;// 优惠券名称
		private int couponsType;// 优惠券类型1、实物商品 2、虚拟服务
		private String endDt;// 结束时间
		private String insertDt;
		private int isValid;// 是否有效
		private int preferentialPrice;// 优惠价格
		private String remark;// 备注
		private String sid;
		private String startDt;// 开始时间
		private String url;// 跳转链接
		private int number;// 数量

		public double getCouponsCondition() {
			return couponsCondition;
		}

		public void setCouponsCondition(double couponsCondition) {
			this.couponsCondition = couponsCondition;
		}

		public String getCouponsName() {
			return couponsName;
		}

		public void setCouponsName(String couponsName) {
			this.couponsName = couponsName;
		}

		public int getCouponsType() {
			return couponsType;
		}

		public void setCouponsType(int couponsType) {
			this.couponsType = couponsType;
		}

		public String getEndDt() {
			return endDt;
		}

		public void setEndDt(String endDt) {
			this.endDt = endDt;
		}

		public String getInsertDt() {
			return insertDt;
		}

		public void setInsertDt(String insertDt) {
			this.insertDt = insertDt;
		}

		public int getIsValid() {
			return isValid;
		}

		public void setIsValid(int isValid) {
			this.isValid = isValid;
		}

		public int getPreferentialPrice() {
			return preferentialPrice;
		}

		public void setPreferentialPrice(int preferentialPrice) {
			this.preferentialPrice = preferentialPrice;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public String getSid() {
			return sid;
		}

		public void setSid(String sid) {
			this.sid = sid;
		}

		public String getStartDt() {
			return startDt;
		}

		public void setStartDt(String startDt) {
			this.startDt = startDt;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public int getNumber() {
			return number;
		}

		public void setNumber(int number) {
			this.number = number;
		}

	}
}
