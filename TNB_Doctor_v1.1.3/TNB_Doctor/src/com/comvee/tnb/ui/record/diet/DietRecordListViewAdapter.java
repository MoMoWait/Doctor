package com.comvee.tnb.ui.record.diet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.frame.BaseFragment;
import com.comvee.tnb.R;
import com.comvee.tnb.common.MyBaseAdapter;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.ui.record.common.NetPic;
import com.comvee.tnb.ui.record.common.ShowImageViewpagerActivity;
import com.comvee.tnb.widget.GridView4Conflict;
import com.comvee.tnb.widget.GridView4Conflict.OnTouchInvalidPositionListener;
import com.comvee.tnb.widget.SectionedBaseAdapter;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.ViewHolder;

public class DietRecordListViewAdapter extends SectionedBaseAdapter {

	private BaseFragment bf;
	private Context ctx;
	private List<String> headsDatas;
	private HashMap<String, List<Diet>> itemDatas;
	private final String[] mealTimes = { "早餐", "午餐", "晚餐", "加餐" };
	private final int[] mealImgRes = { R.drawable.yinshi_10, R.drawable.yinshi_12, R.drawable.yinshi_14, R.drawable.yinshi_16_ };
	private SparseArray<SparseArray<List<ImageView>>> allSmallImageViewMap = new SparseArray<SparseArray<List<ImageView>>>();

	private float hightValue, lowValue;
	private OnClickListener onClickListener;
	private boolean enableShowImage = true;
	

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public DietRecordListViewAdapter(BaseFragment bf, List<String> headsDatas, HashMap<String, List<Diet>> itemDatas) {
		super();
		this.bf = bf;
		hightValue = Float.parseFloat(ConfigParams.getSugarHightValue(bf.getContext()));
		lowValue = Float.parseFloat(ConfigParams.getSugarLowValue(bf.getContext()));
		this.ctx = bf.getActivity().getApplicationContext();
		this.headsDatas = headsDatas;
		this.itemDatas = itemDatas;
	}

	public DietRecordListViewAdapter(BaseFragment bf, List<String> headsDatas, HashMap<String, List<Diet>> itemDatas, boolean showBigImage) {
		this(bf, headsDatas, itemDatas);
		enableShowImage = showBigImage;

	}

	@Override
	public Object getItem(int section, int position) {
		return itemDatas.get(section).get(position);
	}

	@Override
	public long getItemId(int section, int position) {
		return (long) (position * Math.pow(10, section));
	}

	@Override
	public int getSectionCount() {
		return headsDatas.size();
	}

	@Override
	public int getCountForSection(int section) {
		return itemDatas.get(headsDatas.get(section)).size();
	}

	@Override
	public View getItemView(final int section, final int position, View convertView, ViewGroup parent) {

		DecimalFormat decimalFormat = new DecimalFormat("0.0");
		final Diet currentDiet = itemDatas.get(headsDatas.get(section)).get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(ctx).inflate(R.layout.item_record_input_diet_listview, parent, false);
		}
		convertView.setTag(currentDiet);
		convertView.setOnClickListener(onClickListener);
		View lineView = convertView.findViewById(R.id.line_temp);
		View bottomView = convertView.findViewById(R.id.bottomline);
		View bottomContent = convertView.findViewById(R.id.bottomcontent);

		if (position == 0) {
			lineView.setVisibility(View.VISIBLE);
		} else {
			lineView.setVisibility(View.GONE);
		}
		if (position == getCountForSection(section) - 1) {
			bottomView.setVisibility(View.GONE);
			bottomContent.setVisibility(View.GONE);
		} else {
			bottomView.setVisibility(View.VISIBLE);
			bottomContent.setVisibility(View.VISIBLE);
		}

		ImageView mealImg = (ImageView) convertView.findViewById(R.id.meal_img);
		TextView mealTime = (TextView) convertView.findViewById(R.id.meal_time);
		TextView mealContent = (TextView) convertView.findViewById(R.id.name);
		TextView before_mealtime = (TextView) convertView.findViewById(R.id.before_mealtime);
		TextView after_mealtime = (TextView) convertView.findViewById(R.id.after_mealtime);
		TextView before_sugarvalue = (TextView) convertView.findViewById(R.id.before_sugarvalue);
		TextView after_sugarvalue = (TextView) convertView.findViewById(R.id.after_sugarvalue);
		TextView dif_value = (TextView) convertView.findViewById(R.id.dif_value);
		TextView add_or_move_canteen = (TextView) convertView.findViewById(R.id.add_or_move_canteen);

