package com.comvee.tnb.dialog;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.model.PushInfo;

/**
 * Created by yujun on 2016/5/8.
 */
public class CustomFragmentDialog extends BaseDialogFragment {

    protected View mRoot;

    protected void onLaunch(View mRoot) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));// 设置背景透明
        View view = inflater.inflate(R.layout.dialog_layout, null);
        mRoot = view;

        TextView tvTitle = (TextView) view.findViewById(R.id.tv_msg);
        final Button btnOk = (Button) view.findViewById(R.id.btn_ok);
        final Button btnNo = (Button) view.findViewById(R.id.btn_no);
        btnNo.setVisibility(View.VISIBLE);
        btnOk.setVisibility(View.VISIBLE);
        tvTitle.setText(mTitle);
        btnOk.setText(mOkBtn);
        btnNo.setText(mNoBtn);

        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNoLis != null) {
                    mNoLis.onClick(btnNo);
                }
                getDialog().cancel();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOkLis != null) {
                    mOkLis.onClick(btnOk);
                }
                getDialog().cancel();
            }
        });
        onLaunch(view);
        return view;
    }

    private String mTitle, mOkBtn, mNoBtn;
    private View.OnClickListener mOkLis, mNoLis;

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setPositiveButton(String label, View.OnClickListener lis) {
        this.mOkBtn = label;
        this.mOkLis = lis;
    }

    public void setNegativeButton(String label, View.OnClickListener lis) {
        this.mNoBtn = label;
        this.mNoLis = lis;
    }

    @Override
    public void show(android.support.v4.app.FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }
}

