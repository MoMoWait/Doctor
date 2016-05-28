package com.comvee.tnb.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.comvee.tnb.R;
import com.comvee.util.Util;

public class QQLoadingView extends RelativeLayout {

	public QQLoadingView(Context context) {
		super(context);
	}

	public void show(final View view) {

		int loc[] = new int[2];
		view.getLocationInWindow(loc);

		WindowManager mWindows = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
		wmParams.type = 2002;
		wmParams.flags = 8;
		wmParams.format = PixelFormat.RGBA_8888;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		// wmParams.windowAnimations = android.R.style.Animation_Toast;
		wmParams.width = -1;
		wmParams.height = -1;
		final ImageView iv = new ImageView(getContext());
		iv.setImageResource(R.drawable.qq_login);
		// iv.setImageBitmap(view.getDrawingCache());

		View back = new View(getContext());
		back.setBackgroundResource(R.drawable.qq_login_bg);
		int w = Util.dip2px(getContext(), 50);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, w);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		this.addView(back, params);

		params = new RelativeLayout.LayoutParams(-2, -2);
		params.setMargins(loc[0], loc[1] - Util.getStatuBarHight(getContext()), 0, 0);
		this.addView(iv, params);
		mWindows.addView(this, wmParams);

		Animation aniSca = AnimationUtils.loadAnimation(getContext(), R.anim.index_qq_scale_fullscreen);
		aniSca.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				view.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				hide();
				view.setVisibility(View.VISIBLE);
			}
		});

		iv.startAnimation(createToCenterAnim(getContext().getResources().getDisplayMetrics().heightPixels / 2 - loc[1] - view.getMeasuredHeight()
				+ Util.getStatuBarHight(getContext())));
		back.startAnimation(aniSca);

	}

	private Animation createToCenterAnim(int y) {
		TranslateAnimation aniTran = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
				TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.ABSOLUTE, y);
		aniTran.setFillAfter(true);
		aniTran.setDuration(400);
		return aniTran;
	}

	public void hide() {
		((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).removeView(this);
	}

}
