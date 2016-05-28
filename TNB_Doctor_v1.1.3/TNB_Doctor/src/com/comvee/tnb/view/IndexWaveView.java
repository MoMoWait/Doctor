package com.comvee.tnb.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.comvee.tnb.R;

/**
 * 首页  水波纹  动画的  view
 * Created by friendlove-pc on 16/3/20.
 */
public class IndexWaveView extends RelativeLayout {
    private final int WAVE_COUNT = 5;
    private final int DELTA_BROCAS_TTIME = 1000;
    private AnimationSet[] animationSets = new AnimationSet[WAVE_COUNT];


    private ImageView[] waveImageViews = new ImageView[WAVE_COUNT];

    public IndexWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public IndexWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IndexWaveView(Context context) {
        super(context);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.index_wave_layout, this);
        initWaveAnimation();
    }

    private void initWaveAnimation() {
        waveImageViews[0] = (ImageView) findViewById(R.id.wave1);
        waveImageViews[1] = (ImageView) findViewById(R.id.wave2);
        waveImageViews[2] = (ImageView) findViewById(R.id.wave3);
        waveImageViews[3] = (ImageView) findViewById(R.id.wave4);
        waveImageViews[4] = (ImageView) findViewById(R.id.wave5);
    }

    public void startwaveAnim() {

        for (int i = 0; i < WAVE_COUNT; i++) {
            animationSets[i] = buildAnimation();
            if (i != 1)
                animationSets[i].setStartOffset(i * DELTA_BROCAS_TTIME);
            waveImageViews[i].startAnimation(animationSets[i]);
        }
    }


    private AnimationSet buildAnimation() {
        Animation scaleAnimation = new ScaleAnimation(1.0f, 10f, 1.0f, 10f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Animation alphaAnimation = new AlphaAnimation(1.f, 0);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setRepeatMode(Animation.RESTART);
        animationSet.setRepeatCount(5);
        animationSet.setFillAfter(true);
        animationSet.setDuration(WAVE_COUNT * DELTA_BROCAS_TTIME);
        return animationSet;
    }


}
