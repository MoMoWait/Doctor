package com.comvee.tnb.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class MyHorizontal extends HorizontalScrollView {
	private int currentX;
	private int scrollType;
	public static final int IDLE = 1;
	public static final int FLING = 2;
	private ScrollViewListener scrollViewListener;

	public MyHorizontal(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MyHorizontal(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyHorizontal(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void addOnScrollViewListener(ScrollViewListener listener) {
		this.scrollViewListener = listener;
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (getScrollX() == currentX) {
				// 滚动停止 取消监听线程
				scrollType = IDLE;
				if (scrollViewListener != null) {
					scrollViewListener.onScrollChanged(scrollType);
				}
				return;
			} else {
				// 手指离开屏幕 view还在滚动的时候
				scrollType = FLING;
				if (scrollViewListener != null) {
					scrollViewListener.onScrollChanged(scrollType);
				}
			}
			currentX = getScrollX();
			mHandler.sendEmptyMessageDelayed(0, 100);
		};
	};

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			scrollType = FLING;
			if (scrollViewListener != null) {
				scrollViewListener.onScrollChanged(scrollType);
			}
			// 手指在上面移动的时候 取消滚动监听线程
			mHandler.removeMessages(0);
			break;
		case MotionEvent.ACTION_UP:
			// 手指移动的时候
			mHandler.sendEmptyMessageDelayed(0, 100);
			break;
		}
		return super.onTouchEvent(ev);
	}

}
