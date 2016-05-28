package com.comvee.tnb.network;

import android.text.TextUtils;

import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.RadioComment;
import com.comvee.util.JsonHelper;

import org.json.JSONObject;

/**
 * 电台中的点赞和品论接口
 *
 * @author Administrator
 */
public class RadioCommentControl extends TnbBaseNetwork {
    private NetworkCallBack callBack;
    private String mUrl;
    private RadioComment mTempInfo;

    @Override
    protected void onDoInMainThread(int status, Object obj) {
        if (callBack != null) {
            callBack.callBack(what, status, obj);
        }
    }

    @Override
    protected Object parseResponseJsonData(JSONObject resData) {
        if (mUrl.equals(ConfigUrlMrg.RADIO_COMMENT_ADD)) {
            RadioComment temp = JsonHelper.getObjecByJsonObject(RadioComment.class, resData.optJSONObject("body").optJSONObject("obj"));
            return temp;
        }
        return null;
    }

    /**
     * 点赞
     *
     * @param info
     * @param callBack
     */
    public void requestPraise(RadioComment info, NetworkCallBack callBack) {
        mTempInfo = info;
        mUrl = ConfigUrlMrg.RADIO_COMMENT_ADD_PRAISE;
        putPostValue("id", info.id);
        putPostValue("type", "1");
        start();
    }

    /**
     * 添加评论
     *
     * @param id
     * @param content
     * @param type
     * @param info
     * @param name
     * @param callBack
     */
    public void addComment(String id, String content, String type, RadioComment info, String name, NetworkCallBack callBack) {
        this.callBack = callBack;
        mUrl = ConfigUrlMrg.RADIO_COMMENT_ADD;
        resetRequestParams();
        if (info != null) {
            mTempInfo = info;
            putPostValue("commentId", info.id);
        }
        if (!TextUtils.isEmpty(name)) {
            putPostValue("lohasName", name);
        }
        putPostValue("objId", id);
        putPostValue("commentText", content);
        putPostValue("type", type);
        start();
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

}
