/**
 * 
 */
package com.comvee.tnb.ui.follow;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.ui.ask.AskQuestionFragment;
import com.comvee.tnb.widget.TitleBarView;

/**
 * 随访结束完成界面
 * 
 * @author SZM
 * 
 */
public class FollowFinishFragment extends BaseFragment implements OnClickListener {
	private TitleBarView mBarView;

	@Override
	public int getViewLayoutId() {
		return R.layout.follow_finish_fragment;
	}

	public static FollowFinishFragment newInstance() {
		return new FollowFinishFragment();
	}

	public FollowFinishFragment() {
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		init();
	}

	private void init() {
		findViewById(R.id.follow_finish_return).setOnClickListener(this);
		AskQuestionFragment.isUpdata = true;
	}
@Override
public boolean onBackPress() {
	FragmentMrg.toBack(getActivity(),null,3);
	return true;
}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.follow_finish_return:
			// ((MainActivity) getActivity()).toIndexFragment();

			FragmentMrg.toBack(getActivity(),null,3);
			break;

		default:
			break;
		}
	}
}
