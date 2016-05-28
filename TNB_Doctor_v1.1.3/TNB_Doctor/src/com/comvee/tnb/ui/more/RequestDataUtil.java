package com.comvee.tnb.ui.more;

public class RequestDataUtil {
	private static RequestDataUtil instance =new RequestDataUtil();

	private RequestDataUtil() {
	};

	public static synchronized RequestDataUtil getInstance() {
		return instance;
	}
	
}
