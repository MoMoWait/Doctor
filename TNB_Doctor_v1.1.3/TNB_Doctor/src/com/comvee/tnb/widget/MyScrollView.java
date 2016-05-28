package com.comvee.tnb.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {

	private int mContentTop;

	public void init(int spriteHeight) {
		mContentTop = spriteHeight;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyScrollView(Context context) {
		super(context);
	}

	// @Override
	// protected void onScrollChanged(int l, int t, int oldl, int oldt) {
	// super.onScrollChanged(l, t, oldl, oldt);
	// }

	private int getViewTop(View view) {
		int loc[] = new int[2];
		view.getLocationInWindow(loc);
		return loc[1];
	}

	private float mScrollStartY;

	public boolean isBootom() {
		return getScrollY() + getHeight() >= computeVerticalScrollRange();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isScrolling)
			return true;
		switch (ev.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mScrollStartY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if (mScrollStartY == 0) {
				mScrollStartY = ev.getY();
			}
			break;
		case MotionEvent.ACTION_UP:
			if (isBootom() || getScrollY() <= 0) {
				return super.onTouchEvent(ev);
			}
			if (ev.getY() < mScrollStartY) {
				close();
				mScrollStartY = 0;
				return true;
			} else if (ev.getY() > mScrollStartY) {
				if (0 != getScrollY()) {
					open();
					mScrollStartY = 0;
					return true;
				} else {
					mScrollStartY = 0;
				}
			}
			break;

		default:
			break;
		}

		return super.onTouchEvent(ev);
	}

	private void close() {
		// mTop = getViewTop(this);
		if (mListener != null) {
			mListener.onClose();
		}
		isScrolling = true;
		mScollTopY = getScrollY();
		new AsyncTask<Integer, Integer, Integer>() {

			@Override
			protected Integer doInBackground(Integer... params) {
				while (mScollTopY < mContentTop) {
					publishProgress(mScollTopY += 20);
					try {
						Thread.sleep(20);
					} catch (Exception e) {
					}
				}
				return null;
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
				if (values[0] > mContentTop)
					scrollTo(0, mContentTop);
				else
					scrollTo(0, values[0]);
			}

			protected void onPostExecute(Integer result) {
				isScrolling = false;
			};
		}.execute();

	}

	private boolean isScrolling;
	private int mScollTopY;

	private void open() {
		if (mListener != null) {
			mListener.onOpen();
		}
		isScrolling = true;
		mScollTopY = getScrollY();
		new AsyncTask<Integer, Integer, Integer>() {

			@Override
			protected Integer doInBackground(Integer... params) {
				while (mScollTopY > 0) {
					try {
						Thread.sleep(15);
					} catch (Exception e) {
					}
					publishProgress(mScollTopY -= 20);
				}
				return null;
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
				if (values[0] < 0)
					scrollTo(0, 0);
				else
					scrollTo(0, values[0]);
			}

			protected void onPostExecute(Integer result) {
				isScrolling = false;
			};
		}.execute();

	}

	public void setScrollViewListener(ScrollViewListener mListener) {
		this.mListener = mListener;
	}

	private ScrollViewListener mListener;

	public interface ScrollViewListener {
		void onOpen();

		void onClose();
	}

}
