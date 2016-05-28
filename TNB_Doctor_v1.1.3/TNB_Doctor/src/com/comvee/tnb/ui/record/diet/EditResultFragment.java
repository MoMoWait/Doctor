package com.comvee.tnb.ui.record.diet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.FinalDb;
import com.comvee.db.sqlite.DbModel;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.common.MyBaseAdapter;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.model.AlarmPendingCodeInfo;
import com.comvee.tnb.ui.heathknowledge.HealthKnowledgeSubListFrament;
import com.comvee.tnb.ui.record.common.ImageItem4LocalImage;
import com.comvee.tnb.ui.record.common.MyBitmapFactory;
import com.comvee.tnb.ui.tool.HeatFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UserMrg;
import com.comvee.tool.ViewHolder;
import com.comvee.ui.remind.RemindListFragment;
import com.comvee.ui.remind.TimeRemindFragment;
import com.comvee.ui.remind.TimeRemindTransitionInfo;
import com.comvee.ui.remind.TimeRemindUtil;
import com.comvee.util.StringUtil;

/**
 * 饮食记录结果页
 * @author Administrator
 *
 */
public class EditResultFragment extends BaseFragment implements OnClickListener, OnCheckedChangeListener {
	private int resultType;
	private String resultContent;
	private int mealTimeType;
	private String mealContent;

	private GridView gridView;
	private ImageView mealImg;
	private TextView mealTimeTv;
	private TextView mealContentTv;
	private TextView toDoTv;
	private ImageView toDoImg;
	private TextView resultContentTv;

	private CheckBox checkBox0, checkBox1, checkBox2;
	private final String[] mealTimes = { "早餐", "午餐", "晚餐" };
	private final String[] todoStr = { "去学习", "去设置", "去交换", "查食物" };
	private final int[] todoImgSrc = { R.drawable.yinshi_41, R.drawable.yinshi_49, R.drawable.yinshi_45, R.drawable.yinshi_39, R.drawable.yinshi_46 };
	private static final String fileDirStr = Environment.getExternalStorageDirectory() + File.separator + "temp";// 拍照存放的路径
	private final int[] mealImgRes = { R.drawable.yinshi_10, R.drawable.yinshi_12, R.drawable.yinshi_14 };

	private ArrayList<HashMap<String, String>> subDatas;
	private String subTitle;
	private TimeRemindUtil util;
	private String folderId;

