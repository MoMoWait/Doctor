package com.comvee.tnb.ui.money;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.BaseApplication;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.model.MyWalletModel;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.more.WebNewFrag;
import com.comvee.tnb.ui.voucher.VoucherFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.ResUtil;
import com.comvee.tool.UserMrg;

import java.util.ArrayList;

/**
 * 我的钱包页面
 *
 * @author yujun
 */
public class MyWalletFragment extends BaseFragment implements OnClickListener {

    private TitleBarView mTitlebar;
    private LinearLayout mAccountBalance;// 账户余额
    private LinearLayout mVoucher;// 我的卡卷
    private TextView mMoneyText;
    private TextView mMoney;
    private TextView mCardCount;

    private RelativeLayout mShopping;
    private String mTurnUrl;
    private String mTurnType;

    private ImageView mImgPic;
    private TextView mTitleText;

    @Override
    public int getViewLayoutId() {
        return R.layout.my_wallet_fragment;
    }

    public static void toFragment(FragmentActivity fragment, boolean isSliding) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isSliding", isSliding);
        bundle.putInt("from_where", 3);
        boolean isAnima = true;
        if (isSliding) {
            isAnima = false;
        }
        FragmentMrg.toFragment(fragment, MyWalletFragment.class, bundle, false);
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);
        initTitleBarView();
        init();
        initLoader();

    }

    @Override
    public void onFragmentResult(Bundle data) {
        super.onFragmentResult(data);
        initLoader();
    }

    /**
     * 接口回调,获取数据
     */
    private void initLoader() {

        ObjectLoader<MyWalletModel> loader = new ObjectLoader<MyWalletModel>();
        loader.setNeedCache(false);
        loader.loadBodyObject(MyWalletModel.class, ConfigUrlMrg.MONEY_MY_WALLET, loader.new CallBack() {


            @Override
            public void onBodyObjectSuccess(boolean isFromCache, MyWalletModel obj) {
                super.onBodyObjectSuccess(isFromCache, obj);
                if (obj != null) {
                    walletShoppingItem(obj);
                }
            }
        });
    }

    private void init() {
        // 账户余额
        mAccountBalance = (LinearLayout) findViewById(R.id.wallet_account_balance);
        mAccountBalance.setOnClickListener(this);

        mMoneyText = (TextView) findViewById(R.id.wallet_money);
        mMoney = (TextView) findViewById(R.id.wallet_money);
        // 卡卷数
        mCardCount = (TextView) findViewById(R.id.card_count);
        mShopping = (RelativeLayout) findViewById(R.id.wallet_shopping_mall);
        mShopping.setOnClickListener(this);

        mImgPic = (ImageView) findViewById(R.id.img_pic);
        mTitleText = (TextView) findViewById(R.id.title_text);

        mVoucher = (LinearLayout) findViewById(R.id.my_voucher);
        mVoucher.setOnClickListener(this);
    }

    private void initTitleBarView() {
        mTitlebar = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mTitlebar.setTitle(ResUtil.getString(R.string.my_wallet));
        mTitlebar.setLeftDefault(this, R.drawable.top_menu_back_white);
        mTitlebar.setTitleBarBackgroundColor(getResources().getColor(android.R.color.transparent));
        mTitlebar
                .setTextColor(getResources().getColor(R.color.white), getResources().getColor(R.color.white), getResources().getColor(R.color.white));
        mTitlebar.setLineHide();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wallet_account_balance:
                AccountBalanceFragment.toFragment(getActivity(), true);
                break;
            case R.id.my_voucher:
                VoucherFragment.toFragment(this.getActivity(), true);
                break;
            case R.id.wallet_shopping_mall:
                if (!TextUtils.isEmpty(mTurnUrl.toString())) {
                    mTurnUrl += String.format("?sessionID=%s&sessionMemberID=%s", UserMrg.getSessionId(getContext()), UserMrg.getMemberSessionId(getContext()));
                    WebNewFrag.toFragment(getActivity(), "", mTurnUrl);
                }

                break;
            default:
                break;
        }
    }

    private void walletShoppingItem(MyWalletModel obj) {
        showProgressDialog(getString(R.string.msg_loading));
        try {
            if (!TextUtils.isEmpty(obj.money)) {
                mMoney.setText(obj.money);
            }
            if (!TextUtils.isEmpty(obj.card_count)) {
                mCardCount.setText(obj.card_count);
            }

            if (obj.showList.size()>0 && obj.showList != null) {
                ArrayList<MyWalletModel.ShowList> showList = obj.showList;
                mShopping.setVisibility(View.GONE);
                for (int i = 0; i < showList.size(); i++) {
                    MyWalletModel.ShowList list = showList.get(i);
                    //商城图
                    ImageLoaderUtil.getInstance(BaseApplication.getInstance()).displayImage(list.pic, mImgPic, ImageLoaderUtil.default_options);
                    //商城标题
                    mTitleText.setText(list.title.toString());
                    //商城开关
                    mTurnType = list.turn_type;
                    //商城地址
                    mTurnUrl = list.turn_url;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cancelProgressDialog();
    }

    /**
     * 退回
     */
    @Override
    public boolean onBackPress() {

        IndexFrag.toFragment(getActivity(), true);
        return true;

    }
}
