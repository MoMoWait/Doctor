package com.comvee.tnb.ui.user;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.comvee.BaseApplication;
import com.comvee.frame.BaseFragmentActivity;
import com.comvee.tnb.R;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.ComveeTask;
import com.comvee.tnb.network.LoginRequestUtil;
import com.comvee.tool.AppUtil;
import com.comvee.tool.UserMrg;
import com.comvee.util.TimeUtil;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

public class QQLoginManager {
    private Context mContext = BaseApplication.getInstance();
    private String access_token;

    private String openid;
    private long expires_in;
    private FragmentActivity activity;
    private LoginRequestUtil.LoginListener mListener;

    public QQLoginManager(FragmentActivity activity) {
        this.activity = activity;

    }

    public static QQLoginManager getIntence(FragmentActivity activity) {
        return new QQLoginManager(activity);
    }

    public void setLoginListener(LoginRequestUtil.LoginListener lis) {
        this.mListener = lis;
    }

    public void tryLoginQQ() {
        try {

            if (checkQQLogin())
                return;

            // fragment.showProDialog(activity.getResources().getString(R.string.msg_loading));

            final Tencent tencent = Tencent.createInstance("1101117477", activity);
            tencent.login(activity, "all", new IUiListener() {

                @Override
                public void onError(UiError arg0) {
                    if (AppUtil.getAPNType(mContext) == -1) {
                        Toast.makeText(mContext, "无网络", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "绑定失败！", Toast.LENGTH_SHORT).show();
                        // fragment.cancelProDialog();
                    }
                    if (!ConfigParams.IS_LOGIN) {
                        UserMrg.clear(mContext);
                    }
                    // ((MainActivity) activity).setIntent(new Intent());

                }

                @Override
                public void onComplete(Object arg0) {

                    final JSONObject tokenObj = (JSONObject) arg0;

                    if (tokenObj.optInt("ret") != 0 && !ConfigParams.IS_LOGIN) {
                        UserMrg.clear(mContext);
                        return;
                    }

                    access_token = tokenObj.optString("access_token");
                    openid = tokenObj.optString("openid");
                    expires_in = tokenObj.optLong("expires_in") * 1000 + System.currentTimeMillis() - 60 * 60 * 1000 * 24;

                    if (TextUtils.isEmpty(openid)) {
                        access_token = UserMrg.getQQTekon(mContext);
                        expires_in = UserMrg.getQQExpires_in(mContext);
                    } else {
                        UserMrg.setQQTekon(mContext, access_token);
                        UserMrg.setQQ_OpenId(mContext, openid);
                        UserMrg.setQQExpires_in(mContext, expires_in);
                    }

                    // Log.e("qq", "access_token:" + access_token +
                    // "    openid:" +
                    // openid + "     expires_in:" + expires_in);

                    UserInfo mInfo = new UserInfo(mContext, tencent.getQQToken());
                    mInfo.getUserInfo(new IUiListener() {

                        @Override
                        public void onError(UiError arg0) {
                            if (!ConfigParams.IS_LOGIN) {
                                UserMrg.clear(mContext);
                            }
                            // fragment.cancelProDialog();
                        }

                        @Override
                        public void onComplete(Object arg0) {
                            JSONObject obj = (JSONObject) arg0;
                            if (obj.optInt("ret") != 0 && !ConfigParams.IS_LOGIN) {
                                UserMrg.clear(mContext);
                                return;
                            }
                            requestLoginQQ(openid, access_token, expires_in + "", obj.optString("nickname"), obj.optString("figureurl_qq_2"), "",
                                    "男".equals(obj.optString("gender")) ? "1" : "2");
                        }

                        @Override
                        public void onCancel() {
                            // fragment.cancelProDialog();
                            if (!ConfigParams.IS_LOGIN) {
                                UserMrg.clear(mContext);
                            }
                        }
                    });
                }

                @Override
                public void onCancel() {
                    if (AppUtil.getAPNType(activity) == -1) {
                        ComveeHttpErrorControl.parseError(activity, ComveeHttp.ERRCODE_NETWORK);
                    } else {
                        Toast.makeText(activity, "绑定失败！", Toast.LENGTH_SHORT).show();
                        // fragment.cancelProDialog();
                    }
                    if (!ConfigParams.IS_LOGIN) {
                        UserMrg.clear(activity);
                    }
                    // ((MainActivity) activity).setIntent(new Intent());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkQQLogin() {
        if (System.currentTimeMillis() < UserMrg.getQQExpires_in(mContext)) {
            // fragment.showProDialog(mContext.getResources().getString(R.string.msg_loading));

            final Tencent tencent = Tencent.createInstance("1101117477", mContext);

            final String access_token = UserMrg.getQQTekon(mContext);
            final String openid = UserMrg.getQQ_OpenId(mContext);
            final long expires_in = UserMrg.getQQExpires_in(mContext);
            tencent.setOpenId(openid);
            tencent.setAccessToken(access_token, "7776000");
            requestLoginQQ(openid, access_token, expires_in + "", "", "", "", "");
            return true;
        } else {
            Log.e("qq",
                    "QQ未登录或token失效   当前时间：" + TimeUtil.fomateTime(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "    过期时间："
                            + TimeUtil.fomateTime(UserMrg.getQQExpires_in(mContext), "yyyy-MM-dd HH:mm:ss"));
        }

        return false;
    }

    public void requestLoginQQ(final String pid, final String tokenKey, final String validTime, final String nickName, final String photoUrl,
                               final String birthday, final String sex) {
        ((BaseFragmentActivity) activity).showProgressDialog(activity.getResources().getString(R.string.msg_loading));
        final LoginRequestUtil util = new LoginRequestUtil();
        util.setLoginListener(mListener);

        new ComveeTask<String>() {

            @Override
            protected String doInBackground() {

                ComveeHttp http = new ComveeHttp(activity, ConfigUrlMrg.LOGIN);
                http.setPostValueForKey("user_type", "2");
                http.setPostValueForKey("loginType", "1");
                http.setPostValueForKey("pid", pid);
                http.setPostValueForKey("tokenKey", tokenKey);
                http.setPostValueForKey("validTime", validTime);
                http.setPostValueForKey("nickname", nickName);
                http.setPostValueForKey("photoUrl", photoUrl);
                http.setPostValueForKey("birthday", birthday);
                http.setPostValueForKey("sex", sex);
                String result = http.startSyncRequestString();

                ComveePacket packet = null;
                try {
                    packet = ComveePacket.fromJsonString(result);
                    if (packet.getResultCode() != 0) {
                        postError(packet);
                        return null;
                    } else {
                        util.parseLogin(packet);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                return result;
            }


            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                ((BaseFragmentActivity) activity).cancelProgressDialog();
                if (result != null) {

                } else {
                    if (AppUtil.getAPNType(activity) == -1) {
                        ComveeHttpErrorControl.parseError(activity, ComveeHttp.ERRCODE_NETWORK);
                    } else {
                        Toast.makeText(activity, "绑定失败！", Toast.LENGTH_SHORT).show();
                        ((BaseFragmentActivity) activity).cancelProgressDialog();
                    }
                }

            }
        }.execute();
    }

    /**
     * 判断是否使用qq登录
     */
    public void isUseQQLogin() {
        if (!ConfigParams.IS_LOGIN) {
            Intent it = (activity).getIntent();
            if ("qqhealth".equals(it.getStringExtra("from"))) {
                String accesstoken = it.getStringExtra("accesstoken");
                String openid = it.getStringExtra("openid");
                if (!TextUtils.isEmpty(openid) && !openid.equals(UserMrg.getQQ_OpenId(activity)) && UserMrg.getQQ_OpenId(activity) != null) {
                    // 判断 accesstoken 过期
                    if (!TextUtils.isEmpty(accesstoken) && !accesstoken.equals(UserMrg.getQQTekon(activity))) {
                    }
                    Toast.makeText(activity, "当前使用的QQ账户与手机QQ登陆账户不一致，无法在手机QQ健康中心显示数据！", Toast.LENGTH_LONG).show();

                }
                if (UserMrg.getLoginName(activity) != null && UserMrg.getLoginPwd(activity) != null && UserMrg.getMemberSessionId(activity) != null
                        && UserMrg.getSessionId(activity) != null) {
                    UserMrg.setShowToast(activity, true);

                }
                if (!UserMrg.isAutoLogin(activity) || ConfigParams.IS_TEST_DATA) {
                    tryLoginQQ();
                }
                (activity).setIntent(new Intent());
            }
        } else {
            Intent it = (activity).getIntent();
            if ("qqhealth".equals(it.getStringExtra("from"))) {
                String accesstoken = it.getStringExtra("accesstoken");
                String openid = it.getStringExtra("openid");
                if (!TextUtils.isEmpty(openid) && !openid.equals(UserMrg.getQQ_OpenId(activity)) && UserMrg.getQQ_OpenId(activity) != null) {
                    // 判断 accesstoken 过期
                    if (!TextUtils.isEmpty(accesstoken) && !accesstoken.equals(UserMrg.getQQTekon(activity))) {
                    }
                    Toast.makeText(activity, "当前使用的QQ账户与手机QQ登陆账户不一致，无法在手机QQ健康中心显示数据！", Toast.LENGTH_LONG).show();

                }
                if (UserMrg.getLoginName(activity) != null && UserMrg.getLoginPwd(activity) != null) {
                    Toast.makeText(activity, "当前未使用QQ登陆，无法在手机QQ-健康中心中显示数据", Toast.LENGTH_LONG).show();
                }
                if (ConfigParams.IS_TEST_DATA) {
                    tryLoginQQ();
                }
                (activity).setIntent(new Intent());
            }
        }
    }
}
