package com.comvee.tnb.widget.pageview;

import com.comvee.ComveeBaseAdapter;
import com.comvee.ThreadHandler;
import com.comvee.tnb.network.BasePageLoader;
import com.comvee.tnb.network.BasePageLoader.OnPageCallBck;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.ui.xlistview.XListView.IXListViewListener;

import java.io.Serializable;
import java.util.ArrayList;

public class PageViewControl {

    public XListView mListView;
    public BasePageLoader mLoader;
    private ComveeBaseAdapter<?> mAdapter;
    private onPageViewListenerAdapter mListener;
    private long startLoadingTime;
    private boolean isAlreadyLoaderCache;

    public PageViewControl(XListView list, Class<? extends Serializable> cls, ComveeBaseAdapter<? extends Serializable> mAdapter, String requestUrl, onPageViewListenerAdapter mListener) {
        bind(list, cls, mAdapter, requestUrl, mListener);
    }

    public void setRowsString(String str) {
        if (mLoader != null)
            mLoader.setRowsString(str);
    }

    public void setNeedCache(boolean b) {
        if (mLoader != null)
            mLoader.setNeedCache(b);
    }


    /**
     * 设置一页多少条数据
     *
     * @param count
     */
    public void setPageRowCount(int count) {
        if (mLoader != null)
            mLoader.setMax(count);
    }

    public void load() {
        if (mLoader != null) {
            startLoadingTime = System.currentTimeMillis();
            mLoader.loadMore();
            if (mListener != null) {
                mListener.onStartLoading();
            }
        }
    }

    public void loadRefresh() {
        if (mLoader != null)
            mLoader.loadRefresh();
    }

    public void putPostValue(String key, String value) {
        if (mLoader != null) {
            mLoader.putPostValue(key, value);
        }
    }

    public void clearCache() {
        if (null != mLoader) {
            mLoader.clearCache();
        }
    }

    public void bind(XListView list, Class<? extends Serializable> cls, ComveeBaseAdapter<? extends Serializable> mAdapter, String requestUrl, onPageViewListenerAdapter mListener) {
        this.mListener = mListener;
        this.mListView = list;
        this.mAdapter = mAdapter;
        mListView.setAdapter(mAdapter);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mLoader = new BasePageLoader(cls);
        mLoader.setUrl(requestUrl);
        mLoader.setCallback(new OnPageCallBck() {

            @Override
            public void onSuccess(final boolean isFromCach, final int page, final ArrayList list) {

                // 如果加载时间小于1秒，就延迟1秒返回
                long duration = System.currentTimeMillis() - startLoadingTime;
                if (isFromCach || duration > 1000 || (page <= 1 && !isAlreadyLoaderCache)) {
                    duration = 0;
                } else {
                    duration = 1000 - duration;
                }
                if (isFromCach) {
                    isAlreadyLoaderCache = true;
                }
                ThreadHandler.postUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (page <= 1) {
                            PageViewControl.this.mAdapter.removeAllData();
                        }
                        if (list != null)
                            PageViewControl.this.mAdapter.addData(list);
                        PageViewControl.this.mAdapter.notifyDataSetChanged();

                        mListView.stopRefresh();
                        mListView.stopLoadMore();

                        PageViewControl.this.mListener.onStopLoading();
                        PageViewControl.this.mListener.onDataCallBack(page, list);
                    }
                }, duration);
            }

            @Override
            public void onLastNonData() {
                mListView.stopRefresh();
                mListView.stopLoadMore();
                mListView.setPullLoadEnable(false);
                PageViewControl.this.mListener.onStopLoading();
            }

            @Override
            public void onFail() {
                mListView.stopRefresh();
                mListView.stopLoadMore();
                PageViewControl.this.mListener.onStopLoading();

            }
        });

        mListView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
//                mLoader.loadRefresh();
                ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListView.stopRefresh();
                        mListView.stopLoadMore();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
//                startLoadingTime = System.currentTimeMillis();
//                mLoader.loadMore();
                ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListView.stopRefresh();
                        mListView.stopLoadMore();
                        startLoadingTime = System.currentTimeMillis();
                        mLoader.loadMore();
                    }
                }, 1000);
            }
        });

    }

    public static class onPageViewListenerAdapter {
        public void onStopLoading() {
        }

        public void onStartLoading() {
        }

        public void onDataCallBack(int page, ArrayList listData) {
        }
    }

}
