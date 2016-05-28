package com.comvee.tnb.ui.more;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tnb.widget.TitleBarView;

/**
 * 有注册按钮的web页面
 * 
 * @author Administrator
 * 
 */
public class WebRegiterFrag extends BaseFragment implements OnClickListener {
	private WebView web;
	private ProgressBar mBar;
	private String url, title;
	private TitleBarView mBarView;

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		init();
	}

	public WebRegiterFrag() {
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.web_register;
	}

	public static WebRegiterFrag newInstance(String title, String url) {
		WebRegiterFrag bookIndexRootFragment = new WebRegiterFrag();
		bookIndexRootFragment.setUrl(url);
		bookIndexRootFragment.setmTitle(title);
		return bookIndexRootFragment;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setmTitle(String title) {
		this.title = title;

	}

	private void init() {
		if (title != null) {
			mBarView.setTitle(title);
		}
		findViewById(R.id.btn_next).setOnClickListener(this);
		web = (WebView) findViewById(R.id.web);
		mBar = (ProgressBar) findViewById(R.id.pro_loading);
		mBar.setProgress(0);
		web.setVisibility(View.VISIBLE);
		WebSettings setings = web.getSettings();
		setings.setLoadWithOverviewMode(true);
		setings.setUseWideViewPort(true);
		setings.setJavaScriptEnabled(true);
		web.setWebChromeClient(new WebChromeClient() {

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
		web.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				web.loadUrl(url);
				return true;
			}
		});
		web.loadUrl(url);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_next:
			toFragment(LoginFragment.newInstance(), true, true);
			break;

		default:
			break;
		}
	}
}
