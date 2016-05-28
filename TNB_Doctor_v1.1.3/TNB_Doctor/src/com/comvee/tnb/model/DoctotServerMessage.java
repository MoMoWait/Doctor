package com.comvee.tnb.model;

import java.util.List;

/**
 * 套餐详情
 * 
 * @author Administrator
 * 
 */
public class DoctotServerMessage {
	private int useNum;
	private int feeNum;
	private String useUnit;
	private String packageOwnerId;
	private String packageName;
	private String ownerId;
	private int feeNumSale;
	private String packageId;
	private List<DoctorServerItemMsg> itemList;
	private String packageImg;
	private String packageCode;
	private String packageUrl;

	public String getPackageUrl() {
		return packageUrl;
	}

	public void setPackageUrl(String packageUrl) {
		this.packageUrl = packageUrl;
	}

	public int getUseNum() {
		return useNum;
	}

	public void setUseNum(int useNum) {
		this.useNum = useNum;
	}

	public int getFeeNum() {
		return feeNum;
	}

	public void setFeeNum(int feeNum) {
		this.feeNum = feeNum;
	}

	public String getUseUnit() {
		return useUnit;
	}

	public void setUseUnit(String useUnit) {
		this.useUnit = useUnit;
	}

	public String getPackageOwnerId() {
		return packageOwnerId;
	}

	public void setPackageOwnerId(String packageOwnerId) {
		this.packageOwnerId = packageOwnerId;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public int getFeeNumSale() {
		return feeNumSale;
	}

	public void setFeeNumSale(int feeNumSale) {
		this.feeNumSale = feeNumSale;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public List<DoctorServerItemMsg> getItemList() {
		return itemList;
	}

	public void setItemList(List<DoctorServerItemMsg> itemList) {
		this.itemList = itemList;
	}

	public String getPackageImg() {
		return packageImg;
	}

	public void setPackageImg(String packageImg) {
		this.packageImg = packageImg;
	}

	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}
}
