package com.comvee.tnb.ui.member;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.model.MemberInfo;
import com.comvee.tnb.widget.TitleBarView;

/**
 * 新添加成员选择页
 * 
 * @author friendlove
 * 
 */
@SuppressLint("ValidFragment")
public class MemberChooseRelativeFragment extends BaseFragment implements OnClickListener {
	private boolean hasSelf;// 已有的成员列表中是否有“本人”
	private boolean isLancher;
	private TitleBarView mBarView;
	private int oldResId = -1;
	private TextView tvTitle;
	private int[] res = { R.id.btn_relative0, R.id.btn_relative1, R.id.btn_relative2, R.id.btn_relative3, R.id.btn_relative4, R.id.btn_relative5,
			R.id.btn_relative6, R.id.btn_relative7, R.id.btn_relative8, R.id.btn_relative9, R.id.btn_relative10, R.id.btn_relative11,
			R.id.btn_relative12, R.id.btn_relative13 };
	private String[] codes = { "CBYBRGX002", "CBYBRGX003", "CBYBRGX008", "CBYBRGX009", "CBYBRGX010", "CBYBRGX011", "CBYBRGX012", "CBYBRGX013",
			"CBYBRGX014", "CBYBRGX015", "CBYBRGX005", "CBYBRGX006", "CBYBRGX007", "CBYBRGX001" };
	private MemberInfo mInfo;

	public void setHasSelf(boolean b) {
		hasSelf = b;
	}

	public static MemberChooseRelativeFragment newInstance(MemberInfo mInfo) {
		MemberChooseRelativeFragment fragment = new MemberChooseRelativeFragment(mInfo);
		return fragment;
	}

	public MemberChooseRelativeFragment() {

	}

	private MemberChooseRelativeFragment(MemberInfo mInfo) {
		isLancher = true;
		this.mInfo = mInfo;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_first3;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		init();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	private void init() {

		tvTitle = (TextView) findViewById(R.id.tv_title);
		findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.btn_yes).setOnClickListener(this);
		findViewById(R.id.btn_no).setOnClickListener(this);
		findViewById(R.id.btn_unkown).setOnClickListener(this);

		if (isLancher) {
			showRelative();
			// } else {
			// oldResId = Arrays.asList(codes).indexOf(mInfo.relative);
			// if (oldResId != -1) {
			// oldResId = res[oldResId];
			// }
			// System.out.println("是否糖尿病-----" + mInfo.isTnb);
			// if ("RADIO_VALUE_IS".equals(mInfo.isTnb)) {
			// findViewById(R.id.btn_yes).setBackgroundResource(R.drawable.question_04);
			// } else if ("RADIO_VALUE_ISNOT".equals(mInfo.isTnb)) {
			// findViewById(R.id.btn_no).setBackgroundResource(R.drawable.question_04);
			// } else if ("RADIO_VALUE_NOTKNOWN".equals(mInfo.isTnb)) {
			// findViewById(R.id.btn_unkown).setBackgroundResource(R.drawable.question_04);
			// }
			// showTnb();
		}

		View view = null;
		for (int i : res) {
			view = findViewById(i);
			view.setOnClickListener(this);
			if (oldResId == i) {
				view.setBackgroundResource(R.drawable.question_04);
			} else {
				view.setBackgroundResource(R.drawable.question_07);
			}
		}
	}

	@Override
	public void onDestroyView() {
		// isLancher = false;
		super.onDestroyView();

	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();

		boolean isRelative = false;
		switch (id) {

		case R.id.btn_relative0:
			mInfo.relative = codes[0];
			mInfo.sex = "1";
			isRelative = true;
			break;
		case R.id.btn_relative1:
			mInfo.relative = codes[1];
			mInfo.sex = "2";
			isRelative = true;

			break;
		case R.id.btn_relative2:
			mInfo.relative = codes[2];
			mInfo.sex = "1";
			isRelative = true;

			break;
		case R.id.btn_relative3:
			mInfo.relative = codes[3];
			mInfo.sex = "2";
			isRelative = true;

			break;
		case R.id.btn_relative4:
			mInfo.relative = codes[4];
			mInfo.sex = "1";
			isRelative = true;

			break;
		case R.id.btn_relative5:
			mInfo.relative = codes[5];
			mInfo.sex = "2";
			isRelative = true;

			break;
		case R.id.btn_relative6:
			mInfo.relative = codes[6];
			mInfo.sex = "1";
			isRelative = true;

			break;
		case R.id.btn_relative7:
			mInfo.relative = codes[7];
			mInfo.sex = "2";
			isRelative = true;

			break;
		case R.id.btn_relative8:
			mInfo.relative = codes[8];
			mInfo.sex = "1";
			isRelative = true;

			break;
		case R.id.btn_relative9:
			mInfo.relative = codes[9];
			mInfo.sex = "2";
			isRelative = true;

			break;
		case R.id.btn_relative10:
			mInfo.relative = codes[10];
			isRelative = true;
			mInfo.sex = null;
			break;
		case R.id.btn_relative11:
			mInfo.relative = codes[11];
			isRelative = true;
			mInfo.sex = null;
			break;
		case R.id.btn_relative12:
			mInfo.relative = codes[12];
			isRelative = true;
			mInfo.sex = null;
			break;
		case R.id.btn_relative13:
			mInfo.relative = codes[13];
			isRelative = true;
			mInfo.sex = null;
			break;

		case R.id.btn_back:
			if (findViewById(R.id.layout_tnb).getVisibility() == View.VISIBLE) {
				showRelative();
			} else {
				FragmentMrg.toBack(getActivity());
			}
			break;
		case R.id.btn_yes:
			mInfo.isTnb = "RADIO_VALUE_IS";
			toFragment(MemberChooseNormolFragment.newInstance(mInfo), true, true);
			break;
		case R.id.btn_no:
			mInfo.isTnb = "RADIO_VALUE_ISNOT";
			toFragment(MemberChooseNormolFragment.newInstance(mInfo), true, true);
			break;
		case R.id.btn_unkown:
			mInfo.isTnb = "RADIO_VALUE_NOTKNOWN";
			toFragment(MemberChooseNormolFragment.newInstance(mInfo), true, true);
			break;
		default:
			break;
		}

		if (isRelative) {
			mInfo.name = mInfo.getRelativeChinese();
			v.setBackgroundResource(R.drawable.question_04);
			if (oldResId != -1) {
				findViewById(oldResId).setBackgroundResource(R.drawable.question_07);
			}
			oldResId = id;
			toFragment(MemberChooseNormolFragment.newInstance(mInfo), true, true);
			// showTnb();
		}

	}

	// private void showTnb() {
	// tvTitle.setText("他/她是否患糖尿病或服用过治疗糖尿病的药物");
	// findViewById(R.id.layout_tnb).setVisibility(View.VISIBLE);
	// findViewById(R.id.layout_relative).setVisibility(View.GONE);
	// }

	private void showRelative() {
		tvTitle.setText("他/她与你的关系");
		mBarView.setTitle("他/她与你的关系");
		findViewById(R.id.layout_tnb).setVisibility(View.GONE);
		findViewById(R.id.layout_relative).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_relative13).setVisibility(hasSelf ? View.INVISIBLE : View.VISIBLE);
		findViewById(R.id.btn_relative13).setEnabled(!hasSelf);
	}

	@Override
	public boolean onBackPress() {
		return false;
	}
}
