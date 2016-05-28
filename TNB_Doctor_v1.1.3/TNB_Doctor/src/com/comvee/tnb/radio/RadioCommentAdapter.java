package com.comvee.tnb.radio;

import android.text.Spannable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.comvee.BaseApplication;
import com.comvee.ComveeBaseAdapter;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.model.RadioComment;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.util.Util;
import com.easemob.easeui.utils.EaseSmileUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class RadioCommentAdapter extends ComveeBaseAdapter<RadioComment> {

    private OnClickListener mListener;
    private ArrayList<RadioComment> mList;
    private ImageLoader imgLoader = ImageLoaderUtil.getInstance(TNBApplication.getInstance());
    private int brilliantCount;// 精彩评论条数

    public RadioCommentAdapter() {
        super(TNBApplication.getInstance(), R.layout.radio_comment_item);
    }

    public void setList(ArrayList<RadioComment> list) {
        mList = list;
    }

    public void setBrilliantCount(int brilliantCount) {
        this.brilliantCount = brilliantCount;
    }

    public void setOnClickListener(OnClickListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public RadioComment getItem(int arg0) {
        return mList.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

	/*public static class ViewHolder {

		public TextView tv_comment_reply;
		public TextView tv_comment_count;
		public TextView tv_label;
		public ImageView iv_photo;
		public TextView tv_time;
		public TextView tv_content;
		public View layout_repeat;
		public TextView tv_repeat_label;
		public TextView tv_repeat_content;
		public TextView tv_title;
	}
*/
    /*@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(TNBApplication.getInstance(), R.layout.radio_comment_item, null);
			holder = ListViewHelper.getViewHolderByView(ViewHolder.class, convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
//		if (brilliantCount > 0 && position == 0) {
//			holder.tv_title.setVisibility(View.VISIBLE);
//			holder.tv_title.setText(TNBApplication.getInstance().getString(R.string.brilliant_comment));
//		} else if (brilliantCount == position) {
//			holder.tv_title.setVisibility(View.VISIBLE);
//			holder.tv_title.setText(TNBApplication.getInstance().getString(R.string.new_comment));
//		} else {
//			holder.tv_title.setVisibility(View.GONE);
//		}
		RadioComment item = getItem(position);
		imgLoader.displayImage(item.memberPhoto, holder.iv_photo);

		holder.tv_comment_count.setText(item.pariseNum + "");
		holder.tv_label.setText(item.memberName);
		// holder.tv_content.setText(item.commentText);
		holder.tv_time.setText(item.insertDt);
		holder.tv_comment_count.setOnClickListener(mListener);
		holder.tv_comment_reply.setOnClickListener(mListener);
		holder.tv_comment_reply.setTag(item);
		holder.tv_comment_count.setTag(item);
		holder.tv_comment_count.setCompoundDrawablesWithIntrinsicBounds(item.isParise == 1 ? R.drawable.radio_comment_03
				: R.drawable.radio_comment_02, 0, 0, 0);
		try {
			Spannable span = EaseSmileUtils.getSmiledText(TNBApplication.getInstance(), item.commentText);
			holder.tv_content.setText(span, BufferType.SPANNABLE);

			if (item.repetComment != null) {
				holder.layout_repeat.setVisibility(View.VISIBLE);
				holder.tv_repeat_label.setText(item.repetComment.memberName);
				EaseSmileUtils.getSmiledText(TNBApplication.getInstance(), item.repetComment.commentText);
				Spannable span1 = EaseSmileUtils.getSmiledText(TNBApplication.getInstance(), item.repetComment.commentText);
				holder.tv_repeat_content.setText(span1, BufferType.SPANNABLE);
			} else {
				holder.layout_repeat.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}*/

    private int mAnimPosition=-1;

    public void notifyChanged(int position){
        mAnimPosition = position;
        notifyDataSetChanged();
    }

    @Override
    protected void getView(ComveeBaseAdapter.ViewHolder vh, int i) {

        if(i==mAnimPosition){
            AnimationSet set = new AnimationSet(true);
            TranslateAnimation animation = new TranslateAnimation(-Util.getScreenWidth(BaseApplication.getInstance()), 0, 0, 0);
            animation.setDuration(500);
            set.addAnimation(animation);
            vh.mConvertView.startAnimation(set);
            mAnimPosition = -1;
        }

        ImageView phone = vh.get(R.id.iv_photo);//头像
        TextView label = vh.get(R.id.tv_label);
        TextView time = vh.get(R.id.tv_time);
        TextView tvLabel = vh.get(R.id.tv_repeat_label);
        TextView tvContent = vh.get(R.id.tv_repeat_content);
        TextView content = vh.get(R.id.tv_content);
        TextView tvComment = vh.get(R.id.tv_comment_count);
        TextView tvReply = vh.get(R.id.tv_comment_reply);
        RelativeLayout layout_repeat = vh.get(R.id.layout_repeat);
        RadioComment item = getItem(i);
        imgLoader.displayImage(item.memberPhoto, phone);

        tvComment.setText(item.pariseNum + "");
        label.setText(item.memberName);
        // holder.tv_content.setText(item.commentText);
        time.setText(item.insertDt);
        tvComment.setOnClickListener(mListener);
        tvReply.setOnClickListener(mListener);
        tvReply.setTag(item);
        tvComment.setTag(item);
        tvComment.setCompoundDrawablesWithIntrinsicBounds(item.isParise == 1 ? R.drawable.radio_comment_03
                : R.drawable.radio_comment_02, 0, 0, 0);
        try {
            Spannable span = EaseSmileUtils.getSmiledText(TNBApplication.getInstance(), item.commentText);
            content.setText(span, BufferType.SPANNABLE);

            if (item.repetComment != null) {
                tvReply.setVisibility(View.VISIBLE);
                tvLabel.setText(item.repetComment.memberName);
                EaseSmileUtils.getSmiledText(TNBApplication.getInstance(), item.repetComment.commentText);
                Spannable span1 = EaseSmileUtils.getSmiledText(TNBApplication.getInstance(), item.repetComment.commentText);
                tvContent.setText(span1, BufferType.SPANNABLE);
            } else {
                layout_repeat.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
