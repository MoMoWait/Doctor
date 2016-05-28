package com.comvee.tnb.ui.voucher;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.widget.TitleBarView;
/**
 * 选择卡券
 * @author Administrator
 *
 */
public class SelectUseVoucherFrag extends BaseFragment implements OnItemClickListener, OnClickListener {
	private TitleBarView mBarView;
	private ListView listView;
	private List<VoucherModel> list;
	private SelectUseVocucheAdapter mAdapter;
	private int selectPostion = -1;
	private OnSelectVoucherListener listener;

	@Override
	public int getViewLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.select_use_voucher_frag;
	}

	public void setSelectPostion(int selectPostion) {
		this.selectPostion = selectPostion;
	}

	public static SelectUseVoucherFrag newInstance(List<VoucherModel> list) {
		SelectUseVoucherFrag frag = new SelectUseVoucherFrag();
		frag.setList(list);
		return frag;
	}

	public void setList(List<VoucherModel> list) {
		this.list = list;
	}

	@Override
	public void onLaunch(Bundle dataBundle) {
		super.onLaunch(dataBundle);
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		mBarView.setTitle(getString(R.string.voucher_select_title).toString());
		mBarView.setRightButton(getString(R.string.finish).toString(), this);
		init();
	}

	private void init() {
		mBarView.setLeftDefault(this);
		mAdapter = new SelectUseVocucheAdapter(getActivity(), list, R.layout.item_select_voucherlist);
		mAdapter.setSelect(selectPostion);
		listView = (ListView) findViewById(R.id.listview);
		listView.setEmptyView(findViewById(R.id.nodata));
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		SelectUseVocucheAdapter adapter = (SelectUseVocucheAdapter) parent.getAdapter();
		if (position != selectPostion) {
			selectPostion = position;
		} else {
			selectPostion = -1;
		}
		adapter.setSelect(selectPostion);
		adapter.notifyDataSetChanged();
	}

	public void setOnSelectVoucherListener(OnSelectVoucherListener listener) {
		this.listener = listener;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (listener != null) {
			listener.onSelectVoucher(selectPostion);
		}
	}

	public interface OnSelectVoucherListener {
		public void onSelectVoucher(int selectPostion);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case TitleBarView.ID_RIGHT_BUTTON:
			FragmentMrg.toBack(getActivity());
			break;

		default:
			break;
		}
	}
}
