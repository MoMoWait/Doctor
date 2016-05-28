package com.comvee.tnb.dialog;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.ui.more.WebFragment;
import com.comvee.tnb.ui.more.WebNewFrag;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UserMrg;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 送现金活动领取任务的自定义Dialog
 */
public class MoneyGetTaskDialog extends DialogFragment {
	private View mRoot;
	private ImageView ivBg;// 背景图片
	private String imageUrl;
	private String htmlUrl;
	private String h5Title;
	private ImageView ivClose;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));// 设置背景透明
		getDialog().getWindow().setWindowAnimations(R.style.event_in_amin);// 从上往下的动画
		mRoot = inflater.inflate(R.layout.dialog_money_get_task, container, true);
		onLuanch();
		return mRoot;
	}

	private void onLuanch() {
		ivBg = (ImageView) mRoot.findViewById(R.id.iv_bg);
		mRoot.findViewById(R.id.tv_get_task).setOnClickListener(onClickListner);
		ivClose = (ImageView) mRoot.findViewById(R.id.iv_close);
		ivClose.setOnClickListener(onClickListner);
		ivClose.setVisibility(View.GONE);
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
				.build();

		ImageLoaderUtil.getInstance(TNBApplication.getInstance()).loadImage(imageUrl, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				super.onLoadingComplete(imageUri, view, loadedImage);
				ivClose.setVisibility(View.VISIBLE);
				ivBg.setImageBitmap(loadedImage);
//
//				if (!loadedImage.isRecycled()) {
//
//					loadedImage.recycle();// 回收图片所占的内存
//
//					System.gc();// 提醒系统及时回收
//
//				}

			}
		});

		// ImageLoaderUtil.getInstance(TNBApplication.getInstance()).displayImage(imageUrl,
		// ivBg, ImageLoaderUtil.default_options);

	}

	/**
	 * 监听事件
	 */
	private View.OnClickListener onClickListner = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.tv_get_task:// 领取任务,跳转h5

				if (!TextUtils.isEmpty(htmlUrl.toString())) {
					htmlUrl += String.format("?sessionID=%s&sessionMemberID=%s", UserMrg.getSessionId(getContext()),
							UserMrg.getMemberSessionId(getContext()));
					WebNewFrag.toFragment(getActivity(), h5Title, htmlUrl);
				}
				dismiss();
				break;
			case R.id.iv_close:// 关闭窗口
				dismiss();
				break;
			default:
				break;
			}
		}
	};

	// 显示图片，
	public void setImageUrl(String url) {
		imageUrl = url;
	}

	// 跳转h5
	public void setH5Url(String url) {
		htmlUrl = url;
	}

	// h5的title
	public void setTitle(String title) {
		h5Title = title;
	}

}
