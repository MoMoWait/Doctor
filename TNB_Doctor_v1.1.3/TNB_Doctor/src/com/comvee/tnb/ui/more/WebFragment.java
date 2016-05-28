package com.comvee.tnb.ui.more;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.comvee.tnb.DrawerMrg;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.pay.PayMineOrderFragment;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;

/**
 * 健康评估 报告 页面
 *
 * @author friendlove
 */
public class WebFragment extends BaseFragment implements OnClickListener, OnHttpListener {
    private WebView mWebView;
    private String url;
    private String title;
    private String mTaskid;
    private ProgressBar mBar;
    private boolean isSliding;
    private TitleBarView mBarView;
    private boolean toBack;
    private int fromWhere;

    public static WebFragment newInstance(String title, String url) {
        WebFragment frag = new WebFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("title", title);

        frag.setArguments(bundle);
        return frag;
    }

    public static WebFragment newInstance(String title, String url, int fromWhere) {
        WebFragment frag = new WebFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("title", title);
        bundle.putInt("from_where", fromWhere);
        frag.setArguments(bundle);
        return frag;
    }

    public static WebFragment newInstance(String title, String url, boolean toBack) {
        WebFragment frag = new WebFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("title", title);
        bundle.putBoolean("back", toBack);
        frag.setArguments(bundle);
        return frag;
    }

    public static void toFragment(FragmentActivity act, String title, String url) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("title", title);
        FragmentMrg.toFragment(act, WebFragment.class, bundle, false);
    }

    public void setSliding(boolean b) {
        this.isSliding = b;
    }

    public void setTaskId(String id) {
        mTaskid = id;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_web;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        // TODO Auto-generated method stub
        super.onLaunch();
        if (bundle != null) {
            this.fromWhere = bundle.getInt("from_where");
        }
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        url = bundle.getString("url");
        toBack = bundle.getBoolean("back");
        System.out.println(url);
        title = bundle.getString("title");
        mBarView.setTitle(title);
        if (TextUtils.isEmpty(url)) {
            // showToast("error:url is null");
            showToast(R.string.time_out);
            FragmentMrg.toBack(getActivity());
        } else {
        }
        if (isSliding) {
            mBarView.setLeftButton(R.drawable.top_menu_back, this);
        }

        init();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {

        if (TextUtils.isEmpty(mTaskid)) {
            findViewById(R.id.layout_bottom).setVisibility(View.GONE);
        } else {
            findViewById(R.id.layout_bottom).setVisibility(View.VISIBLE);
            final View btnComplete = findViewById(R.id.btn_complete);
            btnComplete.setVisibility(View.VISIBLE);
            btnComplete.setOnClickListener(this);
        }
        mBar = (ProgressBar) findViewById(R.id.pro_loading);
        mBar.setProgress(0);

        mWebView = (WebView) findViewById(R.id.web);
        mWebView.setVisibility(View.VISIBLE);

        WebSettings setings = mWebView.getSettings();

        setings.setAppCacheEnabled(true);
        setings.setLoadWithOverviewMode(true);
        setings.setUseWideViewPort(true);
        setings.setJavaScriptEnabled(true);
        // setings.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.71 Safari/537.1 LBBROWSER");
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
                return false;
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
            case R.id.btn_complete:

                requestTaskComplete(mTaskid);

                break;
            case TitleBarView.ID_LEFT_BUTTON:
                // ((MainActivity) getActivity()).toggle();
                if (fromWhere == 1) {
                    getActivity().onBackPressed();
                } else {
                    FragmentMrg.toBack(getActivity());
                }
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
        cancelProgressDialog();
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }

    @Override
    public boolean onBackPress() {
        if (fromWhere == 1) {
            IndexFrag.toFragment(getActivity(),true); 
            return true;
        }
        if (toBack) {
            return false;
        }

         if (mWebView!=null && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else {
            return false;
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

        public void isLogin() {
            if (ConfigParams.IS_TEST_DATA) {
                toFragment(LoginFragment.newInstance(), true, true);
            }
        }

        @SuppressWarnings("deprecation")
        public void close(String resulteCode) {
            int result = getInt(resulteCode);
            switch (result) {
                case RESULT_RREFRUSH:
                    // setResult(getActivity().RESULT_OK);
                    PayMineOrderFragment.toFragment(getActivity(), 3, false);
                    break;
                default:
                    FragmentMrg.toBack(getActivity());

                    break;
            }
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

        public void toNextWeb(String title, String url, boolean isDirectBack) {
            WebFragment webFragment = WebFragment.newInstance(title, url, isDirectBack);
            toFragment(webFragment, true);
        }
    }

}
