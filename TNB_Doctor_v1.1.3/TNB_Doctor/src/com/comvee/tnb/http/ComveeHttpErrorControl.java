package com.comvee.tnb.http;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.Toast;

import com.comvee.frame.BaseFragmentActivity;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tool.ResUtil;
import com.comvee.tool.UserMrg;
import com.comvee.util.Util;

public class ComveeHttpErrorControl {

    public static final void parseError(final Activity cxt, ComveePacket packet) {
        parseError(cxt, packet, false);
    }

    public static final void parseError(final Activity cxt, ComveePacket packet, final boolean isLongTime) {
        try {
            int resCode = packet.getResultCode();
            switch (resCode) {
                case 0:
                    break;
                case 200001:// 登入超时
                    boolean isLogin = !TextUtils.isEmpty(UserMrg.getLoginName(cxt)) && !TextUtils.isEmpty(UserMrg.getLoginPwd(cxt));
                    if (!isLogin) {
                        UserMrg.setAoutoLogin(cxt, false);
                    }
                    CustomDialog.Builder builder = new CustomDialog.Builder(cxt);
                    builder.setMessage(isLogin ? packet.getResultMsg() : "你目前是游客，无法使用此功能，建议您注册/登入。");
                    builder.setTitle(ResUtil.getString(R.string.menu_head_tip));
                    builder.setPositiveButton(isLogin ? "重新登录" : "确定", new CustomDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentMrg.toFragment((BaseFragmentActivity) cxt, LoginFragment.class, null, true);
                        }
                    });
                    builder.create().show();
                    break;
                default:
                    Toast.makeText(cxt, packet.getResultMsg(), isLongTime ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void parseError(final Activity cxt, int errorCode) {
        try {
            switch (errorCode) {
                case 0:
                    break;
                case ComveeHttp.ERRCODE_NETWORK:
                    try {
                        CustomDialog.Builder builder = new CustomDialog.Builder(cxt);
                        builder.setTitle(ResUtil.getString(R.string.menu_head_tip));
                        builder.setMessage(ResUtil.getString(R.string.no_network));
                        builder.setPositiveButton(R.string.setting, new CustomDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Util.goToSetNetwork(cxt);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builder.create().show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    Toast.makeText(TNBApplication.getInstance(), TNBApplication.getInstance().getString(R.string.time_out), Toast.LENGTH_SHORT).show();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
