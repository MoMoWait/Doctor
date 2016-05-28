package com.comvee.tnb.radio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

public class SlideItemLayout extends FrameLayout {
	private ViewDragHelper viewpagerDragHelper;
	private View contentView, actionView;
	private ViewParent parentView;
	private int actionViewWidth;

	private Point contentViewOriginalPos = new Point();// 内容View的初始位置
	private Point actionViewOriginalPos = new Point();// 操作视图的初始位置

	private float mDownX, mDownY;

	@SuppressLint("NewApi")
	public SlideItemLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		viewpagerDragHelper = ViewDragHelper.create(this, 1, new MCallBack());
	}

	public SlideItemLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlideItemLayout(Context context) {
		this(context, null);
	}

	public SlideItemLayout(Context context, int contentId, int actionId) {
		this(context);
		contentView = LayoutInflater.from(context).inflate(contentId, this, false);
		contentView.setId(1000);
		actionView = LayoutInflater.from(context).inflate(actionId, this, false);
		addView(contentView);
		addView(actionView);
		setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
	}

	private class MCallBack extends ViewDragHelper.Callback {
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return true;
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			int leftBound;
			int newLeft;
			if (child == contentView) {
				leftBound = -actionViewWidth;
				newLeft = Math.min(Math.max(left, leftBound), 0);
				return newLeft;
			} else if (child == actionView) {
				leftBound = getWidth() - actionViewWidth;
				newLeft = Math.min(Math.max(leftBound, left), getWidth());
				return newLeft;
			}
			return super.clampViewPositionHorizontal(child, left, dx);
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
			if (changedView == contentView) {
				actionView.offsetLeftAndRight(dx);
			} else if (changedView == actionView) {
				contentView.offsetLeftAndRight(dx);
			}
			// 要调用一下invalidate方法，不然有时候offsetLeftAndRight无效。
			invalidate();
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			if (releasedChild == contentView) {
				if (releasedChild.getLeft() > -actionViewWidth / 2) {
					viewpagerDragHelper.settleCapturedViewAt(contentViewOriginalPos.x, contentViewOriginalPos.y);
				} else {
					viewpagerDragHelper.settleCapturedViewAt(-actionViewWidth, contentView.getTop());
				}
			} else if (releasedChild == actionView) {
				if (releasedChild.getLeft() > getWidth() - actionViewWidth / 2) {
					viewpagerDragHelper.settleCapturedViewAt(actionViewOriginalPos.x, actionViewOriginalPos.y);
				} else {
					viewpagerDragHelper.settleCapturedViewAt(getWidth() - actionViewWidth, actionViewOriginalPos.y);
				}
			}
			invalidate();
		}

		@Override
		public int getViewHorizontalDragRange(View child) {
			return Math.max(1, getMeasuredWidth() - child.getMeasuredWidth());
		}

		@Override
		public int getViewVerticalDragRange(View child) {
			return Math.max(1, getMeasuredHeight() - child.getMeasuredHeight());
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean b = viewpagerDragHelper.shouldInterceptTouchEvent(ev);
		return b;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		actionViewWidth = actionView.getWidth();
		// 一开始必须让它在最右边
		actionView.layout(getWidth(), 0, actionViewWidth + getWidth(), actionView.getHeight());
		parentView = getParent();
		contentViewOriginalPos.x = contentView.getLeft();
		contentViewOriginalPos.y = contentView.getTop();
		actionViewOriginalPos.x = actionView.getLeft();
		actionViewOriginalPos.y = actionView.getTop();
	}

	@Override
	public void computeScroll() {
		if (viewpagerDragHelper.continueSettling(true)) {
			invalidate();
		}
	}

	/**
	 * 需要parentView来根据XY轴移动差距对比设置事件拦截
	 * 
	 * @param event
	 * @return
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownX = event.getX();
			mDownY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if (Math.abs(event.getX() - mDownX) > Math.abs(event.getY() - mDownY)) {
				parentView.requestDisallowInterceptTouchEvent(true);
			} else {
				parentView.requestDisallowInterceptTouchEvent(false);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			parentView.requestDisallowInterceptTouchEvent(false);
			break;
		default:
			break;
		}
		viewpagerDragHelper.processTouchEvent(event);
		return true;
	}

	public View getContentView() {
		return contentView;
	}
}
