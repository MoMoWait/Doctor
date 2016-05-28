package com.comvee.tnb.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import com.comvee.tnb.R;
import com.comvee.tnb.ui.task.CalendarView;

import java.util.Calendar;

/**
 *
 * Created by friendlove-pc on 16/3/21.
 */
public class CalendarWindow extends PopupWindow{

    private CalendarView calendarView = new CalendarView();
    private View mRootView;

    public CalendarWindow(Context cxt) {
        super(cxt);

        this.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        setWidth(-1);
        setHeight(-2);
        setBackgroundDrawable(new BitmapDrawable());
        setAnimationStyle(R.style.PopupAnimation_top);
        setFocusable(true);
    }

    public void setOnChoiceCalendarListener(CalendarView.OnChoiceCalendarListener mListener) {
        calendarView.setOnChoiceCalendarListener(mListener);
    }


    public void show(View view){
        if(mRootView==null){
            mRootView = calendarView.inflate(view.getContext());
            setContentView(mRootView);
        }
        showAtLocation(view, Gravity.TOP,0,0);
    }

    public void setCurrentDate(Calendar calendar){
        calendarView.setShowDate(calendar);
    }



}
