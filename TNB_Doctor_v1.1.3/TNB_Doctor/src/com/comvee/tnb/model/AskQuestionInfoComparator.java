package com.comvee.tnb.model;

import java.util.Comparator;

public class AskQuestionInfoComparator implements Comparator<AskQuestionInfo> {

	@Override
	public int compare(AskQuestionInfo q1, AskQuestionInfo q2) {
		int flag = q1.getInsertDt().compareTo(q2.getInsertDt());
		return flag;
	}

}
