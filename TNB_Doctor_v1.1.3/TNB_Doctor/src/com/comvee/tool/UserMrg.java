package com.comvee.tool;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.widget.Toast;

import com.comvee.BaseApplication;
import com.comvee.cache.SerializUtil;
import com.comvee.frame.BaseFragment;
import com.comvee.frame.BaseFragmentActivity;
import com.comvee.frame.FragmentMrg;

import com.comvee.tnb.DrawerMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.db.DBManager;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.model.AskIndexInfo;
import com.comvee.tnb.model.MemberInfo;
import com.comvee.tnb.model.RadioMainInfo;
import com.comvee.tnb.model.SugarControl;
import com.comvee.tnb.model.SugarRecord;
import com.comvee.tnb.network.LoginRequestUtil;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.index.IndexModel;
import com.comvee.tnb.ui.member.MemberChooseIndexFragemtn;
import com.comvee.tnb.ui.user.QQLoginManager;
import com.comvee.tool.AppUtil.CheckUserListener;
import com.comvee.util.CacheUtil;
import com.comvee.util.MD5Util;
import com.comvee.util.StringUtil;
import com.comvee.util.TimeUtil;

import org.json.JSONObject;

import java.util.Calendar;

public class UserMrg {

    public static boolean IS_VISITOR;//是否是游客

    public static final String TN = "user";
    public static String MEMBER_SESSION_ID = "";

    public static MemberInfo DEFAULT_MEMBER = new MemberInfo();

