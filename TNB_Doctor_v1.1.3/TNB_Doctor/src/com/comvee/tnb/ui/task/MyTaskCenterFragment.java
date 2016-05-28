package com.comvee.tnb.ui.task;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.tnb.DrawerMrg;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.TaskAdapter;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.guides.DataParser;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.ComveeTask;
import com.comvee.tnb.model.MyTaskInfo;
import com.comvee.tnb.model.TaskItem;
import com.comvee.tnb.ui.assess.AssessFragment;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.task.CalendarFragment.OnChoiceCalendarListener;
import com.comvee.tnb.widget.Panel;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UserMrg;
import com.comvee.util.TimeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyTaskCenterFragment extends BaseFragment implements OnClickListener, OnItemClickListener, OnItemLongClickListener,
        OnChoiceCalendarListener {
    private Panel mPanel;
    private View layoutNonDefault;
    private View layoutCalendarNodata;
    private ListView listView;
    private TaskListAdapter mRemindAdapter;

    private ImageView btnSetType;
    private boolean status;// false、未完成true、完成
    private int typeTab;// 1、日历 0、列表
    private CalendarFragment fragCalendar;

    private TextView tvNonMsg;
    private TextView right, left;
    private View view;
    private boolean isSliding;
    private LinearLayout recomLin;
    private ListView recomListView;
    private ArrayList<TaskItem> listItems;
    private TaskAdapter adapter;
    private TitleBarView mBarView;
    private ArrayList<MyTaskInfo> mAllTaskList;

    public MyTaskCenterFragment() {
    }

    public static MyTaskCenterFragment newInstance(boolean isSliding) {
        MyTaskCenterFragment fragment = new MyTaskCenterFragment();
        fragment.setSliding(isSliding);
        return fragment;
    }

    private void setSliding(boolean isSliding) {
        this.isSliding = isSliding;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_remind_main;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        if (bundle != null) {
            isSliding = bundle.getBoolean("isSliding");
            if (isSliding) {
                DrawerMrg.getInstance().close();
            }
        }
        mBarView.setLeftDefault(this);
        initTitle();
        mBarView.setRightButton("任务中心", this);

        init();
        onChangeType(typeTab);
    }


    // 加载任务列表
    public void loadTaskList(final boolean status) {
        showProgressDialog(getString(R.string.msg_loading));
        new ComveeTask<ArrayList<MyTaskInfo>>() {
            @Override
            protected ArrayList<MyTaskInfo> doInBackground() {
                try {
                    String result = ComveeHttp.getCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TASK_MINE));
                    // if (listItems != null) {
                    // adapter.notifyDataSetChanged();
                    // }
                    // if (mAllTaskList != null && result != null) {
                    // return fileterTaskList(mAllTaskList, status);
                    // }
                    ComveePacket packet = null;
                    // 取缓存，如果缓存为空，取接口数据

                    if (result == null) {
                        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.TASK_MINE);
                        http.setPostValueForKey("type", "0");
                        result = http.startSyncRequestString();

                        packet = ComveePacket.fromJsonString(result);
                        if (packet.getResultCode() != 0) {
                            postError(packet);
                            return null;
                        }
                        ComveeHttp.setCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TASK_MINE), ConfigParams.CHACHE_TIME_LONG,
                                result);
                    }
                    packet = ComveePacket.fromJsonString(result);
                    boolean suggest = packet.optJSONObject("body").optInt("suggest") == 1;
                    if (suggest) {
                        parserRecomTaskList(packet);
                    }
                    mAllTaskList = DataParser.createTaskList(packet);
                    if (mAllTaskList != null && result != null) {
                        return fileterTaskList(mAllTaskList, status);
                    }
                    // return fileterTaskList(mAllTaskList, status);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<MyTaskInfo> result) {
                cancelProgressDialog();
                if (result == null || result.isEmpty()) {
                    if (typeTab == 0) {
                        adapter.setListItems(listItems);
                        adapter.notifyDataSetInvalidated();
                        showNoDataView();
                    } else {
                        recomLin.setVisibility(View.GONE);
                        layoutCalendarNodata.setVisibility(View.VISIBLE);
                        if (status) {
                            ((TextView) findViewById(R.id.tv_no_data)).setText(Html.fromHtml("您当前<font color="
                                    + getResources().getColor(R.color.theme_color_green) + ">没有已完成的任务</font>"));
                        } else {
                            ((TextView) findViewById(R.id.tv_no_data)).setText(Html.fromHtml("您当前<font color="
                                    + getResources().getColor(R.color.theme_color_green) + ">没有未完成的任务</font>"));

                        }
                    }
                    mRemindAdapter.setData(result);
                    mRemindAdapter.notifyDataSetChanged();
                    listView.setVisibility(View.GONE);
                    return;
                }

                layoutCalendarNodata.setVisibility(View.GONE);
                layoutNonDefault.setVisibility(View.GONE);
                findViewById(R.id.task_line).setVisibility(View.GONE);
                tvNonMsg.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                mRemindAdapter.setData(result);
                mRemindAdapter.notifyDataSetChanged();

                super.onPostExecute(result);
            }
        }.execute();

    }

    // 按状态过滤
    private ArrayList<MyTaskInfo> fileterTaskList(ArrayList<MyTaskInfo> all, boolean status) {
        ArrayList<MyTaskInfo> list = new ArrayList<MyTaskInfo>();
        for (MyTaskInfo info : all) {
            if (info.getStatus() == (status ? 0 : 1))
                list.add(info);
        }
        return list;

    }

    private void parserRecomTaskList(ComveePacket packet) {

        JSONArray array;
        try {
            array = packet.getJSONObject("body").getJSONArray("suggestList");
            int len = array.length();
            for (int i = 0; i < len; i++) {
                JSONObject obj = array.getJSONObject(i);
                TaskItem item = new TaskItem();
                item.setImgUrl(obj.optString("imgUrl"));
                item.setName(obj.optString("jobTitle"));
                item.setIsNew(obj.optInt("isNew"));
                item.setDetail(obj.optString("jobInfo"));
                item.setUse(obj.optString("gainNum"));
                item.setJobCfgId(obj.optString("jobCfgId"));
                item.setComment(obj.optString("commentNum"));
                item.setJobType(obj.optInt("jobType"));
                listItems.add(item);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onItemChoice(CalendarFragment frag, int position, Calendar cal) {
        final List<MyTaskInfo> list = getAlarmInfoByCalendar(cal, status);

        layoutCalendarNodata.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);

        // tvRemindCount.setText(String.format("共有%d条任务", list.size()));
        mRemindAdapter.setData(list);
        mRemindAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        return false;
    }

    private void initTitle() {
        view = View.inflate(getApplicationContext(), R.layout.titlebar_follow_record, null);
        left = (TextView) view.findViewById(R.id.tab_left);
        right = (TextView) view.findViewById(R.id.tab_right);
        left.setText("未完成");
        right.setText("已完成");
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        view.setLayoutParams(new LayoutParams(200, 3));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mBarView.addView(view, params);
        mBarView.setTitle("");
        choiceTabUI();
    }

    private void choiceTabUI() {
        left.setBackgroundResource(!status ? R.drawable.jiankangzixun_03 : R.drawable.jiankangzixun_07);
        right.setBackgroundResource(status ? R.drawable.jiankangzixun_08 : R.drawable.jiankangzixun_04);
        int green = getResources().getColor(R.color.theme_color_green);
        right.setTextColor(status ? Color.WHITE : green);
        left.setTextColor(!status ? Color.WHITE : green);

    }

    @Override
    public void onDestroyView() {
        mBarView.removeView(view);
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TitleBarView.ID_LEFT_BUTTON:
                DrawerMrg.getInstance().open();
                break;

            case R.id.btn_second_jump:
                toAssessFragment();
                break;
            case R.id.tab_left:
                this.status = false;
                choiceTabUI();

                loadTaskList(status);
                break;
            case R.id.tab_right:
                this.status = true;
                choiceTabUI();

                recomLin.setVisibility(View.GONE);
                loadTaskList(status);
                break;
            case R.id.btn_set_type:
                onChangeType(typeTab == 0 ? 1 : 0);
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                toFragment(TaskCenterFragment.newInstance(), true, true);
                break;
            case R.id.btn_non_jump:
                toFragment(TaskCenterFragment.newInstance(), true, true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        MyTaskInfo info = mRemindAdapter.getItem(position);
        TaskDetailFragment frag = TaskDetailFragment.newInstance();
        frag.setDoctorName(info.getDoctorName());
        frag.setTaskId(info.memberJobId);
        toFragment(frag, true, true);
    }

    private void init() {
        recomLin = (LinearLayout) findViewById(R.id.lin_recom_task_no_data);
        recomListView = (ListView) findViewById(R.id.lv_recom);
        listItems = new ArrayList<TaskItem>();
        recomListView.setOnItemClickListener(this);
        adapter = new TaskAdapter(getApplicationContext());
        recomListView.setAdapter(adapter);
        recomListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                TaskIntroduceFragment frag = TaskIntroduceFragment.newInstance();
                frag.setTaskInfo(adapter.getItem(arg2));
                frag.setRecommondList(listItems);
                frag.setRecommond(true);
                // if (doctorId != null) {
                // frag.setDoctorId(Long.parseLong(doctorId));
                // }
                toFragment(frag, true, true);
            }
        });
        layoutNonDefault = findViewById(R.id.lin_task_no_data_btn);
        layoutCalendarNodata = findViewById(R.id.layout_calendar_nodata);
        mRemindAdapter = new TaskListAdapter(getApplicationContext());
        tvNonMsg = (TextView) findViewById(R.id.task_no_data);
        final Button btnNonJump = (Button) findViewById(R.id.btn_non_jump);
        final Button btnSeconJump = (Button) findViewById(R.id.btn_second_jump);
        btnNonJump.setOnClickListener(this);
        btnSeconJump.setOnClickListener(this);
        mBarView.setTitle(getString(R.string.left_fragment_my_task));
        mPanel = (Panel) findViewById(R.id.slidingdrawer);
        mPanel.setOpen(true, true);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setAdapter(mRemindAdapter);
        listView.setVisibility(View.VISIBLE);

        btnSetType = (ImageView) findViewById(R.id.btn_set_type);
        btnSetType.setOnClickListener(this);

        // loadTaskList(status);

    }

    private void initCalendar() {
        fragCalendar = CalendarFragment.newInstance(0);
        fragCalendar.setOnChoiceCalendarListener(this);
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.layout_calendar_content, fragCalendar);
        tran.commit();
    }

    // 显示无数据的页面
    private void showNoDataView() {

        if (status) {// 已完成
            recomLin.setVisibility(View.GONE);
            tvNonMsg.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            layoutNonDefault.setVisibility(View.VISIBLE);
            findViewById(R.id.task_line).setVisibility(View.VISIBLE);
            tvNonMsg.setText(Html.fromHtml("您当前<font color=" + getResources().getColor(R.color.theme_color_green) + ">没有已完成的任务</font>"));
        } else {// 未完成
            tvNonMsg.setVisibility(View.GONE);
            layoutNonDefault.setVisibility(View.GONE);
            findViewById(R.id.task_line).setVisibility(View.GONE);
            recomLin.setVisibility(View.VISIBLE);
        }
    }

    /**
     * type 1、日历 0、列表
     *
     * @param type
     */
    private void onChangeType(int type) {
        List<MyTaskInfo> list = null;
        typeTab = type;
        // 列表
        if (0 == type) {
            layoutCalendarNodata.setVisibility(View.GONE);
            mPanel.setVisibility(View.GONE);
            loadTaskList(status);
            // 日历
        } else if (1 == type) {
            loadTaskList(status);

            // if (listItems != null) {// 任务中心
            // layoutCalendarNodata.setVisibility(View.VISIBLE);
            // listView.setVisibility(View.GONE);
            // } else {
            // listView.setVisibility(View.VISIBLE);
            // }
            if (list != null) {
                mRemindAdapter.setData(list);
                if (list.isEmpty()) {
                    layoutCalendarNodata.setVisibility(View.VISIBLE);

                } else {
                    layoutCalendarNodata.setVisibility(View.GONE);
                }
            }
            mRemindAdapter.notifyDataSetChanged();
            initCalendar();
            mPanel.setVisibility(View.VISIBLE);
            list = getAlarmInfoByCalendar(fragCalendar.getCalendar(), status);
            layoutNonDefault.setVisibility(View.GONE);
            findViewById(R.id.task_line).setVisibility(View.GONE);
            tvNonMsg.setVisibility(View.GONE);
            layoutNonDefault.setVisibility(View.GONE);
        }
        initTitle();
        btnSetType.setImageResource(type == 0 ? R.drawable.calendar_icon : R.drawable.list_icon);

    }

    private List<MyTaskInfo> getAlarmInfoByCalendar(Calendar cal, boolean status) {
        if (cal == null) {
            cal = Calendar.getInstance();
        }
        String time = TimeUtil.fomateTime(cal.getTimeInMillis(), "yyyy-MM-dd");
        ArrayList<MyTaskInfo> list = new ArrayList<MyTaskInfo>();
        if (mAllTaskList == null) {
            return null;
        }
        for (MyTaskInfo info : mAllTaskList) {
            if (info.getStatus() == (status ? 0 : 1)) {
                if (info.dateStr != null && info.dateStr.contains(time)) {
                    list.add(info);
                }
            }
        }
        return list;
    }

    private void toAssessFragment() {
        toFragment(AssessFragment.newInstance(), true, true);
    }

    @Override
    public boolean onBackPress() {
        if (isSliding) {
            IndexFrag.toFragment(getActivity(), true);
            return true;
        }
        return super.onBackPress();
    }

    @Override
    public void onMonthChange(CalendarFragment frag, Calendar cal) {

    }

    @Override
    public void onFragmentResult(Bundle data) {
        super.onFragmentResult(data);
       // choiceTabUI();
        initTitle();
        loadTaskList(status);
    }

    @Override
    public boolean isHashTask(CalendarFragment frag, Calendar cal) {
        String time = TimeUtil.fomateTime(cal.getTimeInMillis(), "yyyy-MM-dd");
        if (mAllTaskList == null) {
            return false;
        }
        for (MyTaskInfo info : mAllTaskList) {
            if (info.getStatus() == (status ? 0 : 1)) {
                if (info.dateStr != null && info.dateStr.contains(time)) {
                    return true;
                }
            }
        }
        return false;
    }

    class TaskListAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        private List<MyTaskInfo> mAlarmInfos = new ArrayList<MyTaskInfo>();

        public TaskListAdapter(Context context, ArrayList<MyTaskInfo> infos) {
            mAlarmInfos = infos;
            inflater = LayoutInflater.from(context);
        }

        public TaskListAdapter(Context context) {
            mAlarmInfos = new ArrayList<MyTaskInfo>();
            inflater = LayoutInflater.from(context);
        }

        public void setData(List<MyTaskInfo> infos) {
            mAlarmInfos = infos;
        }

        public int getCount() {
            return mAlarmInfos == null ? 0 : mAlarmInfos.size();
        }

        public int getItemViewType(int position) {
            return position;
        }

        public MyTaskInfo getItem(int arg0) {
            return mAlarmInfos.get(arg0);

        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_task, parent, false);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.tv_label);// 内容
                holder.time = (TextView) convertView.findViewById(R.id.tv_time);// 时间
                holder.decs = (TextView) convertView.findViewById(R.id.tv_stauts);// 剩余时间
                holder.icon = (ImageView) convertView.findViewById(R.id.img_photo);
                holder.tvPercent = (TextView) convertView.findViewById(R.id.tv_percent);
                holder.proPercent = (ProgressBar) convertView.findViewById(R.id.pro_percent);
                holder.doctor = (TextView) convertView.findViewById(R.id.tv_doctor);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {

                final MyTaskInfo remindInfo = mAlarmInfos.get(position);
                ImageLoaderUtil.getInstance(mContext).displayImage(remindInfo.getImgUrl(), holder.icon, ImageLoaderUtil.default_options);
                holder.name.setText(remindInfo.getJobTitle());
                if (!TextUtils.isEmpty(remindInfo.getDoctorName())) {
                    holder.doctor.setVisibility(View.VISIBLE);
                } else {
                    holder.doctor.setVisibility(View.GONE);
                }
                if (remindInfo.jobType == 10) {
                    holder.tvPercent.setVisibility(View.GONE);
                    holder.proPercent.setVisibility(View.GONE);
                    holder.decs.setVisibility(View.GONE);
                } else {
                    holder.tvPercent.setVisibility(View.VISIBLE);
                    holder.proPercent.setVisibility(View.VISIBLE);
                    holder.decs.setVisibility(View.VISIBLE);
                    holder.tvPercent.setText(remindInfo.doPercent + "%");
                    holder.proPercent.setMax(100);
                    holder.proPercent.setProgress(remindInfo.doPercent);
                    if (remindInfo.status == 0) {
                        holder.decs.setText(remindInfo.insertDt.split(" ")[0] + "~" + remindInfo.endDt.split(" ")[0]);
                    } else {
                        holder.decs.setText(String.format("总共%d天 还需%d天", remindInfo.totalNum, remindInfo.residue));
                    }
                }

                try {
                    String str = String.format("于%s日添加本任务", TimeUtil.fomateTime("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", remindInfo.getInsertDt()));

                    holder.time.setText(str);
                } catch (Exception E) {
                    E.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        class ViewHolder {
            public ImageView menu1;
            public ImageView menu2;
            public ImageView menu3;
            public LinearLayout layoutMenu;
            public View layoutConent;
            // public ImageView status;
            public ImageView icon;
            public TextView name;
            public TextView time;
            public TextView decs;
            public TextView tvPercent;
            public ProgressBar proPercent;
            public TextView doctor;
        }
    }
}
