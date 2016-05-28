package com.comvee.tnb.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by friendlove-pc on 16/3/18.
 */
public class AskIndexInfo implements Serializable {
	public int addDoctorShow;
    public String qusrepository;

    public ArrayList<RecommendDoctor> recommendDoctors;
    public ArrayList<MemMsg> memMsg;
    public ArrayList<Banner> banner;


    public static class Banner implements Serializable {
		public String turnurl;
        public int bannerType;
        public String imgurl;
    }

    public static class MemMsg implements Serializable {
		public String doctorName;
        public String headImageUrl;
        public String insertDt;
        public int count;
        public String doctorId;
        public boolean isAdvisors;
        public String dataStr;
        public String userMsg;

    }


    public static class RecommendDoctor implements Serializable {
		public String businessDoctorId;
        public String doctorName;//医生名
        public String positionName;// "副主任医师",
        public String hospitalName;//": "福建医科大学附属协和医院",
        public String tags;//": "糖尿病^$%并发症^$%2型",
        public String photoUrl;
        public String introduction;
        public String speciality;
        public String departId;
        public String tagsId;
    }


}
