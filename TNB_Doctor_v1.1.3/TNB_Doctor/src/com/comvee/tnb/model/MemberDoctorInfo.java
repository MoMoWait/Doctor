package com.comvee.tnb.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Administrator
 * 
 */
public class MemberDoctorInfo implements Serializable {
	public String DEPARTMENT;
	public String HOSPITAL_ID;
	public String HOS_NAME;
	public String PER_NAME;
	public String PER_PER_REAL_PHOTO;
	public String PER_POSITION;
	public String PER_REAL_PHOTO;
	public String PER_SPACIL;
	public String TAGS;
	public String USER_ID;
	public String if_doctor;
	public ArrayList<PackageList> serverList;
	public int isPhoneService;
	public int isBookService;

	public static class PackageList implements Serializable {
		/**
		 * 
		 */

		public packageInfo packageInfo;
		public ArrayList<ServerList> serverList;

		public static class packageInfo implements Serializable {
			/**
			 * 
			 */
			
			public String doctorId;
			public String doctorImage;
			public String doctorName;
			public String endDt;
			public String hospitalName;
			public String packageName;
			public String positionName;
			public String speciality;
			public String startDt;
			public String tags;
			public String valueDate;
		}

		public static class ServerList implements Serializable {
			/**
			 * 
			 */
			
			public String content;
			public String redidueNum;
			public String serverCode;
		}
	}
}
