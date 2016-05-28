package com.comvee.tnb.ui.pharmacy;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.PharmacyTimesAdapter;
import com.comvee.tnb.adapter.PharmacyTimesAdapter.UpdataAddTimeView;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.dialog.CustomFloatNumPickDialog;
import com.comvee.tnb.dialog.CustomFloatNumPickDialog.OnNumChangeListener;
import com.comvee.tnb.dialog.CustomMultiNumPickDialog;
import com.comvee.tnb.dialog.CustomMultiNumPickDialog.OnChangeMultiNumListener;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.ui.pharmacy.MedicinalListFragment.OnSelectDrugListener;
import com.comvee.tnb.widget.MyListView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.AppUtil;
import com.comvee.tool.UITool;
import com.comvee.tool.UserMrg;
import com.comvee.ui.remind.TimeRemindTransitionInfo;
import com.comvee.ui.remind.TimeRemindUtil;
import com.comvee.util.BundleHelper;

import java.util.ArrayList;
import java.util.List;

public class PharmacySetTimeFrag extends BaseFragment implements OnClickListener, OnHttpListener, UpdataAddTimeView, OnNumChangeListener,
        OnItemClickListener, OnSelectDrugListener {
    public final static int FROM_NEW = 0;// 新建用药
    public final static int FROM_HISTORY = 1;// 已有的用药
    private View lin_select_drug, lin_select_drug_num;
    private TextView tv_name, tv_unit, tv_week_1, tv_week_2, tv_week_3, tv_week_4, tv_week_5, tv_week_6, tv_week_7, tv_add_time;
    // 日期（周日、周一。。。周六）
    private MyListView listview_time;
    private PharmacyTimesAdapter mAdapter;
    private List<TimeRemindTransitionInfo> mList;
    private TimeRemindUtil remindUtil;
    private TimeRemindTransitionInfo tempInfo, oldInfo;// 用来临时存储用药的数据信息
    private CheckBox check_pharmacy;
    private Button btn_remove;// 选择的药品
    private int fromWhere;// 从什么地方进入
    private EditText edt_decs;
    private List<TextView> weekViews;
    private int selectWeek;
    private String[] times = {"08:00", "12:30", "18:00", "19:00", "20:00", "21:00"};
    private View group_check;
    private TitleBarView mBarView;

    public PharmacySetTimeFrag(TimeRemindTransitionInfo info, int fromWhere) {
        this.tempInfo = info;
        this.oldInfo = info == null ? null : info.clone();
        if (oldInfo != null) {
            oldInfo.setWeek(info.getWeek().clone());
        }
        this.fromWhere = fromWhere;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.pharmacy_set_time_frag;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        remindUtil = TimeRemindUtil.getInstance(getApplicationContext());
        mBarView.setTitle(getText(R.string.pharmacy_settime_title).toString());
        init();
        initData();
        UITool.setEditextRemove(getActivity(), R.id.scrollView);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (tempInfo == null) {
            tempInfo = new TimeRemindTransitionInfo();
            tempInfo.setType(4);
            tempInfo.setMemberId(UserMrg.DEFAULT_MEMBER.mId);
            tempInfo.setMemName(UserMrg.DEFAULT_MEMBER.name);
            tempInfo.setDiabolo(true);
        }
        switch (fromWhere) {
            case 0:
                if (mList == null) {
                    mList = new ArrayList<TimeRemindTransitionInfo>();
                }
                btn_remove.setVisibility(View.GONE);
                break;
            case 1:
                if (mList == null) {
                    mList = remindUtil.getRemindTimeList("pmType=" + tempInfo.getPmType());
                }
                btn_remove.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        updataDrugView();
        updataWeek();
        notifyTimeList();
    }

    private void updataDrugView() {
        if (!TextUtils.isEmpty(tempInfo.getDrugName())) {
            tv_name.setText(tempInfo.getDrugName());
            tv_name.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            tv_unit.setText(tempInfo.getDrugUnit());
        }

        if (TextUtils.isEmpty(edt_decs.getText().toString()) && !TextUtils.isEmpty(tempInfo.getRemark())) {
            edt_decs.setText(tempInfo.getRemark());
        }
        check_pharmacy.setChecked(tempInfo.isDiabolo());
        group_check.setBackgroundResource(check_pharmacy.isChecked() ? R.color.white : R.color.backage_color);
    }

    /**
     * 更新时间列表
     */
    private void notifyTimeList() {
        mAdapter.setList(mList);
        mAdapter.notifyDataSetChanged();
        if (mList.size() >= 6) {
            findViewById(R.id.line).setVisibility(View.GONE);
            findViewById(R.id.line_1).setVisibility(View.GONE);
        } else if (mList.size() == 0) {
            findViewById(R.id.line_2).setVisibility(View.GONE);
        } else {
            findViewById(R.id.line).setVisibility(View.VISIBLE);
            findViewById(R.id.line_1).setVisibility(View.VISIBLE);
            findViewById(R.id.line_2).setVisibility(View.VISIBLE);
        }
    }

    /**
     * 更新星期控件
     */
    private void updataWeek() {
        selectWeek = 0;
        for (int i = 0; i < tempInfo.getWeek().length; i++) {
            if (tempInfo.getWeek()[i]) {
                weekViews.get(i).setTextColor(getResources().getColor(R.color.white));
                weekViews.get(i).setBackgroundResource(R.drawable.pharmacy_week_bg_blue);
                ++selectWeek;
            } else {
                weekViews.get(i).setTextColor(getResources().getColor(R.color.text_color_2));
                weekViews.get(i).setBackgroundResource(R.drawable.pharmacy_week_bg_default);
            }
        }
    }

    /**
     * 初始化控件
     */
    private void init() {
        mBarView.setRightButton("保存", this);
        weekViews = new ArrayList<TextView>();
        lin_select_drug = findViewById(R.id.lin_select_drug);
        lin_select_drug_num = findViewById(R.id.lin_select_drug_num);
        check_pharmacy = (CheckBox) findViewById(R.id.check_pharmacy);
        listview_time = (MyListView) findViewById(R.id.listview_time);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_unit = (TextView) findViewById(R.id.tv_unit);
        tv_add_time = (TextView) findViewById(R.id.tv_add_time);
        btn_remove = (Button) findViewById(R.id.btn_remove);
        edt_decs = (EditText) findViewById(R.id.edt_decs);
        group_check = findViewById(R.id.group_check);
        tv_week_1 = (TextView) findViewById(R.id.tv_week_1);
        tv_week_2 = (TextView) findViewById(R.id.tv_week_2);
        tv_week_3 = (TextView) findViewById(R.id.tv_week_3);
        tv_week_4 = (TextView) findViewById(R.id.tv_week_4);
        tv_week_5 = (TextView) findViewById(R.id.tv_week_5);
        tv_week_6 = (TextView) findViewById(R.id.tv_week_6);
        tv_week_7 = (TextView) findViewById(R.id.tv_week_7);
        weekViews.add(tv_week_1);
        weekViews.add(tv_week_2);
        weekViews.add(tv_week_3);
        weekViews.add(tv_week_4);
        weekViews.add(tv_week_5);
        weekViews.add(tv_week_6);
        weekViews.add(tv_week_7);
        tv_week_1.setOnClickListener(this);
        tv_week_2.setOnClickListener(this);
        tv_week_3.setOnClickListener(this);
        tv_week_4.setOnClickListener(this);
        tv_week_5.setOnClickListener(this);
        tv_week_6.setOnClickListener(this);
        tv_week_7.setOnClickListener(this);
        tv_add_time.setOnClickListener(this);
        btn_remove.setOnClickListener(this);
        check_pharmacy.setOnClickListener(this);
        lin_select_drug.setOnClickListener(this);
        lin_select_drug_num.setOnClickListener(this);
        listview_time.setOnItemClickListener(this);
        check_pharmacy.setOnClickListener(this);
        AppUtil.registerEditTextListener1(edt_decs, R.string.sport_remarktip, 500, getApplicationContext());
        mAdapter = new PharmacyTimesAdapter(getActivity(), mList, this);
        listview_time.setAdapter(mAdapter);

    }

    /**
     * 添加闹钟时间
     */
    private void addTimeData() {
        TimeRemindTransitionInfo info = new TimeRemindTransitionInfo();
        String time = times[mList.size() % 6];
        info.setHour(Integer.parseInt(time.split(":")[0]));
        info.setMinute(Integer.parseInt(time.split(":")[1]));
        info.setType(4);
        info.setDrugUnit(tempInfo.getDrugUnit());
        info.setMemberId(UserMrg.DEFAULT_MEMBER.mId);
        info.setMemName(UserMrg.DEFAULT_MEMBER.name);
        mList.add(info);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.tv_week_1:
                tempInfo.getWeek()[0] = !tempInfo.getWeek()[0];
                break;
            case R.id.tv_week_2:
                tempInfo.getWeek()[1] = !tempInfo.getWeek()[1];
                break;
            case R.id.tv_week_3:
                tempInfo.getWeek()[2] = !tempInfo.getWeek()[2];
                break;
            case R.id.tv_week_4:
                tempInfo.getWeek()[3] = !tempInfo.getWeek()[3];
                break;
            case R.id.tv_week_5:
                tempInfo.getWeek()[4] = !tempInfo.getWeek()[4];
                break;
            case R.id.tv_week_6:
                tempInfo.getWeek()[5] = !tempInfo.getWeek()[5];
                break;
            case R.id.tv_week_7:
                tempInfo.getWeek()[6] = !tempInfo.getWeek()[6];
                break;
            case R.id.tv_add_time:
                if (mList.size() >= 6) {
                    showToast(getResources().getString(R.string.pharmacy_toast_text_1));
                } else if (TextUtils.isEmpty(tempInfo.getDrugName())) {
                    showToast("请先选择药品");
                } else {
                    addTimeData();
                    notifyTimeList();
                }
                break;
            case R.id.btn_remove:

                showRemoveMsg();
                break;
            case R.id.lin_select_drug:
                toFragment(new MedicinalFragment(this), true, true);
                break;
            case R.id.lin_select_drug_num:
                if (TextUtils.isEmpty(tempInfo.getDrugName())) {
                    showToast("请先选择药品");
                } else {
                    showSetSingleValueDialog();
                }
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                if (TextUtils.isEmpty(tv_unit.getText().toString())) {
                    showToast("请先选择药品");
                } else if (selectWeek == 0) {
                    showToast("请选择用药时间");
                } else if (mList.size() == 0) {
                    showToast("请添加用药次数");
                } else {
                    requestSaveTimeList();
                }
                break;
            case R.id.check_pharmacy:
                tempInfo.setDiabolo(!tempInfo.isDiabolo());
                group_check.setBackgroundResource(check_pharmacy.isChecked() ? R.color.white : R.color.backage_color);
                break;
            default:
                break;

        }
        updataDrugView();
        updataWeek();
    }

    private void showRemoveMsg() {
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setMessage(R.string.pharmacy_remove_msg);
        builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                removeMsg();
            }
        });
        builder.setPositiveButton(R.string.no, null);
        builder.create().show();
    }

    private void showChangeMsg() {
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setMessage(R.string.pharmacy_change_msg);
        builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                requestSaveTimeList();
            }
        });
        builder.setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                FragmentMrg.toBack(getActivity());
            }
        });
        builder.create().show();
    }

    private void showAddMsg() {
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setMessage(R.string.pharmacy_add_msg);
        builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                FragmentMrg.toBack(getActivity());
            }
        });
        builder.setPositiveButton(R.string.no, null);
        builder.create().show();
    }

    @Override
    public boolean onBackPress() {
        switch (fromWhere) {
            case FROM_NEW:
                if (!TextUtils.isEmpty(tv_name.getText().toString()) || !check_pharmacy.isChecked() || !TextUtils.isEmpty(tv_unit.getText().toString())
                        || !TextUtils.isEmpty(edt_decs.getText().toString()) || !contrastWeek(null, tempInfo.getWeek())) {
                    showAddMsg();
                    return true;
                }
                break;
            case FROM_HISTORY:
                if (oldInfo != null) {
                    if (!oldInfo.getDrugName().equals(tv_name.getText().toString()) || !oldInfo.getDrugUnit().equals(tv_unit.getText().toString())
                            || oldInfo.isDiabolo() != check_pharmacy.isChecked() || !oldInfo.getRemark().equals(edt_decs.getText().toString())
                            || !contrastWeek(oldInfo.getWeek(), tempInfo.getWeek())) {

                        showChangeMsg();
                        return true;
                    }
                }
                break;
            default:
                break;
        }
        return false;
    }

    private boolean contrastWeek(boolean[] old, boolean[] now) {
        if (old == null) {
            for (int i = 0; i < now.length; i++) {
                if (now[i]) {
                    return false;
                }
            }
        } else if (old != null && now != null) {
            for (int i = 0; i < now.length; i++) {
                if (old[i] != now[i]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void updataView() {
        notifyTimeList();
    }

    /**
     * 一个值 多列的选择空间
     */
    public void showSetSingleValueDialog() {
        float defValue = 1.0f;

        defValue = Math.min(50f, defValue);
        CustomFloatNumPickDialog builder = new CustomFloatNumPickDialog();
        builder.setTitle("药物剂量");
        if (!TextUtils.isEmpty(tempInfo.getDrugUnit()) && tempInfo.getDrugUnit().split(" ").length == 2) {
            builder.setUnit(" " + tempInfo.getDrugUnit().split(" ")[1]);
            defValue = Float.parseFloat(tempInfo.getDrugUnit().split(" ")[0]);
            int num = (int) (defValue * 10 % 10);
            if (num != 0 && num != 5) {
                defValue = defValue * 10 / 10;
            }
        }
        builder.setDefult(defValue);
        builder.setFloat(true);
        builder.setLimitNum(0, 50);
        builder.addOnNumChangeListener(this);
        builder.setCustomFloat(0, 2, 5);
        builder.show(getActivity().getSupportFragmentManager(), "dialog");
        // builder.getDialog().setOnCancelListener(this);
        //
    }

    @Override
    public void onChange(DialogFragment dialog, float num) {
        if (num == 0) {
            showToast(getString(R.string.pharmacy_select_zero));
            return;
        }
        String str[] = tv_unit.getText().toString().split(" ");
        tv_unit.setText(num + " " + (str.length == 2 ? str[1] : "片"));
        tempInfo.setDrugUnit(num + " " + (str.length == 2 ? str[1] : "片"));
        updateTimeList();
    }

    private void updateTimeList() {
        if (mList != null && tempInfo != null) {
            for (int i = 0; i < mList.size(); i++) {
                mList.get(i).setDrugUnit(tempInfo.getDrugUnit());
            }
        }
        notifyTimeList();
    }

    /**
     * 更改和添加用药
     */
    private void requestSaveTimeList() {
        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.ADD_PHARMACY_REMIND);
        http.setPostValueForKey("drugDepotId", tempInfo.getDrugId() + "");
        http.setPostValueForKey("drugNumber", tempInfo.getDrugUnit().split(" ")[0]);
        http.setPostValueForKey("remark", edt_decs.getText().toString());
        http.setPostValueForKey("onOff", tempInfo.isDiabolo() == true ? "1" : "0");
        http.setPostValueForKey("id", tempInfo.getPmType() + "");
        http.setPostValueForKey("remindTime", getRemindTimeStr());
        http.setOnHttpListener(1, this);
        http.startAsynchronous();
    }

    /**
     * 拼接闹钟时间字符串 时间格式： (08:00,09:00week:0,1,3)其中week后表示闹钟选中的星期 0为星期一 以此类推
     *
     * @return
     */
    private String getRemindTimeStr() {
        StringBuffer str = new StringBuffer();
        for (int j = 0; j < mList.size(); j++) {
            String hour = mList.get(j).getHour() > 9 ? mList.get(j).getHour() + "" : "0" + mList.get(j).getHour();
            String minute = mList.get(j).getMinute() > 9 ? mList.get(j).getMinute() + "" : "0" + mList.get(j).getMinute();
            str.append(hour + ":" + minute);
            if (j != mList.size() - 1) {
                str.append(",");
            }
        }
        str.append("week:");
        for (int i = 0; i < tempInfo.getWeek().length; i++) {
            if (tempInfo.getWeek()[i]) {
                if (i == 0) {
                    str.append(7);
                } else {
                    str.append(i);
                }
                str.append(",");
            }

        }
        str.deleteCharAt(str.length() - 1);
        return str.toString();
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();
        switch (what) {
            case 1:
                parseSaveResult(b);
                remindUtil.star();

                break;
            case 2:
                parseRemoveResut(b);
                break;
            default:
                break;
        }
    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();
    }

    private void parseSaveResult(byte[] b) {
        try {
            ComveePacket packet = ComveePacket.fromJsonString(b);
            if (packet.getResultCode() == 0) {
                long pmType = packet.optJSONObject("body").optLong("obj");
                remindUtil.deleteTime("pmType=" + pmType);
                for (int i = 0; i < mList.size(); i++) {
                    TimeRemindTransitionInfo info = mList.get(i);
                    info.setPmType(pmType);
                    info.setDiabolo(tempInfo.isDiabolo());
                    info.setDrugName(tempInfo.getDrugName());
                    info.setDrugUnit(tempInfo.getDrugUnit());
                    info.setRemark(edt_decs.getText().toString());
                    info.setWeek(tempInfo.getWeek());
                    info.setDrugId(tempInfo.getDrugId());
                    remindUtil.addTime(info);

                }
                showToast(getText(R.string.pharmacy_save_ok).toString());
                FragmentMrg.toBack(getActivity(), new Bundle());
            } else {
                ComveeHttpErrorControl.parseError(getActivity(), packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void removeMsg() {
        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.DELETE_PHARMACY_REMIND);
        http.setPostValueForKey("id", tempInfo.getPmType() + "");
        http.setOnHttpListener(2, this);
        http.startAsynchronous();
    }

    private void parseRemoveResut(byte[] b) {
        try {
            ComveePacket packet = ComveePacket.fromJsonString(b);
            if (packet.getResultCode() == 0) {
                showToast("删除成功");
                remindUtil.deleteTime("pmType=" + tempInfo.getPmType());
                new IndexListDataRequest().clearCache();
                FragmentMrg.toBack(getActivity(), new Bundle());
            } else {
                ComveeHttpErrorControl.parseError(getActivity(), packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 多值 多列 的选择控件
     */
    public void showSetMultiValueDialog(final TimeRemindTransitionInfo info) {

        int[] max = {23, 59};
        int[] min = {0, 0};
        // 如果已经填写这个值 就有 用 这个值 如果没有 用默认值
        float[] def = {info.getHour(), info.getMinute()};
        String[] titles = {"小时", "分钟"};
        String[] units = {"", ""};
        CustomMultiNumPickDialog builder = new CustomMultiNumPickDialog();
        builder.setMultiDefualtNum(def);
        // builder.setFloat(mModel.isFloat);
        builder.setMultiTitles(titles);
        builder.setMultiLimit(min, max);
        builder.setMultiUnits(units);
        builder.setOnChangeMultiNumListener(new OnChangeMultiNumListener() {

            @Override
            public void onChange(DialogFragment dialog, float[] num) {
                if (num != null && num.length == 2) {
                    info.setHour((int) num[0]);
                    info.setMinute((int) num[1]);
                }
                updataView();
            }
        });
        builder.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        showSetMultiValueDialog((TimeRemindTransitionInfo) arg0.getAdapter().getItem(arg2));
    }

    @Override
    public void selectDrug(MedicinalModel model) {
        if (tempInfo == null) {
            tempInfo = new TimeRemindTransitionInfo();
        }
        if (model != null) {
            tempInfo.setDrugName(model.name);
            tempInfo.setDrugUnit("1.0 " + model.unit);
            tempInfo.setDrugId(model.id);
        }
        updataDrugView();
        updateTimeList();
    }

    @Override
    public void onFragmentResult(Bundle data) {
        super.onFragmentResult(data);
        if (data != null) {
            MedicinalModel info = BundleHelper.getSerializableByBundle(data);
            selectDrug(info);
        }
    }
}
