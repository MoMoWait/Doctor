package com.comvee.tnb.radio;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.comvee.ThreadHandler;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.tnb.model.RadioGroup;
import com.comvee.tnb.model.RadioGroup.RadioAlbum;
import com.comvee.tnb.network.NetworkCallBack;
import com.comvee.tnb.network.RadioAlbumListLoader;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.ui.xlistview.XListView.IXListViewListener;
import com.comvee.tnb.view.RadioPlayView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.util.BundleHelper;
/**
 * 栏目列表（更多）
 * 
 * @author Administrator
 * 
 */
public class RadioAblumListFrag extends BaseFragment implements NetworkCallBack, OnClickListener, OnItemClickListener {

	private XListView mListView;
	private RadioAblumListAdapter mAdapter;
	private RadioAlbumListLoader mLoader;
	private RadioGroup group;
	private String mProgramSort;//1 降序 2 升序
	private RadioGroup.RadioAlbum tempAlbum;
	private TitleBarView mTitleView;

	@Override
	public int getViewLayoutId() {
		return R.layout.radio_ablumlist_frag;
	}

	@Override
	public void onLaunch(Bundle dataBundle) {
		super.onLaunch(dataBundle);
		group = BundleHelper.getObjecByBundle(RadioGroup.class, dataBundle);
		mProgramSort = group.relationProgramSort;
		mTitleView = (TitleBarView) findViewById(R.id.layout_top);
		mTitleView.setTitle(group.radioTypeName);
		mTitleView.setVisibility(View.VISIBLE);
		mTitleView.setRightButton(mProgramSort.equals("1")?"排序↑":"排序↓",this);
		mTitleView.setLeftDefault(this);
		mLoader = new RadioAlbumListLoader();
		mListView = (XListView) findViewById(R.id.list_view);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(xlistener);
		mListView.setOnItemClickListener(this);
		if (mAdapter == null) {
			mAdapter = new RadioAblumListAdapter();
			mLoader.load(group.sid,mProgramSort,RadioAblumListFrag.this);
		}
		mListView.setAdapter(mAdapter);
		RadioPlayView player = (RadioPlayView) findViewById(R.id.layout_play_bar);
		player.setListener(new RadioPlayView.PlayerViewListener() {
			@Override
			public void onToCurAlbum() {
				RadioPlayFrag.toFragment(getActivity(), RadioPlayerMrg.getInstance().getAlbum(), RadioPlayerMrg.getInstance().getCurrent());
			}

			@Override
			public void onStart(RadioAlbumItem item) {
			}

			@Override
			public void onPause(RadioAlbumItem item) {
			}
		});
	}

	private IXListViewListener xlistener = new IXListViewListener() {
		@Override
		public void onRefresh() {
			ThreadHandler.postUiThread(new Runnable() {
	            @Override
	            public void run() {
	            	mListView.stopRefresh();
	            	mListView.stopLoadMore();
	            }
	        }, 2000);
	    }
		@Override
		public void onLoadMore() {
			ThreadHandler.postUiThread(new Runnable() {
	            @Override
	            public void run() {
	            	mListView.stopRefresh();
	            	mListView.stopLoadMore();
	            }
	        }, 2000);
		}
	};

	@Override
	public void callBack(int what, int status, Object obj) {
		mListView.stopLoadMore();
		if (obj == null) {
			return;
		}
		mAdapter.addList((ArrayList<RadioAlbum>) obj);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case TitleBarView.ID_RIGHT_BUTTON:
				if (mProgramSort.equals("1")){//降序
					mProgramSort="2";
					mTitleView.setRightButton("排序↓",this);
				}else if (mProgramSort.equals("2")){//升序
					mProgramSort="1";
					mTitleView.setRightButton("排序↑",this);
				}
				mLoader.load(group.sid,mProgramSort,RadioAblumListFrag.this);
				mAdapter.notifyDataSetChanged();
				break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		RadioGroup.RadioAlbum items = mAdapter.getItem(position - 1);
		tempAlbum = items;
		RadioUtil.jumpFrag(getActivity(), items);
//		if (items.radioType == 1||items.radioType==0) {
//			if (items.programType == 1) {//专辑列表
//				toFragment(ProgrammeListFrag.class, BundleHelper.getBundleByObject(items), true);
//			} else if (items.programType == 2) {//单独节目
//				RadioAlbumItem item = RadioUtil.getProgramByRadioAlbum(items);
//				ArrayList<RadioAlbumItem> list = new ArrayList<RadioAlbumItem>();
//				list.add(item);
//				RadioPlayerMrg.getInstance().setDataSource(items, list);
//				RadioPlayerMrg.getInstance().play(0);
//				RadioPlayFrag.toFragment(getActivity(), items, item);
//			}
//		} else if (items.radioType == 3) {
//			RadioAdvanceFrag.toFragment(getActivity(), items);
//			// HuanxinMainFrag.toFragment(getActivity(),
//			// "1452043417916");
//		}
		
	}

	@Override
	public void onFragmentResult(Bundle data) {
		super.onFragmentResult(data);
		if (data != null) {
			try {
				tempAlbum.isCollect = data.getInt("collect");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
