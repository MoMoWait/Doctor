package com.comvee.tnb.ui.user;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.comvee.http.KWHttpRequest.KwHttpRequestListener;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UITool;

public class RegisterFragment2 extends BaseFragment implements OnKeyListener, OnClickListener, KwHttpRequestListener {

	private Button btnSubmit;
	private TextView tvMsg;
	private EditText edtSMS;
	private String phoneNum;
	private boolean isReset;
	private MyCount mCount;
	private TitleBarView mBarView;

	public static RegisterFragment2 newInstance(String phoneNum, boolean isRestPwd) {
		RegisterFragment2 fragment = new RegisterFragment2();
		Bundle bunle = new Bundle();
		bunle.putString("phoneNum", phoneNum);
		bunle.putBoolean("isReset", isRestPwd);
		fragment.setArguments(bunle);
		return fragment;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_regest_2;
	}

	public RegisterFragment2() {
	}

	@Override
	public void onLaunch(Bundle bundle) {
		super.onLaunch();
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		init();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	private void init() {

		isReset = getArgument().getBoolean("isReset");
		phoneNum = getArgument().getString("phoneNum");

		if (null != mCount) {
			mCount.cancel();
		}
		mCount = new MyCount(60000, 1000);
		mCount.start();

		btnSubmit = (Button) findViewById(R.id.btn_submit);
		btnSubmit.setOnClickListener(this);

		tvMsg = (TextView) findViewById(R.id.tv_msg);

		final String msg = String.format("验证码短信已发送至%s", phoneNum);
		tvMsg.setText(msg);

		edtSMS = (EditText) findViewById(R.id.edt_sms_code);
		edtSMS.setOnKeyListener(this);
		edtSMS.requestFocus();

		UITool.setEditWithClearButton(edtSMS, R.drawable.btn_txt_clear);
		if (isReset) {
			final TextView tv = (TextView) findViewById(R.id.tv_regest_step3);
			tv.setText(getString(R.string.regist_reset_pwd));
			mBarView.setTitle(getString(R.string.regist_getback_pwd));
		} else {
			mBarView.setTitle(getString(R.string.regist1));
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mCount != null) {
			mCount.cancel();
		}
		try {
			UITool.closeInputMethodManager(getContext(), edtSMS.getWindowToken());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private boolean checkEdit(EditText edt) {
		final String str = edt.getText().toString();
		if (TextUtils.isEmpty(str)) {
			UITool.showEditError(edt, "不能为空！");
			return false;
		}
		return true;
	}

	private void toSubmit() {
		if (!checkEdit(edtSMS)) {
			return;
		} else if (edtSMS.getText().toString().length() < 6) {
			UITool.showEditError(edtSMS, "请输入正确的验证码");
			return;
		}
		showProgressDialog(getString(R.string.msg_loading));
		final String code = edtSMS.getText().toString();

		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.CHECK_SMS);
		http.setPostValueForKey("sms_valid_code", code);
		http.setPostValueForKey("user_no", phoneNum);
		http.setListener(1, this);
		http.startAsynchronous();
	}

	public void sendSMS() {
		showProgressDialog(getString(R.string.msg_loading));
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.SEND_SMS);
		http.setPostValueForKey("user_no", phoneNum);
		http.setPostValueForKey("type", String.valueOf(isReset ? 2 : 1));
		http.setListener(2, this);
		http.startAsynchronous();
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.btn_submit:
			toSubmit();
			break;
		case TitleBarView.ID_RIGHT_BUTTON:
			mBarView.setRightButtonEnabled(false);
			sendSMS();
			mCount.start();
			break;
		default:
			break;
		}

	}

	private void onSucces() {
		RegisterFragment3 frag = RegisterFragment3.newInstance(phoneNum, isReset);
		toFragment(frag, false, true);
	}

	@Override
	public void loadFinished(int arg0, byte[] arg1) {
		cancelProgressDialog();
		switch (arg0) {
		case 1:
			parse(arg1);
			break;
		case 2:
			parseSendSMS(arg1);
			break;
		default:
			break;
		}
	}

	private void parseSendSMS(byte[] arg1) {
		try {
			ComveePacket packet = ComveePacket.fromJsonString(arg1);
			if (packet.getResultCode() == 0) {
				// onSucces();

				showToast(packet.getResultMsg());
			} else {
				mBarView.setRightButtonEnabled(true);
				ComveeHttpErrorControl.parseError(getActivity(), packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
	public void loadFailed(int arg0, int arg1) {
		cancelProgressDialog();

		ComveeHttpErrorControl.parseError(getActivity(), arg1);
	}

	@Override
	public boolean onBackPress() {
		mCount.cancel();
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

	/* 定义一个倒计时的内部类 */
	class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			mBarView.setRightButton("重新获取", RegisterFragment2.this);

		}

		@Override
		public void onTick(long millisUntilFinished) {
			mBarView.setRightButton(String.format("%02d秒", millisUntilFinished / 1000), RegisterFragment2.this);
			mBarView.setRightButtonEnabled(false);
		}

	}

}