		int mealIndex = Integer.parseInt(currentDiet.period) - 1;
		if (mealIndex > 2) {// 加餐 ，兼容历史遗留问题
			mealImg.setImageResource(mealImgRes[3]);
		} else {
			mealImg.setImageResource(mealImgRes[mealIndex]);
		}
		mealTime.setText(mealTimes[mealIndex]);
		mealContent.setText(currentDiet.name);
		before_mealtime.setText(mealTimes[mealIndex] + "前");
		after_mealtime.setText(mealTimes[mealIndex] + "后");
		if (currentDiet.beforeSugarValue == 0) {
			before_sugarvalue.setTextColor(bf.getResources().getColor(R.color.black));
			before_sugarvalue.setText("- - ");
		} else {
			configTextViewColor(before_sugarvalue, currentDiet.beforeSugarValue);
			before_sugarvalue.setText(decimalFormat.format(currentDiet.beforeSugarValue));
		}
		if (currentDiet.afterSugarValue == 0) {
			after_sugarvalue.setTextColor(bf.getResources().getColor(R.color.black));
			after_sugarvalue.setText("- - ");
		} else {
			configTextViewColor(after_sugarvalue, currentDiet.afterSugarValue);
			after_sugarvalue.setText(decimalFormat.format(currentDiet.afterSugarValue));
		}
		if (currentDiet.afterSugarValue == 0 || currentDiet.beforeSugarValue == 0) {
			dif_value.setText("- - ");
		} else {
			dif_value.setText(decimalFormat.format(currentDiet.afterSugarValue - currentDiet.beforeSugarValue));
		}

		add_or_move_canteen.setTag(R.id.tag_first, currentDiet);
		add_or_move_canteen.setTag(R.id.tag_second, itemDatas.get(headsDatas.get(section)));
		add_or_move_canteen.setTag(R.id.tag_three, headsDatas.get(section));
		add_or_move_canteen.setOnClickListener(onClickListener);
		if (currentDiet.isCollect == 0) {
			add_or_move_canteen.setBackgroundResource(R.drawable.bg_oval_gree);
			add_or_move_canteen.setText("加入食堂");
		} else {
			add_or_move_canteen.setBackgroundResource(R.drawable.bg_oval_ori);
			add_or_move_canteen.setText("移出食堂");
		}

		final GridView4Conflict gridView = (GridView4Conflict) convertView.findViewById(R.id.gridview);
		GridViewAdapter gridViewAdapter = new GridViewAdapter(ctx, currentDiet.netpics, section, position, R.layout.item_record_diet_gridview);
		gridView.setAdapter(gridViewAdapter);
		if (enableShowImage)
			gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position1, long id) {
					Intent it = ShowImageViewpagerActivity.getIntent(bf.getActivity(), currentDiet.netpics, position1,
							allSmallImageViewMap.get(section).get(position));
					bf.getActivity().startActivity(it);

				}
			});
		gridView.setOnTouchInvalidPositionListener(new OnTouchInvalidPositionListener() {
            @Override
            public boolean onTouchInvalidPosition(int motionEvent) {
                return false; 
            }
        });  
		return convertView;
	}

	private void configTextViewColor(TextView tv, float value) {
		if (value < lowValue) {
			tv.setTextColor(bf.getResources().getColor(R.color.index_record_blue));
		} else if (value > hightValue) {
			tv.setTextColor(bf.getResources().getColor(R.color.index_record_red));
		} else {
			tv.setTextColor(bf.getResources().getColor(R.color.index_record_green));
		}
	}

	@Override
	public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(ctx).inflate(R.layout.item_pinned_diet, parent, false);
		}
		TextView tv = (TextView) convertView.findViewById(R.id.title);
		tv.setText(headsDatas.get(section));
		return convertView;
	}

	private class GridViewAdapter extends MyBaseAdapter<NetPic> {
		private List<NetPic> datas;
		private List<ImageView> imageViews = new ArrayList<ImageView>();

		public GridViewAdapter(Context ctx, List<NetPic> datas, int set, int pos, int mayoutid) {
			super(ctx, datas, mayoutid);
			this.datas = datas;
			SparseArray<List<ImageView>> selectionSpar = allSmallImageViewMap.get(set);
			if (selectionSpar == null) {
				selectionSpar = new SparseArray<List<ImageView>>();
				allSmallImageViewMap.put(set, selectionSpar);
			}
			selectionSpar.put(pos, imageViews);
		}

		@Override
		protected void doyouself(ViewHolder holder, final int position) {
			String smallImage = datas.get(position).picSmall;
			final ImageView imageView = holder.get(R.id.image);
			if (imageViews.size() <= position) {
				imageViews.add(imageView);
			}
			ImageLoaderUtil.getInstance(context).displayImage(smallImage, imageView, ImageLoaderUtil.default_options);
		}
	}
	public interface OnInterListener{
		public void onitemClick(int itemPos,Diet diet);
	}
}
