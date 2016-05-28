package com.comvee.tnb.ui.privatedoctor;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.model.AskServerInfo;
import com.comvee.tnb.model.MemberDoctorInfo;
import com.comvee.tnb.model.MemberDoctorInfo.PackageList;
import com.comvee.tnb.ui.ask.AskQuestionFragment;
import com.comvee.tnb.ui.ask.AskTellListFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.util.BundleHelper;

/**
 * 用户服务列表页
 * 
 * @author Administrator
 * 
 */
public class MemberServerListFrag extends BaseFragment implements OnClickListener {
	private ArrayList<PackageList> serverList;
	private TitleBarView mBarView;
	public static boolean isRequest = true;
	private ListView myListView;
	private MemberServerAdapter mAdapter;
	private MemberDoctorInfo memberServerInfo;
	private View group_list;

	@Override
	public int getViewLayoutId() {
		return R.layout.mem_server_list_frag;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLaunch(Bundle dataBundle) {
		super.onLaunch(dataBundle);
		if (dataBundle != null) {
			memberServerInfo = BundleHelper.getSerializableByBundle(dataBundle);
			serverList = memberServerInfo.serverList;
		}
		init();
		initHeadView(memberServerInfo);
	}

	public static void toFragment(FragmentActivity activity, MemberDoctorInfo memberServerInfo) {
		FragmentMrg.toFragment(activity, MemberServerListFrag.class, BundleHelper.getBundleBySerializable(memberServerInfo), true);
	}

	private void init() {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this, R.drawable.top_menu_back_white);
		mBarView.setTitle(getText(R.string.mem_server_title).toString());
		mBarView.setTitleBarBackgroundColor(getResources().getColor(android.R.color.transparent));
		mBarView.setTextColor(getResources().getColor(R.color.white), getResources().getColor(R.color.white), getResources().getColor(R.color.white));
		mBarView.findViewById(R.id.titlebar_line).setVisibility(View.GONE);
		findViewById(R.id.btn_ask).setOnClickListener(this);
		findViewById(R.id.btn_tell).setOnClickListener(this);
		findViewById(R.id.tn_buy_server).setOnClickListener(this);
		if (memberServerInfo.isPhoneService == 0) {
			findViewById(R.id.btn_tell).setEnabled(false);
		}
		if (memberServerInfo.isBookService == 0) {
			findViewById(R.id.btn_ask).setEnabled(false);
		}
		group_list = findViewById(R.id.group_server_list);
		myListView = (ListView) findViewById(R.id.lv_mem_server_list);
		mAdapter = new MemberServerAdapter(getActivity(), serverList, R.layout.item_mem_server);
		myListView.setAdapter(mAdapter);
		setListViewHeight();
		if (serverList != null && serverList.size() > 0) {
			group_list.setVisibility(View.VISIBLE);
		} else {
			group_list.setVisibility(View.GONE);
		}
		myListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				return true;
			}
		});
	}

	private void setListViewHeight() {
		int totalHeight = 0;
		for (int i = 0, len = mAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = mAdapter.getView(i, null, myListView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = myListView.getLayoutParams();
		params.height = totalHeight + (myListView.getDividerHeight() * (mAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		myListView.setLayoutParams(params);
	}

	private void initHeadView(MemberDoctorInfo info) {
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
		ImageLoaderUtil.getInstance(mContext).displayImage(info.PER_PER_REAL_PHOTO, imgPhoto, ImageLoaderUtil.doc_options);
		tvDocName.setText(info.PER_NAME);
		tvPosition.setText(info.PER_POSITION);
		String addrs = null;
		if (TextUtils.isEmpty(info.HOS_NAME) && !TextUtils.isEmpty(info.DEPARTMENT)) {
			addrs = info.DEPARTMENT;
		} else if (!TextUtils.isEmpty(info.HOS_NAME) && TextUtils.isEmpty(info.DEPARTMENT)) {
			addrs = info.HOS_NAME;
		} else {
			addrs = info.HOS_NAME + "—" + info.DEPARTMENT;
		}
		tv_is_my_doc.setVisibility(View.VISIBLE);
		tv_doc_addrss.setText(addrs);
		tv_doc_describe.setText(info.PER_SPACIL);
		if (!TextUtils.isEmpty(info.TAGS)) {
			String str[] = info.TAGS.replace("^$%", "@").split("@");
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
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_ask:
			AskServerInfo info = new AskServerInfo();
			info.setDoctorName(memberServerInfo.PER_NAME);
			info.setDoctorId(memberServerInfo.USER_ID);
			info.setDocTellId(memberServerInfo.USER_ID);
			AskQuestionFragment.toFragment(getActivity(), info);
			break;
		case R.id.btn_tell:
			toFragment(AskTellListFragment.newInstance(memberServerInfo.USER_ID), true, true);
			break;
		case R.id.tn_buy_server:
			DoctorServerList.toFragment(getActivity(), memberServerInfo.USER_ID, true);
			break;
		default:
			break;
		}

	}

}
