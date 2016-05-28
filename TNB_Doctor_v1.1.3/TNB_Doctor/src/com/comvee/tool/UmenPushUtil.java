package com.comvee.tool;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;

import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.PushIntentService;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.dialog.CustomFragmentDialog;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.PushInfo;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.ui.ask.AskIndexFragment;
import com.comvee.tnb.ui.ask.AskQuestionFragment;
import com.comvee.tnb.ui.assess.AssessFragment;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.remind.RemindDialogActivity;
import com.comvee.util.BundleHelper;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

/**
 * Created by friendlove-pc on 15/8/3.
 */
public class UmenPushUtil {

    public static final String ACTION_PUSH = TNBApplication.getInstance().getPackageName() + ".push";
    public static final String ACTION_PUSH_START = "push_start";

    public static final String ACTION_NOTIFYCATION_CLICK = "ACTION_NOTIFYCATION_CLICK";

    public static void showNotification(Context cxt, PushInfo obj) {
        final String title = obj.title;
        final String msg = obj.decs;
        final NotificationManager nm = (NotificationManager) cxt.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification();
        // notification.number = ++num;
        notification.icon = R.drawable.icon;
        notification.tickerText = title;
        notification.defaults = Notification.DEFAULT_ALL;
        notification.ledOnMS = 300; // 亮的时间
        notification.ledOffMS = 1000; // 灭的时间
        notification.flags |= Notification.FLAG_AUTO_CANCEL; // 在通知栏上点击此通知后自动清除此通知
        Intent intent = new Intent(cxt, PushIntentService.class);
        intent.setAction(ACTION_NOTIFYCATION_CLICK);
        try {
            intent.putExtras(BundleHelper.getBundleBySerializable(obj));
        } catch (Exception e) {
            e.printStackTrace();
        }
        intent.setFlags(0x10100000);
        PendingIntent pt = PendingIntent.getService(cxt, (int) obj.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(cxt, title, msg, pt);
        nm.notify((int) obj.id, notification);
    }

    public static void cancleNotification(Context cxt) {
        NotificationManager nm = (NotificationManager) cxt.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
    }

    // public static void sendBroadcastReceiver(PushInfo info) {
    // Intent buttonIntent = new Intent();
    // buttonIntent.setAction(UmenPushUtil.ACTION_PUSH);
    // buttonIntent.putExtras(BundleHelper.getBundleByObject(info));
    // TNBApplication.getInstance().sendBroadcast(buttonIntent);
    // Log.e("sendBroadcastReceiver");
    // }

    public static void showDialog(Context cxt, PushInfo obj) {
        Intent responseIntent = null;
        responseIntent = new Intent(ConfigParams.ACTION_MESSAGE);
        responseIntent.putExtra("title", obj.title);
        responseIntent.putExtra("msg", obj.decs);
        responseIntent.setClass(cxt, RemindDialogActivity.class);
        responseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        cxt.startActivity(responseIntent);
    }

    /**
     * 收到推送后向mainactivity发送一条广播
     *
     * @param context
     * @param info
     */
    public static void senMainReceiver(Context context, PushInfo info, boolean isToFragment) {
        try {
            // Object info = intent.getSerializableExtra("push");
            if (info != null) {
                Intent intent2 = new Intent();
                intent2.setAction(UmenPushUtil.ACTION_PUSH);
                intent2.putExtra("isToFragment", isToFragment);
                intent2.putExtras(BundleHelper.getBundleBySerializable(info));
                context.sendBroadcast(intent2);
            }
        } catch (Exception e) {
        }
    }

    public static void initPushInApplication() {
        PushAgent mPushAgent = PushAgent.getInstance(TNBApplication.getInstance());
        mPushAgent.setDebugMode(false);
        mPushAgent.setPushIntentServiceClass(PushIntentService.class);
    }

    /**
     * 启动推送
     */
    public static void initPushInActivity() {
        Log.e("initPushInActivity");
        // 如果tokenKey不为空，表示已经获取了key，只需在登陆的时候提交这个key就好了，无需重新获取key绑定
        final Context mContext = TNBApplication.getInstance();
        mContext.sendBroadcast(new Intent(ACTION_PUSH_START));
        PushAgent mPushAgent = PushAgent.getInstance(mContext);
        mPushAgent.onAppStart();

        final String pushKey = getPushTokenKey();
        if (!TextUtils.isEmpty(pushKey) && !TextUtils.isEmpty(UserMrg.getSessionId(mContext))) {
            bindPushtoServer(pushKey);
        }

        mPushAgent.enable(new IUmengRegisterCallback() {
            @Override
            public void onRegistered(String registrationId) {
                if (!TextUtils.isEmpty(UserMrg.getSessionId(mContext)) && !TextUtils.isEmpty(pushKey))
                    bindPushtoServer(pushKey);
            }
        });
    }

    public static final void bindPushtoServer(final String pushKey) {
        ObjectLoader<String> loader = new ObjectLoader<String>();
        loader.setNeedCache(false);
        loader.setThreadId(loader.POOL_THREAD_2);
        loader.putPostValue("channelId", pushKey);
        loader.putPostValue("pushTokenKey", pushKey);
        loader.loadBodyObject(String.class, ConfigUrlMrg.BIND_PUSH, null);
    }

    public static final String getPushTokenKey() {
        return UmengRegistrar.getRegistrationId(TNBApplication.getInstance());
    }

    private static BroadcastReceiver mMainReceiver;

    public static final void onDestoryActivity(final FragmentActivity act) {
        if (mMainReceiver != null) {
            try {
                act.unregisterReceiver(mMainReceiver);
               mMainReceiver = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static final void onCreateActivity(final FragmentActivity act, final Intent intent) {

        if (mMainReceiver == null) {
            mMainReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent == null || intent.getAction() == null) {
                        return;
                    }
                    String action = intent.getAction();
                    if (UmenPushUtil.ACTION_PUSH.equals(action)) {//推送信息
                        try {
                            UmenPushUtil.onPushOptionInMainActivity(act, (PushInfo) BundleHelper.getSerializableByBundle(intent.getExtras()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (UmenPushUtil.ACTION_NOTIFYCATION_CLICK.equals(action)) {
                        UmenPushUtil.jumpFragment(act, (PushInfo) BundleHelper.getSerializableByBundle(intent.getExtras()));
                    }

                }
            };
        }
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(UmenPushUtil.ACTION_PUSH);
            filter.addAction(TnbBaseNetwork.ACTION_NO_NETWORK);
            filter.addAction(UmenPushUtil.ACTION_NOTIFYCATION_CLICK);
            act.registerReceiver(mMainReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (intent == null && intent.getExtras() != null) {
            return;
        }
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                PushInfo info = BundleHelper.getSerializableByBundle(intent.getExtras());
                // PushInfo info = BundleHelper.getSerializableByBundle(PushInfo.class, intent.getExtras());
                if (info != null) {
                    jumpFragment(act, info);
                }
//                optPush(act, info, isToFragment);
            }
        }, 300);

    }

    public static final void jumpFragment(FragmentActivity act, PushInfo info) {
        final String id = String.valueOf(info.id);
        switch (info.type) {
            case 18:
                AppUtil.requestSugarBlood(act, id, info.memId);
                break;
            case 19:
                AppUtil.requestHighPressBlood(act, id);
                break;
            case 50:
                AppUtil.jumpByPushOrTask(act, info.memId, info.sendId, info.busi_type, info.url, info.url, info.title, id);
                break;
            case 53:
                AppUtil.jumpByPushOrTask(act, info.memId, info.sendId, info.busi_type, String.valueOf(info.id), info.url, info.title, info.url);
                break;
            default:
                AppUtil.jumpByPushOrTask(act, info.memId, info.sendId, info.busi_type, id, info.url, info.title, id);
                break;
        }

    }

    /**
     * MainActivity开启时的推送 处理
     *
     * @param act
     * @param info
     */
    public static final void onPushOptionInMainActivity(final FragmentActivity act, final PushInfo info) {

        //如果在咨询页面 并且是当前成员的推送，就刷新咨询页面列表
        if (FragmentMrg.getCurrentFragment() instanceof AskQuestionFragment && info.memId.equals(UserMrg.DEFAULT_MEMBER.mId)) {
            Intent intent2 = new Intent();
            intent2.setAction("requestlist");
            act.sendBroadcast(intent2);
            return;
        }
        Fragment frag = FragmentMrg.getCurrentFragment();

        boolean isIndex = frag instanceof IndexFrag || frag instanceof AssessFragment || frag instanceof AskIndexFragment;
        final String id = String.valueOf(info.id);
        //判断是否在一级页面  是则不弹窗,判断是否是主成员，不是就弹窗
        if (UserMrg.DEFAULT_MEMBER.coordinator != 1 || !isIndex) {
            CustomFragmentDialog dialog = new CustomFragmentDialog();
            dialog.setTitle(info.decs);
            dialog.setPositiveButton("查看", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (info.busi_type) {
                        case 53:
                            AppUtil.jumpByPushOrTask(act, info.memId, info.sendId, info.busi_type, id, info.url, info.title, info.url);
                            break;
                        case 100:
                            AppUtil.jumpByPushOrTask(act, info.memId, info.sendId, info.busi_type, id, info.url, String.valueOf(info.msg_type), info.url);
                            break;
                        default:
                            AppUtil.jumpByPushOrTask(act, info.memId, info.sendId, info.busi_type, id, info.url, info.title, id);
                            break;
                    }
                }
            });
            dialog.setNegativeButton("知道了", null);
            dialog.show(act.getSupportFragmentManager(), "DialogFragment");
        } else {
            try {
                //刷新 小红点
                FragmentMrg.getSingleFragment(IndexFrag.class).requestMemUnReadMsgLoader();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
