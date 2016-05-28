package com.comvee.tnb.ui.index;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.comvee.BaseApplication;
import com.comvee.tnb.R;
import com.comvee.tool.Log;
import com.comvee.tool.UserMrg;
import com.comvee.ui.remind.TimeRemindUtil;

import java.text.DecimalFormat;

/**
 * 首页闹钟窗口
 */
@SuppressLint("ViewConstructor")
public class IndexClockWindow extends PopupWindow implements OnDismissListener ,OnClickListener,IndexClockMrg.CallBack{
    private TextView counDownTextView;

    private RotateAnimation rotateAnimation;
    private View ivDir;//指针
    public IndexClockWindow() {
        super(LayoutInflater.from(BaseApplication.getInstance()).inflate(R.layout.pop_countdown_clock, null), LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
        setAnimationStyle(R.style.PopupAnimation);
        setOnDismissListener(this);
        View contentView = getContentView();
        contentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        counDownTextView = (TextView) contentView.findViewById(R.id.countdown_textview);
        contentView.findViewById(R.id.close).setOnClickListener(this);
        contentView.findViewById(R.id.cancel).setOnClickListener(this);
        contentView.findViewById(R.id.reset).setOnClickListener(this);
        ivDir = contentView.findViewById(R.id.point);
        onTick(IndexClockMrg.getInstance().getCurTick()+1,false);
        IndexClockMrg.getInstance().addCallBack(this);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                IndexClockMrg.getInstance().removeCallBack(IndexClockWindow.this);
            }
        });
    }

    @Override
    public void onTick(int tick) {
        onTick(tick,true);
    }

    @Override
    public void onCancel() {

    }

    public void onTick(int tick,boolean anim){

        int second = 7200 - tick;
        float start  = second %3600* 6f;
        float end = start + 6f;
        rotateAnimation = new RotateAnimation(start, end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(anim?1000:0);
        rotateAnimation.setFillAfter(true);
        ivDir.startAnimation(rotateAnimation);

        counDownTextView.setText(formatTime(tick));
    }

    @Override
    public void onDismiss() {
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
    }

    private String formatTime(int countingSecond) {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        String hourStr = decimalFormat.format(countingSecond / 3600);
        String minuteStr = decimalFormat.format((countingSecond / 60) % 60);
        String secondStr = decimalFormat.format(countingSecond % 60);
        return hourStr + ":" + minuteStr + ":" + secondStr;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.close:
                dismiss();
                break;
            case R.id.cancel:
                IndexClockMrg.getInstance().cancel();
                dismiss();
                break;
            case R.id.reset:
                rotateAnimation = new RotateAnimation(0, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(0);
                rotateAnimation.setFillAfter(true);
                ivDir.startAnimation(rotateAnimation);
                IndexClockMrg.getInstance().reStart();
                break;
        }
    }


}
