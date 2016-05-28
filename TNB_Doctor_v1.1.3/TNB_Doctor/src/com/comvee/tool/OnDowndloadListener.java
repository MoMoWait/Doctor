package com.comvee.tool;

import android.os.Handler;
import android.os.Message;

import com.comvee.tnb.model.DownloadInfo;

public abstract class OnDowndloadListener extends Handler {

	@Override
	public final void handleMessage(Message msg) {
		DownloadInfo info = (DownloadInfo) msg.obj;

		switch (msg.what) {
		case 0:
			update(info);
			break;
		case 1:// 错误
			error(msg.arg1, info);
			break;
		case 2:
			complete(info);
			break;
		default:
			break;
		}

		super.handleMessage(msg);
	}

	/**
	 * 1001下载失败
	 * 
	 * @param code
	 */
	public abstract void error(int code, DownloadInfo info);

	public abstract void complete(DownloadInfo info);

	public abstract void update(DownloadInfo info);

}
