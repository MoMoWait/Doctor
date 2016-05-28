package com.comvee.tnb.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.comvee.tnb.R;

public class CustomProgressDialog extends Dialog {

	private static CustomProgressDialog customProgressDialog = null;


	public CustomProgressDialog(Context context) {
		super(context);
	}

	public CustomProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	public static CustomProgressDialog createDialog(Context context) {

		customProgressDialog = new CustomProgressDialog(context, R.style.CustomProgressDialog);
		customProgressDialog.setContentView(R.layout.dialog_progress);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		customProgressDialog.getWindow().setWindowAnimations(R.style.fade_anim);
		return customProgressDialog;

	}

	private void startPro() {
		// View pro = customProgressDialog.findViewById(R.id.loadingImageView);
		// if (pro != null) {
		// pro.startAnimation(AnimationUtils.loadAnimation(getContext(),
		// R.anim.progress_round));
		// }
		ProgressBar pro = (ProgressBar) customProgressDialog.findViewById(R.id.loadingImageView);
		pro.setVisibility(View.VISIBLE);
	}

	@Override
	public void show() {
		super.show();
		startPro();
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

	public CustomProgressDialog setTitile(String strTitle) {
		return customProgressDialog;
	}

	public CustomProgressDialog setMessage(String strMessage) {

		TextView tvMsg = (TextView) customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}

		return customProgressDialog;

	}

}
