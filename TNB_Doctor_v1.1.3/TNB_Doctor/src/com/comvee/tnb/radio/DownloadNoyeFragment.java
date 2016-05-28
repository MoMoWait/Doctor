package com.comvee.tnb.radio;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.common.MyBaseAdapter;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.tnb.model.RadioGroup.RadioAlbum;
import com.comvee.tnb.network.NetworkCallBack;
import com.comvee.tnb.network.RadioAlbumLoader;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ViewHolder;
import com.comvee.util.BundleHelper;

/**
 * 
 * @author friendlove-pc 还没有下载的节目
 */
public class DownloadNoyeFragment extends BaseFragment implements OnClickListener, OnItemClickListener {

	private RadioAlbum mAlbum;
	private MyBaseAdapter<RadioAlbumItem> mAdapter;
	private ArrayList<RadioAlbumItem> mData;

	@Override
	public int getViewLayoutId() {
		return R.layout.download_noye_frag;
	}

	@Override
	public void onLaunch(Bundle dataBundle) {
		super.onLaunch(dataBundle);

		mAlbum = BundleHelper.getObjecByBundle(RadioAlbum.class, dataBundle);

		TitleBarView titlebar = (TitleBarView) findViewById(R.id.layout_top);
		titlebar.setTitle(mAlbum.radioTitle);
		titlebar.setLeftButton(R.drawable.top_menu_back, this);

		findViewById(R.id.btn_start_down).setOnClickListener(this);
		final ListView listView = (ListView) findViewById(R.id.listview);
		RadioAlbumLoader loader = new RadioAlbumLoader();
		loader.loadNoyeDownload(mAlbum.radioId, new NetworkCallBack() {
			@Override
			public void callBack(int what, int status, Object obj) {
				mData = (ArrayList<RadioAlbumItem>) obj;
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
							}
						});
					}
				};
				listView.setAdapter(mAdapter);
				listView.setOnItemClickListener(DownloadNoyeFragment.this);
			}
		});

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {

		if (!mData.get(pos).locationHas) {
			CheckBox cb = (CheckBox) view.findViewById(R.id.cb);
			mData.get(pos).beChecked = !cb.isChecked();
			cb.setChecked(!cb.isChecked());
		}
	}

	public static void toFragment(FragmentActivity act, RadioAlbum mAlbum) {
		FragmentMrg.toFragment(act, DownloadNoyeFragment.class, BundleHelper.getBundleByObject(mAlbum), true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_start_down:
			ArrayList<RadioAlbumItem> tobeDownLoad = new ArrayList<RadioAlbumItem>();
			for (RadioAlbumItem radioAlbumItem : mData) {
				if (radioAlbumItem.beChecked) {
					tobeDownLoad.add(radioAlbumItem);
				}
			}
			DownloadMrg.downlaodAlbum(mAlbum, tobeDownLoad);
			FragmentMrg.toBack(getActivity());
			break;
		case TitleBarView.ID_LEFT_BUTTON:
			getActivity().onBackPressed();
			break;
		default:
			break;
		}
	}

}
