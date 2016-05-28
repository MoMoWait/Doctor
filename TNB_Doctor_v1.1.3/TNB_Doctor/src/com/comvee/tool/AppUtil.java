package com.comvee.tool;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.comvee.BaseApplication;
import com.comvee.frame.BaseFragment;
import com.comvee.frame.BaseFragmentActivity;
import com.comvee.frame.FragmentMrg;
import com.comvee.network.NetStatusManager;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomImageDialog;
import com.comvee.tnb.dialog.ShadeActivityDialog;
import com.comvee.tnb.dialog.ShadeActivityDialog.ShadeClickListener;
import com.comvee.tnb.exception.ExceptionFragment;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.AskServerInfo;
import com.comvee.tnb.model.DierSuggerMsgModel;
import com.comvee.tnb.ui.ask.AskIndexFragment;
import com.comvee.tnb.ui.ask.AskQuestionFragment;
import com.comvee.tnb.ui.assess.AssessFragment;
import com.comvee.tnb.ui.book.BookWebActivity;
import com.comvee.tnb.ui.follow.FollowRecordFragment;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.log.RecordCalendarFragment;
import com.comvee.tnb.ui.machine.MachineListFragment;
import com.comvee.tnb.ui.member.MemberRecordFragment;
import com.comvee.tnb.ui.more.WebFragment;
import com.comvee.tnb.ui.more.WebMouthlySummarizeFragment;
import com.comvee.tnb.ui.more.WebRegiterFrag;
import com.comvee.tnb.ui.record.RecordChooseAddFragment;
import com.comvee.tnb.ui.record.RecordMrg;
import com.comvee.tnb.ui.record.RecordSugarInputNewFrag;
import com.comvee.tnb.ui.task.CheckTaskFragment;
import com.comvee.tnb.ui.task.DoctorRecommendFragment;
import com.comvee.tnb.ui.task.MyTaskCenterFragment;
import com.comvee.tnb.ui.task.TaskCenterFragment;
import com.comvee.tnb.ui.task.TaskDetailFragment;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tnb.ui.voucher.VoucherFragment;
import com.comvee.util.JsonHelper;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * @author Administrator
 */
public class AppUtil {
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    /**
     * @author sky
     * <p/>
     * Email vipa1888@163.com
     * <p/>
     * QQ:840950105
     * <p/>
     * 获取当前的网络状态 -1：没有网络 1：WIFI网络2：wap网络3：net网络
     * @param context
     * @return
     */
    private static int WIFI = 1;
    private static int CMNET = 2;
    private static int CMWAP = 3;
    
    public static int first;


	public static int optionSugar(double value, double downValue, double highValue, double maxValue) {
        double progress = 0;
        double nomalValue = 0;
        double D_value = 0;
        if (value > downValue && value < highValue) {
            nomalValue = highValue - downValue;
            D_value = value - downValue;
            progress = 33.3f + D_value / nomalValue * 33.3f;
        }
        if (value <= downValue) {
            progress = value / downValue * 33.3f;
        }
        if (value >= highValue && value < maxValue) {
            nomalValue = maxValue - highValue;
            D_value = value - highValue;
            progress = 66.6f + D_value / nomalValue * 33.3f;
        }
        if (value >= maxValue) {
            progress = 100;
        }
        return (int) Math.ceil(progress);
    }

