package com.comvee.tnb.ui.ask;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.DoctorListAdapter;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.model.DoctorInfo;
import com.comvee.tnb.ui.privatedoctor.DoctorServerList;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tnb.widget.pageview.PageViewControl;
import com.comvee.tool.UITool;

import java.util.ArrayList;

/**
 * 医生搜索界面
 *
 * @author Administrator
 */
public class SearchFragment extends BaseFragment implements View.OnClickListener,AdapterView.OnItemClickListener {
    public static int WHERE_PRIVATE_DOCTOR = 1;
    public static int WHERE_DOCLIST = 0;
    private EditText titleEditTextView;
    private TextView textViewTip;
    private XListView listView;
    private DoctorListAdapter mAdapter;
    /**
     * 0专家团队1私人医生
     */
    private int fromWhere;
    private String doctorName;
    private ImageView imageViewNodata;
    private LinearLayout lodinggroup;
    private TitleBarView mBarView;

    private PageViewControl mControl;

    public SearchFragment() {

    }

    public static void toFragment(FragmentActivity fragment, int fromWhere) {
        Bundle bundle = new Bundle();
        bundle.putInt("fromwhere", fromWhere);
        FragmentMrg.toFragment(fragment, SearchFragment.class, bundle, true);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.doctorlist_search_frag;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        super.onLaunch(bundle);
        if (bundle != null) {
            fromWhere = bundle.getInt("fromwhere");
        }

        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        View newTitleView = mBarView.resetLayout(R.layout.health_seach_titleview);
        titleEditTextView = (EditText) newTitleView.findViewById(R.id.search_et);
        lodinggroup = (LinearLayout) findViewById(R.id.lin_group_of_search);
        imageViewNodata = (ImageView) findViewById(R.id.loadingImageView_of_search);
        textViewTip = (TextView) findViewById(R.id.tv_of_search);

        newTitleView.findViewById(R.id.btn_back).setOnClickListener(this);
        newTitleView.findViewById(R.id.btn_top_right).setOnClickListener(this);
        UITool.setEditWithClearButton(titleEditTextView, R.drawable.seach_clear);

        titleEditTextView.setFocusable(true);
        titleEditTextView.setFocusableInTouchMode(true);
        titleEditTextView.requestFocus();
        titleEditTextView.setHint("请输入医生关键字");


        mAdapter = new DoctorListAdapter();
        listView = (XListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);

        mAdapter = new DoctorListAdapter();
        mControl = new PageViewControl(listView, DoctorInfo.class, mAdapter, ConfigUrlMrg.ASK_DOC_LIST, new PageViewControl.onPageViewListenerAdapter() {
            @Override
            public void onDataCallBack(int page, ArrayList listData) {
                super.onDataCallBack(page, listData);

                if (mAdapter == null || mAdapter.getDatas().isEmpty()) {
                    notFindResoult();
                } else {
                    listView.setVisibility(View.VISIBLE);
                    findViewById(R.id.line).setVisibility(View.VISIBLE);
                    lodinggroup.setVisibility(View.GONE);
                }

            }
            @Override
            public void onStopLoading() {
                super.onStopLoading();
            }

        });
//        if(fromWhere == WHERE_PRIVATE_DOCTOR){
//            mControl.putPostValue("consult","1");
//        }
        mControl.setRowsString("list");
        mControl.setPageRowCount(20);
        mControl.setNeedCache(false);

        titleEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

//                mControl.putPostValue("doctorName",titleEditTextView.getText().toString());
//                mControl.loadRefresh();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_top_right:
                doctorName = titleEditTextView.getText().toString().trim();
                if (TextUtils.isEmpty(doctorName)) {
                    showToast("请输入你要搜索的医生关键字");
                    return;
                }
                UITool.closeInputMethodManager(getActivity());
                mControl.putPostValue("doctorName",doctorName);
                mControl.loadRefresh();
                break;
            case R.id.btn_back:
                getActivity().onBackPressed();
                break;
        }

    }

    private void notFindResoult() {
        listView.setVisibility(View.GONE);
        findViewById(R.id.line).setVisibility(View.GONE);
        lodinggroup.setVisibility(View.VISIBLE);
        imageViewNodata.setImageResource(R.drawable.task_no_data);
        textViewTip.setText("没有找到相关医生哦~");
        textViewTip.setTextColor(getResources().getColor(R.color.text_color_1));
    }

    @Override
    public boolean onBackPress() {
        return false;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        DoctorInfo info = mAdapter.getItem(i-1);
        DoctorServerList.toFragment(getActivity(), info.USER_ID, info.if_doctor == 1);
    }
}
