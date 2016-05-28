package com.comvee.tnb.guides;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.tencent.sugardoctor.widget.wheelview.WheelView;

public class GuideHealthChooseNumFrag extends BaseFragment implements OnClickListener {
	private WheelView mWheel;
	private WheelView mFloatWheel;
	private GuideQuesInfo mInfo;
	private String mUnit;
	private int mMaxNum, mMinNum, mDefaultNum, mDefaultFloatNum;
	private NumberAdapter mAdapter;
	private NumberAdapter mFloatAdapter;
	private boolean isFloat;
	private TitleBarView mBarView;

	public static GuideHealthChooseNumFrag newInstance(GuideQuesInfo info) {
		GuideHealthChooseNumFrag frag = new GuideHealthChooseNumFrag();
		frag.setGuideInfo(info);
		return frag;
	}

	public void setGuideInfo(GuideQuesInfo info) {
		this.mInfo = info;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mInfo != null) {

			GuideItemInfo itemInfo = mInfo.getItems().get(0);
			mUnit = itemInfo.getItemUnit();
			mMinNum = itemInfo.getMinValue();
			mMaxNum = itemInfo.getMaxValue();
			isFloat = itemInfo.getIfFloat() != 0;

			if (isFloat && itemInfo.getDefaultValue().contains(".")) {
				String value = itemInfo.getDefaultValue();
				mDefaultNum = Integer.valueOf(value.split("\\.")[0] + "");
				mDefaultFloatNum = Integer.valueOf(value.split("\\.")[1] + "");
			} else {
				mDefaultNum = Integer.valueOf(itemInfo.getDefaultValue());
			}
		}
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_guides_health_number;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		findViewById(R.id.btn_next).setOnClickListener(this);

		((TextView) findViewById(R.id.tv_unit)).setText(mUnit);
		((TextView) findViewById(R.id.tv_content)).setText(mInfo.getTopicTitle());
		if (null == mAdapter)
			mAdapter = new NumberAdapter(getApplicationContext(), mMinNum, mMaxNum, 16);
		mWheel = (WheelView) findViewById(R.id.v_number_wheel);
		mWheel.setAdapter(mAdapter);
		// 默认值的位置
		int defaultIndex = mDefaultNum - mMinNum;
		mWheel.setSelection(defaultIndex);
		mAdapter.setSeletedIndex(defaultIndex);
		mWheel.setOnItemSelectedListener(mAdapter);

		mFloatWheel = (WheelView) findViewById(R.id.v_number_wheel_float);
		if (isFloat) {
			if (null == mFloatAdapter)
				mFloatAdapter = new NumberAdapter(getApplicationContext(), 0, 9, 16);
			mFloatWheel.setAdapter(mFloatAdapter);

			mFloatWheel.setSelection(mDefaultFloatNum);
			mFloatAdapter.setSeletedIndex(mDefaultFloatNum);
			mFloatWheel.setOnItemSelectedListener(mFloatAdapter);
		} else {
			findViewById(R.id.v_float).setVisibility(View.GONE);
			mFloatWheel.setVisibility(View.GONE);
			findViewById(R.id.rel_wheel_group).setVisibility(View.GONE);
		}
		if (mInfo.getTitleBar() != null && mInfo != null) {
			mBarView.setTitle(mInfo.getTitleBar());
		}
	}

	@Override
	public boolean onBackPress() {
		return super.onBackPress();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btn_next) {
			String value = mWheel.getSelectedItem().toString();
			mDefaultNum = Integer.valueOf(value);
			if (isFloat) {
				value = value + "." + mFloatWheel.getSelectedItem().toString();
				mDefaultFloatNum = Integer.valueOf(mFloatWheel.getSelectedItem().toString());
			}
			if (mInfo != null) {
				GuideMrg.getInstance().jumpNextQuesTask(this, mInfo, mInfo.getHasGoto(), value);
			} else {
				showToast(getResources().getString(R.string.error));
			}
			// if ("10003".equals(mInfo.getSeq())) {// 体重
			// ConfigUtil.setString("weight", value);
			// } else if ("10002".equals(mInfo.getSeq())) {// 身高
			// ConfigUtil.setString("height", value);
			// } else if ("10001".equals(mInfo.getSeq())) {// 年龄
			// ConfigUtil.setString("age", value);
			// }
		}

	}
}
