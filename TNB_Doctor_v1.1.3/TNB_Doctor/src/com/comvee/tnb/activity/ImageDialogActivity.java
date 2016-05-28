package com.comvee.tnb.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.comvee.tnb.R;
import com.comvee.ui.widget.zoom.PhotoView;
import com.comvee.ui.widget.zoom.PhotoViewAttacher.OnViewTapListener;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class ImageDialogActivity extends Activity implements OnViewTapListener {
	boolean num = false;

	/**
	 * 初始化图片下载器，图片缓存地址<i>("/Android/data/[app_package_name]/cache/dirName")</i>
	 */
	public static ImageLoader createImgLoader(Context context, ImageLoader imageLoader) {
		imageLoader = ImageLoader.getInstance();
		if (imageLoader.isInited()) {
			imageLoader.destroy();
		}
		imageLoader.init(initImageLoaderConfig(context));
		return imageLoader;
	}

	/**
	 * 配置图片下载器
	 * 
	 * @param dirName
	 *            文件名
	 */
	private static ImageLoaderConfiguration initImageLoaderConfig(Context context) {
		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.img_defualt)
				.showImageOnFail(R.drawable.img_defualt).cacheInMemory(true).cacheOnDisc(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).defaultDisplayImageOptions(options)
				.threadPriority(Thread.NORM_PRIORITY - 2).threadPoolSize(3).memoryCacheSize(getMemoryCacheSize(context))
				.denyCacheImageMultipleSizesInMemory().discCacheFileNameGenerator(new Md5FileNameGenerator())
				// .discCache(new UnlimitedDiscCache(new File(dirName)))
				.discCacheFileCount(500).tasksProcessingOrder(QueueProcessingType.LIFO).build();
		return config;
	}

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent = getIntent();
		String strFirstPic = intent.getStringExtra("pic");
		PhotoView image = new PhotoView(this);

		ImageLoader mImageLoader = null;
		mImageLoader = createImgLoader(getApplicationContext(), mImageLoader);
		mImageLoader.displayImage(strFirstPic, image);
		image.setLayoutParams(new LayoutParams(-1, -2));
		image.setOnViewTapListener(this);
		setContentView(image);
	}

	public final static void showImg(Context cxt, String picUrl) {
		Intent it = new Intent(cxt, ImageDialogActivity.class);
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		it.putExtra("pic", picUrl);
		cxt.startActivity(it);
	}

	@Override
	public void onViewTap(View view, float x, float y) {
		finish();
	}

}
