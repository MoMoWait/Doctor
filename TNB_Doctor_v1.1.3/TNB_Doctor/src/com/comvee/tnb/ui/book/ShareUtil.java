package com.comvee.tnb.ui.book;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.comvee.ThreadHandler;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.network.ShareCompleteLoader;
import com.comvee.tnb.view.ShareBounceAnimation;
import com.comvee.tool.UITool;

public class ShareUtil implements OnClickListener, PlatformActionListener {
    private PopupWindow popupWindow;
    private static ShareUtil shareUtil;
    private Activity activity;
    private OnShareItemClickListence listence;
    /**
     * 分享标题
     */
    private String title;
    /**
     * 分享内容摘要
     */
    private String shareText;
    /**
     * 链接地址
     */
    private String url;
    /**
     * 标题点击跳转地址
     */
    private String titleUrl;
    private String site = "掌控糖尿病";
    private String siteUrl = "http://www.izhangkong.com";
    private String TEST_IMAGE;
    /**
     * 分享出去的图片网络地址
     */
    private String shareImgUrl;
    private String contentId;
    private Button mCancel;
    private boolean isAnimation =false;
    private ViewGroup mItemViews;

    public static ShareUtil getInstance(Activity activity) {
        shareUtil = new ShareUtil();
        shareUtil.setActivity(activity);
        shareUtil.initImagePath();
        shareUtil.initShareView();
        return shareUtil;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public void addOnShareItemClickListence(OnShareItemClickListence clickListence) {
        this.listence = clickListence;
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    public void setPopupWindow(PopupWindow popupWindow) {
        this.popupWindow = popupWindow;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShareText() {
        return shareText;
    }

    public void setShareText(String shareText) {
        this.shareText = shareText;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitleUrl() {
        return titleUrl;
    }

    /**
     * qq分享必传参数，否则无法分享
     *
     * @param titleUrl
     */
    public void setTitleUrl(String titleUrl) {
        this.titleUrl = titleUrl;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getShareImgUrl() {
        return shareImgUrl;
    }

    public void setShareImgUrl(String shareImgUrl) {
        this.shareImgUrl = shareImgUrl;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private TextView wechat_frined,wechat ,qzone,sina_weibo;
     private View layout;

    /**
     * 显示“分享”菜单
     *
     */
    private PopupWindow initShareView() {
        View v = View.inflate(activity, R.layout.share_fragment, null);
        mItemViews = (ViewGroup)v.findViewById(R.id.pop_lin);

        sina_weibo = (TextView) v.findViewById(R.id.sinaweibo);
        wechat = (TextView) v.findViewById(R.id.wechat);

        wechat_frined = (TextView) v.findViewById(R.id.wechitfriend);
        qzone = (TextView) v.findViewById(R.id.qzone);
        layout = v.findViewById(R.id.pop_lin);
        mCancel = (Button) v.findViewById(R.id.cancel);
        mCancel.setOnClickListener(this);
        ImageView img = (ImageView) v.findViewById(R.id.popu_img);
        sina_weibo.setOnClickListener(this);
        wechat.setOnClickListener(this);
        wechat_frined.setOnClickListener(this);
        qzone.setOnClickListener(this);
        img.setOnClickListener(this);
        popupWindow = new PopupWindow(v, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

     //   popupWindow.setAnimationStyle(R.anim.fade_in);

       // inAnimation();
        showAnimation(mItemViews);
        //popupWindow.setAnimationStyle(R.style.PopupAnimation);

        return popupWindow;
    }

    @Override
    public void onClick(View arg0) {
       if (popupWindow.isShowing()&&isAnimation) {
           isAnimation=false;
            outAnimation();
        }
        switch (arg0.getId()) {
            case R.id.sinaweibo:
                if (listence != null) {
                    listence.onItemClick(SinaWeibo.NAME);
                } else {
                    ShareArticle(SinaWeibo.NAME);
                }
                popupWindow.dismiss();

                break;
            case R.id.wechat:
                if (listence != null) {
                    listence.onItemClick(Wechat.NAME);
                } else {
                    ShareArticle(Wechat.NAME);
                }
                popupWindow.dismiss();
                break;
            case R.id.wechitfriend:
                if (listence != null) {
                    listence.onItemClick(WechatMoments.NAME);
                } else {
                    ShareArticle(WechatMoments.NAME);
                }
                popupWindow.dismiss();
                break;
            case R.id.qzone:

                if (listence != null) {
                    listence.onItemClick(QQ.NAME);
                } else {
                    ShareArticle(QQ.NAME);
                }
                popupWindow.dismiss();
                break;
            case R.id.cancel:
                //outAnimation();

                break;
        }
    }
    //分享消失动画
    private void outAnimation() {
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                TranslateAnimation translateAnimation = new TranslateAnimation(0f, 0f, 0f, 30f);
                AlphaAnimation animation = new AlphaAnimation(1, 0);
                AnimationSet set = new AnimationSet(true);
                set.addAnimation(translateAnimation);
                set.addAnimation(animation);
                set.setDuration(200);
                qzone.startAnimation(set);
                qzone.setVisibility(View.INVISIBLE);
            }
        }, 100);
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                TranslateAnimation translateAnimation = new TranslateAnimation(0f, 0f, 0f, 30f);
                AlphaAnimation animation = new AlphaAnimation(1, 0);
                AnimationSet set = new AnimationSet(true);
                set.addAnimation(translateAnimation);
                set.addAnimation(animation);
                set.setDuration(200);
                wechat_frined.startAnimation(set);
                wechat_frined.setVisibility(View.INVISIBLE);
            }
        }, 300);
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                TranslateAnimation translateAnimation = new TranslateAnimation(0f, 0f, 0f, 30f);
                AlphaAnimation animation = new AlphaAnimation(1, 0);
                AnimationSet set = new AnimationSet(true);
                set.addAnimation(translateAnimation);
                set.addAnimation(animation);
                set.setDuration(200);
                wechat.startAnimation(set);
                wechat.setVisibility(View.INVISIBLE);
            }
        }, 500);
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                TranslateAnimation translateAnimation = new TranslateAnimation(0f, 0f, 0f, 30f);
                AlphaAnimation animation = new AlphaAnimation(1, 0);
                AnimationSet set = new AnimationSet(true);
                set.addAnimation(translateAnimation);
                set.addAnimation(animation);
                set.setDuration(200);
                sina_weibo.startAnimation(set);
                sina_weibo.setVisibility(View.INVISIBLE);
                isAnimation=false;
                popupWindow.dismiss();
            }
        }, 700);
    }

    public void show(View view, int gravity, int style) {
        //setStyle(style);
        popupWindow.showAtLocation(view, gravity, 0, 0);
    }

    public void show(View view, int gravity) {
        //setStyle(STYLE_WHITE);
        //inAnimation();
        showAnimation(mItemViews);
        popupWindow.showAtLocation(view, gravity, 0, 0);
    }
    /**
     * 选项按钮显示动画
     *
     * @param layout
     */
    private void showAnimation(ViewGroup layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            final View child = layout.getChildAt(i);
            child.setVisibility(View.INVISIBLE);
            child.setOnClickListener(this);
            ThreadHandler.postUiThread(new Runnable() {
                @Override
                public void run() {
                    child.setVisibility(View.VISIBLE);
                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY",60f,
                            0);
                    fadeAnim.setDuration(800);
                    fadeAnim.setInterpolator(new OvershootInterpolator());
                    fadeAnim.setInterpolator(new ShareBounceAnimation());
                    fadeAnim.start();
                    fadeAnim.addListener(new AnimatorListenerAdapter() {
                      /*  @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            AnimationSet set = new AnimationSet(true);
                            set.setInterpolator(new BounceInterpolator());
                            set.setDuration(300);
                            set.start();
                        }
*/
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            // KickBackAnimator kickAnimator = new KickBackAnimator();
                            // kickAnimator.setDuration(150);
                            // fadeAnim.setEvaluator(kickAnimator);
                            isAnimation=true;

                        }
                    });
                }
            }, i * 50);
        }
    }

    /**
     * 分享
     *
     */
    public void ShareArticle(String platform) {
        OnekeyShare oks = new OnekeyShare();
        if (platform != null) {
            oks.setPlatform(platform);
        }
        oks.disableSSOWhenAuthorize();
        oks.setSilent(false);
        oks.setDialogMode();
        oks.setTitle(title);
        oks.setUrl(url);
        if (SinaWeibo.NAME == platform) {
            shareText = shareText + url;
        }
        oks.setText(shareText);
        oks.setTitleUrl(titleUrl);
        oks.setSite(site);
        oks.setSiteUrl(siteUrl);
        if (shareImgUrl == null) {
            oks.setImagePath(TEST_IMAGE);
        } else {
            oks.setImageUrl(shareImgUrl);
        }

        oks.setCallback(this);
        oks.show(activity);
    }

    /**
     * 将图片存放到sd卡中
     *
     * @return
     */
    public String initImagePath() {

        String FILE_NAME = "/icon_share.png";
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && Environment.getExternalStorageDirectory().exists()) {
                TEST_IMAGE = Environment.getExternalStorageDirectory().getAbsolutePath() + FILE_NAME;
            } else {
                TEST_IMAGE = activity.getFilesDir().getAbsolutePath() + FILE_NAME;
            }
            File file = new File(TEST_IMAGE);
            if (!file.exists()) {
                file.createNewFile();
                Bitmap pic = BitmapFactory.decodeResource(activity.getResources(), R.drawable.icon);
                FileOutputStream fos = new FileOutputStream(file);
                pic.compress(CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            TEST_IMAGE = null;
        }
        return FILE_NAME;
    }

    @Override
    public void onCancel(Platform platform, int action) {
        // 取消监听
        mHandler.sendEmptyMessage(1);
    }

    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> arg2) {
        // 成功监听
        mHandler.sendEmptyMessage(2);
    }

    @Override
    public void onError(Platform platform, int action, Throwable t) {
        // 打印错误信息
        t.printStackTrace();
        // 错误监听
        String expName = t.getClass().getSimpleName();
        Message msg = new Message();
        msg.obj = expName;
        msg.what = 3;
        mHandler.sendMessage(msg);

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(activity, "分享已取消", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(activity, "分享成功", Toast.LENGTH_SHORT).show();
                    if (!TextUtils.isEmpty(contentId)) {
                        new ShareCompleteLoader().starLoader(contentId);
                    }
                    break;
                case 3:
                    String expName = (String) msg.obj;
                    if ("WechatClientNotExistException".equals(expName) || "WechatTimelineNotSupportedException".equals(expName)) {

                        Toast.makeText(activity, "目前您的微信版本过低或未安装微信，需要安装微信才能使用", Toast.LENGTH_SHORT).show();
                    } else if ("GooglePlusClientNotExistException".equals(expName)) {
                        Toast.makeText(activity, "Google+ 版本过低或者没有安装，需要升级或安装Google+才能使用！", Toast.LENGTH_SHORT).show();
                    } else if ("QQClientNotExistException".equals(expName)) {
                        Toast.makeText(activity, "QQ 版本过低或者没有安装，需要升级或安装QQ才能使用！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, "分享失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }

        }
    };

    public interface OnShareItemClickListence {
        public void onItemClick(String platform);
    }

    public static final int STYLE_WHITE = 1;
    public static final int STYLE_BLACK = 2;

    public void setStyle(int style) {
        switch (style) {
            case STYLE_WHITE:
                int txtColor = activity.getResources().getColor(R.color.progress_dialog_bg);
                //layout.setBackgroundColor(Color.WHITE);
                //sina_weibo.setTextColor(txtColor);
            /*wechat.setTextColor(txtColor);
			wechat_frined.setTextColor(txtColor);
			qzone.setTextColor(txtColor);*/
                break;
            case STYLE_BLACK:
                //layout.setBackgroundColor(Color.BLACK);
                //sina_weibo.setTextColor(Color.WHITE);
			/*wechat.setTextColor(Color.WHITE);
			wechat_frined.setTextColor(Color.WHITE);
			qzone.setTextColor(Color.WHITE);*/
                break;
            default:
                break;
        }
    }
}
