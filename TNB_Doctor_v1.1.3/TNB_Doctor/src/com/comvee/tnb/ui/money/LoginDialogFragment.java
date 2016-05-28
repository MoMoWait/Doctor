package com.comvee.tnb.ui.money;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.comvee.BaseApplication;
import com.comvee.FinalDb;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.model.AlarmInfo;
import com.comvee.tnb.model.RecommondInfo;
import com.comvee.tnb.model.TendencyPointInfo;
import com.comvee.tool.UserMrg;
import com.comvee.util.UITool;

/**
 * 用户密码验证窗口
 * 
 * @author yujun
 * 
 */
public class LoginDialogFragment extends DialogFragment implements OnClickListener {
	private View view;
	private Button mDeselect;
	private EditText mEdtPwd;
	private Button mEnsure;
	protected Context mContext;

	private String strPwd;
	protected Context getApplicationContext() {
		return mContext;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		Dialog dialog = getDialog();
//		UITool.setImmersive(dialog.getWindow(), true);
//		UITool.setMiuiStatusBarDarkMode(dialog.getWindow(), true);
//		UITool.setMeizuStatusBarDarkIcon(dialog.getWindow(), true);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));// 设置背景透明
		view = inflater.inflate(R.layout.login_dialog_fragment, container);
		init();
		return view;
	}

	private void init() {
		mEdtPwd = (EditText) view.findViewById(R.id.id_txt_password);
		mEdtPwd.requestFocus();
		mDeselect = (Button) view.findViewById(R.id.btn_deselect);
		mDeselect.setOnClickListener(this);
		mEnsure = (Button) view.findViewById(R.id.btn_ensure);
		mEnsure.setOnClickListener(this);
	}
	private boolean checkEdit(EditText edt) {
		final String str = edt.getText().toString();
		if (TextUtils.isEmpty(str)) {
			Toast.makeText(getContext(), "密码不能为空", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_deselect:
			dismiss();
			break;
		case R.id.btn_ensure:
			strPwd = mEdtPwd.getText().toString();
			if (!checkEdit(mEdtPwd)) {
				return;
			}
			if (strPwd.equals(ConfigUrlMrg.userpaw)) {
			} else {
				checkPwd(strPwd);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 验证密码
	 * @param strPwd
     */
	private void checkPwd(String strPwd) {
		if (TextUtils.isEmpty(strPwd)) {
			return;
		}
		this.strPwd = strPwd;
		Context cxt = BaseApplication.getInstance();
		if (UserMrg.isQQAutoLogin(cxt)) {
			UserMrg.loginQQ(getActivity());
		} else {
			String localPwd = UserMrg.getLoginPwd(cxt);
			if (!TextUtils.isEmpty(localPwd) && localPwd.equals(strPwd)) {
				Bundle bundle = getArguments();
				FragmentMrg.toFragment(getActivity(), WithdeawDepositFragment.class, bundle, true);
				dismiss();
			} else {
				Toast.makeText(getContext(), "密码错误，请从新输入！", Toast.LENGTH_LONG).show();
			}
		}

	}
}
