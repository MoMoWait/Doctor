package com.comvee.tnb.ui.more;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.comvee.FinalDb;
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
import com.comvee.tnb.model.AlarmInfo;
import com.comvee.tnb.model.RecommondInfo;
import com.comvee.tnb.model.TendencyPointInfo;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;
import com.comvee.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class AboutFragment extends BaseFragment implements OnClickListener, OnHttpListener {

    private int mCount;
    private TitleBarView mBarView;

    public AboutFragment() {
    }

    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_about;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        mBarView.setTitle("关于");

        TextView tvVesion = (TextView) findViewById(R.id.tv_versionname);
        tvVesion.setText("v" + Util.getAppVersionName(getApplicationContext(), getApplicationContext().getPackageName()));

        initTestAdrrDoor();

        final View btnUpdate = findViewById(R.id.btn_update);
        final View btnWeibo = findViewById(R.id.btn_weibo);
        btnUpdate.setOnClickListener(this);
        btnWeibo.setOnClickListener(this);
    }

    private void initTestAdrrDoor() {
        // final View view = findViewById(R.id.imgCtLogo);
        // view.setOnClickListener(this);
    }

    @Override
    public boolean onBackPress() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imgCtLogo:

                mCount++;
                if (mCount > 10) {
                    mCount = 0;
                    ComveeHttp.clearAllCache(getApplicationContext());
                    UserMrg.clear(getApplicationContext());
                    FinalDb db = FinalDb.create(getApplicationContext(), ConfigParams.DB_NAME);
                    db.deleteByWhere(AlarmInfo.class, "");
                    db.deleteByWhere(RecommondInfo.class, "");
                    db.deleteByWhere(TendencyPointInfo.class, "");
                    toFragment(LoginFragment.class, null, true);
                }
                break;
            case R.id.btn_update:
                WebFragment frag1 = WebFragment.newInstance("更新日志", ConfigParams.getConfig(getApplicationContext(), ConfigParams.TEXT_LOG_URL));
                toFragment(frag1, true, true);
                break;

            case R.id.btn_weibo:
                WebFragment frag = WebFragment.newInstance("官方微博", ConfigParams.getConfig(getApplicationContext(), ConfigParams.TEXT_WEIBO));
                toFragment(frag, true, true);
                break;

            default:
                break;
        }
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();
        try {

            switch (what) {
                case 1:

                    ComveePacket packet = ComveePacket.fromJsonString(b);
                    if (packet.getResultCode() == 0) {
                        showUpdateAppDialog(packet);
                    } else {
                        ComveeHttpErrorControl.parseError(getActivity(), packet);
                    }

                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 是否要更新 对话框
     *
     * @throws JSONException
     */
    private void showUpdateAppDialog(ComveePacket packet) throws Exception {

        JSONObject obj = packet.getJSONObject("body");
        final int versionNum = obj.optInt("versionNum");

        if (versionNum <= Util.getAppVersionCode(getApplicationContext(), getApplicationContext().getPackageName())) {
            showToast("您当前使用的已经是最新版，没有发现更新版本");
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

                        showDowloadDialog(donwloadUrl);
                        if (isForce) {
                            getActivity().finish();
                        }

                        break;
                    case CustomDialog.ID_NO:
                        getActivity().finish();
                        break;

                    default:
                        break;
                }
            }
        };

        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setMessage(info);
        builder.setNegativeButton(strCancel, isForce ? listener : null);
        builder.setPositiveButton("更新", listener);
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

    }

    private void showDowloadDialog(String url) {
        if (!Util.SDCardExists()) {
            Toast.makeText(getApplicationContext(), "无SDCARD，无法下载！", Toast.LENGTH_SHORT).show();
            return;
        }
        String strPkgName = url.substring(url.lastIndexOf("/") + 1);
        String filePath = Environment.getExternalStorageDirectory() + "/.tnb/app/" + strPkgName;
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            file.delete();
        }

        KWDownloadFileParams kwDownloadFileParams = new KWDownloadFileParams(url, getString(R.string.app_name) + "升级包", filePath,
                getApplicationContext(), 10010);
        KWAppDownloadListener kwAppDownloadListener = new KWAppDownloadListener(getApplicationContext());

        KWDownLoadFileTask task = new KWDownLoadFileTask(getApplicationContext(), kwAppDownloadListener);
        task.execute(kwDownloadFileParams);

        Toast.makeText(getApplicationContext(), "正在准备下载，请稍候！", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }

}
