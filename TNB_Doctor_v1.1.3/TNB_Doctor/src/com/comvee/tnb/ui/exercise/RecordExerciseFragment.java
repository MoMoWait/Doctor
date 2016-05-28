package com.comvee.tnb.ui.exercise;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.comvee.ThreadHandler;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tnb.widget.pageview.PageViewControl;
import com.comvee.tool.UserMrg;
import com.comvee.util.CacheUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 运动记录
 */
@SuppressLint("ValidFragment")
public class RecordExerciseFragment extends BaseFragment implements OnClickListener, OnItemClickListener {
    public static boolean reload = false;// 需不需要重新加载
    private View buttonCreate;
    private XListView mListview;
    private SportRecordAdapter listviewAdapter;
    private TitleBarView mBarView;
    private PageViewControl mControl;

    public RecordExerciseFragment() {
    }

    public static RecordExerciseFragment newInstance() {
        RecordExerciseFragment fragment = new RecordExerciseFragment();
        return fragment;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.record_exercise_input_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);

        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        mBarView.setTitle("运动");
        mBarView.setRightButton(R.drawable.ask_list_titlebat_right, this);
        mListview = (XListView) findViewById(R.id.xlistview);
        mListview.setVisibility(View.INVISIBLE);
        listviewAdapter = new SportRecordAdapter();
        mListview.setOnItemClickListener(this);
        mListview.setPullLoadEnable(true);
        mListview.setPullRefreshEnable(true);

        PageViewControl.onPageViewListenerAdapter listener = new PageViewControl.onPageViewListenerAdapter() {
            @Override
            public void onDataCallBack(int page, ArrayList listData) {
                super.onDataCallBack(page, listData);
                mListview.setVisibility(View.VISIBLE);
                if (page <= 1 && (listData == null || listData.isEmpty())) {
                    mListview.setEmptyView(findViewById(R.id.emptyview));
                    buttonCreate = findViewById(R.id.btn_opt);
                    buttonCreate.setOnClickListener(RecordExerciseFragment.this);
                    findViewById(R.id.btn_opt).setOnClickListener(RecordExerciseFragment.this);
                }
            }

            @Override
            public void onStopLoading() {
                super.onStopLoading();
                cancelProgressDialog();
            }
        };
        mControl = new PageViewControl(mListview, SportRecord.class, listviewAdapter, ConfigUrlMrg.SPORT_LIST, listener);
        showProgressDialog(getString(R.string.msg_loading));
        mControl.setPageRowCount(5);
        mControl.load();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onFragmentResult(Bundle data) {
        super.onFragmentResult(data);
        if (data != null) {
            SportRecord record = (SportRecord) data.getSerializable("deletemodel");
            if (record != null) {
                listviewAdapter.remove(record);
                listviewAdapter.notifyDataSetChanged();

                //更新缓存
                ThreadHandler.postWorkThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Serializable obj = (Serializable) listviewAdapter.getDatas();
                            CacheUtil.getInstance().putObjectById(UserMrg.getCacheKey(ConfigUrlMrg.SPORT_LIST), obj);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                if (listviewAdapter.getDatas() == null || listviewAdapter.getDatas().isEmpty()) {
                    mListview.setEmptyView(findViewById(R.id.emptyview));
                    findViewById(R.id.btn_opt).setOnClickListener(RecordExerciseFragment.this);
                }


            } else {
                mControl.loadRefresh();
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v == buttonCreate || v.getId() == R.id.btn_opt) {
            toFragment(CreateSportFragment.class, null, true);
        } else if (v.getId() == TitleBarView.ID_RIGHT_BUTTON) {
            toFragment(new CreateSportFragment(), true, true);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        toFragment(new UpdateSportFragment(listviewAdapter.getItem(position - 1)), true, true);
    }

    @Override
    public boolean onBackPress() {
        IndexFrag.toFragment(getActivity(), true);
        return true;
    }
}
