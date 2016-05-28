package com.comvee.tnb.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.comvee.tnb.config.ConfigParams;
import com.comvee.util.Util;

public class DBManager {
	private final int BUFFER_SIZE = 400000;
	public static String DB_NAME; // 保存的数据库文件名
	public static String DB_PATH; // 在手机里存放数据库的位置

	private SQLiteDatabase database;
	private Context context;

	DBManager(Context context, String dbName) {
		DB_NAME = dbName;
		this.context = context;
		final String PACKAGE_NAME = context.getPackageName();
		DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME;
	}

	public static void deleteDb() {
		try {
			File file = new File(DB_PATH);
			for (File f : file.listFiles()) {
				// Log.e("删除数据库", f.getAbsolutePath());
				f.deleteOnExit();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void checkDbVesion(final Context cxt) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final String PACKAGE_NAME = cxt.getPackageName();
				DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME;
				int code = Util.getAppVersionCode(cxt, cxt.getPackageName());
				if (ConfigParams.getDbVesionCode(cxt) < code) {
					ConfigParams.setDbVesionCode(cxt, code);
					// deleteDb();
					DBManager.cleanDatabases(cxt);
					// Log.e("删除数据库", "删除数据库");
				}
			}
		}).start();
	}

	public SQLiteDatabase openDatabase() {
		return this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
	}

	private SQLiteDatabase openDatabase(String dbfile) {
		try {
			if (!(new File(dbfile).exists())) {// 判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
				InputStream is = this.context.getAssets().open(DB_NAME); // 欲导入的数据库
				FileOutputStream fos = new FileOutputStream(dbfile);
				byte[] buffer = new byte[BUFFER_SIZE];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
			return db;
		} catch (FileNotFoundException e) {
			// Log.e("Database", "File not found");
			e.printStackTrace();
		} catch (IOException e) {
			// Log.e("Database", "IO exception");
			e.printStackTrace();
		} catch (Exception e) {
		}
		return null;
	}

	public void closeDatabase() {
		this.database.close();
	}

	public static void cleanDatabases(Context context) {
		deleteFilesByDirectory(new File("/data/data/" + context.getPackageName() + "/databases"));
	}

	/**
	 * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
	 * context
	 */
	public static void cleanSharedPreference(Context context) {
		deleteFilesByDirectory(new File("/data/data/" + context.getPackageName() + "/shared_prefs"));
	}

	/** * 按名字清除本应用数据库 * * @param context * @param dbName */
	public static void cleanDatabaseByName(Context context, String dbName) {
		context.deleteDatabase(dbName);
	}

	/** * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context */
	public static void cleanFiles(Context context) {
		deleteFilesByDirectory(context.getFilesDir());
	}

	/**
	 * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
	 * context
	 */
	public static void cleanExternalCache(Context context) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			deleteFilesByDirectory(context.getExternalCacheDir());
		}
	}

	private static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				item.deleteOnExit();
			}
		}
	}
}
