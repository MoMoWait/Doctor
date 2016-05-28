package com.comvee.tnb.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.comvee.tool.UITool;

/**
 * 界面指示器
 * 
 * @author Administrator
 * 
 */
public class PageControlView extends LinearLayout {

	private int oldIndex;
	private int size;
	private int choiceRes;
	private int unChoiceRes;
	private Drawable d1, d2;

	public void setChoiceRes(int res) {
		this.choiceRes = res;
	}

	public void setUnChoiceRes(int res) {
		this.unChoiceRes = res;
	}

	public PageControlView(Context context) {
		super(context);
	}

	public PageControlView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public void init(int size, int choiceRes, int unChoiceRes) {
		setChoiceRes(choiceRes);
		setUnChoiceRes(unChoiceRes);
		oldIndex = 0;
		this.size = size;
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.setGravity(Gravity.CENTER_VERTICAL);
		this.removeAllViews();
		d1 = getResources().getDrawable(unChoiceRes);
		d2 = getResources().getDrawable(choiceRes);
		for (int i = 0; i < size; i++) {
			ImageView imageView = new ImageView(getContext());

			if (0 == i) {
				imageView.setLayoutParams(getLayout(6));
				imageView.setImageResource(choiceRes);
			} else {
				imageView.setLayoutParams(getLayout(5));
				imageView.setImageResource(unChoiceRes);
			}
			this.addView(imageView);
		}

	}

	private LayoutParams getLayout(int dip) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		params.setMargins(10, 0, 10, 0);
		params.width = UITool.dip2px(getContext(), dip);
		params.height = UITool.dip2px(getContext(), dip);
		return params;
	}

	public void generatePageControl(int currentIndex) {
		if (size == 0) {
			return;
		}
		if (currentIndex < size) {
			ImageView v = null;
			v = (ImageView) this.getChildAt(oldIndex);
			v.setImageDrawable(d1);
			v.setLayoutParams(getLayout(5));
			v = (ImageView) this.getChildAt(currentIndex);
			v.setImageDrawable(d2);
			v.setLayoutParams(getLayout(6));
			this.oldIndex = currentIndex;
		}

	}
}
