package com.comvee.tnb.network;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.comvee.FinalDb;

import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.FollowQuestionDetailed;
import com.comvee.tnb.model.FollowQuestionOption;
/**
 * 随访问题获取接口
 * @author Administrator
 *
 */
public class FollowQuestinLoader extends TnbBaseNetwork {
	private String url;
	private NetworkCallBack callBack;
	private String followupId;
	private FinalDb db;

	public void loadQuestion(NetworkCallBack callBack, String followId, FinalDb db) {
		resetRequestParams();
		this.callBack = callBack;
		this.followupId = followId;
		this.db = db;
		url = ConfigUrlMrg.FOLLOW_GETQUESTION;
		putPostValue("followupId", followId);
		start();
	}

	@Override
	protected void onDoInMainThread(int status, Object obj) {
		if (callBack != null) {
			callBack.callBack(what, status, obj);
		}
	}

	@Override
	protected Object parseResponseJsonData(JSONObject resData) {

		return parseFollowQuestion(resData);
	}

	@Override
	public String getUrl() {
		return url;
	}

	// 回调解析
	private String parseFollowQuestion(JSONObject packet) {
		String title = null;
		try {

			List<FollowQuestionDetailed> detailedlist = null;
			List<FollowQuestionOption> optionlist = null;
			JSONArray ques = packet.optJSONObject("body").optJSONArray("ques");
			title = packet.optJSONObject("body").optString("title");
			if (ques != null) {
				int len = ques.length();
				if (len > 0) {
					detailedlist = new ArrayList<FollowQuestionDetailed>();
					optionlist = new ArrayList<FollowQuestionOption>();
					JSONObject JsonObject = null;
					JSONArray JSONArrayoptions = null;
					JSONObject JSONoption = null;
					FollowQuestionDetailed detailed = null;
					FollowQuestionOption option = null;
					for (int i = 0; i < len; i++) {
						JsonObject = ques.optJSONObject(i);
						// 题目解析
						detailed = new FollowQuestionDetailed();
						detailed.setFollowUpId(followupId);
						detailed.setCategory(JsonObject.optInt("category"));
						detailed.setCategoryName(JsonObject.optString("categoryName"));
						detailed.setDefualtCheck(JsonObject.optInt("defualtCheck"));
						detailed.setDictName(JsonObject.optString("dictName"));
						detailed.setHelp(JsonObject.optString("help"));
						detailed.setIsNeed(JsonObject.optInt("isNeed"));
						detailed.setIsShow(JsonObject.optInt("isShow"));
						detailed.setItemCode(JsonObject.optString("itemCode"));
						detailed.setItemType(JsonObject.optInt("itemType"));
						detailed.setPath(JsonObject.optString("path"));
						detailed.setParent(JsonObject.optString("pCode", ""));
						detailed.setRules(JsonObject.optString("rules"));
						detailed.setSeq(JsonObject.optString("seq"));
						detailed.setTie(JsonObject.optInt("tie"));
						String[] paths = detailed.getPath().split("_");
						detailed.setLevel(paths.length);
						int isParent = 0;
						String parentPath;
						if (detailed.getLevel() == 1) {
							isParent = 0;
							parentPath = "top";
						} else {
							isParent = 1;
							parentPath = "";
							for (int j = 0; j < paths.length - 1; j++) {
								if (parentPath.length() > 0) {
									parentPath += "_";
								}
								parentPath += paths[j];
							}
						}
						detailed.setParentPath(parentPath);
						detailed.setMhasParent(isParent);
						String value = "";
						// 选项解析
						JSONArrayoptions = JsonObject.optJSONArray("itemList");
						if (JSONArrayoptions != null) {
							for (int j = 0; j < JSONArrayoptions.length(); j++) {
								JSONoption = JSONArrayoptions.optJSONObject(j);
								option = new FollowQuestionOption();
								option.setFollowId(followupId);
								option.setId(JSONoption.optString("id"));
								option.setItemCode(detailed.getItemCode());
								option.setIsRestrain(JSONoption.optInt("isRestrain"));
								option.setIsValue(JSONoption.optInt("isValue"));
								option.setValueCode(JSONoption.optString("valueCode"));
								option.setValueName(JSONoption.optString("valueName"));
								option.setShow_seq(JSONoption.optInt("show_seq", j));
								option.setPosition(optionlist.size());
								if (detailed.getItemType() == 1 || detailed.getItemType() == 2) {
									if (option.getIsValue() == 1) {
										if (value.length() > 0) {
											value += ",";
										}
										value += option.getValueName();
									}
								}
								if (option.getValueCode().equals("unit")) {
									detailed.setUnit(option.getValueName());
								}
								optionlist.add(option);
							}
						}
						detailed.setValue(value);
						detailedlist.add(detailed);
					}
				}
			}
			if (detailedlist != null && detailedlist.size() > 0 && optionlist != null && optionlist.size() > 0) {
				saveSqliteQDetailed(detailedlist, optionlist);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return title;
	}

	// 保存数据
	private void saveSqliteQDetailed(List<FollowQuestionDetailed> dList, List<FollowQuestionOption> oList) {
		db.deleteByWhere(FollowQuestionDetailed.class, "");
		db.deleteByWhere(FollowQuestionOption.class, "");
		db.saveList(dList);
		db.saveList(oList);
	}
}
