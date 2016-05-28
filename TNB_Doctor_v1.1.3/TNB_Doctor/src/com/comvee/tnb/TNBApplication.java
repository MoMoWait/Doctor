package com.comvee.tnb;

import com.comvee.BaseApplication;
import com.comvee.frame.FragmentMrg;
import com.comvee.log.ComveeLog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tool.UmenPushUtil;

public class TNBApplication extends BaseApplication {
//	private PatchManager patchManager;

	@Override
	public void onCreate() {
		super.onCreate();

		//控制Fragment的点击次数
		FragmentMrg.setFastDoubleDuration(200);

		ComveeHttp.init();

		ComveeLog.init(getApplicationContext());
		
		ComveeHttp.DEBUG = true;
		UmenPushUtil.initPushInApplication();
		try {
//			CrashReport.initCrashReport(getApplicationContext(), "900022134", false);
		}catch (Exception e){
			e.printStackTrace();
		}
		//initAndfix();
	}

	private void initAndfix() {
//		patchManager = new PatchManager(this);
//		patchManager.init(Util.getAppVersionName(this, getPackageName()));
//		patchManager.loadPatch();
		
	}

	public void getAndFixFile() {
//		//补丁版本号，默认是0，app同一个版本每增加一个补丁文件就要加1，最新的补丁号要与接口最新的补丁一致
//		int patchCode = 0;
//		Log.e("---------------------", "-----------------"+patchCode+"--------------------");
//		AndFixLoader loader = new AndFixLoader();
//		loader.loaderStar(new NetworkCallBack() {
//			@Override
//			public void callBack(int what, int status, Object obj) {
//				if (obj == null) {
//					return;
//				}
//				AndFixModel model = (AndFixModel) obj;
//				if (!TextUtils.isEmpty(model.patch_url)) {
//					downPatch(model.patch_name, model.patch_url);
//				}
//			}
//		}, Util.getAppVersionCode(this, getPackageName()) + "", patchCode + "");
	}

	public void downPatch(String fileName, String fileUrl) {
//		String TempFilePath = null;
//		if (!Util.SDCardExists()) {
//			TempFilePath = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileName;
//		} else {
//			TempFilePath = Environment.getExternalStorageDirectory() + "/.tnb/app/" + fileName;
//		}
//		final String filePath = TempFilePath;
//		KWDownloadFileParams kwDownloadFileParams = new KWDownloadFileParams(fileUrl, getString(R.string.app_name), filePath, this, 1001011);
//
//		KWDownLoadFileTask task = new KWDownLoadFileTask(this, new KWDownLoadFileListener() {
//
//			@Override
//			public void onLoadProgress(Context arg0, KWDownloadFileParams arg1, int arg2, long arg3, int arg4) {
//
//			}
//
//			@Override
//			public void onLoadFinish(Context arg0, KWDownloadFileParams arg1) {
//				try {
//					patchManager.addPatch(filePath);
//				} catch (Exception e) {
//				}
//
//				File file = new File(filePath);
//				if (file.exists()) {
//					file.delete();
//				}
//
//			}
//
//			@Override
//			public boolean onLoadFileExisting(Context arg0, KWDownloadFileParams arg1) {
//				return false;
//			}
//
//			@Override
//			public void onLoadFailed(Context arg0, KWDownloadFileParams arg1, int arg2) {
//
//			}
//
//			@Override
//			public void onLoadCancel(Context arg0, KWDownloadFileParams arg1) {
//
//			}
//		});
//		task.execute(kwDownloadFileParams);

	}

}
