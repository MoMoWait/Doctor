package com.comvee.tnb.ui.user;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;

import com.comvee.http.KWHttpRequest.KwHttpRequestListener;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UITool;
import com.comvee.tool.UserMrg;
import com.comvee.util.MD5Util;

public class ChangePwdFragment extends BaseFragment implements OnKeyListener, OnClickListener, KwHttpRequestListener {

    private EditText edtPwdNew, edtPwdOld, edtPwdRepeat;
    private TitleBarView mBarView;

    public ChangePwdFragment() {
    }

    public static ChangePwdFragment newInstance() {
        ChangePwdFragment fragment = new ChangePwdFragment();
        return fragment;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_change_pwd;
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
        mBarView.setTitle(getString(R.string.more_modify_pwd));
        mBarView.setRightButton(getString(R.string.save), this);
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
        try {
            UITool.closeInputMethodManager(getContext(), edtPwdNew.getWindowToken());
            UITool.closeInputMethodManager(getContext(), edtPwdOld.getWindowToken());
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
        // btnSubmit = (Button) findViewById(R.id.btn_submit);
        // btnSubmit.setOnClickListener(this);
        // final View btnOk = findViewById(R.id.btn_ok);
        // btnOk.setOnClickListener(this);
        edtPwdNew = (EditText) findViewById(R.id.edt_pwd_new);
        edtPwdOld = (EditText) findViewById(R.id.edt_pwd_old);
        edtPwdRepeat = (EditText) findViewById(R.id.edt_pwd_repeat);
        edtPwdRepeat.setOnKeyListener(this);

        UITool.setEditWithClearButton(edtPwdNew, R.drawable.btn_txt_clear);
        UITool.setEditWithClearButton(edtPwdOld, R.drawable.btn_txt_clear);
        UITool.setEditWithClearButton(edtPwdRepeat, R.drawable.btn_txt_clear);

        if (ConfigParams.IS_TEST_DATA) {
            edtPwdNew.setEnabled(false);
            edtPwdRepeat.setEnabled(false);
            edtPwdOld.setEnabled(false);
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

    public void toSubmit() {
        if (!checkEdit(edtPwdOld) || !checkEdit(edtPwdNew) || !checkEdit(edtPwdRepeat)) {
            return;
        } else if (!edtPwdRepeat.getText().toString().equals(edtPwdNew.getText().toString())) {
            UITool.showEditError(edtPwdRepeat, "密码不匹配");
            return;
        } else if (edtPwdRepeat.getText().toString().length() > 16 || edtPwdRepeat.getText().toString().length() < 6) {
            UITool.showEditError(edtPwdRepeat, "密码必须在6到16位之间");
            return;
        }
        showProgressDialog(getString(R.string.msg_loading));
        final String pwd1 = MD5Util.getMD5String(edtPwdOld.getText().toString());
        final String pwd2 = MD5Util.getMD5String(edtPwdRepeat.getText().toString());

        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.MODIFY_PWD);
        http.setPostValueForKey("oldPwd", pwd1);
        http.setPostValueForKey("newPwd", pwd2);
        http.setListener(1, this);
        http.startAsynchronous();
    }

    private void toServerMsg() {
        toFragment(RegisterServerFragment.newInstance(), true, true);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.btn_submit:

                if (ConfigParams.IS_TEST_DATA) {
                    showTestDataDialog();
                    return;
                }

                toSubmit();
                break;
            case R.id.tv_to_msg:
                toServerMsg();
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                // showToast("修改密码");

                if (ConfigParams.IS_TEST_DATA) {
                    showTestDataDialog();
                    return;
                }
                toSubmit();
                // getActivity().finish();
                break;
            default:
                break;
        }

    }

    private void showTestDataDialog() {

        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setMessage("您目前是游客，无法进行该操作，建议您注册/登录掌控糖尿病，获得权限。");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toFragment(LoginFragment.newInstance(), true, true);
            }
        });
        builder.create().show();

    }

    private void onSucces() {
        // ((RegisterActivity)
        // getActivity()).toLoginActivity(edtPwdNew.getText().toString(),
        // edtPwdOld.getText()
        // .toString());
        closeInputMethodManager(edtPwdNew.getWindowToken());
        closeInputMethodManager(edtPwdOld.getWindowToken());
        closeInputMethodManager(edtPwdRepeat.getWindowToken());
        UserMrg.setLoginPwd(getApplicationContext(), "");
        UserMrg.setAoutoLogin(getApplicationContext(), false);
        toFragment(LoginFragment.class, null, true);
        // FragmentMrg.toBack(this);
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
                showToast(packet.getResultMsg());
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
