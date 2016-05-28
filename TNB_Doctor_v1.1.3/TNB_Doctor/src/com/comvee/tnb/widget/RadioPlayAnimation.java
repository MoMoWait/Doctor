package com.comvee.tnb.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.comvee.ThreadHandler;
import com.comvee.tnb.R;
import com.comvee.tnb.radio.RadioPlayerMrg;

/**
 * 电台播放条UI动画
 * Created by yujun on 2016/4/13.
 */
public class RadioPlayAnimation {
    /**
     * 暂停播放
     *
     * @param vanish
     * @param show
     */
    public void setPauseAnimation(final View vanish, final View show) {
        final Animation alphaAnimation = new AlphaAnimation(1f, 0f);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(alphaAnimation);
        set.setDuration(500);
        vanish.setAnimation(set);
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                RadioPlayerMrg.getInstance().pause();
                final Animation alphaAnimation = new AlphaAnimation(0f, 1f);
                //final Animation translateAnimation = new TranslateAnimation(0, 20, 0, -10);
                               /* translateAnimation.setDuration(800);
                                translateAnimation.setFillAfter(true);*/
                AnimationSet set = new AnimationSet(true);
                set.addAnimation(alphaAnimation);
                //set.addAnimation(translateAnimation);
                set.setDuration(300);
                set.setFillAfter(true);
                show.setAnimation(set);
                vanish.setVisibility(View.GONE);
                show.setVisibility(View.VISIBLE);
            }
        }, 300);
    }

    /**
     * 开始播放
     *
     * @param vanish
     * @param show
     */
    public void setPlayAnimation(final ImageView vanish, final View show) {
       // vanish.setClickable(false);
        final Animation alphaAnimation = new AlphaAnimation(1f, 0f);
       // final Animation translateAnimation = new TranslateAnimation(0, -20, 0, 10);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(alphaAnimation);
      //  set.addAnimation(translateAnimation);
        set.setDuration(500);
        vanish.setAnimation(set);
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                RadioPlayerMrg.getInstance().play();
                final Animation alphaAnimation = new AlphaAnimation(0f, 1f);
                AnimationSet set = new AnimationSet(true);
                set.addAnimation(alphaAnimation);
                set.setDuration(300);
                show.setAnimation(set);
                vanish.setVisibility(View.GONE);
                show.setVisibility(View.VISIBLE);
                vanish.setImageResource(R.drawable.radio_play_detail_play);
              //  vanish.setClickable(true);
            }
        }, 300);
    }
    //3D翻转效果
    public void setObjectAnimator(final View btn) {
        final ObjectAnimator objAnim = ObjectAnimator.ofFloat(btn, "rotationY", 0f, 90f).setDuration(300);
        objAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator anim1 = ObjectAnimator.ofFloat(btn, "rotationY", 270f, 360f).setDuration(300);
                anim1.start();
            }
        });
        objAnim.start();
    }
}
