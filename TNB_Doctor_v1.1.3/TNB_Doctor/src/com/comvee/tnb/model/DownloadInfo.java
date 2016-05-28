package com.comvee.tnb.model;

import java.io.Serializable;

import com.comvee.annotation.sqlite.Id;
import com.comvee.annotation.sqlite.Table;

@Table(name = "download_info_7")
public class DownloadInfo implements Serializable{
	public String url;
	public long fileSize;
	public long complete;
	public String fileName;
	public String path;
	@Id(column = "key")
	public String key;

	public DownloadInfo(String url, long fileSize, long complete, String fileName, String path) {
		super();
		this.url = url;
		this.fileSize = fileSize;
		this.complete = complete;
		this.fileName = fileName;
		this.key = fileName;
		this.path = path;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public long getComplete() {
		return complete;
	}

	public void setComplete(long complete) {
		this.complete = complete;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
