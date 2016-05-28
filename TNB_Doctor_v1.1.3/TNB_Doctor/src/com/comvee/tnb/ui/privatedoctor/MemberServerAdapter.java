package com.comvee.tnb.ui.privatedoctor;

import java.util.List;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.common.MyBaseAdapter;
import com.comvee.tnb.model.MemberDoctorInfo.PackageList;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.ViewHolder;

public class MemberServerAdapter extends MyBaseAdapter<PackageList> {

	public MemberServerAdapter(Context ctx, List<PackageList> datas, int mayoutid) {
		super(ctx, datas, mayoutid);
	}

	@Override
	protected void doyouself(ViewHolder holder, int position) {
		PackageList model = datas.get(position);
		ImageView head = holder.get(R.id.img_server_head);
		TextView name = holder.get(R.id.tv_server_name);
		TextView time = holder.get(R.id.tv_time);
		LinearLayout group_msg_item = holder.get(R.id.group_msg_item);
		ImageLoaderUtil.getInstance(context).displayImage(model.packageInfo.doctorImage, head, ImageLoaderUtil.doc_options);
		name.setText(model.packageInfo.packageName);
		time.setText(model.packageInfo.valueDate);
		if (null == group_msg_item.getTag()) {
			group_msg_item.setTag(true);
		}
		if ((Boolean) group_msg_item.getTag() || group_msg_item.getTag() == null) {
			for (int i = 0; i < model.serverList.size(); i++) {
				addItem(group_msg_item, model.serverList.get(i).content);
			}
			group_msg_item.setTag(false);
		}
	}

	private void addItem(LinearLayout layout, String str) {
		TextView textView = new TextView(context);
		textView.setTextColor(context.getResources().getColor(R.color.text_color_2));
		textView.setTextSize(15);
		textView.setPadding(0, com.comvee.tool.UITool.dip2px(context, 6), 0, 0);
		textView.setText(str);
		layout.addView(textView);
	}
}
