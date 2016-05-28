package com.comvee.tnb.network;


import com.comvee.ThreadHandler;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tool.Log;
import com.comvee.tool.UserMrg;
import com.comvee.util.CacheUtil;
import com.comvee.util.JsonHelper;
import com.comvee.util.MD5Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//////////////////////DEMO////////////////////
//mLoader = new BasePageLoader<SportRecord>();
//mLoader.setUrl(ConfigUrlMrg.SPORT_LIST);
//mLoader.setClass(SportRecord.class);
//mLoader.setCallback(new OnPageCallBck() {
//	@Override
//	public void onSuccess(boolean isFromCach, int page, ArrayList list) {
//		if(page==1){
//			listviewDatas.removeAll(listviewDatas);
//		}
//		cancelProgressDialog();
//		listviewDatas.addAll(list);
//		listviewAdapter.notifyDataSetChanged();
//		listview.stopLoadMore();
//	}
//
//	@Override
//	public void onFail() {
//		cancelProgressDialog();
//	}
//
//	@Override
//	public void onLastNonData() {
//		cancelProgressDialog();
//	}
//});
//////////////////////DEMO////////////////////

/**
 * 加载更多请求的 基类
 *
 * @author friendlove-pc
 */
public class BasePageLoader extends TnbBaseNetwork {

    private String cacheKey;
    private boolean isNeedCache = true;
    private int mPage = 1;
    private int mTotalPage = -1;
    private int mMax = 10;
    private OnPageCallBck mCallback;
    private Class<?> mClass;
    private String mUrl;
    private String mOldFileMD5;
    private String rowsString="rows";//json解析,因为协议不同，又得时候rows，有的时候list，所以开发让外面定制
    public void setRowsString(String str){
        rowsString = str;
    }

    public BasePageLoader(Class<?> mClass) {
        this.mClass = mClass;
    }

    public void clearCache() {
        CacheUtil.getInstance().putObjectById(cacheKey, null);
    }

    public void setMax(int max) {
        this.mMax = max;
    }

    protected void setPage(int page) {
        mPage = page;
    }

    public void setNeedCache(boolean isNeedCache){
        this.isNeedCache = isNeedCache;
    }

    public void loadRefresh() {
        if (isloading()) {
            return;
        }
        mTotalPage = -1;
        mPage = 1;
        putPostValue("page", String.valueOf(mPage));
        putPostValue("rows", String.valueOf(mMax));
        start();
    }

    public void loadMore() {
        // 判断是否是最后一页了
        if (mTotalPage == mPage - 1) {
            try {
                mCallback.onFail();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        if (isloading()) {
            return;
        }
        putPostValue("page", String.valueOf(mPage));
        putPostValue("rows", String.valueOf(mMax));
        start();
    }

    @Override
    protected Object parseResponseJsonData(JSONObject resData) {
        ArrayList<?> list = null;
        try {
            JSONObject body = resData.getJSONObject("body");
            mPage = body.getJSONObject("pager").optInt("currentPage", 0) + 1;
            mTotalPage = body.getJSONObject("pager").optInt("totalPages", -1);

            JSONArray array = body.getJSONArray(rowsString);
            Class<?> t = mClass;
            list = (ArrayList<?>) JsonHelper.getListByJsonArray(t, array);

            if (isNeedCache) {
                if (mPage - 1 == 1) {// 只缓存第一页
                    CacheUtil.getInstance().putObjectById(cacheKey, list);
                    String newFileMD5 = MD5Util.getFileMD5String(CacheUtil.getInstance().getFile(cacheKey));
                    if (newFileMD5.equals(mOldFileMD5)) {
                        Log.e("网络数据 == 缓存数据 >>>>  不刷新页面");
                        return null;
                    }
                } else if (mTotalPage == 0) {// 无数据的情况下，情况缓存
                    CacheUtil.getInstance().putObjectById(cacheKey, null);
                }
            }

            if (mCallback != null && mTotalPage == mPage - 1) {
                ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onLastNonData();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onDoInMainThread(int status, Object obj) {
        if (null != mCallback) {
            if (status == SUCCESS) {
                if(obj!=null)
                mCallback.onSuccess(false, mPage - 1, (ArrayList<?>) obj);
            } else {
                mCallback.onFail();
            }
        }
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
        this.cacheKey = UserMrg.getCacheKey(url);
    }

    public void setCallback(OnPageCallBck callback) {
        this.mCallback = callback;
    }

    public void setClass(Class<?> c) {
        mClass = c;
    }

    @Override
    protected boolean onStartReady() {
        if (isNeedCache && mPage == 1) {
            Object cache = null;
            try {
                if (null != mCallback && (cache = CacheUtil.getInstance().getObjectById(cacheKey)) != null) {
                    mOldFileMD5 = MD5Util.getFileMD5String(CacheUtil.getInstance().getFile(cacheKey));
                    final ArrayList<?> list = (ArrayList<?>) cache;
                    ThreadHandler.postUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCallback.onSuccess(true, mPage, list);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onStartReady();
    }

    public interface OnPageCallBck {
        /**
         * @param isFromCach 是否是缓存数据
         * @param page       第几页
         * @param list       数据
         */
        void onSuccess(boolean isFromCach, int page, ArrayList list);

        /**
         * 请求出错
         */
        void onFail();

        /**
         * 加载到最后一页，无数据
         */
        void onLastNonData();
    }
}
