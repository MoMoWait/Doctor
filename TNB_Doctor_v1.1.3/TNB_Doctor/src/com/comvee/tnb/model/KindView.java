package com.comvee.tnb.model;

import android.widget.LinearLayout;

public class KindView {
	public String kindKey;
	public String kindName;
	public LinearLayout kindLayout;

	public KindView(String kindKey, String kindName, LinearLayout kindLayout) {
		this.kindKey = kindKey;
		this.kindName = kindName;
		this.kindLayout = kindLayout;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		KindView other = (KindView) obj;
		if (other.kindKey.equals(this.kindKey) && other.kindName.equals(this.kindName))
			return true;
		return false;
	}
}
