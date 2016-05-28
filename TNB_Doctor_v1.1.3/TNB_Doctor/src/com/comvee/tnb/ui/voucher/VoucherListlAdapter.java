package com.comvee.tnb.ui.voucher;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.common.MyBaseAdapter;
import com.comvee.tool.ViewHolder;

public class VoucherListlAdapter extends MyBaseAdapter<VoucherModel> {
	private int type;

	public VoucherListlAdapter(Context ctx, List<VoucherModel> datas, int mayoutid, int type_) {
		super(ctx, datas, mayoutid);
		this.type = type_;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	@SuppressLint("NewApi")
	@Override
	protected void doyouself(ViewHolder holder, int position) {
		VoucherModel voucherModel = datas.get(position);
		TextView quanTxTv = holder.get(R.id.quan_tx);
		holder.get(R.id.line).setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		TextView signTv = holder.get(R.id.temp_sign);
		TextView priceTv = holder.get(R.id.price);
		TextView nameTv = holder.get(R.id.name);
		TextView validTv = holder.get(R.id.valid_time);
		TextView totalTv = holder.get(R.id.total);
		View viewGroup = holder.get(R.id.viewgroup);
		priceTv.setText(voucherModel.price + "");
		nameTv.setText(voucherModel.name);

		if (type == 3) {
			validTv.setText(String.format("使用时间：%s", voucherModel.useDt.split(" ")[0]));
		} else {
			validTv.setText(String.format("有效期：%s至%s", voucherModel.from, voucherModel.to));
		}
		totalTv.setText(String.format("共%d张", voucherModel.totalNum));
		if (type == 1) {// 未使用
			viewGroup.setBackgroundResource(R.drawable.kaquan_2);
			quanTxTv.setTextColor(Color.parseColor("#ff9000"));
			signTv.setTextColor(Color.parseColor("#ff9000"));
			priceTv.setTextColor(Color.parseColor("#ff9000"));
			nameTv.setTextColor(Color.parseColor("#333333"));
			validTv.setTextColor(Color.parseColor("#999999"));
			totalTv.setTextColor(Color.parseColor("#999999"));
		} else {// 已使用和已过期
			viewGroup.setBackgroundResource(R.drawable.kaquan_3);
			quanTxTv.setTextColor(Color.parseColor("#cccccc"));
			signTv.setTextColor(Color.parseColor("#cccccc"));
			priceTv.setTextColor(Color.parseColor("#cccccc"));
			nameTv.setTextColor(Color.parseColor("#cccccc"));
			validTv.setTextColor(Color.parseColor("#cccccc"));
			totalTv.setTextColor(Color.parseColor("#cccccc"));
		}

	}
}