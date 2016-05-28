package com.comvee.tnb.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.comvee.BaseApplication;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.model.NewsListInfo;
import com.comvee.tnb.ui.book.BookWebActivity;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UITool;

import java.util.ArrayList;
import java.util.List;

/**
 * 知识页面轮播图
 */
public class NewsViewPagerAdapter extends ComveePageAdapter implements OnClickListener {
    private List<NewsListInfo.TurnlistBean> viewSourceUriList;//轮播图数组
    private ViewGroup indicatorViewGroup;
    private Activity context;
    boolean carrousel;
    final int indicatorPadding = 3;// dp

    public NewsViewPagerAdapter(List<NewsListInfo.TurnlistBean> sourceUriList, ViewGroup indicatorViewGroup, Activity context, boolean carrousel) {
        this.viewSourceUriList = sourceUriList;
        this.indicatorViewGroup = indicatorViewGroup;
        this.context = context;
        this.carrousel = carrousel;
        if (viewSourceUriList == null || viewSourceUriList.size() == 0) {
            return;
        }
        if (indicatorViewGroup != null)
            addIndicator();
    }

    @Override
    public View getView(int position) {
        NewsListInfo.TurnlistBean turnlistBean = viewSourceUriList.get(position % viewSourceUriList.size());
        View view = View.inflate(BaseApplication.getInstance(), R.layout.know_banner_item, null);
        ImageView iv = (ImageView) view.findViewById(R.id.iv_photo);
        TextView tv = (TextView) view.findViewById(R.id.tv_title);
        Glide.with(TNBApplication.getInstance()).load(turnlistBean.turnsPlayUrl).crossFade().into(iv);
        tv.setText(turnlistBean.hot_spot_title);
        view.setTag(turnlistBean);
        view.setOnClickListener(this);
        return view;
    }

    private void addIndicator() {
        indicatorViewGroup.removeAllViews();
        try {
            //int paddingPX = UITool.dip2px(context, indicatorPadding);
            for (int i = 0; i < viewSourceUriList.size(); i++) {
                ImageView imageView = new ImageView(TNBApplication.getInstance());
                if (i == 0) {
                    imageView.setImageResource(R.drawable.news_shape_solid_while_circle);
                } else {
                    imageView.setImageResource(R.drawable.news_shape_solid_gray_circle);
                }
                imageView.setPadding(5, 5, 5, 5);
                indicatorViewGroup.addView(imageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void onClick(View view) {
        NewsListInfo.TurnlistBean model = (NewsListInfo.TurnlistBean) view.getTag();
        //WEB页面跳转
        BookWebActivity.toWebActivity(context, 8, null, model.hot_spot_title, model.url, model.hot_spot_id);
    }
}