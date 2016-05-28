package com.comvee.tnb.ui.more;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.ui.book.ShareUtil;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.pay.PayMineOrderFragment;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.Log;
import com.comvee.tool.UserMrg;
import com.tencent.stat.common.User;

import java.lang.reflect.Method;

import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 使用web页面
 *
 * @author friendlove
 */
public class WebNewFrag extends BaseFragment implements OnClickListener {
    private WebView mWebView;
    private String url;
    private String title;
    private ProgressBar mBar;
    private TitleBarView mBarView;
    private int fromWhere;
    private boolean isTobackIndex;

    public static WebNewFrag newInstance(String title, String url) {
        WebNewFrag frag = new WebNewFrag();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("title", title);
        frag.setArguments(bundle);
        return frag;
    }

    public static WebNewFrag newInstance(String title, String url, int fromWhere) {
        WebNewFrag frag = new WebNewFrag();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("title", title);
        bundle.putInt("from_where", fromWhere);
        frag.setArguments(bundle);
        return frag;
    }

    public static void toFragment(FragmentActivity act, String title, String url) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("title", title);
        FragmentMrg.toFragment(act, WebNewFrag.class, bundle, false);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_web;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (bundle != null) {
            this.fromWhere = bundle.getInt("from_where");
        }
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        url = bundle.getString("url");
        title = bundle.getString("title");
        System.out.println(url);

