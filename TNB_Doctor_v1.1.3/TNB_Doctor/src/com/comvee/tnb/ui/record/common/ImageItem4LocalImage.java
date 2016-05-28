package com.comvee.tnb.ui.record.common;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

/**
 * 本地图片显示Item
 * 
 * @author Administrator
 * 
 */
public class ImageItem4LocalImage implements Serializable {
	
	public String imageId;// 图片ID
	public String thumbnailPath;// 本地缩略图地址
	public String imagePath;// 本地原图地址
	public Drawable drawableThumb;// 缩略图
	public int uploadState = 1;// 1表示未上传，2表示上传中，3表示上传成功，4表示上传失败
	public String  sourceImagePath;//阿里云原图片地址
	public String smallImagePath;//阿里云缩略图地址
	
	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}


}
