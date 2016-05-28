package com.comvee.tnb.network;

import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.ui.record.laboratory.Laboratory;

/**
 * 化验报告列表的加载类
 * 
 * @author friendlove-pc
 *
 */
public class LaboratoryLoader extends BasePageLoader {

	public LaboratoryLoader() {
		super(Laboratory.class);
	}

	public void load() {
		resetRequestParams();
		setUrl(ConfigUrlMrg.LABORATOR_LIST);
		putPostValue("sort", "insert_dt");
		putPostValue("order", "desc");
		loadMore();
	}

	public void reLoad() {
		resetRequestParams();
		setUrl(ConfigUrlMrg.LABORATOR_LIST);
		putPostValue("sort", "insert_dt");
		putPostValue("order", "desc");
		loadRefresh();
	}
}
