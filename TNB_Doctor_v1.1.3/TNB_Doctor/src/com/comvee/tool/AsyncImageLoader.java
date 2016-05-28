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
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;

import com.comvee.util.Util;

public class AsyncImageLoader
{
	public Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();
	private ExecutorService executorService = Executors.newFixedThreadPool(8);

	private final Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch (msg.what)
			{
			case 1:
				LoaderObj obj = (LoaderObj) msg.obj;
				obj.callBack.imageLoaded(obj.drawable);
				break;

			default:
				break;
			}

		};
	};

	public AsyncImageLoader()
	{
	}

	/**
	 * 
	 * @param imageUrl
	 *            图像url地址
	 * @param callback
	 *            回调接口
	 * @return 返回内存中缓存的图像，第一次加载返回null
	 */

	public Drawable loadDrawable(final String imageUrl, final ImageCallback callback)
	{
		// 如果缓存过就从缓存中取出数据
		if (imageCache.containsKey(imageUrl))
		{
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			if (softReference.get() != null)
			{
				return softReference.get();
			}
		}
		// 缓存中没有图像，则从网络上取出数据，并将取出的数据缓存到内存中
		executorService.submit(new Runnable()
		{
			public void run()
			{
				try
				{
					Drawable d = null;
					d = loadImageFromSD(imageUrl);
					if (d != null)
					{
						imageCache.put(imageUrl, new SoftReference<Drawable>(d));
						mHandler.sendMessage(mHandler.obtainMessage(1, new LoaderObj(callback, d)));
						return;
					}

					d = loadImageFromUrl(imageUrl);
					imageCache.put(imageUrl, new SoftReference<Drawable>(d));
					mHandler.sendMessage(mHandler.obtainMessage(1, new LoaderObj(callback, d)));
				} catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		});
		return null;
	}

	protected Drawable loadImageFromUrl(String imageUrl)
	{
		InputStream fis = null;
		try
		{

			fis = new URL(imageUrl).openStream();
			if (Util.SDCardExists())
			{
				saveImageToSD(fis, Environment.getExternalStorageDirectory() + "/.imgCache"
						+ getFileNameByUrl(imageUrl));
				return loadImageFromSD(imageUrl);
			} else
			{
				return Drawable.createFromStream(fis, "iamgeSync");
			}

		} catch (Exception e)
		{
			throw new RuntimeException(e);
		} finally
		{
			if (null != fis)
			{
				try
				{
					fis.close();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	// 对外界开放的回调接口
	public interface ImageCallback
	{
		// 注意 此方法是用来设置目标对象的图像资源
		public void imageLoaded(Drawable imageDrawable);
	}

	public static Drawable loadImageFromSD(String url)
	{
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			File f = new File(Environment.getExternalStorageDirectory() + "/.imgCache/" + getFileNameByUrl(url));
			if (f.exists())
			{
				try
				{
					FileInputStream fis = new FileInputStream(f);
					Drawable d = Drawable.createFromStream(fis, "src");
					fis.close();
					return d;
				} catch (Exception e)
				{
					return null;
				}
			}
		} else
		{
			return null;
		}
		return null;

	}

	private static String getFileNameByUrl(String url)
	{
		return url.substring(url.lastIndexOf("/"));
	}

	private static void saveImageToSD(InputStream i, String path)
	{
		DataInputStream in = null;
		FileOutputStream out = null;
		try
		{
			File f = new File(path);
			if (!f.getParentFile().exists())
			{
				f.getParentFile().mkdirs();
			}
			in = new DataInputStream(i);
			out = new FileOutputStream(f);
			byte[] buffer = new byte[1024];
			int byteread = 0;
			while ((byteread = in.read(buffer)) != -1)
			{
				out.write(buffer, 0, byteread);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (in != null)
			{
				try
				{
					in.close();
					out.close();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	class LoaderObj
	{
		public LoaderObj(ImageCallback callback2, Drawable drawable)
		{
			this.callBack = callback2;
			this.drawable = drawable;
		}

		ImageCallback callBack;
		Drawable drawable;
	}

}
