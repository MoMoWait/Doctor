package com.comvee.tnb.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by friendlove-pc on 16/5/11.
 */
public class AssessQuestion implements Serializable{

    public ArrayList<QuestionItem> items;
    public Question ques;
    public String displayValue;
    public static class Question{
        public String help;
        public int itemType;
        public String realValue;

        public String con;
        public int goTo;
        public String vhQid;
        public int isNeed;
        public int showSeq;
        public String qid;
        public int quesType;
        public String isAnswer;
    }

    public static class QuestionItem implements Serializable{

        public String item;
        public String itemId;
        public int jump;
        public String value;
        public String qid;
        public int tie;//1、以上都没有的标识

    }


}
