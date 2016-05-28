package com.comvee.tnb.guides;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.widget.TitleBarView;

public class GuideHealthFoodCalFrag extends BaseFragment implements OnClickListener {
	private UIFoodInfo mInfo;
	private String calorie;
	private int calorieType;
	private TitleBarView mBarView;

	public void setInt(String key, long value) {
		getApplicationContext().getSharedPreferences("config", 0).edit().putLong(key, value).commit();
	}

	public static GuideHealthFoodCalFrag newInstance(UIFoodInfo info) {
		GuideHealthFoodCalFrag frag = new GuideHealthFoodCalFrag();
		frag.setUIFoodInfo(info);
		return frag;
	}

	public GuideHealthFoodCalFrag() {
	}

	public void setUIFoodInfo(UIFoodInfo info) {
		this.mInfo = info;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_guides_health_foodcalorie;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		// findViewById(R.id.title_main).setBackgroundColor(getResources().getColor(R.color.calendar_bg_color));
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		calorie = mInfo.getNormalsport();
		calorieType = 2;
		TextView tv = (TextView) findViewById(R.id.tv_title);
		tv.setText(Html.fromHtml(getString(R.string.txt_food,
				"<strong><big><font color=" + TNBApplication.getInstance().getResources().getColor(R.color.green_1) + "> " + calorie
						+ " </font></big></strong>")));
		findViewById(R.id.btn_nosport).setOnClickListener(this);
		findViewById(R.id.btn_lowsport).setOnClickListener(this);
		findViewById(R.id.btn_normalsport).setOnClickListener(this);
		findViewById(R.id.btn_highsport).setOnClickListener(this);
		findViewById(R.id.btn_next).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_nosport) {
			calorie = mInfo.getNosport();
			calorieType = 0;
			((TextView) findViewById(R.id.tv_title)).setText(Html.fromHtml(getString(R.string.txt_food, "<strong><big><font color="
					+ TNBApplication.getInstance().getResources().getColor(R.color.green_1) + "> " + calorie + " </font></big></strong>")));
		} else if (v.getId() == R.id.btn_lowsport) {
			calorie = mInfo.getLowsport();
			calorieType = 1;
			((TextView) findViewById(R.id.tv_title)).setText(Html.fromHtml(getString(R.string.txt_food, "<strong><big><font color="
					+ TNBApplication.getInstance().getResources().getColor(R.color.green_1) + "> " + calorie + " </font></big></strong>")));
		} else if (v.getId() == R.id.btn_normalsport) {
			calorie = mInfo.getNormalsport();
			calorieType = 2;
			((TextView) findViewById(R.id.tv_title)).setText(Html.fromHtml(getString(R.string.txt_food, "<strong><big><font color="
					+ TNBApplication.getInstance().getResources().getColor(R.color.green_1) + "> " + calorie + " </font></big></strong>")));
		} else if (v.getId() == R.id.btn_highsport) {
			calorie = mInfo.getHighsport();
			calorieType = 3;
			((TextView) findViewById(R.id.tv_title)).setText(Html.fromHtml(getString(R.string.txt_food, "<strong><big><font color="
					+ TNBApplication.getInstance().getResources().getColor(R.color.green_1) + "> " + calorie + " </font></big></strong>")));
		} else if (v.getId() == R.id.btn_next) {
			setInt("calorie:", calorieType);
			try {
				// IndexTaskInfo info = DataParser.createIndexTaskInfo(new
				// JSONObject(mInfo.getLinkTask()));
				// TaskJumpMrg.getInstance(getActivity()).jumpIndexTask(info,
				// true);
				// TaskJumpMrg.getInstance(getActivity()).jumpSugestFood(calorieType,
				// 0, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
