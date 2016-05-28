package com.comvee.tnb.ui.pharmacy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.comvee.ThreadHandler;
import com.comvee.network.BackgroundTasks;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.PharmacyAdaptr;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.network.PostFinishInterface;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;
import com.comvee.ui.remind.TimeRemindTransitionInfo;
import com.comvee.ui.remind.TimeRemindUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * 用药
 */
public class PharmacyListFragment extends BaseFragment implements OnClickListener, OnItemClickListener, PostFinishInterface {
    private ListView mListView;
    private PharmacyAdaptr mAdaptr;
    private List<TimeRemindTransitionInfo> mList;
    private TimeRemindUtil mUtil;
    private View emptyview, btn_create;
    private TitleBarView mBarView;

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_pharmacy_list;
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        mUtil = TimeRemindUtil.getInstance(getApplicationContext());
        init();
        mBarView.setTitle(getResources().getString(R.string.record_sugarblood_drug));
        notifyListView();

//		ObjectLoader<PharmacyInfo> loader = new ObjectLoader<PharmacyInfo>();
//		loader.loadArrayByBodyobj(PharmacyInfo.class, ConfigUrlMrg.DRUG_REMIND_LIST,loader.new CallBack(){
//
//		});

        showProgressDialog(getString(R.string.msg_loading));
        requestRemindListNew(getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onFragmentResult(Bundle data) {
        super.onFragmentResult(data);
        if (data != null) {
            showProgressDialog(getString(R.string.msg_loading));
            requestRemindListNew(getApplicationContext());
        }

    }

    private void init() {
        btn_create = findViewById(R.id.create);
        emptyview = findViewById(R.id.emptyview);
        mListView = (ListView) findViewById(R.id.lv_pharmacy);
        mAdaptr = new PharmacyAdaptr(getApplicationContext(), mList);
        mListView.setAdapter(mAdaptr);
        mListView.setOnItemClickListener(this);
        btn_create.setOnClickListener(this);
        mBarView.setRightButton(R.drawable.ask_list_titlebat_right, this);

    }

    private void getLocTimeList() {
        mList = new ArrayList<TimeRemindTransitionInfo>();
        initLocTimes();

    }

    /**
     * 获取本地用药
     */
    private void initLocTimes() {
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... arg0) {
                mUtil.star();
                mUtil.getAllRemindTimeList();
                List<TimeRemindTransitionInfo> tempList = mUtil.getRemindTimeList("type=4 and memberId='" + UserMrg.DEFAULT_MEMBER.mId
                        + "' order by diabolo desc ,time asc");
                if (tempList.size() != 0) {
                    for (int i = 0; i < tempList.size(); i++) {
                        if (tempList.get(i).isDiabolo()) {
                            if (!mList.contains(tempList.get(i)) && tempList.get(i).getNextTime() > System.currentTimeMillis()) {
                                mList.add(tempList.get(i));
                            }
                        } else {
                            if (!mList.contains(tempList.get(i))) {
                                mList.add(tempList.get(i));
                            }
                        }
                    }
                } else {
                    mList.clear();
                }
                return tempList.size();
            }

            protected void onPostExecute(Integer result) {
                cancelProgressDialog();
                notifyListView();
                mListView.setEmptyView(emptyview);
                mListView.setVisibility(View.VISIBLE);
            }
        }.execute();
    }

    private void notifyListView() {
        mAdaptr.setList(mList);
        mAdaptr.notifyDataSetChanged();
    }

    @SuppressLint("NewApi")
    private void requestRemindListNew(final Context context) {
        mListView.setVisibility(View.GONE);
        IndexListDataRequest indexListDataRequest = new IndexListDataRequest();
        indexListDataRequest.setPostFinishInterface(this);
        indexListDataRequest.start();
        // BackgroundTasks.getInstance().push(BackgroundTasks.TASK_NETWORK, indexListDataRequest);
    }

    @Override
    public void postFinish(int status, Object obj) {
        if (status == 0) {
            try {
                PharmacyUtil.parseRemindList(getApplicationContext(), ComveePacket.fromJsonString(obj + ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            getLocTimeList();
        } else {
            showToast(obj + "");
        }
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case TitleBarView.ID_RIGHT_BUTTON:
            case R.id.create:
                toFragment(new PharmacySetTimeFrag(null, PharmacySetTimeFrag.FROM_NEW), true, true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//        toFragment(new PharmacySetTimeFrag(null, PharmacySetTimeFrag.FROM_NEW), true, true);
        toFragment(new PharmacySetTimeFrag((TimeRemindTransitionInfo) arg0.getAdapter().getItem(arg2),
                PharmacySetTimeFrag.FROM_HISTORY), true, true);
    }

}
