package com.comvee.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.comvee.http.KWHttpRequest;
import com.comvee.tool.Log;

public class ComveeLog {

	public static Context CONTEXT;
	public static String WHAT_BOOT = "boot";
	public static String WHAT_PAGE_INDEX = "page_index";
	public static String WHAT_PAGE_ASSESS = "page_assess";
	private static String mVesion;

	public static int TYPE_LUANCHER = 1001;
	public static int TYPE_PAGE = 1002;
	public static int TYPE_EVENT = 1003;
	public static int TYPE_ERROR = 1004;

	private static String sessionId;
	private static HashMap<String, Long> mMap = new HashMap<String, Long>();
	private static ExecutorService mTask = Executors.newScheduledThreadPool(3);
	public static void init(Context cxt) {
		CONTEXT = cxt;
		try {
			mVesion = CONTEXT.getPackageManager().getPackageInfo(CONTEXT.getPackageName(), 0).versionName + "";
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public synchronized static void log(final String data,final int type) {
		mTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					LogInfoDom.getInstance().insert(data, type);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});


	}

	protected synchronized static void logError(final String error) {
		mTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Log.e(error);
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

					JSONObject sb = new JSONObject();
					try {
						sb.put("stackTrace", error);
						sb.put("time", format.format(new Date(System.currentTimeMillis())));
						sb.put("version", mVesion);
					} catch (Exception e) {
						e.printStackTrace();
					}
					ComveeLog.log(sb.toString(), ComveeLog.TYPE_ERROR);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});

	}

	public synchronized static void onError() {
		CrashHandler.getInstance();
	}

	public synchronized static String getDeviceId() {
		try {
			SharedPreferences sp = CONTEXT.getSharedPreferences("comvee_log", 0);
			String deviceId = sp.getString("deviceId", null);
			if (TextUtils.isEmpty(deviceId)) {
				try {
					// 有可能被杀软屏蔽，所以加try catch
					TelephonyManager tm = (TelephonyManager) CONTEXT.getSystemService(Context.TELEPHONY_SERVICE);
					deviceId = tm.getDeviceId();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (TextUtils.isEmpty(deviceId)) {
					deviceId = UUID.randomUUID().toString();
				}
				sp.edit().putString("deviceId", deviceId).commit();
			}
			return deviceId;
		}catch (Exception e){
			e.printStackTrace();
			return "";
		}
	}

	public synchronized static void onResumeActivity(final String actName,final String channelName,final String userid) {
		mTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					if (!mMap.containsKey(actName) || System.currentTimeMillis() - mMap.get(actName) > 30000) {
						onError();

						Log.e("记录启动-->" + actName);
						sessionId = UUID.randomUUID().toString();
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						JSONObject sb = new JSONObject();
						try {
							sb.put("os_version", android.os.Build.VERSION.SDK_INT + "");
							sb.put("version", mVesion);
							sb.put("resolution", getScreenPX());
							sb.put("deviceid", getDeviceId());
							sb.put("modulename", Build.MODEL);
							sb.put("platform", "android");
							sb.put("devicename", android.os.Build.DEVICE);
							sb.put("userid", userid);
							sb.put("time", format.format(new Date(System.currentTimeMillis())));
							sb.put("loadfrom", channelName);
							sb.put("mccmnc", ((TelephonyManager) CONTEXT.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId());
							sb.put("language", CONTEXT.getResources().getConfiguration().locale.getLanguage());
							sb.put("network", ((ConnectivityManager) CONTEXT.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo().getTypeName());
						} catch (Exception e) {
							e.printStackTrace();
						}
						log(sb.toString(), TYPE_LUANCHER);
						syncData();
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * 获取分辨率
	 * 
	 * @return 如：480x800
	 */
	private  static String getScreenPX() {
		try {
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager windowMgr = (WindowManager) CONTEXT.getSystemService(Context.WINDOW_SERVICE);
			windowMgr.getDefaultDisplay().getMetrics(dm);
			int width = dm.widthPixels;
			int height = dm.heightPixels;
			return width + "x" + height;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 统计事件
	 * 
	 * @param eventId
	 *            自定义事件ID
	 * @param label
	 *            事件描述
	 */
	public synchronized static void onEvent(String eventId, String label) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			JSONObject sb = new JSONObject();
			try {
				sb.put("event_id", eventId);
				sb.put("label", label);
				sb.put("vesion", mVesion);
				sb.put("time", format.format(new Date(System.currentTimeMillis())));
			} catch (Exception e) {
				e.printStackTrace();
			}
			log(sb.toString(), TYPE_EVENT);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * activity onResume时调用
	 * 
	 * @param actName
	 */
	public synchronized static void onResumeActivity(String actName) {
		onResumeActivity(actName, null, null);
	}

	/**
	 * activity onPause时调用
	 * 
	 * @param actName
	 */
	public  synchronized static void onPauseActivity(String actName) {
		mMap.put(actName, System.currentTimeMillis());
		syncData();
	}

	/**
	 * fragment onResume时调用
	 * 
	 * @param fragName
	 */
	public synchronized static void onResumeFragment(String fragName) {
		mMap.put(fragName, System.currentTimeMillis());
	}

	/**
	 * fragment onPause时调用
	 * 
	 * @param fragName
	 */
	public synchronized static void onPauseFragment(String fragName) {
		try {
			long startTime = mMap.get(fragName);
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			if (fragName != null && mMap != null) {
				mMap.remove(fragName);
			}
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			JSONObject sb = new JSONObject();
			try {
				sb.put("startMils", format.format(new Date(startTime)));
				sb.put("endMils", format.format(new Date(endTime)));
				sb.put("duration", (duration / 1000f) + "");
				sb.put("activity", fragName);
				sb.put("sessionId", sessionId);
				sb.put("version", mVesion);
			} catch (Exception e) {
				e.printStackTrace();
			}

			log(sb.toString(), TYPE_PAGE);
		}catch ( Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 获取需要上传的数据
	 * 
	 * @param type
	 * @return
	 */
	private synchronized static String getSyncDataStr(int type) {
		return LogInfoDom.getInstance().getSycnData(String.format("DB_TYPE='%d'", type)).toString();
	}

	// private static final String URL_DEVICE =
	// "http://192.168.20.75:8099/fetch//servlet/FetchDeviceServlet";
	// private static final String URL_ACTIVITY =
	// "http://192.168.20.75:8099/fetch//servlet/FetchActivityServlet";
	// private static final String URL_EVENT =
	// "http://192.168.20.75:8099/fetch//servlet/FetchEventServlet";
	// private static final String URL_ERROR =
	// "http://192.168.20.75:8099/fetch//servlet/FetchErrorServlet";
	private static final String URL_DEVICE = "http://report.zhangkong.me/servlet/FetchDeviceServlet";
	private static final String URL_ACTIVITY = "http://report.zhangkong.me/servlet/FetchActivityServlet";
	private static final String URL_EVENT = "http://report.zhangkong.me/servlet/FetchEventServlet";
	private static final String URL_ERROR = "http://report.zhangkong.me/servlet/FetchErrorServlet";

	/**
	 * 同步数据
	 */
	public static void syncData() {
		mTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					String result = null;
					String str = null;
					String deviceid = getDeviceId();
					String appkey = "100001";
					KWHttpRequest request = null;
					if (!"[]".equals(result = getSyncDataStr(TYPE_LUANCHER))) {
						Log.e(result);
						request = new KWHttpRequest(CONTEXT, URL_DEVICE);
						request.setPostValueForKey("platform", "1");
						request.setPostValueForKey("appkey", appkey);
						request.setPostValueForKey("deviceid", deviceid);
						request.setPostValueForKey("data", result);
						try {
							LogInfoDom.getInstance().updateOpt(TYPE_LUANCHER);
							str = request.startSyncRequestString();
							if (!TextUtils.isEmpty(str)) {
								Log.e("upload data TYPE_LUANCHER");
								LogInfoDom.getInstance().removeAlreadyOpt(TYPE_LUANCHER);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					if (!"[]".equals(result = getSyncDataStr(TYPE_PAGE))) {
						Log.e(result);
						request = new KWHttpRequest(CONTEXT, URL_ACTIVITY);
						request.setPostValueForKey("platform", "1");
						request.setPostValueForKey("appkey", appkey);
						request.setPostValueForKey("deviceid", deviceid);
						request.setPostValueForKey("data", result);
						try {
							LogInfoDom.getInstance().updateOpt(TYPE_PAGE);
							str = request.startSyncRequestString();
							if (!TextUtils.isEmpty(str)) {
								Log.e("upload data URL_ACTIVITY");
								LogInfoDom.getInstance().removeAlreadyOpt(TYPE_PAGE);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					if (!"[]".equals(result = getSyncDataStr(TYPE_EVENT))) {
						Log.e(result);
						request = new KWHttpRequest(CONTEXT, URL_EVENT);
						request.setPostValueForKey("platform", "1");
						request.setPostValueForKey("appkey", appkey);
						request.setPostValueForKey("deviceid", deviceid);
						request.setPostValueForKey("data", result);
						try {
							LogInfoDom.getInstance().updateOpt(TYPE_EVENT);
							str = request.startSyncRequestString();
							if (!TextUtils.isEmpty(str)) {
								Log.e("upload data URL_EVENT");
								LogInfoDom.getInstance().removeAlreadyOpt(TYPE_EVENT);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					if (!"[]".equals(result = getSyncDataStr(TYPE_ERROR))) {
						Log.e(result);
						request = new KWHttpRequest(CONTEXT, URL_ERROR);
						request.setPostValueForKey("platform", "1");
						request.setPostValueForKey("appkey", appkey);
						request.setPostValueForKey("deviceid", deviceid);
						request.setPostValueForKey("data", result);
						try {
							LogInfoDom.getInstance().updateOpt(TYPE_ERROR);
							str = request.startSyncRequestString();
							if (!TextUtils.isEmpty(str)) {
								Log.e("upload data TYPE_ERROR");
								LogInfoDom.getInstance().removeAlreadyOpt(TYPE_ERROR);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});

	}
}
