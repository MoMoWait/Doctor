package com.comvee.tnb.ui.more;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;

import com.comvee.ThreadHandler;
import com.comvee.frame.BaseFragmentActivity;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.ComveePageAdapter;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.ui.member.MemberChooseIndexFragemtn;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tnb.widget.PageControlView;

import java.util.ArrayList;

/**
 * 启动引导页
 *
 * @author friendlove-pc
 */
public class LuancherFragment extends BaseFragment implements OnPageChangeListener, OnClickListener {

    private ViewPager mViewPager;
    private ArrayList<View> mViews;
    private PageControlView vIndicator;

    public LuancherFragment() {
    }

    public static LuancherFragment newInstance() {
        LuancherFragment fragment = new LuancherFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_guide;
    }

    private void init() {

        vIndicator = (PageControlView) findViewById(R.id.pageindicator);// 页面指示器
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViews = new ArrayList<View>();
        if (mViews.isEmpty()) {
            mViews.add(View.inflate(getApplicationContext(), R.layout.luancher_layout_0, null));
            mViews.add(View.inflate(getApplicationContext(), R.layout.luancher_layout_1, null));
            mViews.add(View.inflate(getApplicationContext(), R.layout.luancher_layout_2, null));
            mViewPager.setAdapter(new PagerAdapter());
        } else {
            mViewPager.getAdapter().notifyDataSetChanged();
        }
        mViewPager.setOnPageChangeListener(this);
        vIndicator.init(mViews.size(), R.drawable.oval_white, R.drawable.oval_blue);
        bindButton();
        if (mViewPager.getCurrentItem() == 0) {
            ThreadHandler.postUiThread(new Runnable() {
                @Override
                public void run() {
                    // startInAnim0();
                }
            }, 500);
        } else {
            vIndicator.generatePageControl(mViewPager.getCurrentItem());
        }
    }

    private void bindButton() {

        View btn1 = findViewById(R.id.btn_1);
        // btn1.setOnTouchListener(TouchedAnimation.TouchLight);
        btn1.setOnClickListener(this);
        View btn = findViewById(R.id.btn_2);
        // btn.setOnTouchListener(TouchedAnimation.TouchLight);
        btn.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            mViewPager.setAdapter(null);
            mViews.clear();
            // mViews = null;
            mViewPager.removeAllViews();
            // mViewPager = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onBackPress() {
        ((BaseFragmentActivity) getActivity()).tryExit();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1:
                toFragment(LoginFragment.newInstance(false), true, true);
                break;
            case R.id.btn_2:
                ConfigParams.IS_TEST_DATA = true;
                toFragment(MemberChooseIndexFragemtn.newInstance(null), true, true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        try {
            vIndicator.generatePageControl(arg0);
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    private class PagerAdapter extends ComveePageAdapter {

        @Override
        public View getView(int position) {
            return mViews.get(position);
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

    }

}
