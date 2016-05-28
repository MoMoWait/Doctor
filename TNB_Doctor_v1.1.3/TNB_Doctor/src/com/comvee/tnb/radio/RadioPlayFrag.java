package com.comvee.tnb.radio;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.ComveePageAdapter;
import com.comvee.tnb.db.DownloadItemDao;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.tnb.model.RadioGroup.RadioAlbum;
import com.comvee.tnb.network.CheckCollectLoader;
import com.comvee.tnb.network.NetworkCallBack;
import com.comvee.tnb.network.RadioCollectRequest;
import com.comvee.tnb.radio.RadioPlayerMrg.PlayerListener;
import com.comvee.tnb.ui.book.ShareUtil;
import com.comvee.tnb.ui.book.ShareUtil.OnShareItemClickListence;
import com.comvee.tnb.ui.heathknowledge.RadioViewPager;
import com.comvee.tnb.view.RadioPlayDetailView;
import com.comvee.tnb.view.RadioPlayDetailView.OnClickToComment;
import com.comvee.tnb.widget.BlurringView;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.Log;
import com.comvee.ui.Rotate3dAnimation;
import com.comvee.ui.RoundedImageView;
import com.comvee.util.BundleHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * 电台播放界面
 */
public class RadioPlayFrag extends BaseFragment implements OnClickListener, OnClickToComment {
    private RadioCollectRequest mCollectRequest;
    private RadioAlbumItem mAlbum;
    private RadioAlbum mAlbumGroup;
    private RadioPlayDetailView mPlayView;
    private ImageView mCollect;
    private TextView tvTitle;
    private CheckCollectLoader checkCollectLoader;
    private BlurringView mBlurringView;
    private ImageView ivComment;
    private ProgressBar mProgressBar;
    private int downloadPercent = 0;
    private int mProgressStatus = 0;
    private RadioViewPager mViewPager;
    private TextView mTvLabel;
    private ImageView mBg;
    private int isCollect;
    private int olderPosition = 50;//当前播放的是第几页
    private PhonePagerAdapter pagerAdapter;
    private boolean isModify;

    @Override
    public int getViewLayoutId() {
        return R.layout.radio_play_frag;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);
        if (dataBundle != null) {
            mAlbum = BundleHelper.getObjecByBundle(RadioAlbumItem.class, dataBundle.getBundle("item"));
            mAlbumGroup = BundleHelper.getObjecByBundle(RadioAlbum.class, dataBundle.getBundle("group"));
        }
        findViewById(R.id.btn_back).setOnClickListener(this);
        ivComment = (ImageView) findViewById(R.id.btn_comment);
        ivComment.setOnClickListener(this);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        tvTitle = (TextView) findViewById(R.id.tv_title);

