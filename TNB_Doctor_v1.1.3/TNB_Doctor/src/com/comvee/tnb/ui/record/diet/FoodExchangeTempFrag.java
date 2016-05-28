package com.comvee.tnb.ui.record.diet;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.widget.TitleBarView;

public class FoodExchangeTempFrag extends BaseFragment implements OnClickListener {
	private TitleBarView mBarView;

	@Override
	public int getViewLayoutId() {
		return R.layout.food_exchange_temp_frag;
	}

	@Override
	public void onLaunch(Bundle dataBundle) {
		// TODO Auto-generated method stub
		super.onLaunch(dataBundle);
		init();
	}

	private void init() {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		mBarView.setTitle(getString(R.string.exchage_frag_essay_title));
		findViewById(R.id.create_food_exchange).setOnClickListener(this);
		findViewById(R.id.img_msg).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.create_food_exchange:
			SwapDietFragment.toFragment(getActivity(),  false);
			break;
		case R.id.img_msg:
			toFragment(ExchangeArticle.class, null, true);
			break;
		default:
			break;
		}
	}
}
