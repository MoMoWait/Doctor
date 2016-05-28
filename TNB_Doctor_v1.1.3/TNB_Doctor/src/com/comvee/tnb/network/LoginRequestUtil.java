package com.comvee.tnb.network;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.comvee.BaseApplication;
import com.comvee.ThreadHandler;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.db.DBManager;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.MemberInfo;
import com.comvee.tool.UmenPushUtil;
import com.comvee.tool.UserMrg;
import com.comvee.ui.remind.TimeRemindUtil;
import com.comvee.util.CacheUtil;
import com.comvee.util.MD5Util;
import com.comvee.util.StringUtil;

import org.json.JSONObject;

/**
 * 登录接口
 *
 * @author Administrator
 */
public class LoginRequestUtil extends TnbBaseNetwork {
    public LoginListener mListener;
    private String strPhone, strPwd;

    public void requestLogin(final String strPhone, final String strPwd, LoginListener callBack) {
        this.mListener = callBack;
        this.strPhone = strPhone;
        this.strPwd = strPwd;
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                putPostValue("user_no", strPhone);
                putPostValue("user_pwd", MD5Util.getMD5String(strPwd));
                putPostValue("user_type", "2");
                putPostValue("pushTokenKey", UmenPushUtil.getPushTokenKey());
                start();
            }
        },100);

    }

    public void setLoginListener(LoginListener callBack) {
        this.mListener = callBack;
    }

    @Override
    protected void onDoInMainThread(int status, Object obj) {
        if (status != SUCCESS && (mListener == null || (mListener != null && !mListener.onFail()))) {
            Toast.makeText(BaseApplication.getInstance(), obj.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void toTryQQLogin(final String msg) {
        if (null != mListener) {
            ThreadHandler.postUiThread(new Runnable() {
                @Override
                public void run() {
                    mListener.onToTryQQLogin(msg);
                }
            });
        }
    }

    public void toIndexPage() {
        if (null != mListener) {
            ThreadHandler.postUiThread(new Runnable() {
                @Override
                public void run() {
                    mListener.onToIndexPage();
                }
            });

        }
    }

    public void toMemberChoosePage(final MemberInfo info) {
        if (null != mListener) {
            ThreadHandler.postUiThread(new Runnable() {
                @Override
                public void run() {
                    mListener.onToMemberChoosePage(info);
                }
            });
        }
    }

    @Override
    public Object parseResponseJsonData(JSONObject packet) {
        parseLogin(packet);
        return null;
    }

    public void parseLogin(JSONObject packet) {
        Context cxt = BaseApplication.getInstance();
        String oldSessionId = UserMrg.getSessionId(cxt);
        String oldMemberSessionId = UserMrg.getMemberSessionId(cxt);
        String memberSessionId = packet.optString("sessionMemberID");
        UserMrg.setMemberSessionId(cxt, memberSessionId);
        UserMrg.setSessionId(cxt, packet.optString("sessionID"));

        UserMrg.setTestData(cxt, false);
        JSONObject body = packet.optJSONObject("body");

        if (body.optInt("qqDatedFlag") == 1) {//QQ登入是否过期
            toTryQQLogin(packet.optJSONObject("body").optString("datedMsg"));
            return;
        }

        com.comvee.tool.Log.i(body.toString());
        JSONObject obj = body.optJSONObject("member");
        MemberInfo info = new MemberInfo();
        info.name = obj.optString("memberName");
        if (StringUtil.checkChinese(info.name)) {
            info.name = info.name.length() > 8 ? info.name.substring(0, 6) + "..." : info.name;
        } else {
            info.name = info.name.length() > 8 ? info.name.substring(0, 6) + "..." : info.name;
        }
        info.mId = obj.optString("memberId");
        info.coordinator = obj.optInt("coordinator");
        info.photo = obj.optString("picUrl") + obj.optString("picPath");
        info.mId = obj.optString("memberId");
        info.callreason = obj.optInt("callreason");
        info.birthday = obj.optString("birthday");
        info.memberHeight = obj.optString("memberHeight");
        info.diabetes_plan = body.optString("diabetes_plan");
        info.score_describe = body.optString("score_describe");
        info.ifLogin = body.optBoolean("ifLogin");
        info.hasMachine = obj.optInt("hasMachine");
        info.memberWeight = obj.optString("memberWeight");
        info.relative = obj.optString("relation");
        info.sex = obj.optString("sex");
        UserMrg.setDefaultMember(info);


        UserMrg.setTestData(cxt, false);
        ConfigParams.IS_TEST_DATA = false;


        if (!memberSessionId.equals(oldMemberSessionId) || !UserMrg.getSessionId(cxt).equals(oldSessionId)) {
            ComveeHttp.clearAllCache(cxt);
            // 清理数据库，，，，临时这样做
            DBManager.cleanDatabases(cxt);
            TimeRemindUtil.getInstance(cxt).stopRemind();
            CacheUtil.getInstance().clear();
        }

        UserMrg.setLoginName(cxt, strPhone);
        UserMrg.setLoginPwd(cxt, strPwd);

        if (!TextUtils.isEmpty(strPhone) && !TextUtils.isEmpty(strPwd)) {//如果用户名和密码为空，是QQ登入
            UserMrg.setQQAoutoLogin(cxt, false);
        } else {
            UserMrg.setQQAoutoLogin(cxt, true);
        }

        if (TextUtils.isEmpty(memberSessionId)) {
            // 判断memberid是否为空，如果为空就是还未添加成员，需要跳转去添加成员
            UserMrg.setAoutoLogin(cxt, false);
            UserMrg.setTestData(cxt, false);
            toMemberChoosePage(null);
        } else if ((info.ifLogin)) {
            toMemberChoosePage(info);
            UserMrg.setAoutoLogin(cxt, true);
        } else {
            UserMrg.setAoutoLogin(cxt, true);
            toIndexPage();
//            PharmacyUtil.getInstance(cxt).requestRemindListNew();
        }
        ConfigParams.updateConfig(cxt);
    }

    @Override
    public String getUrl() {
        return ConfigUrlMrg.LOGIN;
    }

    public interface LoginListener {

        void onToIndexPage();

        void onToMemberChoosePage(MemberInfo info);

        void onToTryQQLogin(String msg);

        boolean onFail();

    }


}
