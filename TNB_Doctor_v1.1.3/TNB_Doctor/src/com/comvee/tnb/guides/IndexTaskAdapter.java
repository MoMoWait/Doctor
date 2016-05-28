package com.comvee.tnb.guides;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tool.ImageLoaderUtil;

public class IndexTaskAdapter extends BaseListAdapter<IndexTaskInfo> {

	public long getInt(String key) {
		return getContext().getSharedPreferences("config", 0).getLong(key, 0);
	}

	public Drawable getDrawableFromRes(String name) {
		int resID = getContext().getResources().getIdentifier(name, "drawable", getContext().getPackageName());
		return resID != 0 ? getContext().getResources().getDrawable(resID) : null;
	}

	public IndexTaskAdapter(Context mContext) {
		super(mContext);
	}

	@Override
	public View getView(int position, View containView, ViewGroup arg2) {
		if (containView == null) {
			containView = View.inflate(getContext(), R.layout.fragment_guides_health_main_item, null);
		}

		IndexTaskInfo info = getItem(position);

		TextView tvTaskName = (TextView) containView.findViewById(R.id.tv_task_name);
		TextView tvTaskDecs = (TextView) containView.findViewById(R.id.tv_task_decs);
		ImageView ivIcon = (ImageView) containView.findViewById(R.id.iv_icon);
		ImageView ivStatus = (ImageView) containView.findViewById(R.id.iv_status);
		TextView tvTaskPosition = (TextView) containView.findViewById(R.id.tv_task_position);

		tvTaskName.setText(info.getTitle());
		if (TextUtils.isEmpty(info.getSubtitle())) {
			tvTaskDecs.setVisibility(View.GONE);
		} else {
			tvTaskDecs.setVisibility(View.VISIBLE);
			tvTaskDecs.setText(info.getSubtitle());
		}

		if (info.getStatus() == 1) {
			tvTaskDecs.setTextColor(getContext().getResources().getColor(R.color.index_task_decs_complete));
			tvTaskName.setTextColor(getContext().getResources().getColor(R.color.index_task_name_complete));
			ivStatus.setVisibility(View.VISIBLE);
			tvTaskPosition.setVisibility(View.INVISIBLE);
		} else {
			tvTaskDecs.setTextColor(getContext().getResources().getColor(R.color.text_defualt));
			tvTaskName.setTextColor(getContext().getResources().getColor(R.color.umeng_fb_color_btn_normal));
			ivStatus.setVisibility(View.INVISIBLE);
			if (info.getType() == 1)
				tvTaskPosition.setVisibility(View.GONE);

		}

		containView.findViewById(R.id.iv_isnew).setVisibility(info.getIsNew() == 1 ? View.VISIBLE : View.GONE);

		// 根据是否看过阅读，如果看过就是0，没看过就根据引擎的结果
//		if (info.getType() == 1||info.getType() == 5||info.getType() == 6) {
//			tvTaskPosition.setText(info.getSeq() + "/" + info.getTotal());
//		}
		if (!TextUtils.isEmpty(info.getIcon())) {

			// FileUtils.getDrawableFromAssest(info.getIcon() );
			//
			// String path = ConfigUtil.getImgDirPath() + File.separator +
			// info.getIcon() + (info.getStatus() == 1 ? "d" : "") + ".png";
			// Drawable d = FileUtils.getDrawableFromFile(path);

			// Drawable d = FileUtils.getDrawableFromAssest(info.getStatus() ==
			// 1 ? info.getIcon() + "d.png" : info.getIcon() + ".png");
			// Drawable d = getDrawableFromRes(info.getStatus() == 1 ?
			// info.getIcon() + "d" : info.getIcon());
			// if (d != null) {
			// ivIcon.setImageDrawable(d);
			// } else {
			// ImageLoaderUtil.getInstance(getApplicationContext()).displayImage(info.getIcon(),
			// ivIcon, ImageLoaderUtil.default_options);
			// }
			ImageLoaderUtil.getInstance(getContext()).displayImage(info.getIcon(), ivIcon, ImageLoaderUtil.default_options);
		} else {
			ivIcon.setImageResource(info.getStatus() == 1 ? R.drawable.index_eat : R.drawable.index_icon_sugar);
		}
		return containView;
	}
}
