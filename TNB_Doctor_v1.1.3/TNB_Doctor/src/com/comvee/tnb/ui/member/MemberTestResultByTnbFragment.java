package com.comvee.tnb.ui.member;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.comvee.tnb.DrawerMrg;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.MemberInfo;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;

/**
 * 测试结果 是糖尿病
 *
 * @author friendlove
 */
@SuppressLint("ValidFragment")
public class MemberTestResultByTnbFragment extends BaseFragment implements OnClickListener, OnHttpListener {
    private MemberInfo mInfo;

    private WebView mWebView;
    private String url;
    private ProgressBar mBar;
    private boolean isSliding;
    private boolean isLuancher;
    private TitleBarView mBarView;

    public MemberTestResultByTnbFragment() {
    }

    @SuppressLint("ValidFragment")
    private MemberTestResultByTnbFragment(MemberInfo mInfo, boolean isLuancher) {
        this.mInfo = mInfo;
        this.isLuancher = isLuancher;
    }

    public static MemberTestResultByTnbFragment newInstance(MemberInfo mInfo, boolean isLuancher) {
        MemberTestResultByTnbFragment frag = new MemberTestResultByTnbFragment(mInfo, isLuancher);
        return frag;
    }

    public void setSliding(boolean b) {
        this.isSliding = b;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_web;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        try {
            url = mInfo.diabetes_plan;
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBarView.setVisibility(View.GONE);

        if (isSliding) {
        }
        mBarView.setTitle("了解计划内容");
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {

        final Button btnComplete = (Button) findViewById(R.id.btn_complete);
        final View btnPre = findViewById(R.id.btn_pre);
        btnPre.setOnClickListener(this);
        btnPre.setVisibility(View.VISIBLE);
        // btnComplete.setText("开始设置目标");
        btnComplete.setText("立即开启糖控健康之旅！");
        btnComplete.setVisibility(View.VISIBLE);
        btnComplete.setOnClickListener(this);

        if (!isLuancher) {
            findViewById(R.id.layout_bottom).setVisibility(View.GONE);
        } else {
            findViewById(R.id.layout_bottom).setVisibility(View.VISIBLE);
        }

        mBar = (ProgressBar) findViewById(R.id.pro_loading);
        mBar.setProgress(0);

        mWebView = (WebView) findViewById(R.id.web);
        mWebView.setVisibility(View.VISIBLE);

        WebSettings setings = mWebView.getSettings();
        setings.setAppCacheEnabled(true);

        setings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JSClient(), "js");
        mWebView.setWebChromeClient(new WebChromeClient() {

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
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mWebView.loadUrl(url);
                return true;
            }
        });
        mWebView.loadUrl(url);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return getActivity().onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pre:
                FragmentMrg.toBack(getActivity());
                break;
            case R.id.btn_complete:
                IndexFrag.toFragment(getActivity(),true); 
//			((MainActivity) getActivity()).toIndexFragment();
                // toFragment(MemberTargetScoreFragment.newInstance(mInfo, true),
                // true, true);
                break;
            case TitleBarView.ID_LEFT_BUTTON:
//			((MainActivity) getActivity()).toggle();
                break;
            default:
                break;
        }
    }

    private void requestTaskComplete(String id) {
        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.TASK_COMPLETE);
        http.setPostValueForKey("memberJobDetailId", id);
        http.setOnHttpListener(1, this);
        http.startAsynchronous();
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        try {
            cancelProgressDialog();
            ComveePacket packet = ComveePacket.fromJsonString(b);
            if (packet.getResultCode() == 0) {
                ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TASK_MINE));
                ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.INDEX));
                FragmentMrg.toBack(getActivity());
            } else {
                ComveeHttpErrorControl.parseError(getActivity(), packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFialed(int what, int errorCode) {
        // TODO Auto-generated method stub
        cancelProgressDialog();
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }

    @Override
    public boolean onBackPress() {
        if (isSliding) {
            DrawerMrg.getInstance().open();
            return true;
        } else {
            FragmentMrg.toBack(getActivity());
            return true;
        }
    }

    class JSClient {

        private static final int RESULT_RREFRUSH = 1;

        public String getIMEI() {
            TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
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
            getActivity().startActivity(intent);
        }

        // 短时提醒
        public void showToast(String time, String msg) {
            int result = getInt(time);
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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
