package com.comvee.tnb.http;

import android.content.Context;
import android.content.Intent;

import com.comvee.BaseApplication;
import com.comvee.ThreadHandler;
import com.comvee.log.ComveeLog;
import com.comvee.network.BaseHttpRequest;
import com.comvee.network.NetStatusManager;
import com.comvee.tnb.TNBApplication;
import com.comvee.tool.UserMrg;
import com.comvee.util.Log;
import com.comvee.util.MD5Util;
import com.comvee.util.TimeUtil;
import com.comvee.util.Util;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

public abstract class TnbBaseNetwork extends BaseHttpRequest {
    public static final String ACTION_NO_NETWORK = TNBApplication.getInstance().getPackageName() + ".no_network";
    public static final String ACTION_TO_LOGIN = TNBApplication.getInstance().getPackageName() + ".to_login";

    static {
        SUCCESS = 0;
    }

    protected int what;
    private boolean isLoading;
    private long id;

    public TnbBaseNetwork() {
        id = Thread.currentThread().getId();
    }

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public static String deviceId;
    public static String reqNum;
    public static String valId;
    public static String version ;
    public static String channel;

    static{
        Context mContext = TNBApplication.getInstance();

        deviceId = ComveeLog.getDeviceId();
//        TimeUtil.fomateTime(System.currentTimeMillis(), "yyyyMMddHHmmss") + "111111"
        reqNum = "";
//        MD5Util.getMD5String(String.format("%s&%s", join_id, req_num))
        valId = "";
        version = Util.getAppVersionCode(mContext, mContext.getPackageName())+"";
        channel = Util.getMetaData(mContext, "APP_CHANNEL", null);
    }


    @Override
    protected void initRequestEntity(RequestParams entry) {
        Context mContext = TNBApplication.getInstance();
        String join_id = "123";
        String req_num = reqNum;
        String valid = valId;
        putPostValue("join_id", join_id);
        putPostValue("req_num", req_num);
        putPostValue("valid", valid);
        putPostValue("dev",deviceId );
        putPostValue("dev_type", "android");
        putPostValue("sessionMemberID", UserMrg.getMemberSessionId(mContext));
        putPostValue("sessionID", UserMrg.getSessionId(mContext));
        putPostValue("ver", version);
        putPostValue("loadFrom", channel);
    }

    @Override
    protected int getResultCode(JSONObject resData) {
        return resData.optJSONObject("res_msg").optInt("res_code");
    }

    @Override
    protected String getResultDesc(JSONObject resData) {
        return resData.optJSONObject("res_msg").optString("res_desc");
    }

    @Override
    protected boolean onStartReady() {

        if (!NetStatusManager.isNetWorkStatus(BaseApplication.getInstance())) {
            TNBApplication.getInstance().sendBroadcast(new Intent(ACTION_NO_NETWORK));
            return true;
        }
        isLoading = true;
        return super.onStartReady();
    }

    @Override
    protected void onSuccess(int statusCode, Header[] headers,final JSONObject response) {

        if (id == Thread.currentThread().getId()) {
            Log.e("在同一个线程");
        } else {
            Log.e("不在同一个线程");
        }

        isLoading = false;

        Log.w("postUrl: onSuccess");
        Log.w(response.toString());
        try {
           final  int resCode = getResultCode(response);
            if (resCode == 200000) {//重新登入
                TNBApplication.getInstance().sendBroadcast(new Intent(ACTION_TO_LOGIN));
                return;
            } else if (resCode != SUCCESS) {
//                postMainThread(resCode, getResultDesc(response));
                ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TnbBaseNetwork.this.onDoInMainThread(resCode, getResultDesc(response));
                        } catch (Exception var3) {
                            var3.printStackTrace();
                        }
                    }
                });
                return;
            }
           // postMainThread(SUCCESS, parseResponseJsonData(response));
            ThreadHandler.postUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TnbBaseNetwork.this.onDoInMainThread(SUCCESS,parseResponseJsonData(response));
                    } catch (Exception var3) {
                        var3.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
           // postMainThread(TIME_OUT, "访问服务器超时");
            ThreadHandler.postUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TnbBaseNetwork.this.onDoInMainThread(TIME_OUT,"访问服务器超时");
                    } catch (Exception var3) {
                        var3.printStackTrace();
                    }
                }
            });
        }


    }

    @Override
    protected void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        super.onFailure(statusCode, headers, responseString, throwable);
        isLoading = false;
    }

    public boolean isloading() {
        return isLoading;
    }


}
