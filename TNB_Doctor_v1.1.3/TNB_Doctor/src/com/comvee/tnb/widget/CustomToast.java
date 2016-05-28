package com.comvee.tnb.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.comvee.tnb.R;

public class CustomToast extends PopupWindow implements View.OnClickListener
{

	private Context mContext;
	private View mRootView;

	public CustomToast(Context cxt, String msg, String title)
	{
		super(cxt);
		this.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
		this.mContext = cxt;
		mRootView = createRootView();
		final TextView tvMsg = (TextView) mRootView.findViewById(R.id.tv_msg);
		tvMsg.setText(msg);

		setContentView(mRootView);
		setWidth(-1);
		setHeight(-2);
		setBackgroundDrawable(new BitmapDrawable());
		setAnimationStyle(R.style.TostAnimation);
		setFocusable(true);
	}


	public void setOutTouchCancel(boolean b)
	{
		mRootView.setOnClickListener(b ? this : null);
	}

	private View createRootView()
	{
		View layout = View.inflate(mContext, R.layout.toast, null);
		return layout;
	}

	@Override
	public void onClick(View v) {

	}

}
