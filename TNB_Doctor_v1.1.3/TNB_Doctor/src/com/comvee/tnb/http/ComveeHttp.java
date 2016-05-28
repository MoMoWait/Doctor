package com.comvee.tnb.http;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.comvee.BaseApplication;
import com.comvee.ThreadHandler;
import com.comvee.cache.CacheMrg;
import com.comvee.http.KWHttpRequest;
import com.comvee.log.ComveeLog;
import com.comvee.network.NetStatusManager;
import com.comvee.tool.UserMrg;

public class ComveeHttp extends KWHttpRequest {

    // private JSONObject mRootObject = new JSONObject();
    public final static int ERRCODE_NETWORK = 1004;
    private Context mContext;
    private String mCachKey;
    private boolean mNeedGetCach;
    private OnHttpListener listener;

    public ComveeHttp(Context cxt, String url) {
        super(cxt, url);
        if (DEBUG) {
            Log.v("http", url);
        }

        this.mContext = cxt.getApplicationContext();
        String join_id = "123";
        String req_num = "";
        String valid = "";
        setPostValueForKey("join_id", join_id);
        setPostValueForKey("req_num", req_num);
        setPostValueForKey("valid", valid);
        setPostValueForKey("dev", ComveeLog.getDeviceId());
        setPostValueForKey("dev_type", "android");
        setPostValueForKey("sessionMemberID", UserMrg.getMemberSessionId(mContext));
        setPostValueForKey("sessionID", UserMrg.getSessionId(mContext));
        setPostValueForKey("ver", "" + TnbBaseNetwork.version);
        setPostValueForKey("loadFrom", TnbBaseNetwork.channel);
        // putHeader("join_id", join_id);
        // putHeader("req_num", req_num);
        // putHeader("valid",valid );
    }

    public static void init() {
        if (Looper.getMainLooper() == Looper.myLooper())
            mHandler = new Handler();
    }

    /**
     * @param cxt
     * @param key
     * @param cacheDuration 缓存时间
     * @param b
     */
    public static void setCache(Context cxt, String key, long cacheDuration, byte[] b) {
        if (cacheDuration != 0) {
            CacheMrg.getInstance(cxt).putStringCache(key, new String(b), cacheDuration);
            if (DEBUG) {
                Log.v("http", "----------->data to cache<------------");
            }
        }
    }

    public static void setCache(Context cxt, String key, long cacheDuration, String str) {
        if (cacheDuration != 0) {
            CacheMrg.getInstance(cxt).putStringCache(key, str, cacheDuration);
            if (DEBUG) {
                Log.v("http", "----------->data to cache<------------");
            }
        }
    }

    public static void clearCache(Context cxt, String key) {
        CacheMrg.getInstance(cxt).deleteCache(key);
        if (DEBUG) {
            Log.v("comvee http", "clear--->" + key);
        }
    }

    public static void clearAllCache(Context cxt) {
        CacheMrg.getInstance(cxt).clear();
    }

    public static String getCache(Context cxt, String key) {
        return CacheMrg.getInstance(cxt).tryGetCache(key);
    }

    public void setMemberID(String mID) {
        setPostValueForKey("sessionMemberID", mID);
    }

    @Override
    public void startAsynchronous() {
        if (isAsynCallBack) {
           ThreadHandler.postWorkThread(new Runnable() {

                @Override
                public void run() {
                    if (!checkCache(mCachKey) && checkNetwork()) {
                        ComveeHttp.super.startAsynchronous();
                    }
                }
            });
        } else {
            if (!checkCache(mCachKey) && checkNetwork()) {
                ComveeHttp.super.startAsynchronous();
            }
        }

    }

    public boolean checkNetwork() {

        boolean hasNet = NetStatusManager.isNetWorkStatus(BaseApplication.getInstance());
        if (!hasNet) {
            loadFailed(getThreadId(), ERRCODE_NETWORK);
        }
        return hasNet;
    }

    public void setOnHttpListener(int nThreadID, OnHttpListener callBack) {
        setThreadId(nThreadID);
        this.listener = callBack;
    }

    @Override
    public final void setListener(int nThreadID, KwHttpRequestListener callBack) {
        super.setListener(nThreadID, callBack);
    }

    public void setNeedGetCache(boolean getCache, String key) {
        this.mCachKey = key;
        mNeedGetCach = getCache;
    }

    private boolean checkCache(String key) {
        try {
            if (CacheMrg.getInstance(mContext).checkLately(mCachKey)) {
                if (listener != null) {
                    listener.onSussece(getThreadId(), CacheMrg.getInstance(mContext).getStringCache(mCachKey).getBytes(), true);
                    if (DEBUG) {
                        Log.v("http", "----------->data is from cache<------------");
                    }
                }
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void loadFinished(int nThreadID, final byte[] b) {
        super.loadFinished(nThreadID, b);
        if (listener != null) {
            ThreadHandler.postUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.onSussece(getThreadId(), b, false);
                }
            });
        }
    }

    protected void loadFailed(final int nThreadID, final int nErrorCode) {
        try {
            super.loadFailed(nThreadID, nErrorCode);
            if (listener != null) {
                if (null != mHandler) {

                    ThreadHandler.postUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFialed(nThreadID, nErrorCode);
                        }
                    });
                } else {
                    listener.onFialed(nThreadID, nErrorCode);

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
