package com.comvee.tnb.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.model.GetRedForApp;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.ui.money.GetRedPacketFragement;

/**
 * 抢红包的自定义Dialog
 */
public class RobRedPacketDialog extends DialogFragment {

	private View mRoot;
	private ImageView ivTear;// 拆
	private ObjectLoader<GetRedForApp> loader;
	private int user_flag;// 1、正常状态 2、游客3、QQ登录用户 以2、3状态的用户要显示输入手机号和验证码
	/**
	 * 监听事件
	 */
	private View.OnClickListener onClickListner = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.iv_close:// 关闭窗口
				dismiss();
				break;
			case R.id.iv_open:// 拆开红包
				ivTear.setSelected(true);
				isPopupRedPacket(user_flag);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));// 设置背景透明
		getDialog().getWindow().setWindowAnimations(R.style.event_in_amin);// 从上往下的动画
		mRoot = inflater.inflate(R.layout.dialog_money_rob_red_packet, container, true);
		onLuanch();
		return mRoot;
	}

	private void onLuanch() {
		ivTear = (ImageView) mRoot.findViewById(R.id.iv_open);
		mRoot.findViewById(R.id.iv_close).setOnClickListener(onClickListner);
		mRoot.findViewById(R.id.iv_open).setOnClickListener(onClickListner);
	}

	public void setUserFlag(int userFlag) {
		user_flag = userFlag;
	}

	/**
	 * 是否领取红包
	 */
	private void isPopupRedPacket(int user_flag) {

		if (user_flag != 1)// 如果是游客或者QQ登录用户
		{
			dismiss();
			FragmentMrg.toFragment(getActivity(), GetRedPacketFragement.class, null, true);
		} else {
			getRedPacket();

		}
	}

	/**
	 * 领取红包请求
	 */

	private void getRedPacket() {
		// showProgressDialog(getString(R.string.msg_loading));
		if (loader == null) {
			loader = new ObjectLoader<GetRedForApp>();
		}
		if (loader.isloading()) {
			Toast.makeText(getActivity(), "请不要重复点击", Toast.LENGTH_LONG).show();
			return;
		}
		loader.setNeedCache(false);
		loader.loadBodyObject(GetRedForApp.class, ConfigUrlMrg.GET_RED_FOR_APP, loader.new CallBack() {
			@Override
			public void onBodyObjectSuccess(boolean isFromCache, GetRedForApp obj) {
				super.onBodyObjectSuccess(isFromCache, obj);
				// cancelProgressDialog();
				if (obj != null) {
					// 弹出领到红包界面
					CelebrateRedPacketDialog celebrateRedPacketDialog = new CelebrateRedPacketDialog();
					celebrateRedPacketDialog.setMoney(obj.money);
					celebrateRedPacketDialog.setUrl(obj.pic);
					celebrateRedPacketDialog.setmBottomMsg(obj.notisMsg);
					celebrateRedPacketDialog.setmTitle(obj.getRedMsg);
					celebrateRedPacketDialog.show(getActivity().getSupportFragmentManager(), "");
					dismiss();
				}

			}

			@Override
			public boolean onFail(int status) {
				// cancelProgressDialog();
				return super.onFail(status);
			}
		});

	}

	@Override
	public void onPause() {
		super.onPause();
		dismiss();
	}
}
