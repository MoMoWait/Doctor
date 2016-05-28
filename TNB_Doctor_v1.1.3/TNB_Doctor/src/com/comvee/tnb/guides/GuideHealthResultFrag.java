package com.comvee.tnb.guides;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.widget.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

public class GuideHealthResultFrag extends BaseFragment implements OnClickListener {

    private GuideResultInfo mInfo;
    private LinearLayout layoutList;
    private IndexTaskInfo indexTaskInfo;
    private TitleBarView mBarView;

    private GuideHealthResultFrag(GuideResultInfo mInfo) {
        this.mInfo = mInfo;
    }

    public GuideHealthResultFrag() {
    }

    public static GuideHealthResultFrag newInstance(GuideResultInfo mInfo) {
        return new GuideHealthResultFrag(mInfo);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_guides_result;
    }

    @Override
    public void onLaunch(Bundle bundle) {

        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);

        if (mInfo == null) {
            return;
        }
        Button iknow = (Button) findViewById(R.id.btn_iknown);
        iknow.setOnClickListener(this);
        iknow.setTag(null);
        layoutList = (LinearLayout) findViewById(R.id.layout_content);
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        TextView tvDesc = (TextView) findViewById(R.id.tv_desc);
        tvTitle.setText(mInfo.getDesc());
        tvDesc.setText(mInfo.getDesc1());
        addText(mInfo.getContent());
        try {
            indexTaskInfo = DataParser.createIndexTaskInfo(new JSONObject(mInfo.getNexttasks()));
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (indexTaskInfo == null) {
            showToast(getResources().getString(R.string.error));
            return;
        }
        if (indexTaskInfo.getTaskCode() > 0) {
            iknow.setText(getString(R.string.txt_food_sugest));
            iknow.setTag(indexTaskInfo);
        }
        if (mInfo.getTitleBar() != null && mInfo != null) {
            mBarView.setTitle(mInfo.getTitleBar());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_iknown:
                if (v.getTag() == null) {
                    String msg = ConfigParams.getGuideReadMsg(getApplicationContext());

                    if (msg == null) {
                        IndexFrag.toFragment(getActivity(),true); 

                    } else {
                        String str[] = msg.split("/");
                        if (str.length == 5) {
                            IndexTaskInfo info = new IndexTaskInfo();
                            info.setType(Integer.parseInt(str[0]));
                            info.setTaskCode(Integer.parseInt(str[1]));
                            info.setSeq(Integer.parseInt(str[2]));
                            info.setStatus(Integer.parseInt(str[3]));
                            info.setTotal(Integer.parseInt(str[4]));
                            GuideMrg.getInstance().jumpGuide(this, info);

                            ConfigParams.setGuideReadMsg(getApplicationContext(), null);
                        }
                    }
                } else {
                    IndexTaskInfo info = (IndexTaskInfo) v.getTag();
                    if (info != null) {
                        GuideMrg.getInstance().jumpGuide(this, info);
                    } else {
                        showToast(getResources().getString(R.string.error));
                    }
                }

                break;

            default:
                break;
        }

    }

    ;

    private void addText(String str) {
        if (str != null && !str.equals("")) {
            View view = View.inflate(getApplicationContext(), R.layout.guides_result_item, null);
            TextView tv = (TextView) view.findViewById(R.id.tv_text);
            tv.setText(str);
            layoutList.addView(view);
        }
    }

}
