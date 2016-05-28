package com.comvee.tnb.http;

public interface OnAsynHttpListener {
	public void onSussece(int what, byte[] b, boolean fromCache);
	public void onFialed(int what, int errorCode);
}
