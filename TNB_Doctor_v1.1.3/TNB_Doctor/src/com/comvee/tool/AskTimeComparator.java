package com.comvee.tool;

import java.util.Comparator;

import com.comvee.tnb.model.AskQuestionInfo;

public class AskTimeComparator implements Comparator<AskQuestionInfo> {

	@Override
	public int compare(AskQuestionInfo obj1, AskQuestionInfo obj2) {
		int flag = obj2.getInsertDt().compareTo(obj2.getInsertDt());
		return flag;
	}

}
