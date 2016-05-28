package com.comvee.tnb.ui.newsknowledg;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.DragAdapter;
import com.comvee.tnb.adapter.NewsFragmentPagerAdapter;
import com.comvee.tnb.adapter.NewsTabHorizonAdapter;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.model.NewsKnowlendgeTest;
import com.comvee.tnb.model.NewsTabColumnInfo;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.ui.heathknowledge.SearchFragment;
import com.comvee.tnb.widget.HorizontalListView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.util.CacheUtil;

import java.util.ArrayList;

/**
 * 新版知识首页
 * Created by yujun on 2016/5/2.
 */
public class NewsKnowledgeFragment extends BaseFragment implements View.OnClickListener {
    private HorizontalListView mHorizontalListView;//Tab滚动条
    private NewsTabHorizonAdapter mTabAdapter;
    private ViewPager mViewPager;
    private ArrayList<Fragment> mPagerFragment = new ArrayList<Fragment>();//显示Fragment集合
    private String[] mTab;//栏目标题数组
    private int[] mType;
    private NewsKnowlendgeTest mNews;
    private ArrayList<ChannelItem> userChannelList = new ArrayList<ChannelItem>(); //用户栏目列表
    private PopupWindow popView;
    private View mView;
    private int tabIndex;
    private String tabString;

