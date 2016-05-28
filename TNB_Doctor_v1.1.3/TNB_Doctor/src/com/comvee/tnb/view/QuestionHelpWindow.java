package com.comvee.tnb.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.comvee.tnb.R;

public class QuestionHelpWindow extends PopupWindow {

	private Context mContext;
	public QuestionHelpWindow(Context cxt,String msg) {
		super(cxt);
		this.mContext = cxt;
		
		TextView tv = new TextView(cxt.getApplicationContext());
		tv.setText(msg);
		tv.setBackgroundResource(R.drawable.bg_question_help);
		setContentView(tv);
		this.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
		setWidth(-2);
		setHeight(-2);
		setBackgroundDrawable(new BitmapDrawable());
		setAnimationStyle(R.style.PopupAnimation);
		setFocusable(true);
	}
}
