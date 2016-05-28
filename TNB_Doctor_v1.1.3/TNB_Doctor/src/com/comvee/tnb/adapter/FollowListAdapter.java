/**
 * 
 */
package com.comvee.tnb.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.model.FollowInfo;

/**
 * @author SZM
 *
 */
public class FollowListAdapter extends BaseAdapter{

	private Context context;
	private List<FollowInfo> followInfos;

	public FollowListAdapter(Context context,List<FollowInfo> followInfos){
		this.context=context;
		this.followInfos=followInfos;
	}
	
	public void setListItems(List<FollowInfo> followInfos){
		this.followInfos=followInfos;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return followInfos.size();
	}

	@Override
	public Object getItem(int arg0) {
		return followInfos.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View containView, ViewGroup arg2) {
		if (null == containView) {
			containView = View.inflate(context, R.layout.follow_list_item, null);
		}
		TextView tvType=(TextView)containView.findViewById(R.id.follow_item_type);
		TextView tvState=(TextView)containView.findViewById(R.id.follow_item_state);
		TextView tvTimeAndDoc=(TextView)containView.findViewById(R.id.follow_item_timeanddoc);
		
		FollowInfo info = followInfos.get(position);
		tvType.setText(info.getTypeText().toString());
		//用户未填
		String text = "未填写";
		int colorid = R.color.follow_no_repeat;
		int bgId = R.drawable.ic_follow_no_repeat;
		if(info.getFillStatus()==0){
		}
		//医生未处理
		if(info.getFillStatus()==1&&info.getDealStatus()==0){
			text = "问卷已提交";
			colorid = R.color.follow_already_submit;
			bgId = R.drawable.ic_follow_already_submit;
		}
		//医生已回复
		if(info.getDealStatus()==1){
			text = "问卷已回复";
			colorid = R.color.follow_already_repeat;
			bgId = R.drawable.ic_follow_already_repeat;
		}

		tvState.setText(text);
		tvState.setTextColor(containView.getResources().getColor(colorid));
		tvState.setBackgroundResource(bgId);
		
		String name="随访医生："+info.getDoctorName().toString();
		String time=info.getInsertDt().toString();
		tvTimeAndDoc.setText(name+" "+time);
		return containView;
	}
	
}
