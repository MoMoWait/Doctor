package com.comvee.tnb.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.model.IndexGridModel;

public class IndexGridViewAdapter extends BaseAdapter {
	private List<IndexGridModel> gridModels;
	private Context context;

	public IndexGridViewAdapter(Context context, List<IndexGridModel> gridModels) {
		this.context = context;
		this.gridModels = gridModels;
	}

	public void setGridModels(List<IndexGridModel> gridModels) {
		this.gridModels = gridModels;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return gridModels == null ? 0 : gridModels.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return gridModels == null ? null : gridModels.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View view, ViewGroup arg2) {
		IndexGridModel model = gridModels.get(arg0);
		if (view == null) {
			view = View.inflate(context, R.layout.item_index_gridveiw, null);
		}
		ImageView imageView = (ImageView) view.findViewById(R.id.img_item_index);
		TextView name = (TextView) view.findViewById(R.id.tv_item_index);
		imageView.setImageResource(model.getImageLocUrl());
		name.setText(model.getName());
//		view.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, (int) (model.getWidth() * 139 / 320)));
		return view;
	}

}
