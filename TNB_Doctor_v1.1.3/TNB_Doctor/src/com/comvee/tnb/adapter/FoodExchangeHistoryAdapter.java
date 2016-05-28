package com.comvee.tnb.adapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.ComveeBaseAdapter;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.model.FoodExchangeModel;
import com.comvee.tool.ImageLoaderUtil;

/**
 * 饮食交换历史列表
 * 
 * @author friendlove-pc
 *
 */
public class FoodExchangeHistoryAdapter extends ComveeBaseAdapter<FoodExchangeModel> {
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
	SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
	SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm", Locale.CHINA);

	public FoodExchangeHistoryAdapter() {
		super(TNBApplication.getInstance(), R.layout.item_food_exchange_history);
	}

	@Override
	protected void getView(ViewHolder holder, int position) {
		FoodExchangeModel model = getItem(position);
		TextView timeTv = holder.get(R.id.time);
		TextView select_name = holder.get(R.id.tv_select_name);
		TextView select_weight = holder.get(R.id.tv_select_weight);
		TextView heat = holder.get(R.id.tv_heat);
		TextView exchange_name = holder.get(R.id.tv_exchange_name);
		TextView exchange_weight = holder.get(R.id.tv_exchange_weight);
		ImageView img_select = holder.get(R.id.img_select);
		ImageView img_exchange = holder.get(R.id.img_exchange);
		timeTv.setText(converDate(model.INSDATE));
		select_name.setText(model.oldname);
		select_weight.setText(model.oldweight + " g");
		heat.setText(model.heat + " kcal");
		exchange_name.setText(model.newname);
		exchange_weight.setText(model.newweight + " g");
		ImageLoaderUtil.getInstance(context).displayImage(model.oldPICURL, img_select, ImageLoaderUtil.default_options);
		ImageLoaderUtil.getInstance(context).displayImage(model.newPICURL, img_exchange, ImageLoaderUtil.default_options);
	}

	private String converDate(String date) {
		try {
			Calendar c1 = Calendar.getInstance();
			c1.setTime(sdf1.parse(date));
			Calendar c2 = Calendar.getInstance();
			if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
				if (c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)) {
					return "今天 " + sdf3.format(c1.getTime());
				} else if (c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR) == 1) {
					return "昨天 " + sdf3.format(c1.getTime());
				}
			}
			return sdf2.format(sdf1.parse(date));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}
}
