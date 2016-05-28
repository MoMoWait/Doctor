package com.comvee.tnb.guides;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.widget.TitleBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GuideHealthFoodRecommendFrag extends BaseFragment implements OnClickListener {
    private GuideFoodInfo info;
    private IndexTaskInfo indexTaskInfo;
    private LinearLayout layoutList;
    private TitleBarView mBarView;

    public GuideHealthFoodRecommendFrag() {
    }

    public static GuideHealthFoodRecommendFrag newInstance(GuideFoodInfo info) {
        GuideHealthFoodRecommendFrag frag = new GuideHealthFoodRecommendFrag();
        frag.setInfo(info);
        return frag;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.foodrecommmend;
    }

    public void setInfo(GuideFoodInfo info) {
        this.info = info;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        init();
    }

    private void init() {

        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        TextView tvDesc = (TextView) findViewById(R.id.tv_desc);
        Button btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);
        try {
            indexTaskInfo = DataParser.createIndexTaskInfo(new JSONObject(info.getLinkTask()));
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (indexTaskInfo == null || info == null) {
            showToast(getResources().getString(R.string.error));
            return;
        }
        layoutList = (LinearLayout) findViewById(R.id.layout_conent);
        tvTitle.setText(info.getTitle());
        tvDesc.setText(info.getDesc());
        btnStart.setText(getString(R.string.txt_continue));
        String list = info.getList();
        JSONArray array;
        try {
            array = new JSONArray(list);
            for (int i = 0; i < array.length(); i++) {
                addText(array.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (indexTaskInfo.getTitle() != null && indexTaskInfo != null) {
            mBarView.setTitle(indexTaskInfo.getTitle());
        }
    }

    private void addText(String str) {
        View view = View.inflate(getApplicationContext(), R.layout.guides_result_item, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_text);
        tv.setText(str);
        layoutList.addView(view);
    }

    @Override
    public void onClick(View arg0) {
        if (indexTaskInfo != null) {
            if (arg0.getId() == R.id.btn_start) {
                GuideMrg.getInstance().jumpGuide(this, indexTaskInfo);
            }
        } else {
            showToast(getResources().getString(R.string.error));
        }
    }

    @Override
    public boolean onBackPress() {
        IndexFrag.toFragment(getActivity(),true); 
        return true;
    }

}
