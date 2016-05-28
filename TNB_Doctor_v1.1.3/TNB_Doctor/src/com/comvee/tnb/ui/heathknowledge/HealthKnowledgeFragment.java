package com.comvee.tnb.ui.heathknowledge;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.ComveeTask;
import com.comvee.tnb.ui.book.BookIndexRootFragment;
import com.comvee.tnb.widget.GridView4Conflict;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.AppUtil;
import com.comvee.tool.UITool;
import com.comvee.ui.RoundedImageView;

public class HealthKnowledgeFragment extends BaseFragment implements OnClickListener, OnItemClickListener {
	private GridView gridView;
	private HeathGridViewAdapter gridViewAdapter;
	private ArrayList<HeathGrideItemModel> gridModels;
	private MyViewPager viewPager;
	private List<HeathViewPageModel> viewPagerData;
	private HealthViewPagerAdapter viewPagerAdapter;
	private RoundedImageView imageView;
	private MyHandler myHandler;
	private TitleBarView mBarView;

	@Override
	public int getViewLayoutId() {
		return R.layout.heath_knowledge_fragment;
	}

	public static BaseFragment newInstance() {
		HealthKnowledgeFragment frag = new HealthKnowledgeFragment();
		return frag;
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			setFoodImageWH();
			if (viewPagerData != null && gridModels != null) {
				initViewPager();
				initGridView();
			} else {
				if (AppUtil.getAPNType(getContext()) != -1) {
					requestHeathKnowledge();
				} else {
					parseHeathKnowledge(ComveeHttp.getCache(getApplicationContext(), ConfigUrlMrg.HEATH_KNOWLEDGE));
				}
			}
		};
	};

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		mBarView.setTitle(getString(R.string.title_info_known));
		mBarView.setRightButton(R.drawable.jkzs_03, this);
		imageView = (RoundedImageView) findViewById(R.id.tnb_class);
		imageView.setOnClickListener(this);
		// 延迟请求数据，避免卡顿
		handler.sendEmptyMessageDelayed(1, 300);
	}

	// private float getDisplauyW() {
	// DisplayMetrics mDisplayMetrics = new DisplayMetrics();
	// getActivity().getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
	// final float W = mDisplayMetrics.widthPixels;
	// return W;
	// }

	private void setFoodImageWH() {
		int W = (int) UITool.getDisplayWidth(getActivity());
		float pad = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getActivity().getResources().getDisplayMetrics());
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.group_of_viewpage);
		layout.setLayoutParams(new LayoutParams(W, (W * 506) / 1242));
		imageView.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, (int) ((W - 2 * pad) * 370 / 1182)));
	}

	private void initViewPager() {
		if (viewPagerData == null || viewPagerData.size() == 0) {
			return;
		}
		viewPager = (MyViewPager) findViewById(R.id.viewpager);
		viewPagerAdapter = new HealthViewPagerAdapter(viewPagerData, (ViewGroup) findViewById(R.id.indicator_layout), getActivity(), true);
		viewPager.setAdapter(viewPagerAdapter);
		// viewPager.setCurrentItem(Integer.MAX_VALUE/2);
		myHandler = new MyHandler(viewPager);
		myHandler.sendEmptyMessage(0);
	}

	private void initGridView() {
		if (viewPagerData == null) {
			return;
		}
		gridView = (GridView4Conflict) findViewById(R.id.knowledge_list);
		gridViewAdapter = new HeathGridViewAdapter(mContext, gridModels);
		gridView.setAdapter(gridViewAdapter);
		gridView.setOnItemClickListener(this);
		imageView.setImageResource(R.drawable.tem_health1);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case TitleBarView.ID_RIGHT_BUTTON:
			toFragment(SearchFragment.newInstance(), true, false);
			break;
		case R.id.tnb_class:
			toFragment(HealthKnowledgeListFrament.newInstance(), true, true);
			break;

		default:
			break;
		}
	}

	class MyHandler extends Handler {
		MyViewPager mViewPager;
		final int PLAY_DELAY = 6000;// 轮播间隔

		public MyHandler(MyViewPager viewPager) {
			this.mViewPager = viewPager;
		}

		@Override
		public void handleMessage(Message msg) {
			mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
			this.sendEmptyMessageDelayed(0, PLAY_DELAY);
		}
	}

	class HeathGridViewAdapter extends BaseAdapter {
		private ArrayList<HeathGrideItemModel> mList;
		private Context context;

		public HeathGridViewAdapter(Context context, ArrayList<HeathGrideItemModel> arrayList) {
			this.context = context;
			this.mList = arrayList;
		}

		public void setmList(ArrayList<HeathGrideItemModel> mList) {
			this.mList = mList;
		}

		@Override
		public int getCount() {
			return mList == null ? 0 : mList.size();
		}

		@Override
		public Object getItem(int i) {
			return mList.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewgroup) {
			HeathGrideItemModel model = mList.get(i);
			if (view == null) {
				view = View.inflate(context, R.layout.item_health_gridview, null);

			}
			RoundedImageView imageView = (RoundedImageView) view.findViewById(R.id.pic);
			// ImageLoaderUtil.getInstance(context).displayImage(model.getHotTypeUrl(),
			// imageView, ImageLoaderUtil.default_options);
			// imageView.setBackgroundResource(mList.get(i));
			view.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, (int) (model.getWidth() * 272 / 576)));
			AppUtil.loadImageByLocationAndNet(imageView, model.getHotTypeUrl());

			return view;
		}
	}

	private void requestHeathKnowledge() {
		showProgressDialog(getString(R.string.msg_loading));
		new ComveeTask<String>() {

			@Override
			protected String doInBackground() {
				ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.HEATH_KNOWLEDGE);
				String result = http.startSyncRequestString();
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				parseHeathKnowledge(result);
				cancelProgressDialog();
			}
		}.execute();
	}

	private float getGridItemWidth() {
		float disW = UITool.getDisplayWidth(getActivity());
		float pad = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getActivity().getResources().getDisplayMetrics());
		float width = (disW - 3 * pad) / 2;
		return width;
	}

	private void parseHeathKnowledge(String result) {
		if (result == null) {
			return;
		}
		try {
			ComveePacket packet = ComveePacket.fromJsonString(result);
			if (packet.getResultCode() == 0) {
				float width = getGridItemWidth();
				JSONObject body = packet.optJSONObject("body");
				JSONArray turnsList = body.optJSONArray("turnsList");
				JSONArray hots = body.optJSONArray("hotTypeList");
				gridModels = new ArrayList<HeathGrideItemModel>();
				for (int i = 0; i < hots.length(); i++) {
					JSONObject object = hots.optJSONObject(i);
					HeathGrideItemModel model = new HeathGrideItemModel();
					model.setHotType(object.optString("hotType"));
					model.setHotTypeUrl(object.optString("hotTypeUrl"));
					model.setUrl(object.optString("url") + "&type=android");
					model.setTitle(object.optString("hotTypeName"));
					model.setWidth(width);
					gridModels.add(model);

				}

				viewPagerData = new ArrayList<HeathViewPageModel>();
				for (int i = 0; i < turnsList.length(); i++) {
					JSONObject object = turnsList.optJSONObject(i);
					HeathViewPageModel pageModel = new HeathViewPageModel();
					pageModel.setHot_spot_id(object.optString("hot_spot_id"));
					pageModel.setTurnsPlayStatus(object.optInt("turnsPlayStatus"));
					pageModel.setTurnsPlayUrl(object.optString("turnsPlayUrl"));
					pageModel.setHotSpotTitle(object.optString("hot_spot_title"));
					pageModel.setUrl(object.optString("url"));
					pageModel.setType(object.optInt("type"));
					// viewPagerData.add(pageModel);
					viewPagerData.add(pageModel);
				}
				ComveeHttp.setCache(getApplicationContext(), ConfigUrlMrg.HEATH_KNOWLEDGE, ConfigParams.WEEK_TIME_LONG, result);
				initGridView();

				initViewPager();

			} else {
				ComveeHttpErrorControl.parseError(getActivity(), packet);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			cancelProgressDialog();
			e.printStackTrace();
		}

	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		HeathGrideItemModel model = (HeathGrideItemModel) arg0.getAdapter().getItem(arg2);
		toFragment(BookIndexRootFragment.newInstance(false, model.getUrl(), model.getTitle()), true, true);
	}
}
