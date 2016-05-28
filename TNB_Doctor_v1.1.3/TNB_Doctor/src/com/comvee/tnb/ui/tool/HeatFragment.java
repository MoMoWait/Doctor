package com.comvee.tnb.ui.tool;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.FoodMode;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tnb.widget.XExpandableListView.IXListViewListener;
import com.comvee.tool.AppUtil;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UserMrg;

public class HeatFragment extends BaseFragment implements OnClickListener, OnItemClickListener, OnHttpListener {

	private ArrayList<FoodMode> listItems = null;
	private GridView gridView;
	private MyAdapter mAdapter;
	private LinearLayout toSearchLayout;
	private TitleBarView mBarView;
	private Class<? extends Fragment> class1;
	private int fromwhere = -1;
	public static final int FROM_SELECT_EAT_FOOD = 0;
	public static final int FROM_SELECT_EXCHANGE_FOOD = 1;

	public static void toFragment(FragmentActivity fragment, Class<? extends Fragment> class1) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("class", class1);
		FragmentMrg.toFragment(fragment, HeatFragment.class, bundle, true);
	}

	public static void toFragment(FragmentActivity fragment, int fromWhere) {
		Bundle bundle = new Bundle();
		bundle.putInt("fromwhere", fromWhere);
		FragmentMrg.toFragment(fragment, HeatFragment.class, bundle, true);
	}

	public HeatFragment() {
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_food;
	}

	@Override
	public void onStart() {

		super.onStart();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLaunch(Bundle bundle) {
		super.onLaunch(bundle);
		if (bundle != null) {
			class1 = (Class<? extends Fragment>) bundle.getSerializable("class");
			fromwhere = bundle.getInt("fromwhere", -1);
		}
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		init();
		requestHeat();

	}

	private void init() {
		mBarView.setTitle("食物库");
		toSearchLayout = (LinearLayout) findViewById(R.id.tosearch);
		toSearchLayout.setOnClickListener(this);
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setOnItemClickListener(this);
		mAdapter = new MyAdapter();
		gridView.setAdapter(mAdapter);

	}

	private void requestHeat() {
		showProgressDialog("请稍候...");
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.HEAT);
		http.setPostValueForKey("food_cate", "0");
		if (fromwhere == FROM_SELECT_EAT_FOOD) {
			http.setPostValueForKey("status", "1");
		}
		http.setOnHttpListener(1, this);
		http.setNeedGetCache(true, UserMrg.getCacheKey(ConfigUrlMrg.HEAT + fromwhere));
		http.startAsynchronous();
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		cancelProgressDialog();
		switch (what) {
		case 1:
			parseHeat(b, fromCache);
			break;
		default:
			break;
		}

	}

	private void parseHeat(byte[] b, boolean fromCache) {

		try {
			ComveePacket packet = ComveePacket.fromJsonString(b);
			JSONObject body = packet.optJSONObject("body");
			if (body.getBoolean("success")) {
				if (listItems == null) {
					listItems = new ArrayList<FoodMode>();
				} else {
					listItems.clear();
				}

				JSONArray array = body.getJSONArray("obj");
				int len = array.length();
				for (int i = 0; i < len; i++) {
					JSONObject mJsonObject = array.getJSONObject(i);
					FoodMode food = new FoodMode();
					food.id = mJsonObject.getString("id");
					food.pid = mJsonObject.getString("pid");
					food.name = mJsonObject.getString("text");
					food.imageSrc = mJsonObject.getString("imgUrl");
					listItems.add(food);
				}
				mAdapter.notifyDataSetChanged();
				if (b != null && !"".equals(b) && len > 0 & !fromCache) {
					ComveeHttp.setCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.HEAT + fromwhere), ConfigParams.DAY_TIME_LONG, b);
				}
			} else {
				showToast(packet.getResultMsg());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onFialed(int what, int errorCode) {
		cancelProgressDialog();
		ComveeHttpErrorControl.parseError(getActivity(), errorCode);
	}

	class MyAdapter extends BaseAdapter {
		final int paddingWidth = 8;// dp
		int imageHeight;

		public MyAdapter() {
			imageHeight = (int) ((AppUtil.getScreenWidth(getActivity()) - AppUtil.getDeviceDensity(getActivity()) * paddingWidth) / 2);
		}

		@Override
		public int getCount() {
			return listItems == null ? 0 : listItems.size();
		}

		@Override
		public FoodMode getItem(int arg0) {
			return listItems.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			ViewHolder holder = null;
			FoodMode info = listItems.get(arg0);
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(), R.layout.item_food, null);
				holder.tvTitle = (TextView) convertView.findViewById(R.id.title);
				holder.imageView = (ImageView) convertView.findViewById(R.id.imageview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			LayoutParams layoutParams = holder.imageView.getLayoutParams();
			layoutParams.height = imageHeight;
			layoutParams.width = imageHeight;
			holder.imageView.setLayoutParams(layoutParams);
			ImageLoaderUtil.getInstance(getApplicationContext()).displayImage(info.imageSrc, holder.imageView, ImageLoaderUtil.default_options);

			holder.tvTitle.setText(info.name);
			return convertView;
		}

		class ViewHolder {
			TextView tvTitle;
			ImageView imageView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		FoodMode info = (FoodMode) parent.getAdapter().getItem(position);
		HeatListFragment.toFragment(getActivity(), info.id, "", fromwhere);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.tosearch) {
			SearchFragment.toFragment(getActivity(), "", "", fromwhere);
		}
	}
}
