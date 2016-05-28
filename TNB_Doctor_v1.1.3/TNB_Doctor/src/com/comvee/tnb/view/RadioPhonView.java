package com.comvee.tnb.view;

import android.content.Context;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.comvee.tnb.R;
import com.comvee.tnb.adapter.ComveePageAdapter;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.tnb.ui.heathknowledge.MyViewPager;
import com.comvee.tnb.widget.RoundImageView;
import com.comvee.util.BundleHelper;

/**
 * Created by yujun on 2016/4/11.
 */
public class RadioPhonView extends RelativeLayout {

    private PhonePagerAdapter mAdapter;
    private MyViewPager mViewPager;
    private RadioAlbumItem mAlbum;
    public RadioPhonView(Context context) {
        super(context);
        init();
    }

    public RadioPhonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadioPhonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();

    }

    private void init() {
        View.inflate(getContext(), R.layout.radio_phone_view, this);
        mAdapter = new PhonePagerAdapter();
        mViewPager = (MyViewPager) findViewById(R.id.viewPager);
       // mAlbum = BundleHelper.getObjecByBundle(RadioAlbumItem.class, dataBundle.getBundle("item"));
       /* mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (null != mHandler) {
                    if (mHandler.hasMessages(0)) {
                        mHandler.removeMessages(0);
                    }
                    if (state == ViewPager.SCROLL_STATE_IDLE) {// viewpager空闲，即图片切换属于停止状态时，发消息。
                        mHandler.sendMessageDelayed(Message.obtain(mHandler, 0), DELAY_MILLIS);
                    }
                }
            }
        });*/
        mViewPager.setAdapter(mAdapter);

    }


    class PhonePagerAdapter extends ComveePageAdapter {

        @Override
        public View getView(int position) {
            View view = View.inflate(getContext(), R.layout.rounded_phone_view, null);
            RoundImageView task_img = (RoundImageView) view.findViewById(R.id.iv_photo);

            	//ImageLoaderUtil.getInstance(BaseApplication.getInstance()).displayImage(info.photo, task_img, ImageLoaderUtil.default_options);



            return null;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }
    }

}