    static {
//        DEFAULT_MEMBER = (MemberInfo) CacheUtil.getInstance().getObjectById("tnb_default_member");
    	try{
    	DEFAULT_MEMBER = (MemberInfo) SerializUtil.fromString(BaseApplication.getInstance().getSharedPreferences(TN, 0).getString("member", null));
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

    public final static boolean isTnb() {
        if (UserMrg.DEFAULT_MEMBER != null && UserMrg.DEFAULT_MEMBER.callreason == 1) {
            return true;
        }
        return false;
    }

    public static void setDefaultMember(MemberInfo member) {
        DEFAULT_MEMBER = member;
        try{
        BaseApplication.getInstance().getSharedPreferences(TN, 0).edit().putString("member", SerializUtil.toString(member)).commit();
        }catch(Exception E){
        	E.printStackTrace();
        }
//        CacheUtil.getInstance().putObjectById("tnb_default_member", member);
    }

    public static String getMemberSessionId(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getString("sessionMemberID", null);
    }

    public static String getSessionId(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getString("sessionID", null);
    }

    public static void setMemberSessionId(Context cxt, String id) {
        MEMBER_SESSION_ID = id;
        cxt.getSharedPreferences(TN, 0).edit().putString("sessionMemberID", id).commit();
    }

    public static void setSessionId(Context cxt, String id) {
        cxt.getSharedPreferences(TN, 0).edit().putString("sessionID", id).commit();
    }

    public static boolean isLoginStatus(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getBoolean("login", false);
    }

    public static void setLoginStatus(Context cxt, boolean b) {
        cxt.getSharedPreferences(TN, 0).edit().putBoolean("login", b).commit();
    }

    public static void setLoginName(Context cxt, String name) {
        cxt.getSharedPreferences(TN, 0).edit().putString("login_name", name).commit();
    }

    public static void setLoginPwd(Context cxt, String pwd) {
        cxt.getSharedPreferences(TN, 0).edit().putString("login_pwd", pwd).commit();
    }

    public static String getLoginName(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getString("login_name", null);
    }

    public static String getLoginPwd(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getString("login_pwd", null);
    }

    public static String getCacheKey(String key) {
        return MD5Util.getMD5String(MEMBER_SESSION_ID + key);
    }

    public static boolean isQQAutoLogin(Context cxt) {
        return cxt.getSharedPreferences("qq", 0).getBoolean("qq_auto_login", false);
    }

    public static void setQQAoutoLogin(Context cxt, boolean b) {
        cxt.getSharedPreferences("qq", 0).edit().putBoolean("qq_auto_login", b).commit();
    }

    public static boolean isAutoLogin(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getBoolean("auto_login", false);
    }

    public static void setAoutoLogin(Context cxt, boolean b) {
        cxt.getSharedPreferences(TN, 0).edit().putBoolean("auto_login", b).commit();
    }

    public static void setTestData(Context cxt, boolean b) {
        cxt.getSharedPreferences(TN, 0).edit().putBoolean("test", b).commit();
        IS_VISITOR = b;
        ConfigParams.IS_TEST_DATA = b;
    }

    public static boolean getTestData(Context cxt) {
        IS_VISITOR = cxt.getSharedPreferences(TN, 0).getBoolean("test", false);
        ConfigParams.IS_TEST_DATA = IS_VISITOR;
        return IS_VISITOR;
    }

    public static void setTestDataSessionId(Context cxt, String sid) {
        cxt.getSharedPreferences(TN, 0).edit().putString("sid", sid).commit();
    }

    public static void setTestDataMemberId(Context cxt, String mid) {
        cxt.getSharedPreferences(TN, 0).edit().putString("mid", mid).commit();
    }

    public static String getTestDataSessionId(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getString("sid", null);
    }

    public static String getTestDataMemberId(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getString("mid", null);
    }

    public static void setChoosePackage(Context cxt, long index) {
        cxt.getSharedPreferences(TN, 0).edit().putLong("ChoosePackage", index).commit();
    }

    public static long getChoosePackage(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getLong("ChoosePackage", 0);
    }

    public static void setTempQQTekon(Context cxt, String tekon) {
        cxt.getSharedPreferences("qq", 0).edit().putString("temp_qq_tekon", tekon).commit();
    }

    public static String getTempQQTekon(Context cxt) {
        return cxt.getSharedPreferences("qq", 0).getString("temp_qq_tekon", null);
    }

    public static String getTempQQ_OpenId(Context cxt) {
        return cxt.getSharedPreferences("qq", 0).getString("temp_openid", null);
    }

    public static void setTempQQ_OpenId(Context cxt, String openid) {
        cxt.getSharedPreferences("qq", 0).edit().putString("temp_openid", openid).commit();
    }

    public static void setTempQQExpires_in(Context cxt, long time) {
        cxt.getSharedPreferences("qq", 0).edit().putLong("temp_qq_expires_in", time).commit();
    }

    public static long getTempQQExpires_in(Context cxt) {
        return cxt.getSharedPreferences("qq", 0).getLong("temp_qq_expires_in", 0);
    }

    public static void setQQTekon(Context cxt, String tekon) {
        cxt.getSharedPreferences("qq", 0).edit().putString("qq_tekon", tekon).commit();
    }

    public static void setQQExpires_in(Context cxt, long time) {
        cxt.getSharedPreferences("qq", 0).edit().putLong("qq_expires_in", time).commit();
    }

    public static String getQQTekon(Context cxt) {
        return cxt.getSharedPreferences("qq", 0).getString("qq_tekon", null);
    }

    public static long getQQExpires_in(Context cxt) {
        return cxt.getSharedPreferences("qq", 0).getLong("qq_expires_in", 0);
    }

    public static String getQQ_OpenId(Context cxt) {
        return cxt.getSharedPreferences("qq", 0).getString("openid", null);
    }

    public static void setQQ_OpenId(Context cxt, String openid) {
        cxt.getSharedPreferences("qq", 0).edit().putString("openid", openid).commit();
    }

    public static void clear(Context cxt) {
        cxt.getSharedPreferences(TN, 0).edit().clear().commit();
        MEMBER_SESSION_ID = "";
        DEFAULT_MEMBER = new MemberInfo();
    }

    public static void setTitle(Context cxt, String mid) {
        cxt.getSharedPreferences(TN, 0).edit().putString("title", mid).commit();
    }

    public static String getTitle(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getString("title", null);
    }

    public static void setMainNum(Context cxt, String mid) {
        cxt.getSharedPreferences(TN, 0).edit().putString("num", mid).commit();
    }

    public static String getMainNum(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getString("num", null);
    }

    public static long getTime_Last(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getLong("time_last", 0);
    }

    public static void setTime_Last(Context cxt, long time) {
        cxt.getSharedPreferences(TN, 0).edit().putLong("time_last", time).commit();
    }

    public static int getAsses_interval(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getInt("asses_interval", 0);
    }

    public static void setAsses_interval(Context cxt, int asses) {
        cxt.getSharedPreferences(TN, 0).edit().putInt("asses_interval", asses).commit();
    }

    public static boolean isShowToast(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getBoolean("isShowToast", false);
    }

    public static void setShowToast(Context cxt, boolean isShowToast) {
        cxt.getSharedPreferences(TN, 0).edit().putBoolean("isShowToast", isShowToast).commit();
    }

    // public static void setMemberCountTimeByMemberId(Context ctx, long time,
    // String memberId) {
    // ctx.getSharedPreferences(TN, 0).edit().putLong(memberId, time).commit();
    // }
    //
    // public static long getMemberCountTimeByMemberId(Context ctx, String
    // memberId) {
    // return ctx.getSharedPreferences(TN, 0).getLong(memberId, -1);
    // }

    /**
     * 获取餐后提醒闹钟时间
     *
     * @param memberId
     * @return
     */
    public static long getMemberMealClockTime(String memberId) {
        return TNBApplication.getInstance().getSharedPreferences(TN, 0).getLong("meal_clock:" + memberId, -1);
    }

    /**
     * 设置餐后测血糖提醒闹钟时间
     *
     * @param memberId
     * @param time
     */
    public static void setMemberMealClockTime(String memberId, long time) {
        TNBApplication.getInstance().getSharedPreferences(TN, 0).edit().putLong("meal_clock:" + memberId, time).commit();
    }

    public static void changMember(final String memberId, final FragmentActivity activity, final BaseFragment frag, final CheckUserListener userListener,
                                   final boolean canBack, final boolean anim) {
        ((BaseFragmentActivity) activity).showProgressDialog("正在切换成员...");
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... arg0) {

                String url = ConfigUrlMrg.MEMBER_CHANGE;
                ComveeHttp http = new ComveeHttp(activity, url);
                http.setPostValueForKey("memberId", memberId);
                String result = http.startSyncRequestString();
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                ((BaseFragmentActivity) activity).cancelProgressDialog();
                parseChangeMember(activity, result);
                ConfigParams.TO_BACK_TYPE = 3;
                // ((MainActivity) activity).updateMsgView(0);
                if (userListener != null) {
                    userListener.onCheckFinash();
                    return;
                }
                IndexFrag.toFragment(activity, false);
                FragmentMrg.toFragment(activity, frag, true, anim);

            }
        }.execute();
    }

