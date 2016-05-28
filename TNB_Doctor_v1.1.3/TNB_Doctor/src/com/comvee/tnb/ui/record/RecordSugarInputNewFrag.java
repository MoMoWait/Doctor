package com.comvee.tnb.ui.record;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.network.BackgroundTasks;
import com.comvee.tnb.R;
import com.comvee.tnb.SugarMrg;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.SugarHorizonAdapter;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.dialog.CustomFloatNumPickDialog;
import com.comvee.tnb.dialog.RobRedPacketDialog;
import com.comvee.tnb.model.HealthResultInfo;
import com.comvee.tnb.model.IndexSugarInfo;
import com.comvee.tnb.model.SugarControl;
import com.comvee.tnb.model.SugarRecord;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.task.CalendarView;
import com.comvee.tnb.view.CalendarWindow;
import com.comvee.tnb.widget.HorizontalListView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;
import com.comvee.util.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 新的记录血糖页面
 * Created by friendlove-pc on 16/3/20.
 */
public class RecordSugarInputNewFrag extends BaseFragment implements View.OnClickListener {

    private HorizontalListView horizontalListView;//滚动标题栏
    private SugarHorizonAdapter sugarHorizonAdapter;
    private int currentIndex;
    private TextView tvSugarControl;

    private SugarControl mSugarControl;

    private TitleBarView mBarView;
    private CalendarWindow mCalendarWindow;

    private View btnSubmit;
    private View btnRemove;

    private TextView tvSugarValue;
    private EditText edtMemo;
    private ImageView sugarIndicatorImg;
    //存储一天几个时段的数据,KEY是时段code
    private HashMap<String, SugarRecord> mSugarMap = new HashMap<String, SugarRecord>();
    private boolean isDialogShowed;//录入对话框  是否显示过

    private boolean isModify;// 数据是否被修改过
    private CustomFloatNumPickDialog mDialog;

    /**
     * @param act
     * @param date      日期格式yyyy-MM-dd
     * @param rangeCode
     */
    public static void toFragment(FragmentActivity act, String date, String rangeCode) {
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        bundle.putString("code", rangeCode);
        FragmentMrg.toFragment(act, RecordSugarInputNewFrag.class, bundle, true);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.record_sugarblood_input_fragment;
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);


        String date = null;
        String code = null;


        if (null != dataBundle) {
            date = dataBundle.getString("date");
            code = dataBundle.getString("code");
            if (TextUtils.isEmpty(date)) {
                date = TimeUtil.fomateTime(System.currentTimeMillis(), "yyyy-MM-dd");
            } else {
               // isDialogShowed = true;//如果是选着日期进来的就不用弹出录入窗口
            }
            if (TextUtils.isEmpty(code)) {
                currentIndex = SugarMrg.getCurrentSugarIndex();
            } else {
                currentIndex = SugarMrg.getSugarRangeIndexByCode(code);
            }
        } else {
            currentIndex = SugarMrg.getCurrentSugarIndex();
            date = TimeUtil.fomateTime(System.currentTimeMillis(), "yyyy-MM-dd");
        }


        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        mBarView.setRightButton("补录", this);
        mBarView.setTextColor(getResources().getColor(R.color.default_title_textcolor), getResources().getColor(R.color.theme_color_green),
                getResources().getColor(R.color.default_title_textcolor));
        mBarView.setTitleDrawer(null, null, getResources().getDrawable(R.drawable.jilu_59), this);

        initSugarRangeView();

        tvSugarControl = (TextView) findViewById(R.id.set_value);

        tvSugarValue = (TextView) findViewById(R.id.tv_input_value);
        edtMemo = (EditText) findViewById(R.id.edt_decs);

        btnRemove = findViewById(R.id.btn_remove);
        btnRemove.setOnClickListener(this);
        btnRemove.setEnabled(false);

        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
        btnSubmit.setEnabled(false);

        sugarIndicatorImg = (ImageView) findViewById(R.id.sugar_indicator_img);

        findViewById(R.id.sugar_indicator_img).setOnClickListener(this);

        tvSugarControl.setOnClickListener(this);
        tvSugarValue.setOnClickListener(this);

        //获取血糖控制目标
        loadSugarControl();

