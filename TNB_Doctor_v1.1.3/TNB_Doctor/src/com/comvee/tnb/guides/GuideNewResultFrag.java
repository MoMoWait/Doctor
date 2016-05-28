package com.comvee.tnb.guides;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.widget.TitleBarView;

public class GuideNewResultFrag extends BaseFragment implements OnClickListener, OnHttpListener {
    private GuideResultInfo guideResultInfo;
    private IndexTaskInfo indexTaskInfo;
    private TitleBarView mBarView;

    public GuideNewResultFrag() {
    }

    public static GuideNewResultFrag newInstance(IndexTaskInfo indexTaskInfo, GuideResultInfo info) {
        GuideNewResultFrag frag = new GuideNewResultFrag();
        frag.setIndexTaskInfo(indexTaskInfo);
        frag.setGuideResultInfo(info);
        return frag;
    }

    private void setGuideResultInfo(GuideResultInfo guideResultInfo) {
        this.guideResultInfo = guideResultInfo;
    }

    private void setIndexTaskInfo(IndexTaskInfo indexTaskInfo) {
        this.indexTaskInfo = indexTaskInfo;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.new_result_fragment;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        mBarView.setTitle(getString(R.string.title_guide_result));
    }

    private void init() {
        if (guideResultInfo == null) {
            showToast(getResources().getString(R.string.error));
            return;
        }
        ((TextView) findViewById(R.id.tv_new_result_desc)).setText(guideResultInfo.getContent());
        // JSONArray array;
        // try {
        // array = new JSONArray(foodRecipeInfo.getDesc1());
        // for (int i = 0; i < array.length(); i++) {
        // ((TextView)
        // findViewById(R.id.tv_food_result_desc)).setText(array.getString(i));
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        findViewById(R.id.btn_next_1).setOnClickListener(this);
        findViewById(R.id.btn_next_2).setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_next_1:
                requesFinashFood(2);
                if (!indexTaskInfo.isCanBackIndex()) {
                    FragmentMrg.toBack(getActivity());

                } else {
                    IndexFrag.toFragment(getActivity(),true); 
                }
                break;
            case R.id.btn_next_2:
                requesFinashFood(1);
                if (!indexTaskInfo.isCanBackIndex()) {
                    FragmentMrg.toBack(getActivity());

                } else {
                    IndexFrag.toFragment(getActivity(),true); 
                }
                break;
            default:
                break;
        }
    }

    private void requesFinashFood(int type) {
        if (indexTaskInfo.getStatus() == 1) {
            return;
        }
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.FINASH_FOOD_REMIDE);
        http.setPostValueForKey("task_code", indexTaskInfo.getTaskCode() + "");
        http.setPostValueForKey("choose", type + "");
        http.setOnHttpListener(1, this);
        http.startAsynchronous();
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFialed(int what, int errorCode) {
        // TODO Auto-generated method stub
        cancelProgressDialog();
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }
}
