package com.comvee.tnb.radio;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.db.DownloadItemDao;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.tnb.model.RadioGroup.RadioAlbum;
import com.comvee.tnb.network.CheckCollectLoader;
import com.comvee.tnb.network.NetworkCallBack;
import com.comvee.tnb.network.RadioAlbumLoader;
import com.comvee.tnb.network.RadioCollectRequest;
import com.comvee.tnb.ui.book.ShareUtil;
import com.comvee.tnb.ui.book.ShareUtil.OnShareItemClickListence;
import com.comvee.tnb.view.RadioPlayView;
import com.comvee.tnb.view.RadioPlayView.PlayerViewListener;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UITool;
import com.comvee.util.BundleHelper;

public class ProgrammeListFrag extends BaseFragment implements OnClickListener, OnItemClickListener {

	private DownloadItemDao downloadItemDao;
	private RadioCollectRequest mCollectRequest;
	private ImageView ivCollect;
	private RadioAlbum album;
	private ListView listview;
	private ProgrammeListAdapter mAdapter;
	private ArrayList<RadioAlbumItem> popData = new ArrayList<RadioAlbumItem>();
	private DownloadWindow mDownloadWindow;
	private int prePlayingLocal = 0;
	private CheckCollectLoader checkCollectLoader;
	private RadioPlayView mPlayView;

	@Override
	public int getViewLayoutId() {
		return R.layout.programme_list_frag;
	}

	public static void toFragment(FragmentActivity act, RadioAlbum album) {
		FragmentMrg.toFragment(act, ProgrammeListFrag.class, BundleHelper.getBundleByObject(album), true);
	}

