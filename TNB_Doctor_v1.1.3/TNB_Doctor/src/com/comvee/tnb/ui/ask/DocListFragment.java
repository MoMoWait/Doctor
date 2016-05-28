package com.comvee.tnb.ui.ask;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.DoctorListAdapter;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.model.DoctorInfo;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.privatedoctor.DoctorServerList;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tnb.widget.pageview.PageViewControl;

import java.util.ArrayList;

/**
 * 医生列表界面
 *
 * @author 陈爱 邮箱：154477474@qq.com 2014-12-26
 */
public class DocListFragment extends BaseFragment implements OnClickListener, DialogInterface.OnClickListener, AdapterView.OnItemClickListener {
    public static int WHERE_PRIVATE_DOCTOR = 1;
    public static int WHERE_DOCLIST = 0;
    public static int WHERE_VOUCHER = 2;
    private XListView listView;
    /**
     * 0专家团队1私人医生2优惠券
     */
    private int fromWhere;
    private TitleBarView mBarView;
    private String couponId;
    private DoctorListAdapter mAdapter;
    private PageViewControl mControl;

    public DocListFragment() {

    }

    public static void toFragment(FragmentActivity fragment, int fromWhere, String couponId) {
        Bundle bundle = new Bundle();
        bundle.putString("couponId", couponId);
        bundle.putInt("fromWhere", fromWhere);
        FragmentMrg.toFragment(fragment, DocListFragment.class, bundle, true);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_server_apply;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        super.onLaunch(bundle);
        if (bundle != null) {
            fromWhere = bundle.getInt("fromWhere", 1);
            couponId = bundle.getString("couponId");
        }
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        mBarView.setTitle(getString(R.string.title_ask_doc));
        mBarView.setRightButton(R.drawable.jkzs_03, this);
        listView = (XListView) findViewById(R.id.list_view);
        listView.setPullRefreshEnable(true);
        listView.setPullLoadEnable(true);
        listView.setOnItemClickListener(this);

        mAdapter = new DoctorListAdapter();
        mControl = new PageViewControl(listView, DoctorInfo.class, mAdapter, ConfigUrlMrg.ASK_DOC_LIST, new PageViewControl.onPageViewListenerAdapter() {
            @Override
            public void onDataCallBack(int page, ArrayList listData) {
                super.onDataCallBack(page, listData);

            }

            @Override
            public void onStopLoading() {
                super.onStopLoading();
            }
        });
        mControl.setRowsString("list");
        mControl.setPageRowCount(5);
        mControl.load();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TitleBarView.ID_LEFT_BUTTON:
                IndexFrag.toFragment(getActivity(), true);
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                SearchFragment.toFragment(getActivity(), fromWhere);
                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    @Override
    public boolean onBackPress() {
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        DoctorInfo info = mAdapter.getItem(i-1);
        DoctorServerList.toFragment(getActivity(), info.USER_ID, info.if_doctor == 1);
    }
}
