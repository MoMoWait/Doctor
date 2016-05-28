package com.comvee.tnb.network;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.comvee.BaseApplication;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.model.IndexGuideModelNew;
import com.comvee.tnb.ui.ask.AskIndexFragment;
import com.comvee.tnb.ui.exercise.RecordExerciseFragment;
import com.comvee.tnb.ui.log.RecordCalendarFragment;
import com.comvee.tnb.ui.more.MoreFragment;
import com.comvee.tnb.ui.more.WebFragment;
import com.comvee.tnb.ui.more.WebNewFrag;
import com.comvee.tnb.ui.record.RecordBmiInputFragment;
import com.comvee.tnb.ui.record.RecordHemoglobinFragment;
import com.comvee.tnb.ui.record.RecordPressBloodInputFragment;
import com.comvee.tnb.ui.record.RecordSugarInputNewFrag;
import com.comvee.tnb.ui.record.diet.RecordDietIndexFragment;
import com.comvee.tnb.ui.record.laboratory.RecordLaboratoryFragment;
import com.comvee.tool.UserMrg;
import com.comvee.ui.remind.RemindListFragment;
import com.comvee.ui.remind.TimeRemindFragment;

/**
 * 首页任务模块点击跳转工具类
 * 
 * @author Administrator
 * 
 */
public class IndexNewGuideUtil {
	private FragmentActivity activity;
	private static IndexNewGuideUtil guideUtil;
	private static final int RECORD_INDEX = 1;// 记录首页
	private static final int RECORD_SUGAR = 2;// 记录血糖
	private static final int RECORD_BLOOD = 3;// 记录血压
	private static final int RECORD_BMI = 4;// 记录bmi

	private static final int RECORD_DIER = 5;// 记录饮食
	private static final int RECORD_EXERCISE = 6;// 记录运动
	private static final int RECORD_PHARMACY = 7;// 记录用药

	private static final int RECORD_HEMOBLOBIN = 8;// 记录糖化血红蛋白
	private static final int RECORD_LABORATORY = 9;// 记录化验单
	private static final int RECORD_CALENTER = 10;// 记录健康
	private static final int SET_REMIND_INDEX = 11;// 记录血糖闹钟
	private static final int SET_REMIND_BEFORE_BREAKFAST = 12;// 记录空腹闹钟
	private static final int SET_REMIND_BEFORE = 13;// 记录餐前闹钟
	private static final int SET_REMIND_AFTER = 14;// 记录餐后闹钟
	private static final int SET_REMIND_BEFORE_DRAM = 15;// 记录凌晨闹钟
	private static final int TO_ASK_DOCTOR = 16;// 去咨询
	private BaseFragment fragment;

	public static IndexNewGuideUtil getInstance(BaseFragment activity) {
		if (guideUtil == null) {
			guideUtil = new IndexNewGuideUtil(activity);
		}
		return guideUtil;
	}

	public IndexNewGuideUtil(BaseFragment activity) {
		this.fragment = activity;
		this.activity = fragment.getActivity();
	}

	public static void newGuideJump(FragmentActivity activity, int type, Object... obj) {
		try {
			switch (type) {
				case 0:
					Context cxt = BaseApplication.getInstance();
					String url = obj[1].toString();
					if(url.contains("?")){
						url += String.format("&sessionID=%s&sessionMemberID=%s",UserMrg.getSessionId(cxt),UserMrg.getMemberSessionId(cxt));
					}else{
						url += String.format("?sessionID=%s&sessionMemberID=%s",UserMrg.getSessionId(cxt),UserMrg.getMemberSessionId(cxt));
					}
					String url2=url;
					WebNewFrag.toFragment(activity, obj[0].toString(), url);
					break;
				case RECORD_INDEX:
//					FragmentMrg.toFragment(activity, RecordSugarInputNewFrag.class, null, true);
					break;
				case RECORD_SUGAR:
					FragmentMrg.toFragment(activity, RecordSugarInputNewFrag.class, null, true);
					break;
				case RECORD_BLOOD:
					FragmentMrg.toFragment(activity, RecordPressBloodInputFragment.newInstance(null), true, true);
					break;
				case RECORD_BMI:
					FragmentMrg.toFragment(activity, RecordBmiInputFragment.newInstance(null), true, true);
					break;
				case RECORD_DIER://饮食
					FragmentMrg.toFragment(activity, RecordDietIndexFragment.class, null, true);
					break;
				case RECORD_EXERCISE:
					FragmentMrg.toFragment(activity, RecordExerciseFragment.newInstance(), true, true);
					break;
				case RECORD_PHARMACY:
					FragmentMrg.toFragment(activity, RecordSugarInputNewFrag.class, null, true);
					break;
				case RECORD_HEMOBLOBIN:
					FragmentMrg.toFragment(activity, RecordHemoglobinFragment.newInstance(null), true, true);
					break;
				case RECORD_LABORATORY:
					FragmentMrg.toFragment(activity, new RecordLaboratoryFragment(), true, true);
					break;
				case RECORD_CALENTER:
					FragmentMrg.toFragment(activity, RecordCalendarFragment.newInstance(), true, true);
					break;
				case SET_REMIND_INDEX:
					RemindListFragment.toFragment(activity, TimeRemindFragment.REMIND_SUGAR, null);
					break;
				case SET_REMIND_BEFORE_BREAKFAST:
					RemindListFragment.toFragment(activity, TimeRemindFragment.REMIND_SUGAR, null);
					break;
				case SET_REMIND_BEFORE:
					RemindListFragment.toFragment(activity, TimeRemindFragment.REMIND_SUGAR, null);
					break;
				case SET_REMIND_AFTER:
					RemindListFragment.toFragment(activity, TimeRemindFragment.REMIND_SUGAR, null);
					break;
				case SET_REMIND_BEFORE_DRAM:
					RemindListFragment.toFragment(activity, TimeRemindFragment.REMIND_SUGAR, null);
					break;
				case TO_ASK_DOCTOR:
					FragmentMrg.toFragment(activity, AskIndexFragment.class, null, true);
					break;
				default:
					showUpdataDialog(activity);
					break;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public IndexGuideModelNew parseGuideModel(String result, int fromWhere) {
		IndexGuideModelNew indexModel = null;
		try {
			ComveePacket packet = ComveePacket.fromJsonString(result);
			if (packet.getResultCode() == 0) {
				indexModel = new IndexGuideModelNew();
				JSONObject body = packet.optJSONObject("body");
				indexModel.taskPhoto = body.optString("taskPhoto");
				indexModel.taskStatus = body.optInt("taskStatus");
				indexModel.taskSubtitle = body.optString("taskSubtitle");
				indexModel.taskText = body.optString("taskText");
				indexModel.taskTitle = body.optString("taskTitle");
				indexModel.taskUrl = body.optString("taskUrl");
				if (fromWhere == 1) {
					ComveeHttp.setCache(activity, ConfigUrlMrg.INDEX_TASK_GUIDE_NEW, ConfigParams.CHACHE_TIME_LONG, result);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return indexModel;
	}

	/**
	 * 升级
	 */
	private static void showUpdataDialog(final FragmentActivity act) {

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == CustomDialog.ID_OK) {
					MoreFragment.upData(act, true);
				}
			}
		};
		CustomDialog.Builder builder = new CustomDialog.Builder(act);
		builder.setMessage(act.getResources().getString(R.string.index_guide_updata_title));
		builder.setPositiveButton(act.getResources().getString(R.string.index_guide_btn_updata), listener);
		builder.setNegativeButton(act.getResources().getString(R.string.index_guide_btn_cancle), listener);
		builder.create().show();
	}
}
