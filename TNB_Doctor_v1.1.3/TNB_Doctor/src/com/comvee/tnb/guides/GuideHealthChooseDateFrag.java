package com.comvee.tnb.guides;

import java.util.Calendar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.tencent.sugardoctor.widget.wheelview.WheelView;

public class GuideHealthChooseDateFrag extends BaseFragment implements View.OnClickListener {
	private WheelView mWheel;
	private WheelView mFloatWheel;
	private GuideQuesInfo mInfo;
	private int mMaxNum, mMinNum, mDefaultNum, mDefaultFloatNum;
	private String mUnit;
	private NumberAdapter mAdapter;
	private NumberAdapter mFloatAdapter;
	private TitleBarView mBarView;

	public void setGuideInfo(GuideQuesInfo info) {
		this.mInfo = info;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_guides_health_date;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mInfo != null) {
			GuideItemInfo itemInfo = mInfo.getItems().get(0);
			mUnit = itemInfo.getItemUnit();
			mMaxNum = itemInfo.getMaxValue();
			mMinNum = itemInfo.getMinValue();

			if (!TextUtils.isEmpty(itemInfo.getDefaultValue()) && itemInfo.getDefaultValue().length() == 6) {
				mDefaultNum = Integer.valueOf(itemInfo.getDefaultValue().substring(0, 4));
				mDefaultFloatNum = Integer.valueOf(itemInfo.getDefaultValue().substring(4, 6)) - 1;
			}
		}
	}

	public GuideHealthChooseDateFrag() {

	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		// findViewById(R.id.title_main).setBackgroundColor(getResources().getColor(R.color.calendar_bg_color));
		findViewById(R.id.more).setVisibility(View.GONE);

		findViewById(R.id.btn_next).setOnClickListener(this);

		((TextView) findViewById(R.id.tv_unit)).setText(mUnit);
		((TextView) findViewById(R.id.tv_content)).setText(mInfo.getTopicTitle());
		if (null == mAdapter)
			mAdapter = new NumberAdapter(getApplicationContext(), mMinNum, mMaxNum, 16);
		mWheel = (WheelView) findViewById(R.id.v_wheel_year);
		mWheel.setAdapter(mAdapter);
		// 默认值的位置
		int defaultIndex = mDefaultNum - mMinNum;
		mWheel.setSelection(defaultIndex);
		mAdapter.setSeletedIndex(defaultIndex);
		mWheel.setOnItemSelectedListener(mAdapter);

		mFloatWheel = (WheelView) findViewById(R.id.v_wheel_month);
		if (null == mFloatAdapter)
			mFloatAdapter = new NumberAdapter(getApplicationContext(), 1, 12, 16);
		mFloatWheel.setAdapter(mFloatAdapter);

		mFloatWheel.setSelection(mDefaultFloatNum);
		mFloatAdapter.setSeletedIndex(mDefaultFloatNum);
		mFloatWheel.setOnItemSelectedListener(mFloatAdapter);
		if (mInfo.getTitleBar() != null && mInfo != null) {
			mBarView.setTitle(mInfo.getTitleBar());
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btn_next) {

			String value = mWheel.getSelectedItem().toString();
			mDefaultNum = Integer.valueOf(value);
			mDefaultFloatNum = Integer.valueOf(mFloatWheel.getSelectedItem().toString());

			if (mDefaultNum == Calendar.getInstance().get(Calendar.YEAR) && mDefaultFloatNum > Calendar.getInstance().get(Calendar.MONTH) + 1) {
				showToast(getString(R.string.txt_date_choose_limit));
			} else {
				value = value + String.format("%02d", Integer.valueOf(mFloatWheel.getSelectedItem().toString()));
				showToast("" + value);

			}
			mDefaultFloatNum--;
		}

	}

	public static GuideHealthChooseDateFrag newInstance(GuideQuesInfo mInfo) {
		GuideHealthChooseDateFrag frag = new GuideHealthChooseDateFrag();
		frag.setGuideInfo(mInfo);
		return frag;
	}

}
