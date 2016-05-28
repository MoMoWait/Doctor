package com.comvee.tnb.model;

import java.io.Serializable;
import java.util.ArrayList;

public class HealthResultInfo implements Serializable {
    public int isCentre;//判断到哪个血糖结果页面
    public String desc;
    public String img;
    public String key;
    public int bloodGlucoseStatus;
    public String title;
    public String head;
 

    public List list;
    
    
    ////////////红包活动///////////
    public int money_package;//有无红包，1有红包
    public int user_flag;//1、正常状态 2、游客3、QQ登录用户  以2、3状态的用户要显示输入手机号和验证码
    ////////////红包活动///////////

    public static class List implements Serializable {
        public int linkType;
        public ArrayList<TaskInfo> taskInfo;

        public static class TaskInfo implements Serializable {
            public String subTitle;
            public int taskCode;
            public String taskID;
            public String taskIcon;
            public int taskNew;
            public String taskRelation;
            public int taskSeq;
            public int taskStatus;
            public String taskTime;
            public String title;
            public int total;
            public int type;
        }

    }

    public Sugger sugger;
    public SuggerRegular suggerRegular;
    public String remind;
    public SugarRecord bean;

    public static class Sugger implements Serializable{
        public SuggerHigh suggerHigh;
        public SuggerHigh suggerLow;
    }


    public static class SuggerRegular implements Serializable {

        public String suggerImage;
        public String suggerText;
        public String suggerTitle;
    }


    public static class SuggerHigh implements Serializable {
        public ArrayList<OptionsValue> optionsValue;
    }

    public static class OptionsValue implements Serializable {

        public String code;
        public String meaning;
        public String textTitle;
        public String hint;
        public String b;
        public ArrayList<Options> options;


    }
    public static class Options implements Serializable {
        public String option;
        public String photo;
        public String text;
        public boolean isSelected;
    }

}
