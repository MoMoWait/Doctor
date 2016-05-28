package com.comvee.tnb.ui.money;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.DrawerMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.model.AccountBalancefInfo;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ResUtil;

/**
 * 这是账户余额界面
 *
 * @author yujun
 */
public class  AccountBalanceFragment extends BaseFragment implements OnClickListener {

    private static TextView mMoney;
    private TitleBarView mTitlebar;// 标题栏
    private Button mWithdrawDeposit;// 提现按钮
    // 接口返回数据
    private float mBalancefMoney;
    private String mAlipayId;
    private TextView mNoticeText;
    private String mAlipayName;
    private int mIsAllTake;// 判断活动是否结束,全部提取
    private SharedPreferences sp;

    private boolean isSliding;
    private int fromWhere;// 1表示从左侧栏跳转
    private String mBannerMemo;
    private String mAlertMemo;

    public static void toFragment(FragmentActivity fragment, boolean isSliding) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isSliding", isSliding);
        bundle.putInt("from_where", 3);
        FragmentMrg.toFragment(fragment, AccountBalanceFragment.class, bundle, true);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.anccount_balance_fragment;
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);
        if (dataBundle != null) {
            isSliding = dataBundle.getBoolean("isSliding");
            fromWhere = dataBundle.getInt("from_where");
        }
        if (isSliding) {
            DrawerMrg.getInstance().close();
        }
        initTitleBarView();
        init();
        initLoader();

    }

    @Override
    public void onFragmentResult(Bundle data) {
        // TODO Auto-generated method stub
        super.onFragmentResult(data);
        if (data != null) {
            try {
                initLoader();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 设置按钮的背景
     */
    public void setBtnBackground() {
        if (mBalancefMoney < 10&&mIsAllTake==0) {
            mWithdrawDeposit.setBackgroundResource(R.drawable.btn_color_gray);
        } else if (mIsAllTake==1&&mBalancefMoney>0.0){
            mWithdrawDeposit.setBackgroundResource(R.drawable.btn_color_green);
        }else if (mBalancefMoney>=10&&mIsAllTake==0){
            mWithdrawDeposit.setBackgroundResource(R.drawable.btn_color_green);

        }else{
            mWithdrawDeposit.setBackgroundResource(R.drawable.btn_color_gray);
        }

    }

    /**
     * 获取接口数据
     */
    private void initLoader() {
        ObjectLoader<AccountBalancefInfo> loader = new ObjectLoader<AccountBalancefInfo>();
        loader.setNeedCache(false);
        loader.loadBodyObject(AccountBalancefInfo.class, ConfigUrlMrg.ACCOUNT_BALANCE, loader.new CallBack() {
            @Override
            public void onBodyObjectSuccess(boolean isFromCache, AccountBalancefInfo obj) {
                super.onBodyObjectSuccess(isFromCache, obj);
                try {
                    if (obj != null) {
                        mBalancefMoney = obj.money;
                        //提现成功提示文案
                        mBannerMemo = obj.bannerMemo;
                        mAlertMemo = obj.alertMemo;
                        mMoney.setText(mBalancefMoney + "");
                        mAlipayId = obj.alipayId;
                        mAlipayName = obj.alipayName;
                        mIsAllTake = obj.isAllTake;
                        if (obj.memo.size() > 0) {
                            for (int i = 0; i < obj.memo.size(); i++) {
                                if (i == 0) {
                                    mNoticeText.setText(obj.memo.get(i) + "\n");
                                } else {
                                    mNoticeText.setText(obj.memo.get(i));
                                }
                            }
                        }
                        setBtnBackground();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 控件初始化
     */
    private void init() {
        mWithdrawDeposit = (Button) findViewById(R.id.btn_withdraw_deposit);
        mWithdrawDeposit.setOnClickListener(this);
        mMoney = (TextView) findViewById(R.id.wallet_money);
        mNoticeText = (TextView) findViewById(R.id.notice_text);
    }

    @Override
    public void onResume() {
        super.onResume();
        initLoader();
        setBtnBackground();
    }

    /**
     * 标题栏
     */
    private void initTitleBarView() {
        mTitlebar = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mTitlebar.setTitle(ResUtil.getString(R.string.wallet_account_balance));
        mTitlebar.setLeftDefault(this);
        mTitlebar.setRightButton("余额明细", this);
        mTitlebar.setTitleBarBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_withdraw_deposit:
                //factDate();
                if (mIsAllTake == 0) {//为0活动没有结束
                    if (mBalancefMoney < 10) {
                        // showToast("未达到可提现的金额,最低10元");
                        return;
                    } else {
                        factDate();
                    }
                } else if (mIsAllTake == 1&&mBalancefMoney>0) {//为1活动结束
                    factDate();
                    mWithdrawDeposit.setBackgroundResource(R.drawable.btn_color_green);
                }
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                BalanceStatementFragment.toFragment(getActivity(), 1, true);
                break;
            default:
                break;
        }
    }

    /**
     * 提醒
     */
    private void factDate() {
        Bundle bundle = new Bundle();
        bundle.putFloat("money", mBalancefMoney);
        bundle.putString("alipayId", mAlipayId);
        bundle.putString("alipayName", mAlipayName);
        bundle.putString("bannerMemo", mBannerMemo);
        bundle.putString("alertMemo", mAlertMemo);
        bundle.putInt("isAllTake", mIsAllTake);
        LoginDialogFragment dialogFragment = new LoginDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getFragmentManager(), "DialogFragment");
    }

    /**
     * 退回
     */
    @Override
    public boolean onBackPress() {
        if (fromWhere == 1) {
            MyWalletFragment.toFragment(getActivity(), true);
            return true;
        } else
            return false;
    }
}
