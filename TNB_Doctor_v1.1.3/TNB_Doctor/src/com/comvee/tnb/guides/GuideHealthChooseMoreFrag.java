package com.comvee.tnb.guides;

import java.util.LinkedList;

import android.annotation.SuppressLint;
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

public class GuideHealthChooseMoreFrag extends BaseFragment implements OnClickListener {
	private GuideQuesInfo mInfo;
	private TextView btn0;
	private TextView btn1;
	private TextView btn2;
	private LinkedList<String> valueList = new LinkedList<String>();
	private TitleBarView mBarView;

	public static GuideHealthChooseMoreFrag newInstance(GuideQuesInfo info) {
		GuideHealthChooseMoreFrag frag = new GuideHealthChooseMoreFrag();
		frag.setGuideInfo(info);
		return frag;
	}

	public void setGuideInfo(GuideQuesInfo info) {
		this.mInfo = info;
	}

	public GuideHealthChooseMoreFrag() {

	}

	@Override
	public int getViewLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_guides_health_more_question;
	}

	@SuppressLint("NewApi")
	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		if (mInfo == null) {
			return;
		}
		ImageView ivIcon = (ImageView) findViewById(R.id.iv_ask_icon);

		final TextView tvTitle = (TextView) findViewById(R.id.tv_ask_title);
		if (TextUtils.isEmpty(mInfo.getTopicKeyword())) {
			tvTitle.setText(Html.fromHtml(mInfo.getTopicTitle()));
		} else {
			// 关键字 放大粗体
			String title = mInfo.getTopicTitle();
			String[] keys = mInfo.getTopicKeyword().split("$$");
			for (String key : keys) {
				title = title.replace(mInfo.getTopicKeyword(),
						"<font color=" + TNBApplication.getInstance().getResources().getColor(R.color.theme_color_green) + "><strong>" + key
								+ "</strong></font>");
			}
			tvTitle.setText(Html.fromHtml(title));

		}

		// String path = getImgDirPath() + File.separator;
		// Drawable d = FileUtils.getDrawableFromFile(path + mInfo.getIcon()
		// + ".png");
		// Drawable d = getDrawableFromRes(mInfo.getTopicIcon());
		// if (d != null) {
		// ivIcon.setImageDrawable(d);
		// }
		ImageLoaderUtil.getInstance(getContext()).displayImage(mInfo.getTopicIcon(), ivIcon, ImageLoaderUtil.default_options);

		if (mInfo.getItems().size() == 3) {

			btn0 = (TextView) findViewById(R.id.tv_0);
			btn1 = (TextView) findViewById(R.id.tv_1);
			btn2 = (TextView) findViewById(R.id.tv_2);

			btn0.setOnClickListener(this);
			btn1.setOnClickListener(this);
			btn2.setOnClickListener(this);

			btn0.setText(mInfo.getItems().get(0).getItemTitle());
			btn1.setText(mInfo.getItems().get(1).getItemTitle());
			btn2.setText(mInfo.getItems().get(2).getItemTitle());

			btn0.setTag(mInfo.getItems().get(0));
			btn1.setTag(mInfo.getItems().get(1));
			btn2.setTag(mInfo.getItems().get(2));
		}
		if (mInfo.getTitleBar() != null && mInfo != null) {
			mBarView.setTitle(mInfo.getTitleBar());
		}

		if (valueList.peekLast() != null) {
			if (valueList.peekLast().equals("1")) {
				btn0.setPressed(true);
				btn1.setPressed(false);
				btn2.setPressed(false);
			} else if (valueList.peekLast().equals("2")) {
				btn0.setPressed(false);
				btn1.setPressed(true);
				btn2.setPressed(false);
			} else if (valueList.peekLast().equals("3")) {
				btn0.setPressed(false);
				btn1.setPressed(false);
				btn2.setPressed(true);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.tv_1 || v.getId() == R.id.tv_2 || v.getId() == R.id.tv_0) {
			GuideItemInfo info = (GuideItemInfo) v.getTag();
			valueList.add(info.getItemValue());
			if (mInfo != null) {
				GuideMrg.getInstance().jumpNextQuesTask(this, mInfo, info.getHasGoto(), info.getItemValue());
			} else {
				showToast(getResources().getString(R.string.error));
			}
		}

	}

}
