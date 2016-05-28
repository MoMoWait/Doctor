package com.comvee.tnb;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.activity.MainActivity;
import com.comvee.tnb.model.PushInfo;
import com.comvee.tnb.ui.book.BookWebActivity;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tool.Log;
import com.comvee.tool.UmenPushUtil;
import com.comvee.util.BundleHelper;
import com.umeng.message.UTrack;
import com.umeng.message.UmengBaseIntentService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.client.BaseConstants;
import org.json.JSONObject;

import java.util.List;

public class PushIntentService extends UmengBaseIntentService {
    private static final String TAG = PushIntentService.class.getName();


    @SuppressLint("InlinedApi")
    @SuppressWarnings("deprecation")
    @Override
    protected void onMessage(Context context, Intent intent) {
        super.onMessage(context, intent);
        try {
            String message = intent.getStringExtra(BaseConstants.MESSAGE_BODY);
            Log.e(message);
            UMessage msg = new UMessage(new JSONObject(message));
            UTrack.getInstance(context).trackMsgClick(msg);
            // PushInfo info = DataParser.createPushInfo(message);
            // 08-1414: 54: 24.940: E/tnb(4346): {
            // "msg_id": "uu10627143953525878100",
            // "display_type": "custom",
            // "alias": "",
            // "random_min": 0,
            // "body": {
            // "after_open": "go_app",
            // "ticker": "掌控糖尿病",
            // "custom":
            // "{\"busi_type\": \"100\",\"url\": \"message.html?id=\",\"type\": \"0\",\"id\": \"150810085100003\",\"memId\": \"201310000381106\"}}",
            // "text": "测试推送",
            // "title": "掌控糖尿病"
            // }
            // }

            JSONObject jsonRefreshObj = new JSONObject(msg.getRaw().toString());
            Log.e(jsonRefreshObj.toString());
            JSONObject body = jsonRefreshObj.optJSONObject("body");
            JSONObject custom = new JSONObject(body.optString("custom"));// 友智将其存为字符串，先转字符串在转jsonobject
            final String text = body.optString("text");
            final String title = body.optString("title");
            final String ticker = body.optString("ticker");
            final String msg_id = body.optString("msg_id");
            final String memId = custom.optString("memId");
            final int busi_type = custom.optInt("busi_type");
            final String url = custom.optString("url");
            final int type = custom.optInt("type");
            final long id = custom.optLong("id");
            PushInfo info = new PushInfo();

            info.busi_type = busi_type;
            info.decs = text;
            info.ticker = ticker;
            info.title = title;
            info.memId = memId;
            info.type = type;
            info.url = url;
            info.id = id;
            info.sendId = String.valueOf(id);

            if (MainActivity.isStart) {
                UmenPushUtil.senMainReceiver(context, info, false);
            } else {
                if (info.type != 1) {
                    UmenPushUtil.showNotification(context, info);
                } else {
                    UmenPushUtil.showDialog(context, info);
                }
            }

            try {
                //刷新 小红点
                FragmentMrg.getSingleFragment(IndexFrag.class).requestMemUnReadMsgLoader();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 触发消息条的点击事件
     *
     * @param intent
     */
    void onNotifycationClick(final Intent intent) {
        final Context context = getApplicationContext();
        ActivityManager mAm = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 获得当前运行的task
        List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
        if (!taskList.isEmpty()) {
            ComponentName topActivity = taskList.get(0).topActivity;
            //MainActivity在显示着，顶端
            if (topActivity.getClassName().equals(MainActivity.class.getName())) {
                try {
                    UmenPushUtil.senMainReceiver(context, (PushInfo) BundleHelper.getSerializableByBundle(intent.getExtras()), true);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                for (ActivityManager.RunningTaskInfo rti : taskList) {
                    // 找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
                    if (rti.baseActivity.getClassName().equals(MainActivity.class.getName())) {
                        try {
                            Intent resultIntent = new Intent(context, Class.forName(rti.topActivity.getClassName()));
                            resultIntent.addCategory("android.intent.category.LAUNCHER");
                            resultIntent.setAction("android.intent.action.MAIN");
                            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(resultIntent);

                            ThreadHandler.postUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent2 = new Intent();
                                    intent2.setAction(UmenPushUtil.ACTION_NOTIFYCATION_CLICK);
                                    intent2.putExtras(intent.getExtras());
                                    context.sendBroadcast(intent2);
                                }
                            },1000);
                           if (!rti.topActivity.getClassName().equals(MainActivity.class.getName())) {
                               //context.sendBroadcast(new Intent(BookWebActivity.INFINSH_ACTION));
                               Intent resultIntents = new Intent(context, MainActivity.class);
                               resultIntents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                               resultIntents.putExtras(BundleHelper.getBundleBySerializable((PushInfo) BundleHelper.getSerializableByBundle(intent.getExtras())));
                               context.startActivity(resultIntents);
                          }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }
                // 若没有找到运行的task，用户结束了task或被系统释放，则重新启动mainactivity
                Intent resultIntent = new Intent(context, MainActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                resultIntent.putExtras(BundleHelper.getBundleBySerializable((PushInfo) BundleHelper.getSerializableByBundle(intent.getExtras())));
                context.startActivity(resultIntent);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && UmenPushUtil.ACTION_NOTIFYCATION_CLICK.equals(intent.getAction())) {
            onNotifycationClick(intent);
            return START_REDELIVER_INTENT;
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
