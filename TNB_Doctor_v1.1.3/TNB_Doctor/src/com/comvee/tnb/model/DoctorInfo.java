package com.comvee.tnb.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by friendlove-pc on 16/3/25.
 */
public class DoctorInfo implements Serializable {

    public String HOSPITAL_ID;
    public String PER_POSITION;
    public String PER_NAME;
    public String PER_PER_REAL_PHOTO;
    public String USER_ID;
    public String PER_SPACIL;
    public int if_doctor;
    public String DEPARTMENT;
    public String PER_REAL_PHOTO;
    public String HOS_NAME;
    public String TAGS;
    public ArrayList<PackageInfo> doctorPackageInfo;

    public static class PackageInfo implements Serializable {
        public boolean isHaveCoupon;
        public String ownerId;
        public String packageCode;
        public String packageId;
        public String packageImg;
        public String packageImgThumb;
        public String packageName;
        public String packageOwnerId;
        public String priceShow;


    }

}
