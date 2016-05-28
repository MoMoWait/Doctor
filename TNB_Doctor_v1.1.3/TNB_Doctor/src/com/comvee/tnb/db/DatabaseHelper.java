package com.comvee.tnb.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.tnb.model.RadioGroup.RadioAlbum;
import com.comvee.util.DbUtil;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "comveedoctor.db";
	private static final int DB_VERSION = 17;// 数据库版本号


	public DatabaseHelper(Context context) {
		this(context, DB_NAME, null, DB_VERSION);
	}

	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	/**
	 * 建表的方法
	 *
	 * @param db
	 * @param tableName
	 * @param id
	 * @param columName
	 * @return
	 */
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

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DbUtil.getCreateTableSqlString(RadioAlbum.class));
		db.execSQL(DbUtil.getCreateTableSqlString(RadioAlbumItem.class));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		deleteTable(db);
		onCreate(db);

	}

	public void deleteTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE "+DbUtil.getTableName(RadioAlbum.class));
		db.execSQL("DROP TABLE "+DbUtil.getTableName(RadioAlbumItem.class));
	}

}
