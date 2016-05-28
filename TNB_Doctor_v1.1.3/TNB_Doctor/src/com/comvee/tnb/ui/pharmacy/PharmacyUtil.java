package com.comvee.tnb.ui.pharmacy;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.ui.remind.TimeRemindTransitionInfo;
import com.comvee.ui.remind.TimeRemindUtil;

public class PharmacyUtil implements OnHttpListener {
	private Context context;
	private static PharmacyUtil util;

	public static PharmacyUtil getInstance(Context context) {
		if (util == null) {
			util = new PharmacyUtil(context);
		}
		return util;
	}

	private PharmacyUtil(Context context) {
		this.context = context;
	}

	public void requestRemindListNew() {
		try {
			ComveeHttp http = new ComveeHttp(context, ConfigUrlMrg.DRUG_REMIND_LIST);
			http.setOnHttpListener(1, PharmacyUtil.this);
			http.startAsynchronous();

		} catch (Exception e) {
		}
		;
	}

	public static void parseRemindList(Context context, ComveePacket packet) {
		try {
			TimeRemindUtil tempUtil = TimeRemindUtil.getInstance(context);
			if (packet.getResultCode() == 0) {
				List<TimeRemindTransitionInfo> allList = new ArrayList<TimeRemindTransitionInfo>();
				JSONArray jsonArray = packet.optJSONObject("body").optJSONArray("obj");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject obj = jsonArray.optJSONObject(i);
					String drugName = obj.optString("drugName");
					String drugNumber = obj.optString("drugNumber");
					long drugId = obj.optLong("drugDepotId");
					String drugUnit = obj.optString("unit");
					int isValid = obj.optInt("onOff");
					String memberId = obj.optString("memberId");
					String memberName = obj.optString("memberName");
					String remark = obj.optString("remark");
					String remindTime = obj.optString("remindTime");
					long typeId = obj.optLong("id");
					if (TextUtils.isEmpty(remindTime)) {
						return;
					}
					// 闹钟时间格式 (8:0,9:0week:0,1,3)其中week后表示闹钟选中的星期 0为星期一 以此类推
					String str[] = remindTime.split("week:");
					if (str.length == 2) {
						boolean weeks[] = new boolean[7];
						String strWeek[] = str[1].split(",");
						for (int j = 0; j < strWeek.length; j++) {
							int index = 0;
							try {
								if (TextUtils.isEmpty(strWeek[j])) {
									index = 0;
								} else {
									index = Integer.parseInt(strWeek[j]);
								}
							} catch (Exception e) {
								index = 0;
							}
							weeks[index % 7] = true;
						}
						String times[] = str[0].split(",");
						for (int j = 0; j < times.length; j++) {
							TimeRemindTransitionInfo info = new TimeRemindTransitionInfo();
							info.setDiabolo(isValid == 1 ? true : false);
							info.setMemberId(memberId);
							info.setMemName(memberName);
							info.setRemark(remark);
							info.setDrugName(drugName);
							info.setType(4);
							info.setPmType(typeId);
							info.setWeek(weeks);
							info.setDrugId(drugId);
							info.setDrugUnit(drugNumber + " " + drugUnit);
							info.setHour(Integer.parseInt(times[j].split(":")[0]));
							info.setMinute(Integer.parseInt(times[j].split(":")[1]));
							allList.add(info);
						}
					}
				}
				if (allList.size() > 0) {
					tempUtil.deleteTime("type=4");
					tempUtil.addTimes(allList);
				}
			} else {
				// ComveeHttpErrorControl.parseError(context, packet);
			}
			tempUtil.star();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		try {
			parseRemindList(TNBApplication.getInstance(), ComveePacket.fromJsonString(b));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFialed(int what, int errorCode) {

	}
}
