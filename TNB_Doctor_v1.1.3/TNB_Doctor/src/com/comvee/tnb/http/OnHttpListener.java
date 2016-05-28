package com.comvee.tnb.http;

public interface OnHttpListener {
	public void onSussece(int what, byte[] b, boolean fromCache);
	public void onFialed(int what, int errorCode);
}
