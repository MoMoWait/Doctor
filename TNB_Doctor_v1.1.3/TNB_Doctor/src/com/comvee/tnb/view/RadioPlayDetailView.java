package com.comvee.tnb.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.comvee.ThreadHandler;
import com.comvee.tnb.R;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.tnb.radio.RadioPlayerMrg;
import com.comvee.tnb.radio.RadioPlayerMrg.PlayerListener;

import com.comvee.ui.Rotate3dAnimation;
import com.comvee.util.TimeUtil;

/**
 * 电台播放条UI界面
 */
public class RadioPlayDetailView extends RelativeLayout implements OnClickListener, PlayerListener {

    private ImageView btnControl;
    private PlayerListener mLisener;
    private SeekBar mSeekbar;
    private MediaPlayer mMediaPlayer;
    private View mLoadingAnim;
    private OnClickToComment clickToComment;
    private TextView tvDuration, tvCurrent;
    private Handler mHnalder = new Handler() {
        public void handleMessage(android.os.Message msg) {
           if (!mMediaPlayer.isPlaying()) {
                btnControl.setImageResource(R.drawable.radio_play_detail_play);
            }
            int duration = mMediaPlayer.getDuration();
            int curPosition = mMediaPlayer.getCurrentPosition();
            tvDuration.setText(TimeUtil.fomateTime(duration, "mm:ss"));
            tvCurrent.setText(TimeUtil.fomateTime(curPosition, "mm:ss"));
            mSeekbar.setMax(duration);
            mSeekbar.setProgress(curPosition);
            mHnalder.sendEmptyMessageDelayed(0, 1000);
        }
    };

    public void setClickToComment(OnClickToComment clickToComment) {
        this.clickToComment = clickToComment;
    }

    public void setListener(PlayerListener mLisener) {
        this.mLisener = mLisener;
    }

    public RadioPlayDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RadioPlayDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadioPlayDetailView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMediaPlayer = RadioPlayerMrg.getInstance().getMediaPlayer();
        View.inflate(getContext(), R.layout.radio_play_bar_detail, this);
        mLoadingAnim = findViewById(R.id.btn_control_anim);
        mSeekbar = (SeekBar) findViewById(R.id.seekbar);
        btnControl = (ImageView) findViewById(R.id.btn_control);
        btnControl.setOnClickListener(this);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        tvCurrent = (TextView) findViewById(R.id.tv_current_position);
        findViewById(R.id.btn_next).setOnClickListener(this);
        findViewById(R.id.btn_pre).setOnClickListener(this);
        findViewById(R.id.btn_download).setOnClickListener(this);
        try {
            RadioPlayerMrg.getInstance().setPlayerListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
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

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            boolean playing = mMediaPlayer.isPlaying();
            boolean playing1 = RadioPlayerMrg.getInstance().isPlaying();
            btnControl.setImageResource(RadioPlayerMrg.getInstance().isPlaying() ? R.drawable.radio_play_detail_pause
                    : R.drawable.radio_play_detail_play);
            RadioPlayerMrg.getInstance().setPlayerListener(this);
            if (playing||playing1){

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_control:
                setObjectAnimator();
                if (RadioPlayerMrg.getInstance().isPlaying()) {
                    ThreadHandler.postUiThread(new Runnable() {
                        @Override
                        public void run() {
                           RadioPlayerMrg.getInstance().pause();
                            btnControl.setImageResource(R.drawable.radio_play_detail_play);
                            mHnalder.removeMessages(0);
                        }
                    },400);
                } else {
                    ThreadHandler.postUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RadioPlayerMrg.getInstance().play();
                            btnControl.setImageResource(R.drawable.radio_play_detail_pause);
                            mHnalder.sendEmptyMessage(0);
                        }
                    },400);
                }
                break;
            case R.id.btn_next:
                RadioPlayerMrg.getInstance().playNext();
                btnControl.setImageResource(R.drawable.radio_play_detail_pause);
                break;
            case R.id.btn_pre:
                RadioPlayerMrg.getInstance().playPre();
                btnControl.setImageResource(R.drawable.radio_play_detail_pause);
                break;
            case R.id.btn_download:
                if (clickToComment != null) {
                    clickToComment.toComment();
                }

                break;
            default:
                break;
        }
    }
    //3D翻转效果
    private void setObjectAnimator() {
        btnControl.setClickable(false);
        final ObjectAnimator objAnim = ObjectAnimator.ofFloat(btnControl, "rotationY", 0.f, 90f).setDuration(500);
        objAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                btnControl.setClickable(true);
                ObjectAnimator anim1 = ObjectAnimator.ofFloat(btnControl, "rotationY", 270f, 360f).setDuration(500);
                anim1.start();
            }
        });
        objAnim.start();
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
            mLisener.onStart(item);
        }
        mHnalder.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    public void startLoading(RadioAlbumItem obj) {
        mLoadingAnim.setVisibility(View.GONE);
        mLoadingAnim.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotate_radio_fast));
    }

    @Override
    public void onWindowSystemUiVisibilityChanged(int visible) {
        super.onWindowSystemUiVisibilityChanged(visible);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHnalder.removeMessages(0);
    }


    @Override
    public void endLoading() {
        mLoadingAnim.setVisibility(View.GONE);
    }

    public interface OnClickToComment {
        public void toComment();
    }
}
