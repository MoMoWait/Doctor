package com.comvee.tnb.radio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;

import com.comvee.tnb.db.DownloadItemDao;
import com.comvee.tnb.model.RadioAlbumItem;

public class DownLoadModel {
	public DownLoadModel() {

	}

	/**
	 * 删除专辑记录和对应的所有节目记录，并且删除本地音频文件
	 * 
	 * @param chanelId
	 *            专辑ID
	 * @param db
	 */
	public static void deleteChanelNItemById(String chanelId, SQLiteDatabase db) {
		String dir = Environment.getExternalStorageDirectory() + "/comvee/radio";
		// db.delete("RadioAlbum", "_id=?", new String[] { chanelId });
		Cursor cursor = db.query("RadioAlbumItem", new String[] { "localFileName" }, "refId=?", new String[] { chanelId }, null, null, null);
		while (cursor.moveToNext()) {
			String localFileName = cursor.getString(cursor.getColumnIndex("localFileName"));
			File file = new File(dir, localFileName);
			file.delete();
		}
		db.delete("RadioAlbumItem", "refId=?", new String[] { chanelId });
	}

	public static void batchOp(ArrayList<RadioAlbumItem> list, boolean isAllstart, Context ctx) {
		for (RadioAlbumItem radioAlbumItem : list) {
			if (isAllstart && radioAlbumItem.downloadState == 2) {
				radioAlbumItem.downloadState = 3;
				DownLoadService.shutdownByID(radioAlbumItem.radioId);
			}
			if (!isAllstart && (radioAlbumItem.downloadState == 3 || radioAlbumItem.downloadState == 4)) {
				radioAlbumItem.downloadState = 2;
			}
		}
		if (!isAllstart) {
			Intent intent = new Intent(ctx, DownLoadService.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("tobe_download", list);
			intent.putExtras(bundle);
			ctx.startService(intent);
		}
	}

	public static void deleteAllDownLoadingFile(List<RadioAlbumItem> list, DownloadItemDao downloadItemDao, String dir) {
		for (RadioAlbumItem radioAlbumItem : list) {
			DownLoadService.shutdownByID(radioAlbumItem.radioId);
			File file = new File(dir, radioAlbumItem.localFileName);
			if (file.exists()) {
				file.delete();
				downloadItemDao.delete(radioAlbumItem);
			}
		}
	}

	/**
	 * 
	 * @param ctx
	 * @return true is wifi
	 */
	public static boolean isWifi(Context ctx) {
		ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	public static void addToDownLoad(RadioAlbumItem radioAlbumItem, Context ctx, SQLiteDatabase sqLiteDatabase) {
		radioAlbumItem.downloadState = 2;
		sqLiteDatabase.execSQL("update RadioAlbumItem set downloadState=2  where radioId='" + radioAlbumItem.radioId + "'");
		Intent intent = new Intent(ctx, DownLoadService.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("tobe_download", radioAlbumItem);
		intent.putExtras(bundle);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startService(intent);
	}

}
