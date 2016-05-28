package com.comvee.tnb.ui.privatedoctor;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.ThreadHandler;
import com.comvee.tnb.DrawerMrg;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.model.MemberDoctorInfo;
import com.comvee.tnb.network.MemberDoctorListLoader;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.ui.ask.DocListFragment;
import com.comvee.tnb.ui.follow.FollowListFragment;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.machine.BarCodeFragment;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.ui.xlistview.XListView.IXListViewListener;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tnb.widget.pageview.PageViewControl;

import java.util.ArrayList;

/**
 * 用户拥有的医生
 *
 * @author Administrator
 */
public class MemberDoctorListFrag extends BaseFragment implements OnClickListener, OnItemClickListener {
    private View view;
    private TextView left, right;
    private MemberDoctorAdapter mAdapter;
    private XListView listView;
    private TitleBarView mBarView;
    private boolean isSliding;
    private MemberDoctorListLoader loader;
    private View no_data;
    private int fromWhere;// 1表示从左侧栏跳转
    private PageViewControl mControl;

    public static void toFragment(FragmentActivity ragment, boolean isSliding) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isSliding", isSliding);
        boolean isAnima = true;
        if (isSliding) {
            isAnima = false;
        }
        FragmentMrg.toFragment(ragment, MemberDoctorListFrag.class, bundle, isAnima);
    }

    public static void toFragment(FragmentActivity ragment, boolean isSliding, int fromWhere) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isSliding", isSliding);
        bundle.putInt("from_where", fromWhere);
        boolean isAnima = true;
        if (isSliding) {
            isAnima = false;
        }
        FragmentMrg.toFragment(ragment, MemberDoctorListFrag.class, bundle, isAnima);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.member_doc_list_frag;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        super.onLaunch(bundle);
        if (bundle != null) {
            isSliding = bundle.getBoolean("isSliding");
            fromWhere = bundle.getInt("from_where");
        }
        if (isSliding) {
            DrawerMrg.getInstance().close();
        }
        init();
    }

    @Override
    public void onResume() {
        initTitleBar();
        if (mAdapter.getCount() == 0) {
            showProgressDialog(getString(R.string.loading));
            loadData();
        }
        super.onResume();
    }

    public void loadData() {

        ObjectLoader<MemberDoctorInfo> loader = new ObjectLoader<MemberDoctorInfo>();
        loader.loadArrayByBodylist(MemberDoctorInfo.class, ConfigUrlMrg.MEMBER_SERVER, loader.new CallBack() {
            @Override
            public void onBodyArraySuccess(boolean isFromCache, ArrayList<MemberDoctorInfo> obj) {
                super.onBodyArraySuccess(isFromCache, obj);
                cancelProgressDialog();
                mAdapter.setDatas(obj);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public boolean onFail(int code) {
                cancelProgressDialog();
                return super.onFail(code);
            }
        });

    }

    private void initTitleBar() {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        view = View.inflate(getApplicationContext(), R.layout.titlebar_follow_record, null);
        left = (TextView) view.findViewById(R.id.tab_left);
        left.setText("我的医生");
        left.setOnClickListener(this);
        left.setTextColor(Color.WHITE);
        left.setBackgroundResource(R.drawable.jiankangzixun_03);

        right = (TextView) view.findViewById(R.id.tab_right);
        right.setText("医生随访");
        right.setOnClickListener(this);

        right.setBackgroundResource(R.drawable.jiankangzixun_04);
        int green = getResources().getColor(R.color.theme_color_green);
        right.setTextColor(green);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mBarView.addView(view, params);
        mBarView.setTitle("");
        mBarView.setLeftButton(R.drawable.top_menu_back, this);
    }

    private void init() {
        no_data = findViewById(R.id.layout_no_data);
        listView = (XListView) findViewById(R.id.member_server_list_view);
        mAdapter = new MemberDoctorAdapter();
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
        listView.setPullRefreshEnable(false);
        listView.setPullLoadEnable(false);
        listView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
                ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.stopRefresh();
                        listView.stopLoadMore();
                    }
                }, 2000);

            }

            @Override
            public void onLoadMore() {
                ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.stopLoadMore();
                        listView.stopRefresh();
                    }
                }, 2000);

                loader.starLoader();
            }
        });
        findViewById(R.id.bt_buy_server).setOnClickListener(this);


    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case TitleBarView.ID_LEFT_BUTTON:
                getActivity().onBackPressed();
                break;
            case R.id.tab_right:
                toFragment(FollowListFragment.newInstance(1, true), false);
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                BarCodeFragment.toFragment(getActivity(), false, 2);// 绑定
                break;
            case R.id.bt_buy_server:
                DocListFragment.toFragment(getActivity(), DocListFragment.WHERE_PRIVATE_DOCTOR, null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        MemberDoctorInfo info = (MemberDoctorInfo) arg0.getAdapter().getItem(arg2);
        MemberServerListFrag.toFragment(getActivity(), info);
    }

    @Override
    public boolean onBackPress() {
        // ((MainActivity) getActivity()).showLeftView();
        // return true;
        if (fromWhere == 1) {
            IndexFrag.toFragment(getActivity(),true); 
            return true;
        } else
            return false;
    }
}
