//package com.comvee.tnb.view;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.drawable.AnimationDrawable;
//import android.graphics.drawable.ColorDrawable;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.LinearLayout.LayoutParams;
//import android.widget.PopupWindow;
//import android.widget.TextView;
//
//import com.comvee.tnb.R;
//import com.comvee.tool.UITool;
//
//public class LoadingWindow extends PopupWindow {
//
//	private Activity mContext;
//	private View customProgressDialog;
//	public static int WHITE_BACKGROUND = 1;
//	public static int TRANSPARENT_BACKAGROUND = 2;
//	public static int FULL_SCREEN = 3;
//	private LinearLayout layout, backage_color_group;
//	private ImageView pro;
//	private TextView textView;
//
//	public LoadingWindow() {
//		super();
//	}
//
//	public LoadingWindow(Activity cxt) {
//		super(cxt);
//		try {
//
//			this.mContext = cxt;
//			customProgressDialog = LayoutInflater.from(mContext).inflate(R.layout.dialog_progress_new, null);
//			setContentView(customProgressDialog);
//			this.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
//			backage_color_group = (LinearLayout) customProgressDialog.findViewById(R.id.backage_color_group);
//			textView = (TextView) customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
//			layout = (LinearLayout) customProgressDialog.findViewById(R.id.loding_group);
//			pro = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
//			setWidth(-1);
//			setHeight(-1);
//			setFocusable(true);
//			pro.setVisibility(View.VISIBLE);
//			AnimationDrawable animationDrawable = (AnimationDrawable) pro.getDrawable();
//			setAnimationStyle(R.style.fade_anim);
//			animationDrawable.start();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//	}
//
//	public void show(View view, String msg, int type) {
//		try {
//			if (type == TRANSPARENT_BACKAGROUND) {
//				pro.setLayoutParams(new LayoutParams(UITool.dip2px(mContext, 60), UITool.dip2px(mContext, 60)));
//				layout.setBackgroundResource(R.drawable.tanchuang_17);
//				backage_color_group.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
//			} else {
//				pro.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//				layout.setBackgroundResource(0);
//				backage_color_group.setBackgroundColor(mContext.getResources().getColor(R.color.white));
//			}
//			if (msg != null && !msg.equals("")) {
//				textView.setVisibility(View.VISIBLE);
//				textView.setText(msg);
//			} else {
//				textView.setVisibility(View.GONE);
//			}
//			if (type == FULL_SCREEN) {
//				this.showAsDropDown(view, 0, -view.getHeight());
//				setFocusable(false);
//			} else {
//				this.showAtLocation(mContext.findViewById(R.id.content), Gravity.BOTTOM, 0, UITool.dip2px(mContext, 45));
//			}
//
//			// this.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
//			// this.showAsDropDown(view, 0, Util.dipToPx(mContext, 90),
//			// Gravity.TOP);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//	}
//}
