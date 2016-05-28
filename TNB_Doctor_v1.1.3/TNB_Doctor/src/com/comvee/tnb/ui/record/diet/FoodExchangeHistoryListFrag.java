package com.comvee.tnb.ui.record.diet;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.FoodExchangeHistoryAdapter;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.model.FoodExchangeModel;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tnb.widget.pageview.PageViewControl;

/**
 * 交换历史
 * 
 * @author Administrator
 *
 */
public class FoodExchangeHistoryListFrag extends BaseFragment implements OnClickListener, OnItemClickListener {

	private XListView listview;
	private FoodExchangeHistoryAdapter mAdapter;
	private TitleBarView mBarView;
	private Class<? extends com.comvee.frame.BaseFragment> class1;
	private PageViewControl mControl;

	@Override
	public int getViewLayoutId() {
		return R.layout.food_exchange_history_list_frag;
	}

	public static void toFragment(FragmentActivity fragment, Class<? extends Fragment> class1) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("class", class1);
		FragmentMrg.toFragment(fragment, FoodExchangeHistoryListFrag.class, bundle, true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLaunch(Bundle dataBundle) {
		super.onLaunch(dataBundle);
		if (dataBundle != null) {
			class1 = (Class<? extends BaseFragment>) dataBundle.getSerializable("class");
		}

		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setTitle(getString(R.string.food_exchange_hirstory));
		mBarView.setLeftDefault(this);

		mAdapter = new FoodExchangeHistoryAdapter();

		listview = (XListView) findViewById(R.id.xlistview);
		listview.setEmptyView(findViewById(R.id.emptyview));
		listview.setOnItemClickListener(this);

		showProgressDialog(getString(R.string.msg_loading));
		mControl = new PageViewControl(listview, FoodExchangeModel.class, mAdapter, ConfigUrlMrg.FOOD_EXCHANGE_HISTORY,
				new PageViewControl.onPageViewListenerAdapter() {

					@Override
					public void onStopLoading() {
						cancelProgressDialog();
					}

					@Override
					public void onStartLoading() {

					}

					@Override
					public void onDataCallBack(int page, ArrayList listData) {

					}
				});
		mControl.load();

	}

	FoodExchangeModel deletemodel;

	@Override
	public void onFragmentResult(Bundle data) {
		// TODO Auto-generated method stub
		super.onFragmentResult(data);
		if (data != null) {
			deletemodel = (FoodExchangeModel) data.get("deletemodel");
			if (mAdapter != null && deletemodel != null) {
				mAdapter.remove(deletemodel);
				mAdapter.notifyDataSetChanged();
				if(mAdapter.getDatas() == null || mAdapter.getDatas().isEmpty()){
					mControl.clearCache();
					listview.setEmptyView(findViewById(R.id.emptyview));
				}



			}
		}
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		FoodExchangeModel info = (FoodExchangeModel) parent.getAdapter().getItem(position);
		info.isValid = 0;
		Bundle bundle = new Bundle();
		bundle.putSerializable("foodexchange", info);
		FragmentMrg.toFragment(getActivity(), SwapItemDetailFragment.class, bundle, true);
	}

	@Override
	public boolean onBackPress() {
		if (deletemodel != null) {
			FragmentMrg.popBackToFragment(getActivity(), RecordDietIndexFragment.class, null);
			return true;
		} else if (class1 != null) {
			FragmentMrg.popBackToFragment(getActivity(), class1, null);
			return true;
		} else {
			FragmentMrg.popBackToFragment(getActivity(), SwapDietFragment.class, new Bundle());
			return true;
		}
	}

}
