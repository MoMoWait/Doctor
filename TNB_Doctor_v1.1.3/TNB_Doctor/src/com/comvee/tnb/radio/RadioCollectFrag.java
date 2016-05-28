package com.comvee.tnb.radio;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.model.CollectItem;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.tnb.model.RadioGroup;
import com.comvee.tnb.network.CollectListLoader;
import com.comvee.tnb.network.NetworkCallBack;
import com.comvee.tnb.network.RadioCollectRequest;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.more.CollectionListFragment;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tnb.widget.pageview.PageViewControl;
import com.comvee.tool.UserMrg;
import com.comvee.util.CacheUtil;
import com.comvee.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class RadioCollectFrag extends BaseFragment implements OnClickListener, OnItemClickListener {

    private XListView mListView;
    private RadioCollectAdapter mAdapter;
    private View layoutNothing;
    private View btnSelect, btnDelete, playBar, headBtn, groupEditting;
    private TitleBarView titleView;
    private boolean isSelectAll;
    private TextView tv_select;
    private RadioCollectRequest collectRequest;
    private CollectListLoader mLoader;
    private boolean isDeleteAll;
    private boolean isEditting;// 是否编辑状态
    private int mFromWhere;

    private PageViewControl mControl;


    public static void toFragment(FragmentActivity fragment, int fromwhere, boolean anim) {
        Bundle bundle = new Bundle();
        bundle.putInt("from_where", fromwhere);
        if (fromwhere==1){
            anim=true;
        }else{
            anim=false;
        }
        FragmentMrg.toFragment(fragment, RadioCollectFrag.class, bundle, anim);
        // FragmentMrg.toFragment();
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.radio_collect_frag;
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);
        try {
            mFromWhere = dataBundle.getInt("from_where");
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSelectImg = (ImageView) findViewById(R.id.btn_select_img);
        titleView = (TitleBarView) findViewById(R.id.layout_top);
        titleView.setTitle("我的收藏");
        titleView.setVisibility(View.VISIBLE);
        titleView.setLeftDefault(this);
        mListView = (XListView) findViewById(R.id.list_view);
        mListView.setEmptyView(findViewById(R.id.layout_no_data));
        btnDelete = findViewById(R.id.btn_delete);
        btnSelect = findViewById(R.id.btn_select);
        playBar = findViewById(R.id.layout_play_bar);
        headBtn = findViewById(R.id.group_btn);
        tv_select = (TextView) findViewById(R.id.tv_select);
        groupEditting = findViewById(R.id.group_editting);
        mAdapter = new RadioCollectAdapter();
        mListView.setAdapter(mAdapter);
        findViewById(R.id.btn_editting).setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        btnDelete.setOnClickListener(this);
        btnSelect.setOnClickListener(this);
        findViewById(R.id.btn_article).setOnClickListener(this);
        layoutNothing = findViewById(R.id.layout_no_data);
        initRequestCancle();

        mControl = new PageViewControl(mListView, CollectItem.class, mAdapter, ConfigUrlMrg.RADIO_COLLECT_LOAD, new PageViewControl.onPageViewListenerAdapter() {

            @Override
            public void onStopLoading() {
                super.onStopLoading();
                cancelProgressDialog();
                mListView.setEmptyView(layoutNothing);
            }
        });

        showProgressDialog("请稍后...");
        mControl.loadRefresh();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
        mControl.loadRefresh();
    }

    private void initRequestCancle() {
        collectRequest = new RadioCollectRequest(new NetworkCallBack() {

            @Override
            public void callBack(int what, int status, Object obj) {
                if (obj != null) {
                    if (StringUtil.isNumble(obj.toString()) && Integer.parseInt(obj.toString()) == 0) {
                        if (isDeleteAll) {
                            mAdapter.removeAllData();
                        } else {
                            deleteListItem();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
                cancelProgressDialog();
            }
        });
    }

    private void deleteListItem() {
        ArrayList<CollectItem> tempList = new ArrayList<CollectItem>();
        List<CollectItem> list = mAdapter.getDatas();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isCheck) {
                tempList.add(list.get(i));
            }
        }
        list.removeAll(tempList);
    }
    boolean isSelect=true; //编辑的转换
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_article:
                CollectionListFragment.toFragment(getActivity(), mFromWhere, false);
                break;
            case R.id.btn_editting:
                if(mAdapter.getDatas()==null || mAdapter.getDatas().isEmpty()){
                }else{
                    if (isSelect){//进入编辑状态
                        isSelect=false;
                        selectEditting();
                    }else {//取消编辑状态
                        isSelect=true;
                        cancleEditting();
                    }
                }
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                cancleEditting();
                break;
            case R.id.btn_select:
                isSelectAll();
                break;
            case R.id.btn_delete:

                CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                builder.setMessage("确定删除？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (collectRequest != null) {
                            showProgressDialog("正在删除...");
                            String reqStr = getDeleteString();
                            collectRequest.requestCancleCollect(reqStr);
                            if ("selectAll".equals(reqStr)) {
                                isDeleteAll = true;
                            } else {
                                isDeleteAll = false;
                            }
                        }
                        cancleEditting();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.setVisible(View.VISIBLE);
                builder.create().show();


                break;
            default:
                break;
        }
    }
    private ImageView mSelectImg;
    private void isSelectAll() {
        isSelectAll = !isSelectAll;
        List<CollectItem> lsit = mAdapter.getDatas();
        for (int i = 0; i < lsit.size(); i++) {
            lsit.get(i).isCheck = isSelectAll;
        }
        mSelectImg.setImageResource(isSelectAll?R.drawable.cb_check_1:R.drawable.cb_uncheck_1);
        tv_select.setText(isSelectAll ?  getString(R.string.select_all) : getString(R.string.select_all));
        mAdapter.notifyDataSetChanged();
    }

    private String getDeleteString() {
        int selectCount = 0;
        StringBuffer buffer = new StringBuffer();
        List<CollectItem> lsit = mAdapter.getDatas();
        for (int i = 0; i < lsit.size(); i++) {

            CollectItem moItem = lsit.get(i);
            if (moItem.isCheck) {
                buffer.append(moItem.radioId);
                buffer.append(",");
                buffer.append(moItem.programType);
                buffer.append(";");
                selectCount++;
            } else {
                continue;
            }
        }

        return selectCount == 0 ? buffer.toString() : (selectCount == lsit.size() ? "selectAll" : buffer.toString());
    }

    private void selectEditting() {
        isEditting = true;
        mAdapter.setShowSelect(true);
        mAdapter.notifyDataSetChanged();
       //titleView.setRightButton(getString(R.string.cancel), this);
        //headBtn.setVisibility(View.GONE);
        playBar.setVisibility(View.GONE);
        btnDelete.setVisibility(View.VISIBLE);
        btnSelect.setVisibility(View.VISIBLE);
      // groupEditting.setVisibility(View.GONE);
    }

    private void cancleEditting() {
        isEditting = false;
        mAdapter.setShowSelect(false);
        mAdapter.notifyDataSetChanged();
        titleView.hideRightButton();
        btnDelete.setVisibility(View.GONE);
        btnSelect.setVisibility(View.GONE);
        headBtn.setVisibility(View.VISIBLE);
        playBar.setVisibility(View.VISIBLE);
        groupEditting.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        CollectItem item = mAdapter.getItem(position - 1);
        if (isEditting) {
            item.isCheck = !item.isCheck;
            mAdapter.notifyDataSetChanged();
        } else {
            switch (item.programType) {// 1专辑2节目
                case 1:
                    ProgrammeListFrag.toFragment(getActivity(), RadioUtil.getRadioAlbumByCollect(item));
                    break;
                case 2:
                    RadioGroup.RadioAlbum album = RadioUtil.getRadioAlbumByCollect(item);
                    RadioAlbumItem program = RadioUtil.getProgramByRadioAlbum(album);
                    ArrayList<RadioAlbumItem> list = new ArrayList<RadioAlbumItem>();
                    list.add(program);
                    RadioPlayerMrg.getInstance().setDataSource(album, list,false);
                    RadioPlayerMrg.getInstance().play(0);
                    RadioPlayFrag.toFragment(getActivity(), album, program);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public boolean onBackPress() {

        if (FragmentMrg.isContain(RadioMainFrag.class)) {
            FragmentMrg.popBackToFragment(getActivity(), RadioMainFrag.class, null, true);
        } else {
            IndexFrag.toFragment(getActivity(), true);
        }
        cancleEditting();

        return true;

    }
    private RadioGroup.RadioAlbum tempAlbum;
    @Override
    public void onFragmentResult(Bundle data) {
        super.onFragmentResult(data);
        if (data != null) {
            mAdapter.removeAllData();
            mAdapter.notifyDataSetChanged();
            CacheUtil.getInstance().putObjectById(UserMrg.getCacheKey(ConfigUrlMrg.RADIO_COLLECT_LOAD), null);
        }
        mControl.loadRefresh();
        if (data != null && tempAlbum != null) {
            try {
                tempAlbum.isCollect = data.getInt("collect");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
