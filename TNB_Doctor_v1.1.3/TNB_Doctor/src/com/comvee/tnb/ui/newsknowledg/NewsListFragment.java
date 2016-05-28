package com.comvee.tnb.ui.newsknowledg;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.ThreadHandler;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.adapter.NewsListAdapter;
import com.comvee.tnb.adapter.NewsViewPagerAdapter;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.model.NewsListInfo;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.ui.book.BookWebActivity;
import com.comvee.tnb.ui.heathknowledge.MyViewPager;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.util.CacheUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.*;

/**
 * 知识列表
 * Created by yujun on 2016/5/3.
 */
class NewsListFragment extends NewsBaseFragment implements XListView.IXListViewListener, AdapterView.OnItemClickListener {

    private int mType;//热点类型
    private XListView mListView;
    private List<NewsListInfo.RowsBean> mNewsList;//列表内容
    private List<NewsListInfo.TurnlistBean> mTurnList;//轮播图
    private View mTurnView;
    private TextView mUpdate;//更新提示
    private NewsListAdapter mAdapter;
    private String startTime;//下拉刷新请求的时间
    private String endTime;//上拉刷新请求的时间
    private LinearLayout lodinggroup;
    private TextView textView;
    private ArrayList<NewsListInfo.RowsBean> mListDate;

