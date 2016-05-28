package com.comvee.tnb.activity;

import com.comvee.tnb.DrawerMrg;
import com.comvee.frame.FragmentMrg;
import com.comvee.log.ComveeLog;
import com.comvee.tool.UITool;
import com.comvee.util.Log;

public abstract class BaseFragment extends com.comvee.frame.BaseFragment {

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		UITool.closeInputMethodManager(getActivity());
		DrawerMrg.getInstance().closeTouch();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		ComveeLog.onResumeFragment(getClass().getSimpleName());
		Log.e(FragmentMrg.getFragmentToString());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		ComveeLog.onPauseFragment(getClass().getSimpleName());
	}

	@Override
	public void showToast(final String msg) {
		try {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					BaseFragment.super.showToast(msg);
				}
			});
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	@Override
	public void showToast(final int resString) {
		try {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					BaseFragment.super.showToast(resString);
				}
			});
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
