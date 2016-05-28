package com.comvee.tnb.ui.assess;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.GuideInfo;
import com.comvee.tnb.ui.member.MemberRecordFragment;
import com.comvee.tnb.ui.more.WebFragment;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.ui.xlistview.XListView.IXListViewListener;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AssessListFragment extends BaseFragment implements IXListViewListener, OnClickListener, OnItemClickListener, OnHttpListener,
        DialogInterface.OnClickListener {

    private XListView listView;
    private ArrayList<GuideInfo> listItems = new ArrayList<GuideInfo>();
    private AskAdapter mAdapter;
    private int pageNum = 1;
    private View layoutNonDefault;
    private boolean isLimit;
    private TitleBarView mBarView;

    public AssessListFragment() {

    }

    public static AssessListFragment newInstance() {
        AssessListFragment fragment = new AssessListFragment();
        return fragment;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_ask_list;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        init();
        mBarView.setTitle(getString(R.string.title_assess_history));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void init() {
        layoutNonDefault = findViewById(R.id.layout_non_default);

        final TextView tvNonMsg = (TextView) findViewById(R.id.tv_non_msg);
        final Button btnNonJump = (Button) findViewById(R.id.btn_non_jump);
        final ImageView imgNonTag = (ImageView) findViewById(R.id.img_non_tag);

        // tvNonMsg.setText(Html.fromHtml("预测<font color='#1a9293'>健康风险</font>，全面评估身体"));
        // tvNonMsg.setText(Html.fromHtml("您还<font color='#1a9293'>没做过评估</font>，赶紧来做一份吧"));
        tvNonMsg.setText("您还没做过评估，赶紧来做一份吧");
        btnNonJump.setOnClickListener(this);
        imgNonTag.setImageResource(R.drawable.task_no_data);
        btnNonJump.setText("立即做一份健康评估");

        listView = (XListView) findViewById(R.id.list_view);
        listView.setPullRefreshEnable(false);
        listView.setPullLoadEnable(true);
        listView.setXListViewListener(this);
        mAdapter = new AskAdapter();
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        listView.setVisibility(View.GONE);
        requestAssessList(1);
        requestAssessCheck();
    }

    private void requestAssessCheck() {
        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.ASSESS_CHECK);
        http.setOnHttpListener(3, this);
        http.startAsynchronous();
    }

    /**
     * 获取可用的套餐列表
     */
    private void requestAssessList(int pageNum) {

        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.ASSESS_LIST);
        http.setOnHttpListener(1, this);
        http.setPostValueForKey("page", "" + pageNum);
        http.setPostValueForKey("rows", "" + ConfigParams.PAGE_COUNT);
        if (pageNum == 1) {
            listItems.clear();
        }
        this.pageNum = pageNum;
        http.startAsynchronous();
    }

    private void parseReport(byte[] b) {
        try {

            ComveePacket packet = ComveePacket.fromJsonString(b);

            if (packet.getResultCode() == 0) {
                JSONArray array = packet.getJSONObject("body").getJSONArray("imageList");
                ArrayList<String> imgs = new ArrayList<String>();
                int len = array.length();
                for (int i = 0; i < len; i++) {
                    imgs.add(array.getString(i));
                }
            } else {
                ComveeHttpErrorControl.parseError(getActivity(), packet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseAssessList(byte[] arg1, boolean fromCache) {
        try {
            listView.stopLoadMore();
            ComveePacket packet = ComveePacket.fromJsonString(arg1);
            if (packet.getResultCode() == 0) {

                JSONArray array = packet.getJSONObject("body").getJSONArray("rows");
                int len = array.length();
                for (int i = 0; i < len; i++) {
                    JSONObject obj = array.getJSONObject(i);
                    GuideInfo info = new GuideInfo();
                    info.time = obj.optString("time");
                    info.date = obj.optString("date");
                    info.type = obj.optInt("type");
                    info.iid = obj.optString("iid");
                    info.url = String.format("%s?sessionID=%s&sessionMemberID=%s&iid=%s", obj.optString("url"),
                            UserMrg.getSessionId(getApplicationContext()), UserMrg.getMemberSessionId(getApplicationContext()), info.iid);
                    listItems.add(info);
                }
                mAdapter.notifyDataSetChanged();
                int total = packet.getJSONObject("body").getJSONObject("pager").optInt("totalRows");
                int curPage = packet.getJSONObject("body").getJSONObject("pager").optInt("currentPage");
                if (listItems.size() >= total) {
                    listView.setPullLoadEnable(false);
                }
                if (total != 0) {
                    layoutNonDefault.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                } else {
                    listView.setVisibility(View.GONE);
                    layoutNonDefault.setVisibility(View.VISIBLE);
                }

                if (!fromCache && curPage == 1) {// 只缓存第一页
                    ComveeHttp.setCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.ASSESS_LIST), ConfigParams.CHACHE_TIME_SHORT, arg1);
                }

            } else {
                ComveeHttpErrorControl.parseError(getActivity(), packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.btn_submit:
                break;
            case R.id.btn_non_jump:
                if (ConfigParams.IS_TEST_DATA) {
                    toFragment(LoginFragment.class, null, true);
                } else {
                    if (isLimit) {
                        showToast("亲，本套餐的评估次数已用完！我们将推出更多套餐，敬请期待！");
                    } else {
                        MemberRecordFragment fragment = MemberRecordFragment.newInstance(1, 0, false);
                        // MemberCreateFragment frag =
                        // MemberCreateFragment.newInstance(UserMrg.DEFAULT_MEMBER.mId);
                        // frag.setFromEdit(true);
                        toFragment(fragment, true, true);
                    }
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // requestImg(listItems.get(arg2 - 1).iid);
        WebFragment frag = WebFragment.newInstance("评估报告", listItems.get(arg2 - 1).url);
        toFragment(frag, true, true);
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();
        switch (what) {
            case 1:
                parseAssessList(b, fromCache);
                break;
            case 2:
                parseReport(b);
                break;
            case 3:
                parseAssessCheck(b);
                break;
            default:
                break;
        }
    }

    private void parseAssessCheck(byte[] b) {

        try {
            ComveePacket packet = ComveePacket.fromJsonString(b);

            if (packet.getResultCode() == 0) {

                if (packet.optJSONObject("body").optInt("assessmentNum") > 0 || packet.optJSONObject("body").optInt("assessmentNum") == -1) {
                    isLimit = false;
                    // layoutLimit.setVisibility(View.GONE);
                } else {
                    if (!ConfigParams.IS_TEST_DATA) {
                        isLimit = true;
                    }

                }

            }

        } catch (Exception e) {
            showToast(R.string.error);
            e.printStackTrace();
        }

    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();
        // showToast(R.string.time_out);
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }

    @Override
    public void onRefresh() {
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                listView.stopRefresh();
                listView.stopLoadMore();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        requestAssessList(pageNum + 1);
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                listView.stopLoadMore();
                listView.stopRefresh();
            }
        }, 2000);

    }

    @Override
    public boolean onBackPress() {
        if (FragmentMrg.indexOfFragment(AssessFragment.class) != -1) {
            FragmentMrg.popBackToFragment(getActivity(), AssessFragment.class, null);
            return true;
        }
        return false;
    }

    class AskAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listItems.size();
        }

        @Override
        public GuideInfo getItem(int arg0) {
            return listItems.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {

            if (arg1 == null) {
                arg1 = View.inflate(getApplicationContext(), R.layout.item_report, null);
            }
            TextView tvName = (TextView) arg1.findViewById(R.id.tv_name);
            TextView tvContent = (TextView) arg1.findViewById(R.id.tv_content);
            GuideInfo info = getItem(arg0);
            tvContent.setText(info.date);
            tvName.setText(String.format("第%d份评估报告", getCount() - arg0));
            return arg1;
        }

    }
}
