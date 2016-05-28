package com.comvee.tnb.ui.record;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.model.RecordDetailItem;
import com.comvee.tool.AppUtil;

public class RecordDetailGridAdaapter extends BaseAdapter {
	private ArrayList<RecordDetailItem> arrayList;
	private Context context;
	private RecordDetailItem info;

	public void setArrayList(ArrayList<RecordDetailItem> arrayList) {
		this.arrayList = arrayList;
	}

	public RecordDetailGridAdaapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (arrayList == null) {
			return 0;
		}
		return arrayList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		if (arrayList == null) {
			return null;
		}
		return arrayList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		info = arrayList.get(arg0);
		if (arg1 == null) {
			arg1 = View.inflate(context, R.layout.record_detail_item, null);
		}

		arg1.setFocusable(false);
		arg1.setClickable(false);
		TextView tv_value = (TextView) arg1.findViewById(R.id.tv_value);
		ImageView select = (ImageView) arg1.findViewById(R.id.img_select);
		ImageView img_value = (ImageView) arg1.findViewById(R.id.img_value);
		AppUtil.loadImageByLocationAndNet(img_value, info.getPhoto());
		tv_value.setText(info.getText());
		if (info.isSelect()) {
			select.setBackgroundResource(R.drawable.check_style_1_b);
			tv_value.setTextColor(Color.parseColor("#333333"));
		} else {
			select.setBackgroundResource(R.drawable.record_unselect);
			tv_value.setTextColor(Color.parseColor("#999999"));
		}
		// 动态设置选项的长宽 长宽比4:3
		arg1.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, info.getViewWidth() * 4 / 3));
		return arg1;
	}
}
