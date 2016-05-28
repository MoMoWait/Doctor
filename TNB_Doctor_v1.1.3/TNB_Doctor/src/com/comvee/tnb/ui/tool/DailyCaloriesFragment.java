package com.comvee.tnb.ui.tool;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.dialog.CustomFloatNumPickDialog;
import com.comvee.tnb.dialog.CustomFloatNumPickDialog.OnNumChangeListener;
import com.comvee.tnb.widget.TasksCompletedView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.SaveInfo;

public class DailyCaloriesFragment extends BaseFragment implements OnClickListener {

	private TasksCompletedView mCompletedView;
	private TextView tvHeight;// 身高
	private TextView tvWeigth;// 体重
	private TextView tvStrength1;// 选择强度
	private TextView tvStrength2;

	private View layoutStrength;
	private View oldView;
	Resources res;

	private int mHeight = 0;
	private int mWeight = 0;// 身高 体重
	private int type = -1;// 0-3 运动强度 卧床、轻体力、中体力、重体力
	private int body;// 强度值
	private int maxEn = 2500;// 每日最大需要能量
	private TitleBarView mBarView;

	// private int oldValue=0;
	// private int newValue=0;

	// private ProgressRunable mProgressRunable;
	public DailyCaloriesFragment() {
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				int tmp = jsCalories();// 圈内要画的值
				if (mCurrentProgress < tmp) {
					mCurrentProgress += 10;
					mCompletedView.setProgress(mCurrentProgress, tmp, maxEn);
					// mHandler.sendEmptyMessageDelayed(0, 1);
					// mHandler.sendEmptyMessage(0);
				}

				if (mCurrentProgress > tmp) {
					mCurrentProgress -= 10;
					mCompletedView.setProgress(mCurrentProgress, tmp, maxEn);
					// mHandler.sendEmptyMessageDelayed(1, 1);
					// mHandler.sendEmptyMessage(1);
				}
				break;

			case 1:
				int tmp1 = jsCalories();// 圈内要画的值
				if (mCurrentProgress > tmp1) {
					mCurrentProgress -= 10;
					mCompletedView.setProgress(mCurrentProgress, tmp1, maxEn);
					// mHandler.sendEmptyMessageDelayed(1, 10);
				}
				break;

			default:
				break;
			}
		};
	};

	public static DailyCaloriesFragment newInstance() {
		return new DailyCaloriesFragment();
	}

	private int mCurrentProgress = 0;

	private void drawPro() {
		mHandler.sendEmptyMessage(0);
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragemnt_daily_calories;
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onLaunch(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onLaunch(bundle);
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		init();
	}

	private void init() {
		mBarView.setTitle("计算每日热量");
		mCompletedView = (TasksCompletedView) findViewById(R.id.tasks_view);

		tvHeight = (TextView) findViewById(R.id.tv_height);
		tvWeigth = (TextView) findViewById(R.id.tv_weight);
		tvStrength1 = (TextView) findViewById(R.id.tv_s1);
		tvStrength2 = (TextView) findViewById(R.id.tv_s2);
		tvHeight.setText("0");
		tvWeigth.setText("0");
		tvStrength1.setText("请选择");

		View view1 = findViewById(R.id.layout1);
		View view2 = findViewById(R.id.layout2);
		View view3 = findViewById(R.id.layout3);
		view1.setOnClickListener(this);
		view2.setOnClickListener(this);
		view3.setOnClickListener(this);
		view3.setBackgroundResource(R.drawable.bg_choice);
		oldView = view3;

		layoutStrength = findViewById(R.id.layout_strength);
		layoutStrength.setVisibility(View.VISIBLE);

		View v1 = findViewById(R.id.layout_sport1);
		View v2 = findViewById(R.id.layout_sport2);
		View v3 = findViewById(R.id.layout_sport3);
		View v4 = findViewById(R.id.layout_sport4);
		v1.setOnClickListener(this);
		v2.setOnClickListener(this);
		v3.setOnClickListener(this);
		v4.setOnClickListener(this);

		mWeight = SaveInfo.getWeight(mContext);
		mHeight = SaveInfo.getHeight(mContext);
		type = SaveInfo.getType(mContext);
		setType(type);
		tvWeigth.setText("" + mWeight);
		tvHeight.setText("" + SaveInfo.getHeight(mContext));

		drawPro();

	}

	// 设置运动类型
	private void setType(int type) {
		switch (type) {
		case -1:
			tvStrength1.setText("请选择");
			break;
		case 0:
			tvStrength1.setText("卧床");
			break;
		case 1:
			tvStrength1.setText("低强度");
			break;
		case 2:
			tvStrength1.setText("中等强度");
			break;
		case 3:
			tvStrength1.setText("高强度");
			break;
		}
		SaveInfo.setType(mContext, type);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout1:
			oldView.setBackgroundResource(R.drawable.btn_tool);
			v.setBackgroundResource(R.drawable.bg_choice);
			layoutStrength.setVisibility(View.GONE);
			oldView = v;
			String[] items = new String[] { "100", "200", "cm" };
			showNumDialog(tvHeight, items, 0);

			drawPro();
			break;

		case R.id.layout2:
			oldView.setBackgroundResource(R.drawable.btn_tool);
			v.setBackgroundResource(R.drawable.bg_choice);
			layoutStrength.setVisibility(View.GONE);
			oldView = v;
			String[] weight = new String[] { "20", "150", "kg" };
			showNumDialog(tvWeigth, weight, 1);

			drawPro();
			break;

		case R.id.layout3:
			oldView.setBackgroundResource(R.drawable.btn_tool);
			v.setBackgroundResource(R.drawable.bg_choice);
			layoutStrength.setVisibility(View.VISIBLE);
			oldView = v;

			drawPro();
			break;

		case R.id.layout_sport1:
			type = 0;
			tvStrength2.setText("运动强度");
			setType(type);

			drawPro();
			break;
		case R.id.layout_sport2:
			type = 1;
			setType(type);
			tvStrength2.setText("运动强度");
			drawPro();
			break;
		case R.id.layout_sport3:
			type = 2;
			setType(type);
			tvStrength2.setText("运动强度");
			drawPro();
			break;
		case R.id.layout_sport4:
			type = 3;
			setType(type);
			tvStrength2.setText("运动强度");
			drawPro();
			break;

		default:
			break;
		}

	}

	// index=0身高 1体重
	public void showNumDialog(final TextView view, final String[] items, final int index) {
		int def = 0;
		if (index == 0) {
			def = 165;
		} else if (index == 1) {
			def = 60;
		}
		CustomFloatNumPickDialog buidler = new CustomFloatNumPickDialog();

		buidler.setLimitNum(Integer.valueOf(items[0]), Integer.valueOf(items[1]));
		buidler.setFloat(false);
		buidler.setDefult(def);
		buidler.setUnit(items[2]);
		buidler.addOnNumChangeListener(new OnNumChangeListener() {

			@Override
			public void onChange(DialogFragment dialog, float num) {
				String n = String.valueOf((int) num);
				view.setText(n);
				if (index == 0) {
					mHeight = (int) num;
					SaveInfo.setHeight(mContext, mHeight);
				} else if (index == 1) {
					mWeight = (int) num;
					SaveInfo.setWeight(mContext, mWeight);
				}
				mHandler.sendEmptyMessage(0);
			}
		});

		buidler.show(getActivity().getSupportFragmentManager(), "dialog");
	}

	// 计算卡路里
	private int jsCalories() {
		if (mHeight == 0) {
			return 0;
		}
		if (mWeight == 0) {
			return 0;
		}
		if (type == -1) {
			return 0;
		}

		int check = checkWeight(mHeight, mWeight);
		switch (check) {
		case -1:// 消瘦
			switch (type) {
			case 0:
				body = 25;
				break;
			case 1:
				body = 35;
				break;
			case 2:
				body = 40;
				break;
			case 3:
				body = 50;
				break;
			}
			break;

		case 0:
			switch (type) {
			case 0:
				body = 20;
				break;
			case 1:
				body = 30;
				break;
			case 2:
				body = 35;
				break;
			case 3:
				body = 40;
				break;
			}
			break;

		case 1:
			switch (type) {
			case 0:
				body = 15;
				break;
			case 1:
				body = 25;
				break;
			case 2:
				body = 30;
				break;
			case 3:
				body = 35;
				break;
			}
			break;
		}
		if ((mHeight - 105) * body < 0) {
			showToast("请输入正常的身高和体重");
			return 0;
		}
		// 每日所需总能量=标准体重*运动强度
		return (mHeight - 105) * body;
	}

	// 判断体重
	private int checkWeight(int h, int w) {
		int standardW = h - 105;// 标准体重（kg）=身高（cM）-105
		float minW = (float) ((float) standardW * 0.9);
		float maxW = (float) ((float) standardW * 1.1);
		if (w < minW) {
			return -1;// 消瘦
		} else if (w >= minW && w <= maxW) {
			return 0;// 正常
		} else {
			return 1;// 肥胖
		}
		// return 0;
	}

}
