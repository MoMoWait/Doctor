package com.comvee.tnb.ui.record.diet;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.model.FoodExchangeModel;
import com.comvee.tnb.network.FoodExchangeLoader;
import com.comvee.tnb.network.NetworkCallBack;
import com.comvee.tnb.widget.TitleBarView;

/**
 * 交换详情
 * 
 * @author Administrator
 * 
 */
public class SwapItemDetailFragment extends BaseFragment implements OnClickListener {
	private TextView tvOriginalName;
	private TextView tvOriginalWeight;
	private TextView tvOriginalCal;
	private TextView tvNewName;
	private TextView tvNewWeight;
	private TextView tvNewCal;
	private Button btConfirm;
	private FoodExchangeModel foodExchangeModel;
	public final static int FROM_RECORD_LIST = 1;

	@Override
	public int getViewLayoutId() {
		return R.layout.swap_diet_frag;
	}

	@Override
	public void onLaunch(Bundle dataBundle) {
		TitleBarView mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		mBarView.setTitle("食物交换");

		initView();
		btConfirm.setOnClickListener(this);

		if (dataBundle != null) {
			foodExchangeModel = (FoodExchangeModel) dataBundle.getSerializable("foodexchange");
			setupValue(foodExchangeModel);
		}
	}

	private void initView() {
		tvOriginalName = (TextView) findViewById(R.id.original_name);
		tvOriginalWeight = (TextView) findViewById(R.id.original_weight);
		tvOriginalCal = (TextView) findViewById(R.id.original_cal);
		tvNewName = (TextView) findViewById(R.id.new_name);
		tvNewWeight = (TextView) findViewById(R.id.new_weight);
		tvNewCal = (TextView) findViewById(R.id.new_cal);
		btConfirm = (Button) findViewById(R.id.confirm);

		btConfirm.setBackgroundResource(R.drawable.button_red1);
		btConfirm.setTextAppearance(getApplicationContext(), R.style.Button_Red1);
		btConfirm.setTextColor(Color.parseColor("#ff0000"));
		btConfirm.setText("删除");
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {

		case R.id.confirm:
			showRemoveMsg();
			break;
		}
	}

	private void showRemoveMsg() {
		CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
		builder.setMessage(R.string.pharmacy_remove_msg);
		builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialoginterface, int i) {
				deleteFoodHistory();
			}
		});
		builder.setPositiveButton(R.string.no, null);
		builder.create().show();
	}

	private void deleteFoodHistory() {
		if (foodExchangeModel == null) {
			return;
		}
		showProgressDialog(getString(R.string.msg_loading));
		FoodExchangeLoader loader = new FoodExchangeLoader();
		loader.deleteFoodHistory(new NetworkCallBack() {

			@Override
			public void callBack(int what, int status, Object obj) {
				cancelProgressDialog();
				if (status == 0) {
					showToast(getString(R.string.delete_ok).toString());
					Bundle bundle = new Bundle();
					bundle.putSerializable("deletemodel", foodExchangeModel);
					FragmentMrg.popBackToFragment(getActivity(), FoodExchangeHistoryListFrag.class, bundle, true);
				} else {
					showToast(obj.toString());

				}
			}
		}, foodExchangeModel.id);
	}

	private void setupValue(FoodExchangeModel foodExchangeModel) {
		tvOriginalName.setText(foodExchangeModel.oldname);
		tvOriginalWeight.setText(foodExchangeModel.oldweight);
		tvOriginalCal.setText(foodExchangeModel.heat + " kcal");
		tvNewName.setText(foodExchangeModel.newname);
		tvNewWeight.setText(foodExchangeModel.newweight);
		tvNewCal.setText(foodExchangeModel.heat + " kcal");
	}

}
