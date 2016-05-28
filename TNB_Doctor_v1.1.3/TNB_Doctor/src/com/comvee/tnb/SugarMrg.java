package com.comvee.tnb;

import android.content.SharedPreferences;
import android.graphics.Color;

import com.comvee.BaseApplication;
import com.comvee.tool.UserMrg;
import com.comvee.util.TimeUtil;

public class SugarMrg {

    public static final float WHEEL_MAX = 33;//可滑动的  最大值
    public static final float WHEEL_MIN = 1;//可滑动的   最低值

    public static final int LEVEL_LOW = 1;
    public static final int LEVEL_HIGH = 5;
    public static final int LEVEL_NORMAL = 3;

    public static final String LEVEL_LOW_COLOR = "#0090ff";
    public static final String LEVEL_HIGH_COLOR = "#ff4200";
    public static final String LEVEL_NORMAL_COLOR = "#00be00";
    /**
     * 凌晨 { "00:00", "03:00" }
     * 空腹{ "03:00", "08:00" }
     * 早餐后{ "08:00", "10:00" }
     * 午餐前{ "10:00", "12:00" }
     * 午餐后{ "12:00", "16:00" },
     * 晚餐前{ "16:00", "18:00" }
     * 晚餐后{ "18:00", "20:00" }
     * 睡前{ "20:00", "23:59" }
     */
    public static final String[][] TIMES_SUGAR = {{"00:00", "03:00"}, {"03:00", "08:00"}, {"08:00", "10:00"}, {"10:00", "12:00"}, {"12:00", "16:00"},
            {"16:00", "18:00"}, {"18:00", "20:00"}, {"20:00", "23:59"}};
    /**
     * 空腹血糖控制目标
     */
    public static float[] SUGAR_EMPTY_LIMIT = {4.4f, 7.0f};
    /**
     * 餐后血糖控制目标
     */
    public static float[] SUGAR_MEAL_LIMIT = {4.4f, 10.0f};
    public static String SUGAR_EMPTY_CODE = "beforeBreakfast";
    //	public static String[] SUGAR_RANGE_TEXT = { "空腹", "早餐后2小时", "午餐前", "午餐后2小时", "晚餐前", "晚餐后2小时", "晚间10点", "晚间0点", "凌晨", };
    public static String[] SUGAR_RANGE_TEXT = {"凌晨", "空腹", "早餐后", "午餐前", "午餐后", "晚餐前", "晚餐后", "睡前"};
    public static String[] SUGAR_RANGE_CODE = {"beforedawn", "beforeBreakfast", "afterBreakfast", "beforeLunch", "afterLunch", "beforeDinner", "afterDinner", "beforeSleep"};

    static {
        SharedPreferences sp = BaseApplication.getInstance().getSharedPreferences(UserMrg.TN, 0);
        float emptyLow = sp.getFloat("emptyLow", SUGAR_EMPTY_LIMIT[0]);
        float emptyHigh = sp.getFloat("emptyHigh", SUGAR_EMPTY_LIMIT[1]);
        float mealLow = sp.getFloat("mealLow", SUGAR_MEAL_LIMIT[0]);
        float mealHigh = sp.getFloat("mealHigh", SUGAR_MEAL_LIMIT[1]);

        if (emptyLow != 0) {
            SUGAR_EMPTY_LIMIT[0] = emptyLow;
        }

        if (emptyHigh != 0) {
            SUGAR_EMPTY_LIMIT[1] = emptyHigh;
        }

        if (mealLow != 0) {
            SUGAR_MEAL_LIMIT[0] = mealLow;
        }
        if (mealHigh != 0) {
            SUGAR_MEAL_LIMIT[1] = mealHigh;
        }
    }

    /**
     * 设置血糖控制目标
     *
     * @param emptyLow
     * @param emptyHigh
     * @param mealLow
     * @param mealHigh
     */
    public static void setLevelControl(float emptyLow, float emptyHigh, float mealLow, float mealHigh, String updateTime) {

        SUGAR_EMPTY_LIMIT[0] = emptyLow;
        SUGAR_EMPTY_LIMIT[1] = emptyHigh;
        SUGAR_MEAL_LIMIT[0] = mealLow;
        SUGAR_MEAL_LIMIT[1] = mealHigh;

        SharedPreferences sp = BaseApplication.getInstance().getSharedPreferences(UserMrg.TN, 0);
        sp.edit().putFloat("emptyLow", emptyLow).putFloat("emptyHigh", emptyHigh).putFloat("mealLow", mealLow).putFloat("mealHigh", mealHigh)
                .putString("target_dt", updateTime).commit();
    }

    public static String getLevelControlUpdateTime() {
        String target_dt = BaseApplication.getInstance().getSharedPreferences(UserMrg.TN, 0).getString("target_dt", null);
        return target_dt;
    }

    /**
     * 获取血糖偏高的极值
     *
     * @param rangeCode 时段code
     * @return
     */
    public static float getHighValue(String rangeCode) {
        return SUGAR_EMPTY_CODE.equals(rangeCode) ? SUGAR_EMPTY_LIMIT[1] : SUGAR_MEAL_LIMIT[1];
    }

