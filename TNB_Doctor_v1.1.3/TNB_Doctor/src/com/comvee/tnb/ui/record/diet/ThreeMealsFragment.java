package com.comvee.tnb.ui.record.diet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

import com.comvee.ThreadHandler;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.ui.record.diet.DietListDataHelper.PostFinishInterface;
import com.comvee.tnb.ui.xlistview.XListView.IXListViewListener;
/**
 * 我的三餐
 * @author Administrator
 *
 */
public class ThreeMealsFragment extends BaseFragment implements OnClickListener, PostFinishInterface {
	private XPinnedHeaderListView listview;
	private List<Diet> listviewDatas;
	private List<String> dateList;
	private HashMap<String, List<Diet>> itemHashMap;
	private DietRecordListViewAdapter dietRecordListViewAdapter;

	@Override
	public int getViewLayoutId() {
		return R.layout.my_threemeal_frag;
	}

	@Override
	public void onLaunch(Bundle dataBundle) {
		dateList = new ArrayList<String>();
		itemHashMap = new HashMap<String, List<Diet>>();
		dietRecordListViewAdapter = new DietRecordListViewAdapter(this, dateList, itemHashMap,false);
		dietRecordListViewAdapter.setOnClickListener(this);
		listview = (XPinnedHeaderListView) findViewById(R.id.xlistview);
		if (listviewDatas == null || listviewDatas.isEmpty()) {
			listviewDatas = new ArrayList<Diet>();
			showProgressDialog(getString(R.string.msg_loading));
			getRemoteData();
		}
		listview.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				ThreadHandler.postUiThread(new Runnable() {
		            @Override
		            public void run() {
		                listview.stopRefresh();   listview.stopLoadMore(); 
		            }
		        }, 2000);
		    

			}

			@Override
			public void onLoadMore() {
				ThreadHandler.postUiThread(new Runnable() {
		            @Override
		            public void run() {
		                listview.stopLoadMore();  listview.stopRefresh();
		            }
		        }, 2000);
		    
				//listview.showFootView();
				getRemoteData();

			}
		});
		listview.setPullRefreshEnable(true);
		listview.setPullLoadEnable(true);
		//listview.hideFootView();
		listview.setAdapter(dietRecordListViewAdapter);
	}

	private void getRemoteData() {
		DietListDataHelper historyRecordDataHelper = new DietListDataHelper(ConfigUrlMrg.THREE_MEAL_COLLECT, DietListDataHelper.POST_THREE_MEAL);
		historyRecordDataHelper.setPostFinishInterface(this);
		historyRecordDataHelper.getCollectDietData(dateList, itemHashMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		if (viewId == R.id.add_or_move_canteen) {
			showProgressDialog(getString(R.string.msg_loading));
			Diet cuDiet = (Diet) v.getTag(R.id.tag_first);
			List<Diet> listdiet = (List<Diet>) v.getTag(R.id.tag_second);
			String dateStr = (String) v.getTag(R.id.tag_three);

			DietListDataHelper dietListDataHelper;
			dietListDataHelper = new DietListDataHelper(ConfigUrlMrg.DIET_REMOVE_COLLECT, DietListDataHelper.POST_REMOVE_COLLECT);
			dietListDataHelper.setPostFinishInterface(this);
			dietListDataHelper.addOrRemoveCanteen(cuDiet);
			listdiet.remove(cuDiet);
			if (listdiet.size() == 0) {// 删除最后一条记录时要连头部一起删除
				dateList.remove(dateStr);
			}
			dietRecordListViewAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void postFinish(int status, Object obj, int tag) {
		cancelProgressDialog();
		if (tag == DietListDataHelper.POST_THREE_MEAL) {
			listview.setEmptyView(findViewById(R.id.emptyview));
			if (status == DietListDataHelper.LAST_ITEM) {
				//listview.hideFootView();
				return;
			}
			if (status != 0) {
				showToast(obj.toString());
				return;
			}
			listview.stopLoadMore();
			dietRecordListViewAdapter.notifyDataSetChanged();
		}
	}
}
