package com.comvee.tnb.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.comvee.ThreadHandler;
import com.comvee.tnb.R;
import com.comvee.util.Util;

public class SingleInputItemWindow extends PopupWindow implements View.OnClickListener {

	private Context mContext;
	private View mRootView;
	private DialogInterface.OnClickListener itemClick;
	private LinearLayout layoutCheck;
	private int curIndex;

	private int imgIsCheck;
	private int imgUnCheck;
	
	public void setCheckImg(int isCheck,int unCheck){
		this.imgIsCheck = isCheck;
		this.imgUnCheck = unCheck;
	}

	public SingleInputItemWindow(Context cxt, String[] items, String title, int defPosition,int isCheck,int unCheck ) {
		super(cxt);
		setCheckImg(isCheck, unCheck);
		this.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
		this.mContext = cxt;
		mRootView = createRootView();
		curIndex = defPosition;
		layoutCheck = (LinearLayout) mRootView.findViewById(R.id.check_layout);
		layoutCheck.setVisibility(View.VISIBLE);
		for (int i = 0; i < items.length; i++) {
			createItemAction(mContext, items[i], layoutCheck, i);
		}

		final TextView tvTitle = (TextView) mRootView.findViewById(R.id.tv_title);
		tvTitle.setText(title);
		if (title.length() > 14) {
			tvTitle.setTextSize(16);
		}
		mRootView.findViewById(R.id.check_ok).setVisibility(View.GONE);
		mRootView.findViewById(R.id.check_no).setVisibility(View.GONE);
		mRootView.findViewById(R.id.check_unkown).setVisibility(View.GONE);
		mRootView.findViewById(R.id.btn_ok).setVisibility(View.GONE);

		setContentView(mRootView);
		setWidth(-1);
		setHeight(-1);
		setBackgroundDrawable(new BitmapDrawable());
		setAnimationStyle(R.style.PopupAnimation);
		setFocusable(true);
	}
	
	public SingleInputItemWindow(Context cxt, String[] items, String title, int defPosition) {
		this(cxt, items, title, defPosition, R.drawable.check_style_1_b,  R.drawable.check_style_1_a);
	}

	public void setOutTouchCancel(boolean b) {
		mRootView.setOnClickListener(b ? this : null);
	}

	private View createRootView() {
		View layout = View.inflate(mContext, R.layout.window_input_item, null);
		// LinearLayout layout = new LinearLayout(mContext);
		// layout.setOrientation(LinearLayout.VERTICAL);
		return layout;
	}

	public void createItemAction(Context context, String str, LinearLayout root, int index) {
		TextView tv = new TextView(context);
		int pading = Util.dipToPx(mContext, 10);
		tv.setPadding(pading, 0, pading, 0);
		tv.setText(str);
		tv.setTextColor(Color.BLACK);
		if (curIndex == index) {
			tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgIsCheck, 0);
		} else {
			tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgUnCheck, 0);
		}
		tv.setTextSize(18);
		tv.setGravity(Gravity.CENTER_VERTICAL);
		tv.setBackgroundResource(R.drawable.bg_check_style1);
		tv.setId(index);
		tv.setOnClickListener(this);
		root.addView(tv);
	}

	public void setOnItemClick(DialogInterface.OnClickListener itemClick) {
		this.itemClick = itemClick;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.root_layout) {
			dismiss();
		} else {
			if (curIndex >= 0) {
				TextView tv1 = (TextView) layoutCheck.getChildAt(curIndex);
				tv1.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgUnCheck, 0);
			}

			final int position = (Integer) v.getId();
			if (itemClick != null) {
				itemClick.onClick(null, position);
			}
			TextView tv = (TextView) v;
			tv.setCompoundDrawablesWithIntrinsicBounds(0, 0,imgIsCheck, 0);
			curIndex = position;
			ThreadHandler.postUiThread(new Runnable() {
				@Override
				public void run() {
					dismiss();
				}
			}, 100);

		}

	}

}
