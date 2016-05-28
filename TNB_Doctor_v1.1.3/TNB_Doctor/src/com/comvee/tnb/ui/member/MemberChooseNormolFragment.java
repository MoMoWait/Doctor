package com.comvee.tnb.ui.member;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.model.MemberInfo;
import com.comvee.tnb.widget.OnScrollChangedListener;
import com.comvee.tnb.widget.RuleHorizontalScrollView;
import com.comvee.tnb.widget.RuleScrollView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UITool;
import com.comvee.util.Util;

/**
 * @author friendlove 用户注册后资料填写页，用于选择性别／年龄／体重／身高
 */
@SuppressLint("ValidFragment")
public class MemberChooseNormolFragment extends BaseFragment implements OnScrollChangedListener, OnClickListener {
	private MemberInfo mInfo;
	private RuleScrollView mRuleScrollView;
	private RuleHorizontalScrollView mRuleHorizontalScrollView;
	private TextView tvHeight;
	private TextView tvWeight;
	private TextView tvYear;
	private TextView tvTitle;
	private int mType;// 0、选择性别1、体重2、身高3、年龄
	private boolean isLancher;

	private Button btnNext, btnLast;
	private View vWeight;
	private View vSex;
	private View vHeight;
	private int mWeight = 60;
	private int mYear = 1980;
	private int mHeight = 170;
	private boolean hasDefualtSex;
	private ImageView img_man, img_woman;
	private boolean isShowSex = true;
	private TitleBarView mBarView;

	// 男 70kg 170cm 1980
	// 女 50kg 160cm 1980
	private void initValues() {
		if ("1".equals(mInfo.sex)) {
			mWeight = 60;
			mHeight = 170;
		} else if ("2".equals(mInfo.sex)) {
			mWeight = 50;
			mHeight = 160;
		}
	}

	public void setShowSex(boolean isShowSex) {
		this.isShowSex = isShowSex;
	}

	public static MemberChooseNormolFragment newInstance(MemberInfo mInfo) {
		MemberChooseNormolFragment fragment = new MemberChooseNormolFragment(mInfo);
		return fragment;
	}

	public MemberChooseNormolFragment() {
	}

