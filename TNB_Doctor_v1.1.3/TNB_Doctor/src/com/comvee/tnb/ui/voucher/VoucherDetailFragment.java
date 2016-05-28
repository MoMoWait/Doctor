package com.comvee.tnb.ui.voucher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.network.NetworkCallBack;
import com.comvee.tnb.network.VoucherShareDataHelper;
import com.comvee.tnb.ui.ask.DocListFragment;
import com.comvee.tnb.ui.book.ShareUtil;
import com.comvee.tnb.ui.more.VoucherShareModel;
import com.comvee.tnb.widget.TitleBarView;

/**
 * 卡券详情
 *
 * @author Administrator
 */
public class VoucherDetailFragment extends BaseFragment implements OnClickListener {
    private VoucherModel voucherModel;
    private ShareUtil shareUtil;
    private TextView mStateText;
    private LinearLayout mLayout;

    private String mRemark;
    private String mPlatForm;
    private TitleBarView titlebar;

    public VoucherDetailFragment(VoucherModel voucherModel) {
        this.voucherModel = voucherModel;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.voucher_detail_fragment;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        titlebar = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mLayout = (LinearLayout) findViewById(R.id.state_layout);
        titlebar.setTitle("礼券祥情");
        titlebar.setLeftDefault(this);

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        shareUtil = ShareUtil.getInstance(getActivity());
        super.onLaunch();
        VoucherShareDataHelper helper = new VoucherShareDataHelper();
        helper.putPostValue("couponTemplateId", voucherModel.couponTemplateId);
        helper.requestShareMsg(new NetworkCallBack() {


            @Override
            public void callBack(int what, int status, Object obj) {
                if (obj != null && obj instanceof VoucherShareModel) {
                    initDate(obj);

                }
            }
        }, voucherModel.promotionId);
        ((TextView) findViewById(R.id.name)).setText(voucherModel.name);
        ((TextView) findViewById(R.id.price)).setText(voucherModel.price + "");
        String dateUtil;
        try {
            dateUtil = String.format("有效期：%s至%s;", sdf2.format(sdf1.parse(voucherModel.from)), sdf2.format(sdf1.parse(voucherModel.to)));
            ((TextView) findViewById(R.id.valid_time)).setText(dateUtil);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void initDate(Object obj) {
        VoucherShareModel model = (VoucherShareModel) obj;
        shareUtil.setTitle(model.shareTitle);
        shareUtil.setUrl(model.shareAddr);
        shareUtil.setTitleUrl(model.shareAddr);
        mPlatForm = model.platForm;


        if (!mPlatForm.equals("2")) {
            titlebar.setRightButton("分享", VoucherDetailFragment.this);
            shareUtil.setShareImgUrl(model.shareImg);
            shareUtil.setShareText(model.shareContent);
            mRemark = model.remark;
        }
        mRemark = model.remark;
        String[] text = mRemark.split("\\^");
        TextView textView;
        for (int i = 0; i < text.length; i++) {
            textView = new TextView(getApplicationContext());
            textView.setText((i + 1) + "." + text[i]);
            textView.setTextColor(getResources().getColor(R.color.sbc_view));
            textView.setTextSize(14);
            mLayout.addView(textView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TitleBarView.ID_RIGHT_BUTTON:

                shareUtil.show(findViewById(R.id.main_titlebar_view), Gravity.BOTTOM);
                break;

            default:
                break;
        }
    }

}
