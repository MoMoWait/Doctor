package com.comvee.tnb.ui.user;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.http.KWHttpRequest.KwHttpRequestListener;
import com.comvee.tnb.DrawerMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.db.DBManager;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.index.IndexModel;
import com.comvee.tnb.ui.more.WebFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UITool;
import com.comvee.tool.UserMrg;
import com.comvee.ui.remind.TimeRemindUtil;
import com.comvee.util.CacheUtil;
import com.comvee.util.MD5Util;

public class RegisterFragment3 extends BaseFragment implements OnKeyListener, OnClickListener, KwHttpRequestListener {

	private EditText edtName, edtPwd, edtPwdRepeat;
	private String strPwd, strRepeatPwd;
	private String phoneNum;
	private boolean isReset;
	private TitleBarView mBarView;

	public static RegisterFragment3 newInstance(String phoneNum, boolean isRestPwd) {
		RegisterFragment3 fragment = new RegisterFragment3();
		Bundle bunle = new Bundle();
		bunle.putString("phoneNum", phoneNum);
		bunle.putBoolean("isReset", isRestPwd);
		fragment.setArguments(bunle);
		return fragment;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_regest_3;
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	public RegisterFragment3() {
	}

	@Override
	public void onLaunch(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onLaunch();
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		init();
		if (isReset) {
			final TextView tv = (TextView) findViewById(R.id.tv_regest_step3);
			tv.setText("重置密码");
			mBarView.setTitle(getString(R.string.regist_getback_pwd));
		} else {
			mBarView.setTitle(getString(R.string.regist1));
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		try {
			UITool.closeInputMethodManager(getContext(), edtName.getWindowToken());
			UITool.closeInputMethodManager(getContext(), edtPwd.getWindowToken());
			UITool.closeInputMethodManager(getContext(), edtPwdRepeat.getWindowToken());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	private void init() {
		final View btnOk = findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(this);
		isReset = getArgument().getBoolean("isReset");
		phoneNum = getArgument().getString("phoneNum");
		// btnSubmit = (Button) findViewById(R.id.btn_submit);
		// btnSubmit.setOnClickListener(this);

		edtName = (EditText) findViewById(R.id.edt_username);
		edtPwd = (EditText) findViewById(R.id.edt_pwd);
		edtPwdRepeat = (EditText) findViewById(R.id.edt_pwd_repeat);

		edtPwd.setText(strPwd);
		edtPwdRepeat.setText(strRepeatPwd);
		edtPwdRepeat.setOnKeyListener(this);

		UITool.setEditWithClearButton(edtPwd, R.drawable.btn_txt_clear);
		UITool.setEditWithClearButton(edtPwdRepeat, R.drawable.btn_txt_clear);

		final View btnServerMsg = findViewById(R.id.tv_to_msg);
		btnServerMsg.setOnClickListener(this);
		edtPwd.requestFocus();

		if (isReset) {
			final TextView tv = (TextView) findViewById(R.id.tv_regest_step3);
			tv.setText("重置密码");
			mBarView.setTitle(getString(R.string.regist_forget_pwd));
		} else {
			mBarView.setTitle(getString(R.string.regist1));
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

	public void toResetPwd() {
		final String pwd2 = MD5Util.getMD5String(edtPwdRepeat.getText().toString());

		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.RESET_PWD);
		http.setPostValueForKey("newPwd", pwd2);
		http.setPostValueForKey("user_type", "2");
		http.setPostValueForKey("mobile", phoneNum);
		http.setListener(1, this);
		http.startAsynchronous();
	}

	public void toSubmit() {
		if (!checkEdit(edtPwd) || !checkEdit(edtPwdRepeat)) {
			return;
		} else if (!edtPwd.getText().toString().equals(edtPwdRepeat.getText().toString())) {
			UITool.showEditError(edtPwdRepeat, "密码不匹配");
			return;
		} else if (edtPwdRepeat.getText().toString().length() > 16 || edtPwdRepeat.getText().toString().length() < 6) {
			UITool.showEditError(edtPwdRepeat, "密码必须在6到16位之间");
			return;
		}
		showProgressDialog(getString(R.string.msg_loading));

		if (isReset) {
			toResetPwd();
		} else {
			toRegist();
		}

	}

	private void toRegist() {
		final String pwd1 = MD5Util.getMD5String(edtPwd.getText().toString());
		final String pwd2 = MD5Util.getMD5String(edtPwdRepeat.getText().toString());

		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.REGIST);
		http.setPostValueForKey("user_pwd", pwd1);
		http.setPostValueForKey("user_pwd2", pwd2);
		http.setPostValueForKey("user_type", "2");
		http.setPostValueForKey("user_no", phoneNum);
		if (UserMrg.getTestData(getApplicationContext()) && ConfigParams.getFirstRegist(getApplicationContext())) {
			http.setPostValueForKey("isGuest", "1");
		} else {
			http.setPostValueForKey("isGuest", "0");
			// http.setPostValueForKey("isGuest",
			// UserMrg.getTestData(getApplicationContext())?"1":"0");
		}
		http.setListener(1, this);
		http.startAsynchronous();
	}

	private void toServerMsg() {
		strPwd = edtPwd.getText().toString();
		strRepeatPwd = edtPwdRepeat.getText().toString();

		UITool.closeInputMethodManager(getApplicationContext(), edtName.getWindowToken());
		UITool.closeInputMethodManager(getApplicationContext(), edtPwd.getWindowToken());
		UITool.closeInputMethodManager(getApplicationContext(), edtPwdRepeat.getWindowToken());

		String url = ConfigParams.getConfig(getApplicationContext(), ConfigParams.TEXT_STATEMENT);
		WebFragment frag = WebFragment.newInstance("服务协议", url);
		toFragment(frag, true, true);
		// toFragment(RegisterServerFragment.newInstance(), true, true);
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.btn_submit:
			toSubmit();
			break;
		case R.id.tv_to_msg:
			toServerMsg();
			break;
		case R.id.btn_ok:
		case TitleBarView.ID_RIGHT_BUTTON:
			toSubmit();
			break;
		default:
			break;
		}

	}

	private void onSuccesRegist(boolean isVisitor) {
		ThreadHandler.postWorkThread(new Runnable() {
			@Override
			public void run() {

					ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.INDEX));
					ComveeHttp.clearAllCache(getApplicationContext());
					// 清理数据库，，，，临时这样做
					DBManager.cleanDatabases(getApplicationContext());
					TimeRemindUtil.getInstance(getApplicationContext()).stopRemind();
					CacheUtil.getInstance().clear();

			}
		});
		FragmentMrg.removeAllFragment(getActivity());
		FragmentMrg.removeAllSigleMap();

		// ===========自动登入============//
		UserMrg.setLoginName(getApplicationContext(), phoneNum);
		UserMrg.setLoginPwd(getApplicationContext(), edtPwd.getText().toString());
		UserMrg.setAoutoLogin(getApplicationContext(), true);
		// ============================//

		// ==========关闭键盘===========//
		UITool.closeInputMethodManager(getApplicationContext(), edtName.getWindowToken());
		UITool.closeInputMethodManager(getApplicationContext(), edtPwd.getWindowToken());
		UITool.closeInputMethodManager(getApplicationContext(), edtPwdRepeat.getWindowToken());

		if (isReset) {
			LoginFragment.toFragment(getActivity(), true);
		} else {
			if(isVisitor){
				FragmentMrg.removeAllFragment(getActivity());
				FragmentMrg.removeAllSigleMap();
				showProgressDialog("自动登录...");
				UserMrg.autoLogin(getActivity());
			}else{
				toFragment(RegistSuccesFrag.newInstance(), false);
			}
		}
		// toFragment(LoginFragment.newInstance(), false, true);
	}

	@Override
	public void loadFinished(int arg0, byte[] arg1) {
		cancelProgressDialog();
		switch (arg0) {
		case 1:
			parseRegist(arg1);
			break;
		default:
			break;
		}
	}

	private void parseRegist(byte[] arg1) {
		try {
			boolean isVisitor = UserMrg.getTestData(getContext());
			UserMrg.setTestData(getApplicationContext(), false);
			ComveePacket packet = ComveePacket.fromJsonString(arg1);
			if (packet.getResultCode() == 0) {
				ConfigParams.setFirstRegist(getApplicationContext(), false);
				String memberSessionId = packet.optString("sessionMemberID");
				UserMrg.setMemberSessionId(getContext(), memberSessionId);
				UserMrg.setSessionId(getContext(), packet.optString("sessionID"));
				onSuccesRegist(isVisitor);
				showToast(packet.getResultMsg());
			} else {
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
}
