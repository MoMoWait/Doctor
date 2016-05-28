package com.comvee.tnb.ui.ask;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.comvee.FinalDb;
import com.comvee.db.sqlite.DbModel;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.model.NewAskModel;
import com.comvee.tnb.model.PageModel;
import com.comvee.tnb.model.ServerListModel;
import com.comvee.tool.UserMrg;
import com.comvee.util.JsonHelper;
import com.comvee.util.StringUtil;

public class AskQuestionParseHelp {

    public static PageModel parseMsgList(Context context, String docId, FinalDb finalDb, JSONObject resData) {
        JSONObject body = resData.optJSONObject("body");
        JSONObject pager = body.optJSONObject("pager");
        ConfigParams.setAskKey(context, UserMrg.DEFAULT_MEMBER.mId + docId + "isQuestion", body.optInt("isQuestion"));
        ConfigParams.setAskKey(context, UserMrg.DEFAULT_MEMBER.mId + docId + "is_phone", body.optInt("isPhone"));
        ConfigParams.setAskKey(context, UserMrg.DEFAULT_MEMBER.mId + docId + "is_showdoc", body.optInt("isShowDoctor"));
        ConfigParams.setAskServerKey(context, UserMrg.DEFAULT_MEMBER.mId + docId + "isHaveBookService", body.optBoolean("isHaveBookService"));
        ConfigParams.setAskServerKey(context, UserMrg.DEFAULT_MEMBER.mId + docId + "isBookServiceExpired", body.optBoolean("isBookServiceExpired"));
        PageModel pageModel = null;
        if (pager != null) {
            pageModel = JsonHelper.getObjecByJsonObject(PageModel.class, pager);
        }
        JSONArray rows = body.optJSONArray("rows");
        String returnTime = body.optJSONObject("returnModel").optString("returnDate");

        String doctorName = body.optString("doctorName");
        String headInageUrl = body.optString("headImageUrl");
        String docTellId = body.optString("relateId");
        if (rows.length() == 0) {
            return pageModel;
        }
        for (int i = 0; i < rows.length(); i++) {
            JSONObject obj = rows.optJSONObject(i);
            NewAskModel askModel = parseJSONObject(obj);
            if (pageModel != null) {
                askModel.setCurrentPage(pageModel.currentPage);
                askModel.setPageSize(pageModel.pageSize);
                askModel.setTotalPages(pageModel.totalPages);
                askModel.setTotalRows(pageModel.totalRows);
            }

            askModel.setDoctorName(doctorName);
            askModel.setHeadImageUrl(headInageUrl);
            if (StringUtil.isNumble(docTellId)) {
                askModel.setDocTellId(Long.parseLong(docTellId));
            }
            if (i == rows.length() - 1) {
                pageModel.starTime = askModel.getInsertDt();
            }
            upOrSaveModel(askModel, finalDb);// 插入本地数据库
        }
        return pageModel;
    }

    private static void upServerList(FinalDb finalDb, ArrayList<ServerListModel> serverListModels, int type) {
        if (serverListModels == null) {
            return;
        }
        for (int i = 0; i < serverListModels.size(); i++) {
            ServerListModel model = serverListModels.get(i);
            if (type == 1) {
                finalDb.save(model);
            } else {
                finalDb.update(model);
            }
        }
    }

    private static void upOrSaveModel(NewAskModel askModel, FinalDb finalDb) {
        DbModel dbModel = finalDb.findDbModelBySQL("select count(*) from NewAskModel where sid=" + askModel.getSid());
        if (Integer.parseInt((String) dbModel.getDataMap().get("count(*)")) == 0) {
            if (askModel.getIsValid().equals("0")) {
                return;
            } else {
                finalDb.save(askModel);
                upServerList(finalDb, askModel.getList(), 1);
            }
        } else {
            if (askModel.getIsValid().equals("0")) {
                finalDb.deleteByWhere(NewAskModel.class, "sid=" + askModel.getSid());
                finalDb.deleteByWhere(ServerListModel.class, "sid=" + askModel.getSid());
                return;
            } else {
                finalDb.update(askModel, "sid=" + askModel.getSid());
                upServerList(finalDb, askModel.getList(), 2);
            }

        }
    }

