package com.comvee.tnb.ui.record.common;

import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.common.MyBaseAdapter;
import com.comvee.tnb.ui.record.laboratory.BitmapCache;
import com.comvee.tnb.ui.record.laboratory.BitmapCache.ImageCallback;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ViewHolder;

/**
 * 查看相册
 * 
 * @author PXL
 */
@SuppressLint("ValidFragment")
public class ShowAlbumFragment extends BaseFragment implements OnClickListener, OnItemClickListener {
	private TitleBarView mBarView;
	ListView listview;
	MyBaseAdapter<ImageBucket> adapter;
	List<ImageBucket> datas;
	BitmapCache cache = new BitmapCache();
	ImageCallback callback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				} else {
					Log.e("tag", "callback, bmp not match");
				}
			} else {
				Log.e("tag", "callback, bmp null");
			}
		}
	};

	public ShowAlbumFragment() {
	}

	public static ShowAlbumFragment newInstance() {
		ShowAlbumFragment fragment = new ShowAlbumFragment();
		return fragment;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.show_album_image;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setTitle(getString(R.string.ask_content_pho));
		mBarView.setRightButton(getString(R.string.cancel), this);

		listview = (ListView) findViewById(R.id.listview);
		AlbumHelper helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		datas = helper.getImagesBucketList(false);
		adapter = new MyBaseAdapter<ImageBucket>(mContext, datas, R.layout.item_album_listview) {
			@Override
			protected void doyouself(ViewHolder holder, int position) {
				ImageView iv = holder.get(R.id.image);
				ImageBucket imageBucket = datas.get(position);
				iv.setTag(imageBucket.imageList.get(0).imagePath);
				cache.displayBmp(iv, imageBucket.imageList.get(0).thumbnailPath, imageBucket.imageList.get(0).imagePath, callback);
				TextView tv = holder.get(R.id.number);
				tv.setText(imageBucket.bucketName + "(" + imageBucket.imageList.size() + ")");
			}

		};
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MyBitmapFactory.tempAllImage = datas.get(position).imageList;
		MyBitmapFactory.albumnName = datas.get(position).bucketName;
		getActivity().onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case TitleBarView.ID_RIGHT_BUTTON:
			getActivity().onBackPressed();
			break;
		default:
			break;
		}
	}

}
