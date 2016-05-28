package com.comvee.tnb.model;

import java.util.ArrayList;

public class RecordDetailModels {
	private String code;
	private String meaning;
	private String text;
	private String hint;
	private String b;
	private ArrayList<RecordDetailItem> options;


	public String getB() {
		return b;
	}

	public void setB(String b) {
		this.b = b;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMeaning() {
		return meaning;
	}

	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ArrayList<RecordDetailItem> getOptions() {
		return options;
	}

	public void setOptions(ArrayList<RecordDetailItem> options) {
		this.options = options;
	}

}
