package com.comvee.tnb.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.comvee.BaseApplication;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.tnb.model.RadioGroup.RadioAlbum;
import com.comvee.util.DbUtil;

public class DownloadIChannelDao {

	private SQLiteDatabase db;
	private static DownloadIChannelDao mInstance;
	public static final String DB_TABLE = DbUtil.getTableName(RadioAlbum.class);
	public static final String DB_ID = "_id";

	public DownloadIChannelDao(Context context) {
		DatabaseHelper helper = new DatabaseHelper(context);
		db = helper.getWritableDatabase();
	}

	public static DownloadIChannelDao getInstance() {
		return mInstance == null ? mInstance = new DownloadIChannelDao(BaseApplication.getInstance()) : mInstance;
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

	public Cursor getCursor() {
		Cursor cursor = db
				.rawQuery(
						"SELECT DISTINCT RadioAlbum.*   FROM RadioAlbum,RadioAlbumItem where  RadioAlbum.radioId=RadioAlbumItem.refId and RadioAlbumItem.downloadState=5 ",
						new String[] {});
		return cursor;
	}

	public void insert(RadioAlbum item) {
		ContentValues value = DbUtil.getContentValue(item);
		value.put(DB_ID, item.radioId);
		db.insert(DB_TABLE, null, value);
	}

	public void update(RadioAlbum item) {
		ContentValues value = DbUtil.getContentValue(item);
		db.update(DB_TABLE, value, DB_ID + "=?", new String[] { item._id });
	}

	public void delete(RadioAlbum item) {
		db.delete(DB_TABLE, DB_ID + "=?", new String[] { item.radioId });
	}

	public static RadioAlbum getInfoByCursor(Cursor cursor) {
		return DbUtil.getObjecByCursor(RadioAlbum.class, cursor);
	}

	public void clear() {
		db.delete(DB_TABLE, null, null);
	}

	public void close() {
		db.close();
		mInstance = null;
	}
}
