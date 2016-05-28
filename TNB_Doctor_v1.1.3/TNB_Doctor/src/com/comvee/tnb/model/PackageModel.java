package com.comvee.tnb.model;

import java.util.List;

import com.comvee.tnb.ui.voucher.VoucherModel;

/**
 * @author Administrator
 * 
 */
public class PackageModel {
	private int packageType;// 套餐类型 0 公有 1 私有
	private String feeNum;// 套餐价格
	private String packageBrand;// 套餐类型
	/**
	 * 
	 * TWZXTC图文, DHZXTC电话 ,SRYSTC私人医生, KWYSTC_XTY血糖仪
	 * 
	 */
	private String packageCode;// 套餐编码
	private String packageName;// 套餐名称
	private long packageid;// 套餐id
	private int user_flag;// 有效期限 使用方式 1一次性 2按天 3包月 4包季度 5包年 6 包半年7周
	private int use_num;
	private String use_unit;
	private String memo;// 描述
	private String packageImg;// 图片地址
	private List<VoucherModel> voucherList;
	private boolean isMyPackage;

	public boolean isMyPackage() {
		return isMyPackage;
	}

	public void setMyPackage(boolean isMyPackage) {
		this.isMyPackage = isMyPackage;
	}

	public List<VoucherModel> getVoucherList() {
		return voucherList;
	}

	public void setVoucherList(List<VoucherModel> voucherList) {
		this.voucherList = voucherList;
	}

	public int getPackageType() {
		return packageType;
	}

	public void setPackageType(int packageType) {
		this.packageType = packageType;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getPackageImg() {
		return packageImg;
	}

	public void setPackageImg(String packageImg) {
		this.packageImg = packageImg;
	}

	public int getUse_num() {
		return use_num;
	}

	public void setUse_num(int use_num) {
		this.use_num = use_num;
	}

	public String getUse_unit() {
		return use_unit;
	}

	public void setUse_unit(String use_unit) {
		this.use_unit = use_unit;
	}

	public String getFeeNum() {
		return feeNum;
	}

	public int getUser_flag() {
		return user_flag;
	}

	public void setUser_flag(int user_flag) {
		this.user_flag = user_flag;
	}

	public void setFeeNum(String feeNum) {
		this.feeNum = feeNum;
	}

	public String getPackageBrand() {
		return packageBrand;
	}

	public void setPackageBrand(String packageBrand) {
		this.packageBrand = packageBrand;
	}

	/**
	 * TWZXTC图文, DHZXTC电话 ,SRYSTC私人医生, SUGAR血糖仪
	 */
	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public long getPackageid() {
		return packageid;
	}

	public void setPackageid(long packageid) {
		this.packageid = packageid;
	}

}
