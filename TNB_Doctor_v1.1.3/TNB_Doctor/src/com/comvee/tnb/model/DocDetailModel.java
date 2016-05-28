package com.comvee.tnb.model;

import java.io.Serializable;
import java.util.ArrayList;

public class DocDetailModel implements Serializable {
	public String hospitalName;
	public String businessDoctorId;
	public String doctorName;
	public String photoUrl;
	public String positionName;
	public String tags;
	public int if_doctor;
	public ArrayList<DoctorPackageInfo> arrayList;



}
