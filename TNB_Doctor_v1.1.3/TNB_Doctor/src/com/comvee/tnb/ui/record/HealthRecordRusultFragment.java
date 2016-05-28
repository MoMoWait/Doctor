package com.comvee.tnb.ui.record;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.guides.GuideMrg;
import com.comvee.tnb.guides.IndexTaskInfo;
import com.comvee.tnb.model.HealthResultInfo;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.record.diet.RecordDietIndexFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.AppUtil;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.util.BundleHelper;

import java.util.ArrayList;

public class HealthRecordRusultFragment extends BaseFragment implements OnClickListener {
    private LinearLayout mRootList;
    private HealthResultInfo info;
    private int fromwhere;// 1 从记录血糖 2 从血糖仪推送 3 从饮食记录
    private TitleBarView mBarView;

    public static void toFragment(FragmentActivity act, HealthResultInfo info, int fromWhere) {
        Bundle bundle = BundleHelper.getBundleBySerializable(info);
        FragmentMrg.toFragment(act, HealthRecordRusultFragment.class, bundle, true);
    }


    @Override
    public int getViewLayoutId() {
        return R.layout.health_record_result_frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);

        info = BundleHelper.getSerializableByBundle(dataBundle);
        fromwhere = dataBundle.getInt("fromWhere");

        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        init();

        mBarView.setTitle(getString(R.string.title_sugar_detail));
        if (info.list != null && info.list.taskInfo.size() > 0 && info.bloodGlucoseStatus != 3) {
            mRootList.removeAllViews();
            mRootList.setVisibility(View.VISIBLE);
            inflateGuideItems(info.list.taskInfo);
        }
    }

    private void init() {

        mBarView.setRightButton(getString(R.string.history), this);
        ImageView img = (ImageView) findViewById(R.id.img_health_result);
        mRootList = (LinearLayout) findViewById(R.id.list_view);
        TextView title = (TextView) findViewById(R.id.tv_health_result_title);
        TextView desc = (TextView) findViewById(R.id.tv_health_result_desc);
        ImageLoaderUtil.getInstance(getApplicationContext()).displayImage(info.img, img, ImageLoaderUtil.default_options);
        int star = info.title.indexOf(info.key);
        int end = star + info.key.length();
        String color = null;
        switch (info.bloodGlucoseStatus) {
            case 1:
            case 2:
                color = "#005ebe";
                break;
            case 3:
                color = "#4eb800";
                break;
            case 4:
            case 5:
                color = "#ff3b30";
                break;
            default:
                color = "#333333";
                break;
        }
        // title.setText(Html.fromHtml(titles));
        title.setText(AppUtil.setTextEffect(info.title, star, end, color));
        desc.setText(AppUtil.ToDBC(info.desc));
        if (fromwhere == 2) {
            mBarView.setVisibility(View.GONE);
            ImageView view = (ImageView) findViewById(R.id.btn_close);
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(this);
            TextView head = (TextView) findViewById(R.id.tv_health_result_head);
            head.setText(info.head);
            head.setVisibility(View.VISIBLE);
        }
    }

    // 加载指导列表的item
    protected void inflateGuideItems(ArrayList<HealthResultInfo.List.TaskInfo> list) {
        if (list.isEmpty()) {
            return;
        }
        View.inflate(getApplicationContext(), R.layout.item_index_group_line, mRootList);
        TextView tvGroup = (TextView) View.inflate(getApplicationContext(), R.layout.item_index_group, null).findViewById(R.id.tv_group_name);

        tvGroup.setText("糖糖小贴士");
        mRootList.addView(tvGroup, -1, -2);

        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                View.inflate(getApplicationContext(), R.layout.item_index_item_line, mRootList);
            } else {
                View.inflate(getApplicationContext(), R.layout.item_index_group_line, mRootList);
            }

            HealthResultInfo.List.TaskInfo info = list.get(i);
            View itemView = View.inflate(getApplicationContext(), R.layout.item_index_item, null);
            View btnItem = itemView.findViewById(R.id.btn_index_item);
            btnItem.setOnClickListener(this);
            btnItem.setTag(info);
            ImageView ivIcon = (ImageView) itemView.findViewById(R.id.iv_index_icon);
            TextView tvLabel = (TextView) itemView.findViewById(R.id.tv_index_label);
            TextView tvDecs = (TextView) itemView.findViewById(R.id.tv_index_decs);

            ImageView tvComplete = (ImageView) itemView.findViewById(R.id.iv_complete);
            if (info.taskStatus == 1) {
                tvComplete.setVisibility(View.VISIBLE);
            }

            AppUtil.loadImageByLocationAndNet(ivIcon, info.taskIcon);
            tvLabel.setText(info.title);
            tvDecs.setText(info.subTitle);

            mRootList.addView(itemView, -1, -2);

        }
        View.inflate(getApplicationContext(), R.layout.item_index_group_line, mRootList);
    }


    private IndexTaskInfo getIndexTaskInfo(HealthResultInfo.List.TaskInfo info) {

        IndexTaskInfo result = new IndexTaskInfo();
        result.setStatus(info.taskStatus);
        result.setSubtitle(info.subTitle);
        result.setId(info.taskID);
        result.setTaskCode(info.taskCode);
        result.setIcon(info.taskIcon);
        result.setIsNew(info.taskNew);
        result.setRelation(info.subTitle);
        result.setTotal(info.total);
        result.setType(info.type);
        result.setTitle(info.title);
        result.setSeq(info.taskSeq);
        result.setTaskTime(info.taskTime);

        return result;

    }


    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_index_item:
                if (arg0.getTag() instanceof HealthResultInfo.List.TaskInfo) { // 指导item的点击事件

                    IndexTaskInfo guideItem = getIndexTaskInfo((HealthResultInfo.List.TaskInfo) arg0.getTag());
                    guideItem.setCanBackIndex(false);
                    if (guideItem.getType() == IndexTaskInfo.JUMP_SPORTS_VALUE_HTML || guideItem.getType() == IndexTaskInfo.JUMP_BROWSE
                            || guideItem.getType() == IndexTaskInfo.JUMP_NEW_SPORTS_VALUE_1 || guideItem.getType() == IndexTaskInfo.JUMP_NEW_SPORTS_VALUE) {
                        guideItem.setTempStatu(1);
                    }
                    GuideMrg.getInstance().jumpGuide(this, guideItem);

                }
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                toFragment(RecordMainFragment.newInstance(true, 0), true, true);
                break;
            case R.id.btn_close:
                IndexFrag.toFragment(getActivity(),true); 
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onBackPress() {

        if(FragmentMrg.isContain(RecordDietIndexFragment.class)){
            FragmentMrg.popBackToFragment(getActivity(), RecordDietIndexFragment.class, new Bundle());
        }/*else if(FragmentMrg.isContain(RecordMainFragment.class)){
            FragmentMrg.popBackToFragment(getActivity(), RecordMainFragment.class, new Bundle());
        }*/else {
            IndexFrag.toFragment(getActivity(),true);
        }
        return true;
    }

}
