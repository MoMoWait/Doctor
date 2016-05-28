package com.comvee.tnb.ui.record;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.comvee.frame.BaseFragmentActivity;
import com.comvee.tnb.R;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.model.HealthResultInfo;
import com.comvee.tool.AppUtil;
import com.comvee.tool.UserMrg;
import com.comvee.util.BundleHelper;
import com.comvee.util.JsonHelper;

public class RecordMrg {//跳转不同页面判断

    public static void getRecordDetailList(final String memId, final FragmentActivity fragment, final ComveePacket packet, final int type) {
        String cache = ComveeHttp.getCache(fragment, UserMrg.getCacheKey(ConfigUrlMrg.RECORD_DETAIL_LIST));

        if (packet.optJSONObject("body") == null || "{}".equals(packet.optJSONObject("body").toString())) {
            return;
        } else if (packet.optJSONObject("body").optInt("isCentre") == 1) {//跳转到异常结果页
            if (cache != null && !"".equals(cache)) {
                try {
                    ComveePacket comveePacket = ComveePacket.fromJsonString(cache);
                    AppUtil.checkUser(fragment, memId,  RecordDetailFragment.newInstance(packet, comveePacket, type), true, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ((BaseFragmentActivity) fragment).showProgressDialog(fragment.getString(R.string.msg_loading));
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... arg0) {
                        String result = null;
                        ComveeHttp http = new ComveeHttp(fragment, ConfigUrlMrg.RECORD_DETAIL_LIST);
                        result = http.startSyncRequestString();
                        return result;
                    }

                    protected void onPostExecute(String result) {
                        if (result == null || "".equals(result)) {
                            Toast.makeText(fragment, fragment.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ((BaseFragmentActivity) fragment).cancelProgressDialog();
                        try {
                            ComveePacket packet1 = ComveePacket.fromJsonString(result);
                            if (packet1.getResultCode() == 0) {
                                ComveeHttp.setCache(fragment, UserMrg.getCacheKey(ConfigUrlMrg.RECORD_DETAIL_LIST), ConfigParams.DAY_TIME_LONG,
                                        result);
                                AppUtil.checkUser(fragment, memId,  RecordDetailFragment.newInstance(packet, packet1, type), true, true);
                            } else {
                                ComveeHttp.setCache(fragment, UserMrg.getCacheKey(ConfigUrlMrg.RECORD_DETAIL_LIST), ConfigParams.DAY_TIME_LONG, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    ;
                }.execute();
            }
        } else if (packet.optJSONObject("body").optInt("isCentre") == 0) {//跳转到正常结果页
            try {
                HealthRecordRusultFragment frag = new HealthRecordRusultFragment();
                Bundle bundle = BundleHelper.getBundleBySerializable(JsonHelper.getObjecByJsonObject(HealthResultInfo.class, packet.optJSONObject("body")));
                bundle.putInt("fromWhere", type);
                frag.setArguments(bundle);
                AppUtil.checkUser(fragment, memId, frag, true, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
