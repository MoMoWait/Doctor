package com.comvee.tool;

import com.comvee.tnb.TNBApplication;

public class ResUtil {

	public static  String getString(int res){
		return TNBApplication.getInstance().getResources().getString(res);
	}
	
}
