package com.comvee.tnb.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.comvee.ThreadHandler;
import com.comvee.tnb.R;
import com.comvee.tnb.model.Package;

public class PackageWindow extends PopupWindow implements View.OnClickListener {

	private Context mContext;
	private View mRootView;
	private DialogInterface.OnClickListener itemClick;
	private LinearLayout layoutCheck;
	private int curIndex;

	private int imgIsCheck;
	private int imgUnCheck;

	public void setCheckImg(int isCheck, int unCheck) {
		this.imgIsCheck = isCheck;
		this.imgUnCheck = unCheck;
	}

	public PackageWindow(Context cxt, ArrayList<Package> items, int defPosition, int isCheck, int unCheck) {
		super(cxt);
		setCheckImg(isCheck, unCheck);
		this.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
		this.mContext = cxt;
		mRootView = createRootView();
		curIndex = defPosition;
		layoutCheck = (LinearLayout) mRootView.findViewById(R.id.check_layout);
		layoutCheck.setVisibility(View.VISIBLE);
		for (int i = 0; i < items.size(); i++) {
			createItemAction(mContext, items.get(i), layoutCheck, i);
		}

		setContentView(mRootView);
		setWidth(-1);
		setHeight(-1);
		setTouchable(true);
		setFocusable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(new BitmapDrawable());
		setAnimationStyle(R.style.PopupAnimation);
		setFocusable(true);
		layoutCheck.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_down_in));
	}

	public PackageWindow(Context cxt, ArrayList<Package> items, int defPosition) {
		this(cxt, items, defPosition, R.drawable.check_style_1_b, R.drawable.check_style_1_a);
	}

	public void setOutTouchCancel(boolean b) {
		mRootView.setOnClickListener(b ? this : null);
	}

	private View createRootView() {
		View layout = View.inflate(mContext, R.layout.window_input_item_top, null);
		layout.findViewById(R.id.root_layout).setOnClickListener(this);
		return layout;
	}

	public void createItemAction(Context context, Package str, LinearLayout root, int index) {

		ViewGroup itemView = (ViewGroup) View.inflate(context, R.layout.ask_package_list_item, null);
		root.addView(itemView, -1, -2);

		TextView tvPackgeName = (TextView) itemView.findViewById(R.id.tv_package_name);
		TextView tvPackgeDecs = (TextView) itemView.findViewById(R.id.tv_package_decs);
		ImageView imgTag = (ImageView) itemView.findViewById(R.id.iv_check_tag);

		tvPackgeName.setText(str.packageName);
		tvPackgeDecs.setText(Html.fromHtml(String.format("咨询剩余<font color='#ff0000'>%d</font>次", str.leaveNum,
				str.tellNum)));

		if (curIndex == index) {
			imgTag.setImageResource(imgIsCheck);
		}else{
			imgTag.setImageResource(imgUnCheck);
		}
		
		itemView.setId(index);
		itemView.setOnClickListener(this);

		// TextView tv = new TextView(context);
		// int pading = Util.dipToPx(mContext, 10);
		// tv.setPadding(pading, 0, pading, 0);
		// tv.setText(str.packageName);
		// tv.setTextColor(Color.BLACK);
		// if (curIndex == index) {
		// tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgIsCheck, 0);
		// } else {
		// tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgUnCheck, 0);
		// }
		// tv.setTextSize(18);
		// tv.setGravity(Gravity.CENTER_VERTICAL);
		// tv.setBackgroundResource(R.drawable.bg_check_style1);
		// tv.setId(index);
		// tv.setOnClickListener(this);
		// root.addView(tv);
	}

	public void setOnItemClick(DialogInterface.OnClickListener itemClick) {
		this.itemClick = itemClick;
	}

	private void setCheck(int index, boolean checked) {
		View view = layoutCheck.getChildAt(index);
		ImageView imgTag = (ImageView) view.findViewById(R.id.iv_check_tag);
		imgTag.setImageResource(checked ? imgIsCheck : imgUnCheck);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.root_layout) {
			dismiss();
		} else {
			if (curIndex >= 0) {
				setCheck(curIndex, false);
			}

			final int position = (Integer) v.getId();
			if (itemClick != null) {
				itemClick.onClick(null, position);
			}
			setCheck(position, true);
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
