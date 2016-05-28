package com.comvee.log;

import android.os.Looper;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
	public static final String TAG = CrashHandler.class.getSimpleName();
	private static CrashHandler INSTANCE = new CrashHandler();
	private long mThreadId;

	private CrashHandler() {
		mThreadId = Thread.currentThread().getId();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public void uncaughtException(Thread thread, final Throwable ex) {

		ex.printStackTrace();
		// Log.e("nightModel", "uncaughtException===未知错误");

//		if (thread.getId() == mThreadId) {

			new Thread() {
				@Override
				public void run() {
					Looper.prepare();
					ex.printStackTrace();

					StringBuffer sb = new StringBuffer();
					sb.append(ex.toString()).append("\n");
					for (StackTraceElement e : ex.getStackTrace()) {
						sb.append(e.toString()).append("\n");
					}
					// ComveeLog.logError(ex.toString());
					ComveeLog.logError(sb.toString());
					android.os.Process.killProcess(android.os.Process.myPid());
					System.exit(0);
					Looper.loop();
				}

			}.start();
		}
//	}
}