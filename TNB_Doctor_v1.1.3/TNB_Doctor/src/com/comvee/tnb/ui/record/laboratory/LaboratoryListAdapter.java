package com.comvee.tnb.ui.record.laboratory;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.ComveeBaseAdapter;
import com.comvee.ThreadHandler;
import com.comvee.tnb.R;
import com.comvee.tnb.ui.record.common.ImageItem4LocalImage;
import com.comvee.tnb.ui.record.common.MyBitmapFactory;
import com.comvee.tnb.ui.record.common.NetPic;
import com.comvee.tnb.ui.record.common.NetPics;
import com.comvee.tnb.ui.record.laboratory.Laboratory.AnswerUploadFolder;
import com.comvee.tool.ImageLoaderUtil;

/**
 * 化验单  适配器
 * @author friendlove-pc
 *
 */
public class LaboratoryListAdapter extends ComveeBaseAdapter<Laboratory> {
	interface OnCallback{
		void onToDetail(Laboratory laboratory);
		void onToPick(List<NetPic> datas, int position, List<ImageView> imageViews);
	}
	private OnCallback mCallback;
	public void setOnCallBack(OnCallback callback){
		this.mCallback = callback;
	}
	private final SparseArray<List<ImageView>> allSmallImageViewMap = new SparseArray<List<ImageView>>();//上传验化单图片
	private final SparseArray<List<ImageView>> allSmallImageViewMapDoctor = new SparseArray<List<ImageView>>();//医生解读的图片
	private  int isImgAdd;//1.是添加验化单图片 2.是添加解读的图片
	public LaboratoryListAdapter(Context ctx, int mayoutid) {
		super(ctx, mayoutid);
	}
	@Override
	protected void getView(final ViewHolder holder, final int position) {
		final Laboratory laboratory = getItem(position);

		TextView nameTv = holder.get(R.id.name);
		TextView timeTv = holder.get(R.id.time);
		LinearLayout answer_layout=holder.get(R.id.answer_layout);
		final AnswerUploadFolder uploadFolder = laboratory.answerUploadFolder;
		TextView nameDoctor = holder.get(R.id.name_doctor);
		TextView timeDoctor = holder.get(R.id.time_doctor);
		TextView answerDoctor = holder.get(R.id.answer_doctor);
		String isUsedServiced = laboratory.isUseService;

		if (uploadFolder==null){
			answer_layout.setVisibility(View.GONE);
		}else{
			answer_layout.setVisibility(View.VISIBLE);
		}
		try {
			nameTv.setText(laboratory.folderName);
			timeTv.setText("化验单时间:  " + laboratory.insertDt.split(" ")[0]);
			if (uploadFolder!=null) {
				nameDoctor.setText(uploadFolder.folderName);
				timeDoctor.setText("解读时间: "+ uploadFolder.insertDt.split(" ")[0]);
				answerDoctor.setText(uploadFolder.answer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (uploadFolder!=null) {
			holder.get(R.id.rl_doctor).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyBitmapFactory.tempSelectBitmap.clear();
                    for (int i = 0; i < uploadFolder.uploadPics.size(); i++) {
                        ImageItem4LocalImage imageItem = new ImageItem4LocalImage();
                        imageItem.sourceImagePath = uploadFolder.uploadPics.get(i).picBig;
                        imageItem.smallImagePath = uploadFolder.uploadPics.get(i).picSmall;
                        MyBitmapFactory.tempSelectBitmap.add(imageItem);
                    }
					/*if(mCallback!=null){
						mCallback.onToDetail(uploadFolder);
					}*/
                }
            });
		}
		holder.get(R.id.rl).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyBitmapFactory.tempSelectBitmap.clear();
				for (int i = 0; i < laboratory.uploadPics.size(); i++) {
					ImageItem4LocalImage imageItem = new ImageItem4LocalImage();
					imageItem.sourceImagePath = laboratory.uploadPics.get(i).picBig;
					imageItem.smallImagePath = laboratory.uploadPics.get(i).picSmall;
					MyBitmapFactory.tempSelectBitmap.add(imageItem);
				}
				if(mCallback!=null){
					mCallback.onToDetail(laboratory);
				}
			}
		});
		if (getCount() == position + 1) {
			if (uploadFolder==null){
				holder.get(R.id.tem5_doctor).setVisibility(View.GONE);
				holder.get(R.id.tem5).setVisibility(View.VISIBLE);
			}else{
				holder.get(R.id.tem5_doctor).setVisibility(View.VISIBLE);
				holder.get(R.id.tem5).setVisibility(View.GONE);
			}

		}
		if (position == 0) {
			holder.get(R.id.firstline).setVisibility(View.VISIBLE);
		} else {
			holder.get(R.id.firstline).setVisibility(View.GONE);
		}
		//加载上传解读图片
		final GridView gridView = holder.get(R.id.gridview);
		isImgAdd=1;
		GridViewAdapter gridViewAdapter = new GridViewAdapter(context, position, R.layout.item_record_input_laboratory_gridview);
		gridViewAdapter.setDatas(laboratory.uploadPics);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position1, long id) {
				if(mCallback!=null){
					mCallback.onToPick(laboratory.uploadPics, position1, allSmallImageViewMap.get(position));
				}
			}
		});
		gridView.setAdapter(gridViewAdapter);
		//加载医生解读图片
		ThreadHandler.postUiThread(new Runnable() {
			@Override
			public void run() {
				if (uploadFolder!=null) {
					isImgAdd=2;
					final GridView gridDoctor = holder.get(R.id.gridview_doctor);
					GridViewAdapter gridDoctorAdapter = new GridViewAdapter(context, position, R.layout.item_record_input_laboratory_gridview);
					gridDoctorAdapter.setDatas(uploadFolder.uploadPics);
					gridDoctor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position1, long id) {
							if(mCallback!=null){
								mCallback.onToPick(uploadFolder.uploadPics, position1, allSmallImageViewMapDoctor.get(position));
							}
						}
					});
					gridDoctor.setAdapter(gridDoctorAdapter);
				}
			}
		},500);
	}
	private class GridViewAdapter extends ComveeBaseAdapter<NetPic> {
		private List<ImageView> imageViews = new ArrayList<ImageView>();

		public GridViewAdapter(Context ctx, int pos, int mayoutid) {
			super(ctx, mayoutid);
			if (isImgAdd==1){//添加上传单图片
				allSmallImageViewMap.put(pos, imageViews);
			}else if(isImgAdd==2){//填加医生解读图片
				allSmallImageViewMapDoctor.put(pos,imageViews);
			}

		}

		@Override
		protected void getView(ViewHolder holder, final int position) {
			String smallImage = getItem(position).picSmall;
			final ImageView imageView = holder.get(R.id.image);
			if (imageViews.size() <= position) {
				imageViews.add(imageView);
			}
			ImageLoaderUtil.getInstance(context).displayImage(smallImage, imageView, ImageLoaderUtil.default_options);
		}
	}

}
