package com.comvee.tnb.adapter;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.comvee.tnb.R;
import com.comvee.tnb.common.MyBaseAdapter;
import com.comvee.tnb.model.DoctorPackageInfo;
import com.comvee.tool.ViewHolder;

public class DocListServerGrideAdapter extends MyBaseAdapter<DoctorPackageInfo> {
	private int count;
	HashMap<String, SoftReference<Bitmap>> serverHeads;

	public DocListServerGrideAdapter(Context ctx, List<DoctorPackageInfo> datas, int mayoutid, int count,
			HashMap<String, SoftReference<Bitmap>> serverHeads) {
		super(ctx, datas, mayoutid);
		this.count = count;
		this.serverHeads = serverHeads;
	}

	@Override
	public int getCount() {
		if (datas == null) {
			return 0;
		} else {
			if (datas.size() > count) {
				return count;
			} else {
				return datas.size();
			}
		}
	}

	@Override
	protected void doyouself(ViewHolder holder, int position) {
		DoctorPackageInfo info = datas.get(position);
		ImageView imageView = holder.get(R.id.img_server_head);
		if (serverHeads != null&&serverHeads.containsKey(info.packageImgThumb)) {
			imageView.setImageBitmap(serverHeads.get(info.packageImgThumb).get());
		}
	}

}
