package com.comvee.tnb.adapter;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public abstract class ComveePageAdapter extends PagerAdapter{

	private HashMap<Integer, SoftReference<View>> map = new HashMap<Integer, SoftReference<View>>();
	
	@Override
	public final boolean isViewFromObject(View arg0, Object arg1) {
		return arg1.equals(arg0);
	}

	@Override
	public final void destroyItem(ViewGroup container, int position, Object object) {
		View view = null;
		if((view = getCacheView(position))!=null){
			container.removeView(view);
		}
		
	}
	
	@Override
	public final Object instantiateItem(ViewGroup container, int position) {
		View view = null;
		try {
			if((view = getCacheView(position))==null){
                view =  getView(position);
                view = putCacheView(position,view);
            }
			container.addView(view);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}
	
	public abstract View getView(int position);
	
	public View getCacheView(int position){
		if(map.containsKey(position) && map.get(position).get()!=null){
			return map.get(position).get();
		}
		return null;
	}

	public View putCacheView(int position,View view){
		SoftReference<View> soft = new SoftReference<View>(view);
		map.put(position,soft );
		return soft.get();
	}

}
