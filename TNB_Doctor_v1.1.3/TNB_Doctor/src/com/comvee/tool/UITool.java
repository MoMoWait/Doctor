package com.comvee.tool;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.comvee.ThreadHandler;
import com.comvee.tnb.R;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.dialog.LunchActivityDialog;
import com.comvee.util.Util;

import java.util.Timer;
import java.util.TimerTask;

public class UITool {

    /**
     * 沉浸式
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("InlinedApi")
    public static void setImmersive(Activity activity, boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            activity.getWindow().setAttributes(lp);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = activity.getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setAttributes(attr);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        int i = 0;
        try {
            i = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i ;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * @param v   填写错误的编辑框
     * @param msg 错误提示
     */
    public static void showEditError(TextView v, String msg) {
        v.requestFocus();
        v.setError(Html.fromHtml("<font color=#B2001F>" + msg + "</font>"));
    }

    public static void showEditError(EditText v, String msg) {
        v.requestFocus();
        v.setError(Html.fromHtml("<font color=#B2001F>" + msg + "</font>"));
    }

    // 关闭键盘
    public static void closeInputMethodManager(Context cxt, IBinder binder) {
        InputMethodManager imm = (InputMethodManager) cxt.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binder, 0);
    }

    public static void openInputMethodManager(Context cxt, View view) {
        InputMethodManager imm = (InputMethodManager) cxt.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_SHOWN);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 带清理按钮
     */
    public static void setEditWithClearButton(final EditText edt, final int imgRes) {

        edt.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Drawable[] drawables = edt.getCompoundDrawables();
                if (hasFocus && edt.getText().toString().length() > 0) {
                    edt.setTag(true);
                    // edt.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgRes,
                    // 0);
                    edt.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], edt.getContext().getResources().getDrawable(imgRes),
                            drawables[3]);

                } else {
                    edt.setTag(false);
                    edt.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], null, drawables[3]);
                    // edt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        });
        final int padingRight = Util.dip2px(edt.getContext(), 50);
        edt.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        int curX = (int) event.getX();
                        if (curX > v.getWidth() - padingRight && !TextUtils.isEmpty(edt.getText())) {
                            if (edt.getTag() != null && (Boolean) edt.getTag()) {
                                edt.setText("");
                                int cacheInputType = edt.getInputType();
                                edt.setInputType(InputType.TYPE_NULL);
                                edt.onTouchEvent(event);
                                edt.setInputType(cacheInputType);
                                return true;
                            } else {
                                return false;
                            }
                        }
                        break;
                }
                return false;
            }
        });

        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Drawable[] drawables = edt.getCompoundDrawables();
                if (edt.getText().toString().length() == 0) {
                    edt.setTag(false);
                    // edt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    edt.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], null, drawables[3]);

                } else {
                    edt.setTag(true);
                    edt.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], edt.getContext().getResources().getDrawable(imgRes),
                            drawables[3]);

                }
            }
        });

    }

    /**
     * 自动打开键盘
     *
     * @param context
     * @param view
     */
    public static void autoOpenInputMethod(final Context context, final View view) {
        autoOpenInputMethod(context, view, 500);
    }

    /**
     * 自动打开键盘
     *
     * @param context
     * @param view
     * @param PendingTime
     */
    public static void autoOpenInputMethod(final Context context, final View view, int PendingTime) {
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        view.setFocusable(true);
        view.requestFocus();
        view.setFocusableInTouchMode(true);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // imm.showSoftInput(editText, InputMethodManager.RESULT_SHOWN);
                // imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                // InputMethodManager.HIDE_IMPLICIT_ONLY);
                // 切换键盘
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, PendingTime);
    }

    public static int getViewWidth(View view) {
        int measure = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(measure, measure);
        return view.getMeasuredWidth();
    }

    public static float getDevicedensity(Activity act) {
        DisplayMetrics metric = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.density;
    }

    public static int getViewHeight(View view) {
        int measure = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(measure, measure);
        return view.getMeasuredHeight();
    }

    public static void showLunchImg(FragmentActivity activity) {
        LunchActivityDialog dialog = new LunchActivityDialog();
        dialog.setImgRes(R.drawable.huanyingye);
        dialog.setDelayTimeint(ConfigParams.getHealthTimes(activity));
        dialog.show(activity.getSupportFragmentManager(), "dialog");
    }

    //
    // /**
    // * 闪屏页
    // *
    // * @param cxt
    // * @param imgRes
    // * @param delayTime
    // */
    // @SuppressLint("NewApi")
    // public static void showLuancherImage(final Context cxt, int imgRes, int
    // delayTime) {
    // final WindowManager mWindows = (WindowManager)
    // cxt.getSystemService(Context.WINDOW_SERVICE);
    // WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
    // wmParams.type = 2002;
    // wmParams.flags = 0x00000400;
    // wmParams.format = PixelFormat.RGBA_8888;
    // wmParams.gravity = Gravity.LEFT | Gravity.TOP;
    // wmParams.windowAnimations = android.R.style.Animation_Toast;
    // wmParams.width = -1;
    // wmParams.height = -1;
    // // wmParams.systemUiVisibility = View. SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    // final ImageView image = new ImageView(cxt);
    // RelativeLayout.LayoutParams params = null;
    // final Bitmap mBitmap = AppUtil.getImage();
    // if (mBitmap == null) {
    // // layoutLuanch.setBackgroundColor(color.white);
    // image.setImageResource(imgRes);
    // params = new RelativeLayout.LayoutParams(new
    // LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    // params.addRule(RelativeLayout.CENTER_IN_PARENT);
    //
    // } else {
    // // layoutLuanch.setBackground(new
    // // BitmapDrawable(AppUtil.getImage()));
    // if (android.os.Build.VERSION.SDK_INT >= 16) {
    // image.setBackground(new BitmapDrawable(mBitmap));
    // } else {
    // image.setBackgroundDrawable(new BitmapDrawable(mBitmap));
    // }
    // params = new RelativeLayout.LayoutParams(new
    // LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    // params.addRule(RelativeLayout.CENTER_IN_PARENT);
    // }
    //
    // final RelativeLayout reLayout = new RelativeLayout(cxt);
    // reLayout.setBackgroundColor(cxt.getResources().getColor(R.color.white));
    // reLayout.addView(image, params);
    // mWindows.addView(reLayout, wmParams);
    // Handler mHandler = new Handler() {
    // public void handleMessage(android.os.Message msg) {
    // try {
    // if (mBitmap != null) {
    // try {
    // mBitmap.recycle();
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // mWindows.removeView(reLayout);
    //
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // System.gc();
    // };
    // };
    // mHandler.sendEmptyMessageDelayed(2, delayTime);
    // }

    public static void setTextView(View root, int resId, CharSequence str) {
        if (root != null)
            ((TextView) root.findViewById(resId)).setText(str);
    }

    public static void closeInputMethodManager(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 获取手机屏幕宽度
     *
     * @param activity
     * @return
     */
    public static float getDisplayWidth(Activity activity) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.widthPixels;
    }

    public static float getDisplayWidth(Context activity) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.widthPixels;
    }

    /**
     * 获取手机屏幕高度
     *
     * @param activity
     * @return
     */
    public static float getDisplayHeight(Context activity) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.heightPixels;
    }

    public static float getDisplayHeight(Activity activity) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.heightPixels;
    }

    /**
     * 设置键盘弹出后enditext的位置，避免布局变形或标题栏消失
     * <p/>
     * 注意：editextview外层必须用scrollview包裹才行
     */
    public static void setEditextRemove(final Activity activity, final int scrollId) {

        try {

            final View rootView = getRootView(activity);
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    int offset = rootView.getRootView().getHeight() - rootView.getHeight();
                    if (offset > 300 && activity != null && scrollId != 0) {
                        final ScrollView svResult = (ScrollView) activity.findViewById(scrollId);
                        if (svResult == null) {
                            return;
                        }
                        ThreadHandler.postUiThread(new Runnable() {
                            public void run() {
                                svResult.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });

                    }
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private static View getRootView(Activity act) {
        return ((ViewGroup) act.findViewById(android.R.id.content)).getChildAt(0);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public static void backgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        activity.getWindow().setAttributes(lp);
    }
}
