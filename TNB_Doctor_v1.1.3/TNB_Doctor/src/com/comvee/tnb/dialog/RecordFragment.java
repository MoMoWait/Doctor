package com.comvee.tnb.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.ui.log.RecordCalendarFragment;
import com.comvee.tnb.ui.record.RecordBmiInputFragment;
import com.comvee.tnb.ui.record.RecordHemoglobinFragment;
import com.comvee.tnb.ui.record.RecordPressBloodInputFragment;
import com.comvee.tnb.ui.record.RecordSugarInputNewFrag;
import com.comvee.tnb.ui.record.laboratory.RecordLaboratoryFragment;
import com.comvee.tool.UITool;

public class RecordFragment extends DialogFragment implements OnClickListener {
    private View recordView;
    private ViewGroup itemViews;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.TransparentDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.record_main_pop_layout, container);
        recordView = contentView.findViewById(R.id.center_music_window_close);
        recordView.setOnClickListener(this);
        itemViews = (ViewGroup) contentView.findViewById(R.id.item_layout);
        rotationImg();
        showAnimation(itemViews);
        getDialog().getWindow().setLayout(-1, -1);
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
                closeAnimation(itemViews);
                clostImg();
                recordView.setOnClickListener(null);
                return false;
            }
        });
        return contentView;
    }

    /**
     * 选项按钮显示动画
     *
     * @param layout
     */
    private void showAnimation(ViewGroup layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            final View child = layout.getChildAt(i);
            child.setVisibility(View.INVISIBLE);
            child.setOnClickListener(this);
            ThreadHandler.postUiThread(new Runnable() {

                @Override
                public void run() {
                    child.setVisibility(View.VISIBLE);
                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", UITool.getDisplayHeight(TNBApplication.getInstance()) / 2,
                            0);
                    fadeAnim.setDuration(300);
                    fadeAnim.setInterpolator(new OvershootInterpolator());
                    // KickBackAnimator kickAnimator = new KickBackAnimator();
                    // kickAnimator.setDuration(150);
                    // fadeAnim.setEvaluator(kickAnimator);
                    fadeAnim.start();
                }
            }, i * 50);
        }
    }

    /**
     * 旋转“+”按钮 变成“x”
     */
    private void rotationImg() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(recordView, "rotation", 0f, 135f);
        animator.setDuration(400);
        animator.start();
    }

    /**
     * 旋转“x”按钮 变成“+”
     */
    private void clostImg() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(recordView, "rotation", 135f, 0f);
        animator.setDuration(400);
        animator.start();
    }

    /**
     * 选项按钮退出动画
     *
     * @param layout
     */
    private void closeAnimation(ViewGroup layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            final View child = layout.getChildAt(i);
            child.setOnClickListener(null);
            ThreadHandler.postUiThread(new Runnable() {

                @Override
                public void run() {
                    child.setVisibility(View.VISIBLE);
                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 0,
                            UITool.getDisplayHeight(TNBApplication.getInstance()) / 2);
                    fadeAnim.setDuration(300);
                    fadeAnim.setInterpolator(new AnticipateInterpolator());
                    fadeAnim.start();
                    fadeAnim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            child.setVisibility(View.INVISIBLE);
                        }
                    });

                }
            }, (layout.getChildCount() - i - 1) * 50);
            // 当第一个选项的动画结束时，通知popuwindow消失
            if (child.getId() == R.id.btn_sugger) {
                ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RecordFragment.super.dismiss();
                    }
                }, layout.getChildCount() * 50 + 300);
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.center_music_window_close:
                dismiss();
                break;
            case R.id.btn_sugger:
                dismiss();
                FragmentMrg.toFragment(getActivity(), RecordSugarInputNewFrag.class, null, true);
                break;
            case R.id.btn_press_blood:
                dismiss();
                FragmentMrg.toFragment(getActivity(), RecordPressBloodInputFragment.newInstance(null), true, true);
                break;
            case R.id.btn_laboratory:
                dismiss();
                FragmentMrg.toFragment(getActivity(), RecordLaboratoryFragment.class, null, true);
                break;
            case R.id.btn_bmi:
                dismiss();
                FragmentMrg.toFragment(getActivity(), RecordBmiInputFragment.newInstance(null), true, true);
                break;
            case R.id.btn_hemoglobin:
                dismiss();
                FragmentMrg.toFragment(getActivity(), RecordHemoglobinFragment.class, null, true);
                break;
            case R.id.btn_health_record:
                dismiss();
                FragmentMrg.toFragment(getActivity(), RecordCalendarFragment.newInstance(), true, true);
                break;
            default:
                break;
        }

    }

    @Override
    public void dismiss() {
        closeAnimation(itemViews);
        clostImg();
        recordView.setOnClickListener(null);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

}
