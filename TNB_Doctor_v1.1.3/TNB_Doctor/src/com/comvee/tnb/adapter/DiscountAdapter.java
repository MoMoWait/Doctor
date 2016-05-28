package com.comvee.tnb.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.model.IndexVoucherModel.DiscountItem;
import com.comvee.util.TimeUtil;

public class DiscountAdapter extends BaseAdapter {
	private ArrayList<DiscountItem> list;
	private Context context;
	private DiscountItem model;

	private DiscountAdapter() {
		// TODO Auto-generated constructor stub
	}

	public static DiscountAdapter getInstance(Context context) {
		DiscountAdapter adapter = new DiscountAdapter();
		adapter.setContext(context);
		return adapter;
	}

	private void setContext(Context context) {
		this.context = context;
	}

	public void setList(ArrayList<DiscountItem> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list == null ? 0 : list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return list == null ? 0 : position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		model = list.get(position);
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.discount_item_layout, null);
		}
		TextView tv_price = (TextView) convertView.findViewById(R.id.tv_price);
		TextView tv_number = (TextView) convertView.findViewById(R.id.tv_number);
		TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
		TextView tv_time = (TextView) convertView.findViewById(R.id.tv_time);
		tv_name.setText(model.getCouponsName());
		if (model.getNumber() > 1) {
			tv_number.setText(String.format(context.getText(R.string.discount_item_num).toString(), model.getNumber()));
		} else {
			tv_number.setText("");
		}
		tv_price.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/IMPACT.TTF"));
		tv_price.setText(model.getPreferentialPrice() + "");
		try {
			tv_time.setText(String.format(context.getText(R.string.discount_item_time).toString(),
					TimeUtil.fomateTime("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", model.getStartDt()),
					TimeUtil.fomateTime("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", model.getEndDt())));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertView;
	}
}
