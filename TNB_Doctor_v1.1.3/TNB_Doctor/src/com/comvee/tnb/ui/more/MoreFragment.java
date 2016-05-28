package com.comvee.tnb.ui.more;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.comvee.frame.BaseFragmentActivity;
import com.comvee.http.KWAppDownloadListener;
import com.comvee.http.KWDownLoadFileTask;
import com.comvee.http.KWDownloadFileParams;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.VersionInfo;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.radio.RadioPlayerMrg;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.record.RecordSurgarSetfragment;
import com.comvee.tnb.ui.record.diet.EditResultFragment;
import com.comvee.tnb.ui.user.ChangePwdFragment;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UITool;
import com.comvee.tool.UserMrg;
import com.comvee.ui.remind.TimeRemindFragment;
import com.comvee.ui.remind.TimeRemindUtil;
import com.comvee.util.Util;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 更多设置
 *
 * @author friendlove
 */
public class MoreFragment extends BaseFragment implements OnClickListener, OnHttpListener {
    private Button btnUnLogin;
    private boolean isSliding;
    private TitleBarView mBarView;
    private String access_token;
    private String openid;
    private long expires_in;

    public MoreFragment() {
    }

    public static MoreFragment newInstance(boolean isSliding) {
        MoreFragment fragment = new MoreFragment();
        fragment.setSliding(isSliding);
        return fragment;
    }

