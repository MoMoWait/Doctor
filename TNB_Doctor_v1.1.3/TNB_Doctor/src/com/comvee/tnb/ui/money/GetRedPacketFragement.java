package com.comvee.tnb.ui.money;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CelebrateRedPacketDialog;
import com.comvee.tnb.model.GetRedForApp;
import com.comvee.tnb.model.SendSmsForAppRed;
import com.comvee.tnb.model.VerficateMsg;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ResUtil;
import com.comvee.tool.UserMrg;
import com.comvee.util.UITool;

/**
 * 领取红包界面,如果是游客或者QQ登录用户
 * 
 * @author linbin 2016.3.16
 */
public class GetRedPacketFragement extends BaseFragment {

	private static final long DELAY_MILLIS = 1000;// 一秒发送一次消息
	private TitleBarView mTitlebar;
	private EditText edtEnterPhone;// 手机号
	private EditText edtVerificate;// 验证码
	private TextView tvCountDown;// 倒计时
	private int startTime = 60;// 60秒倒计时
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			startTime--;
			if (startTime == 0) {
				tvCountDown.setEnabled(true);// 设置按钮可点
				tvCountDown.setSelected(false);
				handler.removeMessages(0);// 停止
				tvCountDown.setText(R.string.get_verify_code);
				startTime = 60;// 时间重置
			} else {
				handler.sendMessageDelayed(Message.obtain(handler, 0), DELAY_MILLIS);
				if(isAdded()){
					tvCountDown.setText(getString(R.string.format_get_verify_code, startTime));
				}
			
			}
			return true;
		}
	});
	/**
	 * 监听事件
	 */
	private View.OnClickListener onClickListner = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.rel_get_now:// 立即获取
				getRedPacket();
				break;
			case R.id.tv_count_down:// 获取验证码
				getVerifyCode();
				break;
			default:
				break;
			}
		}
	};

	public static GetRedPacketFragement newInstance() {
		return new GetRedPacketFragement();
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.money_get_red_packet_fragment;
	}

	@Override
	public void onLaunch(Bundle dataBundle) {
		super.onLaunch(dataBundle);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		initTitleBarView();
		init();

	}

	/**
	 * 初始化标题
	 */
	private void initTitleBarView() {
		mTitlebar = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mTitlebar.setTitle(ResUtil.getString(R.string.get_money));
		mTitlebar.setLeftDefault(this);
		mTitlebar.setTitleBarBackgroundColor(getResources().getColor(android.R.color.transparent));

	}

	private void init() {

		findViewById(R.id.rel_get_now).setOnClickListener(onClickListner);
		edtEnterPhone = (EditText) findViewById(R.id.edt_enter_phone);
		edtVerificate = (EditText) findViewById(R.id.edt_verificate);
		tvCountDown = (TextView) findViewById(R.id.tv_count_down);
		tvCountDown.setOnClickListener(onClickListner);
		UITool.setEditWithClearButton(edtEnterPhone, R.drawable.btn_txt_clear);

	}

	/**
	 * 领取红包
	 */

	private void getRedPacket() {

		if (!verifyPhone()) {
			return;
		}
		if (!verifySecurityCode()) {
			return;
		}

		showProgressDialog(getString(R.string.msg_loading));
		ObjectLoader<GetRedForApp> loader = new ObjectLoader<GetRedForApp>();
		loader.setNeedCache(false);
		loader.putPostValue("user_no", edtEnterPhone.getText().toString());
		loader.putPostValue("sms_valid_code", edtVerificate.getText().toString());
		loader.loadBodyObject(GetRedForApp.class, ConfigUrlMrg.GET_RED_FOR_APP, loader.new CallBack() {

			@Override
			public void onBodyObjectSuccess(boolean isFromCache, GetRedForApp obj) {
				super.onBodyObjectSuccess(isFromCache, obj);
				cancelProgressDialog();
				if (obj != null) {
					// // 弹出领到红包界面
					// if
					// (!TextUtils.isEmpty(obj.isGuest)&&obj.isGuest.equals("0")){
					// //保存用户是否为游客状态
					// UserMrg.setTestData(getApplicationContext(),false);
					// }else{
					// UserMrg.setTestData(getApplicationContext(),true);
					// }
					CelebrateRedPacketDialog celebrateRedPacketDialog = new CelebrateRedPacketDialog();
					celebrateRedPacketDialog.setMoney(obj.money);
					celebrateRedPacketDialog.setUrl(obj.pic);
					celebrateRedPacketDialog.setmBottomMsg(obj.notisMsg);
					celebrateRedPacketDialog.setmTitle(obj.getRedMsg);
					celebrateRedPacketDialog.show(getFragmentManager(), "");
					FragmentMrg.toBack(getActivity());// 显示领到红包页面后消失后面
				}

			}

			@Override
			public boolean onFail(int status) {
				cancelProgressDialog();
				return super.onFail(status);
			}
		});

	}

	/**
	 * 获取验证码
	 */

	private void getVerifyCode() {

		showProgressDialog(getString(R.string.msg_loading));
		// // 获取验证码成功后
		tvCountDown.setEnabled(false);// 设置按钮不可点,字体变灰
		tvCountDown.setSelected(true);// 被选中后背景变暗

		ObjectLoader<VerficateMsg> loader = new ObjectLoader<VerficateMsg>();
		loader.putPostValue("user_no", edtEnterPhone.getText().toString());
		loader.loadBodyObject(VerficateMsg.class, ConfigUrlMrg.SEND_SMS_FOR_APP_RED, loader.new CallBack() {

			@Override
			public void onBodyObjectSuccess(boolean isFromCache, VerficateMsg verficateMsg) {
				super.onBodyObjectSuccess(isFromCache, verficateMsg);
				cancelProgressDialog();

				try {
					if (verficateMsg != null) {
						if (verficateMsg.code.equals("0000")) {
							handler.sendMessageDelayed(Message.obtain(handler, 0), DELAY_MILLIS);// 发送延时消息，实现倒计时效果
						}

					}

				} catch (Exception e) {
					e.printStackTrace();

				}

			}

			@Override
			public boolean onFail(int status) {
				cancelProgressDialog();
				return super.onFail(status);
			}
		});
	}

	/**
	 * 手机号非空判断
	 * 
	 * @return
	 */
	private boolean verifyPhone() {
		boolean verify = true;
		String phone = edtEnterPhone.getText().toString();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(TNBApplication.getInstance(), R.string.enter_phone, Toast.LENGTH_LONG).show();
			verify = false;
		}
		if (edtEnterPhone.length() != 11) {
			Toast.makeText(TNBApplication.getInstance(), R.string.prompt_enter_mobile_phone, Toast.LENGTH_LONG).show();
			verify = false;
		}
		return verify;
	}

	/**
	 * 验证码输入非空判断和输入验证码正确判断
	 * 
	 * @return
	 */

	private boolean verifySecurityCode() {
		boolean verify = true;
		String verifyCode = edtVerificate.getText().toString();
		if (TextUtils.isEmpty(verifyCode)) {
			Toast.makeText(TNBApplication.getInstance(), R.string.enter_verify_code, Toast.LENGTH_LONG).show();
			verify = false;
		}
		return verify;
	}

}
