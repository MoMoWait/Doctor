package com.comvee.tnb.ui.ask;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.AskTellListAdapter;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.AskServerInfo;
import com.comvee.tnb.model.AskTellInfo;
import com.comvee.tnb.model.TellDocDetailModel;
import com.comvee.tnb.widget.MyGridView;
import com.comvee.tnb.widget.MyHorizontal;
import com.comvee.tnb.widget.ScrollViewListener;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UITool;
import com.comvee.util.JsonHelper;
import com.comvee.util.TimeUtil;

/**
 * 预约电话列表
 * 
 * @author friendlove-pc
 * 
 */
@SuppressLint("ValidFragment")
public class AskTellListFragment extends BaseFragment implements OnHttpListener, OnItemClickListener, OnClickListener, ScrollViewListener {
	private String Docname;
	private String mDoctorId;
	private Calendar curCalendar;
	private MyGridView mGridView;
	private AskTellListAdapter mAdapter;
	private TitleBarView mBarView;
	// private HorizontalListView horizontalListView;
	private TellDocDetailModel tellDocDetailModel;
	private ArrayList<AskTellInfo> list = new ArrayList<AskTellInfo>();
	// private AskTellTimeHorAdapter askTellTimeHorAdapter;
	private ArrayList<Calendar> dates = new ArrayList<Calendar>();
	private MyHorizontal myHorizontal;
	private int selectDate;
	private float viewWidth;
	private LinearLayout datesLayout;

