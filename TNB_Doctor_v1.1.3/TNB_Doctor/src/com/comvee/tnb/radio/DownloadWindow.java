package com.comvee.tnb.radio;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alipay.mobilesecuritysdk.deviceID.LOG;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.common.MyBaseAdapter;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.tnb.model.RadioGroup.RadioAlbum;
import com.comvee.tool.Log;
import com.comvee.tool.ViewHolder;
import com.comvee.util.Util;

public class DownloadWindow extends PopupWindow implements OnItemClickListener, OnClickListener {
	private View mRootView;
	private Context mContext;
	private ArrayList<RadioAlbumItem> mData;
	private MyBaseAdapter<RadioAlbumItem> mAdapter;
	private RadioAlbum mAlbum;
	private boolean isCheck;
	private TextView tv_check;
	private ArrayList<RadioAlbumItem> tobeDownLoad;

	public DownloadWindow(ArrayList<RadioAlbumItem> popData, RadioAlbum album) {
		super(TNBApplication.getInstance());
		mContext = TNBApplication.getInstance();
		this.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
		this.mData = popData;
		tobeDownLoad = new ArrayList<RadioAlbumItem>();
		mAlbum = album;
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
		mRootView = View.inflate(TNBApplication.getInstance(), R.layout.programme_down_list_frag, null);
		tv_check = (TextView) mRootView.findViewById(R.id.uncheck);
		tv_check.setOnClickListener(this);
		mRootView.findViewById(R.id.finish).setOnClickListener(this);
		mRootView.findViewById(R.id.start_down).setOnClickListener(this);

		((TextView) mRootView.findViewById(R.id.tv_label)).setText(mAlbum.radioTitle);

		ListView mList = (ListView) mRootView.findViewById(R.id.pop_listview);
		mList.setOnItemClickListener(this);
		mAdapter = new MyBaseAdapter<RadioAlbumItem>(mContext, mData, R.layout.download_item) {
			@Override
			protected void doyouself(ViewHolder holder, int position) {
				final RadioAlbumItem radioAlbumItem = mData.get(position);
				TextView titleTv = holder.get(R.id.title);
				TextView timeTv = holder.get(R.id.time);
				CheckBox cb = holder.get((R.id.cb));
				titleTv.setText(radioAlbumItem.radioTitle);
				timeTv.setText(RadioUtil.getMedaioDurationString(radioAlbumItem.timeLong));
				if (radioAlbumItem.locationHas) {
					cb.setVisibility(View.INVISIBLE);
					timeTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.prog6, 0, R.drawable.radio_download_complete, 0);
				} else {
					timeTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.prog6, 0, 0, 0);
				}
				cb.setChecked(radioAlbumItem.beChecked);
				cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean checked) {
						radioAlbumItem.beChecked = checked;
						if (checked) {
							tobeDownLoad.add(radioAlbumItem);
						} else {
							tobeDownLoad.remove(radioAlbumItem);
						}
						tv_check.setText(mData.size() == tobeDownLoad.size() ? mContext.getString(R.string.no_select_all) :  mContext.getString(R.string.select_all));
					}
				});
			}
		};
		mList.setAdapter(mAdapter);

	}

	public void show(View view) {
		showAtLocation(view, Gravity.BOTTOM, 0, 0);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		if (!mData.get(pos).locationHas) {
			CheckBox cb = (CheckBox) view.findViewById(R.id.cb);
			mData.get(pos).beChecked = !cb.isChecked();
			cb.setChecked(!cb.isChecked());

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.uncheck:
			isCheck = !isCheck;
			for (RadioAlbumItem radioAlbumItem : mData) {
				if (!radioAlbumItem.locationHas) {
					radioAlbumItem.beChecked = isCheck;
				}
			}
			tv_check.setText(isCheck ? mContext.getString(R.string.no_select_all) : mContext.getString(R.string.select_all));
			mAdapter.notifyDataSetChanged();
			break;
		case R.id.finish:
			dismiss();
			break;
		case R.id.start_down:
			dismiss();
			for (RadioAlbumItem radioAlbumItem : mData) {
				if (radioAlbumItem.beChecked) {
					radioAlbumItem.locationHas = true;
				}
			}
			DownloadMrg.downlaodAlbum(mAlbum, tobeDownLoad);
			break;
		}
	}
}
