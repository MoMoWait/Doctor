package com.comvee.tnb.ui.remind;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import com.comvee.tnb.activity.MainActivity;
import com.comvee.tnb.dialog.CustomDialog;

public class RemindDialogActivity extends Activity implements OnCancelListener, DialogInterface.OnClickListener {

    DialogInterface.OnClickListener afterDayListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    Intent aIntent = new Intent();
                    aIntent.setClass(RemindDialogActivity.this, MainActivity.class);
                    // aIntent.setFlags(Intent.FLAG_)
                    aIntent.setAction(Intent.ACTION_MAIN);
                    aIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    startActivity(aIntent);
                    break;
                case DialogInterface.BUTTON_NEUTRAL:
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
            dialog.cancel();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        final String title = getIntent().getStringExtra("title");
        final String msg = getIntent().getStringExtra("msg");
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setOnCancelListener(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("知道了", this);
        builder.create().show();

        playMsgAudio(getApplicationContext());
    }

    @Override
    public void onCancel(DialogInterface arg0) {
        finish();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
        dialog.cancel();

    }

    private void playMsgAudio(Context cxt) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(cxt, notification);
            r.play();
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

}
