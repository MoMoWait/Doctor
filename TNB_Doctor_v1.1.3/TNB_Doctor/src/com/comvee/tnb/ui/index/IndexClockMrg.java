package com.comvee.tnb.ui.index;

import android.os.CountDownTimer;

import com.comvee.BaseApplication;
import com.comvee.tool.UserMrg;

import java.util.ArrayList;

/**
 * 首页 闹钟  时间管理类
 * Created by friendlove-pc on 16/3/20.
 */
public class IndexClockMrg {

    public static int PENDING_CODE = 100111;
    private static IndexClockMrg mInstance;
    private ArrayList<CallBack> mLists = new ArrayList<CallBack>();
    private MyCount mTimer;
    private int curTick;//当前嘀嗒到第几秒
    private int allTick = 7200;//全部嘀嗒两小时完成
    private boolean isRuning;


    private IndexClockMrg() {
    }

    public static IndexClockMrg getInstance() {

        if (mInstance == null) {
            return mInstance = new IndexClockMrg();
        } else {
            return mInstance;
        }

    }

    public boolean isRuning() {
        return isRuning;
    }

    public void addCallBack(CallBack call) {

        if(!mLists.contains(call))
        mLists.add(call);
    }

    public void removeCallBack(CallBack call){
        mLists.remove(call);
    }


    public void pause() {
        isRuning = false;
        if (mTimer != null) {
            mTimer.cancel();
        }
    }


    public void resume() {
        if (null != UserMrg.DEFAULT_MEMBER) {
            long time = UserMrg.getMemberMealClockTime(UserMrg.DEFAULT_MEMBER.mId);
            long duraton = (System.currentTimeMillis() - time) / 1000;
            if (time != 0 && duraton < allTick) {
                curTick = allTick - (int) duraton;
                if (mTimer != null) {
                    mTimer.cancel();
                }
                isRuning = true;
                mTimer = new MyCount();
                mTimer.start();
            }
        }

    }

    public void reStart() {
        UserMrg.setMemberMealClockTime(UserMrg.DEFAULT_MEMBER.mId, System.currentTimeMillis());
        IndexModel.addTempTime(BaseApplication.getInstance(), PENDING_CODE, 2 * 60 * 60 * 1000);

        curTick = allTick;
        if (mTimer != null) {
            mTimer.cancel();
        }
        isRuning = true;

        mTimer = new MyCount();
        mTimer.start();
    }

    public void cancel() {
        isRuning = false;
        UserMrg.setMemberMealClockTime(UserMrg.DEFAULT_MEMBER.mId, 0);
        if (mTimer != null) {
            mTimer.cancel();
        }
        for (CallBack cal : mLists) {
            cal.onCancel();
        }
    }

    public int getCurTick() {
        return curTick;
    }

    public void setCurTick(int tick) {
        this.curTick = tick;
    }


    interface CallBack {
        void onTick(int tick);
        void onCancel();
    }

    /* 定义一个倒计时的内部类 */
    class MyCount extends CountDownTimer {
        public MyCount() {
            super(allTick * 1000, 1000);
        }

        @Override
        public void onFinish() {
            isRuning = false;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            for (CallBack cal : mLists) {
                cal.onTick(curTick);
            }
            curTick--;
        }

    }
}
