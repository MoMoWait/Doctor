package com.comvee.tnb.dialog;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;

/**
 * Created by friendlove-pc on 16/5/3.
 */
public class BaseDialogFragment extends DialogFragment {

    private DialogInterface.OnCancelListener mCancelListener;
    public void setOnCancelListener( DialogInterface.OnCancelListener mCancelListener){
        this.mCancelListener = mCancelListener;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if(null!=mCancelListener){
            mCancelListener.onCancel(dialog);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(null!=mCancelListener){
            mCancelListener.onCancel(dialog);
        }
    }
}
