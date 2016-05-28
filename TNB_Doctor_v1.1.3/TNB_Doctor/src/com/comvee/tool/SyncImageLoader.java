package com.comvee.tool;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;

import com.comvee.ThreadHandler;

public class SyncImageLoader {

	private final Object lock = new Object();

	private boolean mAllowLoad = true;

	private boolean firstLoad = true;

	private int mStartLoadLimit = 0;

	private int mStopLoadLimit = 0;

	private final Handler handler = new Handler();

	public final static int limitThread = 15;

	private final static HashMap<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();
	private static ExecutorService executorService = Executors.newFixedThreadPool(limitThread);

	public interface OnImageLoadListener {
		public void onImageLoad(Integer tag, Drawable drawable);

		public void onError(Integer t);
	}

	public final void setLoadLimit(int startLoadLimit, int stopLoadLimit) {
		if (startLoadLimit > stopLoadLimit) {
			return;
		}
		mStartLoadLimit = startLoadLimit;
		mStopLoadLimit = stopLoadLimit;
	}

	public final void restore() {
		mAllowLoad = true;
		firstLoad = true;
	}

	public final void lock() {
		mAllowLoad = false;
		firstLoad = false;
	}

	public final void unlock() {
		mAllowLoad = true;
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	public final Drawable loadImage(Integer tag, String imageUrl, OnImageLoadListener listener) {
		final OnImageLoadListener mListener = listener;
		final String mImageUrl = imageUrl;
		final Integer mt = tag;
		if (imageCache.containsKey(mImageUrl)) {
			final Drawable d = imageCache.get(mImageUrl).get();
			if (d != null) {
				return d;
			}
		}

		Drawable d = loadImageFromSD(mImageUrl);
		if (null != d) {
			final SoftReference<Drawable> dd = new SoftReference<Drawable>(d);
			if (d != null) {// 进缓存
				imageCache.put(mImageUrl, dd);
			}
			return dd.get();
		}

		executorService.submit(new Runnable() {

			@Override
			public void run() {
				if (!mAllowLoad) {
					synchronized (lock) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				if (mAllowLoad) {
					if (mt <= mStopLoadLimit && mt >= mStartLoadLimit || firstLoad) {
						downloadImage(mImageUrl, mt, mListener);
					}
				}
			}
		});
		return null;
	}

	private void downloadImage(final String mImageUrl, final Integer tag, final OnImageLoadListener mListener) {

		try {
			final SoftReference<Drawable> d = new SoftReference<Drawable>(loadImageFromUrl(mImageUrl));
			if (d != null) {// 进缓存
				imageCache.put(mImageUrl, d);
			}
			if (mListener != null) {
				ThreadHandler.postUiThread(new Runnable() {
					@Override
					public void run() {
						if (mAllowLoad) {
							mListener.onImageLoad(tag, d.get());
						}
					}
				});
			}

		} catch (Exception e) {
			e.printStackTrace();
			if (mListener != null) {
				ThreadHandler.postUiThread(new Runnable() {
					@Override
					public void run() {
						mListener.onError(tag);
					}
				});
			}
		}
	}

	public static Drawable loadImageFromSD(String url) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File f = new File(Environment.getExternalStorageDirectory() + "/.imgCache/" + getFileNameByUrl(url));
			if (f.exists()) {
				try {
					FileInputStream fis = new FileInputStream(f);
					Drawable d = Drawable.createFromStream(fis, "src");
					fis.close();
					return d;
				} catch (Exception e) {
					return null;
				}
			}
		} else {
			return null;
		}
		return null;

	}

	private static String getFileNameByUrl(String url) {
		return url.substring(url.lastIndexOf("/"));
	}

	public static Drawable loadImageFromUrl(String url) throws IOException {
		URL m = new URL(url);
		InputStream i = (InputStream) m.getContent();
		Drawable d = Drawable.createFromStream(i, "src");
		if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
			saveImageToSD(i, Environment.getExternalStorageDirectory() + "/.imgCache" + getFileNameByUrl(url));
		}

		i.close();
		return d;
	}

	private static void saveImageToSD(InputStream i, String path) {
		DataInputStream in = null;
		FileOutputStream out = null;
		try {
			File f = new File(path);
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			in = new DataInputStream(i);
			out = new FileOutputStream(f);
			byte[] buffer = new byte[1024];
			int byteread = 0;
			while ((byteread = in.read(buffer)) != -1) {
				out.write(buffer, 0, byteread);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
