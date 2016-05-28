package com.comvee.tnb.ui.record;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.FinalDb;
import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.SugarHorizonAdapter;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.dialog.CustomFloatNumPickDialog;
import com.comvee.tnb.dialog.CustomFloatNumPickDialog.OnNumChangeListener;
import com.comvee.tnb.dialog.RobRedPacketDialog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.TendencyPointInfo;
import com.comvee.tnb.ui.task.CalendarView;
import com.comvee.tnb.ui.task.CalendarView.OnChoiceCalendarListener;
import com.comvee.tnb.widget.HorizontalListView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UITool;
import com.comvee.tool.UserMrg;
import com.comvee.util.TimeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 血糖录入
 *
 * @author friendlove
 */
@SuppressLint("ValidFragment")
public class RecordSugarInputFragment extends BaseFragment implements OnHttpListener, OnClickListener, OnCancelListener, OnChoiceCalendarListener,
        OnNumChangeListener, OnItemClickListener {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private int currentIndex = 0;
    private FinalDb db;
    private Class<? extends com.comvee.frame.BaseFragment> class1;
    private EditText edtDecs;

    private View btnSubmit;
    private View btnRemove;
    private View showButton;
    private RelativeLayout relative;

    private TextView tvValue;

    private PopupWindow calendarPopupWindow;
    private CalendarView calendarView = new CalendarView();
    private Calendar selectedCalendar;

    private HorizontalListView horizontalListView;
    private SugarHorizonAdapter sugarHorizonAdapter;

    private ImageView sugarIndicatorImg;

    private TextView setValue;
    private float highEmpty = -1, lowEmpty, highFull, lowFull;

    private TendencyPointInfo[] tendencyPointInfos = new TendencyPointInfo[8];// 当天所有时间段的血糖记录数据
    private TitleBarView mBarView;
    private boolean canBack = true;
    private int fromWhere = 1;// 3表示饮食记录

    private boolean isShow = true;
    private boolean money_package;//是否有红包
    private ComveePacket mPacket;
    private String tmp;
    private boolean resetText;
    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!resetText) {
                if (count >= 2) {
                    CharSequence input = s.subSequence(start, start + count);
                    if (!containsEmoji(input.toString())) {
                        resetText = true;
                        edtDecs.setText(tmp);
                        edtDecs.invalidate();
                        showToast("非法字符");
                    }
                }
            } else {
                resetText = false;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (!resetText) {
                tmp = s.toString();
            }

        }

        @Override
        public void afterTextChanged(Editable arg0) {
        }
    };

    public RecordSugarInputFragment() {
    }

    public RecordSugarInputFragment(String title, String time, String code_) {
        try {
            this.currentIndex = (Arrays.asList(ConfigParams.SUGAR_TIME_CODE)).indexOf(code_);
            this.selectedCalendar = Calendar.getInstance();
            this.selectedCalendar.setTimeInMillis(sdf.parse(time).getTime());
        } catch (Exception e) {
        }
    }

    public static void toFragment(FragmentActivity fragment, Class<? extends Fragment> c) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("class", c);
        FragmentMrg.toFragment(fragment, RecordSugarInputNewFrag.class, bundle, true);
    }

    public static RecordSugarInputFragment newInstance() {
        RecordSugarInputFragment fragment = new RecordSugarInputFragment();
        return fragment;
    }

    public static RecordSugarInputFragment newInstance(String id) {
        RecordSugarInputFragment fragment = new RecordSugarInputFragment(null, id, "");
        return fragment;
    }

    public static RecordSugarInputFragment newInstance(String title, String timeNCode) {
        String[] arr = timeNCode.split(" ");
        RecordSugarInputFragment fragment = new RecordSugarInputFragment(title, arr[0], arr[1]);
        return fragment;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.record_sugarblood_input_fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @SuppressWarnings("unchecked")
    @Override
    public void onLaunch(Bundle bundle) {
        super.onLaunch(bundle);
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                toFragment(RecordSugarInputNewFrag.class, null, true);

            }
        },1000);

        if(true)
        return ;
        if (bundle != null) {
            class1 = (Class<? extends com.comvee.frame.BaseFragment>) bundle.getSerializable("class");
            fromWhere = bundle.getInt("from_where", 1);
            this.currentIndex = (Arrays.asList(ConfigParams.SUGAR_TIME_CODE)).indexOf(bundle.getString("code"));
            if (fromWhere != 3) {
                this.selectedCalendar = Calendar.getInstance();
                try {
                    this.selectedCalendar.setTimeInMillis(sdf.parse(bundle.getString("time")).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        init();
        UITool.setEditextRemove(getActivity(), R.id.ScrollView);
        showProgressDialog(getString(R.string.msg_loading));
        requsetSugarLimit();

    }

    private void init() {
        db = FinalDb.create(getContext(), ConfigParams.DB_NAME);
        onChangeTime(Calendar.getInstance());
        mBarView.setRightButton("补录", this);
        mBarView.setLeftButton(R.drawable.top_menu_back, this);
        mBarView.setTextColor(getResources().getColor(R.color.default_title_textcolor), getResources().getColor(R.color.theme_color_green),
                getResources().getColor(R.color.default_title_textcolor));
        mBarView.setTitleDrawer(null, null, getResources().getDrawable(R.drawable.jilu_59), this);
        tvValue = (TextView) findViewById(R.id.tv_input_value);
        sugarIndicatorImg = (ImageView) findViewById(R.id.sugar_indicator_img);
        sugarIndicatorImg.setOnClickListener(this);
        horizontalListView = (HorizontalListView) findViewById(R.id.time_bucket);
        setValue = (TextView) findViewById(R.id.set_value);
        setValue.setOnClickListener(this);

        sugarHorizonAdapter = new SugarHorizonAdapter(getApplicationContext(), ConfigParams.SUGAR_TIME_STR2, horizontalListView);
        horizontalListView.setAdapter(sugarHorizonAdapter);
        horizontalListView.setOnItemClickListener(this);
        edtDecs = (EditText) findViewById(R.id.edt_decs);
        edtDecs.addTextChangedListener(watcher);

        findViewById(R.id.closeButton).setOnClickListener(this);
        showButton = findViewById(R.id.showButton);
        showButton.setOnClickListener(this);
        relative = (RelativeLayout) findViewById(R.id.relative);

        TextView tvDecs = (TextView) findViewById(R.id.tv_decs);
        tvDecs.setVisibility(View.GONE);
        btnRemove = findViewById(R.id.btn_remove);
        btnRemove.setOnClickListener(this);

        btnSubmit = findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(this);
        btnSubmit.setEnabled(false);

    }

    /**
     * 数据完全获取后的操作,true表示是日历取的
     */
    private void dataFetchedOp(boolean b) {
        tendencyPointInfos = new TendencyPointInfo[8];
        initTendencyPointInfos();
        if (b) {// 只有从日历取的才需要去取那天的第一个有记录的时间段，如果那天没记录，则保持currentIndex不变
            List<String> asList = Arrays.asList(ConfigParams.SUGAR_TIME_CODE);
            for (int i = 0; i < tendencyPointInfos.length; i++) {
                if (tendencyPointInfos[i] != null && asList.contains(tendencyPointInfos[i].code)) {
                    currentIndex = i;
                    break;
                }
            }
        }

        // 如果选定的日期没有记录，则时间段以当前时间来定，否则以选定的日期的最早的时间段为准。
        if (tendencyPointInfos[currentIndex] != null) {
            String memo = tendencyPointInfos[currentIndex].comment;
            edtDecs.setText(memo);
        }
        setTextValue();
        upViewValue(true);
        sugarHorizonAdapter.setSelectIndex(currentIndex);
        horizontalListView.setSelection(currentIndex);
        onChangeTime(selectedCalendar);
    }

    /**
     *
     */
    private void initTendencyPointInfos() {
        String sql = null;
        StringBuffer sb = new StringBuffer();
        for (String s : ConfigParams.SUGAR_TIME_CODE) {
            sb.append("code='").append(s).append("'").append(" or ");
        }
        String code = sb.toString().substring(0, sb.lastIndexOf(" or "));
        String day = String.format("date(time)=date('%s')", TimeUtil.fomateTime(selectedCalendar.getTimeInMillis(), "yyyy-MM-dd"));
        sql = String.format("(%s) and %s order by date(time) desc,insertDt desc", code, day);
        List<TendencyPointInfo> infos = db.findAllByWhere(TendencyPointInfo.class, sql);
        List<String> asList = Arrays.asList(ConfigParams.SUGAR_TIME_CODE);
        if (infos.size() != 0) {// 如果当前日期有有记录则重新赋值
            for (TendencyPointInfo tendencyPointInfo : infos) {
                String codeStr = tendencyPointInfo.code;
                if (asList.contains(codeStr)) {
                    tendencyPointInfos[asList.indexOf(codeStr)] = tendencyPointInfo;
                }
            }
        }
    }

    private void requsetSugarLimit() {
        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.RECORD_SUGAR_SET);
        http.setOnHttpListener(5, this);
        http.startAsynchronous();
    }

    /**
     * 远程请求前三个月的所有血糖记录
     */
    private void readHistoryFromRemote() {
        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getContext(), ConfigUrlMrg.TENDENCY_POINT_LIST);
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 24);
        String endDt = TimeUtil.fomateTime(today.getTimeInMillis(), "yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -3);
        String startDt = TimeUtil.fomateTime(cal.getTimeInMillis(), "yyyy-MM-dd HH:mm:ss");
        http.setPostValueForKey("startDt", startDt);
        http.setPostValueForKey("endDt", endDt);
        http.setPostValueForKey("paramKey", RecordTendencyFragment.createParamString());
        http.setOnHttpListener(7, this);
        http.startAsynchronous();
    }

    private void parseData(byte[] b, boolean fromCache) throws Exception {
        ComveePacket packet = ComveePacket.fromJsonString(b);
        if (packet.getResultCode() == 0) {
            JSONObject body = packet.optJSONObject("body");
            highEmpty = (float) body.optDouble("highEmpty");
            lowEmpty = (float) body.optDouble("lowEmpty");
            highFull = (float) body.optDouble("highFull");
            lowFull = (float) body.optDouble("lowFull");
        } else {
            ComveeHttpErrorControl.parseError(getActivity(), packet);
            return;
        }
        if (selectedCalendar != null) {// selectedCalendar不为空表明是从历史记录跳转过来的,数据从本地库取,此时selectedCalender和currentIndex已经有值。
            cancelProgressDialog();
            initPopupWindows();
            dataFetchedOp(false);
            requestCommentByCalendar(selectedCalendar);

        } else {// selectedCalendar为空表明是从健康记录跳转过来的，数据从远程取
            selectedCalendar = Calendar.getInstance();
            if (fromWhere != 3) {
                this.currentIndex = ConfigParams.getClearBloodTimeString();
            }
            readHistoryFromRemote();
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (money_package) {// 有红包，跳出领取红包界面
            RobRedPacketDialog dialog = new RobRedPacketDialog();
            int user_flag = mPacket.optJSONObject("body").optInt("user_flag");
            dialog.setUserFlag(user_flag);
            dialog.show(getFragmentManager(), "");
        }
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        try {
            ComveePacket packet = ComveePacket.fromJsonString(b);
            if (packet.getResultCode() == 0) {

            } else {
                ComveeHttpErrorControl.parseError(getActivity(), packet);
            }
            switch (what) {
                case 8:
                    parseComment(b);
                    String value = tvValue.getText().toString().replace("mmol/L", "");
                    if (TextUtils.isEmpty(value) && isShow) {
                        showSetSingleValueDialog(currentIndex);
                    }
                    isShow = false;
                    break;
                case 7:// 远程请求血糖记录
                    cancelProgressDialog();
                    paseTendencyPointList(what, b, fromCache);
                    initPopupWindows();
                    dataFetchedOp(false);
                    requestCommentByCalendar(selectedCalendar);
                    break;
                case 5:
                    parseData(b, fromCache);
                    horizontalListView.setSelection(currentIndex);
                    upViewValue(false);
                    break;
                case 3:
                    break;
                case 4:
                    cancelProgressDialog();
                    if (packet.getResultCode() == 0) {
                        showToast(packet.getResultMsg());
                        ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.INDEX));
                        ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TENDENCY_POINT_LIST));
                        FragmentMrg.toBack(getActivity());
                    } else {
                        ComveeHttpErrorControl.parseError(getActivity(), packet);
                    }

                    break;
                case 1:
                    cancelProgressDialog();
                    if (packet.getResultCode() == 0) {
                        showToast(packet.getResultMsg());
                        ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.INDEX));
                        ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TENDENCY_POINT_LIST));
                        RecordMrg.getRecordDetailList(null, this.getActivity(), packet, fromWhere);// 跳转到结果页
                        money_package = packet.optJSONObject("body").optInt("money_package") == 1;
                        mPacket = packet;

                    } else {
                        ComveeHttpErrorControl.parseError(getActivity(), packet);
                    }

                    break;
                case 6:
                    cancelProgressDialog();
                    if (packet.getResultCode() == 0) {
                        ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.INDEX));
                        ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TENDENCY_POINT_LIST));
                        RecordMrg.getRecordDetailList(null, this.getActivity(), packet, fromWhere);// 跳转到结果页
                        money_package = packet.optJSONObject("body").optInt("money_package") == 1;
                        mPacket = packet;

                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取当天时间段的备注
     *
     * @param b
     */
    private void parseComment(byte[] b) {
        try {
            ComveePacket packet = ComveePacket.fromJsonString(b);
            if (packet.getResultCode() == 0) {

                JSONArray array = packet.getJSONArray("body");
                int len = array.length();
                for (int i = 0; i < len; i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String memo = obj.getString("memo");
                    String paramCode = obj.getString("paramCode");
                    for (int j = 0; j < tendencyPointInfos.length; j++) {
                        if (tendencyPointInfos[j] != null && paramCode.equals(tendencyPointInfos[j].code)) {
                            tendencyPointInfos[j].comment = memo;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("tag", e.getMessage(), e);
        }
        setDesc();
    }

    /**
     * 先删除再保存血糖记录到本地数据库
     *
     * @param what
     * @param b
     * @param fromCache
     */
    private void paseTendencyPointList(int what, byte[] b, boolean fromCache) {
        try {
            ComveePacket packet = ComveePacket.fromJsonString(b);
            if (packet.getResultCode() == 0) {
                if (!fromCache) {
                    ArrayList<TendencyPointInfo> infos = new ArrayList<TendencyPointInfo>();
                    JSONArray array = packet.getJSONArray("body");
                    int len = array.length();
                    for (int i = 0; i < len; i++) {
                        JSONObject obj = array.getJSONObject(i);
                        final String code = obj.optString("code");
                        JSONArray list = obj.getJSONArray("list");
                        int count = list.length();

                        for (int j = 0; j < count; j++) {
                            JSONObject o = list.getJSONObject(j);
                            TendencyPointInfo info = new TendencyPointInfo();
                            info.code = code.trim();
                            info.time = o.optString("time");
                            info.bloodGlucoseStatus = o.optInt("bloodGlucoseStatus");
                            info.value = (float) o.optDouble("value");
                            info.type = o.optInt("type");
                            info.insertDt = o.optString("insertDt");
                            info.id = o.optString("paramLogId");
                            if (!TextUtils.isEmpty(info.getTime())) {
                                infos.add(info);
                                info.time = info.getTime().substring(0, info.getTime().lastIndexOf(":"));
                                System.out.println(info.time);
                            }
                        }
                    }
                    try {
                        db.deleteByWhere(TendencyPointInfo.class, "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    db.saveList(infos);
                }
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();

        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }

    @Override
    public void onClick(final View v) {
        final int id = v.getId();
        switch (id) {
            case TitleBarView.ID_LEFT_BUTTON:
                isShow = false;
                getActivity().onBackPressed();
                break;
            case R.id.set_value:
                show();
                break;
            case TitleBarView.ID_TITLE_LAYOUT:
                if (calendarPopupWindow != null) {
                    calendarPopupWindow.showAtLocation(findViewById(R.id.ScrollView), Gravity.CENTER, 0, 0);
                    calendarView.setShowDate(selectedCalendar);
                    calendarView.toSpecifyMonth(selectedCalendar);
                }
                break;
            case TitleBarView.ID_TITLE_DRAWABLE_RIGHT_BUTTON:
                if (calendarPopupWindow != null) {
                    calendarPopupWindow.showAtLocation(findViewById(R.id.ScrollView), Gravity.CENTER, 0, 0);
                    calendarView.setShowDate(selectedCalendar);
                    calendarView.toSpecifyMonth(selectedCalendar);
                }
                break;
            case R.id.sugar_indicator_img:
                showSetSingleValueDialog(currentIndex);
                break;
            case R.id.other:
                calendarPopupWindow.dismiss();
                break;
            case R.id.btn_remove:
                CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                builder.setMessage("是否确定要删除当前记录？");
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        requestRemoveValue(tendencyPointInfos[currentIndex].id);
                    }
                });
                builder.create().show();
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                if (calendarPopupWindow != null) {
                    calendarPopupWindow.showAtLocation(findViewById(R.id.ScrollView), Gravity.CENTER, 0, 0);
                    calendarView.setShowDate(selectedCalendar);
                }
                break;

            case R.id.btn_submit:
                try {
                    if (TextUtils.isEmpty(tendencyPointInfos[currentIndex].id)) {
                        requestSaveData();
                    } else {
                        requestModifyValue(tendencyPointInfos[currentIndex].id);
                    }
                } catch (Exception e) {
                    showToast("出错");
                    e.printStackTrace();
                }
                break;
            case R.id.showButton:
                show();
                break;
            default:
                break;
        }

    }

    public void show() {
        toFragment(RecordSurgarSetfragment.class,null,true);

    }

    public void close() {
        relative.setVisibility(View.GONE);
    }

    @SuppressWarnings("deprecation")
    public void initPopupWindows() {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View converView = inflater.inflate(R.layout.window_calendar_record_sugar, null);
        calendarPopupWindow = new PopupWindow(converView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
        calendarPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        calendarPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        calendarPopupWindow.setOutsideTouchable(true);

        LinearLayout linearLayout = (LinearLayout) converView.findViewById(R.id.contentView);
        converView.findViewById(R.id.other).setOnClickListener(this);
        View contentView = calendarView.inflate(getApplicationContext());
        calendarView.setOnChoiceCalendarListener(this);
        linearLayout.addView(contentView, 0);
    }

    /**
     * 一个值 多列的选择空间
     */
    public void showSetSingleValueDialog(int index) {
        float defValue = 0;
        final String dValue = tvValue.getText().toString().replace("mmol/L", "");
        if (!TextUtils.isEmpty(dValue)) {
            defValue = Float.valueOf(dValue);
        } else {
            defValue = 4.0f;
        }
        defValue = Math.min(33.9f, defValue);
        CustomFloatNumPickDialog builder = new CustomFloatNumPickDialog();
        builder.setDefult(defValue);
        builder.setFloat(true);
        builder.setUnit("mmol/L");
        builder.setLimitNum(1, 33);
        builder.addOnNumChangeListener(this);
        builder.show(getActivity().getSupportFragmentManager(), "dialog");
        // builder.getDialog().setOnCancelListener(this);
        //
    }

    private void onChangeTime(Calendar cal) {
        mBarView.setTitle(TimeUtil.fomateTime(cal.getTimeInMillis(), "yyyy-MM-dd"), this);
    }

    private void upViewValue(boolean checkBt) {
        int sugerType = -1;
        if (tendencyPointInfos[currentIndex] != null)
            sugerType = checkValue(tendencyPointInfos[currentIndex].value);
        showValueStatus(sugerType);
        if (checkBt)
            checkButton();
        if (highEmpty != -1) {
            setTextValue();
        }
    }

    /**
     * 显示 输入值的偏高偏低状态
     */
    private void showValueStatus(int num) {
        Drawable drawable = null;
        final String lowColor = "#3399ff";
        final String normalColor = "#66cc66";
        final String hightColor = "#ff3300";
        final int defaultCircle = R.drawable.jilu_65;
        final int lowCircle = R.drawable.jilu_67;
        final int normalCircle = R.drawable.jilu_70;
        final int hightCircle = R.drawable.jilu_71;
        switch (num) {
            case 1:// low

                drawable = getResources().getDrawable(R.drawable.jilu_55);
                drawable.setBounds(0, 10, drawable.getMinimumWidth(), tvValue.getHeight() / 2);
                tvValue.setText(tendencyPointInfos[currentIndex].value + "");
                tvValue.setCompoundDrawables(null, null, drawable, null);
                tvValue.setTextColor(Color.parseColor(lowColor));
                sugarIndicatorImg.setImageResource(lowCircle);
                break;
            case 3:// normal
            default:
                tvValue.setText(tendencyPointInfos[currentIndex].value + "");
                tvValue.setCompoundDrawables(null, null, null, null);
                tvValue.setTextColor(Color.parseColor(normalColor));
                sugarIndicatorImg.setImageResource(normalCircle);
                break;
            case 5:// hight
                tvValue.setText(tendencyPointInfos[currentIndex].value + "");
                drawable = getResources().getDrawable(R.drawable.jilu_57);
                drawable.setBounds(0, 10, drawable.getMinimumWidth(), tvValue.getHeight() / 2);
                tvValue.setCompoundDrawables(null, null, drawable, null);
                tvValue.setTextColor(Color.parseColor(hightColor));
                sugarIndicatorImg.setImageResource(hightCircle);
                break;

        }
    }

    private int checkValue(float num) {
        if (num <= 0)
            return -1;
        float minLimit = -1.0f;
        float maxLimit = -1.0f;
        if (ConfigParams.SUGAR_TIME_STR1[currentIndex].contains("空腹血糖")) {
            minLimit = lowEmpty;
            maxLimit = highEmpty;
        } else {
            minLimit = lowFull;
            maxLimit = highFull;
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
     * 修改值
     **/
    private void requestModifyValue(String id) {
        showProgressDialog(getString(R.string.msg_loading));
        try {
            showProgressDialog(getString(R.string.msg_loading));
            ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.RECORD_MODIFY_SURGAR_VALUE_NEW);
            http.setPostValueForKey("recordTime", mBarView.gettitleView().getText() + " " + ConfigParams.TIMES_SUGAR[currentIndex] + ":00");
            http.setPostValueForKey("value", tvValue.getText().toString().replace("mmol/L", ""));
            http.setPostValueForKey("paramCode", ConfigParams.SUGAR_TIME_CODE[currentIndex]);
            http.setPostValueForKey("paramLogId", id);
            http.setPostValueForKey("memo", edtDecs.getText().toString());
            http.setOnHttpListener(1, this);
            http.startAsynchronous();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestSaveData() throws JSONException {
        String time = mBarView.gettitleView().getText() + " " + ConfigParams.TIMES_SUGAR[currentIndex] + ":00";
        if (null == tendencyPointInfos[currentIndex] || tendencyPointInfos[currentIndex].value == 0) {
            showToast("请先填写测量值!");
            return;
        }
        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.RECORD_SUGGER);
        http.setPostValueForKey("recordTime", time);
        http.setPostValueForKey("paramCode", ConfigParams.SUGAR_TIME_CODE[currentIndex]);
        http.setPostValueForKey("value", tvValue.getText().toString().replace("mmol/L", ""));
        http.setPostValueForKey("memo", edtDecs.getText().toString());
        http.setOnHttpListener(6, this);
        http.startAsynchronous();
    }

    private void requestRemoveValue(String id) {
        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.RECORD_REMOVE_SURGAR_VALUE);
        http.setPostValueForKey("paramLogId", id);
        http.setOnHttpListener(4, this);
        http.startAsynchronous();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        checkButton();
    }

    private void checkButton() {
        boolean b = !TextUtils.isEmpty(tvValue.getText());
        btnSubmit.setEnabled(b);
        if (tendencyPointInfos[currentIndex] == null || TextUtils.isEmpty(tendencyPointInfos[currentIndex].id)) {
            btnRemove.setVisibility(View.GONE);
        } else {
            btnRemove.setVisibility(View.VISIBLE);
        }
        btnSubmit.setVisibility(View.VISIBLE);
    }

    /**
     * 检测是否有emoji字符
     *
     * @param source
     * @return 一旦含有就抛出
     */
    private boolean containsEmoji(String source) {

        int len = source.length();

        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);

            if (isEmojiCharacter(codePoint)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    @Override
    public void onItemChoice(Calendar calendarSelected, int position, boolean isRecord) {
        calendarPopupWindow.dismiss();
        selectedCalendar = calendarSelected;
        requestCommentByCalendar(selectedCalendar);
        dataFetchedOp(true);
    }

    private void requestCommentByCalendar(Calendar selectedCalendar_) {
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.TENDENCY_POINT_LIST_ONE_DAY);
        http.setPostValueForKey("date", sdf.format(selectedCalendar_.getTime()));
        http.setOnHttpListener(8, this);
        http.startAsynchronous();
    }

    @Override
    public void onRightClick(Calendar calendar, boolean isRecord) {
        selectedCalendar = calendar;
        dataFetchedOp(true);
    }

    private void setTextValue() {
        String strLimit = null;
        if (ConfigParams.SUGAR_TIME_STR1[currentIndex].contains("空腹血糖")) {
            strLimit = lowEmpty + "-" + highEmpty;
        } else {
            strLimit = lowFull + "-" + highFull;
        }
        if (UserMrg.isTnb()) {
            setValue.setText(Html.fromHtml(String.format("控制目标: %s mmol/L", strLimit)));
            setValue.setBackgroundResource(R.drawable.bg_textview);
            setValue.setClickable(true);
        } else {
            setValue.setClickable(false);
            setValue.setBackgroundColor(0xffffff);
            setValue.setText(Html.fromHtml(String.format("标准值范围: %s mmol/L", strLimit)));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentIndex = position;
        sugarHorizonAdapter.setSelectIndex(currentIndex);
        horizontalListView.setSelection(currentIndex);
        setTextValue();
        upViewValue(true);
        setDesc();
    }

    private void setDesc() {
        if (tendencyPointInfos[currentIndex] != null) {
            edtDecs.setText(tendencyPointInfos[currentIndex].comment);
        } else {
            edtDecs.setText("");
        }
    }

    @Override
    public void onChange(DialogFragment dialog, float num) {
        if (tendencyPointInfos[currentIndex] == null) {
            tendencyPointInfos[currentIndex] = new TendencyPointInfo();
        }
        tendencyPointInfos[currentIndex].value = num;
        upViewValue(true);
    }

    @Override
    public boolean onBackPress() {
        if (class1 != null) {
            FragmentMrg.popBackToFragment(getActivity(), class1, null);
            return true;
        }
        isShow = false;
        return super.onBackPress();
    }
}
