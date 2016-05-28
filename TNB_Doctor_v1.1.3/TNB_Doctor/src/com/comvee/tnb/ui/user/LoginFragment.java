package com.comvee.tnb.ui.user;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.comvee.FinalDb;
import com.comvee.ThreadHandler;
import com.comvee.frame.BaseFragmentActivity;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.model.AlarmInfo;
import com.comvee.tnb.model.RecommondInfo;
import com.comvee.tnb.model.TendencyPointInfo;
import com.comvee.tnb.ui.more.WebFragment;
import com.comvee.tnb.view.QQLoadingView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tnb.widget.TouchedAnimation;
import com.comvee.tool.AppUtil;
import com.comvee.tool.UITool;
import com.comvee.tool.UserMrg;

public class LoginFragment extends BaseFragment implements OnKeyListener, DialogInterface.OnClickListener, OnClickListener {

    public static final String LOGIN_ACTION = TNBApplication.getInstance().getPackageName() + ".login";
    private Button btnLogin;
    private EditText edtUserName;
    private EditText edtPwd;
    private String strUserName;
    private String strPwd;
    private boolean isExit;

    private TitleBarView mBarView;
    private boolean isShowProgress = false;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    cancelProgressDialog();
                    isShowProgress = false;
                    break;

                default:
                    break;
            }
        }
    };
    private View btnRegist;

    public LoginFragment() {
    }

    public static void toFragment(FragmentActivity frg, boolean isExcept) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isExcept", isExcept);
        FragmentMrg.toFragment(frg, LoginFragment.class, bundle, true);

    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    public static LoginFragment newInstance(boolean isExcept) {
        LoginFragment fragment = new LoginFragment();
        fragment.setExit(isExcept);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    private void setExit(boolean isExit) {
        this.isExit = isExit;
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);
        if (dataBundle != null) {
            setExit(dataBundle.getBoolean("isExcept"));
        }
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_login_1;
    }

    @Override
    public void onStart() {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        super.onStart();
        if (isExit) {
            mBarView.hideLeftButton();
        }

        init();
        mBarView.setTitle("掌控糖尿病");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void setValue(String phone, String pwd) {
        edtUserName.setText(phone);
        edtPwd.setText(pwd);
    }

    private void init() {

        mRoot.setEnabled(false);
        findViewById(R.id.btn_bind_qq).setOnClickListener(this);
        findViewById(R.id.btn_bind_qq).setVisibility(View.VISIBLE);

        final TextView btnResetPwd = (TextView) findViewById(R.id.btn_reset_pwd);
        btnResetPwd.setText(Html.fromHtml("找回密码"));
        btnResetPwd.setOnClickListener(this);

        edtUserName = (EditText) findViewById(R.id.edt_username);
        edtPwd = (EditText) findViewById(R.id.edt_pwd);
        findViewById(R.id.login_relative).setOnClickListener(this);

        findViewById(R.id.lin_head).setOnClickListener(this);
        UITool.setEditWithClearButton(edtUserName, R.drawable.btn_txt_clear);
        UITool.setEditWithClearButton(edtPwd, R.drawable.btn_txt_clear);
        UITool.openInputMethodManager(getApplicationContext(), edtUserName);
        btnRegist = findViewById(R.id.btn_regist);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnTouchListener(TouchedAnimation.TouchLight);
        btnRegist.setOnTouchListener(TouchedAnimation.TouchLight);
        btnRegist.setVisibility(View.VISIBLE);
        btnLogin.setOnClickListener(this);
        btnRegist.setOnClickListener(this);
        edtPwd.setOnKeyListener(this);
        if (!TextUtils.isEmpty(strPwd) && !TextUtils.isEmpty(strUserName)) {
            edtUserName.setText(strUserName);
            edtPwd.setText(strPwd);
        } else {
            edtUserName.setText(UserMrg.getLoginName(getApplicationContext()));
            edtPwd.setText(UserMrg.getLoginPwd(getApplicationContext()));
        }

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private boolean checkEdit(EditText edt) {
        final String str = edt.getText().toString();
        if (TextUtils.isEmpty(str)) {
            showToast("密码不能为空！");
            return false;
        }
        return true;
    }

    private void requestLogin(final String strPhone, final String strPwd) {
        if (TextUtils.isEmpty(strPhone) || TextUtils.isEmpty(strPwd)) {
            return;
        }
        showProgressDialog("正在登录...");
        isShowProgress = true;
        this.strUserName = strPhone;
        this.strPwd = strPwd;

        UserMrg.login(getActivity(), strPhone, strPwd);

    }

    private void toRegisterActivity() {
        toFragment(RegisterFragment1.newInstance(null, false), true, false);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.lin_head:
            case R.id.login_relative:
                UITool.closeInputMethodManager(getActivity());
                break;
            case R.id.btn_statement:
                toStatement();
                break;
            case R.id.btn_bind_qq:
                if (AppUtil.getAPNType(getApplicationContext()) == -1) {
                    ComveeHttpErrorControl.parseError(getActivity(), ComveeHttp.ERRCODE_NETWORK);
                    return;
                }
                QQLoadingView view = new QQLoadingView(getApplicationContext());
                view.show(v);
                ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isVisible()) {
                            UserMrg.loginQQ(getActivity());
                        }
                    }
                }, 1000);
                break;
            case R.id.btn_login:
                if (!checkEdit(edtUserName) || !checkEdit(edtPwd)) {
                    return;
                }
                final String strPhone = edtUserName.getText().toString();
                final String strPwd = edtPwd.getText().toString();
                if (strPhone.equals(ConfigUrlMrg.username) && strPwd.equals(ConfigUrlMrg.userpaw)) {
                    showChang();
                } else {
                    requestLogin(strPhone, strPwd);
                }
                break;
            case R.id.btn_regist:
                toRegisterActivity();
                break;
            case R.id.btn_reset_pwd:
                toResetPwd();
                break;
            default:
                break;
        }

    }

    private void showChang() {
        ComveeHttp.clearAllCache(getApplicationContext());
        UserMrg.clear(getApplicationContext());
        FinalDb db = FinalDb.create(getApplicationContext(), ConfigParams.DB_NAME);
        db.deleteByWhere(AlarmInfo.class, "");
        db.deleteByWhere(RecommondInfo.class, "");
        db.deleteByWhere(TendencyPointInfo.class, "");
    }

    public void toResetPwd() {
        toFragment(RegisterFragment1.newInstance(edtUserName.getText().toString(), true), true, false);
    }


    private void closess() {
        if (edtPwd.getWindowToken() != null) {
            UITool.closeInputMethodManager(getApplicationContext(), edtPwd.getWindowToken());
            UITool.closeInputMethodManager(getApplicationContext(), edtPwd.getWindowToken());
        }
    }

    private void isShowView(String arg1) {
        try {

            ComveePacket packet = ComveePacket.fromJsonString(arg1);
            if (packet.getResultCode() != 0) {

                showToast(packet.getResultMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (!isProgressDialogShowing()) {
                if (!checkEdit(edtUserName) || !checkEdit(edtPwd)) {
                    return false;
                }
                final String strPhone = edtUserName.getText().toString();
                final String strPwd = edtPwd.getText().toString();
                requestLogin(strPhone, strPwd);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onBackPress() {

        try {
            closeInputMethodManager(edtUserName.getWindowToken());
            closeInputMethodManager(edtPwd.getWindowToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isExit) {
            ((BaseFragmentActivity) getActivity()).tryExit();
            return true;
        } else {
            FragmentMrg.toBack(getActivity());
            return true;
        }
    }

    @Override
    public void onClick(DialogInterface arg0, int arg1) {
    }

    /**
     * 免责申明
     */
    private void toStatement() {
        String url = ConfigParams.getConfig(getApplicationContext(), ConfigParams.TEXT_STATEMENT);
        WebFragment frag = WebFragment.newInstance("声明", url);
        toFragment(frag, true, true);
    }

}
