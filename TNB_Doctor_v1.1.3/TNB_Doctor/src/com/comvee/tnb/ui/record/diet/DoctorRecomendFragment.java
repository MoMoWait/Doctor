package com.comvee.tnb.ui.record.diet;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.comvee.ThreadHandler;
import com.comvee.network.BackgroundTasks;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.common.MyBaseAdapter;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.model.FoodInfo;
import com.comvee.tnb.ui.record.diet.DoctorRecommendRequest.PostFinishInterface;
import com.comvee.tnb.widget.TitleBarView;

import java.util.ArrayList;
import java.util.List;

/**
 * 医生推荐饮食交换
 *
 * @author Administrator
 */
public class DoctorRecomendFragment extends BaseFragment implements OnClickListener, PostFinishInterface {
    private ListView listViewMorning, listViewNoon, listViewNight;
    private List<FoodInfo> moriningData, noonData, nightData;
    private MyBaseAdapter<FoodInfo> morningAdapter, noonAdapter, nightAdapter;
    private boolean needRePost;

    @Override
    public int getViewLayoutId() {
        return R.layout.doctor_recommend_frag;
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        TitleBarView mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        mBarView.setTitle("医生推荐食谱");
        moriningData = new ArrayList<FoodInfo>();
        noonData = new ArrayList<FoodInfo>();
        nightData = new ArrayList<FoodInfo>();
        needRePost = true;
        initlistView();
        if (needRePost) {
            ThreadHandler.postUiThread(new Runnable() {
                @Override
                public void run() {
                    getData();
                }
            }, 300);
            needRePost = false;
        }
    }

    private void getData() {
        showProgressDialog(getString(R.string.msg_loading));
        DoctorRecommendRequest doctorRecommendRequest = new DoctorRecommendRequest(ConfigUrlMrg.DIET_DOCTOR_REMOMMEND, this);
        doctorRecommendRequest.start();
        //BackgroundTasks.getInstance().push(BackgroundTasks.TASK_NETWORK, doctorRecommendRequest);
    }

    private void initlistView() {
        listViewMorning = (ListView) findViewById(R.id.listview_morning);
        listViewNoon = (ListView) findViewById(R.id.listview_noon);
        listViewNight = (ListView) findViewById(R.id.listview_night);
        morningAdapter = new DoctorListAdapter(getApplicationContext(), moriningData, R.layout.item_doctor_recommend_diet, this);
        noonAdapter = new DoctorListAdapter(getApplicationContext(), noonData, R.layout.item_doctor_recommend_diet, this);
        nightAdapter = new DoctorListAdapter(getApplicationContext(), nightData, R.layout.item_doctor_recommend_diet, this);
        listViewMorning.setAdapter(morningAdapter);
        listViewNoon.setAdapter(noonAdapter);
        listViewNight.setAdapter(nightAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.swap:
                FoodInfo foodInfo = (FoodInfo) v.getTag();
                SwapDietFragment.toFragment(getActivity(), foodInfo);
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void postFinish(int status, Object obj) {
        cancelProgressDialog();
        List<List<FoodInfo>> allData = (List<List<FoodInfo>>) obj;
        moriningData.addAll(allData.get(0));
        noonData.addAll(allData.get(1));
        nightData.addAll(allData.get(2));
        morningAdapter.notifyDataSetChanged();
        noonAdapter.notifyDataSetChanged();
        nightAdapter.notifyDataSetChanged();
    }
}
