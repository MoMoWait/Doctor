package com.comvee.tnb.model;

import android.view.View;

public class ExceptRecords {
	public String time;
	public View view;

	public ExceptRecords( String time) {
		super();
		this.time = time;
		
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		ExceptRecords other = (ExceptRecords) obj;
		if (other.time.equals(this.time))
			return true;
		return false;
	}
}
