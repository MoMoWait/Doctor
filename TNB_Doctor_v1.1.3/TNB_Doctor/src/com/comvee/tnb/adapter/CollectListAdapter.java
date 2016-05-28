/**
 * 
 */
package com.comvee.tnb.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.model.CollectInfo;
import com.comvee.tool.ImageLoaderUtil;

/**收集列表 
 * @author SZM
 *
 */
public class CollectListAdapter extends BaseAdapter{

	private Context context;
	private List<CollectInfo> collects;
	private boolean tag;//是否开始编辑
	
	public CollectListAdapter(Context context,boolean tag){
		this.context=context;
		this.tag=tag;
	}

	public void setListItems(List<CollectInfo> collects) {
		this.collects = collects;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return collects == null ? 0 : collects.size();
	}

	@Override
	public Object getItem(int arg0) {
		return collects.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	@Override
	public View getView(int position, View containView, ViewGroup arg2) {
		if (null == containView) {
			containView = View.inflate(context, R.layout.collect_list_item, null);
		}
		CollectInfo info = collects.get(position);
		ImageView iv_left=(ImageView)containView.findViewById(R.id.iv_left);
		ImageView iv_booklogo=(ImageView)containView.findViewById(R.id.iv_booklogo);
		TextView tv_title=(TextView)containView.findViewById(R.id.collect_tv_title);
		if(tag==true)
			iv_left.setVisibility(View.VISIBLE);
		ImageLoaderUtil.getInstance(context).displayImage(info.getImgUrl(), iv_booklogo, ImageLoaderUtil.default_options);
		String title=info.getTitle();
		tv_title.setText(title);
		return containView;
	}
}
