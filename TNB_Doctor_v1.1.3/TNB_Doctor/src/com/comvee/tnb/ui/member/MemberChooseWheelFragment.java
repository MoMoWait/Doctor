package com.comvee.tnb.ui.member;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.WheelSelectAdapter;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.db.DBManager;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.MemberInfo;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UmenPushUtil;
import com.comvee.tool.UserMrg;
import com.comvee.util.StringUtil;
import com.tencent.sugardoctor.widget.wheelview.WheelView;

import org.json.JSONObject;

import java.util.Calendar;

public class MemberChooseWheelFragment extends BaseFragment implements OnClickListener, OnHttpListener {
    private WheelView mWheel;
    private WheelSelectAdapter adapter;
    private MemberInfo mInfo;
    private Button next;
    private int minTime = 1850;
    private TitleBarView mBarView;

    public MemberChooseWheelFragment() {
    }

    public MemberChooseWheelFragment(MemberInfo mInfo) {
        this.mInfo = mInfo;
    }

    public static MemberChooseWheelFragment newInstance(MemberInfo mInfo) {
        MemberChooseWheelFragment fragment = new MemberChooseWheelFragment(mInfo);
        return fragment;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_wheel;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        init();
    }

    private void init() {
        mBarView.setTitle("何时确诊糖尿病");
        next = (Button) findViewById(R.id.btn_next);
        next.setOnClickListener(this);
        mWheel = (WheelView) findViewById(R.id.select_wheel);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        adapter = new WheelSelectAdapter(getApplicationContext(), 1850, year);
        mWheel.setAdapter(adapter);
        mWheel.setSelection((year - minTime) - 1);// 默认当前年份减去一年，即去年
        adapter.setSeletedIndex((year - minTime) - 1);
        mWheel.setOnItemSelectedListener(adapter);
    }

    @Override
    public void onClick(View arg0) {
        mInfo.diabetesTime = adapter.getSeletedIndex() + minTime + "-01-01";
        // mInfo.diseaInt = adapter.getSeletedIndex() + minTime;

        if (mInfo.birInt >= (adapter.getSeletedIndex() + minTime)) {
            showToast("确诊日期不得早于出生日期！");
        }
        // toFragment(MemberChooseNormolFragment.newInstance(mInfo), true);
        else {
            requestGuestReg();
        }
    }

