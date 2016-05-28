package com.comvee.tnb.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.common.MyBaseAdapter;
import com.comvee.tnb.model.PackageModel;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.ViewHolder;

public class DocServerListAdapter extends MyBaseAdapter<PackageModel> {
	private boolean isShowRecom;

	public DocServerListAdapter(Context ctx, List<PackageModel> datas, int mayoutid) {
		super(ctx, datas, mayoutid);
	}

	public void setShowRecom(boolean isShowRecom) {
		this.isShowRecom = isShowRecom;
	}

	@Override
	protected void doyouself(ViewHolder holder, int position) {
		PackageModel model = datas.get(position);
		ImageView photh = (ImageView) holder.get(R.id.doc_server_img);
		TextView name = (TextView) holder.get(R.id.doc_server_item_docname);
		TextView money = (TextView) holder.get(R.id.doc_server_item_money);
		TextView serverText = (TextView) holder.get(R.id.doc_server_item_server);
		if (isShowRecom) {
			name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sirenyishengaz_30, 0);
		}
		if (model.isMyPackage()) {
			name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.yisheng_95, 0);
		} else if (model.getVoucherList() != null && model.getVoucherList().size() > 0) {
			name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.kaquan_27, 0);
		} else {
			name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}

		String moneyValue = Integer.parseInt(model.getFeeNum()) / 100 + "";

		serverText.setText(model.getMemo());
		name.setText(model.getPackageName());
		money.setVisibility(View.VISIBLE);
		money.setText(String.format(context.getText(R.string.doc_server_money_text).toString(), moneyValue));
		ImageLoaderUtil.getInstance(context).displayImage(model.getPackageImg(), photh, ImageLoaderUtil.default_options);
	}

}
