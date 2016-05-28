package com.comvee.tnb.ui.money;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.comvee.BaseApplication;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ResUtil;

/**
 * 这是提现界面
 *
 * @author yujun
 */
public class WithdeawDepositFragment extends BaseFragment implements OnClickListener {

	private static final String TN = "withdeaw";
	private float mAllMoney;// 总共的钱
	private float mNeedMoney;// 需要体现的钱
	private int isAllTake;// 1、活动结束0、活动中
	private static String mAlipayId;
	private static String mAlipayName;
	private TitleBarView mTitlebar;
	private EditText mZfbAccounts;
	private EditText mZfbAccountTo;
	private EditText mZfbName;
	private Button mWithdraw;

	private Button btnMoney10, btnMoney20, btnMoney30, btnMoney40, btnMoney50, btnMoneyAll, btnMoneyOld;
	private String mAlertMemo;
	private String mBannerMemo;
	private TextView mText;

	@Override
	public int getViewLayoutId() {

		return R.layout.zfb_money_fragment;
	}

	@Override
	public void onLaunch(Bundle dataBundle) {
		// super.onLaunch(dataBundle);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		initTitleBarView();
		initView();

		mAllMoney = dataBundle.getFloat("money");
		isAllTake = dataBundle.getInt("isAllTake");
		mAlipayId = dataBundle.getString("alipayId");
		mAlipayName = dataBundle.getString("alipayName");

		mAlertMemo = dataBundle.getString("alertMemo");
		mBannerMemo = dataBundle.getString("bannerMemo");

		mText.setText(mBannerMemo);
		mZfbAccounts.setText(mAlipayId);
		mZfbAccountTo.setText(mAlipayId);
		mZfbName.setText(mAlipayName);

	}

	@Override
	public void onResume() {
		super.onResume();
		Bundle bundle = getArgument();
		mAllMoney = bundle.getFloat("money");
		isAllTake = bundle.getInt("isAllTake");
		mAlipayId = bundle.getString("alipayId");
		mAlipayName = bundle.getString("alipayName");
		mNeedMoney = 0;
		if (isAllTake==1){
			mWithdraw.setEnabled(true);
		}else{
			mWithdraw.setEnabled(false);
		}
		mWithdraw.setBackgroundResource(R.drawable.btn_color_gray);
		judgeEnabled(mAllMoney, isAllTake, mAlipayId, mAlipayName);

	}

	/**
	 * 判断金额是多少
	 */
	private void judgeEnabled(float money, int allTake, String alipayId, String aplipay) {

		if (money < 50) {
			btnMoney50.setEnabled(false);
			btnMoney50.setTextColor(getResources().getColor(R.color.sbc_header_text));
		}
		if (money < 40) {
			btnMoney40.setEnabled(false);
			btnMoney40.setTextColor(getResources().getColor(R.color.sbc_header_text));
		}
		if (money < 30) {
			btnMoney30.setEnabled(false);
			btnMoney30.setTextColor(getResources().getColor(R.color.sbc_header_text));
		}
		if (money < 20) {
			btnMoney20.setEnabled(false);
			btnMoney20.setTextColor(getResources().getColor(R.color.sbc_header_text));
		}
		if (money < 10) {
			btnMoney10.setEnabled(false);
			btnMoney10.setTextColor(getResources().getColor(R.color.sbc_header_text));
		}

		if (isAllTake == 1) {
			btnMoneyAll.setEnabled(true);
			btnMoneyAll.setSelected(true);
			btnMoneyOld = btnMoneyAll;
			mNeedMoney=mAllMoney;
			mWithdraw.setBackgroundResource(R.drawable.btn_color_green);
		} else {
			btnMoneyAll.setEnabled(false);
			btnMoneyAll.setTextColor(getResources().getColor(R.color.sbc_header_text));
		}
	}

