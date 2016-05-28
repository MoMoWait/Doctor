package com.comvee.tnb.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author linbin
 */
public class HomeIndex implements Serializable {

    public ArrayList<Tunrs> turns;
    public SugarPay sugar_pay;

    public static class Tunrs implements Serializable {
        public String content;
        public int turn_type;
        public String title;
        public String button;
        public String turn_to;
        public String photo;
    }


    public static class SugarPay implements Serializable {
        public String money;
        public int money_package;
        public int user_flag;
        public Alert alert;
        public String turn_to;
        public String title;


    }

    public static class Alert {
        public String pic;
        public String turnUrl;
        public String title;
    }

}
