package com.comvee.tnb.ui.record.diet;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.common.MyBaseAdapter;
import com.comvee.tnb.model.FoodInfo;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.ViewHolder;

public class DoctorListAdapter extends MyBaseAdapter<FoodInfo> {
	private List<FoodInfo> datas;
	private OnClickListener mOnClickListener;

	public DoctorListAdapter(Context ctx, List<FoodInfo> datas, int mayoutid) {
		super(ctx, datas, mayoutid);
		this.datas = datas;
	}

	public DoctorListAdapter(Context ctx, List<FoodInfo> datas, int mayoutid, OnClickListener mOnClickListener) {
		this(ctx, datas, mayoutid);
		this.mOnClickListener = mOnClickListener;
	}

	@Override
	protected void doyouself(ViewHolder holder, int position) {
		FoodInfo foodInfo = datas.get(position);
		ImageView imageView = holder.get(R.id.image);
		TextView nameTextView = holder.get(R.id.name);
		TextView giTextView = holder.get(R.id.GI);
		TextView weightNCalTextView = holder.get(R.id.weight);
		TextView swapView = holder.get(R.id.swap);
		View bottomLine = holder.get(R.id.bottomline);

		ImageLoaderUtil.getInstance(context).displayImage(foodInfo.imgUrl, imageView, ImageLoaderUtil.default_options);
		nameTextView.setText(foodInfo.name);
		giTextView.setText("GI " + foodInfo.gi);
		weightNCalTextView.setText(foodInfo.recommendWeight + "克  " + foodInfo.recommendHeat + "千卡");
		if (position == datas.size() - 1) {
			bottomLine.setVisibility(View.GONE);
		} else {
			bottomLine.setVisibility(View.VISIBLE);
		}
		swapView.setTag(foodInfo);
		swapView.setOnClickListener(mOnClickListener);
	}

}