	@Override
	public int getViewLayoutId() {
		return R.layout.frame_editresult_diet;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLaunch(Bundle dataBundle) {
		super.onLaunch(dataBundle);
		util = TimeRemindUtil.getInstance(getContext());
		this.resultContent = dataBundle.getString("resultContent");
		this.resultType = dataBundle.getInt("resultType", 0);
		this.mealTimeType = dataBundle.getInt("mealTimeType");
		this.mealContent = dataBundle.getString("mealContent");
		this.subDatas = (ArrayList<HashMap<String, String>>) dataBundle.getSerializable("subDatas");
		this.subTitle = dataBundle.getString("subTitle");
		this.folderId = dataBundle.getString("folderId");

		gridView = (GridView) findViewById(R.id.gridview);
		mealImg = (ImageView) findViewById(R.id.meal_img);
		mealTimeTv = (TextView) findViewById(R.id.meal_time);
		mealContentTv = (TextView) findViewById(R.id.name);
		resultContentTv = (TextView) findViewById(R.id.result_content);
		toDoTv = (TextView) findViewById(R.id.to_do);
		toDoImg = (ImageView) findViewById(R.id.to_doimg);
		checkBox0 = (CheckBox) findViewById(R.id.check0);
		checkBox1 = (CheckBox) findViewById(R.id.check1);
		checkBox2 = (CheckBox) findViewById(R.id.check2);
		checkBox0.setOnClickListener(this);
		checkBox1.setOnClickListener(this);
		checkBox2.setOnClickListener(this);
		toDoTv.setOnClickListener(this);
		fullData();

	}

	private void fullData() {
		TitleBarView mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftButton(R.drawable.top_menu_back, this);
		mBarView.setRightButton("历史", this);
		if (MyBitmapFactory.tempSelectBitmap.size() == 0)
			gridView.setVisibility(View.GONE);
		gridView.setAdapter(new GridViewAdapter(getApplicationContext(), MyBitmapFactory.tempSelectBitmap, R.layout.item_record_diet_gridview));
		int period = mealTimeType - 1;
		mealImg.setImageResource(mealImgRes[period]);
		mealTimeTv.setText(mealTimes[period]);
		if (TextUtils.isEmpty(mealContent))
			mealContentTv.setText("暂无描述");
		else
			mealContentTv.setText(mealContent);
		resultContentTv.setText(resultContent);
		toDoImg.setImageResource(todoImgSrc[resultType - 1]);
		switch (resultType) {
		case 1:
			toDoTv.setText(todoStr[0]);
			break;
		case 2:
			toDoTv.setVisibility(View.GONE);
			break;
		case 3:
			toDoTv.setText(todoStr[1]);
			break;
		case 4:
			toDoTv.setText(todoStr[2]);
			break;
		case 5:
			toDoTv.setText(todoStr[3]);
			break;

		default:

			break;
		}

	}

	private class GridViewAdapter extends MyBaseAdapter<ImageItem4LocalImage> {
		private List<ImageItem4LocalImage> datas;

		public GridViewAdapter(Context ctx, List<ImageItem4LocalImage> datas, int mayoutid) {
			super(ctx, datas, mayoutid);
			this.datas = datas;
		}

		@Override
		protected void doyouself(ViewHolder holder, final int position) {
			ImageView imageView = holder.get(R.id.image);
			ImageItem4LocalImage imageItem4LocalImage = datas.get(position);
			if (!TextUtils.isEmpty(imageItem4LocalImage.smallImagePath)) {// 网络图片
				ImageLoaderUtil.getInstance(getActivity()).displayImage(imageItem4LocalImage.smallImagePath, imageView,
						ImageLoaderUtil.default_options);
			} else if (imageItem4LocalImage.drawableThumb != null) {// 缩略图
				imageView.setImageDrawable(datas.get(position).drawableThumb);
			} else if (!TextUtils.isEmpty(imageItem4LocalImage.imagePath)) {// 拍照图片
				try {
					imageView.setImageBitmap(MyBitmapFactory.revitionImageSize(imageItem4LocalImage.imagePath));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case TitleBarView.ID_LEFT_BUTTON:
			onBackPress();
			break;
		case TitleBarView.ID_RIGHT_BUTTON:
			Bundle bundle=new Bundle();
			bundle.putSerializable("class", RecordDietIndexFragment.class);
			toFragment(HistoryDietFragment.class, bundle, true);
			break;
		case R.id.to_do:
			toNextFrag();
			break;
		case R.id.check0:
			if (checkBox0.isChecked()) {
				addTempTime(mealTimeType, 1);
			} else {
				if (!StringUtil.isNumble(folderId)) {
					showToast("数据错误");
					return;
				}
				util.cancleDisposableAlarm((int) Long.parseLong("1"
						+ (folderId.length() > 8 ? folderId.substring(folderId.length() - 8, folderId.length()) : folderId)));
			}
			break;
		case R.id.check1:
			if (checkBox1.isChecked()) {
				addTempTime(mealTimeType, 2);
			} else {
				if (!StringUtil.isNumble(folderId)) {
					showToast("数据错误");
					return;
				}
				util.cancleDisposableAlarm((int) Long.parseLong("2"
						+ (folderId.length() > 8 ? folderId.substring(folderId.length() - 8, folderId.length()) : folderId)));
			}
			break;
		case R.id.check2:
			if (checkBox2.isChecked()) {
				addTempTime(mealTimeType, 3);
			} else {
				if (!StringUtil.isNumble(folderId)) {
					showToast("数据错误");
					return;
				}
				util.cancleDisposableAlarm((int) Long.parseLong("3"
						+ (folderId.length() > 8 ? folderId.substring(folderId.length() - 8, folderId.length()) : folderId)));
			}
			break;

		default:
			break;
		}
	}

	private void toNextFrag() {
		switch (resultType) {
		case 1:
			Bundle bundle = new Bundle();
			bundle.putSerializable("subDatas", subDatas);
			bundle.putString("subTitle", subTitle);
			toFragment(HealthKnowledgeSubListFrament.class, bundle, true);
			break;
		case 3:
			RemindListFragment.toFragment(getActivity(), TimeRemindFragment.REMIND_SUGAR, null);
			break;
		case 4:
			SwapDietFragment.toFragment(getActivity(), null);
			break;
		case 5:
			HeatFragment.toFragment(getActivity(), null);
			break;

		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onBackPress() {
		deleteallImageLoc();
		FragmentMrg.popBackToFragment(getActivity(), RecordDietIndexFragment.class, null);
		return true;
	}

	private void deleteallImageLoc() {
		MyBitmapFactory.clearAll();
		for (int i = 0; i < 9; i++) {
			File file = new File(fileDirStr + File.separator + i + ".png");
			if (file.exists())
				file.delete();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {

		default:
			break;
		}

	}

	private void addTempTime(int period, int option) {
		if (!StringUtil.isNumble(folderId)) {
			showToast("数据错误");
			return;
		}
		long time = System.currentTimeMillis() + (option * 60 * 60 * 1000);
		TimeRemindTransitionInfo tempinfo = new TimeRemindTransitionInfo();
		tempinfo.setMemberId(UserMrg.DEFAULT_MEMBER.mId);
		String remark = null;

		long pendingCode = -1;
		switch (option) {
		case 1:
			pendingCode = Long.parseLong("1" + (folderId.length() > 8 ? folderId.substring(folderId.length() - 8, folderId.length()) : folderId));
			addCode(getApplicationContext(), (int) pendingCode);
			remark = String.format(getText(R.string.remind_msg_1).toString(), getText(R.string.remind_time_Interval_1).toString());
			break;
		case 2:
			pendingCode = Long.parseLong("2" + (folderId.length() > 8 ? folderId.substring(folderId.length() - 8, folderId.length()) : folderId));
			remark = String.format(getText(R.string.remind_msg_1).toString(), getText(R.string.remind_time_Interval_2).toString());
			addCode(getApplicationContext(), (int) pendingCode);
			break;
		case 3:
			pendingCode = Long.parseLong("3" + (folderId.length() > 8 ? folderId.substring(folderId.length() - 8, folderId.length()) : folderId));
			remark = String.format(getText(R.string.remind_msg_1).toString(), getText(R.string.remind_time_Interval_3).toString());
			addCode(getApplicationContext(), (int) pendingCode);
			break;
		default:
			break;
		}
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("folderId", folderId);
			jsonObject.put("period", period);
			jsonObject.put("option", option);
			jsonObject.put("title", remark);
		} catch (Exception e) {
			e.printStackTrace();
		}

		tempinfo.setType(5);
		tempinfo.setRemark(jsonObject.toString());
		util.addDisposableAlarm((int) pendingCode, tempinfo, time);
	}

	public static void addCode(Context context, int pendingCode) {
		FinalDb db = FinalDb.create(context, ConfigParams.DB_NAME);
		AlarmPendingCodeInfo info = new AlarmPendingCodeInfo();
		info.setCode(pendingCode);
		db.save(info);
		db.findAllByWhere(AlarmPendingCodeInfo.class, "");
	}

	public static void deleteAllCode(Context context, TimeRemindUtil util) {
		FinalDb db = FinalDb.create(context, ConfigParams.DB_NAME);
		DbModel dbModel = db.findDbModelBySQL("SELECT * FROM sqlite_master WHERE type='table' AND name='alarmpendingcode'");
		if (dbModel != null) {
			List<AlarmPendingCodeInfo> list = db.findAllByWhere(AlarmPendingCodeInfo.class, "id>-1");
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i) == null || list.get(i).getCode() == 0) {
						continue;
					}
					util.cancleDisposableAlarm(list.get(i).getCode());
				}
				db.deleteByWhere(AlarmPendingCodeInfo.class, "");
			}
		}
	}
}
