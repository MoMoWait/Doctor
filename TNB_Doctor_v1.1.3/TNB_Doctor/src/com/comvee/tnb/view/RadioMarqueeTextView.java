package com.comvee.tnb.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by yujun on 2016/3/31.
 */
public class RadioMarqueeTextView extends TextView {
    public RadioMarqueeTextView(Context context) {
        super(context);
    }

    public RadioMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadioMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
