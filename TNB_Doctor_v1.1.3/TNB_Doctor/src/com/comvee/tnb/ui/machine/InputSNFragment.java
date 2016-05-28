package com.comvee.tnb.ui.machine;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.comvee.http.KWHttpRequest.KwHttpRequestListener;
import com.comvee.tnb.DrawerMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.ui.privatedoctor.DoctorServerList;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UITool;
import com.comvee.tool.UserMrg;
import com.comvee.util.MD5Util;

/**
 * 此页面 输入设备的SN
 *
 * @author friendlove
 */
public class InputSNFragment extends BaseFragment implements OnClickListener, OnHttpListener, KwHttpRequestListener {

    private EditText edtSN;
    private int FromWhere;// 从那个入口进入 1设备绑定 2医生套餐
    private TitleBarView mBarView;

    public InputSNFragment(int fromwhere) {
        FromWhere = fromwhere;
    }

    public InputSNFragment() {
    }

    public static InputSNFragment newInstance(int fromwhere) {
        InputSNFragment fragment = new InputSNFragment(fromwhere);
        return fragment;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_input_sn;
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void init() {
        // btnSubmit = (Button) findViewById(R.id.btn_submit);
        // btnSubmit.setOnClickListener(this);
        final View btnOk = findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(this);
        edtSN = (EditText) findViewById(R.id.edt_sn);
        if (FromWhere == 1) {
            mBarView.setTitle("输入设备的SN码");
        }
        if (FromWhere == 2) {
            mBarView.setTitle("输入医生编码");
            edtSN.setHint("请输入医生编码");
        }
        UITool.setEditWithClearButton(edtSN, R.drawable.btn_txt_clear);

        if (ConfigParams.IS_TEST_DATA) {
            edtSN.setEnabled(false);
        }

    }

    private boolean checkEdit(EditText edt) {
        final String str = edt.getText().toString();
        if (TextUtils.isEmpty(str)) {
            UITool.showEditError(edt, "不能为空！");
            return false;
        } else if (!isNumber(str)) {
            UITool.showEditError(edt, "请输入正确的格式");
            return false;
        } else if (FromWhere == 1 && str.length() != 10) {
            UITool.showEditError(edt, "设备SN码有十位数哦");
            return false;
        }
        return true;
    }

    public boolean isNumber(String str) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[0-9]*");
        java.util.regex.Matcher match = pattern.matcher(str);
        if (match.matches() == false) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.btn_ok:
                if (FromWhere == 1) {
                    if (!checkEdit(edtSN)) {
                        return;
                    }
                    requestAddMachine(edtSN.getText().toString());
                }
                if (FromWhere == 2) {
                    if (!checkEdit(edtSN)) {
                        return;
                    }
                    DoctorServerList.toFragment(getActivity(), edtSN.getText().toString());
                }
                break;

            default:
                break;
        }

    }

    private void onSucces() {
        closeInputMethodManager(edtSN.getWindowToken());
        UserMrg.setLoginPwd(getApplicationContext(), "");
        UserMrg.setAoutoLogin(getApplicationContext(), false);
        toFragment(LoginFragment.class, null, true);
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
                // onSucces();
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

    // 绑定设备
    private void requestAddMachine(String code) {
        if (!checkEdit(edtSN)) {
            return;
        }
        code += "@02";
        if (TextUtils.isEmpty(code) || !code.contains("@")) {
            showToast("扫描的二维码并不是我们的设备");
            return;
        }
        showProgressDialog("正在绑定...");
        String machineID = MD5Util.getMD5String(code.split("@")[0]);
        String machineType = code.split("@")[1];
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.MACHINE_ADD);
        http.setPostValueForKey("machineId", machineID);
        http.setPostValueForKey("machineType", machineType);
        http.setOnHttpListener(1, this);
        http.startAsynchronous();
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();
        try {
            ComveePacket packet = ComveePacket.fromJsonString(b);
            if (packet.getResultCode() == 0) {

                UserMrg.DEFAULT_MEMBER.hasMachine = 1;
                MachineListFragment.toFragment(getActivity(), false);
                showToast("绑定成功");
                DrawerMrg.getInstance().updateLefFtagment();

            } else {
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
}
