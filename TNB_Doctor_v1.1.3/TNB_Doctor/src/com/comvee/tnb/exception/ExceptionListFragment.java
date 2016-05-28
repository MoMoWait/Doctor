package com.comvee.tnb.exception;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.ExceptionListAdapter;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.ExceptionList;
import com.comvee.tnb.widget.TitleBarView;

public class ExceptionListFragment extends BaseFragment implements OnHttpListener, OnItemClickListener {
	private ListView lv;
	private List<ExceptionList> lists;
	private ExceptionListAdapter adapter;
	private TitleBarView mBarView;

	@Override
	public int getViewLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.follow_list_fragment;
	}

	public static ExceptionListFragment newInstance() {
		return new ExceptionListFragment();

	}

	public ExceptionListFragment() {

	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		mBarView.setTitle(getString(R.string.health_steward_exception_warm));

		init();
		requestExceptionList();
	}

	public void init() {
		lv = (ListView) findViewById(R.id.follow_list_view);
		LinearLayout ln = (LinearLayout) findViewById(R.id.layout_no_data);
		ln.setVisibility(View.GONE);

	}

	private void requestExceptionList() {
		showProgressDialog(getString(R.string.msg_loading));
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.EXCEPTION);
		http.setOnHttpListener(1, this);
		http.startAsynchronous();
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		cancelProgressDialog();
		switch (what) {
		case 1:
			parseException(b);
			if (lists != null) {
				adapter = new ExceptionListAdapter(lists, getApplicationContext());
			} else {
				showToast(getResources().getString(R.string.error));
				FragmentMrg.toBack(getActivity());
				return;
			}
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(this);
			break;

		default:
			break;
		}
	}

	@Override
	public void onFialed(int what, int errorCode) {
		// TODO Auto-generated method stub
		cancelProgressDialog();
		ComveeHttpErrorControl.parseError(getActivity(), errorCode);
	}

	private void parseException(byte[] b) {

		lists = new ArrayList<ExceptionList>();
		try {
			final ComveePacket packet = ComveePacket.fromJsonString(b);
			if (packet.getResultCode() == 0) {
				JSONObject body = packet.optJSONObject("body");
				JSONArray followlist = body.optJSONArray("newsModel");
				for (int i = 0; i < followlist.length(); i++) {
					JSONObject info = followlist.optJSONObject(i);
					ExceptionList record = new ExceptionList();
					long newsId = info.optLong("newsId");
					String doctorId = info.optString("doctorId");
					String insertDt = info.optString("insertDt");
					String contentText = info.optString("contentText");
					int newsType = info.optInt("newsType");
					String doctorName = info.optString("doctorName");
					String adviceContent = info.optString("adviceContent");
					int detailNewsType = info.optInt("detailNewsType");
					record.setAdviceContent(adviceContent);
					record.setContentText(contentText);
					record.setDoctorId(doctorId);
					record.setInsertDt(insertDt);
					record.setNewsId(newsId);
					record.setNewsType(newsType);
					record.setDoctorName(doctorName);
					record.setDetailNewsType(detailNewsType);
					lists.add(record);

				}

			} else {
				ComveeHttpErrorControl.parseError(getActivity(), packet);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		String newId = lists.get(arg2).getNewsId() + "";
		int type = lists.get(arg2).getDetailNewsType();
		toFragment(ExceptionFragment.newInstance(newId, type), true, true);

	}
}
