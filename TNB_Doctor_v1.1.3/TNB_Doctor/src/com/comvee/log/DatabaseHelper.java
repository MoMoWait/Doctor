package com.comvee.log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "log.db";

	public DatabaseHelper(Context context) {
		this(context, DB_NAME, null, 2);
	}

	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable(db, LogInfoDom.DB_TABLE, LogInfoDom.DB_ID, LogInfoDom.DB_DATA, LogInfoDom.DB_OPT,LogInfoDom.DB_TYPE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + LogInfoDom.DB_TABLE);
		onCreate(db);
	}

	public void delete(SQLiteDatabase db) {
		db.execSQL("drop table if exists " + LogInfoDom.DB_TABLE);
		db.close();
	}

	public static int createTable(SQLiteDatabase db, String tableName, String id, String... columName) {

		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE IF NOT EXISTS ");
		sb.append(tableName);
		sb.append(" (");
		sb.append(id + " char[50] primary key,");
		for (String str : columName) {
			sb.append(str + " char[50],");
		}
		sb.replace(sb.length() - 1, sb.length(), "");
		sb.append(")");
		db.execSQL(sb.toString());

		return 0;
	};

}
