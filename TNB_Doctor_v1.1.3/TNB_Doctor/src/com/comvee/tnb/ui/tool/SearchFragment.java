package com.comvee.tnb.ui.tool;

import java.util.ArrayList;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.HeatListAdapter;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.model.FoodInfo;
import com.comvee.tnb.model.HeatInfo;
import com.comvee.tnb.ui.book.BookWebActivity;
import com.comvee.tnb.ui.record.diet.SwapDietFragment;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tnb.widget.pageview.PageViewControl;
import com.comvee.tool.UITool;
import com.comvee.util.BundleHelper;

public class SearchFragment extends BaseFragment implements OnItemClickListener, OnClickListener, DialogInterface.OnClickListener {
	private EditText editInput;
	private TextView textViewTip;
	private XListView listView;
	private HeatListAdapter mAdapter;
	private String foodName;
	private ImageView imageViewNodata;
	private LinearLayout lodinggroup;
	private TitleBarView mBarView;
	private String id;
	private String foodId;
	private int fromwhere = -1;
	private PageViewControl mControl;

	public SearchFragment() {
	}

	public static void toFragment(FragmentActivity fragment, String id, String foodId, int fromwhere) {
		Bundle bundle = new Bundle();
		bundle.putString("id", id);
		bundle.putString("foodid", foodId);
		bundle.putInt("fromwhere", fromwhere);
		FragmentMrg.toFragment(fragment, SearchFragment.class, bundle, false);
	}

	public static Fragment newInstance() {
		SearchFragment frag = new SearchFragment();
		return frag;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.food_search_frag;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		super.onLaunch(bundle);
		if (bundle != null) {
			id = bundle.getString("id");
			foodId = bundle.getString("foodid");
			fromwhere = bundle.getInt("fromwhere", -1);
		}
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		initTitleLayout();

		listView = (XListView) findViewById(R.id.list_view);
		listView.setOnItemClickListener(this);
		textViewTip = (TextView) findViewById(R.id.tv_of_search);
		imageViewNodata = (ImageView) findViewById(R.id.loadingImageView_of_search);
		lodinggroup = (LinearLayout) findViewById(R.id.lin_group_of_search);

		mAdapter = new HeatListAdapter();
		mControl = new PageViewControl(listView, HeatInfo.class, mAdapter, ConfigUrlMrg.HEAT_SECOND, new PageViewControl.onPageViewListenerAdapter() {
			@Override
			public void onStopLoading() {
				super.onStopLoading();
				cancelProgressDialog();
			}

			@Override
			public void onDataCallBack(int page, ArrayList listData) {
				super.onDataCallBack(page, listData);

			}
		});
		mControl.putPostValue("order", "desc");

		if (fromwhere == HeatFragment.FROM_SELECT_EAT_FOOD) {
			mControl.putPostValue("status", "1");
		} else if (fromwhere == HeatFragment.FROM_SELECT_EXCHANGE_FOOD) {
			mControl.putPostValue("status", "2");
			mControl.putPostValue("parentId", id);
		}

		editInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				mControl.putPostValue("name", editInput.getText().toString());
				mControl.loadRefresh();
			}
		});

	}

	private void initTitleLayout() {
		mBarView.setTitle(getString(R.string.title_food));
		editInput = (EditText) findViewById(R.id.search_et);
		findViewById(R.id.search).setOnClickListener(this);
		UITool.setEditWithClearButton(editInput, R.drawable.seach_clear);
		UITool.autoOpenInputMethod(mContext, editInput);
		editInput.setFocusable(true);
		editInput.setFocusableInTouchMode(true);
		editInput.requestFocus();
		editInput.setHint("请输入食材关键字");

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search:
			foodName = editInput.getText().toString().trim();
			if (TextUtils.isEmpty(foodName)) {
				showToast("请输入你要搜索的食材关键字");
				return;
			}
			showProgressDialog(getString(R.string.msg_loading));
			mAdapter.removeAllData();
			mAdapter.notifyDataSetChanged();
			UITool.closeInputMethodManager(getActivity());
			mControl.putPostValue("name", foodName);
			mControl.loadRefresh();

			break;
		case R.id.btn_back:
			getActivity().onBackPressed();
			break;
		}

	}

	private void notFindResoult() {
		listView.setVisibility(View.GONE);
		lodinggroup.setVisibility(View.VISIBLE);
		imageViewNodata.setImageResource(R.drawable.task_no_data);
		textViewTip.setText("没有找到相关食材哦~");
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	@Override
	public boolean onBackPress() {
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		HeatInfo info = mAdapter.getItem(position-1);
		if (fromwhere != -1) {
			if (info.heat == 0 || info.weight == 0) {
				showToast("数据错误，请重新选择");
				return;
			}
			FoodInfo info2 = new FoodInfo();
			info2.id = info.type;
			info2.heat = info.heat;
			info2.imgUrl = info.picurl;
			info2.name = info.name;
			info2.weight = info.weight;
			info2.eatAdvice = info.foodleveltext;
			info2.foodId = info.id;
			FragmentMrg.popBackToFragment(getActivity(), SwapDietFragment.class, BundleHelper.getBundleBySerializable(info2));
//			SwapDietFragment.toFragment(getActivity(), info2);
		} else {
			BookWebActivity.toWebActivity(getActivity(), BookWebActivity.FOOD, null, null, info.urlAddress, null);
		}
	}

	// lodinggroup.setVisibility(View.GONE);

}