    /**
     * 获取血糖偏低的极值
     *
     * @param rangeCode 时段code
     * @return
     */
    public static float getLowValue(String rangeCode) {
        return SUGAR_EMPTY_CODE.equals(rangeCode) ? SUGAR_EMPTY_LIMIT[0] : SUGAR_MEAL_LIMIT[0];
    }

    /**
     * 获取空腹血糖 的等级
     *
     * @param sugarValue
     * @return 2偏低3正常4偏高
     */
    public static int getEmptySugarLevel(float sugarValue) {
        if (sugarValue < SUGAR_EMPTY_LIMIT[0]) {
            return LEVEL_LOW;
        } else if (sugarValue > SUGAR_EMPTY_LIMIT[1]) {
            return LEVEL_HIGH;
        } else {
            return LEVEL_NORMAL;
        }
    }

    public static String getSugarLevelString(int level) {
        switch (level) {
            case LEVEL_HIGH:
                return "偏高";
            case LEVEL_LOW:
                return "偏低";
            case LEVEL_NORMAL:
                return "正常";
        }
        return "";
    }

    /**
     * 获取餐后血糖 的等级
     *
     * @param sugarValue
     * @return 2偏低3正常4偏高
     */
    public static int getMealSugarLevel(float sugarValue) {
        if (sugarValue < SUGAR_MEAL_LIMIT[0]) {
            return LEVEL_LOW;
        } else if (sugarValue > SUGAR_MEAL_LIMIT[1]) {
            return LEVEL_HIGH;
        } else {
            return LEVEL_NORMAL;
        }
    }

    /**
     * 获取 血糖 等级
     *
     * @param sugarValue 血糖值
     * @param rangeCode  血糖时段
     * @return 2偏低3正常4偏高
     */
    public static int getSugarLevel(float sugarValue, String rangeCode) {
        if (SUGAR_EMPTY_CODE.equals(rangeCode)) {
            return getEmptySugarLevel(sugarValue);
        } else {
            return getMealSugarLevel(sugarValue);
        }
    }

    /**
     * 获取当前血糖时段坐标
     *
     * @return
     */
    public static int getCurrentSugarIndex() {
        return getSugarIndex(TimeUtil.fomateTime(System.currentTimeMillis(), "HH:mm"));
    }

    /**
     * 获取血糖时段code
     *
     * @return
     */
    public static String getCurrentSugarCode() {
        return SUGAR_RANGE_CODE[getCurrentSugarIndex()];
    }

    /**
     * 获取血糖时段坐标
     *
     * @param curTime 格式（HH:mm）
     * @return
     */
    private static int getSugarIndex(String curTime) {
        for (int i = 0; i < TIMES_SUGAR.length; i++) {
            if (getSugarCompare(i, curTime)) {
                return i;
            }
        }
        return 0;
    }

    public static int getSugarRangeIndexByCode(String code){
        for (int i = 0; i < SUGAR_RANGE_CODE.length; i++) {
            if (SUGAR_RANGE_CODE[i].equals(code)) {
                return i;
            }
        }
        return 0;
    }


    /**
     * 获取当前血糖时段
     *
     * @return
     */
    public static String getCurrentSugarText() {
        return SUGAR_RANGE_TEXT[getCurrentSugarIndex()];
    }

    /**
     * 获取血糖时段
     *
     * @param time 格式（HH:mm）
     * @return
     */
    public static String getSugarRnageText(String time) {
        return SUGAR_RANGE_TEXT[getSugarIndex(time)];
    }

    public static String getSugarRangeByRangeType(int rangeType) {
        return SUGAR_RANGE_TEXT[rangeType - 1];
    }

    /**
     * 获取血糖时段坐标
     *
     * @param index
     * @param time  格式（HH:mm）
     * @return
     */
    private static boolean getSugarCompare(int index, String time) {
        try {

            long startTime = TimeUtil.getUTC(TIMES_SUGAR[index][0], "HH:mm");
            long endTime = TimeUtil.getUTC(TIMES_SUGAR[index][1], "HH:mm");
            long currentTime = TimeUtil.getUTC(time, "HH:mm");
            if (currentTime > startTime && currentTime <= endTime) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param level 风险值(1偏低3正常5偏高)
     * @return 风险值对应的颜色
     */
    public static int getSugarLevelColor(int level) {
        switch (level) {
            case LEVEL_LOW:
                return Color.parseColor(LEVEL_LOW_COLOR);
            case LEVEL_HIGH:
                return Color.parseColor(LEVEL_HIGH_COLOR);
            case LEVEL_NORMAL:
                return Color.parseColor(LEVEL_NORMAL_COLOR);
        }
        return Color.BLACK;
    }

    /**
     * 获取血糖类型
     *
     * @param type
     * @return 1:1型 1:1型 2;2型 3：妊娠 4:其他
     */
    public static final String getSugarTypeText(int type) {
        switch (type) {
            case 1:
                return "1型";
            case 2:
                return "2型";
            case 3:
                return "妊娠";
            case 4:
                return "其他";
        }
        return null;
    }

}
