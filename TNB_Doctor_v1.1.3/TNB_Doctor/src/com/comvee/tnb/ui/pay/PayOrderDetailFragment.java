package com.comvee.tnb.ui.pay;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.OrderInfo;
import com.comvee.tnb.model.OrderItemInfo;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.util.TimeUtil;

/**
 * 订单详细页
 * 
 * @author Administrator
 * 
 */
@SuppressLint("ValidFragment")
public class PayOrderDetailFragment extends BaseFragment implements OnHttpListener, OnClickListener {

	private OrderInfo mOrderInfo;
	private LinearLayout mLayoutLocation;
	private TextView tvKd;// 快递
	private TextView tvKdCode;
	private TitleBarView mBarView;

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_order_detail;
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	public PayOrderDetailFragment() {
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		mBarView.setTitle(getString(R.string.title_mine_order_detail));
		init();
		requestOrderDetail();
	}

	public static PayOrderDetailFragment newInstance(OrderInfo mOrderInfo) {
		return new PayOrderDetailFragment(mOrderInfo);
	}

	@SuppressLint("ValidFragment")
	public PayOrderDetailFragment(OrderInfo mOrderInfo) {
		this.mOrderInfo = mOrderInfo;
	}

	private void init() {

		tvKd = (TextView) findViewById(R.id.tv_kd_name);
		tvKdCode = (TextView) findViewById(R.id.tv_kd_code);

		findViewById(R.id.btn_call_me).setOnClickListener(this);

		mLayoutLocation = (LinearLayout) findViewById(R.id.layout_locations);
		TextView tvOrderCode = (TextView) findViewById(R.id.tv_order_code);
		TextView tvOrderTime = (TextView) findViewById(R.id.tv_order_time);
		TextView tvOrderMoney = (TextView) findViewById(R.id.tv_order_money);
		TextView tvOrderTotalNum = (TextView) findViewById(R.id.tv_order_totalnum);

		tvOrderCode.setText("订单号：" + mOrderInfo.orderNum);
		tvOrderTime.setText(mOrderInfo.insertDt);
		tvOrderMoney.setText("总价：¥" + mOrderInfo.payMoney);
		tvOrderTotalNum.setText("总数：" + mOrderInfo.totalNum);
		LinearLayout layoutItem = (LinearLayout) findViewById(R.id.layout_item);
		layoutItem.removeAllViews();
		for (int i = 0; i < mOrderInfo.list.size(); i++) {
			inflateFactoryItem(mOrderInfo.list.get(i), layoutItem);
		}

	}

	private void inflateFactoryItem(OrderItemInfo info, LinearLayout layout) {
		View view = View.inflate(mContext, R.layout.item_order_item, layout);
		ImageView ivPhoto = (ImageView) view.findViewById(R.id.iv_photo);
		TextView tvOrderName = (TextView) view.findViewById(R.id.tv_order_name);
		TextView tvOrderMoney = (TextView) view.findViewById(R.id.tv_order_money);
		// TextView tvOrderNum = (TextView)
		// view.findViewById(R.id.tv_order_num);

		tvOrderMoney.setText("¥" + info.money);
		tvOrderName.setText(info.packageName);
		// tvOrderNum.setText("x" + info.num);
		ImageLoaderUtil.getInstance(mContext).displayImage(info.packageUrl, ivPhoto, ImageLoaderUtil.default_options);
	}

	private String lastTime;

	private void inflateLocationItem(String time, String msg, LinearLayout layout, int posiont, int count) {
		try {

			String tempTime = time.substring(0, time.indexOf(" "));

			if (!tempTime.equals(lastTime)) {
				View viewTime = View.inflate(mContext, R.layout.item_order_location_time, null);
				TextView tvDate = (TextView) viewTime.findViewById(R.id.tv_date);
				if (TimeUtil.isSameDay(TimeUtil.getUTC(time, ConfigParams.TIME_FORMAT1), System.currentTimeMillis())) {
					tvDate.setText("今日");
				} else {
					tvDate.setText(tempTime);
				}
				lastTime = tempTime;
				layout.addView(viewTime, -1, -2);
			}

			View view = View.inflate(mContext, R.layout.item_order_location, null);

			ImageView ivPoint = (ImageView) view.findViewById(R.id.iv_point);
			TextView tvLocationTime = (TextView) view.findViewById(R.id.tv_location_time);
			TextView tvLocationInfo = (TextView) view.findViewById(R.id.tv_location_info);

			tvLocationTime.setText(time);
			tvLocationInfo.setText(msg);

			int textColor = 0;
			if (posiont == 0) {
				view.findViewById(R.id.layout_order_loc).setBackgroundResource(R.drawable.order_detail_location_bg1);
				ivPoint.setImageResource(R.drawable.order_detail_point_1);
				textColor = getResources().getColor(R.color.theme_color_green);
			} else if (posiont == count - 1) {
				ivPoint.setImageResource(R.drawable.order_detail_point_3);
				textColor = Color.parseColor("#999999");
			} else {
				ivPoint.setImageResource(R.drawable.order_detail_point_2);
				textColor = Color.parseColor("#999999");
			}
			tvLocationTime.setTextColor(textColor);
			tvLocationInfo.setTextColor(textColor);
			layout.addView(view, -1, -2);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void requestOrderDetail() {
		showProgressDialog(getString(R.string.msg_loading));
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.ORDER_DETAIL);
		http.setPostValueForKey("orderId", mOrderInfo.orderId);
		http.setOnHttpListener(1, this);
		http.startAsynchronous();
	}

	private void paseOrderDeatil(byte[] b) throws Exception {
		ComveePacket packet = ComveePacket.fromJsonString(b);
		if (packet.getResultCode() == 0) {
			JSONArray array = packet.getJSONObject("body").getJSONObject("logisticDetial").optJSONArray("data");
			int len = array.length();
			for (int i = 0; i < len; i++) {
				JSONObject obj = array.getJSONObject(i);
				inflateLocationItem(obj.optString("time"), obj.optString("context"), mLayoutLocation, i, len);
			}
			String kd = packet.getJSONObject("body").getJSONObject("order").optString("logisticsFirmName");
			tvKd.setText(TextUtils.isEmpty(kd) ? "暂无物流信息" : kd);
			String kdCode = packet.getJSONObject("body").getJSONObject("order").optString("logisticsNum");
			tvKdCode.setText(TextUtils.isEmpty(kdCode) ? "" : "运单编号：" + kdCode);

		} else {
			ComveeHttpErrorControl.parseError(getActivity(), packet);
		}
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		try {
			cancelProgressDialog();
			paseOrderDeatil(b);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFialed(int what, int errorCode) {
		cancelProgressDialog();
		ComveeHttpErrorControl.parseError(getActivity(), errorCode);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_call_me:
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4006161313"));
			getActivity().startActivity(intent);
			break;

		default:
			break;
		}
	}
}
