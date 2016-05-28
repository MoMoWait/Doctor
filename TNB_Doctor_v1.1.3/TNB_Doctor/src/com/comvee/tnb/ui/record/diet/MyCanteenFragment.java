package com.comvee.tnb.ui.record.diet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.ui.book.BookIndexRootFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;
/**
 * 我的食堂
 * @author Administrator
 *
 */
public class MyCanteenFragment extends BaseFragment implements OnClickListener {
	private InnerViewpager viewPager;

	@Override
	public int getViewLayoutId() {
		return R.layout.my_canteen_frag;
	}

	@Override
	public void onLaunch(Bundle dataBundle) {
		TitleBarView mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		mBarView.setTitle("我的食堂");
		viewPager = (InnerViewpager) findViewById(R.id.viewpager);

		LinearLayout indicator = (LinearLayout) findViewById(R.id.text_and_indicator);
		viewPager.setAdapterAndIndicator(new CanteenViewpagerAdapter(getChildFragmentManager()), indicator);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		}
	}

	private class CanteenViewpagerAdapter extends FragmentPagerAdapter implements InnerViewpagerAdapterInter {
		Fragment fragment[] = new Fragment[2];

		public CanteenViewpagerAdapter(FragmentManager fm) {
			super(fm);
			fragment[0] = new ThreeMealsFragment();
			String loadurl = ConfigUrlMrg.HOST.substring(0, ConfigUrlMrg.HOST.length() - 6);
			String sessionId = UserMrg.getSessionId(mContext);
			String sessionMemberId = UserMrg.getMemberSessionId(mContext);
			loadurl = String.format(loadurl + "cookbook_list.html?isMyCollected=%s&sessionID=%s&sessionMemberID=%s&type=android", "1", sessionId,
					sessionMemberId);
			fragment[1] = BookIndexRootFragment.newInstance(false, loadurl, "",false);
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragment[arg0];
		}

		@Override
		public int getCount() {
			return fragment.length;
		}

		@Override
		public View getPrimaryItem() {
			return null;
		}

	}
}
