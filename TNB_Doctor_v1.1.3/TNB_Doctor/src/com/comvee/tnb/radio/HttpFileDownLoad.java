package com.comvee.tnb.radio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.comvee.tnb.model.RadioAlbumItem;

public class HttpFileDownLoad implements Callable<Integer> {

	private String id;
	private String urlstr;
	private String sdcard = Environment.getExternalStorageDirectory() + "/";
	private String dir;
	private String filename;
	private HttpURLConnection urlcon;
	private SQLiteDatabase sqLiteDatabase;
	private boolean isShutDown = false;

	private int per;// 即时下载百分比（0到100）
	private long total = 0;// 累积下载大小(B)
	private float remoteFileSizeB; // 源文件大小(B)
	private float downSpeed;// 下载速度(KB/S,保留小数点后一位)

	private TimerTask timerTaskCalc;
	private Timer timer = new Timer();
	private final int calcuDelay = 1000;// 下载中速度和大小的更新间隔

	/**
	 * 
	 * @param folderId
	 *            记录ID
	 * @param urlAddress
	 *            远程文件URL
	 * @param dir
	 *            下载到本地的目录
	 * @param filename
	 *            下载到本地的文件名
	 */
	public HttpFileDownLoad(RadioAlbumItem radioAlbumItem, String dir, SQLiteDatabase sqLiteDatabase) {
		this.per = radioAlbumItem.downloadedPer;
		this.total = per * radioAlbumItem.fileSize / 100;
		this.id = radioAlbumItem.radioId;
		this.urlstr = radioAlbumItem.playUrl;
		this.filename = radioAlbumItem.localFileName;
		this.dir = dir;
		this.sqLiteDatabase = sqLiteDatabase;
		urlcon = getConnection();
		timerTaskCalc = new TimerTask() {
			long lastSize = 0;

			@Override
			public void run() {
				downSpeed = (total - lastSize) * 1000 * 1.f / (calcuDelay * 1024);
				downSpeed = (float) (Math.round(downSpeed * 10) * 1.f / 10);
				feedbackData(2);
				lastSize = total;
			}
		};
	}

	/*
	 * 获取http连接处理类HttpURLConnection
	 */
	private HttpURLConnection getConnection() {
		URL url;
		HttpURLConnection urlcon = null;
		try {
			url = new URL(urlstr);
			urlcon = (HttpURLConnection) url.openConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return urlcon;
	}

	public void shutdownDL(boolean b) {
		isShutDown = b;
	}

	/**
	 * 下载及更新本地库
	 */
	@Override
	public Integer call() throws Exception {
		timer.schedule(timerTaskCalc, 0, calcuDelay);
		BufferedInputStream buffInputStream = null;
		RandomAccessFile fos = null;
		StringBuilder sb = new StringBuilder(sdcard).append(dir);
		File file = new File(sb.toString());
		if (!file.exists()) {
			file.mkdirs();
		}
		// 获取文件全名
		sb.append("/" + filename);
		long startPosition;// 下载起始位置
		file = new File(sb.toString());
		if (file.exists()) {
			startPosition = file.length();
		} else {
			startPosition = 0;
		}
		try {
			remoteFileSizeB = urlcon.getContentLength();
			urlcon.disconnect();
			getConnection();
			URLConnection urlConnection = new URL(urlstr).openConnection();
			urlConnection.setAllowUserInteraction(true);
			urlConnection.setRequestProperty("Range", "bytes=" + startPosition + "-" + remoteFileSizeB);
			fos = new RandomAccessFile(file, "rw");
			fos.seek(startPosition);
			buffInputStream = new BufferedInputStream(urlConnection.getInputStream());
			// 创建文件
			file.createNewFile();
			int readedLen = 0;// 实时下载大小
			byte[] buf = new byte[1024];
			while ((readedLen = buffInputStream.read(buf)) != -1) {
				fos.write(buf, 0, readedLen);
				total += readedLen;
				per = (int) (total * 100 / remoteFileSizeB);
				if (isShutDown) {
					timer.cancel();
					return feedbackData(3);
				}
			}
			buffInputStream.close();
		} catch (Exception e) {
			timer.cancel();
			Log.e("ftttd",e.getMessage(),e);
			return feedbackData(4);
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				timer.cancel();
				return feedbackData(4);
			}
		}
		per = 100;
		timer.cancel();
		return feedbackData(5);
	}

	/**
	 * 
	 * @param downstate
	 *            * 1，等待下载； 2，下载中； 3，暂停下载； 4，下载失败； 5，下载成功。
	 * @return
	 */
	public int feedbackData(int downstate) {
		if (isShutDown) {// 由于暂停时timer中的任务还维持着downState状态位是2
			downstate = 3;
		}
		String sql = String.format("update RadioAlbumItem set downloadedPer='%d',fileSize='%f',speed='%f',localFileName='%s',downloadState='%d' where radioId='%s'"
				, per,remoteFileSizeB,downSpeed,filename,downstate,id);
//		
//		String sqlStr = "update RadioAlbumItem set downloadedPer=" + per + ",fileSize=" + remoteFileSizeB + ",speed=" + downSpeed
//				+ ",localFileName='" + filename + "',downloadState=" + downstate + "  where radioId='" + id + "'";
		sqLiteDatabase.execSQL(sql);
		return downstate;
	}

}
