package com.comvee.tnb.ui.record.diet;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.network.BackgroundTasks;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomNumPickDialog;
import com.comvee.tnb.dialog.CustomNumPickDialog.OnChangeNumListener;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.FoodInfo;
import com.comvee.tnb.ui.tool.HeatFragment;
import com.comvee.tnb.ui.tool.HeatListFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.util.BundleHelper;

/**
 * 食物交换页面
 *
 * @author pxl
 *
 */
public class SwapDietFragment extends BaseFragment implements OnClickListener, OnChangeNumListener {

	private FoodInfo originalFoodInfo;
	private FoodInfo newFoodInfo;

	private TextView tvOldName;
	private TextView tvOldWeight;
	private TextView tvOldCal;// 卡路里

	private TextView tvNewName;
	private TextView tvNewWeight;
	private TextView tvNewCal;
	private Button btConfirm;

	private float unitOriginal;// 原食物每克多少热量
	private float unitNew;// 替换的食物每克多少热量
	private int clickType;// 1 常吃的食物，2交换的食物
	public final static int FROM_RECORD_LIST = 1;
	private boolean containWeight = true;// 重量单位是否需要带上

	public static final int TYPE_OLD = 1;
	public static final int TYPE_NEW = 2;

	@Override
	public int getViewLayoutId() {
		return R.layout.swap_diet_frag;
	}

	public static void toFragment(FragmentActivity act, FoodInfo info) {
		Bundle bundle = BundleHelper.getBundleBySerializable(info);
		FragmentMrg.toFragment(act, SwapDietFragment.class, bundle, true);
	}

	public static void toFragment(FragmentActivity act, boolean containWeight) {
		Bundle bundle = new Bundle();
		bundle.putBoolean("containWeight", containWeight);
		FragmentMrg.toFragment(act, SwapDietFragment.class, bundle, true);
	}

	@Override
	public void onLaunch(Bundle dataBundle) {
		TitleBarView mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		mBarView.setRightButton("交换历史", this);
		mBarView.setTitle("食物交换");

		initView();
		registerView();

		if (dataBundle != null) {
			containWeight = dataBundle.getBoolean("containWeight", true);
			if (containWeight) {
				originalFoodInfo = BundleHelper.getSerializableByBundle(dataBundle);
				if (originalFoodInfo != null) {
					unitOriginal = Float.valueOf(originalFoodInfo.heat) / (Float.valueOf(originalFoodInfo.weight));
					updateOldFood(originalFoodInfo);
				}
			}
		}
		if (newFoodInfo != null) {
			updateOldFood(newFoodInfo);
		}
	}

