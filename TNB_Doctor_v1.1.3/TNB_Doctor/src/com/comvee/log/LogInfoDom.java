package com.comvee.log;

import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.comvee.tool.Log;

public class LogInfoDom {

	public static final String DB_TABLE = "TLog";// 数据库表明
	public static final String DB_ID = "DB_ID";// 数据库主键
	public static final String DB_DATA = "DB_DATA";// 用户ID
	public static final String DB_OPT = "DB_OPT";// 用来判断是否处理过
	public static final String DB_TYPE = "DB_TYPE";

	public static final int OPT_UN = 0;// 未处理
	public static final int OPT_ALREADY = 1;// 已处理

	private SQLiteDatabase db;
	public static LogInfoDom mInstance;

	private LogInfoDom(Context context, SQLiteDatabase db) {
		this.db = db;
	}

	private LogInfoDom(Context context) {
		DatabaseHelper helper = new DatabaseHelper(context);
		db = helper.getWritableDatabase();
	}

	public synchronized static LogInfoDom getInstance() {
		return mInstance == null ? mInstance = new LogInfoDom(ComveeLog.CONTEXT) : mInstance;
	}

	public void execSQL(String sql) {
		synchronized (mInstance) {
			try {
				db.execSQL(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public JSONArray getSycnData(String where) {
		synchronized (mInstance) {
			Cursor cursor = null;
			// StringBuffer sb = new StringBuffer();
			JSONArray array = new JSONArray();
			try {
				String sql = String.format("select * from %s where %s", DB_TABLE,where);
				Log.e(sql);
				cursor = db.rawQuery(sql, null);
				while (cursor.moveToNext()) {
					array.put(new JSONObject(cursor.getString(cursor.getColumnIndex(DB_DATA))));
					// sb.append(cursor.getString(cursor.getColumnIndex(DB_DATA))).append("&");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null)
					cursor.close();
			}
			return array;
		}
	}

	public void updateOpt(int type) {
		synchronized (mInstance) {
			ContentValues values = new ContentValues();
			values.put(DB_OPT, 1);
			db.update(DB_TABLE, values, "DB_OPT=? and DB_TYPE=?", new String[] { "0", String.valueOf(type) });
		}
	}

	public void removeAlreadyOpt(int type) {
		synchronized (mInstance) {
			db.delete(DB_TABLE, "DB_OPT=? and DB_TYPE=?", new String[] { "1", String.valueOf(type) });
		}
	}

	public int getTaskCount(String where) {
		synchronized (mInstance) {
			int nCount = 0;
			Cursor cursor = null;
			try {
				String[] columns = { DB_ID };
				cursor = db.query(DB_TABLE, columns, where, null, null, null, null);
				nCount = cursor.getCount();
				cursor.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null)
					cursor.close();
			}
			return nCount;
		}
	}

	public void insert(String data, int type) {
		synchronized (mInstance) {
			try {
				ContentValues values = new ContentValues();
				values.put(DB_ID, UUID.randomUUID().toString());
				values.put(DB_DATA, data);
				values.put(DB_OPT, OPT_UN);
				values.put(DB_TYPE, type);
				db.insert(DB_TABLE, null, values);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

			}

		}

	}

	public synchronized void close() {
		db.close();
		db = null;
		mInstance = null;
	}

}
