package com.comvee.tnb.ui.pharmacy;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.ui.pharmacy.MedicinalListFragment.OnSelectDrugListener;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.util.BundleHelper;

public class MedicinalFragment extends BaseFragment implements OnClickListener, OnSelectDrugListener {
	private ViewPager viewpager;
	private OnSelectDrugListener onSelectDrug;
	private TitleBarView mBarView;

	public MedicinalFragment(OnSelectDrugListener selectDrug) {
		this.onSelectDrug = selectDrug;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.medicinal_fragment;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		mBarView.setTitle("药品名称");
		mBarView.setRightButton(R.drawable.jkzs_03, this);
		setupViewpager();
	}
	private List<Fragment> fragments;

	private void setupViewpager() {
		viewpager = (ViewPager) findViewById(R.id.viewpager);
		MedicinalListFragment medicinalListFragment1 = new MedicinalListFragment(ConfigParams.MEDICINAL_TYPE[0]);// 降糖
		MedicinalListFragment medicinalListFragment2 = new MedicinalListFragment(ConfigParams.MEDICINAL_TYPE[3]);// 胰岛
		MedicinalListFragment medicinalListFragment3 = new MedicinalListFragment(ConfigParams.MEDICINAL_TYPE[2]);// 降压
		MedicinalListFragment medicinalListFragment4 = new MedicinalListFragment(ConfigParams.MEDICINAL_TYPE[1]);// 降脂
		medicinalListFragment1.setOnSelectDrugListener(this);
		medicinalListFragment2.setOnSelectDrugListener(this);
		medicinalListFragment3.setOnSelectDrugListener(this);
		medicinalListFragment4.setOnSelectDrugListener(this);
		fragments = new ArrayList<Fragment>();
		fragments.add(medicinalListFragment1);
		fragments.add(medicinalListFragment2);
		fragments.add(medicinalListFragment3);
		fragments.add(medicinalListFragment4);
		viewpager.setAdapter(new MedicinalFragmentAdapter(getChildFragmentManager(), fragments));
		viewpager.setOnPageChangeListener(new MedicinalPageChangeListener(((LinearLayout) findViewById(R.id.text_and_indicator))));
		viewpager.setOffscreenPageLimit(4);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mRoot = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//		if(fragments!=null){
//			FragmentTransaction tran = getActivity().getSupportFragmentManager().beginTransaction();
//			for(Fragment f : fragments){
//				tran.remove(f);
//			}
//			tran.commit();
//		}
	}

	@Override
	public void onFragmentResult(Bundle data) {
		super.onFragmentResult(data);
		if(data!=null){
			FragmentMrg.toBack(getActivity(),data);
//			if (onSelectDrug != null) {
//				MedicinalModel info = BundleHelper.getSerializableByBundle(data);
//				onSelectDrug.selectDrug(info);
//			}

		}

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case TitleBarView.ID_RIGHT_BUTTON:
			toFragment(SearchFragment.newInstance(null), true, false);
			break;

		default:
			break;
		}
	}

	private class MedicinalFragmentAdapter extends FragmentPagerAdapter {
		private List<Fragment> fragments;

		public MedicinalFragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		public MedicinalFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			return fragments.get(pos);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

	}

	private class MedicinalPageChangeListener implements OnPageChangeListener {
		private ViewGroup[] indicatorArray;

		public MedicinalPageChangeListener(LinearLayout indicators) {
			indicatorArray = new ViewGroup[indicators.getChildCount()];
			for (int i = 0; i < indicatorArray.length; i++) {
				final int currentIndex = i;
				indicatorArray[i] = (ViewGroup) indicators.getChildAt(i);
				indicatorArray[i].setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						viewpager.setCurrentItem(currentIndex);
					}
				});
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int index) {
			for (int i = 0; i < indicatorArray.length; i++) {
				ViewGroup vg = indicatorArray[i];
				TextView name = (TextView) vg.getChildAt(0);
				View indicator = vg.getChildAt(1);
				if (i == index) {
					name.setTextColor(getResources().getColor(R.color.theme_color_green));
					indicator.setVisibility(View.VISIBLE);
				} else {
					name.setTextColor(Color.parseColor("#333333"));
					indicator.setVisibility(View.INVISIBLE);
				}
			}
		}
	}

	@Override
	public void selectDrug(MedicinalModel model) {
		FragmentMrg.toBack(getActivity());
		if (onSelectDrug != null) {
			onSelectDrug.selectDrug(model);
		}
	}


}
