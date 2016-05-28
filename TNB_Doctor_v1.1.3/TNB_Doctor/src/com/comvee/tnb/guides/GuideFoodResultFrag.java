package com.comvee.tnb.guides;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONObject;

public class GuideFoodResultFrag extends BaseFragment implements OnClickListener, OnHttpListener {
    private IndexTaskInfo indexTaskInfo;
    private GuideFoodRecipeInfo foodRecipeInfo;
    private int fromWhere;// 1
    private GuideBrowseInfo browseInfo;
    private TitleBarView mBarView;

    public GuideFoodResultFrag() {

    }

    public static GuideFoodResultFrag newInstance(IndexTaskInfo indexTaskInfo, GuideFoodRecipeInfo info) {
        GuideFoodResultFrag frag = new GuideFoodResultFrag();
        frag.setFoodRecipeInfo(info);
        frag.setIndexTaskInfo(indexTaskInfo);
        frag.setFromWhere(1);
        return frag;
    }

    public static GuideFoodResultFrag newInstance(IndexTaskInfo indexTaskInfo, GuideBrowseInfo info) {
        GuideFoodResultFrag frag = new GuideFoodResultFrag();
        frag.setBrowseInfo(info);
        frag.setIndexTaskInfo(indexTaskInfo);
        frag.setFromWhere(2);
        return frag;
    }

    @Override
    public int getViewLayoutId() {
        // TODO Auto-generated method stub
        return R.layout.guide_five_result_fragment;
    }

    public void setFromWhere(int fromWhere) {
        this.fromWhere = fromWhere;
    }

    public void setBrowseInfo(GuideBrowseInfo browseInfo) {
        this.browseInfo = browseInfo;
    }

    private void setIndexTaskInfo(IndexTaskInfo indexTaskInfo) {
        this.indexTaskInfo = indexTaskInfo;
    }

    private void setFoodRecipeInfo(GuideFoodRecipeInfo foodRecipeInfo) {
        this.foodRecipeInfo = foodRecipeInfo;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        init();
    }

    private void init() {
        ImageView img = (ImageView) findViewById(R.id.img_five_result);
        TextView tv_title = (TextView) findViewById(R.id.tv_food_result_title);
        TextView tv_desc = (TextView) findViewById(R.id.tv_food_result_desc);
        Button bt_1 = (Button) findViewById(R.id.btn_next_1);
        Button bt_2 = (Button) findViewById(R.id.btn_next_2);
        Button bt_3 = (Button) findViewById(R.id.btn_next_3);
        bt_1.setOnClickListener(this);
        bt_2.setOnClickListener(this);
        bt_3.setOnClickListener(this);
        switch (fromWhere) {
            case 1:
                mBarView.setTitle(foodRecipeInfo.getContent());
                if (foodRecipeInfo == null || foodRecipeInfo.equals("")) {
                    showToast(getResources().getString(R.string.error));
                    return;
                }
                tv_title.setText(foodRecipeInfo.getDesc());
                try {
                    JSONArray array = new JSONArray(foodRecipeInfo.getDesc1());
                    for (int i = 0; i < array.length(); i++) {
                        tv_desc.setText(array.getString(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                bt_3.setVisibility(View.GONE);
                break;
            case 2:
                findViewById(R.id.lin_button).setVisibility(View.GONE);
                bt_3.setVisibility(View.VISIBLE);
                mBarView.setTitle(browseInfo.getContent());
                tv_title.setText(browseInfo.getContent());
                img.setImageResource(R.drawable.wuguanka_03);
                try {
                    JSONArray array = new JSONArray(browseInfo.getDesc1());
                    for (int i = 0; i < array.length(); i++) {
                        tv_desc.setText(array.getString(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

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
            case R.id.btn_next_3:
                if (browseInfo.getLinktype() == 1 && browseInfo.getLinktask() != null && !browseInfo.getLinktask().equals("")) {
                    IndexTaskInfo mLinkInfo = null;
                    try {
                        mLinkInfo = DataParser.createIndexTaskInfo(new JSONObject(browseInfo.getLinktask()));
                        GuideMrg.getInstance().jumpGuide(this, mLinkInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.FINASH_FOOD_TASK);
        http.setPostValueForKey("choose", type + "");
        http.setPostValueForKey("task_code", indexTaskInfo.getTaskCode() + "");
        http.setOnHttpListener(1, this);
        http.startAsynchronous();

    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {

    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }

    @Override
    public boolean onBackPress() {
        if (!indexTaskInfo.isCanBackIndex()) {
            FragmentMrg.toBack(getActivity());

        } else {
            IndexFrag.toFragment(getActivity(),true); 
        }
        return true;
    }
}
