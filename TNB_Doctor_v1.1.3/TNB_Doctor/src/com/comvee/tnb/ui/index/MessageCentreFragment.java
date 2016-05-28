package com.comvee.tnb.ui.index;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.MessageCentreAdapter;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.MessageModel;
import com.comvee.tnb.ui.task.TaskMessageFragment;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.ui.xlistview.XListView.IXListViewListener;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.AppUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统消息中心
 *
 * @author Administrator
 */
public class MessageCentreFragment extends BaseFragment implements OnHttpListener, OnClickListener, OnItemClickListener {
    private XListView listViewSys;
    private List<MessageModel> listMsgSys;
    private MessageCentreAdapter sysAdapter;
    private TextView no_data;
    private int sysPage;
    private int rows;
    private boolean isFromLeftfragment = false;
    private TitleBarView mBarView;

    public static void toFragment(FragmentActivity fragment, boolean fromLeftFragment) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("fromLeftFragment", fromLeftFragment);
        boolean isAnima = true;
        if (fromLeftFragment) {
            isAnima = false;
        }
        FragmentMrg.toFragment(fragment, MessageCentreFragment.class, bundle, isAnima);

    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_messagelist;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        super.onLaunch(bundle);
        if (bundle != null) {
            isFromLeftfragment = bundle.getBoolean("fromLeftFragment");
        }
        if (isFromLeftfragment) {
        }
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        sysPage = 1;
        rows = 10;
        no_data = (TextView) findViewById(R.id.no_data);
        initSysList();
        mBarView.setTitle(getString(R.string.title_sys_info));
        requesetMessageList();
    }

    private void initSysList() {
        listMsgSys = new ArrayList<MessageModel>();
        listViewSys = (XListView) findViewById(R.id.list_view_sys);
        sysAdapter = new MessageCentreAdapter(getApplicationContext(), listMsgSys);
        listViewSys.setAdapter(sysAdapter);
        listViewSys.setOnItemClickListener(this);
        listViewSys.setPullRefreshEnable(false);
        listViewSys.setPullLoadEnable(true);
        listViewSys.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listViewSys.stopRefresh();
                        listViewSys.stopLoadMore();
                    }
                }, 2000);

            }

            @Override
            public void onLoadMore() {
                ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listViewSys.stopLoadMore();
                        listViewSys.stopRefresh();
                    }
                }, 2000);

                requesetMessageList();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void choiceTabUI() {
        try {
            if (listMsgSys.size() == 0 || listMsgSys == null) {
                no_data.setVisibility(View.VISIBLE);
            } else {
                no_data.setVisibility(View.GONE);
            }
            if (sysPage == 1) {
                requesetMessageList();
                listViewSys.setVisibility(View.GONE);
            } else {
                listViewSys.setVisibility(View.VISIBLE);
            }
            sysAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requesetMessageList() {
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.MESSAGE_LIST_NEW);

        if (sysPage == 1) {
            showProgressDialog(getString(R.string.msg_loading));
        }
        http.setPostValueForKey("page", sysPage + "");

        http.setPostValueForKey("rows", rows + "");
        http.setPostValueForKey("type", 2 + "");
        http.setOnHttpListener(1, this);
        http.startAsynchronous();
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        switch (what) {
            case 1:
                cancelProgressDialog();
                parseMessageList(b);
                choiceTabUI();
                break;

            default:
                break;
        }
    }

    private void parseMessageList(byte b[]) {
        try {
            final ComveePacket packet = ComveePacket.fromJsonString(b);
            if (packet.getResultCode() == 0) {
                JSONObject obj = packet.optJSONObject("body");

                JSONObject pager = obj.optJSONObject("pager");
                int totalPages = pager.optInt("totalPages");
                int currentPage = pager.optInt("currentPage");

                if (currentPage == totalPages) {
                    listViewSys.setPullLoadEnable(false);
                }
                sysPage++;
                listViewSys.stopLoadMore();
                parse(obj.optJSONArray("rows"));
            } else {
                ComveeHttpErrorControl.parseError(getActivity(), packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parse(JSONArray list) throws JSONException {
        for (int i = 0; i < list.length(); i++) {
            MessageModel model = new MessageModel();
            JSONObject obj = list.getJSONObject(i);
            model.setCaption(obj.optString("caption"));
            model.setInsertDt(obj.optString("insertDt"));
            model.setJobCenterType(obj.optString("jobCenterType"));
            model.setJobDetailType(obj.optString("jobDetailType"));
            model.setJobDetailUrl(obj.optString("jobDetailUrl"));
            model.setMemberJobDetailId(obj.optString("memberJobDetailId"));
            model.setMemberJobId(obj.optString("memberJobId"));
            model.setNum(obj.optString("num"));
            model.setScore(obj.optString("score"));
            model.setStatus(obj.optString("status"));
            model.setTitle(obj.optString("title"));
            model.setType(obj.optString("type"));
            model.setTypeValue(obj.optString("typeValue"));
            model.setDetailInfo(obj.optString("detailInfo"));
            listMsgSys.add(model);

        }
    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case TitleBarView.ID_LEFT_BUTTON:
                if (isFromLeftfragment) {
                    getActivity().onBackPressed();
                } else {
                    getActivity().onBackPressed();
                }
                break;
            default:
                break;
        }
    }

    private void requestFinish(String finishWarmid) {
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.FINISH_WARM);
        http.setPostValueForKey("memberJobDetailId", finishWarmid);
        http.setOnHttpListener(2, this);
        http.startAsynchronous();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        MessageModel message = (MessageModel) arg1.getTag();

        if (message.getStatus().equals("0")) {
            requestFinish(message.getMemberJobDetailId());
            message.setStatus("1");
            sysAdapter.notifyDataSetChanged();

            try {
                FragmentMrg.getSingleFragment(IndexFrag.class).requestMemUnReadMsgLoader();
            }catch (Exception e){
                e.printStackTrace();
            }

//			((MainActivity) getActivity()).requestMsgUnReadCount();
        }

        String jobId = message.getJobDetailType().equals("1") ? message.getMemberJobDetailId() : message.getMemberJobId();
        if (message.getJobDetailType().equals("3") && message.getStatus().equals("1")) {
            jobId = null;
        }
        if (message.getJobDetailType().equals("50") || message.getJobDetailType().equals("53") || message.getJobDetailType().equals("81")) {
            jobId = message.getDetailInfo();
        }

        if (message.getJobDetailType().equals("3") && message.getType().equals("6")) {
            jobId = "board";
        }
        if (message.getJobDetailType().equals("56")) {
            jobId = message.getJobCenterType() + "";
        }
        if (message.getJobDetailType().equals("24")) {
            jobId = message.getJobCenterType() + "";
            Bundle bundle=new Bundle();
            bundle.putString("insertDt",message.getInsertDt());
            bundle.putString("detailInfo",message.getDetailInfo());
            toFragment(TaskMessageFragment.class,bundle,true);
        }
        AppUtil.jumpByPushOrTask(getActivity(), null, message.getJobCenterType() + "", Integer.parseInt(message.getJobDetailType()), jobId,
                message.getJobDetailUrl(), message.getTitle(), message.getJobCenterType() + "");
    }

    @Override
    public boolean onBackPress() {
        if (isFromLeftfragment) {
            IndexFrag.toFragment(getActivity(),true); 
            return true;
        } else
            return false;
    }
}
