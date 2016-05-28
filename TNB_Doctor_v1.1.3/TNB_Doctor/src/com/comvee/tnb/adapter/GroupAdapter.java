package com.comvee.tnb.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.model.GroupInfo;
import com.comvee.util.Util;

public class GroupAdapter extends BaseAdapter {

	private List<GroupInfo> mListItems;
	private Context mContext;
	private int pading;

	public GroupAdapter(Context cxt, List<GroupInfo> mListItems) {
		this.mContext = cxt;
		this.mListItems = mListItems;
		pading = Util.dip2px(cxt, 10);
	}

	@Override
	public int getCount() {
		return mListItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mListItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		ViewHolder holder = null;

		if (null == arg1) {
			arg1 = View.inflate(mContext, R.layout.item_story, null);
			holder = new ViewHolder();
			holder.name = (TextView) arg1.findViewById(R.id.tv_name);
			holder.group = (TextView) arg1.findViewById(R.id.tv_group);
			holder.count = (TextView) arg1.findViewById(R.id.tv_count);
			holder.group.setVisibility(View.GONE);
			holder.count.setVisibility(View.GONE);
			holder.name.setTextSize(30);
			holder.name.setPadding(pading, pading, pading, pading);
			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}

		final GroupInfo info = mListItems.get(position);

		holder.name.setText(info.name);

		return arg1;
	}

	class ViewHolder {
		TextView name;
		TextView group;
		ImageView img;
		TextView count;
	}

}
