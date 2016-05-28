package com.comvee.tnb.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.comvee.BaseApplication;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.ui.more.WebNewFrag;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.AppUtil;
import com.comvee.tool.UserMrg;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

/**
 * 闪屏页面(活动)
 */
public class LunchActivityDialog extends DialogFragment implements OnClickListener {
    public static boolean isShow;
    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            dismiss();
        }
    };
    private int imgRes, delayTimeint;
    private ImageView imageView;
    private Bitmap mBitmap;
    private View mRootView;

    /**
     * 获取闪屏页图片数据
     */
    public static void loadLaunchData() {

        final Context cxt = TNBApplication.getInstance();

        TnbBaseNetwork reuqest = new TnbBaseNetwork() {
            @Override
            protected void onDoInMainThread(int status, Object obj) {

            }

            @Override
            protected Object parseResponseJsonData(JSONObject packet) {
                try {
                    JSONObject obj = packet.optJSONObject("body");

                    String url = obj.optString("url");
                    int times = obj.optInt("times");
                    String turn_to = obj.optString("turn_to");

                    String web_title = obj.optString("web_title");
                    String end_time = obj.optString("end_time");

                    cxt.getSharedPreferences("launch", 0).edit().putString("url", url)
                            .putString("turn_to", turn_to).putString("web_title", web_title).putString("end_time", end_time).putInt("times", times).commit();

                    if (TextUtils.isEmpty(url)) {
                        //清楚图片
                        AppUtil.deleatImage(cxt);
                    } else {
                        //保存图片
                        AppUtil.loadImageToLocal(url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void initRequestEntity(RequestParams entry) {
                super.initRequestEntity(entry);
                DisplayMetrics mDisplayMetrics = cxt.getResources().getDisplayMetrics();
                final int W = mDisplayMetrics.widthPixels;
                final int H = mDisplayMetrics.heightPixels;
                putPostValue("height", H + "");
                putPostValue("width", W + "");
            }

            @Override
            public String getUrl() {
                return ConfigUrlMrg.LOAD_HEALTH_INDEX;
            }
        };
        reuqest.start(reuqest.POOL_THREAD_2);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialogs);
    }


    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }

    public void setDelayTimeint(int delayTimeint) {
        this.delayTimeint = delayTimeint;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.lunch_img_frag, container);
        mRootView = layout;
        imageView = (ImageView) layout.findViewById(R.id.img_of_lunch);
        imageView.setOnClickListener(this);
        RelativeLayout.LayoutParams params = null;
        mBitmap = AppUtil.getImage();
        if (mBitmap == null) {
            imageView.setImageResource(imgRes);
            params = new RelativeLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            params.addRule(RelativeLayout.CENTER_IN_PARENT);

        } else {
            if (android.os.Build.VERSION.SDK_INT >= 16) {
                imageView.setBackground(new BitmapDrawable(mBitmap));
            } else {
                imageView.setBackgroundDrawable(new BitmapDrawable(mBitmap));
            }
            params = new RelativeLayout.LayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
        }
        imageView.setLayoutParams(params);
        getDialog().getWindow().setLayout(-1, -1);
        getDialog().getWindow().setGravity(Gravity.CENTER);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setWindowAnimations(R.style.event_amin);
        getDialog().setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });
        isShow = true;
        return layout;
    }

    @Override
    public void onResume() {
        delayTimeint = BaseApplication.getInstance().getSharedPreferences("launch", 0).getInt("times", 2000);
        mHandler.sendEmptyMessageDelayed(2, delayTimeint);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        loadLaunchData();
        dismiss();
    }

    @Override
    public void onDestroyView() {
        if (mBitmap != null) {
            if (android.os.Build.VERSION.SDK_INT >= 16) {
                imageView.setBackground(null);
            } else {
                imageView.setBackgroundDrawable(null);
            }
            mBitmap.recycle();
        }

        super.onDestroyView();
        getActivity().sendBroadcast(new Intent(LoginFragment.LOGIN_ACTION));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isShow = false;
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.img_of_lunch:
                Context cxt = BaseApplication.getInstance();
                SharedPreferences sp = cxt.getSharedPreferences("launch", 0);
                String turn_to = sp.getString("turn_to", null);
                String web_title = sp.getString("web_title", null);
                String end_time = sp.getString("end_time", null);
                if (TextUtils.isEmpty(turn_to)) {
                    return;
                }
                String url = turn_to;
                if (url.contains("?")) {
                    url += String.format("&sessionID=%s&sessionMemberID=%s", UserMrg.getSessionId(cxt), UserMrg.getMemberSessionId(cxt));
                } else {
                    url += String.format("?sessionID=%s&sessionMemberID=%s", UserMrg.getSessionId(cxt), UserMrg.getMemberSessionId(cxt));
                }
                WebNewFrag.toFragment(getActivity(), web_title, turn_to);
                dismiss();
                break;
            case TitleBarView.ID_LEFT_BUTTON:
                dismiss();
                break;
            default:
                break;
        }

    }
}
