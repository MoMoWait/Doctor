package com.comvee.tnb.ui.book;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.comvee.tnb.DrawerMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.widget.TitleBarView;

/**
 * 资讯列表页面 web页面 当点击资讯列表中的某一项时单开一个activity
 *
 * @author Administrator
 */
public class BookIndexRootFragment extends BaseFragment implements OnClickListener {

    private boolean isSliding = true;
    private WebView web;
    private ProgressBar mBar;
    private String url = ConfigUrlMrg.HOST.substring(0, ConfigUrlMrg.HOST.length() - 6) + "messageTitle.html?page=1&type=android";
    private String title;
    private String tempUrl;
    private TitleBarView mBarView;
    private boolean showTitle = true;

    public BookIndexRootFragment() {

    }


    public static BookIndexRootFragment newInstance(boolean Sliding, String urlPath, String title) {
        BookIndexRootFragment bookIndexRootFragment = new BookIndexRootFragment();
        bookIndexRootFragment.url = urlPath;
        bookIndexRootFragment.title = title;
//        showTitle = showTitle_;
        bookIndexRootFragment.setSliding(Sliding);

        return bookIndexRootFragment;
    }

    public static BookIndexRootFragment newInstance(boolean Sliding, String urlPath, String title, boolean showTitle_) {
        BookIndexRootFragment bookIndexRootFragment = newInstance(Sliding, urlPath, title);
        bookIndexRootFragment.showTitle = showTitle_;
        return bookIndexRootFragment;
    }

    private void setSliding(boolean isSliding) {
        this.isSliding = isSliding;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_book_index;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        init();
        if (!showTitle) {
            mBarView.setVisibility(View.GONE);
            return;
        }
        mBarView.setLeftDefault(this);
        if (title != null && !"".equals(title)) {
            mBarView.setTitle(title);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void init() {

        web = (WebView) findViewById(R.id.web);

        choiceTabUI();
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case TitleBarView.ID_LEFT_BUTTON:
                onBackPress();
                break;
        }
    }

    @SuppressLint("JavascriptInterface")
    private void choiceTabUI() {

        mBar = (ProgressBar) findViewById(R.id.pro_loading);
        mBar.setProgress(0);
        web.setVisibility(View.VISIBLE);
        WebSettings setings = web.getSettings();
        setings.setLoadWithOverviewMode(true);
        setings.setUseWideViewPort(true);
        setings.setAppCacheEnabled(true);
        setings.setCacheMode(WebSettings.LOAD_DEFAULT); // 设置 缓存模式
        setings.setJavaScriptEnabled(true);// 设置可使用js
        web.addJavascriptInterface(new JSClient(), "tnbJsList");
        web.setWebChromeClient(new WebChromeClient() {

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
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                web.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                tempUrl = url;
            }
        });
        if (tempUrl != null) {
            web.loadUrl(tempUrl);
        } else {
            web.loadUrl(url);
        }

    }

    @Override
    public boolean onBackPress() {
        if (isSliding) {
            DrawerMrg.getInstance().open();
            return true;
        } else if (web.canGoBack()) {
            web.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public class JSClient {
        @JavascriptInterface
        public void toMessage(String messageId) {
            String url = ConfigUrlMrg.HOST.substring(0, ConfigUrlMrg.HOST.length() - 6) + "message_new.html?id=" + messageId;
            BookWebActivity.toWebActivity(getActivity(), BookWebActivity.MESSAGE, null, null, url, messageId);
        }

        @JavascriptInterface
        public void toMessageNew(String url) {
            BookWebActivity.toWebActivity(getActivity(), BookWebActivity.COOKBOOK, null, null,
                    ConfigUrlMrg.HOST.substring(0, ConfigUrlMrg.HOST.length() - 6) + url, null);
        }
    }

}
