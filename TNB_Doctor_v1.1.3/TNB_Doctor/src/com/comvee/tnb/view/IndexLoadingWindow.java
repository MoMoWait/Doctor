package com.comvee.tnb.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.comvee.tnb.R;

public class IndexLoadingWindow extends PopupWindow {

	private Context mContext;
	private View customProgressDialog;

	public IndexLoadingWindow(Context cxt, String msg) {
		super(cxt);
		try {

			this.mContext = cxt;
			customProgressDialog = View.inflate(mContext, R.layout.index_loding_progress, null);
			setContentView(customProgressDialog);
			this.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
			if (msg != null && !msg.equals("")) {
				((TextView) customProgressDialog.findViewById(R.id.id_tv_loadingmsg)).setText(msg);
			}
			setWidth(-1);
			// setHeight(mContext.getResources().getDisplayMetrics().heightPixels
			// -
			// Util.dip2px(mContext, 45)-Util.getStatuBarHight(mContext));
			setHeight(-1);
			// setBackgroundDrawable(new BitmapDrawable());
			setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
			setAnimationStyle(R.style.PopupNoAnimation);
			setFocusable(true);
			ImageView pro = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
			pro.setVisibility(View.VISIBLE);
			AnimationDrawable animationDrawable = (AnimationDrawable) pro.getDrawable();
			setAnimationStyle(R.style.fade_anim);
			animationDrawable.start();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void show(View view, String msg) {
		try {

			if (msg != null && !msg.equals("")) {
				((TextView) customProgressDialog.findViewById(R.id.id_tv_loadingmsg)).setText(msg);
			}
			this.showAsDropDown(view, 0, 0);
			// this.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
			// this.showAsDropDown(view, 0, Util.dipToPx(mContext, 90),
			// Gravity.TOP);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
