package com.comvee.tnb.ui.pharmacy;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.comvee.ThreadHandler;
import com.comvee.tnb.R;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.ui.xlistview.XListView.IXListViewListener;

public class MedicinalListFragment extends Fragment implements OnItemClickListener, OnHttpListener {
	private String type;// 药品分类

	private XListView xlistview;
	private List<MedicinalModel> datas = new ArrayList<MedicinalModel>();
	private MedicinalAdapter adapter;
	private final int pageItemSize = 10;// 每页记录数
	private boolean isLastRecord;
	private OnSelectDrugListener listener;

	public MedicinalListFragment(String type) {
		super();
		this.type = type;
	}

	public void setOnSelectDrugListener(OnSelectDrugListener listener) {
		this.listener = listener;
	}

	public MedicinalListFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View converView = inflater.inflate(R.layout.medicinal_list_fragment, null);
		xlistview = (XListView) converView.findViewById(R.id.xlistview);
		xlistview.setPullRefreshEnable(false);
		xlistview.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				ThreadHandler.postUiThread(new Runnable() {
		            @Override
		            public void run() {
		            	xlistview.stopRefresh();xlistview.stopLoadMore();
		            }
		        }, 2000);
		    
			}

			@Override
			public void onLoadMore() {
				ThreadHandler.postUiThread(new Runnable() {
		            @Override
		            public void run() {
		            	xlistview.stopLoadMore();xlistview.stopRefresh();
		            }
		        }, 2000);
		    
				requestMedicinalList();
			}

		});
		xlistview.setOnItemClickListener(this);
		adapter = new MedicinalAdapter(getActivity().getApplicationContext(), datas, R.layout.item_medicinal);
		xlistview.setAdapter(adapter);
		xlistview.setPullLoadEnable(!isLastRecord);
		//xlistview.hideFootView();
		if (datas.size() == 0) {
			requestMedicinalList();
		}
		return converView;
	}

	private void requestMedicinalList() {
		ComveeHttp http = new ComveeHttp(getActivity().getApplicationContext(), ConfigUrlMrg.MEDICINAL_LIST);
		http.setPostValueForKey("type", type);
		http.setPostValueForKey("rows", pageItemSize + "");
		http.setPostValueForKey("page", datas.size() / pageItemSize + 1 + "");
		http.setOnHttpListener(1, this);
		http.startAsynchronous();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MedicinalModel model = (MedicinalModel) parent.getAdapter().getItem(position);
		if (listener != null) {
			listener.selectDrug(model);
		}
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		try {
			ComveePacket packet = ComveePacket.fromJsonString(b);
			parseResult(packet);
		} catch (Exception e) {
			Log.e("tag", e.getMessage(), e);
			e.printStackTrace();
		}
	}

	private void parseResult(ComveePacket packet) throws JSONException {
		JSONObject baseJsonObject = packet.getJSONObject("body");
		JSONArray array = baseJsonObject.getJSONArray("rows");
		int len = array.length();
		for (int i = 0; i < len; i++) {
			JSONObject mJsonObject = array.getJSONObject(i);
			MedicinalModel medicinalModel = new MedicinalModel();
			medicinalModel.name = mJsonObject.getString("drugName");
			medicinalModel.dose = mJsonObject.getString("dose");
			medicinalModel.unit = mJsonObject.getString("unit");
			medicinalModel.id = mJsonObject.optLong("id");
			datas.add(medicinalModel);
		}
		int totalPagers = baseJsonObject.getJSONObject("pager").getInt("totalRows");
		if (totalPagers == datas.size()) {
			xlistview.setPullLoadEnable(false);
			isLastRecord = true;
		}
		adapter.notifyDataSetChanged();
		xlistview.stopLoadMore();

	}

	@Override
	public void onFialed(int what, int errorCode) {

	}

	public interface OnSelectDrugListener {
		void selectDrug(MedicinalModel model);
	}
}
