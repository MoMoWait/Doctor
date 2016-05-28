package com.comvee.tnb.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.util.Util;

public class MultiInputItemWindow extends PopupWindow implements View.OnClickListener
{

	private Context mContext;
	private View mRootView;
	private OnItemClick itemClick;
	private LinearLayout layoutCheck;
	private TextView tvOk;
	private TextView tvNo;
	private TextView tvUnkown;
	private View btnOk;

	public MultiInputItemWindow(Context cxt, String[] items, String title)
	{
		super(cxt);
		this.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
		this.mContext = cxt;
		mRootView = createRootView();
		layoutCheck = (LinearLayout) mRootView.findViewById(R.id.check_layout);
		for (int i = 0; i < items.length; i++)
		{
			createItemAction(mContext, items[i], layoutCheck, i);
		}

		final TextView tvTitle = (TextView) mRootView.findViewById(R.id.tv_title);
		tvTitle.setText(title);
		if (title.length() > 14)
		{
			tvTitle.setTextSize(16);
		}

		tvOk = (TextView) mRootView.findViewById(R.id.check_ok);
		tvNo = (TextView) mRootView.findViewById(R.id.check_no);
		tvUnkown = (TextView) mRootView.findViewById(R.id.check_unkown);

		tvOk.setTag(false);
		tvNo.setTag(false);
		tvUnkown.setTag(false);

		tvNo.setOnClickListener(this);
		tvOk.setOnClickListener(this);
		tvUnkown.setOnClickListener(this);

		btnOk = mRootView.findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(this);

		setContentView(mRootView);
		setWidth(-1);
		setHeight(-1);
		setBackgroundDrawable(new BitmapDrawable());
		setAnimationStyle(R.style.PopupAnimation);
		setFocusable(true);
	}

	/**
	 * 
	 * @param type
	 * 			1、是2、否3、不知道
	 * @param bs
	 */
	public void setDefaultValue(int  type, boolean[] bs)
	{
		switch (type)
		{
		case 1:
			checkButtonOk(true);
			for (int i = 0; i < bs.length; i++)
			{
				checkItem(i, bs[i]);
			}
			break;
		case 2:
			checkButtonNo(true);
			break;
		case 3:
			checkButton(tvUnkown, true);
			break;
		default:
			break;
		}
	}

	public void setOutTouchCancel(boolean b)
	{
		mRootView.setOnClickListener(b ? this : null);
	}

	private View createRootView()
	{
		View layout = View.inflate(mContext, R.layout.window_input_item, null);
		// LinearLayout layout = new LinearLayout(mContext);
		// layout.setOrientation(LinearLayout.VERTICAL);
		return layout;
	}

	public void createItemAction(Context context, String str, LinearLayout root, int index)
	{
		TextView tv = new TextView(context);
		int pading = Util.dipToPx(mContext, 50);
		tv.setPadding(pading, 0, 12, 0);
		tv.setText(str);
		tv.setTextColor(Color.BLACK);
		tv.setTag(false);
		tv.setTextSize(18);
		tv.setGravity(Gravity.CENTER_VERTICAL);
		tv.setBackgroundResource(R.drawable.bg_check_style1);
		tv.setId(index);
		tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check_style_1_a, 0);
		tv.setOnClickListener(this);
		root.addView(tv);
	}

	public void setOnItemClick(OnItemClick itemClick)
	{
		this.itemClick = itemClick;
	}

	private void checkButtonOk(boolean b)
	{
		checkButton(tvOk, b);
		layoutCheck.setVisibility(b ? View.VISIBLE : View.GONE);
		btnOk.setVisibility(View.VISIBLE);
	}

	private void checkButtonNo(boolean b)
	{
		checkButton(tvNo, b);
	}

	private void checkButton(TextView v, boolean b)
	{
		v.setTag(b);
		v.setCompoundDrawablesWithIntrinsicBounds(0, 0, !b ? R.drawable.check_style_1_a : R.drawable.check_style_1_b, 0);
	}

	private void onClick()
	{
		int type = 0;
		boolean bNO = (Boolean) tvNo.getTag();
		boolean bOK = (Boolean) tvOk.getTag();
		boolean bUnkown = (Boolean) tvUnkown.getTag();
		
		if(bOK){
			type = 1;//是
		}else if(bNO){
			type = 2;//否
		}else if(bUnkown){//不知道
			type = 3;
		}else {
			type = 0;//没选
		}

		int len = layoutCheck.getChildCount();
		boolean[] b = new boolean[len];
		for (int i = 0; i < len; i++)
		{
			b[i] = isCheckItem(i);
		}

		itemClick.onClick(type, b);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_ok) {
			if (itemClick != null) {
				onClick();
			}
			dismiss();
		} else if (v.getId() == R.id.root_layout) {
			dismiss();
		} else if (v.getId() == R.id.check_ok) {
			boolean b = !((Boolean) tvOk.getTag());
			checkButtonOk(b);
			if (b) {
				checkButtonNo(false);
				btnOk.setVisibility(View.VISIBLE);
			}else{
				btnOk.setVisibility(View.GONE);
			}
		} else if (v.getId() == R.id.check_no) {
			boolean b = !((Boolean) tvNo.getTag());
			checkButtonNo(b);
			if (b) {
				btnOk.setVisibility(View.GONE);
				checkButtonOk(false);
				if (itemClick != null) {
					onClick();
				}
				dismiss();
			}
		} else if(v.getId() == R.id.check_unkown){
			boolean b = !((Boolean) tvUnkown.getTag());
			checkButtonNo(false);
			checkButton(tvUnkown, b);
			if (b) {
				btnOk.setVisibility(View.GONE);
				checkButtonOk(false);
				if (itemClick != null) {
					onClick();
				}
				dismiss();
			}
		} else {
//			final int position = (Integer) v.getId();
			TextView tv = (TextView) v;
			boolean b = !((Boolean) tv.getTag());
			tv.setTag(b);
			tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, !b ? R.drawable.check_style_1_a
					: R.drawable.check_style_1_b, 0);
		}

	}

	private boolean isCheckItem(int position)
	{
		return (Boolean) layoutCheck.getChildAt(position).getTag();
	}

	private void checkItem(int position, boolean b)
	{
		TextView tv = (TextView) layoutCheck.getChildAt(position);
		tv.setTag(b);
		tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, !b ? R.drawable.check_style_1_a : R.drawable.check_style_1_b,
				0);
	}

	interface OnItemClick
	{
		/**
		 * 
		 * @param checkType
		 * 			1、是2、否3、不知道0、没选
		 * @param bs
		 */
		void onClick(int checkType, boolean[] bs);
	}

}
