package com.comvee.tnb.model;

import com.comvee.annotation.sqlite.Id;
import com.comvee.annotation.sqlite.Table;

@Table(name = "alarmpendingcode")
public class AlarmPendingCodeInfo {

	@Id(column = "id")
	private int id;
	private int code;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
