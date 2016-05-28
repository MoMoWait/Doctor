package com.comvee.tnb.ui.record.diet;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

import com.comvee.frame.FragmentMrg;
import com.comvee.network.BackgroundTasks;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.network.PostFinishInterface;
import com.comvee.tnb.ui.book.BookIndexRootFragment;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.more.WebNewFrag;
import com.comvee.tnb.ui.tool.HeatFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UITool;
import com.comvee.util.Util;

/**
 * 饮食页面
 *
 * @author Administrator
 */
public class RecordDietIndexFragment extends BaseFragment implements OnClickListener, PostFinishInterface {
	private TitleBarView mBarView;// 头部

	private InnerViewpager mealViewPager;
	private int preViewPagerIndex;
	private boolean isDestoryViewed = false;

	private ImageView picFootRep;

	private ImageView picSwap;

	private ImageView picfoodMenu;
	private String proclamationUrlTitle;
	private String proclamationUrl;

	public static void toFragment(FragmentActivity fragment) {
		FragmentMrg.toFragment(fragment, RecordDietIndexFragment.class, null, true);
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.record_diet_index_frag;
	}

	@Override
	public void onLaunch(Bundle dataBundle) {
		super.onLaunch(dataBundle);
		init();
	}

	private void init() {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		mBarView.setTitle(getString(R.string.record_sugar_food).toString());
		mBarView.setRightButton(getString(R.string.record_index_right_btn).toString(), this);

		picfoodMenu = (ImageView) findViewById(R.id.food_menu);
		picFootRep = (ImageView) findViewById(R.id.record_index_foot_rep);
		picSwap = (ImageView) findViewById(R.id.record_index_swap);
		picfoodMenu.setOnClickListener(this);
		picFootRep.setOnClickListener(this);
		picSwap.setOnClickListener(this);
		mRoot.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				ViewGroup.LayoutParams picfoodMenuParam = picfoodMenu.getLayoutParams();
				ViewGroup.LayoutParams picFootRepParam = picFootRep.getLayoutParams();
				ViewGroup.LayoutParams picSwapParam = picSwap.getLayoutParams();
				float screenWidth = UITool.getDisplayWidth(getActivity());
				picfoodMenuParam.width = (int) ((screenWidth * 354) / 638);
				picfoodMenuParam.height = (picfoodMenuParam.width * 348) / 354;
				picfoodMenu.setLayoutParams(picfoodMenuParam);
				picFootRepParam.width = (int) ((screenWidth * 284) / 638);
				picFootRepParam.height = (picFootRepParam.width * 173) / 284;
				picFootRep.setLayoutParams(picFootRepParam);
				picSwapParam.width = (int) ((screenWidth * 284) / 638);
				picSwapParam.height = (int) (picfoodMenuParam.height - picFootRepParam.height - UITool.getDevicedensity(getActivity()) * 1);
				picSwap.setLayoutParams(picSwapParam);
				mRoot.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
		mealViewPager = (InnerViewpager) findViewById(R.id.vp_diet);
		mealViewPager
				.setAdapterAndIndicator(new DietIndextPagerAdapter(this, null, mealViewPager), (ViewGroup) findViewById(R.id.text_and_indicator));
	}
	@Override
	public void onFragmentResult(Bundle data) {
		super.onFragmentResult(data);
	}

	@Override
	public void onResume() {
		fetchRemoteData();
		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void fetchRemoteData() {
		showProgressDialog(getString(R.string.msg_loading));
		IndexDataHelper dataHelper = new IndexDataHelper(ConfigUrlMrg.DIET_LIST_NEW);
		dataHelper.putPostValue("isToday", 1 + "");
		dataHelper.setPostFinishInterface(this);
		dataHelper.start();
		//BackgroundTasks.getInstance().push(BackgroundTasks.TASK_NETWORK, dataHelper);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.food_menu:
			if (!TextUtils.isEmpty(proclamationUrl)) {
				toFragment(BookIndexRootFragment.newInstance(false, proclamationUrl, proclamationUrlTitle), true, true);
			}
			break;
		case R.id.record_index_foot_rep:
			toFragment(HeatFragment.class, null, true);
			break;
		case R.id.record_index_swap:
			if (Util.checkFirst(getApplicationContext(), "food_exchang")) {
				toFragment(FoodExchangeTempFrag.class, null, true);
			} else {
				SwapDietFragment.toFragment(getActivity(),  false);
			}

			break;
		case TitleBarView.ID_RIGHT_BUTTON:
			toFragment(MyDietFragment.class, null, true);
			break;

		default:
			break;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void postFinish(int status, Object obj) {
		cancelProgressDialog();
		if (status != 0) {
			return;
		}
		Map<String, Object> response = (Map<String, Object>) obj;
		proclamationUrlTitle = (String) response.get("urltitle");
		proclamationUrl = (String) response.get("proclamationUrl") + "&type=android";
		ViewGroup vg = (ViewGroup) findViewById(R.id.text_and_indicator);
		if (obj != null && status == 0) {
			mealViewPager.setAdapterAndIndicator(new DietIndextPagerAdapter(this, (List<TodayDiet>) response.get("diets"), mealViewPager), vg);
		}
		if (isDestoryViewed) {
			mealViewPager.setCurrentItem(preViewPagerIndex);
			return;
		}
		// 请求完后主动滑动viewpager
		moveViewPagerLoc();
	}

	/**
	 * 根据当前时间段移动ViewPager位置
	 */
	private void moveViewPagerLoc() {
		int cHhour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (cHhour > 0 && cHhour < 10) {// 早
			preViewPagerIndex = 0;
			mealViewPager.setCurrentItem(0);
		} else if (cHhour >= 10 && cHhour < 16) {// 中
			preViewPagerIndex = 1;
		} else {// 晚
			preViewPagerIndex = 2;
		}
		mealViewPager.setCurrentItem(preViewPagerIndex);

	}

	@Override
	public void onDestroyView() {
		isDestoryViewed = true;
		preViewPagerIndex = mealViewPager.getCurrentItem();
		super.onDestroyView();
	}

	@Override
	public boolean onBackPress() {
		IndexFrag.toFragment(getActivity(),true); 
		return true;
	}

}