    public static void parseChangeMember(final FragmentActivity activity, String arg1) {
        try {
            ComveePacket packet = ComveePacket.fromJsonString(arg1);

            if (packet.getResultCode() == 0) {
                Toast.makeText(activity, "切换成功", Toast.LENGTH_SHORT).show();
                // 清理数据库，，，，临时这样做
                DBManager.cleanDatabases(activity);
                ComveeHttp.clearAllCache(activity);
                UserMrg.setMemberSessionId(activity, packet.optString("sessionMemberID"));
                parseMember(activity, arg1);
                DrawerMrg.getInstance().updateLefFtagment();

            } else {
                ComveeHttpErrorControl.parseError(activity, packet);
                // showToast(packet.getResultMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, R.string.error, Toast.LENGTH_SHORT);
        }
    }

    private static void parseMember(final FragmentActivity activity, String arg1) {
        try {
            ComveePacket packet = ComveePacket.fromJsonString(arg1);

            if (packet.getResultCode() == 0) {
                JSONObject body = packet.getJSONObject("body");
                JSONObject obj = body.getJSONObject("member");
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
                info.hasMachine = obj.optInt("hasMachine");
                info.memberWeight = obj.optString("memberWeight");
                info.relative = obj.optString("relation");
                UserMrg.setDefaultMember(info);

            } else {
                ComveeHttpErrorControl.parseError(activity, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loginQQ(final FragmentActivity act) {
        final QQLoginManager mrg = QQLoginManager.getIntence(act);
        mrg.setLoginListener(new LoginRequestUtil.LoginListener() {

            @Override
            public boolean onFail() {
                ((BaseFragmentActivity) act).cancelProgressDialog();
                return false;
            }

            @Override
            public void onToIndexPage() {
                ((BaseFragmentActivity) act).cancelProgressDialog();
                IndexModel.notifyMemberClock(act.getApplicationContext());
                FragmentMrg.removeAllFragment(act);
                FragmentMrg.removeAllSigleMap();
                IndexFrag.toFragment(act, false);
                DrawerMrg.getInstance().updateLefFtagment();
            }

            @Override
            public void onToMemberChoosePage(MemberInfo info) {
                ((BaseFragmentActivity) act).cancelProgressDialog();
                FragmentMrg.removeAllFragment(act);
                FragmentMrg.removeAllSigleMap();
                FragmentMrg.toFragment(act, MemberChooseIndexFragemtn.newInstance(info), true, false);
            }

            @Override
            public void onToTryQQLogin(String msg) {
                CustomDialog.Builder dialog = new CustomDialog.Builder(act);
                dialog.setMessage(msg);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        mrg.tryLoginQQ();
                    }
                });
                dialog.setNegativeButton("取消", null);
                dialog.create().show();
            }
        });
        mrg.tryLoginQQ();

    }

    /**
     * 自动登入
     *
     * @param act
     */
    public static void autoLogin(final FragmentActivity act) {
        login(act, getLoginName(BaseApplication.getInstance()), getLoginPwd(BaseApplication.getInstance()));
    }

    public static void login(final FragmentActivity act, String phone, String pwd) {
        LoginRequestUtil login = new LoginRequestUtil();
        login.requestLogin(phone, pwd, new LoginRequestUtil.LoginListener() {

            @Override
            public boolean onFail() {
                ((BaseFragmentActivity) act).cancelProgressDialog();
                return false;
            }

            @Override
            public void onToIndexPage() {
               ((BaseFragmentActivity) act).cancelProgressDialog();
                IndexModel.notifyMemberClock(act.getApplicationContext());
                FragmentMrg.removeAllFragment(act);
                FragmentMrg.removeAllSigleMap();
                IndexFrag.toFragment(act, false);
                DrawerMrg.getInstance().updateLefFtagment();
            }

            @Override
            public void onToMemberChoosePage(MemberInfo info) {
                ((BaseFragmentActivity) act).cancelProgressDialog();
                FragmentMrg.removeAllFragment(act);
                FragmentMrg.removeAllSigleMap();
                FragmentMrg.toFragment(act, MemberChooseIndexFragemtn.newInstance(info), true, false);
            }

            @Override
            public void onToTryQQLogin(String msg) {
                ((BaseFragmentActivity) act).cancelProgressDialog();

                final QQLoginManager mrg = QQLoginManager.getIntence(act);
                mrg.setLoginListener(this);
                CustomDialog.Builder dialog = new CustomDialog.Builder(act);
                dialog.setMessage(msg);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        mrg.tryLoginQQ();
                    }
                });
                dialog.setNegativeButton("取消", null);
                dialog.create().show();
            }
        });
    }


    /**
     * 预加载
     */
    public static void preLoad() {
        try {
            //预加载医生首页
            ObjectLoader<AskIndexInfo> loader = new ObjectLoader<AskIndexInfo>();
            loader.setThreadId(loader.POOL_THREAD_2);
            loader.loadBodyObject(AskIndexInfo.class, ConfigUrlMrg.ASK_NEW_SERVER_LIST, null);

            //预加载糖豆电台
            ObjectLoader<RadioMainInfo> loader1 = new ObjectLoader<RadioMainInfo>();
            loader.setThreadId(loader.POOL_THREAD_2);
            loader1.loadObjectByBodyobj(RadioMainInfo.class, ConfigUrlMrg.RAIOD_MAIN_LIST, null);

            //加载当天血糖数据
            ObjectLoader<SugarRecord> loader2 = new ObjectLoader<SugarRecord>();
            loader.setThreadId(loader.POOL_THREAD_2);
            loader2.putPostValue("date", TimeUtil.fomateTime(System.currentTimeMillis(), "yyyy:MM:dd"));
            loader2.loadBodyArray(SugarRecord.class, ConfigUrlMrg.TENDENCY_POINT_LIST_ONE_DAY, null);

            //加载血糖数据
            ObjectLoader<SugarControl> loader3 = new ObjectLoader<SugarControl>();
            loader.setThreadId(loader.POOL_THREAD_2);
            loader3.loadBodyObject(SugarControl.class, ConfigUrlMrg.RECORD_SUGAR_SET, null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
