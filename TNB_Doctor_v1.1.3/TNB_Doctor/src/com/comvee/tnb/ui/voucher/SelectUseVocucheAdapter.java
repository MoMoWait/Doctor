package com.comvee.tnb.ui.voucher;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.common.MyBaseAdapter;
import com.comvee.tool.ViewHolder;

public class SelectUseVocucheAdapter extends MyBaseAdapter<VoucherModel> {
	private int select;

	public void setSelect(int select) {
		this.select = select;
	}

	public SelectUseVocucheAdapter(Context ctx, List<VoucherModel> datas, int mayoutid) {
		super(ctx, datas, mayoutid);
	}

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
		ImageView img_select = holder.get(R.id.img_select);
		View viewGroup = holder.get(R.id.viewgroup);
		priceTv.setText(voucherModel.price + "");
		nameTv.setText(voucherModel.name);
		validTv.setText(String.format("有效期：%s至%s", voucherModel.from, voucherModel.to));
		totalTv.setText(String.format("共%d张", voucherModel.totalNum));
		if (select == position) {
			img_select.setImageResource(R.drawable.check_style_1_b);
		} else {
			img_select.setImageResource(R.drawable.check_style_1_a);
		}
//		if (voucherModel.status == 1) {// 未使用
			viewGroup.setBackgroundResource(R.drawable.kaquan_2);
			quanTxTv.setTextColor(Color.parseColor("#ff9000"));
			signTv.setTextColor(Color.parseColor("#ff9000"));
			priceTv.setTextColor(Color.parseColor("#ff9000"));
			nameTv.setTextColor(Color.parseColor("#333333"));
//		} else {// 已使用和已过期
//			viewGroup.setBackgroundResource(R.drawable.kaquan_3);
//			quanTxTv.setTextColor(Color.parseColor("#999999"));
//			signTv.setTextColor(Color.parseColor("#999999"));
//			priceTv.setTextColor(Color.parseColor("#999999"));
//			nameTv.setTextColor(Color.parseColor("#999999"));
//		}
	}

}
