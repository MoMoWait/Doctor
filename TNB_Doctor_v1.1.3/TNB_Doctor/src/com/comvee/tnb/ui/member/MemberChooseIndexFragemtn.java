package com.comvee.tnb.ui.member;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.model.MemberInfo;

/**
 * 用户注册后资料填写过渡页
 * 
 * @author Administrator
 * 
 */
public class MemberChooseIndexFragemtn extends BaseFragment implements OnClickListener {
	private MemberInfo mInfo;

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_first1;
	}

	public MemberChooseIndexFragemtn() {
	}

	public static MemberChooseIndexFragemtn newInstance(MemberInfo mInfo) {
		MemberChooseIndexFragemtn fragment = new MemberChooseIndexFragemtn();
		fragment.setmInfo(mInfo);
		return fragment;
	}

	private void setmInfo(MemberInfo mInfo) {
		this.mInfo = mInfo;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		init();
	}

	private void init() {
		if (mInfo == null) {
			mInfo = new MemberInfo();
		}
		findViewById(R.id.btn_next).setOnClickListener(this);
		findViewById(R.id.img_back).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_next:
			MemberChooseNormolFragment member = MemberChooseNormolFragment.newInstance(mInfo);
			member.setShowSex(true);
			toFragment(member, true, true);
			break;
		case R.id.img_back:
			FragmentMrg.toBack(getActivity());
			break;
		default:
			break;
		}
	}

}
