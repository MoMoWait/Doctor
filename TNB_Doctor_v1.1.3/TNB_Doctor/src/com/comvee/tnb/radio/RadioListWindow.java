package com.comvee.tnb.radio;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.util.Util;

public class RadioListWindow extends PopupWindow {

	private View mRootView;
	private Context mContext;

	public RadioListWindow() {
		super(TNBApplication.getInstance());
		mContext = TNBApplication.getInstance();
		this.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
		createRootView();
		setContentView(mRootView);
		setWidth(-1);
		setHeight(Util.getScreenHeight(mContext) * 3 / 4);
		setBackgroundDrawable(new BitmapDrawable());
		setAnimationStyle(R.style.PopupAnimation_bottom);
		setFocusable(true);
		// setOutTouchCancel(true);
	}

	private void createRootView() {
		mRootView = View.inflate(TNBApplication.getInstance(), R.layout.radio_play_list, null);
		mRootView.findViewById(R.id.btn_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_close:
					dismiss();
					break;
				default:
					break;
				}
			}
		});

		try {
			((TextView) mRootView.findViewById(R.id.tv_label)).setText(RadioPlayerMrg.getInstance().getAlbum().radioTitle);
			ListView mList = (ListView) mRootView.findViewById(R.id.list);
			final ProgrammeListAdapter adpater = new ProgrammeListAdapter();
			adpater.setData(RadioPlayerMrg.getInstance().getAlbums());
			mList.setAdapter(adpater);
			mList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
					RadioPlayerMrg.getInstance().play(position);
					adpater.notifyDataSetChanged();
					dismiss();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void show(View view) {
		showAtLocation(view, Gravity.BOTTOM, 0, 0);
	}

}
