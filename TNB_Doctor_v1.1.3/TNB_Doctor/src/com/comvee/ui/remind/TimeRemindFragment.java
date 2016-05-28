package com.comvee.ui.remind;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ResUtil;
import com.comvee.util.Util;

import org.json.JSONObject;

import java.util.List;

/**
 * 提醒首页
 * 
 * @author Administrator
 * 
 */
public class TimeRemindFragment extends BaseFragment implements OnClickListener, OnCheckedChangeListener, OnHttpListener {
	private View rela_0, rela_1, rela_2;
	private CheckBox cb_0, cb_1, cb_2;
	private List<TimeRemindTransitionInfo> list;
	public static final int REMIND_MEDICINE = 2;// 用药
	public static final int REMIND_SUGAR = 1;// 血糖
	private final boolean REMIND_PLAY = true;// 响铃
	private final boolean REMIND_NOT_PLAY = false;// 不响铃
	private TimeRemindUtil util;
	private TitleBarView mBarView;

	@Override
	public int getViewLayoutId() {
		return R.layout.remind_remind;
	}

	public TimeRemindFragment() {
	}

	public static TimeRemindFragment newInstance() {
		TimeRemindFragment fragment = new TimeRemindFragment();
		return fragment;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		init();
		mBarView.setTitle(ResUtil.getString(R.string.more_remind_set));
		if (!ConfigParams.IS_TEST_DATA) {
			requestLoadSet();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mRoot = null;
	}

	/**
	 * 读取开关列表
	 */
	private void requestLoadSet() {
		showProgressDialog(ResUtil.getString(R.string.msg_loading));
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.MORE_LOAD_REMIND_SET);
		http.setOnHttpListener(1, this);
		http.startAsynchronous();

	}

	/**
	 * 保存提醒
	 */
	private void requestSetSubmit() {
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.MORE_MODIFY_REMIND_SET);

		http.setPostValueForKey("taskSet", String.valueOf(cb_2.isChecked() ? 1 : 0));
		http.setOnHttpListener(2, this);
		http.startAsynchronous();
	}

	private void init() {
		util = TimeRemindUtil.getInstance(getApplicationContext());
		util.star();
		list = util.getAllRemindTimeList();// 获取所有数据库中的的时间

		rela_0 = findViewById(R.id.btn_check0);
		rela_1 = findViewById(R.id.btn_check1);
		rela_2 = findViewById(R.id.btn_check2);
		cb_0 = (CheckBox) findViewById(R.id.check0);
		cb_1 = (CheckBox) findViewById(R.id.check1);
		cb_2 = (CheckBox) findViewById(R.id.check2);

		cb_0.setChecked(isCheck(REMIND_SUGAR));
		cb_1.setChecked(isCheck(REMIND_MEDICINE));

		rela_0.setOnClickListener(this);
		rela_1.setOnClickListener(this);
		rela_2.setOnClickListener(this);

		cb_0.setOnCheckedChangeListener(this);
		cb_1.setOnCheckedChangeListener(this);
		cb_2.setOnCheckedChangeListener(this);

		if (Util.checkFirst(getApplicationContext(), "taskremind")) {
			cb_2.setChecked(true);
			requestSetSubmit();
		}

	}

	// 判断checkbox的状态
	private boolean isCheck(int type) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getType() == type && list.get(i).isDiabolo()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_check0:
			RemindListFragment.toFragment(getActivity(), REMIND_SUGAR, null);
			break;
		case R.id.btn_check1:
			RemindListFragment.toFragment(getActivity(), REMIND_MEDICINE, null);
			break;
		case R.id.btn_check2:
			// MyTaskCenterFragment fragment = new MyTaskCenterFragment();
			// fragment.setFromremind(true);
			toFragment(TaskRemindFragement.newInstance(), true, true);
			break;
		case TitleBarView.ID_LEFT_BUTTON:
//			((MainActivity) getActivity()).toggle();
			break;
		default:
			break;
		}
	}

	// 更新所有当前类型的数据 设置是否响铃
	private void updata(int type, boolean isDiabolo) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getType() == type) {
				TimeRemindTransitionInfo data = list.get(i);
				if (isDiabolo) {
					data.setDiabolo(REMIND_PLAY);
					data.setTemp(REMIND_PLAY);
				} else {
					data.setDiabolo(REMIND_NOT_PLAY);
				}

				util.updataTime(data);
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		switch (arg0.getId()) {
		case R.id.check0:
			if (arg1) {
				updata(REMIND_SUGAR, REMIND_PLAY);
			} else {
				updata(REMIND_SUGAR, REMIND_NOT_PLAY);
			}
			if (Util.checkFirst(getApplicationContext(), "remind_medicine") && arg1) {
				updata(REMIND_SUGAR, REMIND_PLAY);
				RemindListFragment.toFragment(getActivity(), REMIND_SUGAR, null);
			}
			break;
		case R.id.check1:
			if (arg1) {
				updata(REMIND_MEDICINE, REMIND_PLAY);
			} else {
				updata(REMIND_MEDICINE, REMIND_NOT_PLAY);
			}
			if (Util.checkFirst(getApplicationContext(), "remind_sugar") && arg1) {
				updata(REMIND_MEDICINE, REMIND_PLAY);
				RemindListFragment.toFragment(getActivity(), REMIND_MEDICINE, null);
			}
			break;
		case R.id.check2:
			if (!ConfigParams.IS_TEST_DATA) {
				requestSetSubmit();
			} else {
				cb_2.setChecked(false);
				toFragment(LoginFragment.newInstance(), true, true);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {

		cancelProgressDialog();
		try {

			ComveePacket packet = ComveePacket.fromJsonString(b);

			switch (what) {
			case 1:
				if (packet.getResultCode() == 0) {
					JSONObject obj = packet.getJSONObject("body");
					cb_2.setChecked(obj.optInt("taskSet") == 1);
					cb_2.setOnCheckedChangeListener(this);
				} else {
					ComveeHttpErrorControl.parseError(getActivity(), packet);
				}

				break;

			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onFialed(int what, int errorCode) {
		cancelProgressDialog();
		ComveeHttpErrorControl.parseError(getActivity(), errorCode);
	}
}
