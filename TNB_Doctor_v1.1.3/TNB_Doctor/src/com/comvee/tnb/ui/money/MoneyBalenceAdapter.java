package com.comvee.tnb.ui.money;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.BaseApplication;
import com.comvee.ComveeBaseAdapter;
import com.comvee.tnb.R;
import com.comvee.tnb.model.BalanceStatementInfo;
import com.comvee.tool.ImageLoaderUtil;

/**
 * 明细列表的数据
 * 
 * @author yujun
 * 
 */
@SuppressLint("ResourceAsColor")
public class MoneyBalenceAdapter extends ComveeBaseAdapter<BalanceStatementInfo> {

	public MoneyBalenceAdapter(Context ctx, int layoutId) {
		super(ctx, layoutId);
		// TODO Auto-generated constructor stub
	}

	public MoneyBalenceAdapter() {
		super(BaseApplication.getInstance(), R.layout.money_balance_list);
	}

	@Override
	protected void getView(com.comvee.ComveeBaseAdapter.ViewHolder holder, int position) {

		BalanceStatementInfo info = getItem(position);
		// 金额
		TextView tvLabel = holder.get(R.id.money);
		// 成员姓名
		TextView memberName = holder.get(R.id.memberName);
		// 转入类型
		ImageView rewards = holder.get(R.id.rewards_img);

		if (!TextUtils.isEmpty(info.moneyType.toString())&&!TextUtils.isEmpty(info.recordType.toString())&&!TextUtils.isEmpty(info.money.toString())) {

			try {
				if (info.moneyType.equals("2")) {
                    rewards.setBackgroundResource(R.drawable.jiance_40);
					//ImageLoaderUtil.getInstance(BaseApplication.getInstance()).displayImage(list.pic, mImgPic, ImageLoaderUtil.default_options);
                    memberName.setText("提现至支付宝");
                    tvLabel.setText("-" + info.money.toString());
                    tvLabel.setTextColor(context.getResources().getColor(R.color.blue_money1));
                } else {
                    rewards.setBackgroundResource(R.drawable.jiance_38);
                    memberName.setText(info.content);
                    tvLabel.setText("+" + info.money + "");
                    tvLabel.setTextColor(context.getResources().getColor(R.color.text_reds));
                }
				// 时间
				if (!TextUtils.isEmpty(info.insertDt)) {
                    TextView insertDt = holder.get(R.id.insertDt);
                    insertDt.setText(info.insertDt);
                }
			} catch (Resources.NotFoundException e) {
				e.printStackTrace();
			}

		} else {
		}
	}
}