        UIHandler.reset();
        mBarView.setTitle(title);
        if (TextUtils.isEmpty(url)) {
            showToast(R.string.time_out);
            FragmentMrg.toBack(getActivity());
        } else {
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

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void init() {
        findViewById(R.id.layout_bottom).setVisibility(View.GONE);

        mBar = (ProgressBar) findViewById(R.id.pro_loading);
        mBar.setProgress(0);

        mWebView = (WebView) findViewById(R.id.web);
        try {
            ViewCompat.setLayerType(mWebView, View.LAYER_TYPE_HARDWARE, null);
            Method m = Class.forName("android.webkit.CacheManager").getDeclaredMethod("setCacheDisabled‌​", boolean.class);
            m.setAccessible(true);
            m.invoke(null, true);
        } catch (Throwable e) {
        }

        mWebView.setVisibility(View.VISIBLE);
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        WebSettings setings = mWebView.getSettings();
        setings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        setings.setAppCacheEnabled(true);
        setings.setLoadWithOverviewMode(true);
        setings.setUseWideViewPort(true);
        setings.setJavaScriptEnabled(true);
        // setings.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.71 Safari/537.1 LBBROWSER");
        mWebView.addJavascriptInterface(new tnbJsList(), "tnbJsList");
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
                isTobackIndex = url.contains("mainPage");
                return false;
            }
        });
        mWebView.loadUrl(url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

    @Override
    public boolean onBackPress() {
        if (isTobackIndex) {
            FragmentMrg.toBack(getActivity());
            return true;
        }
        if (fromWhere == 1) {
            IndexFrag.toFragment(getActivity(), true);
            return true;
        }
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else {
            FragmentMrg.toBack(getActivity());
            return true;
        }
    }

    private ShareUtil shareUtil;

    private void initShare(String platform) {
        shareUtil = new ShareUtil();
        shareUtil.setActivity(getActivity());
        shareUtil.setShareImgUrl(imgUrl);
        shareUtil.initImagePath();
        shareUtil.setTitle("");
        shareUtil.setUrl("");
        shareUtil.setTitleUrl("");
        shareUtil.setShareText("");
        shareUtil.ShareArticle(platform);

    }

    private String imgUrl;

    class tnbJsList {


        public static final int FROM_GENE = 0;//基因分享，只分享图片
        public static final int FROM_MALL = 1;//商城分享，只分享链接

        public static final int SHARE_IMAGE = 1;//分享图片
        public static final int SHARE_TEXT = 2;//分享纯文字
        public static final int SHARE_WEB = 3;//分享（网页类型：音频、视频......）
        private static final int RESULT_RREFRUSH = 1;

        @JavascriptInterface
        public String getSid() {
            return UserMrg.getSessionId(getApplicationContext());
        }

        @JavascriptInterface
        public String getMemberSid() {
            return UserMrg.getMemberSessionId(getApplicationContext());
        }

        @JavascriptInterface
        public void toShareRedBag(String title, String img, String url, String type) {

            imgUrl = img;

            int iType = Integer.valueOf(type);
            switch (iType) {
                case 0://所有
                    break;
                case 1://微博
                    initShare(SinaWeibo.NAME);
                    break;
                case 2://微信
                    initShare(Wechat.NAME);
                    break;
                case 3://朋友圈
                    initShare(WechatMoments.NAME);
                    break;
                case 4://qq
                    initShare(QQ.NAME);
                    break;
            }


        }

        @JavascriptInterface
        public String getIMEI() {
            TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        }

        @JavascriptInterface
        public String getIMSI() {
            TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSubscriberId();
        }

        // 发短信
        @JavascriptInterface
        public void sendSMS(String phoneNum, String content) {
            Uri uri = Uri.parse(String.format("smsto:%s", phoneNum));
            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
            it.putExtra("sms_body", content);
            startActivity(it);
        }

        // 成功回调
        @JavascriptInterface
        public void callbackSucces(String resultCode, String msg) {

        }

        // 失败回调
        @JavascriptInterface
        public void callbackFail(String resultCode, String msg) {

        }

        // 对话框
        @JavascriptInterface
        public void showAlert(String title, String msg) {
            new AlertDialog.Builder(getActivity()).setTitle(title).setMessage(msg).setPositiveButton("确定", null).create().show();
        }

        // 打电话
        @JavascriptInterface
        public void callPhone(String phoneNum) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
            getActivity().startActivity(intent);
        }

        // 短时提醒
        @JavascriptInterface
        public void showToast(String time, String msg) {
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void isLogin() {
            if (ConfigParams.IS_TEST_DATA) {
                toFragment(LoginFragment.newInstance(), true, true);
            }
        }

        @SuppressWarnings("deprecation")
        @JavascriptInterface
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

        @JavascriptInterface
        private int getInt(String code) {
            int result = -12306;
            try {
                result = Integer.valueOf(code);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;

        }


        /**
         * 基因活动html5跳转到登录页
         */

        @JavascriptInterface
        public void toLogin() {
            FragmentMrg.toFragment(getActivity(), LoginFragment.class, null, true);
        }


        /**
         * 基因活动的分享功能
         *
         * @param share_go_type
         * @param share_img_url
         * @param share_url
         * @param share_type
         * @param share_title
         * @param share_content
         */


        @JavascriptInterface
        public void toShareGene(final String share_go_type, final String share_img_url, final String share_url, final String share_type, final String share_title, final String share_content
        ) {
            share(share_go_type, share_img_url, share_url, share_type, share_title, share_content, FROM_GENE);
        }

        /**
         * 分享方法
         *
         * @param share_go_type
         * @param share_img_url
         * @param share_url
         * @param share_type
         * @param share_title
         * @param share_content
         */
        private void share(final String share_go_type, final String share_img_url, final String share_url, final String share_type, final String share_title, final String share_content, final int fromWhere) {
            imgUrl = share_img_url;

            int iType = Integer.valueOf(share_go_type);
            switch (iType) {
                case 0://所有
                    ThreadHandler.postUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showShare(share_go_type, share_img_url, share_url, share_type, share_title, share_content, fromWhere);
                        }
                    });
                    break;
                case 1://微博
                    initShare(SinaWeibo.NAME);
                    break;
                case 2://微信
                    initShare(Wechat.NAME);
                    break;
                case 3://朋友圈
                    initShare(WechatMoments.NAME);
                    break;
                case 4://qq
                    initShare(QQ.NAME);
                    break;
            }
        }

        /**
         * 商城的分享活动
         */
        @JavascriptInterface
        public void toShareMall(final String share_go_type, final String share_img_url, final String share_url, final String share_type, final String share_title, final String share_content
        ) {
            share(share_go_type, share_img_url, share_url, share_type, share_title, share_content, FROM_MALL);
        }


        /**
         * 基因活动的分享弹出框
         */
        private void showShare(String share_go_type, final String share_img_url, final String share_url, final String share_type, String share_title, final String share_content, int fromWhere) {
            final ShareUtil shareUtil = ShareUtil.getInstance(getActivity());
            shareUtil.setTitle(share_title);
            shareUtil.addOnShareItemClickListence(new ShareUtil.OnShareItemClickListence() {
                @Override
                public void onItemClick(String platform) {
                    switch (Integer.parseInt(share_type)) {
                        case SHARE_IMAGE:
                            shareUtil.setTitle("");
                            shareUtil.setTitleUrl("");
                            shareUtil.setShareImgUrl(share_img_url);
                            shareUtil.setUrl("");
                            break;
                        case SHARE_TEXT:
                            shareUtil.setShareText(share_content);
                            break;
                        case SHARE_WEB:
                            shareUtil.setTitleUrl(share_url);
                            shareUtil.setShareImgUrl(share_img_url);
                            shareUtil.setUrl(share_url);
                            break;

                    }
                    shareUtil.ShareArticle(platform);
                }
            });
            shareUtil.show(mRoot, Gravity.BOTTOM);
        }
    }

}
