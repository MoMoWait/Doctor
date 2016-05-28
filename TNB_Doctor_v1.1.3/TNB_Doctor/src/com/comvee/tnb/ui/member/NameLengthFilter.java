package com.comvee.tnb.ui.member;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.InputFilter;
import android.text.Spanned;

public class NameLengthFilter implements InputFilter {
	int MAX_EN;// 最大英文/数字长度 一个汉字算两个字母
	String regEx = "[\\u4e00-\\u9fa5]"; // unicode编码，判断是否为汉字

	public NameLengthFilter(int mAX_EN) {
		super();
		MAX_EN = mAX_EN;
	}

	@Override
	public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
		try {
			int destCount = dest.toString().getBytes("utf-8").length;
			int sourCount = source.toString().getBytes("utf-8").length;
			if (destCount + sourCount > MAX_EN) {
				return "";

			} else {
				return source;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	private int getChineseCount(String str) {
		int count = 0;
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		while (m.find()) {
			for (int i = 0; i <= m.groupCount(); i++) {
				count = count + 1;
			}
		}
		return count;
	}
}