	@Override
	public void onFragmentResult(Bundle data) {
		super.onFragmentResult(data);
		if (data != null) {
			try {
				switch (clickType){
					case TYPE_OLD:
						FoodInfo oldFoodinfo = BundleHelper.getSerializableByBundle( data);
						this.originalFoodInfo = oldFoodinfo;
						if (containWeight) {
							originalFoodInfo.recommendHeat = oldFoodinfo.heat + "";
							originalFoodInfo.recommendWeight = oldFoodinfo.weight + "";
						} else {
							originalFoodInfo.recommendHeat = "";
							originalFoodInfo.recommendWeight = "";
						}
						unitOriginal = Float.valueOf(originalFoodInfo.heat) / (Float.valueOf(originalFoodInfo.weight));
						updateOldFood(originalFoodInfo);

						tvNewName.setText("");
						tvNewWeight.setText("");
						tvNewCal.setText(" --kcal");
						newFoodInfo = null;
						break;
					case TYPE_NEW:
						newFoodInfo =  BundleHelper.getSerializableByBundle( data);
						float totalCal = Float.parseFloat(originalFoodInfo.recommendHeat);
						unitNew = Float.valueOf(newFoodInfo.heat) / (Float.valueOf(newFoodInfo.weight));
						tvNewName.setText(newFoodInfo.name);
						tvNewCal.setText(originalFoodInfo.recommendHeat + " kcal");
						tvNewWeight.setText((int) Math.floor(totalCal / unitNew) + " g");
						newFoodInfo.weight= (int) Math.floor(totalCal / unitNew);
						break;
				}


			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void initView() {
		tvOldName = (TextView) findViewById(R.id.original_name);
		tvOldWeight = (TextView) findViewById(R.id.original_weight);
		tvOldCal = (TextView) findViewById(R.id.original_cal);
		tvNewName = (TextView) findViewById(R.id.new_name);
		tvNewWeight = (TextView) findViewById(R.id.new_weight);
		tvNewCal = (TextView) findViewById(R.id.new_cal);
		btConfirm = (Button) findViewById(R.id.confirm);
	}

	private void registerView() {
		tvOldName.setOnClickListener(this);
		tvOldWeight.setOnClickListener(this);
		tvNewName.setOnClickListener(this);
		btConfirm.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.original_name:
			clickType = TYPE_OLD;
			HeatFragment.toFragment(getActivity(), HeatFragment.FROM_SELECT_EAT_FOOD);
			break;
		case R.id.original_weight:
			if (TextUtils.isEmpty(tvOldName.getText())) {
				showToast("请先选择食物");
				return;
			}
			showNumDialog((int) originalFoodInfo.weight, viewId + "");
			break;
		case TitleBarView.ID_RIGHT_BUTTON:
			Bundle bundle1 = new Bundle();
			bundle1.putSerializable("class", SwapDietFragment.class);
			toFragment(FoodExchangeHistoryListFrag.class, bundle1, true);
			break;
		case R.id.new_name:
			if (originalFoodInfo == null) {
				showToast("请先选择常吃食物");
				return;
			}
			if (TextUtils.isEmpty(tvOldWeight.getText())) {
				showToast("请先选择常吃食物重量");
				return;
			}
			clickType = TYPE_NEW;
			Bundle bundle = new Bundle();
			bundle.putString("id", originalFoodInfo.id);
			bundle.putString("foodid", originalFoodInfo.foodId);
			bundle.putInt("fromwhere", HeatFragment.FROM_SELECT_EXCHANGE_FOOD);
			bundle.putSerializable("clazz", SwapDietFragment.class);
			toFragment(HeatListFragment.class, bundle, true);
			break;
		case R.id.confirm:
			exchangeFood();
			break;
		}
	}

	private void exchangeFood() {
		TnbBaseNetwork tnbBaseNetwork = new TnbBaseNetwork() {

			@Override
			protected Object parseResponseJsonData(JSONObject resData) {
				return resData;
			}

			@Override
			protected void onDoInMainThread(int status, Object obj) {
				cancelProgressDialog();
				if (status != 0) {
					showToast(obj + "");
				} else {
					showToast("食物交换成功");
					Bundle bundle = new Bundle();
					bundle.putSerializable("class", RecordDietIndexFragment.class);
					toFragment(FoodExchangeHistoryListFrag.class, bundle, true);
				}
			}

			@Override
			public String getUrl() {
				return ConfigUrlMrg.ADD_FOOD_EXCHANGE;
			}
		};
		if (originalFoodInfo == null) {
			showToast(getString(R.string.food_exchange_toast).toString());
			return;
		} else if (newFoodInfo == null) {
			showToast(getString(R.string.food_exchange_toast_1).toString());
			return;
		}
		showProgressDialog(getString(R.string.msg_loading));
		tnbBaseNetwork.putPostValue("oldname", originalFoodInfo.name);
		tnbBaseNetwork.putPostValue("oldPICURL", originalFoodInfo.imgUrl);
		tnbBaseNetwork.putPostValue("oldweight", tvOldWeight.getText().toString().replace(" g",""));
		tnbBaseNetwork.putPostValue("newPICURL", newFoodInfo.imgUrl);
		tnbBaseNetwork.putPostValue("newname", newFoodInfo.name);
		tnbBaseNetwork.putPostValue("newweight", tvNewWeight.getText().toString().replace(" g",""));
		tnbBaseNetwork.putPostValue("heat", tvOldCal.getText().toString().replace(" kcal",""));
		tnbBaseNetwork.putPostValue("msg_type", getMsgType() + "");
		tnbBaseNetwork.start();
		//BackgroundTasks.getInstance().push(BackgroundTasks.TASK_NETWORK, tnbBaseNetwork);
	}

	/**
	 * 根据foodlevel判断msgtype
	 *
	 * @return
	 */
	private int getMsgType() {
		String suggest1 = getText(R.string.food_eat_suggest_1).toString();
		String suggest2 = getText(R.string.food_eat_suggest_2).toString();
		String suggest3 = getText(R.string.food_eat_suggest_3).toString();
		int msgType = 0;
		if (suggest1.equals(originalFoodInfo.eatAdvice)) {
			if (suggest2.equals(newFoodInfo.eatAdvice)) {
				msgType = 1;
			} else if (suggest3.equals(newFoodInfo.eatAdvice)) {
				msgType = 2;
			}
		} else if (suggest2.equals(originalFoodInfo.eatAdvice) || suggest3.equals(originalFoodInfo.eatAdvice)) {
			if (suggest1.equals(newFoodInfo.eatAdvice)) {
				msgType = 3;
			} else if (suggest3.equals(newFoodInfo.eatAdvice) || suggest2.equals(newFoodInfo.eatAdvice)) {
				msgType = 4;
			}
		}
		return msgType;

	}

	private void showNumDialog(int defaultNum, String tag) {
		CustomNumPickDialog buidler = new CustomNumPickDialog();
		buidler.setMessage(getString(R.string.food_exchange_dialog_title));
		buidler.setDefualtNum((defaultNum == 0 || defaultNum == -1) ? 100 : defaultNum);
		buidler.setFloat(false);
		buidler.setUnit(getText(R.string.food_exchange_dialog_unit).toString());
		buidler.setLimit(0, 999);
		buidler.setOnChangeNumListener(this);
		buidler.show(getActivity().getSupportFragmentManager(), tag);
	}

	@Override
	public void onChange(DialogFragment dialog, float num) {
		if(num==0){
			showToast("食物分量不能为0克");
			return ;
		}
		String tag = dialog.getTag();
		if ((R.id.original_weight + "").equals(tag)) {
			double totalCal = unitOriginal * num;
			String calStr =  String.format("%.0f",(float)totalCal);
			originalFoodInfo.weight = (int) num;
			originalFoodInfo.heat = (float)totalCal;
			originalFoodInfo.recommendHeat = calStr;
			originalFoodInfo.recommendWeight = (int) num + "";
			updateOldFood(originalFoodInfo);
			//如果替换的食物已经选择了，那么久将替换的食物的重量也替换掉
			if(newFoodInfo!=null){
				updateNewFood(newFoodInfo);
			}
//			if (!TextUtils.isEmpty(tvNewName.getText())) {
//			}
		}
	}

	private void updateOldFood(FoodInfo originalFoodInfo){
		tvOldName.setText(originalFoodInfo.name);
		if (TextUtils.isEmpty(originalFoodInfo.recommendWeight)) {
			tvOldWeight.setText("");
		} else {
			tvOldWeight.setText(originalFoodInfo.recommendWeight + " g");
		}
		if (TextUtils.isEmpty(originalFoodInfo.recommendHeat)) {
			tvOldCal.setText("-- kcal");
		} else {
			tvOldCal.setText(originalFoodInfo.recommendHeat + " kcal");
		}
	}

	private void updateNewFood(FoodInfo newFoodInfo){
		tvNewName.setText(newFoodInfo.name);
		float totalCal = Float.parseFloat(originalFoodInfo.recommendHeat);
		tvNewWeight.setText((int) Math.floor(totalCal / unitNew) + " g");
		tvNewCal.setText(originalFoodInfo.recommendHeat + " kcal");
		newFoodInfo.weight = (int) Math.floor(totalCal / unitNew);
	}

}