    /**
     * 跳转市场评论
     *
     * @param cxt
     */
    public static void initCheckGrade_new(final Activity cxt) {
        // final Context cxt = act.getApplicationContext();
        try {

            long time = UserMrg.getTime_Last(cxt);
            int asses_interval = UserMrg.getAsses_interval(cxt);
            if (asses_interval == 0) {
                UserMrg.setAsses_interval(cxt, 7);
                asses_interval = UserMrg.getAsses_interval(cxt);
            }
            final long nowtime = System.currentTimeMillis();
            if (time == 0) {
                UserMrg.setTime_Last(cxt, nowtime - (8 * 24 * 60 * 60 * 1000));
                return;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(nowtime - time);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (day > asses_interval) {
                CustomImageDialog.Builder dialog = new CustomImageDialog.Builder(cxt);
                dialog.setMessage("亲~欢迎使用掌控糖尿病，您的支持是我们最大的动力！");
                dialog.setPositiveButton("支持一下", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (!NetStatusManager.isNetWorkStatus(BaseApplication.getInstance())) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("market://details?id=" + cxt.getPackageName()));
                                cxt.startActivity(intent);
                                UserMrg.setAsses_interval(cxt, 30);
                            } catch (Exception e) {
                                Toast.makeText(cxt, "未找到可用的应用市场！", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                            UserMrg.setTime_Last(cxt, nowtime);
                        } else {
                            Toast.makeText(cxt, "未发现网络连接！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.setNegativeButton("残忍拒绝", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        UserMrg.setTime_Last(cxt, nowtime);
                    }
                });
                dialog.create().show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkUser(final FragmentActivity activity, final String memberId, final BaseFragment frag, final boolean canBack,
                                 final boolean anim) {
        checkUser(activity, memberId, frag, null, canBack, anim);
    }

    public static void checkUser(final FragmentActivity activity, final String memberId, final BaseFragment frag, CheckUserListener listener,
                                 final boolean canBack, final boolean anim) {
        if (!TextUtils.isEmpty(memberId) && !"0".equals(memberId) && !memberId.equals(UserMrg.DEFAULT_MEMBER.mId)) {
            UserMrg.changMember(memberId, activity, frag, listener, canBack, anim);
        } else {
            if (frag != null) {
                FragmentMrg.toFragment(activity, frag, canBack, anim);
            } else if (listener != null) {
                listener.onCheckFinash();
            }
        }
    }

    /**
     * 页面跳转(用于推送跳转、首页任务跳转)
     *
     * @param act
     * @param jumpType 1、任务详情 3、web页面 4、录入 5、评估 6、登录
     * @param id
     * @param url
     * @param title
     */
    @SuppressWarnings("deprecation")
    public static void jumpByPushOrTask(final FragmentActivity act, String memId, String docId, int jumpType, String id, String url, String title,
                                        String jobCenterType) {
        try {

            switch (jumpType) {
                case 1:// 任务
                    TaskDetailFragment frag = TaskDetailFragment.newInstance();
                    frag.setTaskId(id);
                    checkUser(act, memId, frag, true, true);
                    break;
                case 2:// 建议:
                    // ComveeHttp.clearCache(getApplicationContext(),
                    // UserMrg.getCacheKey(UrlMrg.INDEX));
                    checkUser(act, memId, DoctorRecommendFragment.newInstance(), true, true);
                    break;
                case 3:// URL web
                    if (id.equals("board")) {
                        WebFragment web = WebFragment.newInstance(title, url);
                        FragmentMrg.toFragment(act, web, true, true);
                    } else if (!TextUtils.isEmpty(url)) {
                        // BookWebFragment fragment = new BookWebFragment(title,
                        // url, id);
                        // FragmentMrg.toFragment(act, fragment, true, true);
                        BookWebActivity.toWebActivity(act, BookWebActivity.TASK, null, title, url, id);
                    }
                    break;
                case 4:// 录入
                    checkUser(act, memId, RecordChooseAddFragment.newInstance(), true, true);
                    break;
                case 5:// 评估
                    checkUser(act, memId, AssessFragment.newInstance(), true, true);

                    break;
                case 6:
                    checkUser(act, memId, LoginFragment.newInstance(), true, true);

                    break;
                case 8:// 推荐行动
                    // ComveeHttp.clearCache(getApplicationContext(),
                    // UserMrg.getCacheKey(UrlMrg.INDEX));
                    checkUser(act, memId, LoginFragment.newInstance(), true, true);

                    break;
                case 7:// 咨询
                    ComveeHttp.clearCache(act, UserMrg.getCacheKey(ConfigUrlMrg.ASK_LIST));

                    // AskListFragment ask=AskListFragment.newInstance();
                    // if(jobDetailType.equals("")||jobDetailType==null){
                    // FragmentMrg.toFragment(act, AskListFragment.newInstance(),
                    // true,
                    // true);
                    // }
                    // else{
                    AskServerInfo tempInfo = new AskServerInfo();
                    tempInfo.setDoctorId(docId);
                    final AskServerInfo askInfo = tempInfo;
                    checkUser(act, memId, null, new CheckUserListener() {

                        @Override
                        public void onCheckFinash() {
                            AskQuestionFragment.toFragment(act, askInfo);
                        }
                    }, true, true);
                    break;
                case 9:// 普通的URL
                    // BookWebFragment fragment = new BookWebFragment(title,
                    // url,
                    // null);
                    // FragmentMrg.toFragment(act, fragment, true, true);
                    BookWebActivity.toWebActivity(act, BookWebActivity.TASK, null, title, url, "");
                    break;
                case 10:// 监测任务
                    CheckTaskFragment frag2 = CheckTaskFragment.newInstance();
                    checkUser(act, memId, frag2, true, true);
                    break;
                case 11:
                    TaskCenterFragment frag1 = TaskCenterFragment.newInstance();
                    frag1.setType(jobCenterType);

                    checkUser(act, memId, frag1, true, true);
                    break;
                case 12:
                    checkUser(act, memId, new AskIndexFragment(), true, true);
                    break;
                case 13:
                    checkUser(act, memId, MyTaskCenterFragment.newInstance(false), true, true);
                    break;
                case 14:
                    checkUser(act, memId, MemberRecordFragment.newInstance(0, 0, true), true, true);
                    break;
                case 15:
                    checkUser(act, memId, RecordCalendarFragment.newInstance(), true, true);
                    break;
                case 16:
                    // FragmentMrg.toFragment(act,
                    // MemberTargetScoreFragment.newInstance(UserMrg.DEFAULT_MEMBER,
                    // false), true, true);
                    break;
                case 18:
                    requestSugarBlood(act, id + "", memId);
                    break;
                case 19:
                    requestHighPressBlood(act, id + "");
                    break;
                case 20:
                    checkUser(act, memId, new AskIndexFragment(), true, true);
                    break;
                case 23:// 优惠券
                    VoucherFragment.toFragment(act, true);
                    break;
                case 51:// 新建随访

                    checkUser(act, memId, MemberRecordFragment.newInstance(2, false, jobCenterType), true, true);
                    break;

                case 52:// 推荐行动

                    checkUser(act, memId, FollowRecordFragment.newInstance(1, Long.parseLong(jobCenterType)), true, true);
                    break;
                case 50:// 异常提醒
                    if (jobCenterType != null && !"".equals(jobCenterType)) {
                        checkUser(act, memId, ExceptionFragment.newInstance(jobCenterType, Integer.parseInt(id)), true, true);
                    }
                    break;
                // case 53:// 推荐任务
                // String str[] = jobCenterType.split(",");
                // if (str.length == 1) {
                // TaskItem item = new TaskItem();
                // item.setJobCfgId(jobCenterType + "");
                // TaskIntroduceFragment action =
                // TaskIntroduceFragment.newInstance();
                // action.setTaskInfo(item);
                // action.setDoctorId(Long.parseLong(id));
                // FragmentMrg.toFragment(act, memId, action, true, true);
                // }
                // if (str.length > 1) {
                // TaskCenterFragment fragment2 = new TaskCenterFragment();
                // fragment2.setDoctorId(id);
                // fragment2.setIsTaskcent(2);
                // fragment2.setType(jobCenterType);
                // FragmentMrg.toFragment(act, memId, fragment2, true, true);
                // }
                // break;
                case 100:// 每日推荐

                    String urls = ConfigUrlMrg.HOST.substring(0, ConfigUrlMrg.HOST.length() - 6) + url + id + "&type=android";
                    // BookWebFragment fragment2 = new BookWebFragment(urls, id);
                    // FragmentMrg.toFragment(act, fragment2, true, true);
                    BookWebActivity.toWebActivity(act, BookWebActivity.MESSAGE, null, null, urls, id);
                    break;
                case 81:// 月度报告

                    url = ConfigUrlMrg.URL_HEAD + "yuedubaogao/index.html?sessionID=" + UserMrg.getSessionId(act) + "&sessionMemberID="
                            + UserMrg.getMemberSessionId(act) + "&monthId=" + jobCenterType + "&type=android";
                    String strs[] = id.split("-");
                    String year = strs[0] + "年" + strs[1] + "月";

                    checkUser(act, memId, new WebMouthlySummarizeFragment(url, year), true, true);
                    break;
                case 21:// 有注册按钮的web页面
                    FragmentMrg.toFragment(act, WebRegiterFrag.newInstance(title, url), true, true);
                    break;
                case 22:// 特殊的web页面，因为一些html无法回退，所以此web页面可以进行强制回退
                    WebFragment web = WebFragment.newInstance(title, url, true);
                    FragmentMrg.toFragment(act, web, true, true);
                    break;
                case 56:
                    // //医生服务列表
                    // FragmentMrg.toFragment(act, memId, new
                    // DoctorServerList(jobCenterType), true, true);
                    // break;
                case 59:// 任务推荐，跳消息中心
                case 53:// 推荐任务
                case 54:// 消息中心群发消息
                case 57:// 推送群发消息||消息中心日程提醒
                case 58:// 推送日程提醒
                    AskServerInfo tempInfo1 = new AskServerInfo();
                    tempInfo1.setDoctorId(docId);
                    final AskServerInfo askInfo1 = tempInfo1;
                    checkUser(act, memId, null, new CheckUserListener() {

                        @Override
                        public void onCheckFinash() {
                            AskQuestionFragment.toFragment(act, askInfo1);
                        }
                    }, true, true);
                    break;
                // case 60:
                // FragmentMrg.toFragment(act, new DoctorServerList(id), true,
                // true);
                // break;
                // case 59:// 任务推荐，跳消息中心
                // FragmentMrg.toFragment(act, MessageCentreFragment.newInstance(),
                // true, true);
                // break;
                /**
                 * 跳转我的设备
                 */

                case 70:
                    checkUser(act, memId, MachineListFragment.newInstance(true), true, true);
                    break;
                case 0:
                    IndexFrag.toFragment(act, true);
                    break;
                case -100100:
                    if (!TextUtils.isEmpty(memId) && !"0".equals(memId) && !memId.equals(UserMrg.DEFAULT_MEMBER.mId)) {
                        isChangMember(act, memId, title);
                    } else {
                        // loadSuggerNum(act, title);
                        FragmentMrg.toFragment(act, RecordSugarInputNewFrag.class, null, true);
                    }

                    break;

                default:
                    // Toast.makeText(act, "当前版本不支持该功能，请升级到最新版本！",
                    // Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static void isChangMember(final FragmentActivity activity, final String memberId, final String msg) {
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
                UserMrg.parseChangeMember(activity, result);
                loadSuggerNum(activity, msg);
                ConfigParams.TO_BACK_TYPE = 3;
            }
        }.execute();
    }

    @SuppressWarnings("unused")
    public static void loadSuggerNum(final FragmentActivity activity, final String msg) {
        ((BaseFragmentActivity) activity).showProgressDialog(activity.getText(R.string.loading).toString());
        try {
            final JSONObject jsonObject = new JSONObject(msg);
            if (null == jsonObject) {
                return;
            }
            new AsyncTask<Void, Void, String>() {

                @Override
                protected String doInBackground(Void... arg0) {

                    String url = ConfigUrlMrg.GET_SUGGER_NUM;
                    ComveeHttp http = new ComveeHttp(activity, url);
                    http.setPostValueForKey("folderId", jsonObject.optString("folderId"));
                    String result = http.startSyncRequestString();
                    return result;
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    ((BaseFragmentActivity) activity).cancelProgressDialog();

                    ComveePacket packet;
                    try {
                        packet = ComveePacket.fromJsonString(result);
                        if (packet.getResultCode() == 0) {
                            JSONObject body = packet.optJSONObject("body");
                            DierSuggerMsgModel model = JsonHelper.getObjecByJsonObject(DierSuggerMsgModel.class, body.optJSONObject("obj"));
                            Bundle bundle = new Bundle();
                            bundle.putString("folderId", model.folderId);
                            switch (jsonObject.optInt("option")) {
                                case 1:
                                    bundle.putString("value", model.value_one);
                                    bundle.putString("desc", model.memo_one);
                                    break;
                                case 2:
                                    bundle.putString("value", model.value_two);
                                    bundle.putString("desc", model.memo_two);
                                    break;
                                case 3:
                                    bundle.putString("value", model.value_three);
                                    bundle.putString("desc", model.memo_three);
                                    break;
                                default:
                                    break;
                            }

                            bundle.putString("period", jsonObject.optString("period"));
                            bundle.putInt("option", jsonObject.optInt("option"));
                            //	FragmentMrg.toFragment(activity, EditAfterMealValueFragment.class, bundle, true);
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.execute();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();

        }
    }

    public static void requestHighPressBlood(final FragmentActivity act, String id) {
        ComveeHttp http = new ComveeHttp(act.getApplicationContext(), ConfigUrlMrg.MACHINE_HIGHBLOOD);
        http.setPostValueForKey("paramLogId", id);
        http.setOnHttpListener(1, new OnHttpListener() {

            @Override
            public void onSussece(int what, byte[] b, boolean fromCache) {
                try {
                    IndexFrag.toFragment(act, true);
                    ComveePacket packet = ComveePacket.fromJsonString(b);
                    if (packet.getResultCode() == 0) {
                        ComveeDialogMrg.showBloodPressDialog(act, packet);
                        // FragmentMrg.toFragment(act,
                        // HealthRecordRusultFragment.newInstance(packet, 2),
                        // false, true);

                    } else {
                        ComveeHttpErrorControl.parseError(act, packet);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFialed(int what, int errorCode) {
                ComveeHttpErrorControl.parseError(act, errorCode);
            }
        });
        http.startAsynchronous();
    }

    public static void requestSugarBlood(final FragmentActivity act, String id, final String memId) {
        ComveeHttp http = new ComveeHttp(act.getApplicationContext(), ConfigUrlMrg.MACHINE_SUGARBLOOD);
        http.setPostValueForKey("paramLogId", id);
        http.setOnHttpListener(1, new OnHttpListener() {

            @Override
            public void onSussece(int what, byte[] b, boolean fromCache) {
                try {
                    ComveePacket packet = ComveePacket.fromJsonString(b);
                    if (packet.getResultCode() == 0) {
                        // ComveeDialogMrg.showSugarBloodDialog(act, packet);
                        // if (packet.optJSONObject("body").optInt("isCentre")
                        // == 1) {
                        // FragmentMrg.toFragment(act, memId, new
                        // RecordDetailFragment(packet, 2), false, false);
                        // } else {
                        // FragmentMrg.toFragment(act, memId,
                        // HealthRecordRusultFragment.newInstance(packet, 2),
                        // false, false);
                        // }
                        RecordMrg.getRecordDetailList(memId, act, packet, 2);
                        // mIndex.update();
                        // 是否是当前成员
                        // if (null != mIndex &&
                        // memberId.equals(UserMrg.DEFAULT_MEMBER.mId))
                        // mIndex.update();
                    } else {
                        ComveeHttpErrorControl.parseError(act, packet);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFialed(int what, int errorCode) {
                ComveeHttpErrorControl.parseError(act, errorCode);
            }
        });
        http.startAsynchronous();
    }

    
//	// 回调监听
//	private  AppClickListener listence;
//
//	public void setAppClickListener(AppClickListener clickListence) {
//		this.listence = clickListence;
//	}
//
//	public interface AppClickListener {
//		void onAppClick();
//	}
//    
    // 初始化 启动时的图片 延迟3秒加载
    public static void initLuanchWindow(Activity activity, int image,ShadeClickListener shadeClickListener) {
        ShadeActivityDialog dialog = new ShadeActivityDialog(shadeClickListener);
        dialog.setImgResources(image);
        dialog.show(((BaseFragmentActivity) activity).getSupportFragmentManager(), "dialog");
//        dialog.setShadeClickListener(new ShadeClickListener() {
//
//			@Override
//			public void onShadeClick() {
////				}
//			}
//		});
        // final WindowManager mWindows = (WindowManager)
        // context.getSystemService(Context.WINDOW_SERVICE);
        //
        // WindowManager.LayoutParams wmParams = new
        // WindowManager.LayoutParams();
        // wmParams.type = 2002;
        // wmParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        // wmParams.format = PixelFormat.RGBA_8888;
        // wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // // wmParams.windowAnimations = android.R.style.Animation_Toast;
        // wmParams.width = -1;
        // wmParams.height = -1;
        // final ImageView iv = new ImageView(context);
        // iv.setBackgroundResource(image);
        // iv.setOnClickListener(new OnClickListener() {
        // @Override
        // public void onClick(View arg0) {
        // mWindows.removeView(iv);
        // }
        // });
        // mWindows.addView(iv, wmParams);
    }

    public static int getIndexIconRes(int type) {
        switch (type) {
            case 1:
                return R.drawable.icon_index_1;
            case 2:
                return R.drawable.icon_index_2;
            case 3:
                return R.drawable.icon_index_3;
            case 4:
                return R.drawable.icon_index_4;
            case 5:
                return R.drawable.icon_index_5;
            case 6:
                return R.drawable.icon_index_6;
            case 7:
                return R.drawable.icon_index_7;
            default:
                return R.drawable.icon_index_6;
        }
    }

    public static int getImageIdByUrl(String url) {
        if (TextUtils.isEmpty(url))
            return 0;
        int i = url.lastIndexOf("/");
        if (i < 0)
            return 0;
        return getImageIdByRes(url.substring(i + 1).toString().replace(".png", "").replace(".jpg", ""));
    }

    public static int getImageIdByRes(String resId) {
        return TNBApplication.getInstance().getResources().getIdentifier(resId, "drawable", TNBApplication.getInstance().getPackageName());
    }

    public static void loadImageByLocationAndNet(ImageView iv, String url) {
        ImageLoaderUtil.getInstance(TNBApplication.getInstance()).displayImage(url, iv, ImageLoaderUtil.default_options_knowledge_index);
    }

    public static void loadImageToLocal(String url) {
        ImageLoaderUtil.getInstance(TNBApplication.getInstance()).loadImage(url, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                saveImageToLocal(loadedImage, getImagePath());
            }
        });

    }

    public static void deleatImage(Context context) {
        context.deleteFile("index.png");
    }

    public static String getImagePath() {
        String TEST_IMAGE = null;
        String FILE_NAME = "/index.png";
        // if
        // (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
        // && Environment.getExternalStorageDirectory().exists()) {
        // TEST_IMAGE =
        // Environment.getExternalStorageDirectory().getAbsolutePath() +
        // FILE_NAME;
        // } else {
        TEST_IMAGE = TNBApplication.getInstance().getFilesDir().getAbsolutePath() + FILE_NAME;
        // }

        return TEST_IMAGE;
    }

    private static void saveImageToLocal(Bitmap bitmap, String path) {

        try {
            // File dirFile = new File(path);
            // if (!dirFile.exists()) {
            // dirFile.createNewFile();
            // }

            BufferedOutputStream bos = new BufferedOutputStream((TNBApplication.getInstance()).openFileOutput("index.png", Context.MODE_PRIVATE));
            // BufferedOutputStream bos = new BufferedOutputStream(new
            // FileOutputStream(dirFile));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
            if (bitmap != null) {
                try {
                    bitmap.recycle();
                } catch (Exception e) {
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Bitmap getImage() {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream((TNBApplication.getInstance()).openFileInput("index.png"));
        } catch (Exception e) {
        }
        return bitmap;

    }

    public static int getAPNType(Context context) {

        int netType = -1;

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo == null) {

            return netType;

        }

        int nType = networkInfo.getType();

        if (nType == ConnectivityManager.TYPE_MOBILE) {

            // Log.e("networkInfo.getExtraInfo()",
            // "networkInfo.getExtraInfo() is " + networkInfo.getExtraInfo());

            if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {

                netType = CMNET;
            } else {
                netType = CMWAP;
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = WIFI;

        }

        return netType;

    }

    /**
     * 设置value中从star到end的字体放大1.5倍并设置为color的颜色
     *
     * @param value
     * @param star
     * @param end
     * @param color
     * @return
     */
    public static SpannableString setTextEffect(String value, int star, int end, String color) {
        SpannableString spannableString = new SpannableString(value);
        // 设置字体颜色
        CharacterStyle span = new ForegroundColorSpan(Color.parseColor(color));
        // 设置字体大小为默认的1.5倍
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.5f);

        spannableString.setSpan(sizeSpan, star, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(span, star, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置字体加粗
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), star, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    // /**
    // * 根据时间段清除首页缓存
    // *
    // * @param context
    // */
    // public static void isClearIndexCatch(Context context) {
    // int i = ConfigParams.getClearBloodTimeString();
    // if (i != ConfigParams.getTimeQuantum(context)) {
    // ((MainActivity) context).updateIndex();
    //
    // }
    // }

    public static void showTost(String title, String text, int duration) {
        View layout = View.inflate(TNBApplication.getInstance(), R.layout.toast_layout, null);
        if (TextUtils.isEmpty(title)) {
            layout.findViewById(R.id.tv_title).setVisibility(View.GONE);
        } else {
            UITool.setTextView(layout, R.id.tv_title, title);
        }
        UITool.setTextView(layout, R.id.tv_msg, text);

        Toast toast = new Toast(TNBApplication.getInstance());
        toast.setGravity(Gravity.CENTER, 0, 0);
        // 替换掉原有的ToastView
        toast.setView(layout);
        toast.setDuration(duration);
        toast.show();
    }

    // 将时间格式进行转换
    public static String dateFormatForm(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long time = cal.getTimeInMillis();
        System.out.println(time);
        try {
            long timeStr = format.parse(date).getTime();
            if (timeStr > time) {
                return format1.format(timeStr);
            } else {
                return format2.format(timeStr);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    // 将时间格式进行转换
    public static String dateFormatForm(String starFormat, String date, String endFormat) {
        SimpleDateFormat format = new SimpleDateFormat(starFormat);
        SimpleDateFormat format2 = new SimpleDateFormat(endFormat);
        String time = null;
        try {
            long timeStr = format.parse(date).getTime();
            time = format2.format(timeStr);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return time;
    }

    public static String ToDBC(String input) {
        input = input.trim();
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        String str = new String(c);
        str = str.replaceAll("\\.", ". ").replaceAll("！", "！ ").replaceAll("。", "。 ").replaceAll("？", "？ ").replaceAll("，", "， ")
                .replaceAll("\\?", "? ").replaceAll(",", ", ").replaceAll("、", "、 ");
        return str;
    }

    /**
     * 2、时间排序的优化与消息对话框的时间规则一致，当天的消息，只需要显示具体的时间，如：14:30；若消息发生于昨天/前天，则消息只需要显示：
     * 昨天或前天加时分；若消息超出前天且在当年内，则显示具体日期，如：05-12加时分；若不在当年的日期中，则显示年月日，如：2014-03-12
     *
     * @param toCalendar
     * @return
     */
    public static String formatTime(Calendar toCalendar) {
        Calendar currentCalendar = Calendar.getInstance();
        int toCalendarYear = toCalendar.get(Calendar.YEAR);
        long toCalendarDay = toCalendar.getTimeInMillis() / (1000 * 60 * 60 * 24);
        int currentYear = currentCalendar.get(Calendar.YEAR);
        long currentDay = currentCalendar.getTimeInMillis() / (1000 * 60 * 60 * 24);
        if (toCalendarDay == currentDay) {

            return new SimpleDateFormat("HH:mm").format(toCalendar.getTime());
        } else if (currentDay - toCalendarDay == 1) {
            return "昨天	" + new SimpleDateFormat("HH:mm").format(toCalendar.getTime());
        } else if (currentDay - toCalendarDay == 2) {
            return "前天 " + new SimpleDateFormat("HH:mm").format(toCalendar.getTime());
        } else if (toCalendarYear == currentYear && currentDay > toCalendarDay) {
            return new SimpleDateFormat("MM-dd HH:mm").format(toCalendar.getTime());
        } else {
            return new SimpleDateFormat("yyyy-MM-dd").format(toCalendar.getTime());
        }

    }

    /**
     * * 2、时间排序的优化与消息对话框的时间规则一致，当天的消息，只需要显示具体的时间，如：14:30；若消息发生于昨天/前天，则消息只需要显示：
     * 昨天或前天；若消息超出前天且在当年内，则显示具体日期，如：05-12；若不在当年的日期中，则显示年月日，如：2014-03-12
     *
     * @param toCalendar
     * @return
     */
    public static String formatTime1(Calendar toCalendar) {
        Calendar currentCalendar = Calendar.getInstance();
        int toCalendarYear = toCalendar.get(Calendar.YEAR);
        long toCalendarDay = toCalendar.getTimeInMillis() / (1000 * 60 * 60 * 24);
        int currentYear = currentCalendar.get(Calendar.YEAR);
        long currentDay = currentCalendar.getTimeInMillis() / (1000 * 60 * 60 * 24);
        DecimalFormat decimalFormat = new DecimalFormat("00");
        if (toCalendarDay == currentDay) {
            return new SimpleDateFormat("HH:mm").format(toCalendar.getTime());
        } else if (currentDay - toCalendarDay == 1) {
            return "昨天";
        } else if (currentDay - toCalendarDay == 2) {
            return "前天";
        } else if (toCalendarYear == currentYear && currentDay > toCalendarDay) {
            return new SimpleDateFormat("MM-dd").format(toCalendar.getTime());
        } else {
            return new SimpleDateFormat("yyyy-MM-dd").format(toCalendar.getTime());
        }

    }

    public static String formatTime(String str) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(simpleDateFormat.parse(str));
            return formatTime(calendar);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static float getDeviceDensity(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.density;
    }

    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static void storeImage(Bitmap image, File pictureFile) {
        if (pictureFile == null) {
            Log.d("tag", "Error creating media file, check storage permissions: ");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("tag", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("tag", "Error accessing file: " + e.getMessage());
        }
    }

    public static void registerEditTextListener1(final EditText et, final int tipResource, final int maxLength, final Context ctx) {
        registerEditTextListener(et, ctx.getResources().getString(tipResource), maxLength, ctx);
    }

    /**
     * edittext字符串限制
     *
     * @param et         edittext
     * @param tipContent 提示语
     * @param maxLength  最多多少个字
     * @param ctx
     */
    public static void registerEditTextListener(final EditText et, final String tipContent, final int maxLength, final Context ctx) {
        et.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int lastSequenceLength = 0;
            private int editEnd;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                editEnd = et.getSelectionEnd();
                if (temp != null && temp.length() > maxLength) {
                    Toast.makeText(ctx, tipContent, Toast.LENGTH_SHORT).show();
                    s.delete(editEnd - (s.length() - lastSequenceLength), editEnd);
                    et.setSelection(editEnd);
                    lastSequenceLength = maxLength;
                }
                lastSequenceLength = s.length();
            }
        });
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        return pattern.matcher(str).matches();
    }

    interface CheckUserListener {
        void onCheckFinash();
    }

}
