package com.comvee.tnb.ui.pharmacy;

import java.util.ArrayList;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.ui.pharmacy.MedicinalListFragment.OnSelectDrugListener;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.ui.xlistview.XListView.IXListViewListener;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UITool;
import com.comvee.util.BundleHelper;

public class SearchFragment extends BaseFragment implements OnClickListener, OnItemClickListener, TextWatcher {
	private EditText titleEditTextView;
	private String loadStr = "";

	private XListView mListView;
	private MedicinalAdapter mAdapter;
	private ArrayList<MedicinalModel> listdatas = new ArrayList<MedicinalModel>();

	private LinearLayout lodinggroup;
	private ImageView imageView;
	private TextView textView;
	private OnSelectDrugListener onSelectDrugListener;
	private SearchMedicinalAsync searchMedicinalAsync;
	private TitleBarView mBarView;

	@Override
	public int getViewLayoutId() {
		return R.layout.medicinal_search_frag;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		setupView();
	}

	public void setOnSelectDrugListener(OnSelectDrugListener onSelectDrugListener) {
		this.onSelectDrugListener = onSelectDrugListener;
	}

	private void setupView() {
		View newTitleView = mBarView.resetLayout(R.layout.health_seach_titleview);
		newTitleView.findViewById(R.id.btn_back).setOnClickListener(this);
		titleEditTextView = (EditText) newTitleView.findViewById(R.id.search_et);
		newTitleView.findViewById(R.id.btn_top_right).setOnClickListener(this);
		UITool.setEditWithClearButton(titleEditTextView, R.drawable.seach_clear);
		UITool.autoOpenInputMethod(mContext, titleEditTextView);
		titleEditTextView.setFocusable(true);
		titleEditTextView.setFocusableInTouchMode(true);
		titleEditTextView.requestFocus();
		titleEditTextView.addTextChangedListener(this);
		lodinggroup = (LinearLayout) findViewById(R.id.lin_group_of_search);
		imageView = (ImageView) findViewById(R.id.loadingImageView_of_search);
		textView = (TextView) findViewById(R.id.tv_of_search);

		initListView();
		resetAsyncTask();
	}

	@Override
	public void onResume() {
		if (!TextUtils.isEmpty(loadStr) && titleEditTextView != null) {
			titleEditTextView.setText(loadStr);
		}
		super.onResume();
	}

	private void initListView() {
		mListView = (XListView) findViewById(R.id.list_view_of_search);
		mAdapter = new MedicinalAdapter(getActivity().getApplicationContext(), listdatas, R.layout.item_medicinal);
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(false);
		mListView.setAdapter(mAdapter);
		//mListView.hideFootView();
		mListView.setOnItemClickListener(this);
		mListView.setXListViewListener(new IXListViewListener() {
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
		            	mListView.stopLoadMore();
						mListView.stopRefresh();
		            }
		        }, 2000);
		    
				/*searchMedicinalAsync.cancel(true);
				resetAsyncTask();
				searchMedicinalAsync.execute(loadStr);*/
			}
		});

	}

	public static BaseFragment newInstance(OnSelectDrugListener listener) {
		SearchFragment frag = new SearchFragment();
		frag.setOnSelectDrugListener(listener);
		return frag;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			getActivity().onBackPressed();
			break;
		case R.id.btn_top_right:
			loadStr = titleEditTextView.getText() + "";
			if (TextUtils.isEmpty(loadStr)) {
				showToast("请输入您要搜索的关键字");
				return;
			}
			listdatas.clear();
			mListView.setPullLoadEnable(true);
			searchMedicinalAsync.cancel(true);
			resetAsyncTask();
			starSearch();
			searchMedicinalAsync.execute(loadStr);
			break;
		default:
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		MedicinalModel model = (MedicinalModel) arg0.getAdapter().getItem(arg2);
		if (onSelectDrugListener != null) {
			onSelectDrugListener.selectDrug(model);
		}
		FragmentMrg.toBack(getActivity(), BundleHelper.getBundleBySerializable(model));
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		searchMedicinalAsync.cancel(true);
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		loadStr = titleEditTextView.getText() + "";
		if (TextUtils.isEmpty(loadStr)) {
			return;
		}
		listdatas.clear();
		mListView.setPullLoadEnable(true);
		searchMedicinalAsync.cancel(true);
		resetAsyncTask();
		searchMedicinalAsync.execute(loadStr);
	}

	private void resetAsyncTask() {
		searchMedicinalAsync = new SearchMedicinalAsync(listdatas, mListView, mAdapter, getActivity(), this);
	}

	public void notFindResoult() {
		mListView.setVisibility(View.GONE);
		lodinggroup.setVisibility(View.VISIBLE);
		imageView.setImageResource(R.drawable.task_no_data);
		textView.setTextColor(getResources().getColor(R.color.text_color_1));
		textView.setText("暂无相关药品");
	}

	public void starSearch() {
		mListView.setVisibility(View.GONE);
		lodinggroup.setVisibility(View.VISIBLE);
		imageView.setImageResource(R.drawable.loading_anim);
		AnimationDrawable drawable = (AnimationDrawable) imageView.getDrawable();
		textView.setTextColor(getResources().getColor(R.color.text_color_3));
		textView.setText("正在努力搜索中...");
		drawable.start();
	}

	public void notifySearchList() {
		mListView.setVisibility(View.VISIBLE);
		lodinggroup.setVisibility(View.GONE);
	}
}
