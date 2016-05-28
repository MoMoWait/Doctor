package com.comvee.tnb.ui.record.laboratory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.ui.exercise.SportRecord;
import com.comvee.tnb.ui.record.common.MyBitmapFactory;
import com.comvee.tnb.ui.record.common.NetPic;
import com.comvee.tnb.ui.record.common.ShowImageViewpagerActivity;
import com.comvee.tnb.ui.record.laboratory.LaboratoryListAdapter.OnCallback;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tnb.widget.pageview.PageViewControl;
import com.comvee.util.BundleHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 化验单
 */
@SuppressLint("ValidFragment")
public class RecordLaboratoryFragment extends BaseFragment implements OnClickListener{
    private View buttonCreate;
    private XListView mListview;
    private List<Laboratory> listviewDatas;
    private LaboratoryListAdapter mAdapter;
    private TitleBarView mBarView;
    private PageViewControl mDataControl;
    public RecordLaboratoryFragment() {
    }

    public static RecordLaboratoryFragment newInstance() {
        RecordLaboratoryFragment fragment = new RecordLaboratoryFragment();
        return fragment;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.record_laboratory_input_fragment;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        mBarView.setTitle(getString(R.string.title_report));
        mBarView.setRightButton(R.drawable.ask_list_titlebat_right, this);
        buttonCreate = findViewById(R.id.create);
        mListview = (XListView) findViewById(R.id.xlistview);
        mListview.setPullLoadEnable(true);
        mListview.setPullRefreshEnable(true);
        listviewDatas = new ArrayList<Laboratory>();
        mListview.setEmptyView(findViewById(R.id.emptyview));
        mAdapter = new LaboratoryListAdapter(mContext, R.layout.item_record_input_laboratory_listview);
        mAdapter.setOnCallBack(new OnCallback() {
            @Override
            public void onToPick(List<NetPic> datas, int position, List<ImageView> imageViews) {
                // 跳转到 图片详情页面
                Intent it = ShowImageViewpagerActivity.getIntent(getActivity(), datas, position, imageViews);
                startActivity(it);
            }
            @Override
            public void onToDetail(Laboratory laboratory) {
                // 跳转到化验单详情页面
                FragmentMrg.toFragment(getActivity(), UpdateLaboratoryFragment.class, BundleHelper.getBundleByObject(laboratory), true);
            }
        });
        // ///////////////初始化加载类////////////////////
        mDataControl = new PageViewControl(mListview, Laboratory.class, mAdapter, ConfigUrlMrg.LABORATOR_LIST,
                new PageViewControl.onPageViewListenerAdapter() {
                    @Override
                    public void onStopLoading() {
                        cancelProgressDialog();
                    }
                    @Override
                    public void onStartLoading() {
                        showProgressDialog(getString(R.string.msg_loading));
                    }
                    @Override
                    public void onDataCallBack(int page, ArrayList listData) {
                        super.onDataCallBack(page, listData);
                        mListview.setVisibility(View.VISIBLE);

                    }

                });
        mDataControl.putPostValue("sort", "insert_dt");
        mDataControl.putPostValue("order", "desc");
        mDataControl.load();
        // ///////////////初始化加载类////////////////////

        buttonCreate.setOnClickListener(this);
        if (listviewDatas != null && listviewDatas.size() > 0 && findViewById(R.id.bottom) != null) {
            findViewById(R.id.bottom).setVisibility(View.GONE);
        }

    }


    @Override
    public void onFragmentResult(Bundle data) {
        super.onFragmentResult(data);

        if (data != null) {
            if ("delete".equals(data.get("opt"))) {// 删除 一条
                Laboratory laboratory = BundleHelper.getObjecByBundle(Laboratory.class, data);
                mAdapter.remove(laboratory);

                if(null!=mDataControl)
                mDataControl.clearCache();
                if(mAdapter.getDatas()==null||mAdapter.getDatas().isEmpty()){
                    mListview.setEmptyView(findViewById(R.id.emptyview));
                }

            } else if ("update".equals(data.get("opt"))) {
                mDataControl.loadRefresh();
            } else {// 如果返回回来有数据，就刷新列表
                mDataControl.loadRefresh();
            }
        }else{
            mDataControl.loadRefresh();
        }

        mAdapter.notifyDataSetChanged();
        mListview.setPullLoadEnable(true);
        mListview.setPullRefreshEnable(true);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonCreate || v.getId() == TitleBarView.ID_RIGHT_BUTTON) {
            MyBitmapFactory.clearAll();
            toFragment(new CreateLaboratoryFragment(), true, true);
        }
    }
}
