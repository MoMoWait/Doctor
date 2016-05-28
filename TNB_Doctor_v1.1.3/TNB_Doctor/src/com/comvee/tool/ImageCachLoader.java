package com.comvee.tool;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

public class ImageCachLoader
{

	private static HashMap<Object, SoftReference<Bitmap>> map = new HashMap<Object, SoftReference<Bitmap>>();

	public static Bitmap getImg(Context context, String key)
	{
		Bitmap b = null;

		if (map.containsKey(key) && map.get(key).get() != null)
		{
			b = map.get(key).get();
			return b;
		}

		AssetManager asset = context.getAssets();
		try
		{
			b = BitmapFactory.decodeStream(asset.open("img/" + key));
			map.put(key, new SoftReference<Bitmap>(b));
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return b;
	}

	public static Bitmap getThumbnail(Context ctx, int imgId, String path)
	{
		Bitmap b = null;
		if (map.containsKey(imgId) && map.get(imgId).get() != null)
		{
			b = map.get(imgId).get();
			return b;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		b = MediaStore.Video.Thumbnails.getThumbnail(ctx.getContentResolver(), imgId, Images.Thumbnails.MICRO_KIND,
				options);
		if (b != null)
		{
			map.put(imgId, new SoftReference<Bitmap>(b));
		}

		return b;
	}

	public static Bitmap getThumbnail(final Context ctx, final int imgId, final CallBackImage callback)
	{
		Bitmap b = null;
		if (map.containsKey(imgId) && map.get(imgId).get() != null)
		{
			b = map.get(imgId).get();
			return b;
		}
		final Handler mHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				callback.callBackImage((Bitmap) msg.obj, null);
				super.handleMessage(msg);
			}
		};
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Bitmap b = null;
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inDither = false;
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				b = MediaStore.Video.Thumbnails.getThumbnail(ctx.getContentResolver(), imgId,
						Images.Thumbnails.MICRO_KIND, options);
				Message msg = mHandler.obtainMessage(0, b);
				mHandler.sendMessage(msg);
				if (b != null)
				{
					map.put(imgId, new SoftReference<Bitmap>(b));
				}
			}
		}).start();
		return b;
	}

	public static Bitmap getImgByResId(Context ctx, int resId)
	{

		Bitmap b = null;
		if (map.containsKey(resId) && map.get(resId).get() != null)
		{
			b = map.get(resId).get();
			return b;
		}

		b = BitmapFactory.decodeResource(ctx.getResources(), resId);
		if (b != null)
		{
			map.put(resId, new SoftReference<Bitmap>(b));
		}

		return b;
	}

	public static Bitmap getAsynResImge(final Context ctx, final int imgId, final CallBackImage callback)
	{
		Bitmap b = null;
		if (map.containsKey(imgId) && map.get(imgId).get() != null)
		{
			b = map.get(imgId).get();
			return b;
		}
		final Handler mHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				callback.callBackImage((Bitmap) msg.obj, null);
				super.handleMessage(msg);
			}
		};
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Bitmap b = null;
				b = BitmapFactory.decodeResource(ctx.getResources(), imgId);
				Message msg = mHandler.obtainMessage(0, b);
				mHandler.sendMessage(msg);
				if (b != null)
				{
					map.put(imgId, new SoftReference<Bitmap>(b));
				}
			}
		}).start();
		return b;
	}

}
