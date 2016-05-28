/**
 *
 */
package com.comvee.tnb.ui.follow;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.FollowListAdapter;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.FollowInfo;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.member.MemberRecordFragment;
import com.comvee.tnb.ui.privatedoctor.MemberDoctorListFrag;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 随访列表页面
 *
 * @author SZM
 */
public class FollowListFragment extends BaseFragment implements OnClickListener, OnItemClickListener, OnHttpListener {

    private View view;
    private TextView left, right;
    private ListView listView;
    private View layoutNonDefault;
    private FollowListAdapter mFollowListAdapter;
    private int fromWhere;// 1.来自侧边栏的点击
    private List<FollowInfo> infos;
    private boolean isSliding;
    private TitleBarView mBarView;

    public FollowListFragment() {

    }

    public FollowListFragment(int type) {
        this.fromWhere = type;
    }

    public static FollowListFragment newInstance(int type, boolean isSliding) {
        FollowListFragment fragment = new FollowListFragment(type);
        fragment.setSliding(isSliding);
        return fragment;
    }

    public static FollowListFragment newInstance() {
        return new FollowListFragment(2);
    }

    private void setSliding(boolean isSliding) {
        this.isSliding = isSliding;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.follow_list_fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void initTitleBar() {
        view = View.inflate(getApplicationContext(), R.layout.titlebar_follow_record, null);
        left = (TextView) view.findViewById(R.id.tab_left);
        right = (TextView) view.findViewById(R.id.tab_right);

        findViewById(R.id.bt_addFollowInfo).setVisibility(View.GONE);

        left.setText("我的医生");
        right.setText("医生随访");
        // left.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        left.setBackgroundResource(R.drawable.jiankangzixun_07);
        right.setBackgroundResource(R.drawable.jiankangzixun_08);
        int green = getResources().getColor(R.color.theme_color_green);
        right.setTextColor(Color.WHITE);
        left.setTextColor(green);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mBarView.addView(view, params);
        mBarView.setTitle("");
    }

    @Override
    public void onLaunch(Bundle bundle) {
        super.onLaunch(bundle);
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        if (fromWhere == 1) {
            if (isSliding) {
            }
        } else {
            mBarView.setTitle(getString(R.string.title_doc_follow));
        }
        ConfigParams.setAnswerNew(getApplicationContext(), false);
        // mRoot.setVisibility(View.GONE);

        init();

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initTitleBar();
    }

    private void choiceTabUI() {
        if (FragmentMrg.indexOfFragment(MemberDoctorListFrag.class) == -1) {
            toFragment(MemberDoctorListFrag.class, null, false);
        } else {
            FragmentMrg.popBackToFragment(getActivity(), MemberDoctorListFrag.class, null, 0, 0);
        }
    }

    private void init() {
        layoutNonDefault = findViewById(R.id.layout_no_data);
        Button bt_addFollowInfo = (Button) findViewById(R.id.bt_addFollowInfo);
        bt_addFollowInfo.setOnClickListener(this);
        mFollowListAdapter = new FollowListAdapter(getContext(), infos);
        listView = (ListView) findViewById(R.id.follow_list_view);
        listView.setOnItemClickListener(this);
        requestFollowList();
    }

    private void requestFollowList() {
        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.FOLLOW_LIST);
        http.setOnHttpListener(1, this);
        http.setPostValueForKey("page", "" + 1);
        http.setPostValueForKey("rows", "" + 100);
        // http.setNeedGetCache(true,
        // UserMrg.getCacheKey(ConfigUrlMrg.FOLLOW_LIST));
        http.startAsynchronous();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();
        switch (what) {
            case 1:
                parseFollowList(b, fromCache);
                if (infos == null || infos.size() == 0) {
                    return;
                }
                Collections.sort(infos, new MyComparator());
                break;
            default:
                break;
        }
    }

    private void parseFollowList(byte[] arg1, boolean fromCache) {
        try {
            // mRoot.setVisibility(View.VISIBLE);
            ComveePacket packet = ComveePacket.fromJsonString(arg1);
            if (packet.getResultCode() == 0) {
                infos = new ArrayList<FollowInfo>();
                JSONArray array = packet.getJSONArray("body");
                int len = array.length();
                for (int i = 0; i < len; i++) {
                    JSONObject obj = array.getJSONObject(i);
                    long followupId = obj.optLong("followupId");
                    int doctorId = obj.optInt("doctorId");
                    int memberId = obj.optInt("memberId");
                    int fillStatus = obj.optInt("fillStatus");
                    int dealStatus = obj.optInt("dealStatus");
                    int type = obj.optInt("type");
                    int batchId = obj.optInt("batchId");
                    String insertDt = obj.optString("insertDt");
                    String fillDate = obj.optString("fillDate");
                    String dealDate = obj.optString("dealDate");
                    String typeText = obj.optString("typeText");
                    String doctorName = obj.optString("doctorName");
                    infos.add(new FollowInfo(followupId, doctorId, memberId, insertDt, fillStatus, fillDate, dealStatus, dealDate, type, batchId,
                            typeText, doctorName));
                }
                mFollowListAdapter.setListItems(infos);
                listView.setAdapter(mFollowListAdapter);
                if (mFollowListAdapter.getCount() == 0) {
                    layoutNonDefault.setVisibility(View.VISIBLE);
                } else {
                    layoutNonDefault.setVisibility(View.GONE);
                }
                if (!fromCache) {
                    ComveeHttp.setCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.FOLLOW_LIST), ConfigParams.CHACHE_TIME_SHORT, arg1);
                }
            } else {
                showToast(packet.getResultMsg());
            }
        } catch (Exception e) {
            showToast(R.string.error);
            e.printStackTrace();
        }
    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_addFollowInfo:
                FragmentMrg.toBack(getActivity());
                break;

            case R.id.tab_left:
                choiceTabUI();
                break;

            case TitleBarView.ID_LEFT_BUTTON:
                if (fromWhere == 1) {
                    getActivity().onBackPressed();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        if (infos.get(arg2).getFillStatus() == 0) {
            toFragment(MemberRecordFragment.newInstance(2, false, infos.get(arg2).getFollowupId() + ""), true, true);

        } else {
            toFragment(FollowRecordFragment.newInstance(1, infos.get(arg2).getFollowupId()), true, true);
        }
    }

    @Override
    public boolean onBackPress() {
        if (fromWhere == 1) {
            IndexFrag.toFragment(getActivity(),true); 
            return true;
        }
        // if (fromWhere == 1) {
        // if (isSliding) {
        // ((MainActivity) getActivity()).showLeftView();
        // return true;
        // } else {
        // return false;
        // }
        // } else {
        // return false;
        // }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        // getTitleBar().removeView(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBarView.removeView(view);
        mFollowListAdapter = null;
        layoutNonDefault = null;

    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
        mBarView.removeView(view);
    }

    @SuppressWarnings("rawtypes")
    class MyComparator implements Comparator {
        @Override
        public int compare(Object obj1, Object obj2) {
            FollowInfo k1 = (FollowInfo) obj1;
            FollowInfo k2 = (FollowInfo) obj2;
            if (k1.getFillStatus() < k2.getFillStatus())
                return -1;
            else if (k1.getFillStatus() > k2.getFillStatus())
                return 1;
            else if (k1.getDealStatus() > k2.getDealStatus())
                return -1;
            else if (k1.getDealStatus() < k2.getDealStatus())
                return 1;
            else
                return 0;
        }
    }
}
