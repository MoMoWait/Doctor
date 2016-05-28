package com.comvee.tnb.radio;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.comvee.ThreadHandler;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.db.DownloadIChannelDao;
import com.comvee.tnb.db.DownloadItemDao;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.tnb.model.RadioGroup.RadioAlbum;

public class DownloadMrg {

	public static void deleteAlbum(final RadioAlbum album) {
		DownloadIChannelDao.getInstance().delete(album);
		ThreadHandler.postWorkThread(new Runnable() {
			@Override
			public void run() {
				ArrayList<RadioAlbumItem> list = DownloadItemDao.getInstance().getRaidosByWhere(String.format("refId='%s'", album.radioId));
				String dir = Environment.getExternalStorageDirectory() + "/comvee/radio";
				for (RadioAlbumItem item : list) {
					DownloadItemDao.getInstance().delete(item);
					File file = new File(dir, item.localFileName);
					if (file.exists()) {
						file.delete();
					}
				}
			}
		});

	}

	public static File getDownloadFile(String fileName) {
		String dir = Environment.getExternalStorageDirectory() + "/comvee/radio";
		File file = new File(dir, fileName);
		return file;
	}

	public static void downlaodAlbum(RadioAlbum mAlbum, ArrayList<RadioAlbumItem> list) {

		if (list == null || list.isEmpty()) {
			return;
		}

		Context cxt = TNBApplication.getInstance();
		DownloadIChannelDao downloadIChannelDao = DownloadIChannelDao.getInstance();
		DownloadItemDao downloadItemDao = DownloadItemDao.getInstance();
		if (!downloadIChannelDao.has(mAlbum.radioId)) {
			mAlbum._id = mAlbum.radioId;
			downloadIChannelDao.insert(mAlbum);
		}
		for (RadioAlbumItem radioAlbumItem : list) {
			radioAlbumItem.downloadState = 1;// 等待下载
			radioAlbumItem.refId = mAlbum.radioId;
			radioAlbumItem.beChecked = true;
			radioAlbumItem._id = radioAlbumItem.radioId;
			downloadItemDao.insert(radioAlbumItem);
		}
		Intent intent = new Intent(cxt, DownLoadService.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("tobe_download", list);
		intent.putExtras(bundle);
		cxt.startService(intent);
		Toast.makeText(TNBApplication.getInstance(), "已添加到下载队列", Toast.LENGTH_SHORT).show();
	}

}