	@Override
	public int getViewLayoutId() {
		return R.layout.ask_tell_list_fragment;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	public AskTellListFragment() {
	}

	@Override
	public void onLaunch(Bundle bundle) {
		super.onLaunch(bundle);
		if (viewWidth == 0) {
			viewWidth = UITool.getDisplayWidth(getActivity()) / 3;
		}
		getDates();
		init();
		initHeadView(tellDocDetailModel);
		curCalendar = curCalendar == null ? Calendar.getInstance() : curCalendar;
		requestAskTellList(mDoctorId, TimeUtil.fomateTime(curCalendar.getTimeInMillis(), ConfigParams.DATE_FORMAT));
	}

	public static AskTellListFragment newInstance(String mDoctorId) {
		return new AskTellListFragment(mDoctorId);
	}

	@SuppressLint("ValidFragment")
	public AskTellListFragment(String mDoctorId) {
		this.mDoctorId = mDoctorId;
	}

	private void getDates() {
		for (int i = 0; i < 7; i++) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, i);
			dates.add(calendar);
		}
	}

	private void init() {

		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setTitle(getString(R.string.btn_text_of_tell));
		mBarView.setLeftDefault(this, R.drawable.top_menu_back_white);
		mBarView.setTitleBarBackgroundColor(getResources().getColor(R.color.transparent));
		mBarView.setTextColor(getResources().getColor(R.color.white), getResources().getColor(R.color.white), getResources().getColor(R.color.white));
		mBarView.findViewById(R.id.titlebar_line).setVisibility(View.GONE);

		// horizontalListView = (HorizontalListView)
		// findViewById(R.id.time_horizontal);
		// horizontalListView.setOnItemClickListener(this);
		// horizontalListView.setAdapter(askTellTimeHorAdapter);

		myHorizontal = (MyHorizontal) findViewById(R.id.horizontal);
		myHorizontal.addOnScrollViewListener(this);
		datesLayout = (LinearLayout) findViewById(R.id.lin_date);
		mAdapter = new AskTellListAdapter(getActivity(), list, R.layout.ask_tell_item);
		mGridView = (MyGridView) findViewById(R.id.v_gridview);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);
		addDateView(datesLayout);
	}

	private void addDateView(final LinearLayout layout) {
		for (int index = 0; index < 7; index++) {
			View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.ask_tell_time_horizontal, null);
			view.setTag(index);
			view.setOnClickListener(this);
			Calendar time = dates.get(index);
			TextView textView = (TextView) view.findViewById(R.id.tv_data);
			View food = view.findViewById(R.id.view);
			if (index == selectDate) {
				food.setVisibility(View.VISIBLE);
				textView.setTextColor(getResources().getColor(R.color.theme_color_green));
			}
			textView.setText(TimeUtil.fomateTime(time.getTimeInMillis(), ConfigParams.DATE_FORMAT_1) + " "
					+ ConfigParams.strWeeks_1[time.get(Calendar.DAY_OF_WEEK) - 1]);
			view.setLayoutParams(new LinearLayout.LayoutParams((int) viewWidth, LayoutParams.MATCH_PARENT));
			if (layout.getChildAt(index) == null) {
				layout.addView(view);
			}

		}

	}

	private void initHeadView(TellDocDetailModel info) {
		if (info == null) {
			return;
		}
		ImageView imgPhoto = (ImageView) findViewById(R.id.img_photo);
		TextView tvDocName = (TextView) findViewById(R.id.tv_doc_name);
		TextView tvPosition = (TextView) findViewById(R.id.tv_doc_position);
		TextView tv_doc_addrss = (TextView) findViewById(R.id.tv_doc_addrss);
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

	private void requestAskTellList(String doctorId, String dateStr) {
		showProgressDialog(getString(R.string.msg_loading));

		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.ASK_TELL_LIST);
		http.setPostValueForKey("doctorId", doctorId);
		http.setPostValueForKey("dateStr", dateStr);
		http.setOnHttpListener(1, this);
		http.startAsynchronous();

	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		cancelProgressDialog();
		switch (what) {
		case 1:
			try {
				ComveePacket packet = ComveePacket.fromJsonString(b);
				if (packet.getResultCode() == 0) {
					JSONObject body = packet.getJSONObject("body");
					JSONObject doctor = body.getJSONObject("doctor");
					tellDocDetailModel = JsonHelper.getObjecByJsonObject(TellDocDetailModel.class, doctor);
					initHeadView(tellDocDetailModel);
					String leaveNum = body.optString("leaveNum");
					JSONArray json = body.getJSONArray("list");
					int len = json.length();
					list.clear();
					for (int i = 0; i < len; i++) {
						JSONObject obj = json.getJSONObject(i);
						AskTellInfo info = JsonHelper.getObjecByJsonObject(AskTellInfo.class, obj);
						info.leaveNum = leaveNum;
						info.docName = tellDocDetailModel.PER_NAME;
						info.perRealPhoto = tellDocDetailModel.PER_PER_REAL_PHOTO;
						try {
							if (System.currentTimeMillis() > TimeUtil.getUTC(info.planDate + " " + info.endTime, ConfigParams.TIME_FORMAT1)) {
								// 过期
								info.isLast = true;
							} else {
								info.isLast = false;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						list.add(info);
					}
					mAdapter.notifyDataSetChanged();
					setGoneLine();

				} else {
					ComveeHttpErrorControl.parseError(getActivity(), packet);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 2:
			try {
				ComveePacket comveePacket = ComveePacket.fromJsonString(b);
				if (comveePacket.getResultCode() == 0) {
					AskServerInfo info = new AskServerInfo();

					info.setDoctorId(comveePacket.optJSONObject("body").optString("doctorId"));
					info.setDoctorName(Docname);
					ConfigParams.TO_BACK_TYPE = 2;
					AskQuestionFragment.toFragment(getActivity(), info);
				} else {
					ComveeHttpErrorControl.parseError(getActivity(), comveePacket);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onFialed(int what, int errorCode) {
		cancelProgressDialog();
		ComveeHttpErrorControl.parseError(getActivity(), errorCode);
	}

	private void requestIsHaveTell() {
		showProgressDialog(getString(R.string.msg_loading));

		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.NO_TELL_ORDER);
		http.setPostValueForKey("doctorId", mDoctorId);
		http.setOnHttpListener(2, this);
		http.startAsynchronous();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		// if (arg0.getAdapter() instanceof AskTellTimeHorAdapter) {
		// askTellTimeHorAdapter.setSelectIndex(arg2);
		// horizontalListView.setSelection(arg2);
		// } else
		if (arg0.getAdapter() instanceof AskTellListAdapter) {
			AskTellInfo info = mAdapter.getItem(arg2);
			if (info.isLast || info.total - info.use <= 0) {
				return;
			}
			Docname = info.docName;
			if (info.leaveNum != null && !info.leaveNum.equals("0") && !info.leaveNum.equals("")) {
				toFragment(AskTellVerifyFragment.newInstance(mAdapter.getItem(arg2)), true, true);
			} else {
				requestIsHaveTell();
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.tell_date_item:
			arg0.findViewById(R.id.view).setVisibility(View.VISIBLE);
			((TextView) arg0.findViewById(R.id.tv_data)).setTextColor(getResources().getColor(R.color.theme_color_green));
			View view = datesLayout.getChildAt(selectDate);
			view.findViewById(R.id.view).setVisibility(View.INVISIBLE);
			((TextView) view.findViewById(R.id.tv_data)).setTextColor(getResources().getColor(R.color.text_color_1));
			selectDate = (Integer) arg0.getTag();
			requestAskTellList(mDoctorId, TimeUtil.fomateTime(dates.get(selectDate).getTimeInMillis(), ConfigParams.DATE_FORMAT));
			break;
		default:
			break;
		}
	}

	@Override
	public void onScrollChanged(int scrollType) {
		if (scrollType == MyHorizontal.IDLE) {
			int cur = myHorizontal.getScrollX();
			float w1 = (float) cur % viewWidth;
			int num = (int) (cur / viewWidth);
			float halfW = viewWidth / 2;
			if (w1 >= halfW) {
				myHorizontal.smoothScrollTo((int) ((num + 1) * viewWidth), 0);
			} else {
				myHorizontal.smoothScrollTo((int) (num * viewWidth), 0);
			}
		}
	}

	// 隐藏gridview多余的线
	private void setGoneLine() {
		if (mAdapter.getItem(0) == null) {
			return;
		}
		int itemWith = (int) (UITool.getDisplayWidth(getActivity()) / 3);
		TextView tv_remove_bg = (TextView) findViewById(R.id.tv_remove_bg);
		tv_remove_bg.setVisibility(View.VISIBLE);
		int bg_width = 0;
		View item = mAdapter.getView(0, null, mGridView);
		item.measure(0, 0);
		int bg_height = item.getMeasuredHeight() - 2;
		switch (list.size() % 3) {
		case 0:
			tv_remove_bg.setVisibility(View.GONE);
			break;
		case 1:
			bg_width = itemWith * 2 - 2;
			break;
		case 2:
			bg_width = itemWith - 2;
			break;
		default:
			break;
		}
		tv_remove_bg.setWidth(bg_width);
		tv_remove_bg.setHeight(bg_height);
	}
}