	private void initView() {

		mText = (TextView) findViewById(R.id.text);

		btnMoney10 = (Button) findViewById(R.id.btn_money10);
		btnMoney20 = (Button) findViewById(R.id.btn_money20);
		btnMoney30 = (Button) findViewById(R.id.btn_money30);
		btnMoney40 = (Button) findViewById(R.id.btn_money40);
		btnMoney50 = (Button) findViewById(R.id.btn_money50);
		btnMoneyAll = (Button) findViewById(R.id.btn_moneyall);

		mZfbAccounts = (EditText) findViewById(R.id.zfb_accounts);
		mZfbAccountTo = (EditText) findViewById(R.id.zfb_accounts_to);
		mZfbName = (EditText) findViewById(R.id.zfb_name);

		mZfbName.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (mNeedMoney > 0 && !TextUtils.isEmpty(mZfbName.getText()) && !TextUtils.isEmpty(mZfbAccounts.getText())
						&& !TextUtils.isEmpty(mZfbAccountTo.getText())
						&& mZfbAccounts.getText().toString().equals(mZfbAccountTo.getText().toString())) {
					mWithdraw.setBackgroundResource(R.drawable.btn_color_green);
					mWithdraw.setEnabled(true);
				} else {

					mWithdraw.setBackgroundResource(R.drawable.btn_color_gray);
				}
			}
		});

		mZfbAccountTo.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (mNeedMoney > 0 && !TextUtils.isEmpty(mZfbName.getText()) && !TextUtils.isEmpty(mZfbAccounts.getText())
						&& !TextUtils.isEmpty(mZfbAccountTo.getText())
						&& mZfbAccounts.getText().toString().equals(mZfbAccountTo.getText().toString())) {
					mWithdraw.setBackgroundResource(R.drawable.btn_color_green);
					mWithdraw.setEnabled(true);
				} else {

					mWithdraw.setBackgroundResource(R.drawable.btn_color_gray);
				}
			}
		});

		mZfbAccounts.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (mNeedMoney > 0 && !TextUtils.isEmpty(mZfbName.getText()) && !TextUtils.isEmpty(mZfbAccounts.getText())
						&& !TextUtils.isEmpty(mZfbAccountTo.getText())
						&& mZfbAccounts.getText().toString().equals(mZfbAccountTo.getText().toString())) {
					mWithdraw.setBackgroundResource(R.drawable.btn_color_green);
					mWithdraw.setEnabled(true);
				} else {

					mWithdraw.setBackgroundResource(R.drawable.btn_color_gray);
				}
			}
		});
		mWithdraw = (Button) findViewById(R.id.btn_withdraw_deposit1);
		mWithdraw.setOnClickListener(this);
		btnMoneyAll.setOnClickListener(this);
		btnMoney10.setOnClickListener(this);
		btnMoney20.setOnClickListener(this);
		btnMoney30.setOnClickListener(this);
		btnMoney40.setOnClickListener(this);
		btnMoney50.setOnClickListener(this);

	}

	private void initTitleBarView() {
		mTitlebar = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mTitlebar.setTitle(ResUtil.getString(R.string.zfb_withdeaw_deposit));
		mTitlebar.setLeftDefault(this);
		mTitlebar.setTitleBarBackgroundColor(getResources().getColor(android.R.color.transparent));
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_withdraw_deposit1:
			checkInputMoney();
			break;
		case R.id.btn_money10:
			chooseMoney(v, 10f);
			btnMoney10.setTextColor(getResources().getColor(R.color.btn_green));
			break;
		case R.id.btn_money20:
			chooseMoney(v, 20f);
			btnMoney20.setTextColor(getResources().getColor(R.color.btn_green));
			break;
		case R.id.btn_money30:
			chooseMoney(v, 30f);
			btnMoney30.setTextColor(getResources().getColor(R.color.btn_green));
			break;
		case R.id.btn_money40:
			chooseMoney(v, 40f);
			btnMoney40.setTextColor(getResources().getColor(R.color.btn_green));
			break;
		case R.id.btn_money50:
			chooseMoney(v, 50f);
			btnMoney50.setTextColor(getResources().getColor(R.color.btn_green));
			break;
		case R.id.btn_moneyall:
			chooseMoney(v, mAllMoney);
			break;
		default:
			break;
		}
	}

	private void chooseMoney(View v, float money) {
		if (btnMoneyOld != null) {
			btnMoneyOld.setTextColor(getResources().getColor(R.color.contents_text));
			btnMoneyOld.setSelected(false);
		}
		v.setSelected(true);
		btnMoneyOld = (Button) v;
		mNeedMoney = money;

		if (mNeedMoney > 0 && !TextUtils.isEmpty(mZfbName.getText()) && !TextUtils.isEmpty(mZfbAccounts.getText())
				&& !TextUtils.isEmpty(mZfbAccountTo.getText()) && mZfbAccounts.getText().toString().equals(mZfbAccountTo.getText().toString())) {
			mWithdraw.setBackgroundResource(R.drawable.btn_color_green);
			mWithdraw.setEnabled(true);
		} else {
			mWithdraw.setEnabled(false);
			mWithdraw.setBackgroundResource(R.drawable.btn_color_gray);
		}

	}

	// 传递数据会零钱首页
	private void checkInputMoney() {
		if (mNeedMoney == 0) {
			Toast.makeText(mContext, "请点击金额", Toast.LENGTH_LONG).show();
		} else if (!TextUtils.isEmpty(mZfbName.getText()) && !TextUtils.isEmpty(mZfbAccounts.getText())
				&& !TextUtils.isEmpty(mZfbAccountTo.getText())) {
			if (!mZfbAccounts.getText().toString().equals(mZfbAccountTo.getText().toString())) {// 验证账号是否一致
				Toast.makeText(mContext, "亲,两次输入不一样!", Toast.LENGTH_LONG).show();
			} else {
				requestMoney();
			}
		} else {
			Toast.makeText(mContext, "帐号和姓名不能为空", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 获取接口数据
	 */
	private void requestMoney() {
		ObjectLoader<String> loader = new ObjectLoader<String>();
		loader.putPostValue("alipayId", mZfbAccounts.getText().toString());
		loader.putPostValue("alipayName", mZfbName.getText().toString());
		loader.putPostValue("money", String.valueOf(mNeedMoney));
		loader.setNeedCache(false);
		loader.loadBodyObject(String.class, ConfigUrlMrg.ZFB_BINDALIPAY, loader.new CallBack() {
			@Override
			public void onBodyObjectSuccess(boolean isFromCache, String obj) {
				super.onBodyObjectSuccess(isFromCache, obj);
				Bundle bundle = new Bundle();
				bundle.putString("money", String.valueOf(mNeedMoney));
				if (mNeedMoney > 0 || (isAllTake == 1)) {
					moneyDialog(bundle);
				}

			}

		});
	}

	private void moneyDialog(final Bundle bundle) {
		CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
		builder.setTitle("温馨提示!");
		builder.setMessage(mAlertMemo);
		builder.setNegativeButton("我知道了", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialoginterface, int i) {
				FragmentMrg.popSingleFragment(getActivity(), AccountBalanceFragment.class, bundle, true);
			}
		});
		builder.create().show();
	}
}
