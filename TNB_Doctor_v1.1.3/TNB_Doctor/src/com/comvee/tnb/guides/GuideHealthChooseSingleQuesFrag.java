package com.comvee.tnb.guides;

import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ImageLoaderUtil;

public class GuideHealthChooseSingleQuesFrag extends BaseFragment implements OnClickListener {
	private GuideQuesInfo mInfo;
	private ImageView mImage;
	private LinkedList<String> valueList = new LinkedList<String>();
	private TextView btnOk;
	private TextView btnNo;
	private TitleBarView mBarView;

	// public String getImgDirPath() {
	// return "file:///android_asset/index_img/";
	// }

	public Drawable getDrawableFromRes(String name) {
		int resID = getResources().getIdentifier(name, "drawable", getApplicationContext().getPackageName());
		return resID != 0 ? getResources().getDrawable(resID) : null;
	}

	public GuideHealthChooseSingleQuesFrag() {
	}

	public static GuideHealthChooseSingleQuesFrag newInstance(GuideQuesInfo info) {
		GuideHealthChooseSingleQuesFrag frag = new GuideHealthChooseSingleQuesFrag();
		frag.setGuideInfo(info);
		return frag;
	}

	private void setGuideInfo(GuideQuesInfo info) {
		this.mInfo = info;
	}

	@SuppressLint("NewApi")
	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		if (mInfo == null) {
			return;
		}
		mImage = (ImageView) findViewById(R.id.iv_ask_icon);

		final TextView tvTitle = (TextView) findViewById(R.id.tv_ask_title);
		if (TextUtils.isEmpty(mInfo.getTopicKeyword())) {
			tvTitle.setText(Html.fromHtml(mInfo.getTopicTitle()));
		} else {
			// 关键字 放大粗体
			String title = mInfo.getTopicTitle();
			String[] keys = mInfo.getTopicKeyword().split("$$");
			for (String key : keys) {
				// title = title.replace(mInfo.getTopicKeyword(),
				// "<strong><big> " + key + " </big></strong>");
				title = title.replace(mInfo.getTopicKeyword(),
						"<font color=" + TNBApplication.getInstance().getResources().getColor(R.color.theme_color_green) + "><strong>" + key
								+ "</strong></font>");
			}
			tvTitle.setText(Html.fromHtml(title));
		}

		// String path = getImgDirPath() + File.separator;
		// Drawable d = getDrawableFromRes(mInfo.getTopicIcon());
		// if (d != null) {
		// mImage.setImageDrawable(d);
		// }
		ImageLoaderUtil.getInstance(getContext()).displayImage(mInfo.getTopicIcon(), mImage, ImageLoaderUtil.default_options);

		if (mInfo.getItems().size() == 2) {

			btnOk = (TextView) findViewById(R.id.tv_ok);
			btnNo = (TextView) findViewById(R.id.tv_no);

			btnOk.setOnClickListener(this);
			btnNo.setOnClickListener(this);

			btnOk.setText(mInfo.getItems().get(0).getItemTitle());
			btnNo.setText(mInfo.getItems().get(1).getItemTitle());

			btnOk.setTag(mInfo.getItems().get(0));
			btnNo.setTag(mInfo.getItems().get(1));

		}
		if (mInfo.getTitleBar() != null && mInfo != null) {
			mBarView.setTitle(mInfo.getTitleBar());
		}

		// if(valueList.size()>0
		// &&(valueList.get(mInfo.getTopicSeq()-1)!=null)){
		// if(valueList.get(mInfo.getTopicSeq()-1).equals("1")){
		// btnOk.setPressed(true);
		// btnNo.setPressed(false);
		// }else if(valueList.get(mInfo.getTopicSeq()-1).equals("2")){
		// btnNo.setPressed(true);
		// btnOk.setPressed(false);
		// }
		// }
		// 存储回答过的答案
		if (valueList.peekLast() != null) {
			if (valueList.peekLast().equals("1")) {
				btnOk.setPressed(true);
				btnNo.setPressed(false);
			} else if (valueList.peekLast().equals("2")) {
				btnNo.setPressed(true);
				btnOk.setPressed(false);
			}
		}
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_guides_health_single_question;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.tv_no || v.getId() == R.id.tv_ok) {
			GuideItemInfo info = (GuideItemInfo) v.getTag();
			// if(valueList.size()>0){
			// valueList.removeLast();
			// }
			valueList.add(info.getItemValue());
			if (mInfo != null) {
				GuideMrg.getInstance().jumpNextQuesTask(this, mInfo, info.getHasGoto(), info.getItemValue());
			} else {
				showToast(getResources().getString(R.string.error));
			}
		}

	}
}