    @Override
    public int getViewLayoutId() {
        return R.layout.news_list_fragment;
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (mTurnList != null) {
                ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListView.autoRefresh();
                        ThreadHandler.postUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mListView.stopRefresh();
                            }
                        }, 1500);
                    }
                }, 100);
            }
        }
    };

    private void initViewPager() {
        if (mTurnList == null || mTurnList.size() == 0) {
            return;
        }
        MyViewPager viewPager = (MyViewPager) mTurnView.findViewById(R.id.viewpager);
        NewsViewPagerAdapter viewPagerAdapter = new NewsViewPagerAdapter(mTurnList, (ViewGroup) findViewById(R.id.indicator_layout), getActivity(), true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                LinearLayout layout = (LinearLayout) findViewById(R.id.indicator_layout);
                if (layout != null) {
                    for (int i = 0; i < mTurnList.size(); i++) {
                        try {
                            ImageView img = (ImageView) layout.getChildAt(i);
                            if (img != null) {
                                img.setImageResource(R.drawable.news_shape_solid_gray_circle);
                                if (i == position % mTurnList.size()) {
                                    img.setImageResource(R.drawable.news_shape_solid_while_circle);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(mTurnList.size() * 100);  //让轮播图刚开始就可以向右滑
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);
        mUpdate = (TextView) findViewById(R.id.update_text);
        mListView = (XListView) findViewById(R.id.new_list_view);
        lodinggroup = (LinearLayout) findViewById(R.id.lin_group_of_search);
        imageView = (ImageView) findViewById(R.id.loadingImageView_of_search);
        textView = (TextView) findViewById(R.id.tv_of_search);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mTurnView = inflater.inflate(R.layout.news_turn_list, mListView, false);
    }

    private ImageView imageView;

    private void starSearch() {
        mListView.setVisibility(View.GONE);
        lodinggroup.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.loading_anim);
        AnimationDrawable drawable = (AnimationDrawable) imageView.getDrawable();
        textView.setText("努力加载中...");
        textView.setTextColor(getResources().getColor(R.color.text_color_3));
        drawable.start();
    }

    private void successResoult() {
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                mListView.setVisibility(View.VISIBLE);
                lodinggroup.setVisibility(View.GONE);
            }
        }, 500);
    }

    private void initLoader(int i, final boolean isShow) {

        if (isShow) {
            starSearch();
        }
        ObjectLoader<NewsListInfo> loader = new ObjectLoader<NewsListInfo>();
        if (i == 1 && !TextUtils.isEmpty(startTime)) {
            loader.putPostValue("startTime", startTime);
        } else if (i == 3 && !TextUtils.isEmpty(endTime)) {
            loader.putPostValue("endTime", endTime);
        }
        loader.putPostValue("hotType", valueOf(mType));
        loader.loadBodyObject(NewsListInfo.class, ConfigUrlMrg.NEWS_KNOWLEDGE_LIST, loader.new CallBack() {
                    @Override
                    public void onBodyObjectSuccess(boolean isFromCache, NewsListInfo obj) {
                        super.onBodyObjectSuccess(isFromCache, obj);
                        successResoult();
                        if (obj != null) {
                            mNewsList = obj.rows;
                            mTurnList = obj.turnlist;
                            if (isShow) {
                                CacheUtil.getInstance().putObjectById(valueOf(mType)+"type", (Serializable) obj.rows);
                                CacheUtil.getInstance().putObjectById(valueOf(mType)+"turn", (Serializable) obj.turnlist);
                            }
                            if (mTurnList.size()>0){
                                initViewPager();
                            }
                        }
                    }
                    @Override
                    public boolean onFail(int code) {
                        return super.onFail(code);
                    }
                }
        );
    }

    //获取更新数据请求时的时间
    private void upDataTime() {
        //获取缓存中的数据
        mListDate = CacheUtil.getInstance().getObjectById(valueOf(mType)+"type");
        if (mNewsList != null) {
            if (mNewsList.size() > 0 && mListDate != null) {//有新数据
                startTime = mNewsList.get(0).send_time;
                if (isRefresh) {
                    endTime = mNewsList.get(mNewsList.size() - 1).send_time;
                } else {
                    endTime = mListDate.get(mListDate.size() - 1).send_time;
                }
                if (mNewsList.get(0).hot_spot_id.equals(mListDate.get(0).hot_spot_id)) {
                    updateAnima(0);
                } else {
                    mAdapter.addData(mNewsList, true);
                    CacheUtil.getInstance().putObjectById(valueOf(mType)+"type", (Serializable) mAdapter.getData());
                    updateAnima(mNewsList.size());
                }
            } else {
                updateAnima(mNewsList.size());
            }
        }
    }


    /**
     * ListView初始化化
     */
    private void initListView() {
        if (null != mNewsList && mNewsList.size() > 0) {
            mAdapter = new NewsListAdapter(getContext(), mNewsList);
        } else {
            mAdapter = new NewsListAdapter(getContext(), mListDate);
        }
        mListView.setXListViewListener(this);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        if (null != mTurnList && mTurnList.size() > 0) {
            if (mTurnList.get(0).turnsPlayStatus == 1) {
                mListView.addHeaderView(mTurnView);
            }
        } else {
            mTurnView.setVisibility(View.GONE);
        }
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.setOnItemClickListener(this);
        // 延迟请求数据，避免卡顿
        handler.sendEmptyMessageDelayed(1, 300);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mTurnList.size() > 0) {
            position = position - 2;
        } else {
            position = position - 1;
        }
        if (position >= 0) {
            final NewsListInfo.RowsBean item = (NewsListInfo.RowsBean) mAdapter.getItem(position);
            String url = item.url;
            if (!TextUtils.isEmpty(url)) {
                //每次点击item加一次阅读数
               /* ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String anInt = TNBApplication.getInstance().getSharedPreferences("clickNums", 0).getString("clickNum", "");
                        if (TextUtils.isEmpty(anInt)) {
                            item.setClickNum(item.clickNum);
                        } else {
                            item.setClickNum(anInt);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }, 1500);*/
                ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        item.setClickNum(item.clickNum + 1);
                        mAdapter.notifyDataSetChanged();
                    }
                },500);
                //跳转WEB页面
                BookWebActivity.toWebActivity(getActivity(), item.hotType, null, item.hot_spot_title, url, item.hot_spot_id);
            }
        }
    }

    private boolean isRefresh = true;

    @Override
    public void onRefresh() {
        initLoader(1, false);
        mListView.setEnabled(false);
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                isRefresh = false;
                upDataTime();
                mListView.setEnabled(true);
                mListView.stopRefresh();
                mAdapter.notifyDataSetChanged();
            }
        }, 500);
    }

    @Override
    public boolean onBackPress() {
        return super.onBackPress();
    }
    //更新提示动画
    private void updateAnima(final int i) {
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                mUpdate.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.news_uptade_alpha);
                mUpdate.setText("已为您更新" + i + "条信息");
                mUpdate.startAnimation(animation);
                ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mUpdate.setVisibility(View.GONE);
                    }
                }, 500);
            }
        }, 1000);
    }

    @Override
    public void onLoadMore() {
        if (mNewsList.size() > 0 && isRefresh) {
            endTime = mNewsList.get(mNewsList.size() - 1).send_time;
        }
        isRefresh = true;
        initLoader(3, false);
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.addData(mNewsList, false);
                mAdapter.notifyDataSetChanged();
                mListView.stopLoadMore();
            }
        }, 1000);
    }

    @Override
    public void onFragmentResult(Bundle data) {
        super.onFragmentResult(data);
        updateAnima(0);
    }

    public static NewsListFragment newInstance(int sectionNumber) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putInt("type", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void initData() {
        Bundle argument = getArgument();
        mType = argument.getInt("type");
        mListDate = CacheUtil.getInstance().getObjectById(valueOf(mType)+"type");
        mTurnList = CacheUtil.getInstance().getObjectById(valueOf(mType)+"turn");
        if (mListDate!=null){
            initListView();
            startTime = mListDate.get(0).send_time;
            endTime = mListDate.get(mListDate.size()-1).send_time;
        } else{
            initLoader(2, true);
            ThreadHandler.postUiThread(new Runnable() {
                @Override
                public void run() {
                    initListView();
                }
            }, 800);
        }

    }


}
