package com.comvee.tnb.ui.record;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.comvee.FinalDb;
import com.comvee.frame.FragmentMrg;
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
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.view.TendencyMainPageView;
import com.comvee.tnb.widget.CustomViewpager;
import com.comvee.tnb.widget.PageControlView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.TendencyInputMrg;
import com.comvee.util.TimeUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 趋势图
 *
 * @author friendlove
 */
public class RecordTendencyFragment extends BaseFragment implements IndexPageable, OnHttpListener, OnClickListener, OnPageChangeListener {

    FinalDb db = null;
    private ArrayList<TendencyInputModel> infos;
    private HashMap<String, ArrayList<TendencyInputModelItem>> maps;
    private CustomViewpager mViewPage;
    private MyPageAdapter mAdapter;
    private PageControlView vIndicator;
    private int pageIndex;
    private int oldTabIndex = 1;
    private int fromFragment = 0;// 0首页/1趋势图大页面
    private String memberId;
    private TitleBarView mBarView;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    updateAllPage();
                    cancelProgressDialog();
                    break;

                default:
                    break;
            }
        }

        ;
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            requestTendencyPointList();
        }

    };

    public RecordTendencyFragment() {
    }

    public static RecordTendencyFragment newInstance() {
        RecordTendencyFragment fragment = new RecordTendencyFragment();
        return fragment;
    }

    public static String createParamString() {
        JSONArray array = new JSONArray();
        try {
            JSONObject obj = new JSONObject();
            obj.put("code", "bmi");
            array.put(obj);
            obj = new JSONObject();
            obj.put("code", "bloodpressurediastolic");
            array.put(obj);
            obj = new JSONObject();
            obj.put("code", "bloodpressuresystolic");
            array.put(obj);
            obj = new JSONObject();
            obj.put("code", "beforedawn");
            array.put(obj);
            obj = new JSONObject();
            obj.put("code", "beforeBreakfast");
            array.put(obj);
            obj = new JSONObject();
            obj.put("code", "afterDinner");
            array.put(obj);
            obj = new JSONObject();
            obj.put("code", "afterLunch");
            array.put(obj);
            obj = new JSONObject();
            obj.put("code", "beforeLunch");
            array.put(obj);
            obj = new JSONObject();
            obj.put("code", "beforeDinner");
            array.put(obj);
            obj = new JSONObject();
            obj.put("code", "afterBreakfast");
            array.put(obj);
            obj = new JSONObject();
            obj.put("code", "beforeSleep");
            array.put(obj);

            obj = new JSONObject();
            obj.put("code", "height");
            array.put(obj);
            obj = new JSONObject();
            obj.put("code", "weight");
            array.put(obj);

            obj = new JSONObject();
            obj.put("code", "hemoglobin");
            array.put(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array.toString();
    }

    public void setMemberId(String mid) {
        this.memberId = mid;
    }

    public int getCurrentIndex() {
        return pageIndex;
    }

    public void setCurrentInex(int pageIndex) {
        this.pageIndex = pageIndex;

    }

    public void setFromFragment(int from) {
        this.fromFragment = from;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_tendency;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        db = FinalDb.create(getApplicationContext(), ConfigParams.DB_NAME);
        mRoot.requestFocus();
        mRoot.setFocusableInTouchMode(true);
        init();
        requestTendencyPointList();
        registReceiver();
    }

    @Override
    public void onDestroyView() {
        unRegistReceiver();
        mRoot = null;
        super.onDestroyView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void init() {
        mBarView.setRightButton(R.drawable.ask_list_titlebat_right, this);
        vIndicator = (PageControlView) findViewById(R.id.pageindicator);// 页面指示器

        if (maps == null) {
            maps = new HashMap<String, ArrayList<TendencyInputModelItem>>();
        } else {
            maps.clear();
        }
        if (infos == null) {
            ArrayList<TendencyInputModel> list = (ArrayList<TendencyInputModel>) TendencyInputMrg.getInstance(getApplicationContext())
                    .getArrayDisplay().clone();
            list.remove(0);

            TendencyInputModel item = new TendencyInputModel();
            item.label = "睡前血糖";
            item.typeDisplay = 2;
            item.isFloat = true;
            list.add(0, item);
            item = new TendencyInputModel();
            item.label = "晚餐后2小时血糖";
            item.typeDisplay = 2;
            item.isFloat = true;
            list.add(0, item);
            item = new TendencyInputModel();
            item.label = "晚餐前血糖";
            item.typeDisplay = 2;
            item.isFloat = true;
            list.add(0, item);
            item = new TendencyInputModel();
            item.label = "午餐后2小时血糖";
            item.typeDisplay = 2;
            item.isFloat = true;
            list.add(0, item);
            item = new TendencyInputModel();
            item.label = "午餐前血糖";
            item.typeDisplay = 2;
            item.isFloat = true;
            list.add(0, item);
            item = new TendencyInputModel();
            item.label = "早餐后2小时血糖";
            item.typeDisplay = 2;
            item.isFloat = true;
            list.add(0, item);
            item = new TendencyInputModel();
            item.label = "空腹血糖";
            item.typeDisplay = 2;
            item.isFloat = true;
            list.add(0, item);
            item = new TendencyInputModel();
            item.label = "凌晨血糖";
            item.typeDisplay = 2;
            item.isFloat = true;
            list.add(0, item);
            infos = list;
        }

        if (mAdapter == null) {
            mAdapter = new MyPageAdapter();
        }

        mViewPage = (CustomViewpager) findViewById(R.id.vPager);
        mViewPage.setAdapter(mAdapter);
        vIndicator.init(infos.size(), R.drawable.tendencypoit1, R.drawable.tendencypoit3);
        vIndicator.generatePageControl(0);
        mViewPage.setOnPageChangeListener(this);
        mAdapter.notifyDataSetChanged();

        mViewPage.setCurrentItem(pageIndex);
        switch (pageIndex) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                mBarView.setTitle(getString(R.string.record_history_sugar));
                break;
            case 8:
                mBarView.setTitle(getString(R.string.record_history_blood));
                break;
            case 9:
                mBarView.setTitle(getString(R.string.record_history_bmi));
                break;
            case 10:
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
            case TitleBarView.ID_RIGHT_BUTTON:
                switch (pageIndex) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        toFragment(RecordSugarInputNewFrag.class, null, true);
                        break;
                    case 8:
                        toFragment(RecordPressBloodInputFragment.newInstance(null), true, true);
                        break;
                    case 9:
                        toFragment(RecordBmiInputFragment.newInstance(null), true, true);
                        break;
                    case 10:
                        toFragment(RecordHemoglobinFragment.newInstance(null), true, true);
                        break;
                    default:
                        break;
                }
                break;
            default:
                RecordMainFragment frag = RecordMainFragment.newInstance();
                frag.setCurrentInex(pageIndex);
                toFragment(frag, true, true);
                break;
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
        vIndicator.generatePageControl(arg0);
        switch (pageIndex) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                mBarView.setTitle(getString(R.string.record_history_sugar));
                break;
            case 8:
                mBarView.setTitle(getString(R.string.record_history_blood));
                break;
            case 9:
                mBarView.setTitle(getString(R.string.record_history_bmi));
                break;
            case 10:
                mBarView.setTitle(getString(R.string.record_history_ghb));
                break;
            default:
                break;
        }
    }

    public void requestTendencyPointList() {

        if (fromFragment == 0) {
            try {
                // ((MainActivity)
                // getActivity()).getIndexFragment().showHeadProgress();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showProgressDialog(getString(R.string.msg_loading));
        }

        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.TENDENCY_POINT_LIST);
        // http.setNeedGetCache(true,
        // UserMrg.getCacheKey(UrlMrg.TENDENCY_POINT_LIST));
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 24);
        String endDt = TimeUtil.fomateTime(today.getTimeInMillis(), "yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -3);
        String startDt = TimeUtil.fomateTime(cal.getTimeInMillis(), "yyyy-MM-dd HH:mm:ss");
        http.setPostValueForKey("startDt", startDt);
        if (!TextUtils.isEmpty(memberId)) {
            http.setPostValueForKey("memberId", memberId);
        }
        http.setPostValueForKey("endDt", endDt);
        http.setPostValueForKey("paramKey", createParamString());
        http.setCallBackAsyn(true);
        http.setOnHttpListener(1, this);
        http.startAsynchronous();

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

                        String time = "";
                        JSONObject obj = array.getJSONObject(i);
                        final String code = obj.optString("code");
                        JSONArray list = obj.getJSONArray("list");
                        int count = list.length();
                        for (int j = count - 1; j >= 0; j--) {
                            JSONObject o = list.getJSONObject(j);
                            TendencyPointInfo info = new TendencyPointInfo();
                            info.code = code.trim();
                            info.time = o.optString("time");
                            info.value = (float) o.optDouble("value");
                            info.type = o.optInt("type");
                            info.insertDt = o.optString("insertDt");
                            info.id = o.optString("paramLogId");
                            info.bloodGlucoseStatus = o.optInt("bloodGlucoseStatus");

                            if (!TextUtils.isEmpty(info.getTime())) {
                                info.time = info.getTime().substring(0, info.getTime().lastIndexOf(":"));
                            }

                            if (!time.equals(info.time)) {
                                infos.add(info);
                            }
                            time = info.time;

                        }

                    }

                    try {
                        db.deleteByWhere(TendencyPointInfo.class, "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    db.saveList(infos);

                } else {

                }
                mHandler.sendEmptyMessage(1);

            } else {
                ComveeHttpErrorControl.parseError(getActivity(), packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        paseTendencyPointList(what, b, fromCache);
    }

    @Override
    public void onFialed(int what, int errorCode) {
        try {
            if (fromFragment == 0) {
                // ((MainActivity)
                // getActivity()).getIndexFragment().hideHeadProgress();
            } else {
                cancelProgressDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 刷新一个分页
     *
     * @param index
     */
    public void onUpdate(int index) {
        try {
            if (mAdapter != null && infos != null) {
                onUpdate((TendencyMainPageView) mAdapter.getCacheView(index));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onUpdate(TendencyMainPageView view) {
        if (view != null) {
            view.updateData(oldTabIndex, fromFragment == 0 ? 4 : 10);
        }
    }

    public final void choiceTabUI(int index) {
        oldTabIndex = index;
        // onUpdate(pageIndex);
        updateAllPage();
    }

    public void updateAllPage() {
        if (null != infos) {
            for (int i = 0; i < infos.size(); i++) {
                onUpdate(i);
            }
        }
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
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConfigParams.ACTION_BLOOD);
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
        }

        @Override
        public View getView(int position) {
            TendencyMainPageView view = new TendencyMainPageView(getApplicationContext());
            TendencyInputModel info = infos.get(position);
            view.setTendencyInputModel(info);
            ArrayList<TendencyInputModelItem> items = null;
            if (maps.containsKey(info.label)) {
                items = maps.get(info.label);
            } else {
                if (info.label.equals("凌晨血糖")) {
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

            if (fromFragment == 0) {

                if ("血压".equals(info.label)) {
                    view.setLayoutId(R.layout.fragemnt_tendency_index_page1);
                } else {
                    view.setLayoutId(R.layout.fragemnt_tendency_index_page);
                }
            } else {
                if ("血压".equals(info.label)) {
                    view.setLayoutId(R.layout.fragemnt_tendency_main_page1);
                } else {
                    view.setLayoutId(R.layout.fragemnt_tendency_main_page);
                }
            }
            view.setTendencyInputModelItem(items);
            view.init();
            onUpdate(view);
            if (fromFragment == 0) {
                view.setId(position);
                view.setOnClickListener(RecordTendencyFragment.this);
            }

            return view;
        }

        @Override
        public int getCount() {
            return infos.size();
        }

    }

}
