package com.comvee.tnb.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

public class GridView4Conflict extends GridView {

	private OnTouchInvalidPositionListener mTouchInvalidPosListener;

	public GridView4Conflict(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GridView4Conflict(Context context) {
		super(context);
	}

	public GridView4Conflict(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			return false;
		}
		if (mTouchInvalidPosListener == null) {
			return super.onTouchEvent(ev);
		}
		if (!isEnabled()) {
			// A disabled view that is clickable still consumes the touch
			// events, it just doesn't respond to them.
			return isClickable() || isLongClickable();
		}
		final int motionPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
		if (motionPosition == INVALID_POSITION) {
			super.onTouchEvent(ev);
			return mTouchInvalidPosListener.onTouchInvalidPosition(ev.getActionMasked());
		}
		return super.onTouchEvent(ev);
	}

	public interface OnTouchInvalidPositionListener {
		/**
		 * motionEvent 可使用 MotionEvent.ACTION_DOWN 或者
		 * MotionEvent.ACTION_UP等来按需要进行判断
		 * 
		 * @return 是否要终止事件的路由
		 */
		boolean onTouchInvalidPosition(int motionEvent);
	}

	/**
	 * 点击空白区域时的响应和处理接口
	 */
	public void setOnTouchInvalidPositionListener(OnTouchInvalidPositionListener listener) {
		mTouchInvalidPosListener = listener;
	}
}