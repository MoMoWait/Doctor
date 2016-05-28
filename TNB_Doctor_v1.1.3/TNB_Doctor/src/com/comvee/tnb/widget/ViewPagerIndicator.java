package com.comvee.tnb.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.comvee.tnb.R;


public class ViewPagerIndicator extends LinearLayout {
	private Bitmap selectedBitmap;
	private Bitmap unselectedBitmap;
	private int curPosition = -1;

	public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ViewPagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);

	}

	public ViewPagerIndicator(Context context) {
		super(context);
		init(context, null);

	}

	private void init(Context context, AttributeSet attrs) {
		selectedBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.indicator_selected);
		unselectedBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.indicator_unselected);
		setGravity(Gravity.CENTER_HORIZONTAL);
		setOrientation(LinearLayout.HORIZONTAL);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	public void updateIndicator(int count) {
		removeAllViews();
		for (int i = 0; i < count; i++) {
			LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
			ImageView imageView = new ImageView(getContext());
			imageView.setImageBitmap(unselectedBitmap);
			this.addView(imageView, params);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (selectedBitmap != null) {
			selectedBitmap.recycle();
		}
		if (unselectedBitmap != null) {
			unselectedBitmap.recycle();
		}
	}

	public void selectTo(int position) {

		try {
			((ImageView) this.getChildAt(position)).setImageBitmap(selectedBitmap);
			((ImageView) this.getChildAt(curPosition)).setImageBitmap(unselectedBitmap);
		} catch (Exception e) {
		}finally{
			curPosition = position;
		}

	}
}
