package com.comvee.tnb.ui.ask;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.AskTellOrderInfo;
import com.comvee.tnb.widget.TitleBarView;

/**
 * 电话咨询详情
 * 
 * @author friendlove-pc
 * 
 */
public class AskTellDetailFragment extends BaseFragment implements OnClickListener, OnHttpListener {
	private TextView code, time, department, phone, doctor, hospital;
	private AskTellOrderInfo info;
	private String sid;
	private TitleBarView mBarView;

	private void setAskTellOrderInfo(String sid) {
		this.sid = sid;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.ask_tell_detail_fragment;
	}

	public AskTellDetailFragment() {
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		mBarView.setTitle(getString(R.string.title_order_detail));

		code = (TextView) findViewById(R.id.tv_order_code);
		time = (TextView) findViewById(R.id.tv_order_time);
		department = (TextView) findViewById(R.id.tv_order_department);
		phone = (TextView) findViewById(R.id.tv_order_phone);
		doctor = (TextView) findViewById(R.id.tv_order_doctor);
		hospital = (TextView) findViewById(R.id.btn_order_hospital);

		requestGetDetail();
		super.onLaunch();
	}

	private void UpdataUI() {
		if (info != null) {
			code.setText("预约号：" + info.sid);
			time.setText("预约日期：" + info.planDate + "  " + info.startTime + "-" + info.endTime);
			department.setText(info.department);
			phone.setText(info.mobile);
			doctor.setText(info.doctorName);
			hospital.setText(info.hospital);
			Button button = (Button) findViewById(R.id.btn_order__remove);
			button.setOnClickListener(this);
			if (isEnabled(info.planDate + " " + info.startTime, 2)) {
				button.setEnabled(false);

			}
			if (info.status == 1 || isEnabled(info.planDate + " " + info.endTime, 1)) {
				button.setVisibility(View.GONE);
				findViewById(R.id.tv_order_message).setVisibility(View.GONE);
			} else {
				button.setVisibility(View.VISIBLE);
			}
		}

	}

	public static AskTellDetailFragment newInstance(String sid) {
		AskTellDetailFragment frag = new AskTellDetailFragment();
		frag.setAskTellOrderInfo(sid);
		return frag;
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		cancelProgressDialog();
		switch (what) {
		case 1:
			parseTellOrderList(b, fromCache);
			UpdataUI();
			break;
		case 3:
			try {
				ComveePacket packet = ComveePacket.fromJsonString(b);
				if (packet.getResultCode() == 0) {
					showToast("取消预约成功");
					// ComveeHttp.clearCache(getApplicationContext(),
					// UserMrg.getCacheKey(ConfigUrlMrg.ASK_TELL_ORDER_LIST));
					// ((MainActivity)
					// getActivity()).toFragmentAfterIndex(AskListFragment.newInstance(),
					// true);
					AskQuestionFragment.isDeleat = true;
					FragmentMrg.toBack(getActivity());
				}
			} catch (Exception e) {

				e.printStackTrace();
			}

			break;
		default:
			break;
		}

	}

	private void parseTellOrderList(byte[] arg1, boolean fromCache) {
		try {
			ComveePacket packet = ComveePacket.fromJsonString(arg1);
			if (packet.getResultCode() == 0) {

				JSONObject obj = packet.optJSONObject("body");
				info = new AskTellOrderInfo();
				info.department = obj.optString("department");
				info.planDate = obj.optString("planDate");
				info.doctorId = obj.optString("doctorId");
				info.doctorName = obj.optString("doctorName");
				info.startTime = obj.optString("startTime");
				info.endTime = obj.optString("endTime");
				info.hospital = obj.optString("hospital");
				info.insertDt = obj.optString("insertDt");
				info.isValid = obj.optString("isValid");
				info.memberId = obj.optString("memberId");
				info.mobile = obj.optString("mobile");
				info.status = obj.optInt("status");
				info.sid = obj.optString("sid");
				info.perRealPhoto = obj.optString("perRealPhoto");
			}

		} catch (Exception e) {
			showToast(getString(R.string.error));
			e.printStackTrace();
		}
	}

	@Override
	public void onFialed(int what, int errorCode) {
		cancelProgressDialog();
		ComveeHttpErrorControl.parseError(getActivity(), errorCode);
	}

	@Override
	public void onClick(View arg0) {
		showRemoveVerifyDialog(sid);
	}

	/**
	 * 删除预约 确认
	 */
	private void showRemoveVerifyDialog(final String sid) {
		CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
		builder.setMessage("是否确认取消此次预约?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				requestRemoveOrder(sid);
			}
		});
		builder.create().show();

	}

	private void requestRemoveOrder(String id) {
		showProgressDialog(getString(R.string.msg_loading));
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.ASK_TELL_ORDER_REMOVE);
		http.setOnHttpListener(3, this);
		http.setPostValueForKey("sid", id);
		http.startAsynchronous();
	}

	/**
	 * 是否过期
	 * 
	 * @param time
	 * @return
	 */
	public static boolean isEnabled(String time, int type) {

		long times = 0;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			times = formatter.parse(time).getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long time2 = new Date().getTime();
		if (type == 1) {
			if (times - time2 < 0) {
				return true;
			}
		}
		if (type == 2) {
			if (times - time2 < 30 * 60 * 1000) {
				return true;
			}
		}
		return false;
	}

	private void requestGetDetail() {
		showProgressDialog(getString(R.string.msg_loading));
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.GET_TELL_DETAIL);
		http.setPostValueForKey("sid", sid);
		// http.setNeedGetCache(true,
		// UserMrg.getCacheKey(ConfigUrlMrg.MEMBER_SERVER));
		http.setOnHttpListener(1, this);
		http.startAsynchronous();
	}
}
