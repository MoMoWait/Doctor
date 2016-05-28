package com.comvee.tnb.model;

import java.util.ArrayList;

/**
 * @author Administrator
 * 
 */
public class MemberServerInfo {
	private String doctorImage;
	private String valueDate;
	private String packageName;
	private String doctorId;
	private ArrayList<String> arrayList;
	public String getDoctorImage() {
		return doctorImage;
	}
	public void setDoctorImage(String doctorImage) {
		this.doctorImage = doctorImage;
	}
	public String getValueDate() {
		return valueDate;
	}
	public void setValueDate(String valueDate) {
		this.valueDate = valueDate;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	public ArrayList<String> getArrayList() {
		return arrayList;
	}
	public void setArrayList(ArrayList<String> arrayList) {
		this.arrayList = arrayList;
	}
	
}
