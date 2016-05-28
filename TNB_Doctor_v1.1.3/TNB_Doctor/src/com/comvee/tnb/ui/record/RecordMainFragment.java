package com.comvee.tnb.ui.record;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.model.TendencyInputModel;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.widget.TitleBarView;

/**
 * 趋势图主界面（支配 趋势图 和 列表）
 * 
 * @author friendlove
 * 
 */
@SuppressLint({ "ValidFragment", "ResourceAsColor" })
public class RecordMainFragment extends BaseFragment implements IndexPageable, OnClickListener {

	private TextView btnListOrImg;
	private ViewGroup layoutTag;
	private int oldTabIndex = 1;
	private RecordTendencyFragment fragmenTendency;
	private RecordTableFragment fragmentList;
	private boolean isList;
	private int pageIndex;
	private String memberId;
	private int backToWhere;
	public static int BACK_RECORD_CHOOSE_FRG = 1;

	public void setMemberId(String mid) {
		memberId = mid;
	}

	public void setBackToWhere(int backToWhere) {
		this.backToWhere = backToWhere;
	}

	public RecordMainFragment() {
	}

	public void setList(boolean isList) {
		this.isList = isList;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public static RecordMainFragment newInstance(boolean isList, int index, int backToWhere) {
		RecordMainFragment fragment = new RecordMainFragment();
		fragment.setList(isList);
		fragment.setPageIndex(index);
		fragment.setBackToWhere(backToWhere);
		return fragment;
	}

	public static RecordMainFragment newInstance(boolean isList, int index) {
		RecordMainFragment fragment = new RecordMainFragment();
		fragment.setList(isList);
		fragment.setPageIndex(index);
		return fragment;
	}

	public static RecordMainFragment newInstance(boolean isList) {
		RecordMainFragment fragment = new RecordMainFragment();
		fragment.setList(isList);
		fragment.setPageIndex(0);
		return fragment;
	}

	public static RecordMainFragment newInstance() {
		RecordMainFragment fragment = new RecordMainFragment();
		fragment.setList(true);
		fragment.setPageIndex(0);
		return fragment;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragemnt_tendency_main;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onLaunch(Bundle bundle) {
		RecordIntputModel.initInputModel();
		init();

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	private void init() {
		btnListOrImg = (TextView) findViewById(R.id.btn_left);// 切换曲线图/列表

		layoutTag = (ViewGroup) findViewById(R.id.layout_tab);// 天、周、月 选项卡

		if (isList) {
			toTendencyList();
		} else {
			toTendencyImg();
		}

		btnListOrImg.setOnClickListener(this);
		initTabLayout();

		choiceTabUI(oldTabIndex);

	}

	public void setOldTabIndex(int oldTabIndex) {
		if (oldTabIndex == 0 || oldTabIndex > 3) {
			this.oldTabIndex = 1;
		} else {
			this.oldTabIndex = oldTabIndex;
		}
	}

	/**
	 * 初始化选项卡UI (循环绑定tab点击事件)
	 */
	private final void initTabLayout() {
		View.OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				int index = (Integer) v.getTag();
				if (index != oldTabIndex) {
					choiceTabUI(index);
				}
			}
		};
		final ViewGroup layout = layoutTag;
		int size = layout.getChildCount();
		for (int i = 0; i < size; i++) {
			View item = layout.getChildAt(i);
			item.setTag(i);
			item.setOnClickListener(listener);
		}
	}

	private final void choiceTabUI(int index) {
		final TextView oldView = (TextView) layoutTag.getChildAt(oldTabIndex);
		final TextView choiceView = (TextView) layoutTag.getChildAt(index);
		oldView.setTextColor(getResources().getColor(R.color.theme_color_green));
		choiceView.setTextColor(getResources().getColor(R.color.white));
		switch (index) {
		case 1:
			layoutTag.getChildAt(1).setBackgroundResource(R.drawable.tab_solid_left);
			layoutTag.getChildAt(2).setBackgroundResource(R.drawable.tendecy_center);
			layoutTag.getChildAt(3).setBackgroundResource(R.drawable.tendecy_right);
			break;
		case 2:
			layoutTag.getChildAt(1).setBackgroundResource(R.drawable.tendecy_left);
			layoutTag.getChildAt(2).setBackgroundColor(getResources().getColor(R.color.theme_color_green));
			layoutTag.getChildAt(3).setBackgroundResource(R.drawable.tendecy_right);
			break;
		case 3:
			layoutTag.getChildAt(1).setBackgroundResource(R.drawable.tendecy_left);
			layoutTag.getChildAt(2).setBackgroundResource(R.drawable.tendecy_center);
			layoutTag.getChildAt(3).setBackgroundResource(R.drawable.tab_solid_right);
			break;
		default:
			break;
		}
		onChoiceTab(index);
		oldTabIndex = index;
		if (!isList) {
			fragmenTendency.choiceTabUI(index);
		} else {
			fragmentList.choiceTabUI(index);
		}
	}

	/**
	 * 选择选项卡要做的事。、、、、
	 * 
	 * @param index
	 */
	private final void onChoiceTab(int index) {

	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case TitleBarView.ID_RIGHT_BUTTON:
			toInputTendencyData();
			break;
		case R.id.btn_left:
			if (isList) {
				pageIndex = null == fragmentList ? pageIndex : (fragmentList.getCurrentIndex() == 0 ? 0 : fragmentList.getCurrentIndex() + 7);
				toTendencyImg();
				fragmenTendency.setCurrentInex(pageIndex);
			} else {
				pageIndex = fragmenTendency.getCurrentIndex() < 7 ? 0 : fragmenTendency.getCurrentIndex() - 7;
				toTendencyList();
				fragmentList.setCurrentInex(pageIndex);
			}
			break;
		default:
			break;
		}

	}