	@Override
	public void onLaunch(Bundle dataBundle) {
		super.onLaunch(dataBundle);
		album = BundleHelper.getObjecByBundle(RadioAlbum.class, dataBundle);
		downloadItemDao = DownloadItemDao.getInstance();
		TitleBarView titleView = (TitleBarView) findViewById(R.id.layout_top);
		titleView.setTitle(album.radioTitle);
		titleView.setVisibility(View.VISIBLE);
		titleView.setLeftButton(R.drawable.top_menu_back, this);

		mPlayView = (RadioPlayView) findViewById(R.id.layout_play_bar);
		listview = (ListView) findViewById(R.id.listview);
		listview.setOnItemClickListener(this);
		listview.addFooterView(new View(getApplicationContext()));

		ImageLoaderUtil.getInstance(getApplicationContext()).displayImage(album.bannerUrl, ((ImageView) findViewById(R.id.iv_bannel)),
				ImageLoaderUtil.null_defult);

		((TextView) findViewById(R.id.tv_label)).setText(album.radioTitle);

		ivCollect = (ImageView) findViewById(R.id.iv_collect);
		ivCollect.setImageResource(album.isCollect == 1 ? R.drawable.radio_program_collect_press : R.drawable.radio_program_collect);
		findViewById(R.id.head_image);
		findViewById(R.id.collect).setOnClickListener(this);
		findViewById(R.id.download).setOnClickListener(this);
		findViewById(R.id.argument).setOnClickListener(this);
		findViewById(R.id.share).setOnClickListener(this);

		if (mAdapter == null) {
			mAdapter = new ProgrammeListAdapter();
			showProgressDialog("请稍后...");
			RadioAlbumLoader loader = new RadioAlbumLoader();
			loader.load(album.radioId, new NetworkCallBack() {

				@Override
				public void callBack(int what, int status, Object obj) {
					cancelProgressDialog();
					ArrayList<RadioAlbumItem> list = (ArrayList<RadioAlbumItem>) obj;
					mAdapter.setData(list);
					mAdapter.notifyDataSetChanged();
				}
			});
		}
		listview.setAdapter(mAdapter);
		mPlayView.setListener(new PlayerViewListener() {

			@Override
			public void onToCurAlbum() {
				RadioPlayFrag.toFragment(getActivity(), RadioPlayerMrg.getInstance().getAlbum(), RadioPlayerMrg.getInstance().getCurrent());
			}

			@Override
			public void onStart(RadioAlbumItem item) {
				if (mAdapter != null) {
					mPlayView.setLabel(item.radioTitle);
					mAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onPause(RadioAlbumItem item) {
				if (mAdapter != null)
					mAdapter.notifyDataSetChanged();
			}
		});
		initCheckLoader();
		setHeadImageWH();
		checkCollectLoader.starCheck(album.radioId, "1");
	}

	/**
	 * 初始化验证是否收藏接口
	 */
	private void initCheckLoader() {
		checkCollectLoader = new CheckCollectLoader(new NetworkCallBack() {
			@Override
			public void callBack(int what, int status, Object obj) {
				if (obj != null) {
					album.isCollect = (Integer) obj;
					ivCollect.setImageResource(album.isCollect == 1 ? R.drawable.radio_program_collect_press : R.drawable.radio_program_collect);
				}
			}
		});
	}

	/**
	 * 设置头部图片宽高
	 */
	private void setHeadImageWH() {
		int W = (int) UITool.getDisplayWidth(getActivity());
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (W * 200) / 640);
		layoutParams.addRule(RelativeLayout.BELOW, R.id.layout_top);
		findViewById(R.id.head_image).setLayoutParams(layoutParams);
	}

	private String setRequestStr() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(album.radioId);
		buffer.append(",");
		buffer.append("1");
		buffer.append(";");
		return buffer.toString();

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case TitleBarView.ID_LEFT_BUTTON:
			onBackPress();
			break;
		case R.id.head_image:
			break;
		case R.id.collect:
			if (album.isCollect != 1) {
				mCollectRequest = new RadioCollectRequest(null);
				mCollectRequest.requestCollect("1", album.radioId);
				album.isCollect = 1;
			} else {
				mCollectRequest = new RadioCollectRequest(null);
				mCollectRequest.requestCancleCollect(setRequestStr());
				album.isCollect = 0;
			}
			ivCollect.setImageResource(album.isCollect == 1 ? R.drawable.radio_program_collect_press : R.drawable.radio_program_collect);

			break;
		case R.id.download:
			showChoosePop();
			break;
		case R.id.argument:
			RadioCommentFrag.toFragment(getActivity(), album.radioId, "1");
			break;
		case R.id.share:
			showShare();
			break;
		}
	}

	private void showShare() {
		final ShareUtil shareUtil = ShareUtil.getInstance(getActivity());

		shareUtil.setTitle(album.radioTitle);
		shareUtil.setShareImgUrl(album.photoUrl);
		shareUtil.setTitleUrl(album.shareHtml);
		shareUtil.setUrl(album.shareHtml);

		shareUtil.addOnShareItemClickListence(new OnShareItemClickListence() {

			@Override
			public void onItemClick(String platform) {
				shareUtil.setShareText(album.radioInfo);
				shareUtil.ShareArticle(platform);
			}
		});
		shareUtil.show(mRoot, Gravity.BOTTOM);
	}

	private void showChoosePop() {

		if (mAdapter.getItems() == null) {
			showToast("没有节目可以下载");
			return;
		}
		popData.clear();
		popData.addAll(mAdapter.getItems());
		mDownloadWindow = new DownloadWindow(popData, album);
		mDownloadWindow.show(findViewById(R.id.oplayout));
		mDownloadWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int pos, long arg3) {
		switch (adapterView.getId()) {
		case R.id.listview:
			mAdapter.getItem(prePlayingLocal).state = 0;
			mAdapter.getItem(pos).state = 1;
			mAdapter.notifyDataSetChanged();
			prePlayingLocal = pos;

			RadioPlayerMrg mrg = RadioPlayerMrg.getInstance();
			mrg.setDataSource(album, mAdapter.getItems(),false);
			mrg.play(pos);

			// RadioPlayFrag.toFragment(getActivity(), album,
			// mAdapter.getItem(pos));
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onBackPress() {
		Bundle bundle = new Bundle();
		bundle.putInt("collect", album.isCollect);
		FragmentMrg.toBack(getActivity(), bundle);
		return true;
	}
}