        mCollect = (ImageView) findViewById(R.id.btn_collect);
        mCollect.setBackgroundResource(mAlbum.isCollect == 1 ? R.drawable.radio_play_collect_press
                : R.drawable.radio_play_collect);
        mCollect.setOnClickListener(this);
        findViewById(R.id.btn_share).setOnClickListener(this);
        mTvLabel = (TextView) findViewById(R.id.tv_info);
        viewPagerListener();
        mBg = (ImageView) findViewById(R.id.iv_backgroud);
        mBlurringView = (BlurringView) findViewById(R.id.layout_blur);
        mBlurringView.setBlurredView(mBg);
        initPlayView();
        initCheckLoader();
        checkCollectLoader.starCheck(mAlbumGroup.radioId, "2");
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                onSelectAlbum(mAlbum);
            }
        }, 100);
    }

    /**
     * 播放条UI
     */
    boolean isPlay = false;

    private void initPlayView() {
        mPlayView = (RadioPlayDetailView) findViewById(R.id.playview);
        mPlayView.findViewById(R.id.btn_next).setOnClickListener(this);
        mPlayView.findViewById(R.id.btn_pre).setOnClickListener(this);
        mPlayView.setClickToComment(this);
        mPlayView.setListener(new PlayerListener() {
            @Override
            public void onStart(RadioAlbumItem item) {
                try {
                    if (!isPlay) {
                        mTvLabel.setText(item.radioSubhead);
                        onSelectAlbum(item);
                        mCollect.setBackgroundResource(R.drawable.radio_play_collect);
                        checkCollectLoader.starCheck(item.radioId, "2");
                        isPlay = true;
                    } else {
                        isPlay = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mAlbum = item;
            }

            @Override
            public void onPause(RadioAlbumItem item) {
            }

            @Override
            public void startLoading(RadioAlbumItem item) {
            }

            @Override
            public void endLoading() {
            }
        });
    }

    /**
     * 圆形图片的加载
     *
     * @param mAlbum
     */
    private void onSelectAlbum(RadioAlbumItem mAlbum) {
        try {
            tvTitle.setText(mAlbum.radioTitle);
            ImageLoaderUtil.getInstance(getApplicationContext()).displayImage(mAlbum.photoUrl, mBg, ImageLoaderUtil.null_defult, imgListener);
            mTvLabel.setText(mAlbum.radioSubhead);
            View view = null;
            if ((view = pagerAdapter.getCacheView(mViewPager.getCurrentItem())) != null && !TextUtils.isEmpty(mAlbum.photoUrl.toString())) {
                RoundedImageView ivPhoto = (RoundedImageView) view.findViewById(R.id.iv_photo);
                final RoundedImageView ivRounded = (RoundedImageView) view.findViewById(R.id.iv_rounded);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_radio);
                ivPhoto.setAnimation(animation);
                ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivRounded.setVisibility(View.VISIBLE);
                    }
                }, 100);
                ImageLoaderUtil.getInstance(getApplicationContext()).displayImage(mAlbum.photoUrl, ivPhoto, ImageLoaderUtil.null_defult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * viewPager的监听事件
     */
    private void viewPagerListener() {
        mViewPager = (RadioViewPager) findViewById(R.id.RadioViewPager);
        pagerAdapter = new PhonePagerAdapter();
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(olderPosition);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private float positionOffset1;//当前页面偏移的百分比
            private int offsetPixels;//

            @Override
            public void onPageSelected(int position) {
                setPagerAnimation(0);
                if (olderPosition < position) {//下一首
                    TnbBaseNetwork.cancel(TnbBaseNetwork.POOL_THREAD_1);
                    RadioPlayerMrg.getInstance().playNext();
                } else if (olderPosition > position) {//上一首
                    TnbBaseNetwork.cancel(TnbBaseNetwork.POOL_THREAD_1);
                    RadioPlayerMrg.getInstance().playPre();
                }
                olderPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                switch (i) {
                    case 2:
                        if (0.1 <= positionOffset1 && positionOffset1 <= 0.5) {
                            setPagerAnimation(-80);
                        }
                        if (0.5 < positionOffset1 && positionOffset1 < 0.9) {
                            setPagerAnimation(80);
                        }
                        break;
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                positionOffset1 = positionOffset;
                offsetPixels = positionOffsetPixels;
            }
        });
    }

    //viewpager 会弹动画
    public void setPagerAnimation(final float f) {
        final ObjectAnimator objAnim = ObjectAnimator.ofFloat(mViewPager, "translationX", 0, -f).setDuration(300);
        objAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator anim1 = ObjectAnimator.ofFloat(mViewPager, "translationX", -f, f).setDuration(300);
                anim1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ObjectAnimator anim1 = ObjectAnimator.ofFloat(mViewPager, "translationX", f, 0).setDuration(300);
                        anim1.start();
                    }
                });
                anim1.start();
            }
        });
        objAnim.start();
    }

    private ImageLoadingListener imgListener = new ImageLoadingListener() {
        @Override
        public void onLoadingStarted(String arg0, View arg1) {
        }

        @Override
        public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
        }

        @Override
        public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
            mBlurringView.postInvalidate();
        }

        @Override
        public void onLoadingCancelled(String arg0, View arg1) {
        }
    };

    /**
     * viewPager 配适器
     */
    class PhonePagerAdapter extends ComveePageAdapter {

        @Override
        public View getView(int position) {
            Log.e("缓存页" + position);
            View mView = View.inflate(getContext(), R.layout.rounded_phone_view, null);
            return mView;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

    }

    /**
     * 初始化验证是否收藏接口
     */
    private void initCheckLoader() {
        checkCollectLoader = new CheckCollectLoader(new NetworkCallBack() {
            @Override
            public void callBack(int what, int status, Object obj) {
                try {
                    if (obj != null) {
                        mAlbum.isCollect = (Integer) obj;
                        mCollect.setBackgroundResource(mAlbum.isCollect == 1 ? R.drawable.radio_play_collect_press
                                : R.drawable.radio_play_collect);
                        isCollect = mAlbum.isCollect;//1收藏 0没有收藏
                        DownloadItemDao downloadItemDao = DownloadItemDao.getInstance();
                        RadioAlbumItem item = RadioPlayerMrg.getInstance().getCurrent();
                        ImageLoaderUtil.getInstance(getApplicationContext()).displayImage(item.photoUrl, mBg, ImageLoaderUtil.null_defult, imgListener);
                        mProgressBar.setVisibility(View.INVISIBLE);
                        ivComment.setClickable(true);
                        if (!downloadItemDao.has(item.radioId)) {
                            ivComment.setBackgroundResource(R.drawable.play_down);
                            downloadPercent = 0;
                        } else {
                            ivComment.setBackgroundResource(R.drawable.play_down_press);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void toFragment(FragmentActivity act, RadioAlbum mAlbumGroup, RadioAlbumItem mAlbum) {
        Bundle bundle = new Bundle();
        bundle.putBundle("item", BundleHelper.getBundleByObject(mAlbum));
        bundle.putBundle("group", BundleHelper.getBundleByObject(mAlbumGroup));
        FragmentMrg.toFragment(act, RadioPlayFrag.class, bundle, false);
    }

    @Override
    public boolean onBackPress() {
        FragmentMrg.toBack(getActivity(), isModify ? new Bundle() : null);
        return true;
    }

    private String setRequestStr() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(mAlbum.radioId);
        buffer.append(",");
        buffer.append("2");
        buffer.append(";");
        return buffer.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next://下一首
                mViewPager.setCurrentItem(olderPosition + 1);
                break;
            case R.id.btn_pre:// 上一首
                mViewPager.setCurrentItem(olderPosition - 1);
                break;
            case R.id.btn_comment:
                showDownLoad();
                break;
            case R.id.btn_collect:
                //电台收藏
                if (isCollect != 1) {
                    mCollectRequest = new RadioCollectRequest(new NetworkCallBack() {
                        @Override
                        public void callBack(int what, int status, Object obj) {
                            if (status == RadioCollectRequest.SUCCESS) {
                                //showToast("收藏成功!");
                                setRotate3DAnimation();
                                ThreadHandler.postUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mCollect.setBackgroundResource(R.drawable.radio_play_collect_press);
                                    }
                                }, 700);
                            }
                        }
                    }
                    );
                    mCollectRequest.requestCollect("2", mAlbum.radioId);
                    isCollect = 1;
                    isModify = true;
                } else {//取消收藏
                    mCollectRequest = new RadioCollectRequest(new NetworkCallBack() {
                        @Override
                        public void callBack(int what, int status, Object obj) {
                            if (status == RadioCollectRequest.SUCCESS) {
                                //showToast("取消收藏!");
                                setRotate3DAnimation();
                                ThreadHandler.postUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mCollect.setBackgroundResource(R.drawable.radio_play_collect);
                                    }
                                }, 700);
                            }
                        }
                    }
                    );
                    mCollectRequest.requestCancleCollect(setRequestStr());
                    isCollect = 0;
                    isModify = true;
                }
                break;
            case R.id.btn_share:
                showShare();
                break;
            case R.id.btn_back:
                onBackPress();
                break;
            default:
                break;
        }
    }

    //3D翻转效果
    private void setRotate3DAnimation() {
        mCollect.setClickable(false);
        Rotate3dAnimation rotation = new Rotate3dAnimation(0, 360, mCollect.getWidth() / 2, mCollect.getHeight() / 2, 0, false);
        rotation.setFillAfter(true);
        //初始化 Translate动画
        TranslateAnimation translateAnimation = new TranslateAnimation(0f, 0f, 0f, -30f);
        //动画集
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(translateAnimation);
        set.addAnimation(rotation);
        //设置动画时间 (作用到每个动画)
        set.setDuration(1000);
        set.setFillAfter(true);
        //set.setInterpolator(new BouncneweInterpolator());
        mCollect.startAnimation(set);
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                TranslateAnimation translateAnimation1 = new TranslateAnimation(0f, 0f, -30f, 0f);
                AnimationSet set = new AnimationSet(true);
                set.addAnimation(translateAnimation1);
                //设置动画时间 (作用到每个动画)
                set.setDuration(1000);
                set.setInterpolator(new BounceInterpolator());
                mCollect.startAnimation(set);
                set.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mCollect.setClickable(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        }, 1000);
    }

    private void showDownLoad() {
        DownloadItemDao downloadItemDao = DownloadItemDao.getInstance();
        RadioAlbumItem item = RadioPlayerMrg.getInstance().getCurrent();

        if (!downloadItemDao.has(item.radioId)) {
            ivComment.setClickable(false);
            ArrayList<RadioAlbumItem> tobeDownLoad = new ArrayList<RadioAlbumItem>();
            tobeDownLoad.add(item);
            DownloadMrg.downlaodAlbum(mAlbumGroup, tobeDownLoad);
            mProgressBar.setVisibility(View.VISIBLE);
            startHandler();
        } else {
            showToast("该节目已下载");
            ivComment.setClickable(true);
        }
    }

    /**
     * 进度条显示
     */
    private void startHandler() {
        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                if (msg.what == 0x111) {
                    mProgressStatus = downloadPercent;
                    //设置进度条当前的完成进度
                    mProgressBar.setProgress(mProgressStatus);
                    if (mProgressStatus == 100) {//下载完成的时候图标动画
                        ivComment.setBackgroundResource(R.drawable.play_down_press);
                        TranslateAnimation translateAnimation = new TranslateAnimation(0f, 0f, 0f, -8f);
                        AnimationSet set = new AnimationSet(true);
                        set.addAnimation(translateAnimation);
                        set.setDuration(800);
                        set.setInterpolator(new BounceInterpolator());
                        ivComment.startAnimation(set);
                        mProgressBar.setVisibility(View.INVISIBLE);
                        ivComment.setClickable(true);
                    }
                }
            }

        };

        ThreadHandler.postWorkThread(new Runnable() {
            @Override
            public void run() {
                while (downloadPercent < 100) {
                    doWork();
                    Message msg = new Message();
                    msg.what = 0x111;
                    mHandler.sendMessage(msg);
                }
            }
        });
    }

    private int doWork() {
        try {
            DownloadItemDao downloadItemDao = DownloadItemDao.getInstance();
            RadioAlbumItem item = RadioPlayerMrg.getInstance().getCurrent();
            if (!downloadItemDao.has(item.radioId)) {
                downloadPercent = item.downloadedPer;
            } else {
                item = downloadItemDao.getRadioById(item.radioId);
                downloadPercent = item.downloadedPer;
            }
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return downloadPercent;
    }

    private void showShare() {
        final ShareUtil shareUtil = ShareUtil.getInstance(getActivity());
        shareUtil.setTitle(mAlbum.radioSubhead);
        shareUtil.setShareImgUrl(mAlbum.photoUrl);
        shareUtil.setTitleUrl(mAlbum.shareHtml);
        shareUtil.setUrl(mAlbum.shareHtml);
        shareUtil.addOnShareItemClickListence(new OnShareItemClickListence() {
            @Override
            public void onItemClick(String platform) {
                shareUtil.setShareText(mAlbumGroup.radioInfo);
                shareUtil.ShareArticle(platform);
            }
        });
        shareUtil.show(mRoot, Gravity.BOTTOM, ShareUtil.STYLE_BLACK);
    }

    @Override
    public void toComment() {
        RadioCommentFrag.toFragment(getActivity(), mAlbumGroup.radioId, "2");
    }

}
