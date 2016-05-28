package com.comvee.tnb.ui.record.diet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.ui.record.common.ImageItem4LocalImage;
import com.comvee.tnb.ui.record.common.MyBitmapFactory;
import com.comvee.tnb.ui.xlistview.XListView.IXListViewListener;
import com.comvee.tnb.widget.TitleBarView;
/**
 * 饮食历史记录
 * @author Administrator
 *
 */
public class HistoryDietFragment extends BaseFragment implements OnClickListener,
		com.comvee.tnb.ui.record.diet.DietListDataHelper.PostFinishInterface {
	private View buttonCreate;

	private XPinnedHeaderListView listview;
	private List<Diet> listviewDatas;

	public static boolean needReRequest = false;
	private TitleBarView mBarView;
	private List<String> dateList;
	private HashMap<String, List<Diet>> itemHashMap;
	private DietRecordListViewAdapter historyDietListViewAdapter;

	private Class<? extends BaseFragment> clazz;

	public static HistoryDietFragment newInstance() {
		HistoryDietFragment fragment = new HistoryDietFragment();
		return fragment;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.record_diet_input_fragment;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLaunch(Bundle dataBundle) {
		super.onLaunch(dataBundle);
		if (dataBundle != null) {
			clazz = (Class<? extends BaseFragment>) dataBundle.getSerializable("class");
		}
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setTitle("饮食记录");
		mBarView.setLeftDefault(this);
		buttonCreate = findViewById(R.id.create_diet);

		dateList = new ArrayList<String>();
		itemHashMap = new HashMap<String, List<Diet>>();
		historyDietListViewAdapter = new DietRecordListViewAdapter(this, dateList, itemHashMap);
		historyDietListViewAdapter.setOnClickListener(this);
		listview = (XPinnedHeaderListView) findViewById(R.id.xlistview);
		listview.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				ThreadHandler.postUiThread(new Runnable() {
		            @Override
		            public void run() {
		                listview.stopRefresh(); listview.stopLoadMore(); 
		            }
		        }, 2000);
		    
			}

			@Override
			public void onLoadMore() {
				ThreadHandler.postUiThread(new Runnable() {
		            @Override
		            public void run() {
		                listview.stopLoadMore(); listview.stopRefresh();
		            }
		        }, 2000);
		    
				//listview.showFootView();
				getRemoteData();

			}
		});
		listview.setPullRefreshEnable(true);
		listview.setPullLoadEnable(true);
		//listview.hideFootView();
		listview.setAdapter(historyDietListViewAdapter);
		if (listviewDatas == null || listviewDatas.isEmpty() || needReRequest) {
			needReRequest = false;
			listviewDatas = new ArrayList<Diet>();
			showProgressDialog(getString(R.string.msg_loading));
			getRemoteData();
		}
		buttonCreate.setOnClickListener(this);

	}

	private void getRemoteData() {
		DietListDataHelper historyRecordDataHelper = new DietListDataHelper(ConfigUrlMrg.DIET_LIST_NEW, DietListDataHelper.POST_HISTORY);
		historyRecordDataHelper.setPostFinishInterface(this);
		historyRecordDataHelper.getHistoryDietData(dateList, itemHashMap);

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mRoot = null;
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		if (v == buttonCreate) {
			Bundle bundle = new Bundle();
			int cHhour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			int mealType;
			if (cHhour > 5 && cHhour < 11) {// 早
				mealType = 1;
			} else if (cHhour >= 11 && cHhour < 17) {// 中
				mealType = 2;
			} else {// 晚
				mealType = 3;
			}
			bundle.putInt("mealTimeType", mealType);
			bundle.putString("from", "history");
			toFragment(CreateDietFragment.class, bundle, true);
		} else if (viewId == R.id.add_or_move_canteen) {
			showProgressDialog(getString(R.string.msg_loading));
			Diet cuDiet = (Diet) v.getTag(R.id.tag_first);
			DietListDataHelper dietListDataHelper;
			if (cuDiet.isCollect == 0) {
				dietListDataHelper = new DietListDataHelper(ConfigUrlMrg.DIET_COLLECT, DietListDataHelper.POST_COLLECT);
			} else {
				dietListDataHelper = new DietListDataHelper(ConfigUrlMrg.DIET_REMOVE_COLLECT, DietListDataHelper.POST_REMOVE_COLLECT);
			}
			dietListDataHelper.setPostFinishInterface(this);
			dietListDataHelper.addOrRemoveCanteen(cuDiet);
			cuDiet.isCollect = cuDiet.isCollect == 0 ? 1 : 0;
			historyDietListViewAdapter.notifyDataSetChanged();
		} else if (viewId == R.id.conver_view) {
			Diet currentDiet = (Diet) v.getTag();
			MyBitmapFactory.tempSelectBitmap.clear();
			for (int i = 0; i < currentDiet.netpics.size(); i++) {
				ImageItem4LocalImage imageItem = new ImageItem4LocalImage();
				imageItem.sourceImagePath = currentDiet.netpics.get(i).picBig;
				imageItem.smallImagePath = currentDiet.netpics.get(i).picSmall;
				MyBitmapFactory.tempSelectBitmap.add(imageItem);
			}
			Bundle bundle = new Bundle();
			bundle.putString("from", "history");
			bundle.putSerializable("currentDiet", currentDiet);
			toFragment(UpdateDietFragment.class, bundle, true);
		}
	}

	@Override
	public void postFinish(int status, Object obj, int tag) {
		cancelProgressDialog();
		switch (tag) {
		case DietListDataHelper.POST_HISTORY:
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
			historyDietListViewAdapter.notifyDataSetChanged();
			break;

		case DietListDataHelper.POST_COLLECT:

			if (status != 0) {
				showToast(obj.toString());
			} else {
			}
			break;

		case DietListDataHelper.POST_REMOVE_COLLECT:
			if (status != 0) {
				showToast(obj.toString());
			} else {
			}
			break;
		}

	}

	@Override
	public boolean onBackPress() {
		if (clazz != null) {
			FragmentMrg.popBackToFragment(getActivity(), clazz, null, true);
			return true;
		} else {
			return super.onBackPress();
		}
	}

	@Override
	public void onFragmentResult(Bundle data) {
		super.onFragmentResult(data);

//		if(data!=null){
//			getRemoteData();
//		}

	}
}