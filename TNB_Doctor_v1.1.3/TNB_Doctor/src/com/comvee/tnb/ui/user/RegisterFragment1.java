package com.comvee.tnb.ui.user;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.comvee.http.KWHttpRequest.KwHttpRequestListener;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.ui.more.WebFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UITool;

public class RegisterFragment1 extends BaseFragment implements OnKeyListener, OnClickListener, KwHttpRequestListener {

	private Button btnSubmit;
	private EditText edtPhone;
	private String phoneNum;
	private boolean isReset;
	private TitleBarView mBarView;

	public static RegisterFragment1 newInstance(String phoneNum, boolean isRestPwd) {
		RegisterFragment1 fragment = new RegisterFragment1();
		Bundle bunle = new Bundle();
		bunle.putString("phoneNum", phoneNum);
		bunle.putBoolean("isReset", isRestPwd);
		fragment.setArguments(bunle);
		return fragment;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_regest_1;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	public RegisterFragment1() {
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	@Override
	public void onLaunch(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onLaunch();
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		init();
	}

	/**
	 * 免责申明
	 */
	private void toStatement() {
		String url = ConfigParams.getConfig(getApplicationContext(), ConfigParams.TEXT_STATEMENT);
		WebFragment frag = WebFragment.newInstance("声明", url);
		toFragment(frag, true, true);
	}

	private void init() {
		isReset = getArgument().getBoolean("isReset");
		phoneNum = getArgument().getString("phoneNum");
		btnSubmit = (Button) findViewById(R.id.btn_submit);
		btnSubmit.setOnClickListener(this);

		edtPhone = (EditText) findViewById(R.id.edt_phone);
		edtPhone.setText(phoneNum);
		edtPhone.setOnKeyListener(this);
		findViewById(R.id.btn_statement).setOnClickListener(this);
		UITool.setEditWithClearButton(edtPhone, R.drawable.btn_txt_clear);
		WebView mWebView = (WebView) findViewById(R.id.web);
		if (isReset) {
			final TextView tv = (TextView) findViewById(R.id.tv_regest_step3);
			tv.setText(getString(R.string.regist_reset_pwd));
			mBarView.setTitle(getString(R.string.regist_getback_pwd));
			mWebView.setVisibility(View.GONE);
		} else {
			mBarView.setTitle(getString(R.string.regist1));

			// mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
			// mWebView.loadUrl(ConfigParams.getConfig(getApplicationContext(),
			// ConfigParams.TEXT_VIP_RELOGIN));
		}

	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.btn_submit:
			toSubmit();
			break;
		case R.id.btn_statement:
			toStatement();
			break;
		default:
			break;
		}

	}

	private boolean checkEdit(EditText edt) {
		final String str = edt.getText().toString();
		if (TextUtils.isEmpty(str)) {
			UITool.showEditError(edt, "不能为空！");
			return false;
		}
		// else if (!StringUtil.isPhoneNum(str)) {
		// UITool.showEditError(edt, "请输入正确的手机号");
		// return false;
		// }
		return true;
	}

	private void toSubmit() {
		if (!checkEdit(edtPhone)) {
			return;
		}
		showProgressDialog(getString(R.string.msg_loading));
		final String strPhone = edtPhone.getText().toString();

		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.SEND_SMS);
		http.setPostValueForKey("user_no", strPhone);
		http.setPostValueForKey("type", String.valueOf(isReset ? 2 : 1));
		http.setListener(1, this);
		http.startAsynchronous();
	}

	private void onSucces() {
		final String phone = edtPhone.getText().toString();
		toFragment(RegisterFragment2.newInstance(phone, isReset), false, true);
	}

	@Override
	public void loadFailed(int arg0, int arg1) {
		cancelProgressDialog();

		ComveeHttpErrorControl.parseError(getActivity(), arg1);
	}

	@Override
	public void loadFinished(int arg0, byte[] arg1) {
		cancelProgressDialog();
		switch (arg0) {
		case 1:
			parse(arg1);
			break;
		default:
			break;
		}
	}

	private void parse(byte[] arg1) {
		try {
			ComveePacket packet = ComveePacket.fromJsonString(arg1);

			if (packet.getResultCode() == 0) {
				onSucces();
			} else {
				// showToast(packet.getResultMsg());
				ComveeHttpErrorControl.parseError(getActivity(), packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onBackPress() {
		return false;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			if (!isProgressDialogShowing()) {
				toSubmit();
			}
			return true;
		}
		return false;
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		try {
			UITool.closeInputMethodManager(getContext(), edtPhone.getWindowToken());
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
