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

public class MenuPopwindow extends PopupWindow implements View.OnClickListener
{

	private Context mContext;
	private View mRootView;
	private DialogInterface.OnClickListener itemClick;
	private LinearLayout layoutCheck;

	public MenuPopwindow(Context cxt, String[] items)
	{
		super(cxt);
		this.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
		this.mContext = cxt;
		mRootView = createRootView();
		layoutCheck = (LinearLayout) mRootView.findViewById(R.id.check_layout);
		layoutCheck.setVisibility(View.VISIBLE);
		for (int i = 0; i < items.length; i++)
		{
			createItemAction(mContext, items[i], layoutCheck, i);
		}
		setContentView(mRootView);
		setWidth(-1);
		setHeight(-1);
		setBackgroundDrawable(new BitmapDrawable());
		setAnimationStyle(R.style.PopupAnimation);
		setFocusable(true);
		setOutTouchCancel(true);
	}

	public void setOutTouchCancel(boolean b)
	{
		mRootView.setOnClickListener(b ? this : null);
	}

	private View createRootView()
	{
		View layout = View.inflate(mContext, R.layout.menu_head_layout, null);
		// LinearLayout layout = new LinearLayout(mContext);
		// layout.setOrientation(LinearLayout.VERTICAL);
		return layout;
	}

	public void createItemAction(Context context, String str, LinearLayout root, int index)
	{
		TextView tv = new TextView(context);
		int pading = Util.dipToPx(mContext, 15);
		tv.setPadding(pading, pading, pading, pading);
		tv.setText(str);
		tv.setTextColor(Color.BLACK);
		tv.setTextSize(20);
		tv.setGravity(Gravity.LEFT);
		tv.setBackgroundResource(R.drawable.seletion_default_btn);
		tv.setId(index);
		tv.setOnClickListener(this);
		root.addView(tv, -1, -2);
		View.inflate(mContext, R.layout.item_index_group_line, root);
	}

	public void setOnItemClick(DialogInterface.OnClickListener itemClick)
	{
		this.itemClick = itemClick;
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.root_layout)
		{
			dismiss();
		} else
		{
			final int position = (Integer) v.getId();
			if (itemClick != null)
			{
				itemClick.onClick(null, position);
			}
			ThreadHandler.postUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					dismiss();
				}
			}, 100);

		}

	}

}