	/**
	 * 进入输入数据界面
	 */
	public void toInputTendencyData() {
		int index = 0;
		// TendencyAddDataFragment fragmentInuptData =
		// TendencyAddDataFragment.newInstance();
		if (isList) {
			// fragmentInuptData.setCurrentInex(fragmentList.getCurrentIndex());
			// fragmentInuptData.setMainFragment(this);
			// fragmentInuptData.setFromWhere(1);
			index = fragmentList.getCurrentIndex();
		} else {

			index = fragmenTendency.getCurrentIndex() < 7 ? 0 : fragmenTendency.getCurrentIndex() - 7;
			// fragmentInuptData.setMainFragment(this);
			// fragmentInuptData.setCurrentInex(index);
			// fragmentInuptData.setFromWhere(2);
		}
		switch (index) {
		case 0:
			toFragment(RecordSugarInputNewFrag.class, null, true);
			break;
		case 1:
			toFragment(RecordPressBloodInputFragment.newInstance(null), true, true);
			break;
		case 2:
			toFragment(RecordBmiInputFragment.newInstance(null), true, true);
			break;
		default:
			break;
		}
		// toFragment(TendencyAddFragment.newInstance(), true, true);
	}

	/**
	 * 曲线图
	 */
	private void toTendencyImg() {
		isList = false;
		btnListOrImg.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tendency_btn_list, 0, 0);
		btnListOrImg.setText("列表");
		fragmenTendency = RecordTendencyFragment.newInstance();
		fragmenTendency.setMemberId(memberId);
		fragmenTendency.setFromFragment(1);
		fragmenTendency.setCurrentInex(pageIndex);
		fragmenTendency.choiceTabUI(oldTabIndex);
		FragmentTransaction tran = getFragmentManager().beginTransaction();
		tran.replace(R.id.layout_tendency, fragmenTendency);
		tran.commit();
	}

	/**
	 * 列表
	 */
	private void toTendencyList() {
		isList = true;
		btnListOrImg.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tendency_btn_img, 0, 0);
		btnListOrImg.setText("曲线");
		fragmentList = RecordTableFragment.newInstance();
		// fragmentList.setCurrentInex(null == fragmenTendency ? pageIndex :
		// fragmenTendency.getCurrentIndex());
		fragmentList.setMemberId(memberId);
		fragmentList.setCurrentInex(pageIndex);
		fragmentList.choiceTabUI(oldTabIndex);

		FragmentTransaction tran = getFragmentManager().beginTransaction();
		tran.replace(R.id.layout_tendency, fragmentList);
		tran.commit();
	}

	@Override
	public boolean onBackPress() {
		if(FragmentMrg.isContain(HealthRecordRusultFragment.class)){
			FragmentMrg.popBackToFragment(getActivity(),HealthRecordRusultFragment.class,null,true);
		}else{
			IndexFrag.toFragment(getActivity(), false);
		}
		return true;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void setCurrentInex(int index) {
		pageIndex = index;
	}

	@Override
	public void onFragmentResult(Bundle data) {
		super.onFragmentResult(data);
		if (data != null && fragmentList != null) {
			fragmentList.requestTendencyPointList();
		}
	}
}
