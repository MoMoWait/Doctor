package com.comvee.tnb.dialog;

import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.ui.book.ShareUtil;
import com.comvee.tnb.ui.book.ShareUtil.OnShareItemClickListence;
import com.comvee.tnb.ui.money.GlobalEntity;
import com.comvee.tnb.ui.record.RecordMrg;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 庆祝抢到红包的自定义Dialog
 */
public class CelebrateRedPacketDialog extends DialogFragment {
	private View mRoot;
	private String mMoney;
	private String mShareUrl;// 分享的图片
	private ShareUtil shareUtil;
	private String mBottomMsg;// 底部文字
	public String mTitle;// 头部文在

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));// 设置背景透明
		mRoot = inflater.inflate(R.layout.dialog_celebrate_red_packet, container, true);
		onLuanch();
		return mRoot;
	}

	private void onLuanch() {
		((TextView) mRoot.findViewById(R.id.tv_money)).setText(mMoney);
		((TextView) mRoot.findViewById(R.id.tv_title)).setText(mTitle);
		((TextView) mRoot.findViewById(R.id.tv_content)).setText(mBottomMsg);
		((TextView) mRoot.findViewById(R.id.tv_share_weibo)).setOnClickListener(onClickListner);
		((TextView) mRoot.findViewById(R.id.tv_share_weixin)).setOnClickListener(onClickListner);
		((TextView) mRoot.findViewById(R.id.tv_share_friends)).setOnClickListener(onClickListner);
		((TextView) mRoot.findViewById(R.id.tv_share_qq)).setOnClickListener(onClickListner);
		mRoot.findViewById(R.id.iv_close).setOnClickListener(onClickListner);
		final ImageView ivBomb = (ImageView) mRoot.findViewById(R.id.iv_bomb);
		final AnimationDrawable anim = (AnimationDrawable) ivBomb.getBackground();// 领取到红包爆炸效果动画
		anim.start();
	}

	/**
	 * 监听事件
	 */
	private View.OnClickListener onClickListner = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.iv_close:// 关闭窗口
				dismiss();
				break;
			case R.id.tv_share_weibo:// 分享微博
				initShare(SinaWeibo.NAME);
				break;
			case R.id.tv_share_weixin:// 分享微信
				initShare(Wechat.NAME);
				break;
			case R.id.tv_share_friends:// 分享朋友圈
				initShare(WechatMoments.NAME);
				break;
			case R.id.tv_share_qq:// 分享qq
				initShare(QQ.NAME);
				break;
			default:
				break;
			}
		}
	};

	private void initShare(String platform) {
		shareUtil = new ShareUtil();
		shareUtil.setActivity(getActivity());
		shareUtil.setShareImgUrl(mShareUrl);
		shareUtil.initImagePath();
		shareUtil.setTitle("");
		shareUtil.setUrl("");
		shareUtil.setTitleUrl("");
		shareUtil.setShareText("");
		shareUtil.ShareArticle(platform);

	}

	/**
	 * 红包金额
	 * 
	 * @param money
	 */

	public void setMoney(String money) {
		mMoney = money;

	}

	public void setUrl(String url) {
		mShareUrl = url;

	}

	public void setmBottomMsg(String mBottomMsg) {
		this.mBottomMsg = mBottomMsg;
	}

	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}

}
