package com.comvee.tool;

public class Log {

	private static boolean DEBUG = true;

	public static void e(String msg) {
		if (DEBUG)
			android.util.Log.e("tnb", msg);
	}

	public static void d(String msg) {
		if (DEBUG)
			android.util.Log.d("tnb", msg);
	}

	public static void i(String msg) {
		if (DEBUG)
			android.util.Log.i("tnb", msg);
	}

	public static void w(String msg) {
		if (DEBUG)
			android.util.Log.w("tnb", msg);

	}

}