	@SuppressLint("ValidFragment")
	public MemberChooseNormolFragment(MemberInfo mInfo) {
		isLancher = true;
		if (mInfo == null) {
			mInfo = new MemberInfo();
		}
		this.mInfo = mInfo;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_first2;
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

	@SuppressLint("NewApi")
	public void showChooseAge() {
		mType = 3;
		tvTitle.setText(R.string.member_choose_age);
		mBarView.setTitle(getString(R.string.member_choose_age));
		mRuleHorizontalScrollView.setEnabled(true);
		mRuleScrollView.setEnabled(false);
		final ImageView ivIcon = (ImageView) findViewById(R.id.iv_man);
		ivIcon.setImageResource("1".equals(mInfo.sex) ? R.drawable.icon_man1 : R.drawable.icon_men2);
		vHeight.setVisibility(View.INVISIBLE);
		vWeight.setVisibility(View.VISIBLE);
		findViewById(R.id.layout_year).setVisibility(View.VISIBLE);
		findViewById(R.id.layout_weight).setVisibility(View.GONE);
		final View vRule = findViewById(R.id.v_rule2);
		int width = UITool.getViewWidth(vRule);
		double cel = width / 30d / 10;
		ImageView iv = (ImageView) findViewById(R.id.v_rule3);
		if (android.os.Build.VERSION.SDK_INT > 10)
			mRuleHorizontalScrollView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		Bitmap newb = Bitmap.createBitmap(vRule.getWidth(), 60, Config.ARGB_8888);
		Canvas cv = new Canvas(newb);
		cv.save(Canvas.ALL_SAVE_FLAG);
		Paint paint = new Paint(Paint.FAKE_BOLD_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		paint.setColor(getResources().getColor(R.color.text_defualt));
		paint.setStrokeWidth(2);
		paint.setTextSize(28);
		paint.setAntiAlias(true);
		for (int i = 0; i < 30; i++) {
			double x = cel * 10 * i - 35;
			cv.drawText(1735 + i * 10 + "", (float) x, 28, paint);
		}
		iv.setImageBitmap(newb);
		scrollToAge(mYear);

	}

	public void scrollToAge(int year) {
		View vRule = findViewById(R.id.v_rule2);
		double cel = vRule.getWidth() / 30d / 10;
		int width = Util.dip2px(getApplicationContext(), 260);
		mRuleHorizontalScrollView.scrollTo((int) Math.ceil(cel * (year - 1735) - width / 2), 0);
	}

	@SuppressLint("NewApi")
	public void showChooseHeight() {
		mType = 2;
		tvTitle.setText(R.string.member_choose_height);
		mBarView.setTitle(getString(R.string.member_choose_height));
		final ImageView ivIcon = (ImageView) findViewById(R.id.btn_per);
		ivIcon.setImageResource("1".equals(mInfo.sex) ? R.drawable.icon_man1 : R.drawable.icon_men2);
		vHeight.setVisibility(View.VISIBLE);
		vWeight.setVisibility(View.INVISIBLE);
		int height = Util.dip2px(getApplicationContext(), 320);
		View vRule = findViewById(R.id.v_rule);
		int h = UITool.getViewHeight(vRule);
		double cel = h / 30d / 10;
		ImageView iv = (ImageView) findViewById(R.id.v_rule1);
		if (android.os.Build.VERSION.SDK_INT > 10)
			mRuleScrollView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		Bitmap newb = Bitmap.createBitmap(Util.dip2px(getApplicationContext(), 50), vRule.getHeight(), Config.ARGB_8888);
		Canvas cv = new Canvas(newb);
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();
		Paint paint = new Paint(Paint.FAKE_BOLD_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		paint.setColor(getResources().getColor(R.color.text_defualt));
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2);
		paint.setTextSize(28);
		for (int i = 0; i < 30; i++) {
			cv.drawText((int) ((30 - i) * 10) + "", 0f, (float) (cel * 10 * i) + 13, paint);
		}
		iv.setImageBitmap(newb);

		int width = Util.dip2px(getApplicationContext(), 320);
		mRuleScrollView.scrollTo(0, (int) Math.ceil(vRule.getHeight() - cel * (mHeight) - width / 2));
		mRuleHorizontalScrollView.setEnabled(false);
		mRuleScrollView.setEnabled(true);
	}

	public void scrollToHeight(int height) {
		View vRule = findViewById(R.id.v_rule);
		double cel = vRule.getHeight() / 30d / 10;
		int width = Util.dip2px(getApplicationContext(), 320);
		mRuleScrollView.smoothScrollTo(0, (int) Math.ceil(vRule.getHeight() - cel * (height) - width / 2));
	}

	@SuppressLint("NewApi")
	public void showChooseWeight() {
		mRuleHorizontalScrollView.setEnabled(true);
		mRuleScrollView.setEnabled(false);
		mType = 1;
		tvTitle.setText(R.string.member_choose_weight);
		mBarView.setTitle(getString(R.string.member_choose_weight));
		if (android.os.Build.VERSION.SDK_INT > 10)
			mRuleHorizontalScrollView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		btnNext.setVisibility(View.VISIBLE);
		final ImageView ivIcon = (ImageView) findViewById(R.id.iv_man);
		ivIcon.setImageResource("1".equals(mInfo.sex) ? R.drawable.icon_man1 : R.drawable.icon_men2);
		findViewById(R.id.layout_year).setVisibility(View.GONE);
		findViewById(R.id.layout_weight).setVisibility(View.VISIBLE);

		vSex.setVisibility(View.GONE);
		vWeight.setVisibility(View.VISIBLE);
		vHeight.setVisibility(View.INVISIBLE);

		View vRule = findViewById(R.id.v_rule2);// 尺子
		int w = UITool.getViewWidth(vRule);
		double cel = w / 30d / 10;
		ImageView iv = (ImageView) findViewById(R.id.v_rule3);
		Bitmap newb = Bitmap.createBitmap(vRule.getWidth(), 60, Config.ARGB_8888);
		Canvas cv = new Canvas(newb);
		cv.save(Canvas.ALL_SAVE_FLAG);
		Paint paint = new Paint(Paint.FAKE_BOLD_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		paint.setColor(getResources().getColor(R.color.text_defualt));
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2);
		paint.setTextSize(28);
		for (int i = 0; i < 30; i++) {
			float x = (float) (cel * 10f * i - 15);
			cv.drawText(i * 10 + "", x, 28, paint);
		}
		iv.setImageBitmap(newb);
		int width = Util.dip2px(getApplicationContext(), 260);
		mRuleHorizontalScrollView.scrollTo((int) Math.ceil(cel * (mWeight) - width / 2), 0);

	}

	public void scrollToWeight(int weight) {
		View vRule = findViewById(R.id.v_rule2);// 尺子
		double cel = vRule.getWidth() / 30d / 10;
		int width = Util.dip2px(getApplicationContext(), 260);
		mRuleHorizontalScrollView.smoothScrollTo((int) Math.ceil(cel * (weight) - width / 2), 0);
	}

	public void showChooseSex() {
		mType = 0;
		tvTitle.setText(R.string.member_choose_sex);
		mBarView.setTitle(getString(R.string.member_choose_sex));
		btnNext.setVisibility(View.VISIBLE);
		vWeight.setVisibility(View.INVISIBLE);
		vHeight.setVisibility(View.INVISIBLE);
		vSex.setVisibility(View.VISIBLE);
		img_man.setVisibility(View.GONE);

		if (mInfo.sex != null) {
			if (mInfo.sex.equals("1")) {
				img_man.setVisibility(View.VISIBLE);
				img_woman.setVisibility(View.GONE);
			} else if (mInfo.sex.equals("2")) {
				img_man.setVisibility(View.GONE);
				img_woman.setVisibility(View.VISIBLE);
			}
		}
	}

	private void init() {
		// getTitleBar().setVisibility(View.GONE);

		if (!TextUtils.isEmpty(mInfo.memberHeight)) {
			mHeight = Integer.valueOf(mInfo.memberHeight);
		}

		if (!TextUtils.isEmpty(mInfo.memberWeight)) {
			mWeight = Integer.valueOf(mInfo.memberWeight);
		}

		if (!TextUtils.isEmpty(mInfo.birthday)) {
			mYear = Integer.valueOf(mInfo.birthday.substring(0, mInfo.birthday.indexOf("-")));
		}
		img_man = (ImageView) findViewById(R.id.img_man);
		img_woman = (ImageView) findViewById(R.id.img_woman);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		btnNext = (Button) findViewById(R.id.btn_next);
		btnLast = (Button) findViewById(R.id.btn_last);
		btnNext.setOnClickListener(this);
		btnLast.setOnClickListener(this);
		findViewById(R.id.btn_pre).setOnClickListener(this);
		tvWeight = (TextView) findViewById(R.id.tv_weight);
		tvHeight = (TextView) findViewById(R.id.tv_height);
		tvYear = (TextView) findViewById(R.id.tv_year);
		mRuleScrollView = (RuleScrollView) findViewById(R.id.v_scroll);
		mRuleHorizontalScrollView = (RuleHorizontalScrollView) findViewById(R.id.v_scroll2);
		mRuleHorizontalScrollView.setOnScrollChangedListener(this);
		mRuleScrollView.setOnScrollChangedListener(this);
		vSex = findViewById(R.id.layout_sex);
		vHeight = findViewById(R.id.layout_height);
		vWeight = findViewById(R.id.layout_horizontal);
		findViewById(R.id.btn_man).setOnClickListener(this);
		findViewById(R.id.btn_woman).setOnClickListener(this);
		if (isLancher) {
			if (!TextUtils.isEmpty(mInfo.sex) || TextUtils.isEmpty(mInfo.sex)) {
				hasDefualtSex = false;
				showChooseSex();
			} else if (TextUtils.isEmpty(mInfo.sex) && isShowSex) {
				hasDefualtSex = false;
				showChooseSex();
			} else {
				hasDefualtSex = true;
				ThreadHandler.postUiThread(new Runnable() {
					@Override
					public void run() {
						initValues();
						showChooseWeight();
					}
				}, 100);
			}
		} else {
			ThreadHandler.postUiThread(new Runnable() {
				@Override
				public void run() {
					showChooseAge();
				}
			}, 100);
		}

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		// getTitleBar().setVisibility(View.VISIBLE);
		isLancher = false;
	}

	// private void requestGuestReg() {
	// showProDialog("请稍后...");
	// ComveeHttp http = null;
	// if (ConfigParams.IS_TEST_DATA) {
	// http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.GUEST_REG);
	// } else {
	// http = new ComveeHttp(getApplicationContext(),
	// ConfigUrlMrg.REG_FIRST_CREATE_MEMBER);
	// }
	// http.setPostValueForKey("sex", mInfo.sex);
	// http.setPostValueForKey("birthday", mInfo.birthday);
	// http.setPostValueForKey("weight", mInfo.memberWeight);
	// http.setPostValueForKey("height", mInfo.memberHeight);
	// http.setPostValueForKey("relation", mInfo.relative);
	// http.setPostValueForKey("callreason", mInfo.isTnb);
	// http.setOnHttpListener(1, this);
	// http.startAsynchronous();
	// }

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.btn_last:
			if (mType == 3) {
				btnNext.setText("下一步");
				showChooseHeight();
			} else if (mType == 2) {
				showChooseWeight();
			} else if (mType == 1) {
				if (hasDefualtSex) {
					FragmentMrg.toBack(getActivity());
				} else {
					showChooseSex();
				}
			} else {
				mInfo.sex = null;
				FragmentMrg.toBack(getActivity());
			}
			break;
		case R.id.btn_next:
			if (mType == 0) {
				initValues();
				if (TextUtils.isEmpty(mInfo.sex)) {
					showToast("请选择性别");
				} else {
					showChooseWeight();
				}
			} else if (mType == 1) {
				mInfo.memberWeight = mWeight + "";
				showChooseHeight();
			} else if (mType == 2) {
				mInfo.memberHeight = mHeight + "";
				showChooseAge();
				// btnNext.setText("完成");
			} else if (mType == 3) {
				mInfo.birthday = mYear + "-01-01";
				mInfo.birInt = mYear;
				// if ("RADIO_VALUE_IS".equals(mInfo.isTnb)) {// 是否糖尿病
				// requestGuestReg();
				// } else {
				// toFragment(MemberTestFragment.newInstance(mInfo), true,
				// true);
				// }
				toFragment(MemberChooseWheelFragment.newInstance(mInfo), true, true);
			}
			break;
		case R.id.btn_man:
			mInfo.sex = "1";
			img_man.setVisibility(View.VISIBLE);
			img_woman.setVisibility(View.GONE);
			break;
		case R.id.btn_woman:
			mInfo.sex = "2";
			img_man.setVisibility(View.GONE);
			img_woman.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onBackPress() {
		return false;
	}

	// @Override
	// public void onSussece(int what, byte[] b, boolean fromCache) {
	// cancelProDialog();
	// try {
	// switch (what) {
	// case 1:
	//
	// ComveePacket packet = ComveePacket.fromJsonString(b);
	// if (packet.getResultCode() == 0) {
	// final String sid = packet.optString("sessionID");
	// final String mid = packet.optString("sessionMemberID");
	// UserMrg.setMemberSessionId(getApplicationContext(), mid);
	// UserMrg.setSessionId(getApplicationContext(), sid);
	// // UserMrg.setAoutoLogin(getApplicationContext(), true);
	// JSONObject body = packet.optJSONObject("body");
	//
	// mInfo.diabetes_plan = body.optString("diabetes_plan");
	// mInfo.score_describe = body.optString("score_describe");
	// mInfo.score = body.optInt("score");
	// mInfo.testMsg = body.optString("testMsg");
	//
	// if (ConfigParams.IS_TEST_DATA) {
	// UserMrg.setTestData(getApplicationContext(), true);
	// UserMrg.setTestDataSessionId(getApplicationContext(), sid);
	// UserMrg.setTestDataMemberId(getApplicationContext(), mid);
	// }
	//
	// // if (ParamsConfig.IS_TEST_DATA)
	// // {
	// // toFragment(MemberTestResultFragment.newInstance(mInfo), false, true);
	// // } else
	// // {
	// // showToast("添加成功");
	// // ((MainActivity) getActivity()).toIndexFragment();
	// // }
	//
	// } else {
	// ComveeHttpErrorControl.parseError(getActivity(), packet);
	// }
	//
	// break;
	//
	// default:
	// break;
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }
	//
	// @Override
	// public void onFialed(int what, int errorCode) {
	// cancelProDialog();
	// ComveeHttpErrorControl.parseError(getActivity(), errorCode);
	// }

	@Override
	public void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (mType == 1) {// 体重
			int width = Util.dip2px(getApplicationContext(), 260);
			View vRule = findViewById(R.id.v_rule2);
			double cel = vRule.getWidth() / 30d / 10;
			mWeight = (int) Math.rint((mRuleHorizontalScrollView.getScrollX() + width / 2d) / cel);
			tvWeight.setText(mWeight + "");
			tvWeight.invalidate();
			if (mWeight < 20) {
				scrollToWeight(20);
			} else if (mWeight > 260) {
				scrollToWeight(260);
			}
		} else if (mType == 2) {// 身高

			int height = Util.dip2px(getApplicationContext(), 320);
			View vRule = findViewById(R.id.v_rule);
			double cel = vRule.getHeight() / 30d / 10;
			mHeight = (int) Math.rint((vRule.getHeight() - mRuleScrollView.getScrollY() - height / 2d) / cel);
			tvHeight.setText(mHeight + "");

			if (mHeight > 250) {
				scrollToHeight(250);
			} else if (mHeight < 40) {
				scrollToHeight(40);
			}
		} else if (mType == 3) {// 年龄

			int width = Util.dip2px(getApplicationContext(), 260);
			View vRule = findViewById(R.id.v_rule2);
			double cel = vRule.getWidth() / 30d / 10;
			mYear = 1735 + (int) Math.rint((mRuleHorizontalScrollView.getScrollX() + width / 2d) / cel);
			tvYear.setText(mYear + "");
			if (mYear > 2013) {
				scrollToAge(2013);
			} else if (mYear < 1870) {
				scrollToAge(1870);
			}

		}
	}

}
