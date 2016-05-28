package com.comvee.tnb.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.model.TaskItem;
import com.comvee.tool.ImageLoaderUtil;

public class TaskAdapter extends BaseAdapter {
	private ArrayList<TaskItem> listItems = null;
	private Context context;

	public TaskAdapter( Context context) {
		this.context = context;
	}

	public void setListItems(ArrayList<TaskItem> listItems) {
		this.listItems = listItems;
	}

	@Override
	public int getCount() {
		return listItems == null ? 0 : listItems.size();
	}

	@Override
	public TaskItem getItem(int arg0) {
		return listItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_task_center, null);
			holder.task_icon = (ImageView) convertView.findViewById(R.id.task_icon);
			holder.task_star = (RatingBar) convertView.findViewById(R.id.task_star);
			holder.task_name = (TextView) convertView.findViewById(R.id.task_name);
			holder.task_new = (TextView) convertView.findViewById(R.id.task_new);
			holder.task_detail = (TextView) convertView.findViewById(R.id.task_detail);
			holder.task_use = (TextView) convertView.findViewById(R.id.task_use);
			holder.task_comment = (TextView) convertView.findViewById(R.id.task_comment);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (listItems == null) {
			return convertView;
		}
		TaskItem item = listItems.get(arg0);

		holder.task_star.setVisibility(View.GONE);
		holder.task_name.setText(item.getName());
		holder.task_detail.setText(item.getDetail());
		holder.task_use.setText(item.getUse());
		holder.task_comment.setText(item.getComment());
		holder.task_new.setVisibility(item.getIsNew() == 1 ? View.VISIBLE : View.GONE);
		if (!TextUtils.isEmpty(item.getImgUrl())) {
			ImageLoaderUtil.getInstance(context).displayImage(item.getImgUrl(), holder.task_icon, ImageLoaderUtil.default_options);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView task_icon;
		RatingBar task_star;
		TextView task_name;
		TextView task_new;
		TextView task_detail;
		TextView task_use;
		TextView task_comment;
	}

}
