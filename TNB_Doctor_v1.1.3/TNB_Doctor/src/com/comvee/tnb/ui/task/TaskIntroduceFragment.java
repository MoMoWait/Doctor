package com.comvee.tnb.ui.task;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.TaskItem;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UserMrg;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TaskIntroduceFragment extends BaseFragment implements OnCheckedChangeListener, OnClickListener, OnHttpListener {
    private ArrayList<TaskItem> listItems;
    private TaskItem mInfo;
    private Button btnDetail, btnMatter;
    private View addApplyTask;
    private TextView tvLable, tvApplyCount, tvdoctor, tvAddTask;
    private TextView tvInfo, tvSuggest;
    private View layout1, layout2;
    private boolean isRecommond;// 是否是推荐行动
    private long doctorId;
    private String doctorName;
    private ImageView ivPhoto;
    private View v;
    private TitleBarView mBarView;

    public TaskIntroduceFragment() {
    }

    public static TaskIntroduceFragment newInstance() {
        TaskIntroduceFragment fragment = new TaskIntroduceFragment();
        return fragment;
    }

    public void setRecommondList(ArrayList<TaskItem> listItems) {
        this.listItems = listItems;
    }

    public void setDoctorId(long doctorId) {
        this.doctorId = doctorId;

    }

    public void setRecommond(boolean isRecommond) {
        this.isRecommond = isRecommond;
    }

    public void setTaskInfo(TaskItem mInfo) {
        this.mInfo = mInfo;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_task_introduce;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        mRoot.setVisibility(View.GONE);
        mBarView.setTitle(getString(R.string.title_task_info));
        init();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void init() {

        ivPhoto = (ImageView) findViewById(R.id.img_photo);

        addApplyTask = findViewById(R.id.rel_add);
        btnDetail = (Button) findViewById(R.id.btn_detail);
        btnMatter = (Button) findViewById(R.id.btn_matter);
        tvAddTask = (TextView) findViewById(R.id.tv_addtask);
        tvLable = (TextView) findViewById(R.id.tv_label);
        tvApplyCount = (TextView) findViewById(R.id.tv_apply_count);
        tvInfo = (TextView) findViewById(R.id.tv_info);
        tvSuggest = (TextView) findViewById(R.id.tv_suggest);
        tvdoctor = (TextView) findViewById(R.id.tv_doctor);
        layout1 = findViewById(R.id.linearLayout1);
        layout2 = findViewById(R.id.linearLayout2);

        btnDetail.setOnClickListener(this);
        btnMatter.setOnClickListener(this);
        addApplyTask.setOnClickListener(this);

        requestIntroduce();
    }

    private void showTestDataDialog() {

        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setMessage("您目前是游客，无法进行该操作，建议您注册/登录掌控糖尿病，获得权限。");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toFragment(LoginFragment.class, null, true);
            }
        });
        builder.create().show();

    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {

        try {
            if (1 == what) {
                ComveePacket packet = ComveePacket.fromJsonString(b);
                if (packet.getResultCode() == 0) {
                    tvAddTask.setText("已加入");
                    addApplyTask.setEnabled(false);
                    addApplyTask.setBackgroundResource(R.drawable.rwxq_40);
                    tvAddTask.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    showToast("成功领取");
                    ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.INDEX));
                    ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TASK_CENTER));
                    ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TASK_MINE));
                    // toFragment(IndexFragment.newInstance(), false, true);
                    if (isRecommond) {
                        FragmentMrg.toBack(getActivity());
                        if (listItems != null) {
                            listItems.remove(mInfo);
                        }
                    } else {
                        toFragment(MyTaskCenterFragment.newInstance(false), true);
                    }
                } else {
                    ComveeHttpErrorControl.parseError(getActivity(), packet);
                }
            } else if (what == 2) {
                mInfo = new TaskItem();
                ComveePacket packet = ComveePacket.fromJsonString(b);
                if (packet.getResultCode() == 0) {
                    doctorName = packet.getJSONObject("body").optString("doctorName");
                    JSONObject obj = packet.getJSONObject("body").getJSONObject("job");
                    mInfo.setImgUrl(obj.optString("imgUrl"));
                    mInfo.setName(obj.optString("jobTitle"));
                    mInfo.setIsNew(obj.optInt("isNew"));
                    mInfo.setDetail(obj.optString("jobInfo"));
                    mInfo.setUse(obj.optString("gainNum"));
                    mInfo.setJobCfgId(obj.optString("jobCfgId"));
                    mInfo.setComment(obj.optString("commentNum"));
                    mInfo.setJobType(obj.optInt("jobType"));
                    tvLable.setText(mInfo.getName());
                    mInfo.setJobType(obj.optInt("jobType"));
                    tvApplyCount.setText(mInfo.getUse());
                    tvInfo.setText("\t\t" + obj.optString("jobInfo"));
                    tvSuggest.setText("\t\t" + obj.optString("doSuggest"));

                    ImageLoaderUtil.getInstance(mContext).displayImage(mInfo.getImgUrl(), ivPhoto, ImageLoaderUtil.default_options);
                    if (packet.getJSONObject("body").optBoolean("isAdd")) {
                        tvAddTask.setText("已加入");
                        addApplyTask.setEnabled(false);
                        addApplyTask.setBackgroundResource(R.drawable.rwxq_40);
                        tvAddTask.setTextColor(getResources().getColor(R.color.theme_color_green));
                        tvAddTask.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    } else {
                        tvAddTask.setTextColor(Color.parseColor("#30c29d"));
                        tvAddTask.setText("加入");
                    }

                    JSONArray array = packet.getJSONObject("body").getJSONArray("jobDetail");

                    final int[] resInfos = {R.id.tv_info1, R.id.tv_info2, R.id.tv_info3};
                    final int[] resDays = {R.id.tv_day1, R.id.tv_day2, R.id.tv_day3};
                    for (int i = 0; i < resInfos.length; i++) {
                        TextView tvDay = (TextView) findViewById(resDays[i]);
                        TextView tvInfo = (TextView) findViewById(resInfos[i]);

                        if (i >= array.length() - 1) {
                            tvDay.setVisibility(View.GONE);
                            tvInfo.setVisibility(View.GONE);
                        } else {
                            tvInfo.setText(array.getJSONObject(i).optString("detailInfo"));
                        }
                    }
                    mRoot.setVisibility(View.VISIBLE);
                } else {
                    ComveeHttpErrorControl.parseError(getActivity(), packet);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(doctorName)) {
            tvdoctor.setVisibility(View.VISIBLE);
            tvdoctor.setText(doctorName + "医生推荐");
        } else {
            tvdoctor.setVisibility(View.GONE);
        }
        cancelProgressDialog();
        v = findViewById(R.id.re_task);
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
        View view = findViewById(R.id.view_line);
        RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) view.getLayoutParams();
        params.width = 1;
        params.height = v.getMeasuredHeight();
        view.setLayoutParams(params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TitleBarView.ID_RIGHT_BUTTON:
                if (ConfigParams.IS_TEST_DATA) {
                    showTestDataDialog();
                } else {
                }
                break;
            case R.id.btn_detail:

                btnMatter.setTextColor(getResources().getColor(R.color.theme_color_green));
                btnDetail.setBackgroundResource(R.drawable.tab_solid_left);
                btnDetail.setTextColor(Color.WHITE);
                btnMatter.setBackgroundResource(R.drawable.tab_stroke_right);
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
                break;
            case R.id.btn_matter:
                btnMatter.setTextColor(Color.WHITE);
                btnDetail.setBackgroundResource(R.drawable.tab_stroke_left);

                btnDetail.setTextColor(getResources().getColor(R.color.theme_color_green));
                btnMatter.setBackgroundResource(R.drawable.tab_solid_right);
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                break;
            case R.id.rel_add:
                // if(ParamsConfig.IS_TEST_DATA && mInfo.getJobType()==2){
                // toFragment(LoginFragment.newInstance(), true, true);
                // }else{
                requesetApplyTask();
                // }
                break;
            default:
                break;
        }
    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();

        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }

    private void requestIntroduce() {
        showProgressDialog("正在加载...");
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.TASK_INTRODUCE);
        http.setPostValueForKey("jobId", mInfo.getJobCfgId());
        http.setPostValueForKey("doctorId", doctorId + "");
        http.setOnHttpListener(2, this);
        http.startAsynchronous();
    }

    /**
     * 领取任务
     */
    private void requesetApplyTask() {
        showProgressDialog("正在领取...");
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.TASK_APPLY);
        http.setPostValueForKey("jobId", mInfo.getJobCfgId());
        http.setPostValueForKey("doctorId", doctorId + "");
        http.setOnHttpListener(1, this);
        http.startAsynchronous();
    }

    @Override
    public boolean onBackPress() {
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    }
}
