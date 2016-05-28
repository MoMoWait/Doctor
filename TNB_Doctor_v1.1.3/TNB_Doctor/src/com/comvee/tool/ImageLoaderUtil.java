package com.comvee.tool;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.comvee.tnb.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class ImageLoaderUtil {
	public static final DisplayImageOptions default_options_knowledge_index = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.img_defualt1) 
			.showImageForEmptyUri(R.drawable.jkzs_30).showImageOnFail(R.drawable.jkzs_30).cacheInMemory(true) 
			.cacheOnDisc(true) 
			.build();
	public static final DisplayImageOptions default_options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.img_defualt1) 
			.showImageForEmptyUri(R.drawable.img_defualt1).showImageOnFail(R.drawable.img_defualt1).cacheInMemory(true) 
			.cacheOnDisc(true) 
			.build();
	public static final DisplayImageOptions pay_options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.sirenyishengaz_20) 
			.showImageForEmptyUri(R.drawable.sirenyishengaz_20).showImageOnFail(R.drawable.sirenyishengaz_20).cacheInMemory(true) 
			.cacheOnDisc(true) 
			.build();
	public static final DisplayImageOptions user_options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.icon_head) 
			.showImageForEmptyUri(R.drawable.icon_head).showImageOnFail(R.drawable.icon_head).cacheInMemory(true) 
			.cacheOnDisc(true) 
			.build();
	public static final DisplayImageOptions doc_options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.doctor1) 
			.showImageForEmptyUri(R.drawable.doctor1).showImageOnFail(R.drawable.doctor1).cacheInMemory(true)
			.cacheOnDisc(true) 
			.build();
	public static final DisplayImageOptions news_options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.doctor1)
			.showImageForEmptyUri(R.drawable.doctor1).showImageOnFail(R.drawable.doctor1).cacheInMemory(false)
			.cacheOnDisc(true)
			.build();
	public static final DisplayImageOptions null_defult = new DisplayImageOptions.Builder().showImageOnLoading(0) 
			.showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true) 
			.cacheOnDisc(true) 
			.build();
	public static final DisplayImageOptions default_options_1 = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.img_defualt1) 
			.cacheInMemory(true) 
			.cacheOnDisc(true) 
			.build();
	/** 图片异步加载器 */
	private static ImageLoader mImageLoader;

	public static DisplayImageOptions getUnSuccessOptions(String filePath) {
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);
		Drawable drawable = new BitmapDrawable(bitmap);

		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(drawable).build();
		return displayImageOptions;
	}

	public static ImageLoader getInstance(Context cxt) {
		return mImageLoader == null ? mImageLoader = createImageLoader(cxt) : mImageLoader;
	}

	private static ImageLoader createImageLoader(Context cxt) {
		ImageLoader imageLoader = ImageLoader.getInstance();
		if (imageLoader.isInited()) {
			imageLoader.destroy();
		}
		imageLoader.init(initImageLoaderConfig(cxt));
		return imageLoader;
	}

	/**
	 * 配置图片下载器
	 */
	public static ImageLoaderConfiguration initImageLoaderConfig(Context context) {
		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.icon_head) 
				.showImageForEmptyUri(R.drawable.icon_head).showImageOnFail(R.drawable.icon_head).cacheInMemory(true) 
				.cacheOnDisc(true) 
				.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).defaultDisplayImageOptions(options)
				.threadPriority(Thread.NORM_PRIORITY - 2).threadPoolSize(3).memoryCacheSize(getMemoryCacheSize(context))
				.denyCacheImageMultipleSizesInMemory().discCacheFileNameGenerator(new Md5FileNameGenerator())
				// .discCache(new UnlimitedDiscCache(new File(dirName)))
				.discCacheFileCount(500).tasksProcessingOrder(QueueProcessingType.LIFO).build();
		return config;
	}

	/**
	 * 计算缓存区大小
	 * */
	private static int getMemoryCacheSize(Context context) {
		int memoryCacheSize;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
			int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
			memoryCacheSize = (memClass / 8) * 1024 * 1024; // 1/8 of app memory
															// limit
		} else {
			memoryCacheSize = 2 * 1024 * 1024;
		}
		return memoryCacheSize;
	}

}