    /**
     * 是否要更新 对话框
     *
     * @throws JSONException
     */
    private static void showUpdateAppDialog(final Activity activity, ComveePacket packet, boolean isShow) throws Exception {

        JSONObject obj = packet.getJSONObject("body");
        final int versionNum = obj.optInt("versionNum");

        if (versionNum <= Util.getAppVersionCode(activity, activity.getPackageName())) {
            if (isShow) {
                Toast.makeText(activity, "您当前使用的已经是最新版，没有发现更新版本", Toast.LENGTH_SHORT).show();
            }
            return;

        } else {
        }

        final String info = obj.optString("info");
        final boolean isForce = obj.optInt("isForce") == 1;// 是否强制更新
        final String donwloadUrl = obj.optString("downUrl");

        final String strCancel = isForce ? "退出" : "取消";
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CustomDialog.ID_OK:

                        showDowloadDialog(activity, donwloadUrl);
                        if (isForce) {
                            activity.finish();
                        }

                        break;
                    case CustomDialog.ID_NO:
                        activity.finish();
                        break;

                    default:
                        break;
                }
            }
        };

        CustomDialog.Builder builder = new CustomDialog.Builder(activity);
        builder.setMessage(info);
        builder.setNegativeButton(strCancel, isForce ? listener : null);
        builder.setPositiveButton("更新", listener);
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

    }

    /**
     * 是否要更新 对话框
     *
     * @throws JSONException
     */
    private static void showUpdateAppDialog(final Activity activity, VersionInfo obj, boolean isShow) throws Exception {

        final int versionNum = obj.versionNum;

        if (versionNum <= Util.getAppVersionCode(activity, activity.getPackageName())) {
            if (isShow) {
                Toast.makeText(activity, "您当前使用的已经是最新版，没有发现更新版本", Toast.LENGTH_SHORT).show();
            }
            return;

        } else {
        }

        final String info = obj.info;
        final boolean isForce = obj.isForce == 1;// 是否强制更新
        final String donwloadUrl = obj.downUrl;

        final String strCancel = isForce ? "退出" : "取消";
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CustomDialog.ID_OK:

                        showDowloadDialog(activity, donwloadUrl);
                        if (isForce) {
                            activity.finish();
                        }

                        break;
                    case CustomDialog.ID_NO:
                        activity.finish();
                        break;

                    default:
                        break;
                }
            }
        };

        CustomDialog.Builder builder = new CustomDialog.Builder(activity);
        builder.setMessage(info);
        builder.setNegativeButton(strCancel, isForce ? listener : null);
        builder.setPositiveButton("更新", listener);
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

    }

    private static void showDowloadDialog(Activity activity, String url) {
        if (!Util.SDCardExists()) {
            Toast.makeText(activity, "无SDCARD，无法下载！", Toast.LENGTH_SHORT).show();
            return;
        }
        String strPkgName = url.substring(url.lastIndexOf("/") + 1);
        String filePath = Environment.getExternalStorageDirectory() + "/.tnb/app/" + strPkgName;
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            file.delete();
        }

        KWDownloadFileParams kwDownloadFileParams = new KWDownloadFileParams(url, activity.getString(R.string.app_name) + "升级包", filePath, activity,
                10010);
        KWAppDownloadListener kwAppDownloadListener = new KWAppDownloadListener(activity);

        KWDownLoadFileTask task = new KWDownLoadFileTask(activity, kwAppDownloadListener);
        task.execute(kwDownloadFileParams);

        Toast.makeText(activity, "正在准备下载，请稍候！", Toast.LENGTH_SHORT).show();

    }

    /**
     * 检查更新版本
     *
     * @param activity
     * @param isShow
     */
    public static void upData(final Activity activity, final boolean isShow) {
        if (isShow) {
            ((BaseFragmentActivity) activity).showProgressDialog("正在检查版本，请稍等...");
        }
        ObjectLoader<VersionInfo> loader = new ObjectLoader<VersionInfo>();
        loader.setNeedCache(false);
        loader.putPostValue("loadFrom", Util.getMetaData(activity, "APP_CHANNEL", null));
        loader.loadBodyObject(VersionInfo.class, ConfigUrlMrg.MORE_UPDATE_APP, loader.new CallBack() {
            @Override
            public void onBodyObjectSuccess(boolean isFromCache, VersionInfo obj) {
                super.onBodyObjectSuccess(isFromCache, obj);
                ((BaseFragmentActivity) activity).cancelProgressDialog();
                try {
                    showUpdateAppDialog(activity, obj, isShow);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setSliding(boolean isSliding) {
        this.isSliding = isSliding;
    }

    @Override
    public boolean onBackPress() {
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_more;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        requestQQIfBind();
        if (isSliding) {
        }

        init();
        mBarView.setTitle("设置");
    }

    private void requestQQIfBind() {
        // TODO Auto-generated method stub
        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.MEMBER_CENTER_NUM);
        http.setOnHttpListener(3, this);
        http.startAsynchronous();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void toAbout() {
        AboutFragment txt = AboutFragment.newInstance();
        toFragment(txt, true, true);
    }

    private void toSuggest() {
        toFragment(new SuggestFragment(), true, true);
    }

    private void toUnLogin() {
        // 清除前一个成员的闹钟
        TimeRemindUtil.getInstance(getApplicationContext()).cancleDisposableAlarm(IndexFrag.PENDING_CODE);
        // ((MainActivity) getActivity()).setIntent(new Intent());
        Tencent.createInstance("1101117477", getApplicationContext()).logout(getApplicationContext());
        // UserMrg.clear(getApplicationContext());

        UserMrg.setQQAoutoLogin(getApplicationContext(), false);
        UserMrg.setQQ_OpenId(getApplicationContext(), null);
        UserMrg.setQQExpires_in(getApplicationContext(), 0);
        UserMrg.setQQTekon(getApplicationContext(), null);
        ConfigParams.isShowDiscount = true;
        UserMrg.setTestData(getApplicationContext(), false);
        // String userName = UserMrg.getLoginName(getApplicationContext());
        // String userPwd = UserMrg.getLoginPwd(getApplicationContext());
        UserMrg.setAoutoLogin(getApplicationContext(), false);
        UserMrg.setQQAoutoLogin(getApplicationContext(), false);
        UserMrg.setSessionId(getApplicationContext(), null);
        UserMrg.setMemberSessionId(getApplicationContext(), null);
        // UserMrg.setLoginName(getApplicationContext(), "");
        // UserMrg.setLoginPwd(getApplicationContext(), "");
        // ComveeHttp.clearAllCache(getApplicationContext());
        // 清理数据库，，，，临时这样做
        // DBManager.cleanDatabases(getApplicationContext());
        // UserMrg.setLoginName(getApplicationContext(), userName);
        // UserMrg.setLoginPwd(getApplicationContext(), userPwd);
        // //如果一开始有注册
        // +
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        //
        // 游客身份，就返回游客身份
        // UserMrg.setSessionId(getApplicationContext(),
        // UserMrg.getTestDataSessionId(getApplicationContext()));
        // UserMrg.setTestDataMemberId(getApplicationContext(),
        // UserMrg.getMemberSessionId(getApplicationContext()));
        // ((MainActivity) getActivity()).toIndexFragment();
        // toFragment(LoginFragment.newInstance(true), false);

        toFragment(LuancherFragment.newInstance(), true, true);
        ConfigParams.IS_LOGIN = false;
    }

    private void setTvQQ() {
        // ((TextView) findViewById(R.id.tv_qq)).setCompoundDrawables(null,
        // null, null, null);
        // ((TextView)
        // findViewById(R.id.tv_qq)).setCompoundDrawablesWithIntrinsicBounds(0,
        // 0, R.drawable.dir_right, 0);
        // int px = org.chenai.util.Util.dip2px(getApplicationContext(), 35);
        // ((TextView) findViewById(R.id.tv_qq)).setPadding(0, 0, px, 0);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case TitleBarView.ID_LEFT_BUTTON:
//			((MainActivity) getActivity()).toggle();
                break;
            case R.id.btn_unregist:
                TimeRemindUtil.getInstance(getApplicationContext()).stopRemind();
                EditResultFragment.deleteAllCode(getApplicationContext(), TimeRemindUtil.getInstance(getContext()));
                if (ConfigParams.IS_TEST_DATA) {
                    toFragment(LoginFragment.class, null, true);

                } else {
                    RadioPlayerMrg.exitRadio();
                    toUnLogin();
                }
                break;
            case R.id.btn_about:
                toAbout();
                break;
            case R.id.btn_suggest:
                toSuggest();
                break;
            case R.id.btn_assess:
                toAsses();
                break;
            case R.id.btn_change_pwd:
                toChangePwd();
                break;
            case R.id.btn_statement:
                toStatement();
                break;
            case R.id.btn_update:
                upData(getActivity(), true);
                break;
            case R.id.btn_qq:
                if ("(已绑定)".equals(((TextView) findViewById(R.id.tv_qq)).getText().toString())) {
                    setTvQQ();
                    return;
                }

                final Tencent tencent = Tencent.createInstance("1101117477", getApplicationContext());
                tencent.logout(getActivity());
                tencent.login(getActivity(), "all", new IUiListener() {

                    @Override
                    public void onError(UiError arg0) {
                        showToast("绑定失败！");
                    }

                    @Override
                    public void onComplete(Object arg0) {

                        showProgressDialog(getString(R.string.msg_loading));

                        final JSONObject tokenObj = (JSONObject) arg0;

                        access_token = tokenObj.optString("access_token");
                        openid = tokenObj.optString("openid");
                        expires_in = tokenObj.optLong("expires_in") * 1000 + System.currentTimeMillis() - 60 * 60 * 1000 * 24;

                        if (TextUtils.isEmpty(openid)) {
                            access_token = UserMrg.getTempQQTekon(getApplicationContext());
                            openid = UserMrg.getTempQQ_OpenId(getApplicationContext());
                            expires_in = UserMrg.getTempQQExpires_in(getApplicationContext());
                        } else {
                            UserMrg.setTempQQTekon(getApplicationContext(), access_token);
                            UserMrg.setTempQQ_OpenId(getApplicationContext(), openid);
                            UserMrg.setTempQQExpires_in(getApplicationContext(), expires_in);
                        }

                        UserInfo mInfo = new UserInfo(getApplicationContext(), tencent.getQQToken());
                        mInfo.getUserInfo(new IUiListener() {

                            @Override
                            public void onError(UiError arg0) {
                            }

                            @Override
                            public void onComplete(Object arg0) {
                                JSONObject obj = (JSONObject) arg0;
                                requestBindQQ(openid, access_token, expires_in + "", obj.optString("nickname"), obj.optString("figureurl_qq_2"), "",
                                        "男".equals(obj.optString("gender")) ? "1" : "2");
                            }

                            @Override
                            public void onCancel() {
                            }
                        });
                    }

                    @Override
                    public void onCancel() {
                        showToast("绑定失败！");
                    }
                });
                break;
            case R.id.btn_more_remid:
                toFragment(TimeRemindFragment.newInstance(), true, true);
                break;
            case R.id.btn_more_sugar_target:
                toFragment(RecordSurgarSetfragment.class, null, true);
                break;

            default:
                break;
        }

    }

    private void requestBindQQ(String pid, String tokenKey, String validTime, String nickName, String photoUrl, String birthday, String sex) {
        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.BIND_QQ);
        http.setPostValueForKey("user_type", "2");
        http.setPostValueForKey("loginType", "1");
        http.setPostValueForKey("pid", pid);
        http.setPostValueForKey("tokenKey", tokenKey);
        http.setPostValueForKey("validTime", validTime);
        http.setPostValueForKey("nickname", nickName);
        http.setPostValueForKey("photoUrl", photoUrl);
        http.setPostValueForKey("birthday", birthday);
        http.setPostValueForKey("sex", sex);
        http.setOnHttpListener(2, this);
        http.startAsynchronous();
    }

    public void onSusse() {
        showToast("保存成功！");
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();
        try {

            switch (what) {
                case 1:

                    break;

                case 2:
                    try {
                        ComveePacket packet1 = ComveePacket.fromJsonString(b);

                        if (packet1.getResultCode() == 0) {
                            showToast(packet1.getResultMsg());
                            UITool.setTextView(getView(), R.id.tv_qq, "(已绑定)");
                            setTvQQ();
                        } else {
                            ComveeHttpErrorControl.parseError(getActivity(), packet1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast(R.string.error);
                    }
                    break;

                case 3:
                    ComveePacket packet2 = ComveePacket.fromJsonString(b);
                    parseBindQQ(packet2);
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void parseBindQQ(ComveePacket packet2) {
        // TODO Auto-generated method stub
        try {
            JSONObject body = packet2.getJSONObject("body");
            UITool.setTextView(getView(), R.id.tv_qq, body.optInt("bindQQFlag") == 1 ? "(已绑定)" : "(未绑定)");
            TextView qq = (TextView) findViewById(R.id.tv_qq);
            if (qq.getText().toString().trim().equals("(未绑定)")) {
                setTvQQ();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void init() {
        btnUnLogin = (Button) findViewById(R.id.btn_unregist);
        final View btnAbout = findViewById(R.id.btn_about);
        final View btnAssess = findViewById(R.id.btn_assess);
        final View btnChangePwd = findViewById(R.id.btn_change_pwd);
        final View btnStatement = findViewById(R.id.btn_statement);
        final View btnUpdate = findViewById(R.id.btn_update);
        final View btnSuggest = findViewById(R.id.btn_suggest);
        final View btnSugarTarget = findViewById(R.id.btn_more_sugar_target);
        final View line = findViewById(R.id.line);
        final View btnRemind = findViewById(R.id.btn_more_remid);
        // 添加了QQ绑定事件
        final View btnBindQQ = findViewById(R.id.btn_qq);
        // final View btnRemind = findViewById(R.id.btn_remind);
        btnSugarTarget.setOnClickListener(this);
        btnRemind.setOnClickListener(this);
        btnSuggest.setOnClickListener(this);
        btnAbout.setOnClickListener(this);
        btnChangePwd.setOnClickListener(this);
        btnAssess.setOnClickListener(this);
        btnStatement.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnBindQQ.setOnClickListener(this);

        btnUnLogin.setOnClickListener(this);
        if (UserMrg.isTnb()) {
            line.setVisibility(View.VISIBLE);
            btnSugarTarget.setVisibility(View.VISIBLE);
        } else {
            line.setVisibility(View.GONE);
            btnSugarTarget.setVisibility(View.GONE);
        }
        if (ConfigParams.IS_TEST_DATA) {
            btnChangePwd.setVisibility(View.GONE);
            // btnUnLogin.setVisibility(View.GONE);
            btnUnLogin.setBackgroundResource(R.drawable.button_green);
            btnUnLogin.setText("注册/登录");
        } else {

        }
    }

    /**
     * 免责什么
     */
    private void toStatement() {
        String url = ConfigParams.getConfig(getApplicationContext(), ConfigParams.TEXT_STATEMENT);
        WebFragment frag = WebFragment.newInstance("声明", url);
        toFragment(frag, true, true);
    }

    private void toAsses() {

        // Intent it = new Intent(Intent.ACTION_VIEW,
        // Uri.parse("http://www.appchina.com/app/com.mylyAndroid/"));
        // // Uri uri = Uri.parse("http://w");
        // startActivity(it);

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + getContext().getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "未找到可用的应用市场！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    private void toChangePwd() {
        // Intent it = new Intent(getActivity(), RegisterActivity.class);
        // it.putExtra("fragment", RegisterActivity.FRAMENT_CHANGE_PWD);
        // startActivity(it);
        toFragment(ChangePwdFragment.newInstance(), true, true);
    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }
}
