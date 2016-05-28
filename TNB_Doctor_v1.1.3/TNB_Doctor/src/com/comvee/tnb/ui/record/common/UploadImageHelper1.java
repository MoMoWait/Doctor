package com.comvee.tnb.ui.record.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.comvee.ThreadHandler;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;

public class UploadImageHelper1 implements OnHttpListener {
	private Context context;
	private ComveeHttp bighttp;
	private List<String> bigUrl = new ArrayList<String>();
	private List<ImageItem4LocalImage> waitingUpload = new ArrayList<ImageItem4LocalImage>();
	private int progressIndex = 0;// 当前正在处理的索引
	ImageUploadFinishListener mImageUploadResultListener;

	private File uploadFile;
	private final String uploadFileName = "upload.jpg";

	public UploadImageHelper1(Context context_, List<ImageItem4LocalImage> waitingData) {
		this.context = context_;
		bighttp = new ComveeHttp(context, ConfigUrlMrg.UPLOAD_FILE);
		for (int i = 0; i < waitingData.size(); i++) {
			ImageItem4LocalImage image = waitingData.get(i);
			if (TextUtils.isEmpty(image.sourceImagePath)) {
				waitingUpload.add(image);
			}
		}
	}

	public void start() {
		if (waitingUpload.size() == 0) {
			mImageUploadResultListener.allFinish(bigUrl);
			return;
		}
		startCompressNUpload();
	}

	/**
	 * 压缩并上传
	 */
	private void startCompressNUpload() {
		final ImageItem4LocalImage imageItem = waitingUpload.get(progressIndex);
		//new CompressSaveThread(480, 800, imageItem.imagePath, 1).start();
		ThreadHandler.postWorkThread(new Runnable() {
			@Override
			public void run() {
				try {
					BitmapFactory.Options newOpts = new BitmapFactory.Options();
					newOpts.inJustDecodeBounds = true;
					Bitmap bitmap = BitmapFactory.decodeFile(imageItem.imagePath, newOpts);// 此时返回bm为空
					newOpts.inJustDecodeBounds = false;
					int w = newOpts.outWidth;
					int h = newOpts.outHeight;
					float be = 1;// be=1表示不缩放
					if (w > h && w > 480) {// 如果宽度大的话根据宽度固定大小缩放
						be = (w * 1.0f / 480);
					} else if (w < h && h > 800) {// 如果高度高的话根据宽度固定大小缩放
						be = (h * 1.0f / 800);
					}
					if (be <= 0)
						be = 1;
					newOpts.inSampleSize = (int) Math.ceil(be);// 设置缩放比例
					bitmap = BitmapFactory.decodeFile(imageItem.imagePath, newOpts);
					// ** 质量压缩 *//*
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
					int options = 100;
					while (baos.toByteArray().length / 1024 > 90) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
						baos.reset();// 重置baos即清空baos
						options = (int) (options * 0.99);
						bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
						if (options <= 1) {
							break;
						}
					}
					byte[] bts = baos.toByteArray();
					bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, bts.length);
					// ***/
					FileOutputStream out;
					uploadFile = new File(context.getFilesDir() + uploadFileName);
					out = new FileOutputStream(uploadFile);
					bitmap.compress(Bitmap.CompressFormat.JPEG, options, out);
					out.flush();
					out.close();
					bitmap.recycle();
					System.gc();
					bighttp.setUploadFileForKey("file", uploadFile.getPath());
					bighttp.setPostValueForKey("platCode", "198");
					bighttp.setOnHttpListener(1, UploadImageHelper1.this);
					bighttp.startAsynchronous();

				} catch (Exception e) {
				}

			}
		});
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		try {
			uploadFile.delete();
			ComveePacket packet = ComveePacket.fromJsonString(b);
			String url = parsePacket(packet.toString());
			bigUrl.add(url);
			if (progressIndex == waitingUpload.size() - 1) {
				mImageUploadResultListener.allFinish(bigUrl);
			} else {
				progressIndex++;
				startCompressNUpload();
			}
		} catch (Exception e) {
			Log.e("comvee", e.getMessage(), e);
			mImageUploadResultListener.allFinish(bigUrl);
		}

	}

	@Override
	public void onFialed(int what, int errorCode) {
		uploadFile.delete();
		mImageUploadResultListener.allFinish(bigUrl);
	}

	private String parsePacket(String jsonStr) {
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			JSONObject resultJsonObject = jsonObject.getJSONArray("body").getJSONObject(0);
			String key = resultJsonObject.getString("key");
			return key;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonStr;

	}

	class CompressSaveThread extends Thread {
		private int toWidth;
		private String picPath;
		private int toHeight;

		@Override
		public void run() {
			try {
				BitmapFactory.Options newOpts = new BitmapFactory.Options();
				newOpts.inJustDecodeBounds = true;
				Bitmap bitmap = BitmapFactory.decodeFile(picPath, newOpts);// 此时返回bm为空
				newOpts.inJustDecodeBounds = false;
				int w = newOpts.outWidth;
				int h = newOpts.outHeight;
				float be = 1;// be=1表示不缩放
				if (w > h && w > toWidth) {// 如果宽度大的话根据宽度固定大小缩放
					be = (w * 1.0f / toWidth);
				} else if (w < h && h > toHeight) {// 如果高度高的话根据宽度固定大小缩放
					be = (h * 1.0f / toHeight);
				}
				if (be <= 0)
					be = 1;
				newOpts.inSampleSize = (int) Math.ceil(be);// 设置缩放比例
				bitmap = BitmapFactory.decodeFile(picPath, newOpts);
				// ** 质量压缩 *//*
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
				int options = 100;
				while (baos.toByteArray().length / 1024 > 90) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
					baos.reset();// 重置baos即清空baos
					options = (int) (options * 0.99);
					bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
					if (options <= 1) {
						break;
					}
				}
				byte[] bts = baos.toByteArray();
				bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, bts.length);
				// ***/
				FileOutputStream out;
				uploadFile = new File(context.getFilesDir() + uploadFileName);
				out = new FileOutputStream(uploadFile);
				bitmap.compress(Bitmap.CompressFormat.JPEG, options, out);
				out.flush();
				out.close();
				bitmap.recycle();
				System.gc();
				bighttp.setUploadFileForKey("file", uploadFile.getPath());
				bighttp.setPostValueForKey("platCode", "198");
				bighttp.setOnHttpListener(1, UploadImageHelper1.this);
				bighttp.startAsynchronous();

			} catch (Exception e) {
			}
		}

		public CompressSaveThread(int toWidth, int toHeight, String picPath, int what) {
			this.toWidth = toWidth;
			this.picPath = picPath;
			this.toHeight = toHeight;
		}
	}

	public void setAllfishListener(ImageUploadFinishListener listener) {
		this.mImageUploadResultListener = listener;
	}

	public interface ImageUploadFinishListener {

		public void allFinish(List<String> bigUrl);
	}
}
