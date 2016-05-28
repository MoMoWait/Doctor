package com.comvee.tool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.BaseApplication;
import com.comvee.frame.BaseFragmentActivity;
import com.comvee.network.NetStatusManager;
import com.comvee.tnb.R;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.ui.record.RecordMainFragment;
import com.comvee.tnb.widget.CircleView;
import com.comvee.util.Util;

import org.json.JSONObject;

public class ComveeDialogMrg {

    /**
     * 推送血糖对话框
     *
     * @param act
     * @param packet
     */
    public static void showSugarBloodDialog(final Activity act, ComveePacket packet) {

        JSONObject obj = packet.optJSONObject("body");
        String title = obj.optString("title");
        String msg = obj.optString("tips");
        String msg1 = obj.optString("content");
        final String memberId = obj.optString("memberId");
        int bloodGlucoseStatus = obj.optInt("bloodGlucoseStatus");
        String limit = obj.optString("range");

        final Dialog dialog = new Dialog(act, R.style.CustomDialog);

        View view = View.inflate(act.getApplicationContext(), R.layout.dialog_sugarblood, null);
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_history) {
                    RecordMainFragment fra = RecordMainFragment.newInstance(true, 0);
                    fra.setMemberId(memberId);
                    ((BaseFragmentActivity) act).toFragment(fra, true);
                }
                dialog.cancel();
            }

        };

        view.findViewById(R.id.btn_kown).setOnClickListener(listener);
        view.findViewById(R.id.btn_history).setOnClickListener(listener);
        view.findViewById(R.id.btn_close).setOnClickListener(listener);

        CircleView vCircle = (CircleView) view.findViewById(R.id.v_circle);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvMsg = (TextView) view.findViewById(R.id.tv_msg);
        TextView tvMsg1 = (TextView) view.findViewById(R.id.tv_msg1);
        TextView tvLimit = (TextView) view.findViewById(R.id.tv_limit);

        int color = 0;

        int cYellow = Color.parseColor("#ffcc00");
        int cGreen = Color.parseColor("#9ee800");
        int cRed = Color.parseColor("#ff6600");

        switch (bloodGlucoseStatus) {
            case 3:
                tvMsg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sugarblood_0, 0, 0, 0);
                color = cGreen;
                break;
            case 2:
            case 4:
                tvMsg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sugarblood_1, 0, 0, 0);
                color = cYellow;
                break;
            case 5:
            case 1:
                tvMsg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sugarblood_2, 0, 0, 0);
                color = cRed;
                break;
            default:
                tvMsg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sugarblood_0, 0, 0, 0);
                color = cGreen;

                break;
        }

        tvTitle.setText(title);
        tvMsg.setText(msg);
        tvMsg1.setText(msg1);
        // String value1 = obj.optString("value") + "\n" +
        // obj.optString("unit");
        String value1 = obj.optString("value") + "\n" + obj.optString("unit");
        tvLimit.setText(limit);
        vCircle.setCircleColor(color);
        vCircle.setTextColor(color);
        vCircle.setText(value1);
        vCircle.setTextSize(30);
        vCircle.setProgress(100);
        vCircle.setRadius(Util.dip2px(act, 100));
        dialog.setContentView(view);
        dialog.show();

    }


    public static void showBloodPressDialog(final Activity act, ComveePacket packet) {
        final Dialog dialog = new Dialog(act, R.style.CustomDialog);
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_history) {
                    ((BaseFragmentActivity) act).toFragment(RecordMainFragment.newInstance(true, 1), true);
                }
                dialog.cancel();
            }

        };

        View view = View.inflate(act.getApplicationContext(), R.layout.dialog_bloodpres, null);
        view.findViewById(R.id.btn_kown).setOnClickListener(listener);
        view.findViewById(R.id.btn_history).setOnClickListener(listener);
        view.findViewById(R.id.btn_close).setOnClickListener(listener);

        TextView tvStatus = (TextView) view.findViewById(R.id.textView);
        ImageView ivStatus = (ImageView) view.findViewById(R.id.imageView);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);

        TextView tvMsg0 = (TextView) view.findViewById(R.id.tv_msg0);
        // TextView tvMsg1 = (TextView) view.findViewById(R.id.tv_msg1);
        TextView tvMsgg = (TextView) view.findViewById(R.id.tv_msgg);

        TextView tvValue0 = (TextView) view.findViewById(R.id.tv_msg00);
        TextView tvValue1 = (TextView) view.findViewById(R.id.tv_msg11);
        TextView tvValue2 = (TextView) view.findViewById(R.id.tv_msg22);

        JSONObject obj = packet.optJSONObject("body");
        String title = obj.optString("title");
        String msg1 = obj.optString("message");
        String value0 = obj.optString("high");
        String value1 = obj.optString("low");
        String value2 = obj.optString("bpm");
        int state = obj.optInt("state");

        switch (state) {
            case 1:
                ivStatus.setImageResource(R.drawable.gxytu1);
                tvStatus.setText(ResUtil.getString(R.string.sugar_tolow));
                break;
            case 2:
                ivStatus.setImageResource(R.drawable.gxytu2);
                tvStatus.setText(ResUtil.getString(R.string.sugar_normal));
                break;
            case 3:
                ivStatus.setImageResource(R.drawable.gxytu3);
                tvStatus.setText(ResUtil.getString(R.string.sugar_tohigh));

                break;
            case 4:
                ivStatus.setImageResource(R.drawable.gxytu4);
                tvStatus.setText(ResUtil.getString(R.string.blood_low));

                break;
            case 5:
                ivStatus.setImageResource(R.drawable.gxytu5);
                tvStatus.setText(ResUtil.getString(R.string.blood_middle));

                break;
            case 6:
                ivStatus.setImageResource(R.drawable.gxytu6);
                tvStatus.setText(ResUtil.getString(R.string.blood_high));

                break;
            default:
                ivStatus.setImageResource(R.drawable.gxytu2);
                tvStatus.setText(ResUtil.getString(R.string.blood_error));
                break;
        }

        tvTitle.setText(title);
        tvMsgg.setText(msg1);
        // String value = obj.optString("value") + "\n" + obj.optString("unit");
        tvValue0.setText(value0 + "");
        tvValue1.setText(value1 + "");
        tvValue2.setText(value2 + "");
        dialog.setContentView(view);
        dialog.show();

    }

    /**
     * 检测是否有网络，如果无网络弹出对话框设置网络
     *
     * @param act
     * @return
     */
    public static boolean showCheckNetWorkDialog(final Activity act) {
        if (!NetStatusManager.isNetWorkStatus(BaseApplication.getInstance())) {
            DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            Util.goToSetNetwork(act);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:

                            break;
                        default:
                            break;
                    }
                    dialog.dismiss();
                }
            };

            new AlertDialog.Builder(act).setMessage(ResUtil.getString(R.string.no_network)).setTitle(ResUtil.getString(R.string.remind)).setPositiveButton(ResUtil.getString(R.string.ok), dialogListener)
                    .setNegativeButton(ResUtil.getString(R.string.cancel), null).create().show();
            return false;
        } else {
            return true;
        }

    }

}
