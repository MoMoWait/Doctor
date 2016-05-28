package com.comvee.tnb.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tool.UITool;

public class CustomProgressNewDialog extends Dialog {
	public static int WHITE_BACKGROUND = 1;
	public static int TRANSPARENT_BACKAGROUND = 2;
	public static int TRANSPARENT_BACKAGROUND_2 = 3;
	private LinearLayout layout, backage_color_group;
	private ImageView pro;
	private TextView textView;
	private static CustomProgressNewDialog customProgressDialog = null;

	public CustomProgressNewDialog(Context context) {
		super(context);
		init();
	}

	public CustomProgressNewDialog(Context context, int theme) {
		super(context, theme);
		init();
	}

	public static CustomProgressNewDialog createDialog(Context context) {

		customProgressDialog = new CustomProgressNewDialog(context, R.style.progressDialog);
		return customProgressDialog;

	}

	private void init() {
		setContentView(R.layout.dialog_progress_new);
		getWindow().getAttributes().gravity = Gravity.CENTER;
		getWindow().setWindowAnimations(R.style.fade_anim);
		backage_color_group = (LinearLayout) findViewById(R.id.backage_color_group);
		textView = (TextView) findViewById(R.id.id_tv_loadingmsg);
		layout = (LinearLayout) findViewById(R.id.loding_group);
		pro = (ImageView) findViewById(R.id.loadingImageView);
	}

	private void startPro() {
		pro.setVisibility(View.VISIBLE);
		AnimationDrawable animationDrawable = (AnimationDrawable) pro.getDrawable();
		animationDrawable.start();
	}

	@Override
	public void show() {
		super.show();
		startPro();
	}

	public void show(String msg, int type) {
		try {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.setMargins(0, UITool.dip2px(getContext(), 45), 0, 0);

			if (type == TRANSPARENT_BACKAGROUND) {
				pro.setLayoutParams(new LayoutParams(UITool.dip2px(getContext(), 60), UITool.dip2px(getContext(), 60)));
				layout.setBackgroundResource(R.drawable.tanchuang_17);
				backage_color_group.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));

			} else {
				pro.setLayoutParams(new LayoutParams(UITool.dip2px(getContext(), 60), UITool.dip2px(getContext(), 60)));
				layout.setBackgroundResource(R.drawable.jzbg_01);
				backage_color_group.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
			}
			backage_color_group.setLayoutParams(params);
			if (msg != null && !msg.equals("")) {
				textView.setVisibility(View.VISIBLE);
				textView.setText(msg);
			} else {
				textView.setVisibility(View.GONE);
			}
			// this.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
			// this.showAsDropDown(view, 0, Util.dipToPx(mContext, 90),
			// Gravity.TOP);
		} catch (Exception e) {
			// TODO: handle exception
		}
		show();
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

	public CustomProgressNewDialog setMessage(String strMessage) {

		TextView tvMsg = (TextView) customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}

		return customProgressDialog;

	}

}
