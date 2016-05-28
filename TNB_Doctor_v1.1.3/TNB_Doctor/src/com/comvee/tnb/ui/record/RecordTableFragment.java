package com.comvee.tnb.ui.record;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.comvee.FinalDb;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.CanTitleSeleteable;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.ComveePageAdapter;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.TendencyInputModel;
import com.comvee.tnb.model.TendencyInputModelItem;
import com.comvee.tnb.model.TendencyPointInfo;
import com.comvee.tnb.ui.assess.AssessFragment;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.view.RecordTableView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.TendencyInputMrg;
import com.comvee.tool.UserMrg;
import com.comvee.util.TimeUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 录入趋势图数据 列表
 *
 * @author friendlove
 */
public class RecordTableFragment extends BaseFragment implements IndexPageable, OnHttpListener, OnClickListener, OnPageChangeListener,
        CanTitleSeleteable {
    FinalDb db = null;
    private ArrayList<TendencyInputModel> infos;
    private HashMap<String, ArrayList<TendencyInputModelItem>> maps;
    private ViewPager vPager;
    private MyPageAdapter mAdapter;
    private int pageIndex;
    private int oldTabIndex = 1;
    private String memberId;
    private TitleBarView mBarView;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            requestTendencyPointList();
        }

    };

    public static RecordTableFragment newInstance() {
        RecordTableFragment fragment = new RecordTableFragment();
        return fragment;
    }

    public void setMemberId(String merberId) {
        this.memberId = merberId;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_tendency_add_data;
    }

    @Override
    public void onStart() {
        super.onStart();
        // setTitle("健康记录");

    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        db = FinalDb.create(getApplicationContext(), ConfigParams.DB_NAME);
        init();
        requestTendencyPointList();
        registReceiver();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public int getCurrentIndex() {
        return pageIndex;
    }

    @Override
    public void onDetach() {

        super.onDetach();

    }

    @Override
    public void onDestroyView() {
        unRegistReceiver();
        super.onDestroyView();
    }

    public void requestTendencyPointList() {
        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.TENDENCY_POINT_LIST);
        // http.setNeedGetCache(true,
        // UserMrg.getCacheKey(UrlMrg.TENDENCY_POINT_LIST));
        String endDt = TimeUtil.fomateTime(getEndTime(), "yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -3);
        String startDt = TimeUtil.fomateTime(cal.getTimeInMillis(), "yyyy-MM-dd HH:mm:ss");
        http.setPostValueForKey("startDt", startDt);
        http.setPostValueForKey("endDt", endDt);
        http.setPostValueForKey("paramKey", RecordTendencyFragment.createParamString());
        if (!TextUtils.isEmpty(memberId)) {
            http.setPostValueForKey("memberId", memberId);
        }
        http.setOnHttpListener(1, this);
        http.startAsynchronous();

    }

    private Long getEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 24);
        return todayEnd.getTimeInMillis();
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();
        paseTendencyPointList(what, b, fromCache);
    }

    public void paseTendencyPointList(int index, byte[] b, boolean fromCache) {

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
                            // info.tempTime = o.optString("time");
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

                    ComveeHttp.setCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TENDENCY_POINT_LIST),
                            ConfigParams.CHACHE_TIME_LONG, b);

                } else {

                }

                updateAllPage();
            } else {
                // showToast(packet.getResultMsg());
                ComveeHttpErrorControl.parseError(getActivity(), packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void init() {
        mBarView.setRightButton(R.drawable.ask_list_titlebat_right, this);
        if (maps == null) {
            maps = new HashMap<String, ArrayList<TendencyInputModelItem>>();
        } else {
            maps.clear();
        }
        if (infos == null) {
            ArrayList<TendencyInputModel> list = (ArrayList<TendencyInputModel>) TendencyInputMrg.getInstance(getApplicationContext())
                    .getArrayDisplay().clone();
            infos = list;
        }
        if (mAdapter == null) {
            mAdapter = new MyPageAdapter();
        }
        // 初始化分页
        vPager = (ViewPager) findViewById(R.id.vPager);
        vPager.setAdapter(mAdapter);
        vPager.addOnPageChangeListener(this);
        vPager.setCurrentItem(pageIndex);
        onUpdate(oldTabIndex);
        // choiceTabUI(oldTabIndex);
        switch (pageIndex) {
            case 0:
                mBarView.setTitle(getString(R.string.record_history_sugar));
                break;
            case 1:
                mBarView.setTitle(getString(R.string.record_history_blood));
                break;
            case 2:
                mBarView.setTitle(getString(R.string.record_history_bmi));
                break;
            case 3:
                mBarView.setTitle(getString(R.string.record_history_ghb));
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.btn_non_jump:
                toFragment(new AssessFragment(), true, true);
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                switch (pageIndex) {
                    case 0:
                        toFragment(RecordSugarInputNewFrag.class, null, true);
                        break;
                    case 1:
                        toFragment(RecordPressBloodInputFragment.newInstance(null), true, true);
                        break;
                    case 2:
                        toFragment(RecordBmiInputFragment.newInstance(null), true, true);
                        break;
                    case 3:
                        toFragment(RecordHemoglobinFragment.newInstance(null), true, true);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    public void clearInputData(int index) {
        if (infos == null || index > infos.size() - 1) {
            return;
        }

        final TendencyInputModel model = infos.get(index);

        final ArrayList<TendencyInputModelItem> list = maps.get(model.label);
        for (int j = 0; j < list.size(); j++) {

            if (TextUtils.isEmpty(list.get(j).realValue)) {
                showToast("请先填写测量值!");
                return;
            }

            list.get(j).realValue = "";
        }

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        pageIndex = arg0;
        switch (pageIndex) {
            case 0:
                mBarView.setTitle(getString(R.string.record_history_sugar));
                break;
            case 1:
                mBarView.setTitle(getString(R.string.record_history_blood));
                break;
            case 2:
                mBarView.setTitle(getString(R.string.record_history_bmi));
                break;
            case 3:
                mBarView.setTitle(getString(R.string.record_history_ghb));
                break;
            default:
                break;
        }
    }

    @Override
    public void onSelete(int index) {
        vPager.setCurrentItem(index, true);
    }

    @Override
    public void onLoadMore() {
    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }

    public void updateAllPage() {
        if (null != infos) {
            for (int i = 0; i < infos.size(); i++) {
                onUpdate(i);
            }
        }
    }

    public void onUpdate(int index) {
        try {
            if (mAdapter != null && infos != null) {
                onUpdate((RecordTableView) mAdapter.getCacheView(index), infos.get(index).label);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onUpdate(RecordTableView view, String label) {
        if (view != null) {
            view.updateData(label, oldTabIndex);
        }
    }

    public final void choiceTabUI(int index) {
        oldTabIndex = index;
        updateAllPage();
        // onUpdate(pageIndex);
    }

    public void setCurrentInex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    @Override
    public boolean onBackPress() {
        if (FragmentMrg.isContain(HealthRecordRusultFragment.class)) {
            FragmentMrg.popBackToFragment(getActivity(), HealthRecordRusultFragment.class, null, true);
        } else {
            IndexFrag.toFragment(getActivity(), false);
        }
        return true;
    }

    private void registReceiver() {
        IntentFilter filter = new IntentFilter(ConfigParams.ACTION_BLOOD);
        getActivity().registerReceiver(receiver, filter);
    }

    private void unRegistReceiver() {
        try {
            getActivity().unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MyPageAdapter extends ComveePageAdapter {

        public MyPageAdapter() {
            maps = new HashMap<String, ArrayList<TendencyInputModelItem>>();
        }

        @Override
        public View getView(int position) {
            TendencyInputModel info = infos.get(position);
            RecordTableView view = new RecordTableView(getApplicationContext());
            view.setMainFragment(RecordTableFragment.this);
            view.setTendencyInputModel(info);
            ArrayList<TendencyInputModelItem> items = null;
            if (maps.containsKey(info.label)) {
                items = maps.get(info.label);
            } else {
                if (info.label.equals("血糖")) {
                    items = TendencyInputMrg.getInstance(getApplicationContext()).getInputModelItems(
                            new String[]{"beforedawn", "beforeBreakfast", "afterBreakfast", "beforeLunch", "afterLunch", "beforeDinner",
                                    "afterDinner", "beforeSleep"});
                } else if (info.label.equals("凌晨血糖")) {
                    items = TendencyInputMrg.getInstance(getApplicationContext()).getInputModelItems(new String[]{"beforedawn"});
                } else if (info.label.equals("空腹血糖")) {
                    items = TendencyInputMrg.getInstance(getApplicationContext()).getInputModelItems(new String[]{"beforeBreakfast"});
                } else if (info.label.equals("早餐后2小时血糖")) {
                    items = TendencyInputMrg.getInstance(getApplicationContext()).getInputModelItems(new String[]{"afterBreakfast"});
                } else if (info.label.equals("午餐前血糖")) {
                    items = TendencyInputMrg.getInstance(getApplicationContext()).getInputModelItems(new String[]{"beforeLunch"});
                } else if (info.label.equals("午餐后2小时血糖")) {
                    items = TendencyInputMrg.getInstance(getApplicationContext()).getInputModelItems(new String[]{"afterLunch"});
                } else if (info.label.equals("晚餐前血糖")) {
                    items = TendencyInputMrg.getInstance(getApplicationContext()).getInputModelItems(new String[]{"beforeDinner"});
                } else if (info.label.equals("晚餐后2小时血糖")) {
                    items = TendencyInputMrg.getInstance(getApplicationContext()).getInputModelItems(new String[]{"afterDinner"});
                } else if (info.label.equals("睡前血糖")) {
                    items = TendencyInputMrg.getInstance(getApplicationContext()).getInputModelItems(new String[]{"beforeSleep"});
                } else {
                    items = TendencyInputMrg.getInstance(getApplicationContext()).getInputModelItems(info.label);
                }

                maps.put(info.label, items);
            }

            // maps.put(info.label, items);
            view.setTendencyInputModelItem(items);

            view.init();
            onUpdate(view, info.label);
            return view;
        }

        @Override
        public int getCount() {
            return infos.size();
        }
    }
}
