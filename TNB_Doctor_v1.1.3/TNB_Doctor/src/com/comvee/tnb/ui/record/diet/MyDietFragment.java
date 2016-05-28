package com.comvee.tnb.ui.record.diet;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.widget.TitleBarView;
/**
 * 我的饮食
 * @author Administrator
 *
 */
public class MyDietFragment extends BaseFragment implements OnClickListener {

	@Override
	public int getViewLayoutId() {
		return R.layout.mydiet_frag;
	}

	@Override
	public void onLaunch(Bundle dataBundle) {
		TitleBarView mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		mBarView.setTitle("我的饮食");
		super.onLaunch(dataBundle);
		findViewById(R.id.doctor_recommend).setOnClickListener(this);
		findViewById(R.id.myrecord).setOnClickListener(this);
		findViewById(R.id.mycanteen).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.doctor_recommend:
			toFragment(DoctorRecomendFragment.class, null, true);
			break;
		case R.id.myrecord:
			toFragment(HistoryDietFragment.class, null, true);
			break;
		case R.id.mycanteen:
			toFragment(MyCanteenFragment.class, null, true);
			break;

		default:
			break;
		}
	}

}
