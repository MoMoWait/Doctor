package com.comvee.tnb.ui.ask;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.AskServerInfo;
import com.comvee.tnb.model.AskTellInfo;
import com.comvee.tnb.ui.machine.BarCodeFragment;
import com.comvee.tnb.ui.more.WebFragment;
import com.comvee.tnb.ui.privatedoctor.DoctorServerList;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UITool;
import com.comvee.tool.UserMrg;

/**
 * 电话咨询 下单确认
 * 
 * @author friendlove-pc
 * 
 */
public class AskTellVerifyFragment extends BaseFragment implements OnClickListener, OnHttpListener {

	private AskTellInfo mInfo;
	private EditText edtPhone;
	private TitleBarView mBarView;

	private void setAskTellInfo(AskTellInfo mInfo) {
		this.mInfo = mInfo;

	}

	@Override
	public int getViewLayoutId() {
		return R.layout.ask_tell_verify_fragment;
	}

	@Override
	public void onStart() {

		super.onStart();

	}

	public AskTellVerifyFragment() {
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		mBarView.setTitle(getString(R.string.title_order_tell));
		if (!mInfo.leaveNum.equals("0") && mInfo.leaveNum != null && !mInfo.leaveNum.equals("")) {
			findViewById(R.id.layout_no_data).setVisibility(View.GONE);
			findViewById(R.id.layout_root).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.layout_no_data).setVisibility(View.VISIBLE);
			findViewById(R.id.layout_root).setVisibility(View.GONE);
		}
		edtPhone = (EditText) findViewById(R.id.edt_tell_phone);
		UITool.setTextView(getView(), R.id.tv_tell_docname, mInfo.docName);
		UITool.setTextView(getView(), R.id.tv_tell_date, mInfo.planDate);
		UITool.setTextView(getView(), R.id.tv_tell_time, mInfo.startTime + "-" + mInfo.endTime);
		UITool.setTextView(getView(), R.id.tv_tell_num, mInfo.leaveNum);
		edtPhone.setText(UserMrg.getLoginName(getApplicationContext()));
		findViewById(R.id.btn_verify_order).setOnClickListener(this);
		findViewById(R.id.btn_buy_sugar).setOnClickListener(this);
		findViewById(R.id.btn_buy_doc_server).setOnClickListener(this);
	}

	public static AskTellVerifyFragment newInstance(AskTellInfo mInfo) {
		AskTellVerifyFragment frag = new AskTellVerifyFragment();
		frag.setAskTellInfo(mInfo);
		return frag;
	}

	private void reuqestSubmitMobile(String mobile) {

		if (TextUtils.isEmpty(mobile)) {
			UITool.showEditError(edtPhone, "不能为空");
			return;
		}
		showProgressDialog(getString(R.string.msg_loading));
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.ASK_TELL_VERIFY);
		http.setPostValueForKey("doctorId", mInfo.doctorId);
		http.setPostValueForKey("mobile", mobile);
		http.setPostValueForKey("planDate", mInfo.planDate);
		http.setPostValueForKey("perRealPhoto", mInfo.perRealPhoto);
		http.setPostValueForKey("startTime", mInfo.planDate + " " + mInfo.startTime);
		http.setPostValueForKey("endTime", mInfo.planDate + " " + mInfo.endTime);
		http.setPostValueForKey("foreignKey", mInfo.sid);
		http.setOnHttpListener(1, this);
		http.startAsynchronous();

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_verify_order:
			reuqestSubmitMobile(edtPhone.getText().toString());
			break;
		case R.id.btn_buy_sugar:
			String url = ConfigParams.getConfig(getApplicationContext(), ConfigParams.TEXT_MECHINE_INFO) + "?origin=android&sessionID="
					+ UserMrg.getSessionId(getApplicationContext()) + "&sessionMemberID=" + UserMrg.getMemberSessionId(getApplicationContext());
			WebFragment web = WebFragment.newInstance("购买设备", url);

			toFragment(web, true, true);
			break;
		case R.id.btn_buy_doc_server:
			DoctorServerList.toFragment(getActivity(), mInfo.doctorId);
			break;
		default:
			break;
		}
	}

	/** 显示无套餐提示 **/
	private void showNoPackageDialog() {
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int what) {
				switch (what) {
				case CustomDialog.ID_NO:
					BarCodeFragment.toFragment(getActivity(), false, 1);
					break;
				case CustomDialog.ID_OK:
					String url = ConfigParams.getConfig(getApplicationContext(), ConfigParams.TEXT_MECHINE_INFO) + "?origin=android&sessionID="
							+ UserMrg.getSessionId(getApplicationContext()) + "&sessionMemberID="
							+ UserMrg.getMemberSessionId(getApplicationContext());
					WebFragment web = WebFragment.newInstance("购买设备", url);
					toFragment(web, true, true);
					break;
				default:
					break;
				}
			}
		};
		CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
		builder.setMessage("购买并绑定掌控糖尿病智能血糖仪，可立即获得一年12次电话咨询资格。");
		builder.setTitle("对不起，您没有可用的电话咨询次数哦！");
		builder.setPositiveButton("购买血糖仪", listener);
		builder.setNegativeButton("绑定血糖仪", listener);
		builder.create().show();
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		cancelProgressDialog();
		try {
			ComveePacket packet = ComveePacket.fromJsonString(b);
			if (packet.getResultCode() == 0) {
				showToast(packet.getResultMsg());
				ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.ASK_TELL_ORDER_LIST));
				// toFragment(AskListFragment.newInstance(), true);
				// toFragment(AskListFragment.newInstance(AskListFragment.TYPE_ORDER),
				// true, true);
				AskServerInfo info = new AskServerInfo();
				info.setDoctorId(mInfo.doctorId);
				info.setDoctorName(mInfo.docName);
				ConfigParams.TO_BACK_TYPE = 2;
				AskQuestionFragment.toFragment(getActivity(), info);
			} else if (packet.getResultCode() == 100063) {
				showNoPackageDialog();
			} else {
				ComveeHttpErrorControl.parseError(getActivity(), packet);
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
