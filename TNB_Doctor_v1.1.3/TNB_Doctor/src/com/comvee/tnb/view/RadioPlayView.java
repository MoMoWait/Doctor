package com.comvee.tnb.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.comvee.ThreadHandler;
import com.comvee.tnb.R;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.tnb.radio.RadioPlayerMrg;
import com.comvee.tnb.radio.RadioPlayerMrg.PlayerListener;
import com.comvee.tnb.widget.RadioPlayAnimation;

public class RadioPlayView extends RelativeLayout implements OnClickListener, PlayerListener {

    private TextView tvCount;
    private RelativeLayout tvRelative;
    private ImageView mRadioDetail;
    private RadioPlayAnimation mAnimation;

    public interface PlayerViewListener {
        public void onStart(RadioAlbumItem item);

        public void onPause(RadioAlbumItem item);

        public void onToCurAlbum();
    }

    private PlayerViewListener mLisener;
    private MediaPlayer mMediaPlayer;
    private View mLoadingAnim;
    private ImageView btnControl;
    private TextView tvLabel;
    private SeekBar mSeekbar;
    private static boolean isPaly = false;
    /**
     * 发送消息更新进度条
     */
    private Handler mHnalder = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (!mMediaPlayer.isPlaying()) {
                btnControl.setImageResource(R.drawable.radio_play_detail_play);
            }
            int duration = mMediaPlayer.getDuration();
            int curPosition = mMediaPlayer.getCurrentPosition();
            mSeekbar.setMax(duration);
            mSeekbar.setProgress(curPosition);
            mHnalder.sendEmptyMessageDelayed(0, 1000);
        }
    };

    public void setListener(PlayerViewListener mLisener) {
        this.mLisener = mLisener;
    }

    public RadioPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RadioPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadioPlayView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMediaPlayer = RadioPlayerMrg.getInstance().getMediaPlayer();
        View.inflate(getContext(), R.layout.radio_play_bar, this);
        mLoadingAnim = findViewById(R.id.btn_control_anim);
        mSeekbar = (SeekBar) findViewById(R.id.seekbar);
        btnControl = (ImageView) findViewById(R.id.btn_control);
        btnControl.setOnClickListener(this);
        mAnimation = new RadioPlayAnimation();
        tvLabel = (TextView) findViewById(R.id.tv_label);
        tvCount = (TextView) findViewById(R.id.tv_count);
        tvRelative = (RelativeLayout) findViewById(R.id.radio_play);
        mRadioDetail = (ImageView) findViewById(R.id.radio_detail);
        mRadioDetail.setOnClickListener(this);
        tvLabel.setOnClickListener(this);
        tvCount.setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        try {
            RadioPlayerMrg.getInstance().setPlayerListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                mHnalder.sendEmptyMessage(0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                mHnalder.removeMessages(0);
            }

            @Override
            public void onProgressChanged(SeekBar seekbar, int position, boolean fromUser) {
                if (fromUser) {
                    RadioPlayerMrg.getInstance().getMediaPlayer().seekTo(position);
                }
            }
        });

        if (RadioPlayerMrg.getInstance().isLoading()) {
            startLoading(null);
        }
        mHnalder.sendEmptyMessage(0);

    }

    public void setLabel(String str) {
        if (tvLabel != null) {
            tvLabel.setText(str);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            btnControl.setImageResource(RadioPlayerMrg.getInstance().isPlaying() ? R.drawable.radio_play_detail_pause
                    : R.drawable.radio_play_detail_play);
            RadioPlayerMrg.getInstance().setPlayerListener(this);
            if (RadioPlayerMrg.getInstance().getAudioListSize() > 0) {
                tvRelative.setVisibility(View.VISIBLE);
                mRadioDetail.setVisibility(View.INVISIBLE);
                tvLabel.setText(RadioPlayerMrg.getInstance().getCurrent().radioTitle);
                tvCount.setText(RadioPlayerMrg.getInstance().getCurrent().radioSubhead);
                isPaly = true;
            }else{
                tvRelative.setVisibility(View.GONE);
                mRadioDetail.setVisibility(View.GONE);
                isPaly = false;
            }
            if (!RadioPlayerMrg.getInstance().isPlaying() && isPaly) {
                tvRelative.setVisibility(View.GONE);
                mRadioDetail.setVisibility(View.VISIBLE);
            }
            mHnalder.sendEmptyMessage(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.tv_label:
                    if (null != mLisener)
                        mLisener.onToCurAlbum();
                    break;
                case R.id.tv_count:
                    if (null != mLisener)
                        mLisener.onToCurAlbum();
                    break;
                case R.id.btn_control:
                    if (RadioPlayerMrg.getInstance().isPlaying()) {
                        mAnimation.setObjectAnimator(btnControl);
                        ThreadHandler.postUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RadioPlayerMrg.getInstance().pause();
                                mAnimation.setPauseAnimation(tvRelative, mRadioDetail);
                                mHnalder.removeMessages(0);
                            }
                        }, 300);
                    }
                    break;
                case R.id.btn_next:
                    RadioPlayerMrg.getInstance().playNext();
                    tvLabel.setText(RadioPlayerMrg.getInstance().getCurrent().radioTitle);
                    break;
                case R.id.radio_detail:
                    if (!RadioPlayerMrg.getInstance().isPlaying()) {
                        mAnimation.setObjectAnimator(mRadioDetail);
                        ThreadHandler.postUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RadioPlayerMrg.getInstance().play();
                                mRadioDetail.setImageResource(R.drawable.radio_play_detail_pause);
                                mAnimation.setPlayAnimation(mRadioDetail, tvRelative);
                                mHnalder.sendEmptyMessage(0);
                            }
                        }, 100);
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause(RadioAlbumItem item) {
        if (btnControl != null) {
            btnControl.setImageResource(R.drawable.radio_play_detail_play);
        }
        if (mLisener != null) {
            mLisener.onPause(item);
        }
        mHnalder.removeMessages(0);
    }

    @Override
    public void onStart(RadioAlbumItem item) {
        if (btnControl != null) {
            btnControl.setImageResource(R.drawable.radio_play_detail_pause);
        }
        if (mLisener != null) {
            if (RadioPlayerMrg.getInstance().getAudioListSize() > 0) {
                ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvRelative.setVisibility(View.VISIBLE);
                        mRadioDetail.setVisibility(View.INVISIBLE);
                        tvLabel.setText(RadioPlayerMrg.getInstance().getCurrent().radioTitle);
                        tvCount.setText(RadioPlayerMrg.getInstance().getCurrent().radioSubhead);
                    }
                }, 800);
            }
            mLisener.onStart(item);
        }
        mHnalder.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    public void startLoading(RadioAlbumItem obj) {

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHnalder.removeMessages(0);
    }

    @Override
    public void endLoading() {
    }

    public interface OnClickToComment {
        public void toComment();
    }

    public void setClickToComment(OnClickToComment clickToComment) {
        this.clickToComment = clickToComment;
    }

    private OnClickToComment clickToComment;

}
