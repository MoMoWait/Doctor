package com.comvee.tnb.guides;

import org.json.JSONArray;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.widget.TitleBarView;

public class GuideHealthFullCheck extends BaseFragment implements OnClickListener {
	private GuideFullCheckInfo fcInfo;
	private LinearLayout layoutList;
	private TitleBarView mBarView;

	public void setFullCheckInfo(GuideFullCheckInfo mInfo) {
		this.fcInfo = mInfo;
	}

	public GuideHealthFullCheck() {
	}

	public static GuideHealthFullCheck newInstance(GuideFullCheckInfo info) {
		GuideHealthFullCheck frag = new GuideHealthFullCheck();
		frag.setFullCheckInfo(info);
		return frag;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.guides_health_full_check;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		if (fcInfo == null) {
			return;
		}
		TextView tvTitle = (TextView) findViewById(R.id.tv_title);
		TextView tvDesc = (TextView) findViewById(R.id.tv_desc);
		Button btnStart = (Button) findViewById(R.id.btn_start);
		btnStart.setOnClickListener(this);

		layoutList = (LinearLayout) findViewById(R.id.layout_conent);
		tvTitle.setText(fcInfo.getTitle());
		tvDesc.setText(fcInfo.getDesc());
		btnStart.setText(fcInfo.getButton());
		String list = fcInfo.getList();
		if (TextUtils.isEmpty(fcInfo.getTitle())) {
			mBarView.setTitle(fcInfo.getTitle());
		}
		JSONArray array;
		try {
			array = new JSONArray(list);
			for (int i = 0; i < array.length(); i++) {
				addText(array.getString(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void addText(String str) {
		View view = View.inflate(getApplicationContext(), R.layout.guides_result_item, null);
		TextView tv = (TextView) view.findViewById(R.id.tv_text);
		tv.setText(str);
		layoutList.addView(view);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btn_start) {
			if (fcInfo != null) {
				GuideMrg.getInstance().jumpToIndex(this, fcInfo);
			} else {
				showToast(getResources().getString(R.string.error));
			}
		}

	}

}
