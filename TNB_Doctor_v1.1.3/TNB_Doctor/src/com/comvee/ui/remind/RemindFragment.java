package com.comvee.ui.remind;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.widget.PickerView;
import com.comvee.tnb.widget.PickerView.onSelectListener;
import com.comvee.tnb.widget.TitleBarView;

/**
 * 
 * @author Administrator 设置提醒时间界面
 */
public class RemindFragment extends BaseFragment implements OnClickListener {

	private TextView tv_1, tv_2, tv_3, tv_4, tv_5, tv_6, tv_7;
	private TimeRemindTransitionInfo oldInfo;
	private List<TextView> tvs;
	// private boolean strWeek[];
	private Button btn_add;
	private EditText edi;
	PickerView minute_pv;
	PickerView hour_pv;
	private TimeRemindUtil util;
	private TitleBarView mBarView;

	private RemindFragment(TimeRemindTransitionInfo info) {
		this.oldInfo = info;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_remind;
	}

	public RemindFragment() {
	}

	public static RemindFragment newInstance(TimeRemindTransitionInfo info) {
		RemindFragment fragment = new RemindFragment(info);
		return fragment;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		// newInfo = new TimeRemindData(oldInfo.getHour(), oldInfo.getMinute(),
		// oldInfo.getType(), oldInfo.getDate(), oldInfo.getIsDiabolo(),
		// oldInfo.getRank(), oldInfo.getTemp());
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		mBarView.setTitle("时间设置");
		util = TimeRemindUtil.getInstance(getApplicationContext());
		initTime();
		initWeek();
	}

	/**
	 * 初始化时间控件
	 */
	private void initTime() {

		minute_pv = (PickerView) findViewById(R.id.minute_pv);
		hour_pv = (PickerView) findViewById(R.id.hour_pv);
		List<String> data = new ArrayList<String>();
		List<String> seconds = new ArrayList<String>();
		for (int i = 0; i < 24; i++) {
			data.add(i < 10 ? "0" + i : "" + i);
		}
		for (int i = 0; i < 60; i++) {
			seconds.add(i < 10 ? "0" + i : "" + i);
		}
		minute_pv.setData(seconds);
		minute_pv.setSelected(oldInfo.getMinute());
		minute_pv.setOnSelectListener(new onSelectListener() {

			@Override
			public void onSelect(String text) {
				oldInfo.setMinute(Integer.parseInt(text));
			}
		});
		hour_pv.setData(data);
		hour_pv.setSelected(oldInfo.getHour());
		hour_pv.setOnSelectListener(new onSelectListener() {

			@Override
			public void onSelect(String text) {
				oldInfo.setHour(Integer.parseInt(text));
			}
		});
		btn_add = (Button) findViewById(R.id.btn_remind_add);
		btn_add.setOnClickListener(this);

	}

	/**
	 * 初始化日期控件
	 */
	private void initWeek() {
		tvs = new ArrayList<TextView>();
		tv_1 = (TextView) findViewById(R.id.remind_1);
		tv_1.setOnClickListener(this);
		tv_1.setTag(0);
		tv_2 = (TextView) findViewById(R.id.remind_2);
		tv_2.setOnClickListener(this);
		tv_2.setTag(1);
		tv_3 = (TextView) findViewById(R.id.remind_3);
		tv_3.setOnClickListener(this);
		tv_3.setTag(2);
		tv_4 = (TextView) findViewById(R.id.remind_4);
		tv_4.setOnClickListener(this);
		tv_4.setTag(3);
		tv_5 = (TextView) findViewById(R.id.remind_5);
		tv_5.setOnClickListener(this);
		tv_5.setTag(4);
		tv_6 = (TextView) findViewById(R.id.remind_6);
		tv_6.setOnClickListener(this);
		tv_6.setTag(5);
		tv_7 = (TextView) findViewById(R.id.remind_7);
		tv_7.setOnClickListener(this);
		tv_7.setTag(6);

		tvs.add(tv_1);
		tvs.add(tv_2);
		tvs.add(tv_3);
		tvs.add(tv_4);
		tvs.add(tv_5);
		tvs.add(tv_6);
		tvs.add(tv_7);
		if (oldInfo.getRemark() == null) {
			boolean[] weeks = { true, true, true, true, true, true, true };
			oldInfo.setWeek(weeks);
		}
		updata(oldInfo.getWeek());

		TextView tv = (TextView) findViewById(R.id.remind_tv);
		edi = (EditText) findViewById(R.id.remind_edt);

		if (oldInfo.getType() == 2) {
			tv.setVisibility(View.VISIBLE);
			edi.setVisibility(View.VISIBLE);
			if (oldInfo.getRemark() != null && !oldInfo.getRemark().equals("")) {
				edi.setText(oldInfo.getRemark());
			}
		} else {
			tv.setVisibility(View.GONE);
			edi.setVisibility(View.GONE);
		}

	}

	/**
	 * 更新日期控件
	 * 
	 * @param week
	 */
	private void updata(boolean week[]) {
		for (int i = 0; i < week.length; i++) {
			if (week[i]) {
				tvs.get(i).setBackgroundResource(R.drawable.round_fill_blue_bg);
				tvs.get(i).setTextColor(getResources().getColor(R.color.white));
			} else {
				tvs.get(i).setBackgroundResource(R.drawable.round_stroke_blue_bg);
				tvs.get(i).setTextColor(getResources().getColor(R.color.theme_color_green));
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		// 保存
		case R.id.btn_remind_add:
			// String date = "";
			// for (int i = 0; i < strWeek.length; i++) {
			// date = date + strWeek[i];
			// if (i != strWeek.length - 1) {
			// date = date + "/";
			// }
			// }

			oldInfo.setDiabolo(true);
			oldInfo.setTemp(true);
			if (oldInfo.getType() == 2 && oldInfo.getRemark() == null) {// 若rank为空且类型为用药提醒
				oldInfo.setRemark(""); // 说明是新建的提醒
				oldInfo.setRemark(edi.getText().toString().trim());
				util.addTime(oldInfo);
				FragmentMrg.toBack(getActivity());
				util.star();
				return;
			}
			if (oldInfo.getType() == 1 || oldInfo.getRemark() != null) {
				oldInfo.setRemark(""); // 说明是新建的提醒
				oldInfo.setRemark(edi.getText().toString().trim());
			}
			// 更新数据库中的数据
			util.updataTime(oldInfo);
			FragmentMrg.toBack(getActivity());
			util.star();
			return;

		default:
			break;
		}
		// 日期控件的点击事件
		int id = (Integer) arg0.getTag();
		if (oldInfo.getWeek()[id]) {
			oldInfo.getWeek()[id] = false;
		} else {
			oldInfo.getWeek()[id] = true;
		}
		updata(oldInfo.getWeek());
	}

}
