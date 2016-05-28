package com.comvee.tnb.ui.more;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.ui.assess.AssessFragment;
import com.comvee.tnb.ui.log.RecordTableFragment;
import com.comvee.tnb.ui.machine.BarCodeFragment;
import com.comvee.tnb.ui.record.RecordMainFragment;
import com.comvee.tnb.ui.task.TaskCenterFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;

/**
 * 月度报告详细页 web页面
 * 
 * @author Administrator
 * 
 */
public class WebMouthlySummarizeFragment extends BaseFragment {
	private ProgressBar mBar;
	private WebView mWebView;
	private String url;
	private String year;
	private TitleBarView mBarView;

	public WebMouthlySummarizeFragment() {
	}

	public WebMouthlySummarizeFragment(String url, String year) {
		this.year = year;
		this.url = url;
	}

	public static void toFragment(FragmentActivity fragment, String url, String year) {
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		bundle.putString("year", year);
		FragmentMrg.toFragment(fragment, WebMouthlySummarizeFragment.class, bundle, true);
	}

	@Override
	public int getViewLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.mouth_sum_fragment;
	}

	private Handler h;

	@Override
	public void onLaunch(Bundle bundle) {
		super.onLaunch(bundle);
		if (bundle != null) {
			this.year = bundle.getString("year");
			this.url = bundle.getString("url");
		}
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		init();
		h = new Handler() {
			public void handleMessage(Message msg) {
				Bundle b = msg.getData();

				String str = b.getString("key");

				if (str.equals("ydzj01")) {
					toFragment(RecordTableFragment.newInstance(1), true, true);// 饮食
				}
				if (str.equals("ydzj02")) {
					toFragment(RecordTableFragment.newInstance(2), true, true);// 运动
				}
				if (str.equals("ydzj03")) {
					toFragment(RecordTableFragment.newInstance(3), true, true);// 生活
				}
				if (str.equals("ydzj04")) {
					toFragment(RecordTableFragment.newInstance(5), true, true);// 用药
				}
				if (str.equals("ydzj05")) {
					toFragment(RecordTableFragment.newInstance(6), true, true);// 血糖监测
				}
				if (str.equals("ydzj06")) {
					toFragment(RecordTableFragment.newInstance(4), true, true);// 情绪
				}

				if (str.equals("ydzj07")) {
					RecordMainFragment f = RecordMainFragment.newInstance(false, 0);
					f.setOldTabIndex(3);
					toFragment(f, true, true);// 血糖
				}
				if (str.equals("ydzj08")) {
					RecordMainFragment f = RecordMainFragment.newInstance(false, 8);
					f.setOldTabIndex(3);
					toFragment(f, true, true);// 血压
				}

				if (str.equals("ydzj09")) {
					RecordMainFragment f = RecordMainFragment.newInstance(false, 9);
					f.setOldTabIndex(3);
					toFragment(f, true, true);// bmi
				}
				if (str.equals("ydzj10")) {
					mBarView.hideRightButton();
					toFragment(TaskCenterFragment.newInstance(), true, true);// 任务
				}
				if (str.equals("ydzj11")) {

					String url = ConfigParams.getConfig(getApplicationContext(), ConfigParams.TEXT_MECHINE_INFO) + "?origin=android&sessionID="
							+ UserMrg.getSessionId(getApplicationContext()) + "&sessionMemberID="
							+ UserMrg.getMemberSessionId(getApplicationContext());
					WebFragment web = WebFragment.newInstance("购买设备", url);

					mBarView.hideRightButton();
					toFragment(web, true, true);// 购买
				}

				if (str.equals("ydzj12")) {
					BarCodeFragment.toFragment(getActivity(), false, 1);// 绑定

				}
				if (str.equals("ydzj13")) {
					toFragment(AssessFragment.class, null, true);
				}
			}
		};
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void init() {

		mBar = (ProgressBar) findViewById(R.id.pro_loading);
		mBar.setProgress(0);

		mWebView = (WebView) findViewById(R.id.web);
		mWebView.setVisibility(View.VISIBLE);
		WebSettings setings = mWebView.getSettings();
		setings.setAppCacheEnabled(true);
		setings.setLoadWithOverviewMode(true);
		setings.setUseWideViewPort(true);
		setings.setCacheMode(WebSettings.LOAD_DEFAULT); // 设置 缓存模式
		setings.setJavaScriptEnabled(true);// 设置可使用js
		mWebView.addJavascriptInterface(new JSClient(), "js");
		mWebView.setDrawingCacheEnabled(false);// 允许进行可视区域的截图
		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, final int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (newProgress < 100) {
					mBar.setVisibility(View.VISIBLE);
				} else if (newProgress == 100) {
					mBar.setVisibility(View.GONE);
				}
				mBar.setProgress(newProgress);
			}
		});
		mWebView.getSettings().setAppCacheEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				mWebView.loadUrl(url);
				return true;
			}
		});
		mBarView.setTitle(year);
		mWebView.loadUrl(url);
	}

	final class JSClient {

		/**
		 * 月度报告回调
		 * 
		 * @param str
		 */
		public void toOtherFragment(String str) {

			Message msg = new Message();
			Bundle b = new Bundle();
			b.putString("key", str);
			msg.setData(b);
			h.sendMessage(msg);

		}

	}
}
