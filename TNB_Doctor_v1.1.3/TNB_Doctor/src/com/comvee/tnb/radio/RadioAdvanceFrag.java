package com.comvee.tnb.radio;

import java.text.ParseException;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.model.RadioGroup.RadioAlbum;
import com.comvee.tnb.network.NetworkCallBack;
import com.comvee.tnb.network.RadioAdvanceSubmit;
import com.comvee.tnb.widget.ComveeAlertDialog;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.ui.remind.TimeRemindTransitionInfo;
import com.comvee.ui.remind.TimeRemindUtil;
import com.comvee.util.BundleHelper;
import com.comvee.util.TimeUtil;

/**
 * 
 * @author friendlove-pc 直播预告
 */
public class RadioAdvanceFrag extends BaseFragment {
	private TextView btnRemind;
	private RadioAlbum mAlbum;
	private String[] items = { "活动开始前5分钟", "活动开始前10分钟", "活动开始前15分钟" };

	@Override
	public int getViewLayoutId() {
		return R.layout.radio_advance_frag;
	}

	@Override
	public void onLaunch(Bundle dataBundle) {
		super.onLaunch(dataBundle);
		mAlbum = BundleHelper.getObjecByBundle(RadioAlbum.class, dataBundle);
		TitleBarView titleView = (TitleBarView) findViewById(R.id.layout_top);
		titleView.setTitle(mAlbum.radioTitle);
		titleView.setVisibility(View.VISIBLE);
		titleView.setLeftDefault(this);

		TextView tvLabel = (TextView) findViewById(R.id.tv_label);
		TextView tvContent = (TextView) findViewById(R.id.tv_content);
		btnRemind = (TextView) findViewById(R.id.btn_remind);
		ImageView ivPhoto = (ImageView) findViewById(R.id.iv_bannel);

		tvContent.setText(mAlbum.radioInfo);

		btnRemind.setText(mAlbum.isSet == 1 ? "取消提醒" : "设置提醒");
		ImageLoaderUtil.getInstance(getApplicationContext()).displayImage(mAlbum.bannerUrl, ivPhoto, ImageLoaderUtil.null_defult);
		btnRemind.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mAlbum.isSet == 1) {
					requestSetRemind(0);
				} else {
					showDialog();
				}
				// TimeRemindUtil.getInstance(getApplicationContext()).addDisposableAlarm(11111,
				// model, alarmTime);
			}
		});
	}

	public static void toFragment(FragmentActivity act, RadioAlbum album) {
		Bundle bundle = BundleHelper.getBundleByObject(album);
		FragmentMrg.toFragment(act, RadioAdvanceFrag.class, bundle, false);
	}

	/**
	 * 提醒时间选择弹框
	 */
	private void showDialog() {

		ComveeAlertDialog.Builder builder = new ComveeAlertDialog.Builder(getContext());
		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				requestSetRemind(arg1);

			}
		});
		builder.setNegativeButton(getString(R.string.cancel), null);
		builder.create().show();
	}

	/**
	 * 设置短信提醒
	 */
	private void requestSetRemind(final int postion) {
		showProgressDialog("正在设置...");
		// 点击 提醒
		if (mAlbum.isSet == 1) {
			RadioAdvanceSubmit submit = new RadioAdvanceSubmit();
			submit.cancleRemind(mAlbum.radioId, new NetworkCallBack() {
				@Override
				public void callBack(int what, int status, Object obj) {
					cancelProgressDialog();
					mAlbum.isSet = 0;
					btnRemind.setText("设置提醒");
					TimeRemindUtil.getInstance(getContext()).cancleDisposableAlarm(10012);
				}
			});
		} else {
			RadioAdvanceSubmit submit = new RadioAdvanceSubmit();
			submit.addRemind(mAlbum.radioId, mAlbum.radioTitle, mAlbum.startTime, (postion + 1) * 5 + "", new NetworkCallBack() {
				@Override
				public void callBack(int what, int status, Object obj) {
					cancelProgressDialog();
					mAlbum.isSet = 1;
					btnRemind.setText("取消提醒");
					setLocationRemind(postion);
				}
			});
		}
	}

	/**
	 * 设置本地闹钟 type 提前提醒时间 0，五分钟 1，十分钟，2 十五分钟
	 */
	private void setLocationRemind(int type) {
		long tempTime = 0;
		switch (type) {
		case 0:
			tempTime = 5 * 60 * 1000;
			break;
		case 1:
			tempTime = 10 * 60 * 1000;
			break;
		case 2:
			tempTime = 15 * 60 * 1000;
			break;
		default:
			break;
		}
		try {
			long time = TimeUtil.getUTC(mAlbum.startTime, ConfigParams.TIME_FORMAT);
			long remindTime = time - tempTime;
			if (remindTime > System.currentTimeMillis()) {
				TimeRemindTransitionInfo model = new TimeRemindTransitionInfo();
				model.setType(6);
				TimeRemindUtil.getInstance(getContext()).addDisposableAlarm(1, model, remindTime);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
