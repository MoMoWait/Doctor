package com.comvee.tnb.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;


import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tool.UserMrg;
import com.comvee.util.TimeUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConfigParams {

    public static final String Guide_Blood_Sugar = "GuideBloodSugar";
    public static final float MIN_beforeBreakfast_ISTNB = 4.4f;
    public static final float MAX_beforeBreakfast_ISTNB = 7.2f;
    public static final float MIN_beforeBreakfast_NOTNB = 3.62f;
    public static final float MAX_beforeBreakfast_NOTNB = 6.99f;
    public static final float MIN_NObeforeBreakfast_ISTNB = 4.4f;
    public static final float MAX_NObeforeBreakfast_ISTNB = 10.0f;
    public static final float MIN_NObeforeBreakfast_NOTNB = 4.41f;
    public static final float MAX_NObeforeBreakfast_NOTNB = 11.09f;
    public static final String ACTION_BLOOD = "ACTION_BLOOD";// 来自血糖仪器的数据
    public final static int STATUS_LAST = -10021;
    public final static int STATUS_FIRT_PAGE = -10022;
    public final static int STATUS_FIRT_AND_LAST = -10023;
    // ***********ACTION**********//
    public static final String ACTION_ATOU_LOGIN = "ACTION_ATOU_LOGIN";
    public static final String ACTION_INDEX_UPDATE = "ACTION_INDEX_UPDATE";
    // ===========套餐CODE============//
    public static final String PACKAGE_ASK = "FWXW0017";
    public static final String PACKAGE_ASSESS = "FWXW0014";// 健康评估
    public static final String IMG_CACHE_PATH = Environment.getExternalStorageDirectory() + "/.tnb12";
    public static final int IMG_HEAD_WIDHT = 100;
    public static final int IMG_HEAD_HEIGHT = 100;
    public static final int IMG_WIDHT = 800;
    // ===========套餐CODE============//
    public static final int IMG_HEIGHT = 800;
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMAT1 = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FORMAT_1 = "MM-dd";
    public final static String DB_NAME = "tnb_remind.db";
    public static final long WEEK_TIME_LONG = 7 * 24 * 60 * 60 * 1000;
    public static final long DAY_TIME_LONG = 24 * 60 * 60 * 1000;
    public static final long CHACHE_TIME_LONG = 3 * 60 * 60 * 1000;
    public static final long CHACHE_TIME_SHORT = 10 * 60 * 1000;
    public static final int PAGE_COUNT = 100;// 一页的个数
    public static final int TEXT_SIZE_WHEEL = 18;
    public static final int[] SHADOWS_COLORS = new int[]{0xFFf8f9fa, 0xb9f8f9fa, 0xb9f8f9fa};
    public static final String[] strWeeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    public static final String[] strWeeks_1 = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    // 在百度开发者中心查询应用的API Key
    public static final String API_KEY = "MY67XzyyRwlPVDDA11Iz8IMo";
    public static final String RESPONSE_METHOD = "method";
    public static final String RESPONSE_CONTENT = "content";
    public static final String RESPONSE_ERRCODE = "errcode";
    public static final String ACTION_PUSH_CLICK = "com.baidu.pushdemo.action.ACTION_PUSH_CLICK";
    public static final String ACTION_LOGIN = "com.baidu.pushdemo.action.LOGIN";
    public static final String ACTION_MESSAGE = "com.baiud.pushdemo.action.MESSAGE";
    public static final String ACTION_RESPONSE = "bccsclient.action.RESPONSE";
    public static final String ACTION_SHOW_MESSAGE = "bccsclient.action.SHOW_MESSAGE";
    public static final String EXTRA_ACCESS_TOKEN = "access_token";
    public static final String EXTRA_MESSAGE = "message";
    public static final String TEXT_ABOUT = "about";// 关于
    public static final String TEXT_STATEMENT = "statement";// 免责声明
    public static final String TEXT_RELATION = "";
    public static final String TEXT_SERVER = "";// 服务介绍
    public static final String TEXT_APP_URL = "";// 下载地址
    public static final String TEXT_ASK_SHARE = "";
    public static final String TEXT_NEWS_SHARE = "";
    public static final String TEXT_WEIBO = "weibo_suggest";
    public static final String TEXT_LOG_URL = "app_update_log";
    public static final String TEXT_ASSESS_INTRODUCE = "assessment_introduce";
    public static final String TEXT_ASSESS_DEMO = "assessment_demo";
    public static final String TEXT_VIP_LOGIN = "thp_vip_hyfw_login";
    public static final String TEXT_VIP_UNLOGIN = "thp_vip_hyfw_nologin";
    public static final String TEXT_VIP_RELOGIN = "thp_vip_hyfw_register";
    public static final String TEXT_FIRST_LUANCHER_HELP = "diabetes_plan";
    public static final String TEXT_MECHINE_INFO = "mechine_introduce";
    public static final String TEXT_PAY_URL = "thp_machine_order_buy";
    public static final String TEXT_GREEN_PLAN = "greenStar";
    public static final String TEXT_URL = "healthIndex";
    public static final String Text_HEALTH_URL = "healthUrl";
    public static final String COOK_BOOK = "cookbook";
    public static final String MYORDER_URL="myorder_url";
    public static final String[] textCodes = {TEXT_PAY_URL, TEXT_FIRST_LUANCHER_HELP, TEXT_VIP_LOGIN, TEXT_VIP_UNLOGIN, TEXT_VIP_RELOGIN,
            TEXT_ASSESS_DEMO, TEXT_ASSESS_INTRODUCE, TEXT_LOG_URL, TEXT_WEIBO, TEXT_ASK_SHARE, TEXT_NEWS_SHARE, TEXT_ABOUT, TEXT_STATEMENT,
            TEXT_RELATION, TEXT_SERVER, TEXT_APP_URL, TEXT_MECHINE_INFO, TEXT_GREEN_PLAN, TEXT_URL, Text_HEALTH_URL, COOK_BOOK,MYORDER_URL};
    public static final String[] MEDICINAL_TYPE = {"1", "2", "3", "4"};// 药品类型，1降糖药，2降脂，3降压，4胰岛素
    public static final String[] SUGAR_TIME_CODE = {"beforedawn", "beforeBreakfast", "afterBreakfast", "beforeLunch", "afterLunch", "beforeDinner",
            "afterDinner", "beforeSleep"};
    public static final String[] TIMES_SUGAR = {"00:00", "03:00", "08:00", "10:00", "12:00", "16:00", "18:00", "20:00"};
    public static final String[] SUGAR_TIME_STR = TNBApplication.getInstance().getResources().getStringArray(R.array.sugar_time_str1);
    public static final String[] SUGAR_TIME_STR2 = TNBApplication.getInstance().getResources().getStringArray(R.array.sugar_time_str);
    public static final String[] SUGAR_TIME_STR1 = TNBApplication.getInstance().getResources().getStringArray(R.array.sugar_time_str2);
    public static final String[] SUGAR_TIME_STR3 = TNBApplication.getInstance().getResources().getStringArray(R.array.sugar_time_str3);
    private static final String TN = "config1";
    public static boolean isShowDiscount = true;
    public static boolean IS_TEST_DATA;// 未登入、模拟数据
    public static boolean IS_LOGIN;
    public static boolean IS_AUTO_LOGIN_QQ = true;
    public static String ASK_DOCID_MEMID = null;
    public static boolean ASKLIST_IS_REFRESH = false;
    public static int TO_BACK_TYPE = 1;// 1 直接返回 2 返回医生列表 3返回首页
    public static String[] HOURS = {"00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00",
            "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", "24:00"};

    public static String getOnlineConfig(Context cxt, String msg) {
        return cxt.getSharedPreferences(TN, 0).getString(msg, "");
    }

    public static void setDbVesionCode(Context cxt, int vesion) {
        cxt.getSharedPreferences(TN, 0).edit().putInt("vesion", vesion).commit();
    }

    public static int getDbVesionCode(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getInt("vesion", 0);
    }

    public static void updateConfig(final Context cxt) {

        TnbBaseNetwork request = new TnbBaseNetwork() {
            @Override
            protected void onDoInMainThread(int status, Object obj) {

            }

            @Override
            protected Object parseResponseJsonData(JSONObject packet) {
                SharedPreferences.Editor edit = cxt.getSharedPreferences(TN, 0).edit();
                try {
                    JSONArray array = packet.getJSONArray("body");
                    int len = array.length();
                    for (int i = 0; i < len; i++) {
                        JSONObject obj = array.optJSONObject(i);
                        String time = obj.optString("time");
                        String code = obj.optString("code");
                        String info = obj.optString("info");
                        edit.putString(code + "_time", time);

                        if (!TextUtils.isEmpty(info)) {
                            edit.putString(code, info);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                edit.commit();

                return null;
            }

            @Override
            protected void initRequestEntity(RequestParams entry) {
                super.initRequestEntity(entry);
                SharedPreferences sp = cxt.getSharedPreferences(TN, 0);

                JSONArray array = new JSONArray();
                for (String str : textCodes) {
                    try {

                        if (!TextUtils.isEmpty(str)) {
                            String time = sp.getString(str + "_time", "");
                            JSONObject obj = new JSONObject();
                            obj.put("code", str);
                            obj.put("time", time);
                            array.put(obj);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                putPostValue("paramKey", array.toString());
            }

            @Override
            public String getUrl() {
                return ConfigUrlMrg.MORE_TEXT_INFO;
            }
        };
        request.start(request.POOL_THREAD_2);


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ComveeHttp http = new ComveeHttp(cxt, ConfigUrlMrg.MORE_TEXT_INFO);
//
//                SharedPreferences sp = cxt.getSharedPreferences(TN, 0);
//
//                JSONArray array = new JSONArray();
//                for (String str : textCodes) {
//                    try {
//
//                        if (!TextUtils.isEmpty(str)) {
//                            String time = sp.getString(str + "_time", "");
//                            JSONObject obj = new JSONObject();
//                            obj.put("code", str);
//                            obj.put("time", time);
//                            array.put(obj);
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                http.setPostValueForKey("paramKey", array.toString());
//
//                SharedPreferences.Editor edit = cxt.getSharedPreferences(TN, 0).edit();
//                try {
//                    ComveePacket packet = ComveePacket.fromJsonString(http.startSyncRequestString());
//                    array = packet.getJSONArray("body");
//                    int len = array.length();
//                    for (int i = 0; i < len; i++) {
//                        JSONObject obj = array.optJSONObject(i);
//                        String time = obj.optString("time");
//                        String code = obj.optString("code");
//                        String info = obj.optString("info");
//                        edit.putString(code + "_time", time);
//
//                        if (!TextUtils.isEmpty(info)) {
//                            edit.putString(code, info);
//                        }
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                edit.commit();
//            }
//        }).start();

    }

    public static String getCurBloodTimeString(String[] suge) {

        int index = TIMES_SUGAR.length - 1;
        for (int i = TIMES_SUGAR.length - 1; i >= 0; i--) {
            try {
                long time1 = TimeUtil.getUTC(TIMES_SUGAR[i], "HH:mm");
                long time2 = TimeUtil.getUTC(TimeUtil.fomateTime(System.currentTimeMillis(), "HH:mm"), "HH:mm");
                index = i;
                if (time2 > time1) {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return suge[index];

    }

    public static int getClearBloodTimeString() {

        int index = TIMES_SUGAR.length - 1;
        for (int i = TIMES_SUGAR.length - 1; i >= 0; i--) {
            try {
                long time1 = TimeUtil.getUTC(TIMES_SUGAR[i], "HH:mm");
                long time2 = TimeUtil.getUTC(TimeUtil.fomateTime(System.currentTimeMillis(), "HH:mm"), "HH:mm");
                index = i;
                if (time2 > time1) {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return index;

    }

    public static int getCurBloodTimeIndex() {
        return getCurBloodTimeIndex(TimeUtil.fomateTime(System.currentTimeMillis(), "HH:mm"));
    }

    public static int getCurBloodTimeIndex(String time) {
        String str[] = time.split(" ");
        int index = TIMES_SUGAR.length - 1;
        for (int i = TIMES_SUGAR.length - 1; i >= 0; i--) {
            try {
                long time1 = TimeUtil.getUTC(TIMES_SUGAR[i], "HH:mm");
                long time2 = TimeUtil.getUTC(str[1], "HH:mm");
                index = i;
                if (time2 > time1) {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return index;

    }

    public static String getConfig(Context cxt, String code) {
        return cxt.getSharedPreferences(TN, 0).getString(code, "");
    }

    public static void setAnswerNew(Context cxt, boolean b) {
        cxt.getSharedPreferences(TN, 0).edit().putBoolean(UserMrg.DEFAULT_MEMBER.mId + "answer_new", b).commit();
    }

    public static void setReportNew(Context cxt, boolean b) {
        cxt.getSharedPreferences(TN, 0).edit().putBoolean(UserMrg.DEFAULT_MEMBER.mId + "report_new", b).commit();
    }

    public static void setTaskNew(Context cxt, boolean b) {
        cxt.getSharedPreferences(TN, 0).edit().putBoolean(UserMrg.DEFAULT_MEMBER.mId + "task_new", b).commit();
    }

    public static boolean isAnswerNew(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getBoolean(UserMrg.DEFAULT_MEMBER.mId + "answer_new", false);
    }

    public static boolean isReportNew(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getBoolean(UserMrg.DEFAULT_MEMBER.mId + "report_new", false);
    }

    public static boolean isTaskNew(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getBoolean(UserMrg.DEFAULT_MEMBER.mId + "task_new", false);
    }

    public static void setAssessNum(Context cxt, int num) {
        cxt.getSharedPreferences(TN, 0).edit().putInt("assess_num", num).commit();
    }

    public static void setNewJobNum(Context cxt, int num) {
        cxt.getSharedPreferences(TN, 0).edit().putInt("new_job_num", num).commit();
    }

    public static int getAssessNum(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getInt("assess_num", 1000);
    }

    public static int getNewJobNum(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getInt("new_job_num", 0);
    }

    public static String getHostAdrr(Context cxt, String url, boolean isUrl) {
        if (isUrl) {
            return cxt.getSharedPreferences("addrss", 0).getString("addr", url);
        } else {
            return url;
        }

    }

    public static void setHostAddr(Context cxt, String addr) {
        cxt.getSharedPreferences("addrss", 0).edit().putString("addr", addr).commit();
    }

    public static void setFirstRegist(Context cxt, boolean b) {
        cxt.getSharedPreferences(TN, 0).edit().putBoolean("firstRegist", b).commit();
    }

    public static boolean getFirstRegist(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getBoolean("firstRegist", true);
    }

    public static void setDeleteRemindTable(Context cxt, boolean b) {
        cxt.getSharedPreferences(TN, 0).edit().putBoolean("deleteremindtable", b).commit();
    }

    public static boolean isDeleteRemindTable(Context cxt) {
        return cxt.getSharedPreferences(TN, 0).getBoolean("deleteremindtable", true);
    }

    /**
     * 血糖获取记录设置信息
     *
     * @param cxt
     * @param key
     * @param defualt
     * @return
     */
    public static long getInt(Context cxt, String key) {
        return cxt.getSharedPreferences(TN, 0).getLong(key, 0);
    }

    public static long getInt(Context cxt, String key, long defualt) {
        return cxt.getSharedPreferences(TN, 0).getLong(key, defualt);
    }

    public static void setInt(Context cxt, String key, long value) {
        cxt.getSharedPreferences(TN, 0).edit().putLong(key, value).commit();
    }

    public static void setGuideReadMsg(Context cxt, String msg) {
        cxt.getSharedPreferences(TN, 0).edit().putString("GuideReadMsg", msg).commit();
    }

    public static String getGuideReadMsg(Context cxt) {
        return cxt.getSharedPreferences(TN, 1).getString("GuideReadMsg", null);
    }

    public static String getNetImgUrl(Context cxt) {
        return cxt.getSharedPreferences(TN, 1).getString("NetImgUrl", null);
    }

    public static void SetNetImgUrl(Context cxt, String str) {
        cxt.getSharedPreferences(TN, 0).edit().putString("NetImgUrl", str).commit();
    }

    public static String getHealthUrl(Context cxt) {
        return cxt.getSharedPreferences(TN, 1).getString("HealthUrl", null);
    }

    public static void SetHealthUrl(Context cxt, String str) {
        cxt.getSharedPreferences(TN, 0).edit().putString("HealthUrl", str).commit();
    }

    public static void setHealthTimes(Context cxt, int time) {
        cxt.getSharedPreferences(TN, 0).edit().putInt("healthTime", time).commit();
    }

    public static int getHealthTimes(Context cxt) {
        return cxt.getSharedPreferences(TN, 1).getInt("healthTime", 2500);
    }

    public static void setTimeQuantum(Context cxt, int time) {
        cxt.getSharedPreferences(TN, 0).edit().putInt("TimeQuantum", time).commit();
    }

    public static int getTimeQuantum(Context cxt) {
        return cxt.getSharedPreferences(TN, 1).getInt("TimeQuantum", 0);
    }

    public static void setDraftValue(Context cxt, String key, String value) {
        cxt.getSharedPreferences(TN, 0).edit().putString(key, value).commit();
    }

    public static String getDraftValue(Context cxt, String key) {
        return cxt.getSharedPreferences(TN, 1).getString(key + "", null);
    }

    public static boolean getAskServerKey(Context cxt, String key) {
        return cxt.getSharedPreferences(TN, 1).getBoolean(key, false);
    }

    public static void setAskServerKey(Context cxt, String key, boolean value) {
        cxt.getSharedPreferences(TN, 0).edit().putBoolean(key, value).commit();
    }

    public static int getAskKey(Context cxt, String key) {
        return cxt.getSharedPreferences(TN, 1).getInt(key, 0);
    }

    public static void setAskKey(Context cxt, String key, int value) {
        cxt.getSharedPreferences(TN, 0).edit().putInt(key, value).commit();
    }

    public static void setMsgSysCount(Context cxt, int count) {
        cxt.getSharedPreferences(TN, 0).edit().putInt("msgSysCount", count).commit();
    }

    public static int getMsgSysCount(Context cxt) {
        return cxt.getSharedPreferences(TN, 1).getInt("msgSysCount", 0);
    }

    public static void setMsgDocCount(Context cxt, int count) {
        cxt.getSharedPreferences(TN, 0).edit().putInt("msgDocCount", count).commit();
    }

    public static int getMsgDocCount(Context cxt) {
        return cxt.getSharedPreferences(TN, 1).getInt("msgDocCount", 0);
    }

    public static String getActivityUrl(Context cxt) {
        return cxt.getSharedPreferences(TN, 1).getString("activityUrl", "");
    }

    public static void setActivityUrl(Context cxt, String url) {
        cxt.getSharedPreferences(TN, 0).edit().putString("activityUrl", url).commit();
    }

    public static String getActivityTitle(Context cxt) {
        return cxt.getSharedPreferences(TN, 1).getString("activityTitle", "活动详情");
    }

    public static void setActivityTitle(Context cxt, String Title) {
        cxt.getSharedPreferences(TN, 0).edit().putString("activityTitle", Title).commit();
    }

    public static void setExceprionDbUrl(Context cxt, String url) {
        cxt.getSharedPreferences(TN, 0).edit().putString("ExceprionDbUrl", url).commit();
    }

    public static String getExceprionDbUrl(Context cxt) {
        return cxt.getSharedPreferences(TN, 1).getString("ExceprionDbUrl", null);
    }

    public static void setNowCity(Context cxt, String NowCity) {
        cxt.getSharedPreferences(TN, 0).edit().putString("NowCity", NowCity).commit();
    }

    public static String getNowCity(Context cxt) {
        return cxt.getSharedPreferences(TN, 1).getString("NowCity", "");
    }

    public static void setSugarHightValue(Context cxt, String SugarHightValue) {
        cxt.getSharedPreferences(TN, 0).edit().putString("SugarHightValue", SugarHightValue).commit();
    }

    public static String getSugarHightValue(Context cxt) {
        return cxt.getSharedPreferences(TN, 1).getString("SugarHightValue", "");
    }

    public static void setSugarLowValue(Context cxt, String SugarLowValue) {
        cxt.getSharedPreferences(TN, 0).edit().putString("SugarLowValue", SugarLowValue).commit();
    }

    public static String getSugarLowValue(Context cxt) {
        return cxt.getSharedPreferences(TN, 1).getString("SugarLowValue", "");
    }
}