    private void requestGuestReg() {
        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = null;
        if (!(mInfo.ifLogin) && (!ConfigParams.IS_TEST_DATA)) {// 添加
            final String url = ConfigUrlMrg.REG_FIRST_CREATE_MEMBER;
            http = new ComveeHttp(getApplicationContext(), url);
        } else if (ConfigParams.IS_TEST_DATA) {
            http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.GUEST_REG);
        } else {// 修改
            http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.MODIFY_MEMBER1);
            http.setPostValueForKey("paramStr", getModifyString());
            http.setPostValueForKey("perRealPhoto", "");
            http.setOnHttpListener(1, this);
            http.startAsynchronous();
            return;
        }
        http.setPostValueForKey("pushTokenKey", UmenPushUtil.getPushTokenKey());
        http.setPostValueForKey("birthday", mInfo.birthday);
        http.setPostValueForKey("sex", mInfo.sex);
        http.setPostValueForKey("relation", mInfo.relative);
        // http.setPostValueForKey("diabetesTime", mInfo.diabetesTime);
        http.setPostValueForKey("callreason", "RADIO_VALUE_IS");
        if (ConfigParams.IS_TEST_DATA) {
            http.setPostValueForKey("weight", mInfo.memberWeight);
            http.setPostValueForKey("height", mInfo.memberHeight);
        } else {
            http.setPostValueForKey("diabetesTime", mInfo.diabetesTime);
            http.setPostValueForKey("memberName", mInfo.name);
            http.setPostValueForKey("height", mInfo.memberHeight);
            http.setPostValueForKey("weight", mInfo.memberWeight);
        }

        http.setOnHttpListener(1, this);
        http.startAsynchronous();
    }

    private String getModifyString() {
        StringBuffer paramStr = new StringBuffer();
        paramStr.append("[");
        paramStr.append("{\"code\":\"sex\",\"pcode\":\"" + "" + "\",\"value\":\"" + mInfo.sex + "\"},");
        paramStr.append("{\"code\":\"birthday\",\"pcode\":\"" + "" + "\",\"value\":\"" + mInfo.birthday + "\"},");
        paramStr.append("{\"code\":\"JBDAJWS001\",\"pcode\":\"" + "JBDAJWS" + "\",\"value\":\"" + "RADIO_VALUE_IS" + "\"},");
        paramStr.append("{\"code\":\"memberWeight\",\"pcode\":\"" + "" + "\",\"value\":\"" + mInfo.memberWeight + "\"},");
        paramStr.append("{\"code\":\"memberHeight\",\"pcode\":\"" + "" + "\",\"value\":\"" + mInfo.memberHeight + "\"},");
        paramStr.append("{\"code\":\"JBDAJWS00101\",\"pcode\":\"" + "JBDAJWS001" + "\",\"value\":\"" + mInfo.diabetesTime + "\"}");
        paramStr.append("]");
        return paramStr.toString();
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        switch (what) {
            case 1:
                cancelProgressDialog();
                ComveePacket packet;
                try {
                    packet = ComveePacket.fromJsonString(b);
                    if (packet.getResultCode() == 0) {
                        final String sid = packet.optString("sessionID");
                        final String mid = packet.optString("sessionMemberID");
                        UserMrg.setMemberSessionId(getApplicationContext(), mid);
                        UserMrg.setSessionId(getApplicationContext(), sid);
                        // UserMrg.setAoutoLogin(getApplicationContext(), true);
                        JSONObject body = packet.optJSONObject("body");
                        if (body != null && !body.equals("") && body.length() > 0) {
                            mInfo.diabetes_plan = body.optString("diabetes_plan");
                            mInfo.score_describe = body.optString("score_describe");
                            mInfo.score = body.optInt("score");
                            mInfo.testMsg = body.optString("testMsg");
                            JSONObject obj = body.getJSONObject("member");
                            MemberInfo info = new MemberInfo();
                            info.name = obj.optString("memberName");

                            if (StringUtil.checkChinese(info.name)) {
                                info.name = info.name.length() > 8 ? info.name.substring(0, 6) + "..." : info.name;
                            } else {
                                info.name = info.name.length() > 8 ? info.name.substring(0, 6) + "..." : info.name;
                            }
                            info.mId = obj.optString("memberId");
                            info.coordinator = obj.optInt("coordinator");
                            info.photo = obj.optString("picUrl") + obj.optString("picPath");
                            info.mId = obj.optString("memberId");
                            info.callreason = obj.optInt("callreason");
                            info.birthday = obj.optString("birthday");
                            info.memberHeight = obj.optString("memberHeight");
                            info.diabetes_plan = body.optString("diabetes_plan");
                            info.score_describe = body.optString("score_describe");
                            info.ifLogin = body.optBoolean("ifLogin");
                            info.hasMachine = obj.optInt("hasMachine");
                            info.memberWeight = obj.optString("memberWeight");
                            info.relative = obj.optString("relation");
                            UserMrg.setDefaultMember(info);
                            if (ConfigParams.IS_TEST_DATA) {
                                ComveeHttp.clearAllCache(getActivity());
                                DBManager.cleanDatabases(getActivity());
                                UserMrg.setLoginName(getApplicationContext(), null);
                                UserMrg.setLoginPwd(getApplicationContext(), null);
                                UserMrg.setMemberSessionId(getApplicationContext(), mid);
                                UserMrg.setSessionId(getApplicationContext(), sid);
                                UserMrg.setTestData(getApplicationContext(), true);
                                UserMrg.setTestDataSessionId(getApplicationContext(), sid);
                                UserMrg.setTestDataMemberId(getApplicationContext(), mid);
                                UserMrg.DEFAULT_MEMBER.callreason = 1;
                            }
                        }
                        UserMrg.DEFAULT_MEMBER.callreason = 1;// 添加一律为糖尿病
                        IndexFrag.toFragment(getActivity(),true); 
                        UserMrg.setAoutoLogin(getActivity(), true);
                    } else {
                        ComveeHttpErrorControl.parseError(getActivity(), packet);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cancelProgressDialog();
                break;

            default:
                break;
        }
    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }
}
