package com.comvee.tnb;

import com.comvee.tnb.ui.index.LeftFragment;
import com.comvee.tnb.widget.ResideLayout;

public class DrawerMrg {

    private static DrawerMrg mInstance;
    private ResideLayout mDrawerLayout;
    private LeftFragment mLeft;

    public static DrawerMrg getInstance() {
        return mInstance == null ? mInstance = new DrawerMrg() : mInstance;
    }

    /**
     * 初始化(必须先调用)
     *
     * @param layout
     */
    public void init(ResideLayout layout, LeftFragment mLeft) {
        mDrawerLayout = layout;
        this.mLeft = mLeft;
    }

    /**
     * 关闭
     */
    public void close() {
        if (mDrawerLayout != null)
            mDrawerLayout.closePane();
//			mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    /**
     * 开启
     */
    public void open() {
        if (mDrawerLayout != null)
            mDrawerLayout.openPane();
//			mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    /**
     * @param mode DrawerLayout.LOCK_MODE_LOCKED_CLOSED关闭手势
     *             DrawerLayout.LOCK_MODE_LOCKED_OPEN开启手势
     */
    public void setDrawerLockMode(int mode) {
//		if (mDrawerLayout != null)
//			mDrawerLayout.setDrawerLockMode(mode);
    }

    public void updateLefFtagment() {
        if (mLeft != null) {
            try {
				mLeft.updateView();
            } catch (Exception e) {
            }
        }
    }

    public void closeTouch() {
        if (mDrawerLayout != null)
            mDrawerLayout.setCanTouch(false);
//			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); // 关闭手势滑动
    }

    public void openTouch() {
        if (mDrawerLayout != null)
            mDrawerLayout.setCanTouch(true);
//			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED); // 打开手势滑动
    }

}
