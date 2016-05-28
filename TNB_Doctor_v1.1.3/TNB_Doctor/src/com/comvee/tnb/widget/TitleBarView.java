package com.comvee.tnb.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;

/**
 * 标题栏
 * 
 * @author friendlove
 * 
 */
public class TitleBarView extends RelativeLayout {

	public final static int ID_RIGHT_BUTTON = R.id.btn_top_right;
	public final static int ID_LEFT_BUTTON = R.id.btn_top_left;
	public final static int ID_TITLE_BUTTON = R.id.tv_center;
	public final static int ID_TITLE_DRAWABLE_LEFT_BUTTON = R.id.title_drawer_left;
	public final static int ID_TITLE_DRAWABLE_RIGHT_BUTTON = R.id.title_drawer_right;
	public final static int ID_TITLE_LAYOUT = R.id.title_layout;

	private TextView tvTitle;
	private ImageView titleDrawerLeft, titleDrawerRight;
	private ImageView ivRight, ivLeft;
	private TextView tvRight, tvLeft;
	private RelativeLayout titleLayout;
	private View titleView;
	private View titleBar;
	private View line;

	public TitleBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TitleBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TitleBarView(Context context) {
		super(context);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.layout_top_bar, this);
		tvTitle = (TextView) findViewById(R.id.tv_center);
		titleDrawerLeft = (ImageView) findViewById(R.id.title_drawer_left);
		titleDrawerRight = (ImageView) findViewById(R.id.title_drawer_right);
		titleLayout = (RelativeLayout) findViewById(R.id.title_layout);
		titleBar = findViewById(R.id.layout_titlebar);
		ivRight = (ImageView) findViewWithTag("iv_right");
		ivLeft = (ImageView) findViewWithTag("iv_left");
		tvRight = (TextView) findViewWithTag("tv_right");
		tvLeft = (TextView) findViewWithTag("tv_left");
		line = findViewById(R.id.titlebar_line);
	}

	public void setTitleBarBackgroundColor(int color) {
		titleBar.setBackgroundColor(color);
	}

	public void setTitle(String text) {
		getChildAt(0).setVisibility(View.VISIBLE);
		titleDrawerLeft.setVisibility(GONE);
		titleDrawerRight.setVisibility(GONE);
		if (titleView != null) {
			titleView.setVisibility(View.GONE);
		}
		titleLayout.setOnClickListener(null);
		tvTitle.setText(text);

	}

	@SuppressLint("NewApi")
	public void setTitle(String text, OnClickListener listener) {
		tvTitle.setText(text);
		titleLayout.setOnClickListener(listener);
	}

	public void hideRightButton() {
		tvRight.setVisibility(View.GONE);
		ivRight.setVisibility(View.GONE);
	}

	public void hideLeftButton() {
		tvLeft.setVisibility(View.GONE);
		ivLeft.setVisibility(View.GONE);
	}

	public void setTitleDrawer(Drawable leftDrawable, OnClickListener leftListener, Drawable rightDrawable, OnClickListener rightListener) {
		if (leftDrawable == null) {
			titleDrawerLeft.setVisibility(View.GONE);
		} else {
			titleDrawerLeft.setVisibility(View.VISIBLE);
			titleDrawerLeft.setImageDrawable(leftDrawable);
			titleDrawerLeft.setOnClickListener(leftListener);
		}
		if (rightDrawable == null) {
			titleDrawerRight.setVisibility(View.GONE);
		} else {
			titleDrawerRight.setVisibility(View.VISIBLE);
			titleDrawerRight.setImageDrawable(rightDrawable);
			titleDrawerRight.setOnClickListener(rightListener);
		}
	}

	public void setRightButton(String label, OnClickListener listener) {
		ivRight.setVisibility(View.GONE);
		tvRight.setVisibility(View.VISIBLE);
		tvRight.setEnabled(true);
		tvRight.setText(label);
		tvRight.setOnClickListener(listener);
	}

	public void setLeftButton(String label, OnClickListener listener) {
		ivLeft.setVisibility(View.GONE);
		tvLeft.setVisibility(View.VISIBLE);
		tvLeft.setEnabled(true);
		tvLeft.setText(label);
		tvLeft.setOnClickListener(listener);
	}

	public void setTextColor(String colorLeft, String colorTitle, String colorRight) {
		tvLeft.setTextColor(Color.parseColor(colorLeft));
		tvTitle.setTextColor(Color.parseColor(colorTitle));
		tvRight.setTextColor(Color.parseColor(colorRight));
	}

	public void setTextColor(int colorLeft, int colorTitle, int colorRight) {
		tvLeft.setTextColor(colorLeft);
		tvTitle.setTextColor(colorTitle);
		tvRight.setTextColor(colorRight);
	}

	public void setLeftButton(int imgRes, OnClickListener listener) {
		tvLeft.setVisibility(View.GONE);
		ivLeft.setEnabled(true);
		ivLeft.setVisibility(View.VISIBLE);
		ivLeft.setImageResource(imgRes);
		ivLeft.setOnClickListener(listener);
	}

	public void setRightButton(int imgRes, OnClickListener listener) {
		tvRight.setVisibility(View.GONE);
		ivRight.setVisibility(View.VISIBLE);
		ivRight.setEnabled(true);
		ivRight.setImageResource(imgRes);
		ivRight.setOnClickListener(listener);
	}

	public void setRightButtonEnabled(boolean b) {
		tvRight.setEnabled(b);
		ivRight.setEnabled(b);
	}

	public void setLeftButtonEnabled(boolean b) {
		tvLeft.setEnabled(b);
		ivLeft.setEnabled(b);
	}

	public TextView gettitleView() {
		return tvTitle;
	}

	public View getTitleView() {
		return titleView;
	}

	public View resetLayout(int layoutId) {
		getChildAt(0).setVisibility(View.GONE);
		LayoutInflater.from(getContext()).inflate(layoutId, this);
		return getChildAt(1);
	}

	public void setLeftDefault(final BaseFragment fragment) {
		setLeftButton(R.drawable.top_menu_back, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					if (!fragment.onBackPress()) {
						FragmentMrg.toBack(fragment.getActivity());
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});

	}

	public void setLeftDefault(final BaseFragment fragment, int imgRes) {
		setLeftButton(imgRes, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					if (!fragment.onBackPress()) {
						FragmentMrg.toBack(fragment.getActivity());
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});

	}

	public void setLineHide(){
		line.setVisibility(View.GONE);
	}
}
