package com.comvee.tnb.ui.record.diet;

import android.os.Bundle;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.widget.TitleBarView;

public class ExchangeArticle extends BaseFragment {
	private TitleBarView mBarView;

	@Override
	public int getViewLayoutId() {
		return R.layout.exchage_article_frag;
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
		mBarView.setTitle(getString(R.string.exchage_frag_essay_title).toString());
	}
}
