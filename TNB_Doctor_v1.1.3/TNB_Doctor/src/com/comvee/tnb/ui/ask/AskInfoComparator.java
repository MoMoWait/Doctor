package com.comvee.tnb.ui.ask;

import java.util.Comparator;

import com.comvee.tnb.model.AskDocInfo;

public class AskInfoComparator implements Comparator<AskDocInfo> {  
  
    @Override  
    public int compare(AskDocInfo obj1, AskDocInfo obj2) {  
        int flag = -obj2.insertDt.compareTo(obj1.insertDt);  
        return flag;  
    }  
  
}  
