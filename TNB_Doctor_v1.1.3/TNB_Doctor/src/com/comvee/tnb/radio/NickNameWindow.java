package com.comvee.tnb.radio;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.comvee.BaseApplication;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.util.UITool;

public class NickNameWindow extends PopupWindow {

    private View mRootView;
    private Context mContext;
    private EditText edtName;
    private TextView mText;

    public NickNameWindow() {
        super(TNBApplication.getInstance());
        mContext = TNBApplication.getInstance();
        this.setInputMethodMode(PopupWindow.INPUT_METHOD_FROM_FOCUSABLE);
        createRootView();
        setContentView(mRootView);
        setWidth(-1);
        setHeight(-1);
        setBackgroundDrawable(new BitmapDrawable());
        setAnimationStyle(R.style.PopupAnimation);
        setFocusable(true);
        // setOutTouchCancel(true);
    }

    private void createRootView() {
        mRootView = View.inflate(TNBApplication.getInstance(), R.layout.radio_nickname_window, null);
        mText = (TextView) mRootView.findViewById(R.id.text);
        edtName = (EditText) mRootView.findViewById(R.id.edit_nickname);
        /*mRootView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dismiss();
            }
        });*/
        mRootView.findViewById(R.id.btn_set).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String name = edtName.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    mText.setText("请先设置昵称");
                    isAnimation = true;
                    Animation shakeAnim = AnimationUtils.loadAnimation(mRootView.getContext(), R.anim.radio_shake_y);
                    mRootView.startAnimation(shakeAnim);
                } else {
                    if (mCallBack != null) {
                        mCallBack.onCallBack(name);
                    }
                    //dismiss();
                }
            }
        });
    }

    public  void setShow(boolean isShow){
        this.isShow=isShow;
    }
    @Override
    public void dismiss() {
        super.dismiss();
    }

    private boolean isShow=false;

    @Override
    public boolean isShowing() {
        return isShow;
    }

    private NickNameCallback mCallBack;

    public void show(View view, NickNameCallback callBack) {
        mCallBack = callBack;
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
        UITool.autoOpenInputMethod(mContext, edtName, 500);
    }

    private boolean isAnimation = false;

    public void setTetle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mText.setText(title);
            if (isAnimation) {
                Animation shakeAnim = AnimationUtils.loadAnimation(mRootView.getContext(), R.anim.radio_shake_y);
                mRootView.startAnimation(shakeAnim);
            }
        }
    }

    ;

    public interface NickNameCallback {
        public void onCallBack(String name);

        public void onCancel();
    }

}