    @Override
    public int getViewLayoutId() {
        return R.layout.news_knowledge_fragment;
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);
        initBarView();//标题栏初始化
        initTabColumn();//知识标题的view
        initView();//view的初始化
    }

    public static void toFragment(FragmentActivity fragment) {
        FragmentMrg.toFragment(fragment, NewsKnowledgeFragment.class, null, false);
    }

    private void initView() {
        findViewById(R.id.add_item).setOnClickListener(this);
    }

    private void initTabColumn() {
        mHorizontalListView = (HorizontalListView) findViewById(R.id.time_bucket);
        ObjectLoader<NewsTabColumnInfo> loader = new ObjectLoader<NewsTabColumnInfo>();
        loader.loadBodyArray(NewsTabColumnInfo.class, ConfigUrlMrg.NEWS_KNOWLEDGE, loader.new CallBack() {
            @Override
            public void onBodyArraySuccess(boolean isFromCache, ArrayList<NewsTabColumnInfo> obj) {
                super.onBodyArraySuccess(isFromCache, obj);
                mNews = new NewsKnowlendgeTest();
                mNews.mNewsTabColumnInfo = obj;
                mTab = new String[obj.size()];//标题
                mType = new int[obj.size()];//热点类型
                for (int i = 0; i < obj.size(); i++) {
                    mTab[i] = obj.get(i).hotTypeName;
                    Log.d("dddd",mType[i]+"");
                    mType[i] = obj.get(i).hotType;
                }
                if (mTab != null)
                    setTabAdapter(mTab);//设置Tab栏目'
            }
        });
    }

    private void initBarView() {
        TitleBarView mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        mBarView.setTitle("知识");
        mBarView.setRightButton(R.drawable.jkzs_03, this);//搜索按钮
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TitleBarView.ID_RIGHT_BUTTON:
                //咨询搜索界面
                if (popView != null && popView.isShowing()) {
                    popView.dismiss();
                }
                SearchFragment.toFragment(getActivity());
                break;
            case R.id.add_item:
                ImageView mImageView = (ImageView) findViewById(R.id.img_add);
                Animation animation = new RotateAnimation(0, 135, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(500);
                animation.setFillAfter(false);
                mImageView.startAnimation(animation);
                //旋转动画结束后弹出POP
                ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            operatePop();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 500);
                break;
            case R.id.finish:
                DragGrid.isOperate = false;
                mView.findViewById(R.id.sort).setVisibility(View.VISIBLE); //显示“排序”
                mView.findViewById(R.id.finish).setVisibility(View.GONE); //隐藏“完成”
                StringBuilder sb = new StringBuilder();
                mTab = new String[mTab.length];
                mType = new int[mType.length];
                for (int i = 0; i < userChannelList.size(); i++) {
                    sb.append(userChannelList.get(i).type).append(",");
                    mTab[i] = String.valueOf(userChannelList.get(i).name);
                    if (userChannelList.get(i).name.equals(tabString)) {
                        tabIndex = i;//标记排序之前的type
                    }
                    mType[i] = userChannelList.get(i).type;
                }
                String str = sb.toString();
                if (!TextUtils.isEmpty(str)) {
                    str = str.substring(0, str.length() - 1);
                }
                commit(str);
                if (0 < mType.length) {
                    mViewPager.removeAllViewsInLayout();
                    CacheUtil.getInstance().putObjectById("news_tab", mTab);
                    setTabAdapter(mTab);
                }
                break;
            case R.id.sort:
                mView.findViewById(R.id.sort).setVisibility(View.GONE); //隐藏“排序”
                mView.findViewById(R.id.finish).setVisibility(View.VISIBLE); //显示“完成”
                DragGrid.isOperate = true; //可拖动开关
                break;
            case R.id.cancel:
                popView.dismiss();
                DragGrid.isOperate = false;
                userChannelList.clear();  // 取消POP后清除列表
                break;
            default:
                break;
        }
    }

    /**
     * PopupWindow 界面操作
     */
    private void operatePop() {
        mView = LayoutInflater.from(getContext()).inflate(R.layout.news_title_frag, null);
        mView.setAlpha(0.98f);
        popView = new PopupWindow(mView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popView.showAsDropDown(findViewById(R.id.main_titlebar_view), 0, 0);
        if (userChannelList.size() == 0) {
            for (int i = 0; i < mNews.mNewsTabColumnInfo.size(); i++) {
                ChannelItem item = new ChannelItem();
                item.name = mNews.mNewsTabColumnInfo.get(i).hotTypeName;
                item.type = mNews.mNewsTabColumnInfo.get(i).hotType;
                userChannelList.add(item);
            }
        }
        DragGrid mDragGrid = (DragGrid) mView.findViewById(R.id.gridview);
        DragAdapter mDragAdapter = new DragAdapter(getContext(), userChannelList);
        mDragGrid.setAdapter(mDragAdapter);
        mView.findViewById(R.id.cancel).setOnClickListener(this);
        mView.findViewById(R.id.finish).setOnClickListener(this);
        mView.findViewById(R.id.sort).setOnClickListener(this);
        popView.setAnimationStyle(R.style.PopupAnimation);
        popView.setFocusable(true);
        popView.setBackgroundDrawable(new BitmapDrawable());
    }
    /**
     * 排序后栏目传回接口
     */
    private void commit(String str) {
        ObjectLoader<String> load = new ObjectLoader<String>();
        load.setNeedCache(false);
        load.setThreadId(ObjectLoader.POOL_THREAD_2);
        load.putPostValue("memberHotSpotstr", str);
        load.loadBodyObject(String.class, ConfigUrlMrg.NEWS_TITLE_LIST, load.new CallBack() {
        });
    }
    private void setTabAdapter(String[] tabs) {
        if (tabIndex == 0) {
            onTabIndex(0);
        } else {
            onTabIndex(tabIndex);
        }
        mTabAdapter = new NewsTabHorizonAdapter(getApplicationContext(), tabs, mHorizontalListView);
        mHorizontalListView.setAdapter(mTabAdapter);
        mHorizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tabIndex = mType[i];
                tabString = mTab[i];
                onTabIndex(i);//给Tab设置背景
            }
        });
        initViewPager(mType);//
        mTabAdapter.notifyDataSetChanged();
    }
    private void onTabIndex(final int i) {
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(i);
                mTabAdapter.setSelectIndex(i);
                mHorizontalListView.setSelection(i);
            }
        }, 100);
    }
    /**
     * 知识fragment页面加载
     */
    private void initViewPager(final int[] types) {
        mPagerFragment.clear();
        mViewPager = (ViewPager) findViewById(R.id.news_knowledge_viewpager);
        NewsFragmentPagerAdapter newsPagerAdapter = new NewsFragmentPagerAdapter(getFragmentManager(), mPagerFragment);
        for (int i = 0; i < mTab.length; i++) {
            //实例化Viewpager的Fragment
            NewsListFragment newsListFragment = NewsListFragment.newInstance(types[i]);
            mPagerFragment.add(newsListFragment);
        }
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(newsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }
            @Override
            public void onPageSelected(int position) {
                tabIndex = mType[position];
                tabString = mTab[position];
                mViewPager.setCurrentItem(position);
                mTabAdapter.setSelectIndex(position);
                mHorizontalListView.setSelection(position);
            }
            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }
    @Override
    public boolean onBackPress() {
        if (popView != null && popView.isShowing()) {
            popView.dismiss();
            DragGrid.isOperate = false;
            return true;
        } else {
            return false;
        }
    }
}
