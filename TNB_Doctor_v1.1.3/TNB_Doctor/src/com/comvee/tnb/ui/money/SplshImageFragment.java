package com.comvee.tnb.ui.money;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.comvee.frame.BaseFragment;
import com.comvee.tnb.R;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tool.AppUtil;
/**
 * 测血糖活动页
 * @author yujun
 *
 */
public class SplshImageFragment extends BaseFragment implements OnClickListener {

	private ImageView mSplashImage;

	@Override
	public int getViewLayoutId() {
		return R.layout.splash_home_fragment;
	}

	@Override
	public void onLaunch(Bundle dataBundle) {
		super.onLaunch(dataBundle);
		init();
	}

	private void init() {
		mSplashImage = (ImageView) findViewById(R.id.splash_img);
		mSplashImage.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Toast.makeText(mContext, "进入活动界面", 0).show();
	}

	/**
	 * 获取闪屏页图片
	 */
	public static void getLunchImage(final Activity activity) {
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		final int W = mDisplayMetrics.widthPixels;
		final int H = mDisplayMetrics.heightPixels;
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... arg0) {
				ComveeHttp http = new ComveeHttp(activity, ConfigUrlMrg.LOAD_HEALTH_INDEX);
				http.setPostValueForKey("height", H + "");
				http.setPostValueForKey("width", W + "");
				String result = http.startSyncRequestString();

				return result;
			}

			protected void onPostExecute(String result) {
				if (result == null) {
					return;
				}
				try {
					final ComveePacket packet = ComveePacket.fromJsonString(result);
					if (packet.getResultCode() == 0) {
						JSONObject obj = packet.optJSONObject("body");
						String url = obj.optString("url");
						int time = obj.optInt("times");
						String webUrl = obj.optString("urlHtml");
						String title = obj.optString("title");
						setHealthTimes(activity, time);
						if (AppUtil.getImage() == null || ConfigParams.getNetImgUrl(activity) == null
								|| (ConfigParams.getNetImgUrl(activity) != null && !ConfigParams.getNetImgUrl(activity).equals(url))) {

							if (url != null && !url.equals("")) {
								setActivityUrl(activity, webUrl);
								setActivityTitle(activity, title);
								AppUtil.loadImageToLocal(url);
								SetNetImgUrl(activity, url);
							} else {
								setActivityUrl(activity, "");
								setActivityTitle(activity, "");
								SetNetImgUrl(activity, null);
								AppUtil.deleatImage(activity);
							}

						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			};
		}.execute();

	}

	public static void setActivityTitle(Context cxt, String Title) {
		cxt.getSharedPreferences(TN, 0).edit().putString("activityTitle", Title).commit();
	}

	private static final String TN = "config1";

	public static void setActivityUrl(Context cxt, String url) {
		cxt.getSharedPreferences(TN, 0).edit().putString("activityUrl", url).commit();
	}

	public static void setHealthTimes(Context cxt, int time) {
		cxt.getSharedPreferences(TN, 0).edit().putInt("healthTime", time).commit();
	}

	public static void SetNetImgUrl(Context cxt, String str) {
		cxt.getSharedPreferences(TN, 0).edit().putString("NetImgUrl", str).commit();
	}
}
