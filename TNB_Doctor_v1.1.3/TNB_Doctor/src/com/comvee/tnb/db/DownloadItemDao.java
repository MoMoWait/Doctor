package com.comvee.tnb.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.comvee.BaseApplication;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.util.DbUtil;

public class DownloadItemDao {
	private SQLiteDatabase db;
	private static DownloadItemDao mInstance;
	public static final String DB_TABLE = DbUtil.getTableName(RadioAlbumItem.class);
	public static final String DB_ID = "_id";

	public DownloadItemDao(Context context) {
		DatabaseHelper helper = new DatabaseHelper(context);
		db = helper.getWritableDatabase();
	}

	public static DownloadItemDao getInstance() {
		return mInstance == null ? mInstance = new DownloadItemDao(BaseApplication.getInstance()) : mInstance;
	}

	public SQLiteDatabase getSQLiteDatabase() {
		return db;
	}

	public boolean has(String id) {
		int nCount = 0;
		Cursor cursor = db.query(DB_TABLE, new String[] { DB_ID }, DB_ID + "=" + id, null, null, null, null);
		nCount = cursor.getCount();
		cursor.close();
		return nCount > 0;
	}

	public String getAlreadyDownload(String id) {
		if (TextUtils.isEmpty(id)) {
			return null;
		}
		Cursor cursor = null;
		try {
			cursor = db.query(DB_TABLE, new String[] { "localFileName" }, DB_ID + "=" + id + " and downloadState=5", null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				return cursor.getString(cursor.getColumnIndex("localFileName"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		return null;
	}

	public void insert(RadioAlbumItem item) {
		ContentValues value = DbUtil.getContentValue(item);
		value.put(DB_ID, item.radioId);
		db.insert(DB_TABLE, null, value);
	}

	public void update(RadioAlbumItem item) {
		ContentValues value = DbUtil.getContentValue(item);
		db.update(DB_TABLE, value, DB_ID + "=?", new String[] { item._id });
	}

	public void delete(RadioAlbumItem item) {
		db.delete(DB_TABLE, DB_ID + "=?", new String[] { item._id });
	}

	public static RadioAlbumItem getInfoByCursor(Cursor cursor) {
		return DbUtil.getObjecByCursor(RadioAlbumItem.class, cursor);
	}

	public RadioAlbumItem getRadioById(String id) {
		// db.execSQL("select * from %s where %s='%s'",DB_TABLE,DB_ID,id);
		/*Cursor cursor = db.query(DB_TABLE, new String[] { "*" }, DB_ID + "=" + id, null, null, null, null);
		return DbUtil.getObjecByCursor(RadioAlbumItem.class, cursor);
*/		return DbUtil.getObjectByWhere(RadioAlbumItem.class,db,DB_ID + "=" + id);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<RadioAlbumItem> getRaidosByWhere(String where) {
		return (ArrayList<RadioAlbumItem>) DbUtil.getArrayByWhere(RadioAlbumItem.class, db, where);
	}

	public void clear() {
		db.delete(DB_TABLE, null, null);
	}

	public void close() {
		db.close();
		mInstance = null;
	}

	public ArrayList<RadioAlbumItem> getDownloadUnComplectes() {
		return getRaidosByWhere("downloadState not in ('5')");
	}

	public int getDownloadComplecteCount(String id) {
		Cursor itemCursor = DownloadItemDao.getInstance().getSQLiteDatabase()
				.query("RadioAlbumItem", new String[] { "_id" }, "refId" + "=" + id + " and downloadState=5", null, null, null, null);
		int count = itemCursor.getCount();
		itemCursor.close();
		return count;
	}
}