    private static NewAskModel parseJSONObject(JSONObject obj) {
        NewAskModel askModel = new NewAskModel();
        askModel.setSid(obj.optLong("sid"));
        askModel.setIsValid(obj.optString("isValid"));
        askModel.setIsCount(obj.optInt("isCount"));
        askModel.setDoctorId(obj.optLong("doctorId"));
        askModel.setOwnerType(obj.optInt("ownerType"));
        askModel.setMsgContent(obj.optString("userMsg"));
        askModel.setMsgType(obj.optInt("msgType"));
        askModel.setMemberId(obj.optLong("memberId"));
        askModel.setInsertDt(obj.optString("insertDt"));
        askModel.setIsDispose(obj.optInt("isDispose"));
        askModel.setForeignId(obj.optLong("foreignId"));
        JSONObject dataStruct = obj.optJSONObject("dataStruct");
        if (dataStruct == null) {
            return askModel;
        }
        // 1 血糖异常 2 血压异常 3 BMI异常 4 咨询 5 任务推荐 6 创建随访 7 完成随访
        // 8 创建日程提醒 9 请求确认医患关系 10 通过医患关系 11 预约 12无服务 13 纯消息展示
        switch (askModel.getMsgType()) {

            case 1:
                askModel.setBloodglucoseValue(dataStruct.optDouble("bloodglucoseValue"));
                askModel.setParamCode(dataStruct.optString("paramCode"));
                askModel.setParamLevel(dataStruct.optInt("paramLevel"));
                askModel.setRecordTime(dataStruct.optString("recordTime"));
                askModel.setUnit(dataStruct.optString("unit"));
                askModel.setTitle(dataStruct.optString("title"));

                break;
            case 2:
                askModel.setBloodpressurediastolic(dataStruct.optInt("bloodpressurediastolic"));
                askModel.setBloodpressuresystolic(dataStruct.optInt("bloodpressuresystolic"));
                askModel.setParamCode(dataStruct.optString("paramCode"));
                askModel.setParamLevel(dataStruct.optInt("paramLevel"));
                askModel.setRecordTime(dataStruct.optString("recordTime"));
                askModel.setUnit(dataStruct.optString("unit"));
                askModel.setTitle(dataStruct.optString("title"));
                break;
            case 3:
                askModel.setParamLevel(dataStruct.optInt("paramLevel"));
                askModel.setRecordTime(dataStruct.optString("recordTime"));
                askModel.setParamCode(dataStruct.optString("paramCode"));
                askModel.setWeight(dataStruct.optInt("weight"));
                askModel.setHeigth(dataStruct.optInt("heigth"));
                askModel.setBmiValue(dataStruct.optDouble("bmiValue"));
                askModel.setTitle(dataStruct.optString("title"));
                break;
            case 4:
                askModel.setContent(dataStruct.optString("content"));
                askModel.setAttachList(dataStruct.optString("attachList"));
                askModel.setAttachType(dataStruct.optString("attachType"));
                askModel.setAttachUrl(dataStruct.optString("attachUrl"));
                askModel.setVoiceMins(dataStruct.optInt("voiceMins"));
                break;
            case 5:
                askModel.setTitle(dataStruct.optString("title"));
                askModel.setContent(dataStruct.optString("userContent"));
                askModel.setJobList(dataStruct.optString("jobList"));
                break;

            case 6:
            case 7:
                askModel.setEntrance(dataStruct.optInt("entrance"));
                askModel.setContent(dataStruct.optString("userContent"));
                askModel.setTitle(dataStruct.optString("title"));
                askModel.setFollowupType(dataStruct.optInt("followupType"));
                break;
            case 8:
                askModel.setTitle(dataStruct.optString("title"));
                askModel.setRemindTitle(dataStruct.optString("remindTitle"));
                askModel.setDate(dataStruct.optString("date"));
                askModel.setTime(dataStruct.optString("time"));
                askModel.setRemindPeople(dataStruct.optInt("remindPeople"));
                askModel.setRemark(dataStruct.optString("remark"));
                break;

            case 9:

                break;

            case 10:
                askModel.setContent(dataStruct.optString("content"));
                askModel.setAttachList(dataStruct.optString("attachList"));
                askModel.setAttachType(dataStruct.optString("attachType"));
                askModel.setAttachUrl(dataStruct.optString("attachUrl"));
                askModel.setVoiceMins(dataStruct.optInt("voiceMins"));
                break;
            case 11:
                // askModel.setForeignId(obj.optLong("foreignId "));
                askModel.setStatus(dataStruct.optInt("status"));
                askModel.setTitle(dataStruct.optString("title"));
                askModel.setStartTime(dataStruct.optString("startTime"));
                askModel.setEndTime(dataStruct.optString("endTime"));
                askModel.setDate(dataStruct.optString("date"));
                askModel.setDoctorName(dataStruct.optString("doctorName"));
                break;
            case 12:

                askModel.setContent(dataStruct.optString("content"));
                askModel.setTitle(dataStruct.optString("title"));
                JSONArray list = dataStruct.optJSONArray("list");
                if (list != null) {
                    ArrayList<ServerListModel> mList = new ArrayList<ServerListModel>();
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject obj2 = list.optJSONObject(i);
                        ServerListModel listmodel = new ServerListModel();
                        listmodel.setContent(obj2.optString("content"));
                        listmodel.setImage(obj2.optString("image"));
                        listmodel.setUrl(obj2.optString("url"));
                        listmodel.setSid(askModel.getSid());
                        mList.add(listmodel);
                    }
                    askModel.setList(mList);
                }
                break;
            case 13:
                askModel.setContent(dataStruct.optString("content"));
                askModel.setTitle(dataStruct.optString("title"));
                break;
            case 19:
                askModel.setTitle(dataStruct.optString("title"));
                askModel.setStartTime(dataStruct.optString("startTime"));
                askModel.setEndTime(dataStruct.optString("endTime"));
                askModel.setRemark(dataStruct.optString("packageInfo"));
                break;
            case 22:
                askModel.setContent(dataStruct.optString("content"));
                askModel.setTitle(dataStruct.optString("title"));
                askModel.setStartTime(dataStruct.optString("startTime"));
                askModel.setEndTime(dataStruct.optString("endTime"));
                askModel.setRemark(dataStruct.optString("packageInfo"));
                break;
            default:
                break;

        }
        return askModel;
    }
}
