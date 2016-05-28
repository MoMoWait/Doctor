package com.comvee.tnb.ui.privatedoctor;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.DocServerListAdapter;
import com.comvee.tnb.model.DoctorModel;
import com.comvee.tnb.model.DoctorServerListModel;
import com.comvee.tnb.model.PackageModel;
import com.comvee.tnb.network.DoctorServerLoader;
import com.comvee.tnb.network.NetworkCallBack;
import com.comvee.tnb.widget.MyListView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ImageLoaderUtil;

/**
 * 医生服务列表页面
 * 
 * @author Administrator
 * 
 */
public class DoctorServerList extends BaseFragment implements OnItemClickListener {
	private String doctorId;
	private List<PackageModel> priList = new ArrayList<PackageModel>();
	private List<PackageModel> pubList = new ArrayList<PackageModel>();
	private DoctorModel info;
	private TitleBarView mBarView;
	public static boolean isRequest = true;
	private MyListView lv_doc_server_of_private;
	private MyListView lv_server_of_public;
	private View group_private, group_public;
	private DocServerListAdapter priAdapter, pubAdapter;
	private DoctorServerLoader loader;
	private boolean isMyDoc;

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_doc_server_list;
	}

	private void init() {
		findViewById(R.id.tempview).setVisibility(View.GONE);
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setTitle("");
		mBarView.setLeftDefault(this, R.drawable.top_menu_back_white);
		mBarView.setTitleBarBackgroundColor(getResources().getColor(R.color.transparent));
		mBarView.setTextColor(getResources().getColor(R.color.white), getResources().getColor(R.color.white), getResources().getColor(R.color.white));
		mBarView.findViewById(R.id.titlebar_line).setVisibility(View.GONE);

		group_public = findViewById(R.id.group_server_public);
		group_private = findViewById(R.id.group_doc_private);

		lv_server_of_public = (MyListView) findViewById(R.id.lv_server_list_of_public);
		lv_doc_server_of_private = (MyListView) findViewById(R.id.lv_doc_server_list_of_private);

		lv_server_of_public.setOnItemClickListener(this);
		lv_doc_server_of_private.setOnItemClickListener(this);

		priAdapter = new DocServerListAdapter(getActivity(), priList, R.layout.doc_server_item);
		pubAdapter = new DocServerListAdapter(getActivity(), pubList, R.layout.doc_server_item);

		lv_doc_server_of_private.setAdapter(priAdapter);
		lv_server_of_public.setAdapter(pubAdapter);
		initHeadView(info);
		notifyList();
		initLoader();
		if (isRequest || priList.size() == 0) {
			showProgressDialog(getString(R.string.loading));
			ThreadHandler.postUiThread(new Runnable() {
				@Override
				public void run() {
					loader.starLoader(doctorId);
				}
			}, 300);
			isRequest = false;
		}
	}

	public DoctorServerList() {

	}

	private void initLoader() {
		loader = new DoctorServerLoader(new NetworkCallBack() {

			@Override
			public void callBack(int what, int status, Object obj) {
				cancelProgressDialog();
				findViewById(R.id.tempview).setVisibility(View.VISIBLE);
				if (obj instanceof DoctorServerListModel) {
					DoctorServerListModel model = (DoctorServerListModel) obj;
					info = model.info;
					initHeadView(info);
					priList.clear();
					pubList.clear();
					priList.addAll(model.priList);
					pubList.addAll(model.pubList);
					notifyList();
				} else if (obj instanceof String) {
					showToast((String) obj);
					FragmentMrg.toBack(getActivity());
				}
			}
		});
	}

	public static void toFragment(FragmentActivity fragment, String doctorId) {
		toFragment(fragment, doctorId, false);
	}

	public static void toFragment(FragmentActivity fragment, String doctorId, boolean isMyDoc) {
		Bundle bundle = new Bundle();
		bundle.putString("doctorid", doctorId);
		bundle.putBoolean("isMyDoc", isMyDoc);
		FragmentMrg.toFragment(fragment, DoctorServerList.class, bundle, true);
	}

	@Override
	public void onLaunch(Bundle bundle) {
		super.onLaunch(bundle);
		if (bundle != null) {
			doctorId = bundle.getString("doctorid");
			isMyDoc = bundle.getBoolean("isMyDoc");
		}
		init();

	}

	private void notifyList() {
		if (priList.size() > 0) {
			priAdapter.notifyDataSetChanged();
			group_private.setVisibility(View.VISIBLE);
		} else {
			group_private.setVisibility(View.GONE);
		}
		if (pubList.size() > 0) {
			pubAdapter.notifyDataSetChanged();
			group_public.setVisibility(View.VISIBLE);
		} else {
			group_public.setVisibility(View.GONE);
		}
	}

	private void initHeadView(DoctorModel info) {
		if (info == null) {
			return;
		}
		ImageView imgPhoto = (ImageView) findViewById(R.id.img_photo);
		TextView tvDocName = (TextView) findViewById(R.id.tv_doc_name);
		TextView tvPosition = (TextView) findViewById(R.id.tv_doc_position);
		TextView tv_doc_addrss = (TextView) findViewById(R.id.tv_doc_addrss);
		TextView tv_is_my_doc = (TextView) findViewById(R.id.tv_is_my_doc);
		TextView tv_doc_label_1 = (TextView) findViewById(R.id.tv_doc_label_1);
		TextView tv_doc_label_2 = (TextView) findViewById(R.id.tv_doc_label_2);
		TextView tv_doc_label_3 = (TextView) findViewById(R.id.tv_doc_label_3);
		TextView tv_doc_describe = (TextView) findViewById(R.id.tv_doc_describe);
		ImageLoaderUtil.getInstance(mContext).displayImage(info.getPerRealPhotos(), imgPhoto, ImageLoaderUtil.doc_options);
		tvDocName.setText(info.getPerName());
		tvPosition.setText(info.getPosition());
		String addrs = null;
		if (TextUtils.isEmpty(info.getHospitalNameText()) && !TextUtils.isEmpty(info.getDepartmentNameText())) {
			addrs = info.getHospitalNameText();
		} else if (!TextUtils.isEmpty(info.getHospitalNameText()) && TextUtils.isEmpty(info.getDepartmentNameText())) {
			addrs = info.getHospitalNameText();
		} else {
			addrs = info.getHospitalNameText() + "—" + info.getDepartmentNameText();
		}
		tv_doc_addrss.setText(addrs);
		tv_doc_describe.setText(info.getPerSpacil());
		if (info.isMyPackage() || isMyDoc) {
			tv_is_my_doc.setVisibility(View.VISIBLE);
		} else {
			tv_is_my_doc.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(info.getTags())) {
			String str[] = info.getTags().replace("^$%", "@").split("@");
			tv_doc_label_1.setVisibility(View.GONE);
			tv_doc_label_2.setVisibility(View.GONE);
			tv_doc_label_3.setVisibility(View.GONE);
			for (int i = 0; i < str.length; i++) {
				if (TextUtils.isEmpty(str[i]))
					continue;
				switch (i) {
				case 0:
					tv_doc_label_1.setText(str[i]);
					tv_doc_label_1.setVisibility(View.VISIBLE);
					break;
				case 1:
					tv_doc_label_2.setText(str[i]);
					tv_doc_label_2.setVisibility(View.VISIBLE);
					break;
				case 2:
					tv_doc_label_3.setText(str[i]);
					tv_doc_label_3.setVisibility(View.VISIBLE);
					break;
				default:
					break;
				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		PackageModel packageModel = (PackageModel) arg0.getAdapter().getItem(arg2);
		ServerMessageFragment.toFragment(getActivity(), packageModel.getPackageType(), packageModel.getPackageCode(), info.getDoctorId(),
				packageModel.getPackageid() + "", packageModel.getVoucherList());
	}

}
