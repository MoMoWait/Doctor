package com.comvee.tnb.db;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.comvee.util.DbUtil;

/**
 * Created by friendlove-pc on 16/1/26.
 */
public abstract class BaseDao<T> extends DatabaseHelper {

	public static final String DB_ID = "_id";
	public SQLiteDatabase db;

	public BaseDao(Context context) {
		super(context);
		db = getWritableDatabase();
	}

	public void insert(T t) {
		synchronized (DB_ID) {
			ContentValues values = DbUtil.getContentValue(t);
			db.insert(DbUtil.getTableName(t.getClass()), null, values);
		}

	}

	public void delete(String id) {
		synchronized (DB_ID) {
			Class<T> t = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			db.delete(DbUtil.getTableName(t), String.format("%s='%s'", DB_ID, id), null);
		}
	}

	public void deleteAll() {
		synchronized (DB_ID) {
			Class<T> t = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			db.delete(DbUtil.getTableName(t), null, null);
		}
	}

	public void update(String id, T t) {
		synchronized (DB_ID) {
			ContentValues values = DbUtil.getContentValue(t);
			String table = DbUtil.getTableName(t.getClass());
			db.update(table, values, String.format("%s='%s'", DB_ID, id), null);
		}
	}

	public boolean has(String id) {
		synchronized (DB_ID) {
			return getCount(String.format("%s='%s'", DB_ID, id)) > 0;
		}
	}

	public int getCount(String where) {
		synchronized (DB_ID) {
			Class<T> t = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			String table = DbUtil.getTableName(t);
			Cursor cursor = db.query(table, null, where, null, null, null, null);
			int i = cursor.getCount();
			return i;
		}
	}

	public ArrayList<?> getItems(String where) {
		synchronized (DB_ID) {
			Class<T> t = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			ArrayList<?> list = DbUtil.getArrayByWhere(t, db, where);
			return list;
		}
	}

}
