package com.comvee.ui.remind;

import java.util.List;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ResUtil;

/**
 * 血糖提醒列表页面
 * 
 * @author Administrator
 * 
 */
public class RemindListFragment extends BaseFragment implements OnClickListener, OnCheckedChangeListener {
	private int type;
	private View no_data;
	private LinearLayout root;
	private List<TimeRemindTransitionInfo> list;
	private View lin;
	private boolean isEdit;
	private TimeRemindUtil util;
	private String[] str = TNBApplication.getInstance().getResources().getStringArray(R.array.sugar_time_str);
	private String[] weekstr = TNBApplication.getInstance().getResources().getStringArray(R.array.week_num);
	private TitleBarView mBarView;
	private Class<? extends com.comvee.frame.BaseFragment> class1;

	@Override
	public int getViewLayoutId() {

		return R.layout.fragment_remind_list;
	}

	public RemindListFragment() {
	}

	public static void toFragment(FragmentActivity fragment, int type, Class<? extends Fragment> class1) {
		Bundle bundle = new Bundle();
		bundle.putInt("type", type);
		bundle.putSerializable("class", class1);
		FragmentMrg.toFragment(fragment, RemindListFragment.class, bundle, true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLaunch(Bundle bundle) {
		super.onLaunch(bundle);
		if (bundle != null) {
			type = bundle.getInt("type");
			class1 = (Class<? extends com.comvee.frame.BaseFragment>) bundle.getSerializable("class");
		}
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		util = TimeRemindUtil.getInstance(getApplicationContext());

	}

	@Override
	public boolean onBackPress() {
		if (class1 != null) {
			FragmentMrg.popBackToFragment(getActivity(), class1, null);
			return true;
		}
		return super.onBackPress();
	}

	@Override
	public void onStart() {
		super.onStart();
		initData();
	}

	private RemindListFragment(int type) {
		this.type = type;
	}

	private void initData() {

		root = (LinearLayout) findViewById(R.id.list_view);
		root.removeAllViews();
		no_data = findViewById(R.id.remind_gone);
		lin = findViewById(R.id.bt_understandTNBinfo);
		View view = findViewById(R.id.bt_add);
		Button btn = (Button) findViewById(R.id.btn_remind_add);
		btn.setOnClickListener(this);
		lin.setOnClickListener(this);
		list = util.getRemindTimeList(type);
		if (list == null || list.size() == 0) {
			no_data.setVisibility(View.VISIBLE);
		} else {
			no_data.setVisibility(View.GONE);
		}
		if (type == 2 && list.size() == 0) {
			isEdit = false;
			mBarView.hideRightButton();
			view.setVisibility(View.GONE);

		}
		if (type == 2 && list.size() > 0) {
			view.setVisibility(View.VISIBLE);
			if (isEdit) {
				mBarView.setRightButton(ResUtil.getString(R.string.save), this);
			} else {
				mBarView.setRightButton(ResUtil.getString(R.string.edit), this);
			}
		}

		if (type == 1) {
			mBarView.setTitle(getString(R.string.remind_remind_blood_sugar_checktip));
		} else {
			mBarView.setTitle(getString(R.string.remind_remind_drug_tip));
		}
		createView();

	}

	private void showCancleCollectDialog(final TimeRemindTransitionInfo data) {
		CustomDialog.Builder dialog = new CustomDialog.Builder(getActivity());
		dialog.setMessage("是否取消该提醒？");
		dialog.setPositiveButton(ResUtil.getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				util.deleateTime(data);
				root.removeAllViews();
				initData();
			}

		});
		dialog.create().show();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.bt_understandTNBinfo:
		case R.id.btn_remind_add:
			TimeRemindTransitionInfo data = new TimeRemindTransitionInfo();
			data.setType(2);
			toFragment(RemindFragment.newInstance(data), true, true);
			break;
		case TitleBarView.ID_RIGHT_BUTTON:
			isEdit = !isEdit;
			if (isEdit) {
				mBarView.setRightButton(ResUtil.getString(R.string.save), this);
			} else {
				mBarView.setRightButton(ResUtil.getString(R.string.edit), this);
			}
			root.removeAllViews();
			createView();
			break;
		case 123456:
			TimeRemindTransitionInfo value = (TimeRemindTransitionInfo) arg0.getTag();
			if (!isEdit) {
				toFragment(RemindFragment.newInstance(value), true, true);
			} else {
				showCancleCollectDialog(value);
			}
		default:
			break;
		}

	}

	private void createView() {
		ImageView iv_lift = null;
		View view = null;
		TextView tv_time = null;
		TextView tv_data = null;
		TextView tv_remark = null;
		CheckBox check = null;
		for (int i = 0; i < list.size(); i++) {
			TimeRemindTransitionInfo data = list.get(i);
			this.type = data.getType();
			String time = "";
			if (data.getHour() < 10) {
				time = "0" + data.getHour() + ":";
			} else {
				time = data.getHour() + ":";
			}
			if (data.getMinute() < 10) {
				time = time + "0" + data.getMinute();
			} else {
				time = time + data.getMinute();
			}

			switch (type) {
			case 1:
				view = View.inflate(getContext(), R.layout.item_remind_sugger, null);
				tv_time = (TextView) view.findViewById(R.id.remind_time);
				tv_data = (TextView) view.findViewById(R.id.remind_data);
				tv_remark = (TextView) view.findViewById(R.id.remind_remark);
				iv_lift = (ImageView) view.findViewById(R.id.iv_left);
				check = (CheckBox) view.findViewById(R.id.remind_check);

				tv_remark.setText(str[i % 8]);

				break;
			case 2:
				view = View.inflate(getContext(), R.layout.item_remind_time, null);
				tv_time = (TextView) view.findViewById(R.id.remind_time);
				tv_data = (TextView) view.findViewById(R.id.remind_data);
				tv_remark = (TextView) view.findViewById(R.id.remind_remark);
				iv_lift = (ImageView) view.findViewById(R.id.iv_left);
				check = (CheckBox) view.findViewById(R.id.remind_check);
				if (data.getRemark() != null && !data.getRemark().equals("")) {
					tv_remark.setText(data.getRemark());
				} else {
					tv_remark.setText(ResUtil.getString(R.string.without_remark));
				}

				break;
			default:
				break;
			}
			tv_time.setText(time);
			setDateText(tv_data, data.getWeek());
			if (data.isDiabolo()) {
				check.setChecked(true);
			} else {
				check.setChecked(false);
			}
			if (isEdit) {
				iv_lift.setVisibility(View.VISIBLE);
			} else {
				iv_lift.setVisibility(View.GONE);
			}
			view.setOnClickListener(this);
			view.setId(123456);
			view.setTag(data);
			check.setOnCheckedChangeListener(this);
			check.setTag(data);
			root.addView(view);
		}
	}

	private void setDateText(TextView tv, boolean[] week) {

		String value = ResUtil.getString(R.string.item_remind_week);
		int num = 0;
		for (int i = 0; i < week.length; i++) {
			if (week[i]) {
				num++;
				value = value + weekstr[i] + "、";
			}
		}
		if (num == 7) {
			tv.setText(ResUtil.getString(R.string.any_day));
		} else {
			tv.setText(value.substring(0, value.length() - 1));
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		TimeRemindTransitionInfo data = (TimeRemindTransitionInfo) arg0.getTag();
		if (arg1) {
			data.setDiabolo(true);
			data.setTemp(true);
		} else {
			data.setDiabolo(false);
			data.setTemp(false);
		}
		util.updataTime(data);
		root.removeAllViews();
		initData();
	}

}