        onChangeTime(date);


    }

    /**
     * 修改日期  重新加载数据
     *
     * @param date 日期格式yyyy-MM-dd
     */
    private void onChangeTime(String date) {
        mBarView.setTitle(date, this);
//        recordTime = TimeUtil.fomateTime(cal.getTimeInMillis(), ConfigParams.TIME_FORMAT);
        mSugarMap.clear();
        showProgressDialog(getString(R.string.msg_loading));
        loadSugarByDate(date);
    }

    /**
     * 初始化血糖时间段选择器
     */
    private void initSugarRangeView() {
        horizontalListView = (HorizontalListView) findViewById(R.id.time_bucket);
        sugarHorizonAdapter = new SugarHorizonAdapter(getApplicationContext(), SugarMrg.SUGAR_RANGE_TEXT, horizontalListView);
        horizontalListView.setAdapter(sugarHorizonAdapter);
        horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onSugarIndex(i);
            }
        });
    }

    private void onSugarIndex(final int i) {
        currentIndex = i;
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                sugarHorizonAdapter.setSelectIndex(i);
                horizontalListView.setSelection(i);
            }
        }, 100);
        updateSugarControlView(i, mSugarControl);

        SugarRecord record = mSugarMap.get(SugarMrg.SUGAR_RANGE_CODE[i]);

        if (null != record) {
            setupSugarValueView(record.value);
            edtMemo.setText(record.memo);
        } else {
            edtMemo.setText(null);
            setupSugarValueView(-1);//设置为默认
        }
        checkButton();

    }

    private void setupSugarValueView(float value) {
        if (value <= 0) {
            tvSugarValue.setText(null);
            showValueStatus(-1);
        } else {
            tvSugarValue.setText(String.valueOf(value));
            showValueStatus(checkValue(value));
        }
    }

    /**
     * 刷新控制目标的view
     *
     * @param index
     * @param control
     */
    private void updateSugarControlView(int index, SugarControl control) {

        if (control != null) {
            String strLimit = (index == 1) ? (control.lowEmpty + "-" + control.highEmpty) : (control.lowFull + "-" + control.highFull);

            if (UserMrg.isTnb()) {
                tvSugarControl.setText(Html.fromHtml(String.format("控制目标: %s mmol/L", strLimit)));
                tvSugarControl.setBackgroundResource(R.drawable.bg_textview);
                tvSugarControl.setClickable(true);
            } else {
                tvSugarControl.setClickable(false);
                tvSugarControl.setBackgroundColor(0xffffff);
                tvSugarControl.setText(Html.fromHtml(String.format("标准值范围: %s mmol/L", strLimit)));
            }

        }
    }

    /**
     * 获取血糖控制目标
     */
    public void loadSugarControl() {
        ObjectLoader<SugarControl> loader = new ObjectLoader<SugarControl>();
        loader.setNeedCache(false);
        loader.loadBodyObject(SugarControl.class, ConfigUrlMrg.RECORD_SUGAR_SET, loader.new CallBack() {
            @Override
            public void onBodyObjectSuccess(boolean isFromCache, SugarControl obj) {
                super.onBodyObjectSuccess(isFromCache, obj);

                mSugarControl = obj;
                onSugarIndex(currentIndex);

            }

            @Override
            public boolean onFail(int code) {
                return super.onFail(code);
            }
        });

    }

    public void removeSugarValue(final SugarRecord info) {
        if (info != null) {

            CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
            builder.setMessage("是否确定要删除当前记录？");
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    showProgressDialog("正在删除");
                    ObjectLoader<String> loader = new ObjectLoader<String>();
                    loader.putPostValue("paramLogId", String.valueOf(info.paramLogId));
                    loader.setNeedCache(false);
                    loader.loadBodyObject(String.class, ConfigUrlMrg.RECORD_REMOVE_SURGAR_VALUE, loader.new CallBack() {
                        @Override
                        public void onBodyObjectSuccess(boolean isFromCache, String obj) {
                            cancelProgressDialog();
//                            setupSugarValueView(-1);
//                            btnRemove.setVisibility(View.GONE);
//                            btnSubmit.setEnabled(false);
//                            mSugarMap.remove(info.paramCode);
                            isModify = true;
                            FragmentMrg.toBack(getActivity(), isModify ? new Bundle() : null);

                        }

                        @Override
                        public boolean onFail(int code) {
                            cancelProgressDialog();
                            return super.onFail(code);
                        }
                    });

                }
            });
            builder.create().show();

        } else {
            setupSugarValueView(-1);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_value:
                if (UserMrg.isTnb()) {
                    toFragment(RecordSurgarSetfragment.class, null, true);
                }

                break;
            case R.id.btn_remove:
                removeSugarValue(mSugarMap.get(SugarMrg.SUGAR_RANGE_CODE[currentIndex]));
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
            case TitleBarView.ID_TITLE_LAYOUT:
                if (null == mCalendarWindow) {
                    mCalendarWindow = new CalendarWindow(getApplicationContext());
                    mCalendarWindow.setOnChoiceCalendarListener(new CalendarView.OnChoiceCalendarListener() {
                        @Override
                        public void onItemChoice(Calendar calendarSelected, int position, boolean isRecord) {
                            onChangeTime(TimeUtil.fomateTime(calendarSelected.getTimeInMillis(), "yyyy-MM-dd"));
                            mCalendarWindow.dismiss();
                        }

                        @Override
                        public void onRightClick(Calendar calendar, boolean isRecord) {
                            onChangeTime(TimeUtil.fomateTime(calendar.getTimeInMillis(), "yyyy-MM-dd"));
                            mCalendarWindow.dismiss();
                        }
                    });
                }
                mCalendarWindow.show(mBarView);
                break;
            case R.id.btn_submit:
                submitSugar();
                break;
            case R.id.sugar_indicator_img:

                break;
            case R.id.tv_input_value:
                showValueInputDialog(currentIndex);
                break;
        }
    }

    public void submitSugar() {
        String url = null;

        String time = mBarView.gettitleView().getText() + " " + SugarMrg.TIMES_SUGAR[currentIndex][0] + ":00";
        String value = tvSugarValue.getText().toString();
        String memo = edtMemo.getText().toString();
        String code = ConfigParams.SUGAR_TIME_CODE[currentIndex];

        if (TextUtils.isEmpty(value)) {
            showToast("请先填写测量值!");
            return;
        }
        showProgressDialog(getString(R.string.msg_loading));

        ObjectLoader<HealthResultInfo> loader = new ObjectLoader<HealthResultInfo>();

        ///////////////如果map中已经有血糖值，就传ID修改这个值，避免血糖重复////////////////
        SugarRecord old = null;
        if ((old = mSugarMap.get(code)) != null) {
            loader.putPostValue("paramLogId", String.valueOf(old.paramLogId));
            url = ConfigUrlMrg.RECORD_MODIFY_SURGAR_VALUE_NEW;//修改血糖
        } else {
            url = ConfigUrlMrg.RECORD_SUGGER;//新增血糖
        }
        /////////////////////////////////////////////////////////////////////////////
        loader.putPostValue("recordTime", time);
        loader.putPostValue("paramCode", code);
        loader.putPostValue("value", value.replace("mmol/L", ""));
        loader.putPostValue("memo", memo);
        loader.setNeedCache(false);
        loader.loadBodyObject(HealthResultInfo.class, url, loader.new CallBack() {
            @Override
            public void onBodyObjectSuccess(boolean isFromCache, HealthResultInfo obj) {
                cancelProgressDialog();
                toJumpResultFragment(obj);

                try {
                    FragmentMrg.getSingleFragment(IndexFrag.class).clearBannerCache();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public boolean onFail(int code) {
                cancelProgressDialog();
                return super.onFail(code);
            }
        });

    }

    private void toJumpResultFragment(HealthResultInfo obj) {
        switch (obj.isCentre) {
            case 0://选择异常因素
                HealthRecordRusultFragment.toFragment(getActivity(), obj, 1);
                break;
            case 1://结果页
                obj.bean.level = checkValue(obj.bean.value);
                RecordSugarFactorFrag.toFragment(getActivity(), obj);
                break;
        }
        if (obj.money_package == 1) {// 有红包，跳出领取红包界面
            RobRedPacketDialog dialog = new RobRedPacketDialog();
            dialog.setUserFlag(obj.user_flag);
            dialog.show(getFragmentManager(), "");
        }

        if (currentIndex == SugarMrg.getCurrentSugarIndex()) {//如果是当前时段就预加载首页的信息，（还未判断日期，偷个懒）
            ///预加载首页的血糖值////
            ObjectLoader<IndexSugarInfo> loader = new ObjectLoader<IndexSugarInfo>();
            loader.setThreadId(BackgroundTasks.TASK_BACKGROUND);
            loader.loadObjByPath(IndexSugarInfo.class, ConfigUrlMrg.INDEX_SUGAR_MSG, null, "body", "log");
        }

    }

    private void checkButton() {
        boolean b = !TextUtils.isEmpty(tvSugarValue.getText().toString());
        btnSubmit.setEnabled(b);
        btnSubmit.setVisibility(View.VISIBLE);

        if (mSugarMap.containsKey(SugarMrg.SUGAR_RANGE_CODE[currentIndex])) {
            btnRemove.setEnabled(true);
            btnRemove.setVisibility(View.VISIBLE);
        } else {
            btnRemove.setEnabled(false);
            btnRemove.setVisibility(View.GONE);
        }


    }

    /**
     * 显示  血糖值录入  对话框
     */
    public void showValueInputDialog(int index) {

        isDialogShowed = true;
        float defValue = 0;
        final String dValue = tvSugarValue.getText().toString().replace("mmol/L", "");
        if (!TextUtils.isEmpty(dValue)) {
            defValue = Float.valueOf(dValue);
        } else {
            defValue = 4.0f;
        }
        defValue = Math.min(33.9f, defValue);
        mDialog = new CustomFloatNumPickDialog();
        mDialog.setDefult(defValue);
        mDialog.setFloat(true);
        mDialog.setUnit("mmol/L");
        mDialog.setLimitNum(1, 33);
        mDialog.addOnNumChangeListener(new CustomFloatNumPickDialog.OnNumChangeListener() {
            @Override
            public void onChange(DialogFragment dialog, float num) {
                int sugerType = -1;
                setupSugarValueView(num);
                checkButton();
            }
        });
        mDialog.show(getActivity().getSupportFragmentManager(), "dialog");

    }

    /**
     * 判断血糖高低值
     *
     * @param num
     * @return 1、低值  3、正常  5高值
     */
    private int checkValue(float num) {
        if (num <= 0 || mSugarControl == null)
            return -1;
        float minLimit = -1.0f;
        float maxLimit = -1.0f;
        if (ConfigParams.SUGAR_TIME_STR1[currentIndex].contains("空腹血糖")) {
            minLimit = mSugarControl.lowEmpty;
            maxLimit = mSugarControl.highEmpty;
        } else {
            minLimit = mSugarControl.lowFull;
            maxLimit = mSugarControl.highFull;
        }
        if (minLimit == -1.0f || maxLimit == -1.0f) {
            return -1;
        }
        if (num >= minLimit && num <= maxLimit) {
            return 3;
        }
        if (num < minLimit) {
            return 1;
        }
        if (num > maxLimit) {
            return 5;
        }
        return -1;
    }

    /**
     * 显示 输入值的偏高偏低状态
     */
    private void showValueStatus(int level) {
        Drawable drawable = null;
        final String lowColor = "#3399ff";
        final String normalColor = "#66cc66";
        final String hightColor = "#ff3300";
        final int defaultCircle = R.drawable.jilu_65;
        final int lowCircle = R.drawable.jilu_67;
        final int normalCircle = R.drawable.jilu_70;
        final int hightCircle = R.drawable.jilu_71;
        switch (level) {
            case 1:// low
                drawable = getResources().getDrawable(R.drawable.jilu_55);
                drawable.setBounds(0, 10, drawable.getMinimumWidth(), tvSugarValue.getHeight() / 2);
                tvSugarValue.setCompoundDrawables(null, null, drawable, null);
                tvSugarValue.setTextColor(Color.parseColor(lowColor));
                sugarIndicatorImg.setImageResource(lowCircle);
                break;
            case 3:// normal
            default:
                tvSugarValue.setCompoundDrawables(null, null, null, null);
                tvSugarValue.setTextColor(Color.parseColor(normalColor));
                sugarIndicatorImg.setImageResource(normalCircle);
                break;
            case 5:// hight
                drawable = getResources().getDrawable(R.drawable.jilu_57);
                drawable.setBounds(0, 10, drawable.getMinimumWidth(), tvSugarValue.getHeight() / 2);
                tvSugarValue.setCompoundDrawables(null, null, drawable, null);
                tvSugarValue.setTextColor(Color.parseColor(hightColor));
                sugarIndicatorImg.setImageResource(hightCircle);
                break;
            case -1:
                tvSugarValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.jilu_53, 0);
                sugarIndicatorImg.setImageResource(defaultCircle);
                break;

        }
    }

    /**
     * 获取当天的血糖数据
     *
     * @param date yyyy-MM-dd
     */
    public void loadSugarByDate(String date) {
        ObjectLoader<SugarRecord> loader = new ObjectLoader<SugarRecord>();
        loader.putPostValue("date", date);
        loader.setNeedCache(false);
        loader.loadBodyArray(SugarRecord.class, ConfigUrlMrg.TENDENCY_POINT_LIST_ONE_DAY, loader.new CallBack() {

            @Override
            public void onBodyArraySuccess(boolean isFromCache, ArrayList<SugarRecord> obj) {
                cancelProgressDialog();

                mSugarMap.clear();
                for (SugarRecord item : obj) {
                    mSugarMap.put(item.paramCode, item);
                }
                onSugarIndex(currentIndex);

                if (mDialog==null) {
                    if (isVisible() && !isDialogShowed&&TextUtils.isEmpty(tvSugarValue.getText().toString())) {
                        showValueInputDialog(currentIndex);
                    }
                } else {
                    if (mDialog != null ) {
                        mDialog.dismiss();
                    }
                }

            }

            @Override
            public boolean onFail(int code) {
                cancelProgressDialog();
                return super.onFail(code);
            }
        });

    }

    @Override
    public boolean onBackPress() {
        FragmentMrg.toBack(getActivity(), isModify ? new Bundle() : null);
        return true;
    }
}
