package com.comvee.tnb.network;

import android.widget.Toast;

import com.comvee.BaseApplication;

import com.comvee.ThreadHandler;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tool.Log;
import com.comvee.tool.UserMrg;
import com.comvee.util.CacheUtil;
import com.comvee.util.JsonHelper;
import com.comvee.util.MD5Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by friendlove-pc on 16/3/17.
 */
public class ObjectLoader<T> extends TnbBaseNetwork {
    public static final int TYPE_BODY_ARRAY = 1;// 数组
    public static final int TYPE_BODY_OBJECT = 2;// 对象
    public static final int TYPE_ROWS_ARRAY = 3;
    @Deprecated
    public static final int TYPE_BODYOBJ_OBJECT = 4;
    @Deprecated
    public static final int TYPE_BODYOBJ_ARRAY = 5;
    @Deprecated
    public static final int TYPE_BODYLIST_ARRAY = 6;

    public static final int TYPE_OBJ_PATH = 7;//根据路径解析对象
    public static final int TYPE_ARRAY_PATH = 8;//根据路径解析数组
    private static final int CACHE = Integer.MAX_VALUE;
    private String mUrl;
    private Class<?> mClass;
    private CallBack mCallback;
    private String mCacheKey;
    private int mLoadType;
    private boolean isNeedCache = true;
    private int threadId;
    private long startTime;
    private String mOldFileMD5;
    private String[] mPath;

    public void setThreadId(int id) {
        this.threadId = id;
    }

    public void setNeedCache(boolean b) {
        isNeedCache = b;
    }

