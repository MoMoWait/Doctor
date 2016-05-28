package com.comvee.tnb.ui.heathknowledge;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.comvee.tnb.R;
import com.comvee.tnb.widget.RoundImageView;
import com.comvee.tool.ImageLoaderUtil;

public class KnowledgeListAdapter extends SimpleAdapter {
	Context context;
	List<? extends Map<String, ?>> mdata;

	public KnowledgeListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		mdata = data;
		this.context=context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		View borderLine1 = view.findViewById(R.id.borderline1);
		View borderLine2 = view.findViewById(R.id.borderline2);
		RoundImageView img=(RoundImageView) view.findViewById(R.id.knowledge_ico);
		ImageLoaderUtil.getInstance(context).displayImage((String) mdata.get(position).get("imgUrl"), img, ImageLoaderUtil.default_options);
	//	View hot = view.findViewById(R.id.hot);
		if (position == getCount() - 1) {
			borderLine1.setVisibility(View.GONE);
			borderLine2.setVisibility(View.VISIBLE);

		} else {
			borderLine1.setVisibility(View.VISIBLE);
			borderLine2.setVisibility(View.GONE);
		}
		/*if ((Boolean) mdata.get(position).get("is_hot")) {
			hot.setVisibility(View.VISIBLE);
		} else {
			hot.setVisibility(View.GONE);
		}*/
		
		return view;
	}

}
