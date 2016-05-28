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

public class RecordItemWindow extends PopupWindow implements View.OnClickListener {

	private Context mContext;
	private View mRootView;
	private DialogInterface.OnClickListener itemClick;
	private LinearLayout layoutCheck;

	public RecordItemWindow(Context cxt, String[] items, boolean[] isShow) {
		super(cxt);
		this.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
		this.mContext = cxt;
		mRootView = createRootView();
		layoutCheck = (LinearLayout) mRootView.findViewById(R.id.check_layout);
		layoutCheck.setVisibility(View.VISIBLE);
		for (int i = 0; i < items.length; i++) {
			if (isShow != null && isShow.length == items.length) {
				createItemAction(mContext, items[i], layoutCheck, i, isShow[i]);
			}else{
				createItemAction(mContext, items[i], layoutCheck, i, true);
			}
		}
		setContentView(mRootView);

		setWidth(Util.dip2px(mContext, 110));
		setHeight(-1);
		setBackgroundDrawable(new BitmapDrawable());
		setAnimationStyle(R.style.PopupAnimation);
		setFocusable(true);
		setOutTouchCancel(true);
	}

	public void setOutTouchCancel(boolean b) {
		mRootView.setOnClickListener(b ? this : null);
	}

	private View createRootView() {
		View layout = View.inflate(mContext, R.layout.window_record_item, null);
		// LinearLayout layout = new LinearLayout(mContext);
		// layout.setOrientation(LinearLayout.VERTICAL);
		return layout;
	}

	public void createItemAction(Context context, String str, LinearLayout root, int index, boolean isShow) {
		TextView tv = new TextView(context);
		int pading = Util.dipToPx(mContext, 10);
		tv.setPadding(pading, 0, pading, 0);
		tv.setText(str);
		tv.setTextColor(Color.WHITE);
		tv.setTextSize(15);
		tv.setGravity(Gravity.CENTER);
		tv.setBackgroundResource(R.drawable.bg_check_style2);
		tv.setId(index);
		tv.setOnClickListener(this);
		root.addView(tv, -1, -2);
		if (isShow) {
			tv.setVisibility(View.VISIBLE);
		} else {
			tv.setVisibility(View.GONE);
		}
	}

	public void setOnItemClick(DialogInterface.OnClickListener itemClick) {
		this.itemClick = itemClick;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.root_layout) {
			dismiss();
		} else {
			final int position = (Integer) v.getId();
			if (itemClick != null) {
				itemClick.onClick(null, position);
			}
			ThreadHandler.postUiThread(new Runnable() {
				@Override
				public void run() {
					dismiss();
				}
			}, 100);

		}

	}

}
