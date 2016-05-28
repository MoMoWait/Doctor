package com.comvee.tnb.radio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.comvee.tnb.db.DatabaseHelper;
import com.comvee.tnb.model.RadioAlbumItem;

public class DownLoadService extends Service {
	private static final String DIR = "comvee/radio";
	private SQLiteDatabase sqLiteDatabase;
	private Intent downloadFinishIntent;// 下载完成列表的广播
	public static Map<String, DownLoadEntity> downLoadQueue;

	private final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	private final int CORE_POOL_SIZE = CPU_COUNT + 1;
	private final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
	private final int KEEP_ALIVE = 1;
	private final BlockingQueue<Runnable> mPoolWorkQueue = new LinkedBlockingQueue<Runnable>(128);
	private final ThreadFactory mThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			return new Thread(r, "downloadtask #" + mCount.getAndIncrement());
		}
	};
	private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
			mPoolWorkQueue, mThreadFactory);

	@Override
	public void onCreate() {
		downLoadQueue = new HashMap<String, DownLoadService.DownLoadEntity>();
		sqLiteDatabase = new DatabaseHelper(getApplicationContext()).getWritableDatabase();
		downloadFinishIntent = new Intent(DownLoadLocalFragment.DOWNLOAD_FINISH);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (downLoadQueue == null) {
			downLoadQueue = new HashMap<String, DownLoadService.DownLoadEntity>();
		}
		try {
			Object obj = intent.getSerializableExtra("tobe_download");
			if (obj != null && obj instanceof RadioAlbumItem) {
				submitTask((RadioAlbumItem) obj);
			} else if (obj != null && obj instanceof ArrayList<?>) {
				@SuppressWarnings("unchecked")
				ArrayList<RadioAlbumItem> radioAlbumItems = (ArrayList<RadioAlbumItem>) obj;
				for (RadioAlbumItem radioAlbumItem : radioAlbumItems) {
					submitTask(radioAlbumItem);
				}
			} else {
				return Service.START_REDELIVER_INTENT;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Service.START_REDELIVER_INTENT;
	}

	private void submitTask(RadioAlbumItem radioAlbumItem) {
		if (TextUtils.isEmpty(radioAlbumItem.localFileName)) {
			radioAlbumItem.localFileName = UUID.randomUUID() + ".mp3";
		}
		HttpFileDownLoad httpFileDownLoad = new HttpFileDownLoad(radioAlbumItem, DIR, sqLiteDatabase);
		FutureTask<Integer> futureTask = new DLFutureTask(httpFileDownLoad);
		downLoadQueue.put(radioAlbumItem.radioId, new DownLoadEntity(futureTask, httpFileDownLoad));
		threadPool.submit(futureTask);
	}

	private class DLFutureTask extends FutureTask<Integer> {
		public DLFutureTask(Callable<Integer> callable) {
			super(callable);
		}

		@Override
		protected void done() {
			try {
				switch (get()) {// get函数会阻塞主线程，故放在done中判断是否下载成功
				case 5:
					sendBroadcast(downloadFinishIntent);
					break;
				}
			} catch (Exception e) {
			}
			super.done();
		}
	}

	private class DownLoadEntity {
		private FutureTask<Integer> futureTask;
		private HttpFileDownLoad httpFileDownLoad;

		public DownLoadEntity(FutureTask<Integer> futureTask, HttpFileDownLoad httpFileDownLoad) {
			this.futureTask = futureTask;
			this.httpFileDownLoad = httpFileDownLoad;
		}
	}

	public static void shutdownByID(String radioId) {
		DownLoadEntity downLoadEntity = downLoadQueue.get(radioId);
		if (downLoadEntity != null) {
			downLoadEntity.futureTask.cancel(true);
			downLoadEntity.httpFileDownLoad.shutdownDL(true);
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onTrimMemory(int level) {
		sqLiteDatabase.execSQL("update RadioAlbumItem set downloadState=3 where downloadState<>5");
		super.onTrimMemory(level);
	}

	@Override
	public void onDestroy() {
		Log.e("ffsfds", "Fq32434");
		sqLiteDatabase.execSQL("update RadioAlbumItem set downloadState=3 where downloadState<>5");
		super.onDestroy();
	}
}