    @Override
    protected void onDoInMainThread(int status, Object obj) {
        if (CACHE == status) {
            if (obj != null)
                callback(true, obj);
        } else if (status == SUCCESS) {
            if (obj != null)
                callback(false, obj);
        } else {
            if (mCallback != null && !mCallback.onFail(status)) {
                if (obj != null && obj instanceof String) {
                    Toast.makeText(BaseApplication.getInstance(), obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected Object parseResponseJsonData(JSONObject resData) {
        Object obj = null;

        try {
            switch (mLoadType) {
                case TYPE_BODY_ARRAY:
                    JSONArray array = resData.getJSONArray("body");
                    obj = JsonHelper.getListByJsonArray(mClass, array);
                    break;
                case TYPE_BODY_OBJECT:
                    JSONObject body = null;
                    if (mClass == String.class) {
                        obj = resData.optString("body");
                    } else {
                        body = resData.getJSONObject("body");
                        obj = JsonHelper.getObjecByJsonObject(mClass, body);
                    }
                    break;
                case TYPE_BODYOBJ_ARRAY:
                    array = resData.getJSONObject("body").getJSONArray("obj");
                    obj = JsonHelper.getListByJsonArray(mClass, array);
                    break;
                case TYPE_BODYOBJ_OBJECT:
                    body = resData.getJSONObject("body").getJSONObject("obj");
                    obj = JsonHelper.getObjecByJsonObject(mClass, body);
                    break;
                case TYPE_BODYLIST_ARRAY:
                    array = resData.getJSONObject("body").getJSONArray("list");
                    obj = JsonHelper.getListByJsonArray(mClass, array);
                    break;

                case TYPE_ARRAY_PATH:
                    array = null;
                    body = resData;
                    if (mPath != null) {
                        for (int i = 0; i < mPath.length; i++) {
                            if (i < mPath.length - 1) {
                                body = body.getJSONObject(mPath[i]);
                            } else if (i == mPath.length - 1) {
                                array = body.getJSONArray(mPath[i]);
                            }
                        }
                    }
                    obj = JsonHelper.getListByJsonArray(mClass, array);
                    break;
                case TYPE_OBJ_PATH:
                    body = resData;
                    if (mPath != null) {
                        for (int i = 0; i < mPath.length; i++) {
                            body = body.getJSONObject(mPath[i]);
                        }
                    }
                    obj = JsonHelper.getObjecByJsonObject(mClass, body);
                    break;
            }
            if (isNeedCache) {
                CacheUtil.getInstance().putObjectById(mCacheKey, (Serializable) obj);
                File file = CacheUtil.getInstance().getFile(mCacheKey);
                if (file != null) {
                    String newFileMD5 = MD5Util.getFileMD5String(file);
                    if (newFileMD5.equals(mOldFileMD5)) {
                        Log.e("网络数据 == 缓存数据 >>>>  不刷新页面:" + mClass.getSimpleName());
                        return null;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            //postMainThread(ERROR, null);
            ThreadHandler.postUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ObjectLoader.this.onDoInMainThread(ERROR, null);
                    } catch (Exception var3) {
                        var3.printStackTrace();
                    }
                }
            });
        }

        return obj;
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    /**
     * 加载 对象数据 适用于{body:{....}}.
     *
     * @param c
     * @param call
     * @param url
     */
    public void loadBodyObject(Class<? extends Serializable> c, String url, CallBack call) {
        mLoadType = TYPE_BODY_OBJECT;
        load(c, call, url);
    }

    /**
     * 加载 数组数据 适用于{body:[{},{},{}...]}
     *
     * @param c
     * @param call
     * @param url
     */
    public void loadBodyArray(Class<? extends Serializable> c, String url, CallBack call) {
        mLoadType = TYPE_BODY_ARRAY;
        load(c, call, url);
    }

    private void load(Class<? extends Serializable> c, CallBack call, String url) {
        this.mCacheKey = UserMrg.getCacheKey(url + getEntryString());
        this.mUrl = url;
        this.mCallback = call;
        this.mClass = c;
        start(threadId == 0 ? POOL_THREAD_1: threadId);
//        BackgroundTasks.getInstance().push(threadId == 0 ? BackgroundTasks.TASK_NETWORK : threadId, this);
    }

    private void load(Class<? extends Serializable> c, CallBack call, String url, String... path) {
        this.mPath = path;
        load(c, call, url);
    }

    public void loadObjByPath(Class<? extends Serializable> c, String url, CallBack call, String... path) {
        mLoadType = TYPE_OBJ_PATH;
        load(c, call, url, path);
    }

    public void loadArrayByPath(Class<? extends Serializable> c, String url, CallBack call, String... path) {
        mLoadType = TYPE_ARRAY_PATH;
        load(c, call, url, path);
    }


    /**
     * 加载 数组数据 适用于{body:{obj:[{},{},{}...]}
     *
     * @param c
     * @param call
     * @param url
     */
    @Deprecated
    public void loadArrayByBodyobj(Class<? extends Serializable> c, String url, CallBack call) {
        mLoadType = TYPE_BODYOBJ_ARRAY;
        load(c, call, url);
    }

    /**
     * 加载 数组数据 适用于{body:{list:[{},{},{}...]}}
     *
     * @param c
     * @param call
     * @param url
     */
    @Deprecated
    public void loadArrayByBodylist(Class<? extends Serializable> c, String url, CallBack call) {
        mLoadType = TYPE_BODYLIST_ARRAY;
        load(c, call, url);
    }

    /**
     * 加载 对象数据 适用于{body:{obj{....}}}.
     *
     * @param c
     * @param url
     * @param call
     */
    public void loadObjectByBodyobj(Class<? extends Serializable> c, String url, CallBack call) {
        mLoadType = TYPE_BODYOBJ_OBJECT;
        load(c, call, url);
    }

    @Override
    protected boolean onStartReady() {
        if (isNeedCache) {
            startTime = System.currentTimeMillis();
            Object cache = null;
            try {
                if (null != mCallback && (cache = CacheUtil.getInstance().getObjectById(mCacheKey)) != null) {
                    mOldFileMD5 = MD5Util.getFileMD5String(CacheUtil.getInstance().getFile(mCacheKey));
                    final Object obj = cache;
                    postMainThread(CACHE, obj);

                    Log.e("缓存时间：" + (System.currentTimeMillis() - startTime));
//                    postMainThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            callback(true, obj);
//                        }
//                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return super.onStartReady();
    }

    public void clearCache(){
        CacheUtil.getInstance().putObjectById(mCacheKey,null);
    }

    private void callback(boolean fromCache, Object obj) {
        if (mCallback == null) {
            return;
        }
        try {
            switch (mLoadType) {
                case TYPE_BODY_OBJECT:
                case TYPE_BODYOBJ_OBJECT:
                case TYPE_OBJ_PATH:
                    mCallback.onBodyObjectSuccess(fromCache, (T) obj);
                    break;
                case TYPE_ARRAY_PATH:
                case TYPE_BODY_ARRAY:
                case TYPE_BODYOBJ_ARRAY:
                case TYPE_BODYLIST_ARRAY:
                    mCallback.onBodyArraySuccess(fromCache, (ArrayList<T>) obj);
                    break;
            }
        } catch (Exception e) {
            CacheUtil.getInstance().putObjectById(mCacheKey, null);
            e.printStackTrace();
        }
    }

    public class CallBack {
        public void onBodyObjectSuccess(boolean isFromCache, T obj) {
        }

        public void onBodyArraySuccess(boolean isFromCache, ArrayList<T> obj) {

        }

        public boolean onFail(int code) {
            return false;
        }
    }

}
