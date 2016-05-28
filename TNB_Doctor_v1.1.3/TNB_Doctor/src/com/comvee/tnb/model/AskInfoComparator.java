package com.comvee.tnb.model;

import java.util.Comparator;

public class AskInfoComparator implements Comparator<AskInfo> {

	@Override
	public int compare(AskInfo q1, AskInfo q2) {
		int flag = q2.insertDt.compareTo(q1.insertDt);
		return flag;
	}

}
