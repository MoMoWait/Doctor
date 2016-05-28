package com.comvee.tnb.ui.record.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.comvee.tnb.R;

public class CustomerPopupWindow {
	public static final int LEFT_ID = R.id.tv_left;
	public static final int RIGHT_ID = R.id.tv_right;
	public static final int TEXT_ID = R.id.text;

	@SuppressWarnings("deprecation")
	public static PopupWindow getPopupWindowTwoButton(Context context, String title, String left, String right, String leftColor, String rightColor,
			OnClickListener leftClickListener, OnClickListener rightClickListener) {
		View converView = LayoutInflater.from(context).inflate(R.layout.popup_tip_two_button, null);
		PopupWindow popup = new PopupWindow(converView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		popup.setBackgroundDrawable(new BitmapDrawable());
		popup.setOutsideTouchable(true);
		TextView leftTv = (TextView) converView.findViewById(R.id.tv_left);
		TextView rightTv = (TextView) converView.findViewById(R.id.tv_right);
		TextView titleView = (TextView) converView.findViewById(R.id.tip);
		titleView.setText(title);
		leftTv.setText(left);
		rightTv.setText(right);
		leftTv.setTextColor(Color.parseColor(leftColor));
		rightTv.setTextColor(Color.parseColor(rightColor));
		leftTv.setOnClickListener(leftClickListener);
		rightTv.setOnClickListener(rightClickListener);
		return popup;
	}

	@SuppressWarnings("deprecation")
	public static PopupWindow getPopupWindowOneButton(Context context, String text, String textColor, String title, OnClickListener onclickListener) {
		View converView = LayoutInflater.from(context).inflate(R.layout.popup_tip_one_button, null);
		PopupWindow popup = new PopupWindow(converView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		popup.setBackgroundDrawable(new BitmapDrawable());
		popup.setOutsideTouchable(true);
		TextView titleView = (TextView) converView.findViewById(R.id.tip);
		titleView.setText(title);
		TextView textTv = (TextView) converView.findViewById(R.id.text);
		textTv.setText(text);
		textTv.setTextColor(Color.parseColor(textColor));
		textTv.setOnClickListener(onclickListener);
		return popup;
	}
}
