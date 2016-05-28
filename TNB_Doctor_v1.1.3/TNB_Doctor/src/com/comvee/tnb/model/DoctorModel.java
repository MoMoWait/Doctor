package com.comvee.tnb.model;

public class DoctorModel {
	private String birthday;// 生日 yyyy-MM-dd
	private String perSpacil;// 专长
	private String departmentNameText;// 科室文本
	private String perName;// 姓名
	private String perSex;// 性别 1 男 2女
	private String doctorId;// 医生id
	private String perRealPhotos;// 照片地址
	private String perRealPhoto;// 缩略照片地址
	private String province;// 省份id (修改时需传)
	private String signature;// 个性签名
	private String departmentName;// 科室值 (修改时需传)
	private String position;// 职称值 (修改时需传)
	private String hospitalName;// 医院名值 (修改时需传)
	private String city;// 城市id (修改时需传)
	private String tags;// 医生标签
	private String perContent;// 医生描述
	private boolean isMyPackage;
	private String hospitalNameText;

	public String getHospitalNameText() {
		return hospitalNameText;
	}

	public void setHospitalNameText(String hospitalNameText) {
		this.hospitalNameText = hospitalNameText;
	}

	public String getPerContent() {
		return perContent;
	}

	public void setPerContent(String perContent) {
		this.perContent = perContent;
	}

	public boolean isMyPackage() {
		return isMyPackage;
	}

	public void setMyPackage(boolean isMyPackage) {
		this.isMyPackage = isMyPackage;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}


	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getPerSpacil() {
		return perSpacil;
	}

	public void setPerSpacil(String perSpacil) {
		this.perSpacil = perSpacil;
	}

	public String getDepartmentNameText() {
		return departmentNameText;
	}

	public void setDepartmentNameText(String departmentNameText) {
		this.departmentNameText = departmentNameText;
	}

	public String getPerName() {
		return perName;
	}

	public void setPerName(String perName) {
		this.perName = perName;
	}

	public String getPerSex() {
		return perSex;
	}

	public void setPerSex(String perSex) {
		this.perSex = perSex;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getPerRealPhotos() {
		return perRealPhotos;
	}

	public void setPerRealPhotos(String perRealPhotos) {
		this.perRealPhotos = perRealPhotos;
	}

	public String getPerRealPhoto() {
		return perRealPhoto;
	}

	public void setPerRealPhoto(String perRealPhoto) {
		this.perRealPhoto = perRealPhoto;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}
