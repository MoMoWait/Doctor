package com.comvee.tnb.ui.record.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MyBitmapFactory {
	public static int max = 0;

	// 这个表示最终从相册选择回来后的数据
	public static ArrayList<ImageItem4LocalImage> tempSelectBitmap = new ArrayList<ImageItem4LocalImage>(); // 选择的图片的临时列表
	// 这个表示在相册中选择的数据
	public static ArrayList<ImageItem4LocalImage> tempSelectBitmapInAlbum = new ArrayList<ImageItem4LocalImage>(); // 选择的图片的临时列表
	// 这个表示需要在相册中显示的所有图片数据
	public static List<ImageItem4LocalImage> tempAllImage = new ArrayList<ImageItem4LocalImage>(); // 选择的图片的临时列表
	public static String albumnName;
	public static boolean canback = true;// 是否能返回相册界面

	public static void clearAll() {
		tempSelectBitmap.clear();
		tempSelectBitmapInAlbum.clear();
		tempAllImage.clear();
		albumnName = null;
		canback = true;
	}

	/**
	 * 压缩需要上传的图片
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 1000) && (options.outHeight >> i <= 1000)) {
				in = new BufferedInputStream(new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}
}
