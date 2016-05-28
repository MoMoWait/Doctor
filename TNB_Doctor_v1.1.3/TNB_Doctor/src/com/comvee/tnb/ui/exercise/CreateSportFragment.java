package com.comvee.tnb.ui.exercise;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDateTimePickDialog;
import com.comvee.tnb.dialog.CustomDateTimePickDialog.OnDateTimeChangeListener;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.dialog.CustomIntegerPickDialog;
import com.comvee.tnb.dialog.CustomIntegerPickDialog.OnChangeNumListener;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.AppUtil;
import com.comvee.util.BundleHelper;
import com.comvee.util.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 新建运动
 *
 * @author Administrator
 */
public class CreateSportFragment extends BaseFragment implements OnClickListener, OnHttpListener {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private SimpleDateFormat sdf1 = new SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA);

    private RelativeLayout timeRl;
    private RelativeLayout typeRl;
    private RelativeLayout minuteRl;

    private TextView timeTv;
    private TextView typeTv;
    private TextView minuteTv;
    private TextView calTv;
    private TitleBarView mBarView;
    private String typeStr = "";

    private EditText remarkEt;

    private boolean leaveFragment = false;// 是否离开该 fragment

    private Map<String, String> postData = new HashMap<String, String>();// 将要提交的数据


    @Override
    public int getViewLayoutId() {
        return R.layout.input_create_sport_input_fragment;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        mBarView.setTitle("记录运动");
        mBarView.setRightButton(getText(R.string.save).toString(), this);
        findView();
        registerListener();
        if (TextUtils.isEmpty(postData.get("inputDt"))) {
            long timeMill = Calendar.getInstance().getTimeInMillis();
            postData.put("inputDt", TimeUtil.fomateTime(timeMill, "yyyy-MM-dd HH:mm:ss"));
            String str = TimeUtil.fomateTime(timeMill, "MM月-dd HH:mm");
            timeTv.setText(str);
        }
        super.onLaunch();
    }

    private void findView() {

        timeRl = (RelativeLayout) findViewById(R.id.timerl);
        typeRl = (RelativeLayout) findViewById(R.id.typerl);
        minuteRl = (RelativeLayout) findViewById(R.id.minuterl);

        timeTv = (TextView) findViewById(R.id.time);
        typeTv = (TextView) findViewById(R.id.type);
        if (!TextUtils.isEmpty(typeStr)) {
            typeTv.setTextColor(Color.parseColor("#666666"));
            typeTv.setText(typeStr);
        }

        minuteTv = (TextView) findViewById(R.id.minute);
        calTv = (TextView) findViewById(R.id.cal);

        remarkEt = (EditText) findViewById(R.id.remarks);
    }

    private void registerListener() {
        timeRl.setOnClickListener(this);
        typeRl.setOnClickListener(this);
        minuteRl.setOnClickListener(this);
        AppUtil.registerEditTextListener1(remarkEt, R.string.sport_remarktip, 500, getApplicationContext());
    }

    @Override
    public void onResume() {
        if (!TextUtils.isEmpty(postData.get("sportTime"))) {
            minuteTv.setTextColor(Color.parseColor("#666666"));
            minuteTv.setText(postData.get("sportTime") + "分钟");
        }
        if (!TextUtils.isEmpty(postData.get("calorie"))) {
            calTv.setText(postData.get("calorie") + " 千卡");
        }
        String inputDt = postData.get("inputDt");
        try {
            timeTv.setText(sdf1.format(sdf.parse(inputDt)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 显示日期控件
     */
    public void showDateDialog() {
        CustomDateTimePickDialog builder = new CustomDateTimePickDialog();
        builder.setDefaultTime(Calendar.getInstance());
        builder.setTitle("运动时长");
        builder.setOnDateTimeChangeListener(new OnDateTimeChangeListener() {
            @Override
            public void onChange(DialogFragment dialog, Calendar cal) {
                if (Calendar.getInstance().getTimeInMillis() < cal.getTimeInMillis()) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.txt_date_choose_limit), Toast.LENGTH_SHORT).show();
                    return;
                }
                postData.put("inputDt", TimeUtil.fomateTime(cal.getTimeInMillis(), "yyyy-MM-dd HH:mm:ss"));
                String str = TimeUtil.fomateTime(cal.getTimeInMillis(), "MM月dd日 HH:mm");
                timeTv.setText(str);
            }
        });
        Calendar startTime = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        builder.setDefaultTime(cal);
        startTime.add(Calendar.YEAR, -1);
        long eT = cal.getTimeInMillis();
        long sT = startTime.getTimeInMillis();
        builder.setLimitTime(sT, eT);
        builder.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    /**
     * 显示运动分钟控件
     */
    public void showMinuteDialog(int index) {
        CustomIntegerPickDialog buidler = new CustomIntegerPickDialog();
        buidler.canCircle(false).setInteval(5).setTitle("运动时长").setLable("分钟").setDefaultIndex(index).setMinValue(5).setIndexSize(100)
                .setOnChangeNumListener(new OnChangeNumListener() {
                    @Override
                    public void onChange(DialogFragment dialog, int num) {
                        postData.put("sportTime", num + "");
                        minuteTv.setText(num + "分钟");
                        minuteTv.setTextColor(Color.parseColor("#666666"));
                        if (!TextUtils.isEmpty(postData.get("sportCalorieId"))) {
                            float calPearMinute = Float.parseFloat(postData.get("caloriesOneMinutes"));
                            float totalCal = calPearMinute * num;
                            int totalCalParse = Math.round(totalCal);
                            calTv.setText(totalCalParse + " 千卡");
                            postData.put("calorie", totalCalParse + "");
                        }
                    }
                });
        buidler.show(getActivity().getSupportFragmentManager(), "dialog");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timerl:
                showDateDialog();
                break;
            case R.id.typerl:
                toFragment(ChooseSportFragment.class, null, true);
                break;
            case R.id.minuterl:
                if (postData.get("sportTime") == null) {
                    showMinuteDialog(5);
                } else {
                    showMinuteDialog(Integer.parseInt(postData.get("sportTime")) / 5 - 1);
                }
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                saveSportRecord();
                break;

            default:
                break;
        }
    }

    /**
     * 保存运动记录
     */
    private void saveSportRecord() {
        if (TextUtils.isEmpty(postData.get("sportCalorieId"))) {
            showToast("请选择运动类型");
            return;
        } else if (TextUtils.isEmpty(postData.get("sportTime"))) {
            showToast("请输入运动时长");
            return;
        }
        showProgressDialog(getString(R.string.msg_loading));
        postData.put("remark", remarkEt.getText().toString());
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.ADD_SPORT_RECORD);
        http.setPostValueForKey("sportCalorieId", postData.get("sportCalorieId"));
        http.setPostValueForKey("sportTime", postData.get("sportTime"));
        http.setPostValueForKey("inputDt", postData.get("inputDt"));
        http.setPostValueForKey("calorie", postData.get("calorie"));
        http.setPostValueForKey("remark", postData.get("remark"));
        http.setOnHttpListener(1, this);
        http.startAsynchronous();
    }

    @Override
    public void onFragmentResult(Bundle data) {
        super.onFragmentResult(data);
        if(null!=data){
            try {
                Exercise exercise = BundleHelper.getSerializableByBundle(data);
                postData.put("sportCalorieId", exercise.id);
                postData.put("caloriesOneMinutes", exercise.caloriesOneMinutes);
                typeStr = exercise.name;
                typeTv.setTextColor(Color.parseColor("#666666"));
                typeTv.setText(typeStr);
                String sportTimeStr = postData.get("sportTime");
                if (sportTimeStr != null) {
                    float totalCal = Float.parseFloat(exercise.caloriesOneMinutes) * Integer.parseInt(sportTimeStr);
                    int totalCalParse = Math.round(totalCal);
                    calTv.setText(totalCalParse + " 千卡");
                    postData.put("calorie", totalCalParse + "");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();
        try {
            ComveePacket packet = ComveePacket.fromJsonString(b);
            if (packet.getJSONObject("body").getBoolean("success")) {
                leaveFragment = true;
                RecordExerciseFragment.reload = true;
                //保存成功跳回 上个页面
                FragmentMrg.toFragment(getActivity(), RecordExerciseFragment.class, new Bundle(), true);
            } else {
                showToast(packet.getJSONObject("body").getString("msg"));
            }
        } catch (Exception e) {
            Log.e("tag", e.getMessage(), e);
        }
    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }

    @Override
    public boolean onBackPress() {
        if (leaveFragment) {
            return false;
        }
        if (!TextUtils.isEmpty(postData.get("sportCalorieId")) || !TextUtils.isEmpty(postData.get("sportTime"))
                || !TextUtils.isEmpty(postData.get("calorie")) || !TextUtils.isEmpty(remarkEt.getText().toString())) {
            showDialog();
            return true;
        }
        return super.onBackPress();
    }

    private void showDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setMessage(R.string.sport_back_text);
        builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                FragmentMrg.toBack(getActivity());
            }
        });
        builder.setPositiveButton(R.string.no, null);
        builder.create().show();
    }
}
