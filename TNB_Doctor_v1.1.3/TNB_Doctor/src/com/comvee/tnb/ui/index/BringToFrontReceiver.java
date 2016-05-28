package com.comvee.tnb.ui.index;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.comvee.ThreadHandler;
import com.comvee.tnb.activity.MainActivity;
import com.comvee.tnb.model.PushInfo;
import com.comvee.tnb.ui.book.BookWebActivity;
import com.comvee.tool.UmenPushUtil;
import com.comvee.util.BundleHelper;

import java.util.List;

/**
 * 通知栏点击管理 如果程序在后台，弹出程序，并跳转 如果程序在前台，直接跳转 如果程序关闭，打开程序并跳转
 *
 * @author Administrator
 */
public class BringToFrontReceiver extends BroadcastReceiver {
    public static final String ACTION_BRING_TO_FRONT = "neal.pushtest.action.BringToFront";

    public BringToFrontReceiver() {
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        // 获取ActivityManager
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
            }else{
                boolean isHashMainAct = false;
                //如果activity列表中存在MainActivity就将其从后台调出来显示
                for(ActivityManager.RunningTaskInfo rti : taskList){
                    if(rti.baseActivity.getClassName().equals(MainActivity.class.getName())){
                        Intent resultIntent = new Intent(context,MainActivity.class);
                        resultIntent.addCategory("android.intent.category.LAUNCHER");
                        resultIntent.setAction("android.intent.action.MAIN");
                        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                       // resultIntent.putExtras(BundleHelper.getBundleBySerializable((PushInfo)BundleHelper.getSerializableByBundle(intent.getExtras())));
                        context.startActivity(resultIntent);
                        ThreadHandler.postUiThread(new Runnable() {
                            @Override
                            public void run() {
                               // UmenPushUtil.senMainReceiver(context, (PushInfo) BundleHelper.getSerializableByBundle(intent.getExtras()), true);
                                UmenPushUtil.senMainReceiver(context, BundleHelper.getObjecByBundle(PushInfo.class, intent.getExtras()), true);
                            }
                        },800);
                        isHashMainAct = true;
                    }
                }
                if(!isHashMainAct){
                    // 若没有找到运行的task，用户结束了task或被系统释放，则重新启动mainactivity
                    Intent resultIntent = new Intent(context, MainActivity.class);
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    resultIntent.putExtras(BundleHelper.getBundleBySerializable((PushInfo)BundleHelper.getSerializableByBundle(intent.getExtras())));
                    context.startActivity(resultIntent);
                }
            }
//            for (ActivityManager.RunningTaskInfo rti : taskList) {
//                // 找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
//                if (rti.baseActivity.getClassName().equals(MainActivity.class.getName())) {
//                    try {
//                        Intent resultIntent = new Intent(context, Class.forName(rti.topActivity.getClassName()));
//                        resultIntent.addCategory("android.intent.category.LAUNCHER");
//                        resultIntent.setAction("android.intent.action.MAIN");
//                        resultIntent.putExtras(BundleHelper.getBundleBySerializable((PushInfo)BundleHelper.getSerializableByBundle(intent.getExtras())));
//                        context.startActivity(resultIntent);
//
//
//                      if (!rti.topActivity.getClassName().equals(MainActivity.class.getName())) {
//                            context.sendBroadcast(new Intent(BookWebActivity.INFINSH_ACTION));
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return;
//                }
//            }

        }
    }
}
