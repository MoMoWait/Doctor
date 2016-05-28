package com.comvee.tnb.ui.money;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.model.BalanceStatementInfo;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tnb.widget.pageview.PageViewControl;
import com.comvee.tool.ResUtil;

import java.util.ArrayList;

/**
 * 余额明细列表
 *
 * @author yujun
 */
public class BalanceStatementFragment extends BaseFragment {

    private PageViewControl mControl;
    private TitleBarView mTitlebar;
    private XListView mListView;
    private MoneyBalenceAdapter mAdapter;

    public static void toFragment(FragmentActivity fragment, int fromWhere, boolean isSliding) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isSliding", isSliding);
        bundle.putInt("from_where", 3);
        boolean isAnima = true;
        if (isSliding) {
            isAnima = false;
        }
        FragmentMrg.toFragment(fragment, BalanceStatementFragment.class, bundle, true);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.money_balance_statement_frgment;
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);
        initTitleBarView();
        mListView = (XListView) findViewById(R.id.xlistview);
        mListView.setVisibility(View.INVISIBLE);
        mListView.setEmptyView(findViewById(R.id.emptyview));
        controlList();
    }

    private void controlList() {
        mAdapter = new MoneyBalenceAdapter();
        PageViewControl.onPageViewListenerAdapter listener = new PageViewControl.onPageViewListenerAdapter() {
            @Override
            public void onDataCallBack(int page, ArrayList listData) {
                super.onDataCallBack(page, listData);
                mListView.setEmptyView(findViewById(R.id.nodata));
                mListView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopLoading() {
                super.onStopLoading();
                cancelProgressDialog();
            }
        };
        mControl = new PageViewControl(mListView, BalanceStatementInfo.class, mAdapter, ConfigUrlMrg.BALANCE_MONEY, listener);
        showProgressDialog(getString(R.string.msg_loading));
        mControl.load();
    }

    private void initTitleBarView() {
        mTitlebar = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mTitlebar.setTitle(ResUtil.getString(R.string.money_balance_statement));
        mTitlebar.setLeftDefault(this);
    }

}
