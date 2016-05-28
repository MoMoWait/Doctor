package com.comvee.tnb.ui.user;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.ui.member.MemberChooseNormolFragment;
import com.comvee.tnb.widget.TitleBarView;

public class RegistSuccesFrag extends BaseFragment implements OnClickListener {
    private TitleBarView mBarView;

    public RegistSuccesFrag() {
    }

    public static RegistSuccesFrag newInstance() {
        RegistSuccesFrag fragment = new RegistSuccesFrag();
        return fragment;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.regist_success_frag;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        mBarView.setTitle("注册");
        init();
    }

    private void init() {
        findViewById(R.id.btn_input).setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        toFragment(MemberChooseNormolFragment.newInstance(null), false, true);
    }

    @Override
    public boolean onBackPress() {
        LoginFragment.toFragment(getActivity(), true);
        return true;

    }
}
