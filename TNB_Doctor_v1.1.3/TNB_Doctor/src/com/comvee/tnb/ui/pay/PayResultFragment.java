package com.comvee.tnb.ui.pay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.ui.privatedoctor.DoctorServerList;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UITool;

/**
 * 下订单后结果页
 * 
 * @author Administrator
 * 
 */
public class PayResultFragment extends BaseFragment implements OnClickListener {
	private GridView gridView;
	private int type;// 1 成功 0 失败
	private TextView tv_date, tv_time, tv_order_num, tv_order_type, tv_money, tv_name;
	private ImageView img_order_type;
	private Button btn1, btn2;
	private String orderNum, orderName, orderTime;
	private double orderMoney;
	private TitleBarView mBarView;
	private ArrayList<Map<String, Integer>> arrayList;

	public PayResultFragment(String orderNum, String orderName, double orderMoney, String orderTime, int type) {
		this.type = type;
		this.orderMoney = orderMoney;
		this.orderName = orderName;
		this.orderNum = orderNum;
		this.orderTime = orderTime;
	}

	@Override
	public int getViewLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.pay_result_fragment;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		init();
		initData();
	}

	private void init() {
		mBarView.setTitle(getString(R.string.pay_result));
		gridView = (GridView) findViewById(R.id.graidview_pay_result);
		tv_date = (TextView) findViewById(R.id.tv_date);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_order_num = (TextView) findViewById(R.id.tv_order_num);
		tv_order_type = (TextView) findViewById(R.id.tv_order_type);
		tv_money = (TextView) findViewById(R.id.tv_money);
		tv_name = (TextView) findViewById(R.id.tv_name);
		btn1 = (Button) findViewById(R.id.btn1);
		btn2 = (Button) findViewById(R.id.btn2);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		img_order_type = (ImageView) findViewById(R.id.img_order_type);
	}

	private void initData() {
		getData();
		gridView.setNumColumns(arrayList.size());
		gridView.setLayoutParams(new LinearLayout.LayoutParams(UITool.dip2px(getApplicationContext(), 12) * arrayList.size()
				- UITool.dip2px(getContext(), 4), LayoutParams.WRAP_CONTENT));
		gridView.setAdapter(new SimpleAdapter(getContext(), arrayList, R.layout.item_pay_result_grid, new String[0], new int[0]));
		if (type == 1) {

			tv_order_type.setText(Html.fromHtml("订单<font color=" + TNBApplication.getInstance().getResources().getColor(R.color.pay_ok)
					+ ">支付完成</font>，感谢您的使用"));
			img_order_type.setBackgroundResource(R.drawable.ddjg_01);
			btn2.setText("继续购买");
		} else {
			tv_order_type.setText(Html.fromHtml(String.format(
					"您的订单<font color=" + TNBApplication.getInstance().getResources().getColor(R.color.pay_fail) + ">未支付成功</font>，请重试！", "")));
			img_order_type.setBackgroundResource(R.drawable.ddjg_03);
			btn2.setText("重新支付");
		}
		tv_money.setText("￥" + String.format("%1$.2f", orderMoney / 100));
		tv_name.setText(orderName);
		tv_order_num.setText(orderNum);
		if (orderTime != null && !"".equals(orderTime)) {
			String[] str = orderTime.split(" ");
			if (str.length == 2) {
				tv_date.setText(str[0]);
				tv_time.setText(str[1]);
			} else if (str.length == 1) {
				tv_date.setText(str[0]);
			}
		}
	}

	private ArrayList<Map<String, Integer>> getData() {
		final int W = (int) UITool.getDisplayWidth(getActivity());
		int mag = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getActivity().getResources().getDisplayMetrics());
		int viewWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getActivity().getResources().getDisplayMetrics());
		int len = (W - mag) / viewWidth;
		arrayList = new ArrayList<Map<String, Integer>>();
		for (int i = 0; i < len - 1; i++) {
			arrayList.add(new HashMap<String, Integer>());
		}
		return arrayList;

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn1:
			PayMineOrderFragment.toFragment(getActivity(), 3, true);
			break;
		case R.id.btn2:
			if (type == 1) {
				DoctorServerList.isRequest = true;
				FragmentMrg.toBack(getActivity());
				FragmentMrg.toBack(getActivity());
			} else {
				PayMrg payMrg = PayMrg.getIntance(this);
				payMrg.setOrderMoney(orderMoney);
				payMrg.setOrderName(orderName);
				payMrg.setOrderTime(orderTime);
				payMrg.setOrderNum(orderNum);
				payMrg.requestSignMsg();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onBackPress() {
		PayMineOrderFragment.toFragment(getActivity(), 3, true);
		return true;
	}
}
