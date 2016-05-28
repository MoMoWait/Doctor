package com.comvee.tnb.ui.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.widget.TitleBarView;

public class RegisterServerFragment extends BaseFragment implements OnClickListener {

	private WebView mWebView;
	private View mPro;
	private TitleBarView mBarView;

	public static RegisterServerFragment newInstance() {
		RegisterServerFragment fragment = new RegisterServerFragment();
		return fragment;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_regest_server;
	}

	public RegisterServerFragment() {
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		init();
		mBarView.setTitle("服务条款");
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		requestData();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void init() {
		mWebView = (WebView) findViewById(R.id.view_web);

		mPro = findViewById(R.id.pro);

		WebSettings setings = mWebView.getSettings();
		setings.setAppCacheEnabled(true);

		// setings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		setings.setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(new JSClient(), "js");
		mWebView.setWebChromeClient(new WebChromeClient() {
		});
		mWebView.setWebViewClient(new WebViewClient() {
			public void onPageFinished(WebView view, String url) {
				mPro.setVisibility(View.GONE);
			};

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				mWebView.loadUrl(url);
				return true;
			}
		}

		);
		mWebView.loadUrl(ConfigUrlMrg.MORE_WEB_SERVER);
	}

	private void requestData() {
		// startPro();
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.btn_submit:
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onBackPress() {
		return false;
	}

	class JSClient {

		private static final int RESULT_RREFRUSH = 1;
		private static final int RESULT_UNCONTROL = 0;

		public String getIMEI() {
			TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
			return tm.getDeviceId();
			// return "asda";
		}

		public String getIMSI() {
			TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
			return tm.getSubscriberId();
		}

		// 发短信
		public void sendSMS(String phoneNum, String content) {
			Uri uri = Uri.parse(String.format("smsto:%s", phoneNum));
			Intent it = new Intent(Intent.ACTION_SENDTO, uri);
			it.putExtra("sms_body", content);
			startActivity(it);
		}

		// 成功回调
		public void callbackSucces(String resultCode, String msg) {

		}

		// 失败回调
		public void callbackFail(String resultCode, String msg) {

		}

		// 对话框
		public void showAlert(String title, String msg) {
			new AlertDialog.Builder(getActivity()).setTitle(title).setMessage(msg).setPositiveButton("确定", null).create().show();
		}

		// 打电话
		public void callPhone(String phoneNum) {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
			startActivity(intent);
		}

		// 短时提醒
		public void showToast(String time, String msg) {
			int result = getInt(time);
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
		}

		public void close(String resulteCode) {
			int result = getInt(resulteCode);
			switch (result) {
			case RESULT_RREFRUSH:
				// setResult(getActivity().RESULT_OK);
				break;
			default:
				break;
			}
			FragmentMrg.toBack(getActivity());
			// finish();
		}

		private int getInt(String code) {
			int result = -12306;
			try {
				result = Integer.valueOf(code);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;

		}

	}
}
