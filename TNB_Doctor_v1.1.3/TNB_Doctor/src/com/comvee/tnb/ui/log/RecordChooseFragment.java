package com.comvee.tnb.ui.log;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

import com.comvee.http.KWHttpRequest.KwHttpRequestListener;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.ui.task.DoctorRecommendFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;
import com.comvee.util.Util;

/**
 * 健康日志 选择分析页面
 * 
 * @author friendlove
 * 
 */
@SuppressLint("ValidFragment")
public class RecordChooseFragment extends BaseFragment implements OnClickListener, KwHttpRequestListener {

	private boolean isFromCalendar;//
	private TitleBarView mBarView;

	private RecordChooseFragment(boolean isFromCalendar) {
		this.isFromCalendar = isFromCalendar;
	}

	@SuppressLint("ValidFragment")
	public static RecordChooseFragment newInstance(boolean isFromCalendar) {
		RecordChooseFragment fragment = new RecordChooseFragment(isFromCalendar);
		return fragment;
	}

	public RecordChooseFragment() {
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_record_choose;
	}

	@Override
	public void onStart() {
		super.onStart();
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		init();
		mBarView.setTitle(getString(R.string.title_log));
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case TitleBarView.ID_RIGHT_BUTTON:
			toFragment(RecordCalendarFragment.newInstance(), true, true);
			break;
		case R.id.btn_record_total:
			toFragment(RecordTableFragment.newInstance(0), true, true);
			break;
		case R.id.btn_record_1:
			toFragment(RecordTableFragment.newInstance(1), true, true);
			break;
		case R.id.btn_record_2:
			toFragment(RecordTableFragment.newInstance(2), true, true);
			break;
		case R.id.btn_record_3:
			toFragment(RecordTableFragment.newInstance(3), true, true);
			break;
		case R.id.btn_record_4:
			toFragment(RecordTableFragment.newInstance(4), true, true);
			break;
		case R.id.btn_record_5:
			toFragment(RecordTableFragment.newInstance(5), true, true);
			break;
		case R.id.btn_record_6:
			toFragment(RecordTableFragment.newInstance(6), true, true);
			break;
		default:
			break;
		}

	}

	private void toRecommond() {
		toFragment(DoctorRecommendFragment.newInstance(), true, true);
	}

	public void onSusse() {
		showToast("保存成功！");
	}

	@Override
	public void loadFailed(int arg0, int arg1) {
		cancelProgressDialog();
		showToast(R.string.error);
	}

	@Override
	public void loadFinished(int arg0, byte[] arg1) {
		cancelProgressDialog();
		switch (arg0) {
		case 1:
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onBackPress() {
		return false;
	}

	private View btn_total, btn_record_1, btn_record_2, btn_record_3, btn_record_4, btn_record_5, btn_record_6;

	private void init() {
		btn_total = findViewById(R.id.btn_record_total);
		btn_record_1 = findViewById(R.id.btn_record_1);
		btn_record_2 = findViewById(R.id.btn_record_2);
		btn_record_3 = findViewById(R.id.btn_record_3);
		btn_record_4 = findViewById(R.id.btn_record_4);
		btn_record_5 = findViewById(R.id.btn_record_5);
		btn_record_6 = findViewById(R.id.btn_record_6);
		btn_record_1.setOnClickListener(this);
		btn_record_2.setOnClickListener(this);
		btn_record_3.setOnClickListener(this);
		btn_record_4.setOnClickListener(this);
		btn_total.setOnClickListener(this);

		if (!UserMrg.isTnb()) {
			btn_record_5.setVisibility(View.GONE);
			btn_record_6.setVisibility(View.GONE);
			btn_record_4.setBackgroundResource(R.drawable.button_white_buttom);
			int w = Util.dip2px(getApplicationContext(), 20);
			btn_record_4.setPadding(w, 0, w, 0);

		} else {
			btn_record_4.setVisibility(View.GONE);
			btn_record_5.setOnClickListener(this);
			btn_record_6.setOnClickListener(this);
		}

	}

}
