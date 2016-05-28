package com.comvee.tnb.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.comvee.frame.BaseFragmentActivity;
import com.comvee.frame.FragmentMrg;
import com.comvee.log.ComveeLog;
import com.comvee.tnb.DrawerMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.db.DBManager;
import com.comvee.tnb.dialog.CustomProgressNewDialog;
import com.comvee.tnb.dialog.LunchActivityDialog;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.PushInfo;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.index.LeftFragment;
import com.comvee.tnb.ui.more.LuancherFragment;
import com.comvee.tnb.ui.more.MoreFragment;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tnb.widget.ResideLayout;
import com.comvee.tool.AppUtil;
import com.comvee.tool.UmenPushUtil;
import com.comvee.tool.UserMrg;
import com.comvee.util.BundleHelper;
import com.comvee.util.Util;

/**
 * Created by friendlove-pc on 16/3/21.
 */
public class MainActivity extends BaseFragmentActivity {
    public static boolean isStart;
    private CustomProgressNewDialog mProDialog;
    private LeftFragment mLeftFrag;// 左侧栏
    private boolean isAlreadyShowNetworkToast;//是否提示过网络未开启
    private BroadcastReceiver mMainReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }
            String action = intent.getAction();
            if (TnbBaseNetwork.ACTION_TO_LOGIN.equals(action)) {//重新登录
                FragmentMrg.removeAllFragment(MainActivity.this);
                toFragment(LoginFragment.class, null, false);
            } else if (TnbBaseNetwork.ACTION_NO_NETWORK.equals(action)) {
                if (!isAlreadyShowNetworkToast) {
                    Toast.makeText(getApplicationContext(), "请检查您的网络是否开启", Toast.LENGTH_SHORT).show();
                    isAlreadyShowNetworkToast = true;
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
//        UITool.setImmersive(this.getWindow(), true);
//        UITool.setMiuiStatusBarDarkMode(getWindow(), true);
//        UITool.setMeizuStatusBarDarkIcon(getWindow(), true);
        super.onCreate(arg0);
        //应用市场评论
        AppUtil.initCheckGrade_new(MainActivity.this);
        //检测版本更新
        MoreFragment.upData(MainActivity.this, false);
        //数据库检测版本号
        DBManager.checkDbVesion(getApplicationContext());
        //请求文字说明接口
        ConfigParams.updateConfig(getApplication());
        final ResideLayout resideLayout = (ResideLayout) findViewById(R.id.drawer_layout);
        //初始化左侧栏
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_left, mLeftFrag = new LeftFragment()).commit();
        // 初始化侧边栏
        DrawerMrg.getInstance().init(resideLayout, mLeftFrag);
        //初始化，判断跳转到哪个页面
        initFrag();
        //初始化友盟推送
         UmenPushUtil.onCreateActivity(MainActivity.this, getIntent());
        //闪屏
        LunchActivityDialog dialog = new LunchActivityDialog();
        dialog.setImgRes(R.drawable.huanyingye);
        dialog.show(getSupportFragmentManager(), "launch");
    }

    private void initFrag() {

        if (Util.checkFirst(getApplicationContext(), "first_Launch")) {//安装后第一次启动
            FragmentMrg.toFragment(this, LuancherFragment.class, null, false);
        } else if (UserMrg.DEFAULT_MEMBER == null) {//默认成员为空
            FragmentMrg.toFragment(this, LuancherFragment.class, null, false);
        } else if (UserMrg.getTestData(getApplicationContext()) && !TextUtils.isEmpty(UserMrg.getSessionId(getApplicationContext()))) {
            //是游客用户
            IndexFrag.toFragment(this, false);
        } else if (!TextUtils.isEmpty(UserMrg.getSessionId(getApplicationContext()))) {//sid不为空
            IndexFrag.toFragment(this, false);
        } else {
            FragmentMrg.toFragment(this, LuancherFragment.class, null, false);
        }
    }

    @Override
    public boolean isProgressDialogShowing() {
        return mProDialog != null && mProDialog.isShowing();
    }

    @Override
    public void showProgressDialog(String msg) {
        if (mProDialog == null) {
            mProDialog = CustomProgressNewDialog.createDialog(this);
//            UITool.setImmersive(mProDialog.getWindow(), true);
//            UITool.setMiuiStatusBarDarkMode(mProDialog.getWindow(), true);
//            UITool.setMeizuStatusBarDarkIcon(mProDialog.getWindow(), true);
        }
        if (!mProDialog.isShowing() && !LunchActivityDialog.isShow) {
            mProDialog.show(msg, CustomProgressNewDialog.WHITE_BACKGROUND);
        }
    }

    @Override
    public void cancelProgressDialog() {
        if (mProDialog != null) {
            mProDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UmenPushUtil.onDestoryActivity(this);
        ComveeLog.syncData();
        //创建快捷键
        if (Util.checkFirst(getApplicationContext(), "first")) {
            Util.shortcut(getApplicationContext());
        }

        //退出，预加载图片
        LunchActivityDialog.loadLaunchData();

    }

    @Override
    protected void onResume() {
        isStart = true;
        super.onResume();
        ComveeLog.onResumeActivity(getClass().getSimpleName(), Util.getMetaData(getApplicationContext(), "APP_CHANNEL", null),
                UserMrg.getSessionId(getApplicationContext()));
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(TnbBaseNetwork.ACTION_TO_LOGIN);
            filter.addAction(TnbBaseNetwork.ACTION_NO_NETWORK);
            registerReceiver(mMainReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        isStart = false;
        super.onPause();
        ComveeLog.onPauseActivity(getClass().getSimpleName());
        try {
            unregisterReceiver(mMainReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
