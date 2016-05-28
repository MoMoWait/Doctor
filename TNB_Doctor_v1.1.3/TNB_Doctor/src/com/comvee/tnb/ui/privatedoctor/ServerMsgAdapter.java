package com.comvee.tnb.ui.privatedoctor;

import java.util.List;

import android.app.Activity;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.model.DoctorServerItemMsg;
import com.comvee.tnb.model.DoctotServerMessage;

public class ServerMsgAdapter extends BaseAdapter {
	private List<DoctotServerMessage> list;
	private Activity activity;
	private int select;

	public ServerMsgAdapter(Activity activity) {
		this.activity = activity;
	}

	public void setSelect(int select) {
		this.select = select;
	}

	public void setList(List<DoctotServerMessage> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int i) {
		// TODO Auto-generated method stub
		return list.get(i);
	}

	@Override
	public long getItemId(int i) {
		// TODO Auto-generated method stub
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewgroup) {
		DoctotServerMessage message = list.get(i);
		if (view == null) {
			view = View.inflate(activity, R.layout.serverperiod, null);
			view.setTag(true);
		}
		ImageView img = (ImageView) view.findViewById(R.id.img_is_select);
		if (list.size() > 1) {
			img.setVisibility(View.VISIBLE);
		} else {
			img.setVisibility(View.GONE);
		}

		TextView tv_OriCost = (TextView) view.findViewById(R.id.tv_package_fee_num);
		TextView tv_Cost = (TextView) view.findViewById(R.id.tv_package_server_money);
		Paint paint = tv_OriCost.getPaint();
		paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		paint.setAntiAlias(true);
		if ( message.getFeeNum() > message.getFeeNumSale()) {
			tv_OriCost.setText("原价(￥" + message.getFeeNum() / 100 + ")");
			tv_OriCost.setVisibility(View.VISIBLE);
		}
		if (message.getFeeNumSale() >= 0) {
			String moneys = message.getFeeNumSale() / 100 + "";
			if (message.getUseNum() == -1) {
				moneys = "￥" + moneys + "/次";
			} else {
				String num = "";
				if (message.getUseNum() == 1) {
					num = "";
				} else {
					num = message.getUseNum() + "";
				}

				if (message.getUseUnit().equals("MI")) {
					moneys = "￥" + moneys + "/" + num + "分钟";
				}
				if (message.getUseUnit().equals("H")) {
					moneys = "￥" + moneys + "/" + num + "小时";
				}
				if (message.getUseUnit().equals("D")) {
					moneys = "￥" + moneys + "/" + num + "天";
				}
				if (message.getUseUnit().equals("W")) {
					moneys = "￥" + moneys + "/" + num + "周";
				}
				if (message.getUseUnit().equals("M")) {
					moneys = "￥" + moneys + "/" + num + "月";
				}
				if (message.getUseUnit().equals("Y")) {
					moneys = "￥" + moneys + "/" + num + "年";
				}
			}
			tv_Cost.setText(moneys);
		}
		if ((Boolean) view.getTag()) {
			createChildView((LinearLayout) view.findViewById(R.id.layout_server_msg), message);
			view.setTag(false);
		}
		if (i == select) {
			img.setBackgroundResource(R.drawable.check_style_1_b);
		} else {
			img.setBackgroundResource(R.drawable.check_style_1_a);
		}
		return view;
	}

	private void createChildView(LinearLayout layout, DoctotServerMessage models) {

		for (int i = 0; i < models.getItemList().size(); i++) {

			DoctorServerItemMsg item = models.getItemList().get(i);

			View view = View.inflate(activity, R.layout.buy_server_item, null);
			TextView name = (TextView) view.findViewById(R.id.tv_item_name);
			TextView desc1 = (TextView) view.findViewById(R.id.tv_desc_1);
			TextView desc2 = (TextView) view.findViewById(R.id.tv_desc_2);
			View img_doc_server_expain = view.findViewById(R.id.img_doc_server_expain);
			if (list.size() > 1) {
				img_doc_server_expain.setVisibility(View.INVISIBLE);
			} else {
				img_doc_server_expain.setVisibility(View.VISIBLE);
			}
			int star = item.getMemo().indexOf("<hx>");
			int end = item.getMemo().indexOf("</hx>");
			if (star >= 0 && end >= 0) {
				String value = item.getMemo().substring(star + 4, end);
				if (!value.equals("") && value != null) {
					desc1.setText(value);
					desc1.setVisibility(View.VISIBLE);
					Paint paint = desc1.getPaint();
					paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
					paint.setAntiAlias(true);
				}
			}
			int star2 = item.getMemo().indexOf("</hx>");
			if (star2 >= 0) {
				desc2.setText(item.getMemo().substring(star2 + 5, item.getMemo().length()));
			} else {
				desc2.setText(item.getMemo());
			}
			name.setText(item.getDictName());
			layout.addView(view);
		}

	}
}
