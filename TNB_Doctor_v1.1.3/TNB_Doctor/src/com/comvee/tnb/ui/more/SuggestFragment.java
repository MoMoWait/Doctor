package com.comvee.tnb.ui.more;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UITool;

public class SuggestFragment extends BaseFragment implements OnKeyListener, OnClickListener, OnHttpListener {

    private EditText edtMail;
    private EditText edtSuggest;
    private TitleBarView mBarView;

    public SuggestFragment() {
    }

    public static SuggestFragment newInstance() {
        SuggestFragment fragment = new SuggestFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_suggest;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        mBarView.setTitle("意见反馈");
        init();
    }

    private void init() {
        mBarView.setRightButton(R.drawable.right_logo, this);
        edtMail = (EditText) findViewById(R.id.edt_mail);
        edtSuggest = (EditText) findViewById(R.id.edt_suggest);
        edtMail.setOnKeyListener(this);
    }

    private void showTestDataDialog() {

        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setMessage("您目前是游客，无法进行该操作，建议您立刻注册加入掌控糖尿病，获得权限。注册后范例数据将初始化，您修改的所有数据都将被还原！");
        builder.setNegativeButton("稍后注册", null);
        builder.setPositiveButton("立即注册", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toFragment(LoginFragment.newInstance(), true, true);
            }
        });
        builder.create().show();

    }

    private void toSubmit() {

        if (ConfigParams.IS_TEST_DATA) {
            showTestDataDialog();
            return;
        }

        final String email = edtMail.getText().toString();
        final String suggest = edtSuggest.getText().toString();

        // if (TextUtils.isEmpty(email)) {
        // UITool.showEditError(edtMail, "不能为空");
        // return;
        // }
        if (TextUtils.isEmpty(suggest)) {
            UITool.showEditError(edtSuggest, "不能为空");
            return;
        }

        if (!TextUtils.isEmpty(email)) {
            UITool.showEditError(edtMail, "必须填写电话号码或者邮箱地址！");
            return;
        }

        if (suggest.length() > 500 || suggest.length() < 5) {
            UITool.showEditError(edtSuggest, "只能输入5~500个字符！");
            return;
        }

        showProgressDialog("提交中...");

        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.SUGGEST);
        // http.setPostValueForKey("userTell", "");
        http.setPostValueForKey("userMail", email);
        http.setPostValueForKey("suggestInfo", suggest);
        // http.setPostValueForKey("userTell", "");
        http.setOnHttpListener(0, this);
        http.startAsynchronous();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TitleBarView.ID_RIGHT_BUTTON:
                toSubmit();
                break;
            case TitleBarView.ID_LEFT_BUTTON:
//			((MainActivity) getActivity()).toggle();
                break;
            default:
                break;
        }

    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();
        try {

            ComveePacket packet = ComveePacket.fromJsonString(b);

            if (packet.getResultCode() == 0) {
                showToast("提交成功");
                closeInputMethodManager(edtMail.getWindowToken());
                closeInputMethodManager(edtSuggest.getWindowToken());
                FragmentMrg.toBack(getActivity());
            } else {
                // showToast(packet.getResultMsg());
                ComveeHttpErrorControl.parseError(getActivity(), packet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();

        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
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
