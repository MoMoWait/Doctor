package com.comvee.tnb.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MainFragmentPagerAdapter extends FragmentStatePagerAdapter {

	private ArrayList<Fragment> fragments;

	public MainFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public MainFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
	 */
	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		return fragments.size();
	}

	
//	@Override
//	public long getItemId(int position) {
//		return super.getItemId(position);
//	}
	
	@Override  
	public int getItemPosition(Object object) {  
//	    return super.getItemPosition(object);
		return POSITION_NONE;
	}  

}
