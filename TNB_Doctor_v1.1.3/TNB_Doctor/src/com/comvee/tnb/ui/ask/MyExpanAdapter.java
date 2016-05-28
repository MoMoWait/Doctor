package com.comvee.tnb.ui.ask;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.adapter.DocListServerGrideAdapter;
import com.comvee.tnb.model.DocDetailModel;
import com.comvee.tnb.model.DoctorPackageInfo;
import com.comvee.tnb.widget.XExpandableListView;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UITool;
import com.comvee.tool.ViewHolder;
import com.comvee.util.BitmapUtil;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class MyExpanAdapter extends BaseExpandableListAdapter {
	private ArrayList<DocDetailModel> arrayList;
	private Activity context;
	private int gridCount = -1;
	private HashMap<String, SoftReference<Bitmap>> serverHeads = new HashMap<String, SoftReference<Bitmap>>();
	private SoftReference<Bitmap> defaultBit;

	public MyExpanAdapter(Activity context, ArrayList<DocDetailModel> arrayList) {
		this.arrayList = arrayList;
		this.context = context;
		defaultBit = new SoftReference<Bitmap>(BitmapUtil.getBitmap(context, R.drawable.img_defualt1));
	}

	public void setArrayList(ArrayList<DocDetailModel> arrayList) {
		this.arrayList = arrayList;
	}

	@Override
	public Object getChild(int arg0, int arg1) {
		return arrayList == null ? null : (arrayList.get(arg0).arrayList == null ? null : arrayList.get(arg0).arrayList.get(arg1));
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		return arg1;
	}

	@Override
	public View getChildView(final int arg0, int arg1, boolean arg2, View arg3, final ViewGroup arg4) {
		final DocDetailModel model = arrayList.get(arg0);
		DoctorPackageInfo info = model.arrayList.get(arg1);
		ViewHolder holder = ViewHolder.getViewHolder(context, arg3, arg4, R.layout.item_doc_server);
		ImageView imageView = holder.get(R.id.img_server_head);
		TextView server_name = holder.get(R.id.tv_server_name);
		TextView server_money = holder.get(R.id.tv_server_money);
		// ImageLoaderUtil.getInstance(context).displayImage(info.packageImg,
		// imageView, this);
		if (serverHeads != null && serverHeads.containsKey(info.packageImgThumb)) {
			imageView.setImageBitmap(serverHeads.get(info.packageImgThumb).get());
		}

		server_name.setText(info.packageName);
		server_money.setText(info.priceShow);

		if (arg1 == 0) {
			holder.get(R.id.btn_server_detail).setVisibility(View.VISIBLE);
		} else {
			holder.get(R.id.btn_server_detail).setVisibility(View.INVISIBLE);
		}
		if (info.isHaveCoupon) {
			server_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.kaquan_27, 0);
		}
		holder.get(R.id.btn_server_detail).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				((XExpandableListView) arg4).collapseGroup(arg0);
			}
		});
		if (arg1 == 0 || arg1 == 1) {
			server_money.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					((XExpandableListView) arg4).collapseGroup(arg0);
				}
			});
		}
		return holder.mConvertView;
	}

	@Override
	public int getChildrenCount(int arg0) {
		return arrayList == null ? 0 : (arrayList.get(arg0).arrayList == null || arrayList.get(arg0).arrayList.size() == 0 ? 0
				: arrayList.get(arg0).arrayList.size());
	}

	@Override
	public Object getGroup(int arg0) {
		return arrayList == null ? null : arrayList.get(arg0);
	}

	@Override
	public int getGroupCount() {
		return arrayList == null ? 0 : arrayList.size();
	}

	@Override
	public long getGroupId(int arg0) {
		return arg0;
	}

	@Override
	public View getGroupView(final int arg0, boolean arg1, final View arg2, final ViewGroup arg3) {
		final DocDetailModel model = arrayList.get(arg0);
		ViewHolder holder = ViewHolder.getViewHolder(context, arg2, arg3, R.layout.item_doc_detail);
		ImageView img_doc = holder.get(R.id.img_doc);
		TextView doc_name = holder.get(R.id.tv_doc_name);
		TextView doc_desc = holder.get(R.id.tv_doc_desc);
		TextView doc_label_1 = holder.get(R.id.tv_label_1);
		TextView doc_label_2 = holder.get(R.id.tv_label_2);
		TextView doc_label_3 = holder.get(R.id.tv_label_3);
		TextView doc_address = holder.get(R.id.tv_doc_address);
		if (model.if_doctor == 1) {
			holder.get(R.id.tv_my_doctor_lable).setVisibility(View.VISIBLE);
		} else {
			holder.get(R.id.tv_my_doctor_lable).setVisibility(View.GONE);
		}
		ImageLoaderUtil.getInstance(context).displayImage(model.photoUrl, img_doc, ImageLoaderUtil.doc_options);
		doc_name.setText(model.doctorName);
		doc_desc.setText(model.positionName);
		doc_address.setText(model.hospitalName);
		if (!TextUtils.isEmpty(model.tags)) {
			String str[] = model.tags.replace("^$%", "@").split("@");
			doc_label_1.setVisibility(View.GONE);
			doc_label_2.setVisibility(View.GONE);
			doc_label_3.setVisibility(View.GONE);
			for (int i = 0; i < str.length; i++) {
				if (TextUtils.isEmpty(str[i]))
					continue;
				switch (i) {
				case 0:
					doc_label_1.setText(str[i]);
					doc_label_1.setVisibility(View.VISIBLE);
					break;
				case 1:
					doc_label_2.setText(str[i]);
					doc_label_2.setVisibility(View.VISIBLE);
					break;
				case 2:
					doc_label_3.setText(str[i]);
					doc_label_3.setVisibility(View.VISIBLE);
					break;
				default:
					break;
				}
			}
		}

		if (model.arrayList == null || model.arrayList.size() == 0) {
			holder.get(R.id.group_doc_server_item).setVisibility(View.INVISIBLE);
			return holder.mConvertView;
		} else if (arg1) {
			holder.get(R.id.group_doc_server_item).setVisibility(View.GONE);
			return holder.mConvertView;
		}

		for (int i = 0; i < model.arrayList.size(); i++) {
			String url = model.arrayList.get(i).packageImgThumb;
			if (!serverHeads.containsKey(url)) {
				loadImage(url);
			}
		}
		GridView gridView = holder.get(R.id.grid_doc_server);
		View groupBtn = holder.get(R.id.btn_server_detail);
		if (gridCount == -1) {
			gridCount = getGridCount(UITool.getViewWidth(groupBtn));
			gridCount = gridCount > 8 ? 8 : gridCount;
		}
		if (gridCount != -1) {
			gridView.setNumColumns(gridCount);
			gridView.setHorizontalSpacing(UITool.dip2px(context, 7));
			holder.get(R.id.group_doc_server_item).setVisibility(View.VISIBLE);
			DocListServerGrideAdapter grideAdapter = new DocListServerGrideAdapter(context, model.arrayList, R.layout.item_doc_server_head_grid,
					gridCount, serverHeads);
			gridView.setAdapter(grideAdapter);
			gridView.setFocusable(false);
		}
		groupBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				((XExpandableListView) arg3).expandGroup(arg0);
			}
		});
		return holder.mConvertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return false;
	}

	/**
	 * 计算屏幕宽度可以放下几个服务的缩略图
	 * 
	 * @param btnWidth
	 * @return
	 */
	private int getGridCount(int btnWidth) {
		float dispWidth = UITool.getDisplayWidth(context);
		int gridCount = (int) ((dispWidth - btnWidth - UITool.dip2px(context, 70))) / (UITool.dip2px(context, 26));
		return gridCount;
	}

	/**
	 * 缓存医生服务的图片各一张，本地就直接去缓存，不需要重新加载图片
	 * 
	 * @param uri
	 */
	private void loadImage(String uri) {
		if (!serverHeads.containsKey(uri)) {
			serverHeads.put(uri, defaultBit);
			ImageLoaderUtil.getInstance(context).loadImage(uri, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingComplete(String s, View view1, Bitmap bitmap) {
					super.onLoadingComplete(s, view1, bitmap);
					serverHeads.put(s, new SoftReference<Bitmap>(bitmap));
					bitmap = null;
					notifyDataSetChanged();
				}
			});

		}
	}
}
