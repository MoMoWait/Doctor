package com.comvee.tnb.ui.exercise;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.comvee.frame.BaseFragmentActivity;
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

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 更新运动
 *
 * @author Administrator
 */
public class UpdateSportFragment extends BaseFragment implements OnClickListener, OnHttpListener {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private SimpleDateFormat sdf1 = new SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA);

    private SportRecord sportRecord;

    private SportRecord spordOrg;

    private Button deleteBt;

    private RelativeLayout timeRl;
    private RelativeLayout typeRl;
    private RelativeLayout minuteRl;

    private TextView timeTv;
    private TextView typeTv;
    private TextView minuteTv;
    private TextView calTv;
    private EditText remarkEt;
    private TitleBarView mBarView;
    private boolean leaveFragment = false;
    private SportRecord deleteRecord;

    public UpdateSportFragment() {

    }

    public UpdateSportFragment(SportRecord sportRecord) {
        this.deleteRecord = sportRecord;
        this.sportRecord = sportRecord;
        this.spordOrg = sportRecord;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.input_update_sport_input_fragment;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        mBarView.setTitle("记录运动");
        mBarView.setRightButton(getText(R.string.save).toString(), this);
        findView();
        registerListener();
        fullData();
        super.onLaunch();
    }

    private void findView() {
        deleteBt = (Button) findViewById(R.id.delete);

        timeRl = (RelativeLayout) findViewById(R.id.timerl);
        typeRl = (RelativeLayout) findViewById(R.id.typerl);
        minuteRl = (RelativeLayout) findViewById(R.id.minuterl);

        timeTv = (TextView) findViewById(R.id.time);
        typeTv = (TextView) findViewById(R.id.type);
        minuteTv = (TextView) findViewById(R.id.minute);
        calTv = (TextView) findViewById(R.id.cal);

        remarkEt = (EditText) findViewById(R.id.remarks);
    }

    private void registerListener() {
        timeRl.setOnClickListener(this);
        typeRl.setOnClickListener(this);
        minuteRl.setOnClickListener(this);
        deleteBt.setOnClickListener(this);
        AppUtil.registerEditTextListener1(remarkEt, R.string.sport_remarktip, 500, getApplicationContext());
    }

    private void fullData() {
        try {
            timeTv.setText(sdf1.format(sdf.parse(sportRecord.inputDt)));
            typeTv.setText(sportRecord.sportName);
            minuteTv.setText(sportRecord.sportTime + "分钟");
            float totalCal = (Float.parseFloat(sportRecord.caloriesOneMinutes)) * (Integer.parseInt(sportRecord.sportTime));
            int totalCalParse = Math.round(totalCal);
            calTv.setText(totalCalParse + " 千卡");
            remarkEt.setText(sportRecord.remark);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示日期控件
     */
    public void showDateDialog() {
        CustomDateTimePickDialog builder = new CustomDateTimePickDialog();
        builder.setTitle("运动时长");
        builder.setDefaultTime(Calendar.getInstance());
        builder.setOnDateTimeChangeListener(new OnDateTimeChangeListener() {
            @Override
            public void onChange(DialogFragment dialog, Calendar cal) {
                if (Calendar.getInstance().getTimeInMillis() < cal.getTimeInMillis()) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.txt_date_choose_limit), Toast.LENGTH_SHORT).show();
                    return;
                }
                sportRecord.inputDt = TimeUtil.fomateTime(cal.getTimeInMillis(), "yyyy-MM-dd HH:mm:ss");
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
     *
     * @param index
     */
    public void showMinuteDialog(int index) {
        CustomIntegerPickDialog buidler = new CustomIntegerPickDialog();
        buidler.setTitle("运动时长");
        buidler.canCircle(false).setInteval(5).setLable("分钟").setDefaultIndex(index).setMinValue(5).setIndexSize(100)
                .setOnChangeNumListener(new OnChangeNumListener() {
                    @Override
                    public void onChange(DialogFragment dialog, int num) {
                        sportRecord.sportTime = num + "";
                        minuteTv.setText(num + "分钟");

                        float calPearMinute = Float.parseFloat(sportRecord.caloriesOneMinutes);
                        float totalCal = calPearMinute * num;
                        int totalCalParse = Math.round(totalCal);
                        calTv.setText(totalCalParse + " 千卡");
                        sportRecord.calorie = totalCalParse + "";
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
                Bundle bundle = new Bundle();
                bundle.putSerializable("sportRecord", sportRecord);
                toFragment(ChooseSportFragment.class, bundle, true);
                break;
            case R.id.minuterl:
                if (TextUtils.isEmpty(sportRecord.sportTime)) {
                    showMinuteDialog(5);
                } else {
                    showMinuteDialog(Integer.parseInt(sportRecord.sportTime) / 5 - 1);
                }
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                saveSportRecord();
                break;
            case R.id.delete:
                showRemoveMsg();
                break;
            default:
                break;
        }
    }

    /**
     * 保存运动记录
     */
    private void saveSportRecord() {
        try {
            if (remarkEt.getText().toString().getBytes("GBK").length > 1000) {
                showToast("备注不能超过500个字哦");
                return;
            }
        } catch (UnsupportedEncodingException e1) {
            Log.e("tag", e1.getMessage(), e1);
        }
        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.UPDATE_SPORT_RECORD);
        http.setPostValueForKey("sportCalorieId", sportRecord.sportCalorieId);
        http.setPostValueForKey("sportTime", sportRecord.sportTime);
        http.setPostValueForKey("inputDt", sportRecord.inputDt);
        http.setPostValueForKey("calorie", sportRecord.calorie);
        http.setPostValueForKey("remark", remarkEt.getText() + "");
        http.setPostValueForKey("id", sportRecord.id + "");

        http.setOnHttpListener(1, this);
        http.startAsynchronous();
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();
        try {
            ComveePacket packet = ComveePacket.fromJsonString(b);
            if (packet.getJSONObject("body").getBoolean("success")) {
                leaveFragment = true;
                Bundle bundle = new Bundle();
                if (what == 1) {//修改

                } else {//删除
                    bundle.putSerializable("deletemodel", deleteRecord);
                    RecordExerciseFragment.reload = true;
                }
                FragmentMrg.popBackToFragment(getActivity(), RecordExerciseFragment.class, bundle);
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
        if (!spordOrg.sportCalorieId.equals(sportRecord.sportCalorieId) || !spordOrg.inputDt.equals(sportRecord.inputDt) || !spordOrg.sportTime.equals(sportRecord.sportTime)
                || !spordOrg.calorie.equals(sportRecord.calorie) || !spordOrg.remark.equals(remarkEt.getText().toString())) {
            showExitMsg();
            return true;
        }
        return super.onBackPress();
    }

    private void showRemoveMsg() {
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setMessage(R.string.sport_remove_text);
        builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                showProgressDialog(getString(R.string.msg_loading));
                ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.DELETESPORT_RECORD);
                http.setPostValueForKey("id", sportRecord.id);
                http.setOnHttpListener(2, UpdateSportFragment.this);
                http.startAsynchronous();
            }
        });
        builder.setPositiveButton(R.string.no, null);
        builder.create().show();
    }

    private void showExitMsg() {
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setMessage(R.string.sport_show_text);
        builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                saveSportRecord();
            }
        });
        builder.setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                leaveFragment = true;
                ((BaseFragmentActivity) getActivity()).onBackPressed();
            }
        });
        builder.create().show();
    }

    @Override
    public void onFragmentResult(Bundle data) {
        super.onFragmentResult(data);
        if (data != null) {
            try {
                Exercise exercise = BundleHelper.getSerializableByBundle(data);
                typeTv.setTextColor(Color.parseColor("#666666"));
                typeTv.setText(exercise.name);
                sportRecord.sportName = exercise.name;
                sportRecord.sportCalorieId = exercise.id;
                if (sportRecord.sportTime != null) {
                    float totalCal = Float.parseFloat(exercise.caloriesOneMinutes) * Integer.parseInt(sportRecord.sportTime);
                    int totalCalParse = Math.round(totalCal);
                    calTv.setText(totalCalParse + " 千卡");
                    sportRecord.calorie = totalCalParse + "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
