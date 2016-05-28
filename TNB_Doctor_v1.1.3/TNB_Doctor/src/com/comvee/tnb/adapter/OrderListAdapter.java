package com.comvee.tnb.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.model.OrderInfo;
import com.comvee.tnb.model.OrderItemInfo;
import com.comvee.tool.ImageLoaderUtil;

public class OrderListAdapter extends BaseAdapter {

	private ArrayList<OrderInfo> mListItem;
	private Context mContext;
	private OnClickListener mListener;
	private int mOrderType = 1;// 1、已支付0、未支付

	public void setOrderType(int type) {
		mOrderType = type;
	}

	public OrderListAdapter(Context cxt) {
		mContext = cxt;
	}

	public void setOnClickListener(OnClickListener listener) {
		mListener = listener;
	}

	public void setListItems(ArrayList<OrderInfo> mListItem) {
		this.mListItem = mListItem;
	}

	@Override
	public int getCount() {
		return mListItem == null ? 0 : mListItem.size();
	}

	@Override
	public OrderInfo getItem(int arg0) {
		return mListItem.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	private void inflateItem(OrderItemInfo info, LinearLayout layout) {
		View view = View.inflate(mContext, R.layout.item_order_item, layout);
		ImageView ivPhoto = (ImageView) view.findViewById(R.id.iv_photo);
		TextView tvOrderName = (TextView) view.findViewById(R.id.tv_order_name);
		TextView tvOrderMoney = (TextView) view.findViewById(R.id.tv_order_money);
		// TextView tvOrderNum = (TextView)
		// view.findViewById(R.id.tv_order_num);

		tvOrderMoney.setText("价格： ¥" + info.money + "    数量： " + info.num);
		tvOrderName.setText(info.packageName);
		// tvOrderNum.setText("数量： " + info.num);
		if (TextUtils.isEmpty(info.packagecode)) {
			ImageLoaderUtil.getInstance(mContext).displayImage(info.packageUrl, ivPhoto, ImageLoaderUtil.pay_options);
		} else if (info.packagecode.equals("SRYSTC")) {
			ivPhoto.setImageResource(R.drawable.sirenyishengaz_20);

		} else if (info.packagecode.equals("TWZXTC")) {
			ivPhoto.setImageResource(R.drawable.sirenyishengaz_22);

		} else if (info.packagecode.equals("DHZXTC")) {
			ivPhoto.setImageResource(R.drawable.sirenyishengaz_24);

		} else {
			ImageLoaderUtil.getInstance(mContext).displayImage(info.packageUrl, ivPhoto, ImageLoaderUtil.pay_options);
		}
	}

	@Override
	public View getView(int position, View containView, ViewGroup arg2) {

		if (null == containView) {
			containView = View.inflate(mContext, R.layout.item_order, null);
		}

		OrderInfo info = mListItem.get(position);

		TextView tvOrderCode = (TextView) containView.findViewById(R.id.tv_order_code);
		// TextView tvOrderTime = (TextView)
		// containView.findViewById(R.id.tv_order_time);
		TextView tvOrderMoney = (TextView) containView.findViewById(R.id.tv_order_total_money);
		// TextView tvOrderTotalNum = (TextView)
		// containView.findViewById(R.id.tv_order_totalnum);

		LinearLayout layoutItem = (LinearLayout) containView.findViewById(R.id.layout_item);
		layoutItem.removeAllViews();
		for (int i = 0; i < info.list.size(); i++) {
			inflateItem(info.list.get(i), layoutItem);
		}

		tvOrderCode.setText("订单号：" + info.orderNum);
		// tvOrderTime.setText(info.insertDt);
		tvOrderMoney.setText("总价：¥" + info.payMoney);
		// tvOrderTotalNum.setText("数量：" + info.totalNum);

		TextView btnPay = (TextView) containView.findViewById(R.id.btn_oder_pay);
		View btnRemove = containView.findViewById(R.id.btn_remove);
		// btnRemove.setVisibility(mOrderType != 0 ? View.GONE : View.VISIBLE);
		btnPay.setTag(info);
		btnPay.setOnClickListener(mListener);
		if (info.payStatus == 0) {
			btnRemove.setTag(info);
			btnPay.setText("去付款");
			btnPay.setBackgroundResource(R.drawable.blue_strok_bg);
			btnRemove.setOnClickListener(mListener);
		} else {
			if (info.type == 2) {
				btnRemove.setTag(info);
				btnRemove.setOnClickListener(mListener);
				btnPay.setText("已付款");
				btnPay.setBackgroundResource(R.drawable.blue_strok_bg);
				btnPay.setOnClickListener(null);
			} else {
				btnPay.setText("查看物流");
				btnPay.setBackgroundResource(R.drawable.blue_strok_bg);
			}
		}

		return containView;
	}

}
