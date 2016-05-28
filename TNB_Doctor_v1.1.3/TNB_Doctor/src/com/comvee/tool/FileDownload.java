package com.comvee.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Message;

import com.comvee.FinalDb;
import com.comvee.tnb.model.DownloadInfo;
import com.comvee.util.SerializUtil;

public class FileDownload {

	private static FileDownload instance;
	private ArrayList<DownloadInfo> mInfos;
	private FinalDb db;
	private SharedPreferences sp;

	private FileDownload(Context cxt) {
		db = FinalDb.create(cxt);
		sp = cxt.getSharedPreferences("download", 0);
		try {
			mInfos = (ArrayList<DownloadInfo>) SerializUtil.fromString(sp.getString("download", ""));
		} catch (Exception e) {
			mInfos = new ArrayList<DownloadInfo>();
		}
	}

	public void commit() {
		try {
			sp.edit().putString("download", SerializUtil.toString(mInfos)).commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getDownloadPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + "/.tingshu";
	}

	public static FileDownload getInstance(Context cxt) {
		return instance == null ? (instance = new FileDownload(cxt)) : instance;
	}

	public List<DownloadInfo> getDownloadInfos() {
		return mInfos;
	}

	public void download(String key, String urlString, String path, OnDowndloadListener listener) {
		DownloadInfo info = new DownloadInfo(urlString, 0, 0, key, path);
		DownloadThread thread = new DownloadThread(info, listener);
		thread.start();
		mInfos.add(info);
	}

	class DownloadThread extends Thread {
		private OnDowndloadListener listener;
		private DownloadInfo mInfo;

		public DownloadThread(DownloadInfo mInfo, OnDowndloadListener listener) {
			super();
			this.listener = listener;
			this.mInfo = mInfo;
		}

		@Override
		public void run() {

			InputStream is = null;
			FileOutputStream fos = null;
			try {
				URL url = new URL(mInfo.url);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();

				mInfo.fileSize = conn.getContentLength();

				File file = new File(mInfo.path);
				file.mkdirs();
				File f = new File(mInfo.path, mInfo.fileName);
				is = conn.getInputStream();
				fos = new FileOutputStream(f, false);

				byte[] buffer = new byte[1024 * 1024];
				int total = 0;
				int size = 0;
				while ((size = is.read(buffer)) != -1) {
					total += size;
					fos.write(buffer);
					if (null != listener) {
						mInfo.setComplete(total);
						Message msg = listener.obtainMessage();
						msg.what = 0;
						msg.obj = mInfo;
						listener.sendMessage(msg);
					}
				}
				fos.flush();
				// db.delete(mInfo);
				if (null != listener) {
					Message msg = listener.obtainMessage();
					msg.what = 2;
					msg.obj = mInfo;
					listener.sendMessage(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (null != listener) {
					Message msg = listener.obtainMessage();
					msg.what = 1;
					msg.arg1 = 1001;
					msg.obj = mInfo;
					listener.sendMessage(msg);
				}
			} finally {
				if (null != is) {
					try {
						is.close();
						is = null;
					} catch (IOException e) {
					}
				}
				if (null != fos) {
					try {
						fos.close();
						fos = null;
					} catch (IOException e) {
					}
				}
			}

			super.run();
		}
	}

}
