package com.comvee.tnb.guides;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.tencent.sugardoctor.widget.wheelview.StringWheelAdapter;
import com.tencent.sugardoctor.widget.wheelview.WheelView;

public class GuideHealthChooseWheel extends BaseFragment implements OnClickListener {
	private WheelView mWheelView;
	private GuideQuesInfo mInfo;
	private String mUnit;
	private StringWheelAdapter mAapter;
	private ArrayList<String> list;
	private TitleBarView mBarView;

	public static GuideHealthChooseWheel newInstance(GuideQuesInfo info) {
		GuideHealthChooseWheel frag = new GuideHealthChooseWheel();
		frag.setGuideInfo(info);
		return frag;
	}

	public void setGuideInfo(GuideQuesInfo info) {
		this.mInfo = info;
	}

	public GuideHealthChooseWheel() {
	}

	@Override
	public int getViewLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_guides_health_number;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mInfo != null) {
			list = new ArrayList<String>();
			for (GuideItemInfo info : mInfo.getItems()) {
				list.add(info.getItemTitle());
			}
		}

	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		findViewById(R.id.tv_unit).setVisibility(View.GONE);
		findViewById(R.id.v_number_wheel_float).setVisibility(View.GONE);
		findViewById(R.id.v_float).setVisibility(View.GONE);
		findViewById(R.id.btn_next).setOnClickListener(this);

		((TextView) findViewById(R.id.tv_unit)).setText(mUnit);
		((TextView) findViewById(R.id.tv_content)).setText(mInfo.getTopicTitle());
		if (null == mAapter)
			mAapter = new StringWheelAdapter(getApplicationContext(), list, 16);
		mWheelView = (WheelView) findViewById(R.id.v_number_wheel);
		mWheelView.setAdapter(mAapter);
		// 默认值的位置
		mWheelView.setSelection(0);
		mAapter.setSeletedIndex(0);
		mWheelView.setOnItemSelectedListener(mAapter);
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
		if (v.getId() == R.id.btn_next) {
			//String value = mInfo.getItems().get(mWheelView.getSelectedItemPosition()).getItemValue();
			// JumpTaskInfo.getInstance(getActivity()).jumpNextTask(mInfo,
			// value);
		}

	}

}
