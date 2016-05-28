package com.comvee.tnb;

import android.content.Context;
import android.os.Looper;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
	public static final String TAG = CrashHandler.class.getSimpleName();
	private static CrashHandler INSTANCE = new CrashHandler();

	private CrashHandler() {
	}

	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	public void init(Context ctx) {
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

		ex.printStackTrace();
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
				Looper.loop();

			}

		}.start();
	}
}