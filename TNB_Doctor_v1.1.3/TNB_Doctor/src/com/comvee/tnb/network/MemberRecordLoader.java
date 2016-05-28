package com.comvee.tnb.network;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;


import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.ArchivePic;
import com.comvee.tnb.model.MembeArchive;
import com.comvee.tnb.model.MemberArchiveItem;
import com.comvee.tnb.model.MemberRecordModel;
import com.comvee.tool.UserMrg;

/**
 * 成员档案接口
 * 
 * @author Administrator
 * 
 */
public class MemberRecordLoader extends TnbBaseNetwork {
	private String url;
	private NetworkCallBack callBack;

	public void loaderUseMsg(NetworkCallBack callBack, int entrance) {
		resetRequestParams();
		this.callBack = callBack;
		url = ConfigUrlMrg.MEMBER_ARCHIVENEW;
		putPostValue("entrance", entrance + "");

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
		return parserMemberInfo(resData);
	}

	@Override
	public String getUrl() {
		return url;
	}

	// 回调解析数据
	private MemberRecordModel parserMemberInfo(JSONObject resData) {
		if (resData == null) {
			return null;
		}
		MemberRecordModel mContext = new MemberRecordModel();
		ArrayList<MembeArchive> archives = new ArrayList<MembeArchive>();
		ArchivePic archivePic = new ArchivePic();
		try {
			JSONObject body = resData.optJSONObject("body");
			JSONObject pic = body.optJSONObject("pic");
			JSONArray memberArchive = body.optJSONArray("memberArchive");

			archivePic.setPicPath(pic.optString("picPath"));
			archivePic.setPicPathS(pic.optString("picPathS"));
			archivePic.setPicUrl(pic.optString("picUrl"));

			String photo = archivePic.getPicUrl() + archivePic.getPicPath();
			if (!TextUtils.isEmpty(photo) && !photo.equalsIgnoreCase("null")) {
				archivePic.setPhone(photo);
				UserMrg.DEFAULT_MEMBER.photo = photo;
			}

			for (int i = 0; i < memberArchive.length(); i++) {
				JSONObject object = memberArchive.getJSONObject(i);
				MembeArchive archive = new MembeArchive();
				archive.setItemCode(object.optString("itemCode"));
				archive.setDictName(object.optString("dictName"));
				if (object.optString("dictName").equals("昵称") || object.optString("dictName").equals("姓名")) {
					UserMrg.DEFAULT_MEMBER.name = object.optString("values");
				}
				archive.setpCode(object.optString("pCode"));
				archive.setItemType(object.optString("itemType"));
				archive.setIsShow(object.optString("isShow"));
				archive.setHelp(object.optString("help"));
				archive.setSeq(object.optString("seq"));
				archive.setTie(object.optString("tie"));
				archive.setIsNeed(object.optString("isNeed"));
				archive.setCategory(object.optString("category"));
				archive.setCategoryName(object.optString("categoryName"));
				archive.setValues(object.optString("values"));
				archive.setRule(object.optJSONObject("rule"));

				List<MemberArchiveItem> optionItems = null;
				JSONArray itemList = object.optJSONArray("itemList");
				if (itemList != null) {
					optionItems = new ArrayList<MemberArchiveItem>();
					for (int j = 0; j < itemList.length(); j++) {
						object = itemList.getJSONObject(j);
						MemberArchiveItem optionItem = new MemberArchiveItem();
						optionItem.setId(object.optString("id"));
						optionItem.setValueCode(object.optString("valueCode"));
						optionItem.setValueName(object.optString("valueName"));
						optionItem.setIsRestrain(object.optString("isRestrain"));
						optionItem.setIsValue(object.optString("isValue"));
						optionItems.add(optionItem);

					}
				}
				archive.setItemList(optionItems);

				if (archive.getSeq().equals("seq6")) {
					if (archive.getItemList().get(0).getIsValue().equals("1")) {
						UserMrg.DEFAULT_MEMBER.callreason = 1;
					} else {
						UserMrg.DEFAULT_MEMBER.callreason = 0;
					}
				}
				archives.add(archive);
			}
			mContext.archivePic = archivePic;
			mContext.archives = archives;
		} catch (Exception e) {
		}
		return mContext;

	}

}
